/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2011 - 2018.  Author phweda : phweda1@yahoo.com
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.phweda.mfm.ui;

import com.github.phweda.mfm.MAMEInfo;
import com.github.phweda.mfm.MAMEexe;
import com.github.phweda.mfm.MFM;
import com.github.phweda.mfm.MFM_Constants;
import com.github.phweda.utils.ClickListener;
import com.github.phweda.utils.PersistUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import static com.github.phweda.mfm.MFM_Constants.NULL_STRING;
import static com.github.phweda.mfm.MFM_Constants.SPACE_STRING;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 1/21/12
 * Time: 1:55 AM
 */
@SuppressWarnings({"squid:MaximumInheritanceDepth", "WeakerAccess"})
class CommandDialog extends JDialog {
    private static final String MY_COMMANDS_XML = "myCommands.xml";
    public static final String RUN_COMMAND = "Run Command";
    public static final String SAVE_COMMAND = "Save Command";
    public static final String MAME_COMMAND_BUILDER = "MAME Command Builder";

    private JTextField commandTextField;
    private JComboBox<String> commandsComboBox;
    private HashMap<String, String> myCommands;

    private JTree commandTree;

    private CommandDialog(Frame frame) {
        super(frame, MAME_COMMAND_BUILDER);
        loadCommands();
        init();
        setLocationRelativeTo(frame);
        setLocation(100, 100);
        this.setPreferredSize(new Dimension(frame.getSize().width - 600,
                frame.getSize().height - 200));
    }

    protected static void showCommands(Frame frame) {
        CommandDialog cd = new CommandDialog(frame);
        cd.pack();
        cd.setVisible(true);
    }

    private void loadCommands() {

        if (Paths.get(MFM.getMfmSettingsDir() + MY_COMMANDS_XML).toFile().exists()) {
            try {
                myCommands = (HashMap) PersistUtils.loadAnObjectXML(MFM.getMfmSettingsDir() + MY_COMMANDS_XML);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            createBaseCommands();
            persistCommands();
        }
    }

    private void createBaseCommands() {
        myCommands = new HashMap<>();
        myCommands.put("?", "-help");
        myCommands.put("Help", "-help");
        myCommands.put("Create config", "-createconfig");
        myCommands.put("Show config", "-showconfig");
    }

    private void init() {
        createCommandTree();
        // NOTE order is important here
        loadCommands();
        Set<String> keys = myCommands.keySet();
        commandsComboBox = new JComboBox<>(keys.toArray(new String[0]));

        this.getContentPane().add(createCommandPanel(), BorderLayout.SOUTH);
        this.pack();

    }

    private JPanel createCommandPanel() {
        JPanel commandPanel = new JPanel();
        commandTextField = new JTextField();
        commandTextField.setBackground(Color.BLACK);
        commandTextField.setForeground(Color.GREEN);
        commandTextField.getCaret().setVisible(true);
        commandTextField.setCaretColor(Color.white);
        commandTextField.setColumns(40);

        commandPanel.add(commandTextField);

        JButton saveCommandButton = new JButton("Save");
        saveCommandButton.addActionListener(e -> {
            myCommands.put(Objects.requireNonNull(commandsComboBox.getSelectedItem()).toString(),
                    commandTextField.getText());
            commandsComboBox.addItem(commandsComboBox.getSelectedItem().toString());
            persistCommands();
        });

        JButton runCommandButton = new JButton(RUN_COMMAND);
        runCommandButton.addActionListener(e -> {
                    try {
                        // Run whatever is displayed in the textfield
                        String command = commandTextField.getText().trim();

                        // Split string on spaces
                        String[] splitArray = command.split("\\s+");
                        MAMEexe.run(splitArray);
                    } catch (PatternSyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
        );

        JPanel commandButtonPanel = new JPanel();

        commandsComboBox.setEditable(true);
        // fixme after first edit comboBoxChanged fails to run
        commandsComboBox.addActionListener(e -> {
            if (e.getActionCommand().equals("comboBoxEdited")) {
                // Refresh needed?
            } else if (e.getActionCommand().equals("comboBoxChanged")) {
                String command = (String) commandsComboBox.getSelectedItem();
                String value = myCommands.get(command);
                if (!NULL_STRING.equals(value)) {
                    commandTextField.setText(value + MFM_Constants.SPACE_CHAR);
                    commandTextField.requestFocus();
                }

                //    commandsComboBox
            }
        });

        commandButtonPanel.add(commandsComboBox);
        commandButtonPanel.add(new JLabel(SPACE_STRING));
        commandButtonPanel.add(saveCommandButton);
        commandButtonPanel.add(runCommandButton);
        commandButtonPanel.add(new JLabel(SPACE_STRING));
        commandPanel.add(commandButtonPanel);

        commandPanel.setPreferredSize(new

                Dimension(500, 100));

        return commandPanel;
    }

    private void createCommandTree() {

        DefaultMutableTreeNode root;
        root = new DefaultMutableTreeNode(MFM_Constants.COMMANDS);
        commandTree = new JTree(root);

        for (Object category : MAMEInfo.Commands().keySet()) {
            DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(category);
            for (Object command : ((Map) MAMEInfo.Commands().get(category)).keySet()) {

                DefaultMutableTreeNode commandNode = new DefaultMutableTreeNode(command +
                        "  ->  " + ((HashMap) MAMEInfo.Commands().get(category)).get(command));
                categoryNode.add(commandNode);
            }
            root.add(categoryNode);
        }


        commandTree.addMouseListener(new CommandController());
        this.add(new JScrollPane(commandTree), BorderLayout.CENTER);
        // Expand root folder
        commandTree.expandRow(0);
    }

    private void persistCommands() {
        PersistUtils.saveAnObjectXML(myCommands, MFM.getMfmSettingsDir() + MY_COMMANDS_XML);
    }

    private class CommandController extends ClickListener {
        @Override
        public void singleClick(MouseEvent e) {
            // Eliminate this?
        }

        @Override
        public void doubleClick(MouseEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    commandTree.getLastSelectedPathComponent();
            Object nodeInfo = node.getUserObject();
            commandTextField.setText(commandTextField.getText() +
                    nodeInfo.toString().substring(0, nodeInfo.toString().indexOf(' ')) + " ");
            commandTextField.requestFocus();
        }
    }
}
