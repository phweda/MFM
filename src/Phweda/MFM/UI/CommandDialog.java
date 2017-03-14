/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2017.  Author phweda : phweda1@yahoo.com
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

package Phweda.MFM.UI;

import Phweda.MFM.MAMEInfo;
import Phweda.MFM.MAMEexe;
import Phweda.MFM.MFM;
import Phweda.utils.ClickListener;
import Phweda.utils.PersistUtils;

import javax.swing.*;
import javax.swing.text.Caret;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 1/21/12
 * Time: 1:55 AM
 */
class CommandDialog extends JDialog {
    private static final String MAMECOMMANDS = "MAME Command Builder";
    private static final String MY_COMMANDS_XML = "myCommands.xml";

    private static JTextField commandTextField;
    private static JComboBox<String> commandsComboBox;
    private static HashMap<String, String> myCommands;

    private static JTree commandTree;

    private CommandDialog(Frame frame) {
        super(frame, MAMECOMMANDS);
        loadCommands();
        init();
        setLocationRelativeTo(frame);
        setLocation(100, 100);
        this.setPreferredSize(new Dimension(frame.getSize().width - 600,
                frame.getSize().height - 200));
    }

    //TODO add MESS
    protected static void showCommands(Frame frame) {
        CommandDialog cd = new CommandDialog(frame);
        cd.pack();
        cd.setVisible(true);
    }

    private void loadCommands() {

        if (Paths.get(MFM.MFM_SETTINGS_DIR + MY_COMMANDS_XML).toFile().exists()) {
            try {
                myCommands = (HashMap) PersistUtils.loadAnObjectXML(MFM.MFM_SETTINGS_DIR + MY_COMMANDS_XML);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            createBaseCommands();
            persistCommands();
        }
    }

    private void createBaseCommands() {
        myCommands = new HashMap<String, String>();
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
        commandsComboBox = new JComboBox<String>(keys.toArray(new String[keys.size()]));

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
        //    commandTextField.setCaretColor(Color.orange);
        //    commandTextField.set
        Caret caret = commandTextField.getCaret();

        //commandTextField.setText("1234awerqawerqwerqwerqwer");
        commandTextField.setColumns(40);

        commandPanel.add(commandTextField);

        JButton saveCommandButton = new JButton("Save");
        saveCommandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myCommands.put(commandsComboBox.getSelectedItem().toString(),
                        commandTextField.getText());
                commandsComboBox.addItem(commandsComboBox.getSelectedItem().toString());
                persistCommands();
            }
        });

        JButton runCommandButton = new JButton("Run Command");
        runCommandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    // This runs the saved one
                    //    String command = myCommands.get(commandsComboBox.getSelectedItem().toString());

                    // Run whatever is displayed in the textfield
                    String command = commandTextField.getText().trim();

                    //TODO here is where we split out for piping
                    // Split string on spaces
                    String[] splitArray = command.split("\\s+");
                    MAMEexe.run(splitArray);
                } catch (PatternSyntaxException | MAMEexe.MAME_Exception ex) {
                    //
                }
            }
        });

        JPanel commandButtonPanel = new JPanel();

        commandsComboBox.setEditable(true);
        // fixme after first edit comboBoxChanged fails to run
        commandsComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("comboBoxEdited")) {
                    // TODO what?? Really do when we Save .... I think

                } else if (e.getActionCommand().equals("comboBoxChanged")) {
                    String command = (String) commandsComboBox.getSelectedItem();
                    String value = myCommands.get(command);
                    if (!value.equals("null")) {
                        commandTextField.setText(value + " ");
                        commandTextField.requestFocus();
                    }

                    //    commandsComboBox
                }
            }
        });

        commandButtonPanel.add(commandsComboBox);
        commandButtonPanel.add(new JLabel(" "));
        commandButtonPanel.add(saveCommandButton);
        commandButtonPanel.add(runCommandButton);
        commandButtonPanel.add(new JLabel(" "));
        commandPanel.add(commandButtonPanel);

        commandPanel.setPreferredSize(new Dimension(500, 100));

        return commandPanel;
    }

    private void createCommandTree() {

        DefaultMutableTreeNode root;
        root = new DefaultMutableTreeNode("Commands");
        commandTree = new JTree(root);

        for (Object category : MAMEInfo.Commands().keySet()) {
            DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(category);
            for (Object command : ((HashMap) MAMEInfo.Commands().get(category)).keySet()) {

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
        PersistUtils.saveAnObjectXML(myCommands, MFM.MFM_SETTINGS_DIR + MY_COMMANDS_XML);
    }

    private class CommandController extends ClickListener {
        @Override
        public void singleClick(MouseEvent e) {

        }

        @Override
        public void doubleClick(MouseEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    commandTree.getLastSelectedPathComponent();
            if (node == null) {
                //fixme anything here? We should never get here but ...
            }
            //gameName = (String) node.getUserObject();
            Object nodeInfo = node.getUserObject();
            commandTextField.setText(commandTextField.getText() +
                    nodeInfo.toString().substring(0, nodeInfo.toString().indexOf(' ')) + " ");
            commandTextField.requestFocus();

        }
    }
}
