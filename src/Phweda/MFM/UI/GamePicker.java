/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2016.  Author phweda : phweda1@yahoo.com
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

import Phweda.MFM.MFMListBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/15/2015
 * Time: 9:25 PM
 */
public class GamePicker implements ActionListener {

    private static final GamePicker ourInstance = new GamePicker();

    private JPanel gamePickerPanel;
    private JPanel buttonPanel;
    private JTextField nameTextField;
    private JButton createListButton;
    private JButton addButton;
    private JScrollPane leftScrollPane;
    private JScrollPane rightScrollPane;
    private JList gamesList;
    private JTextArea textArea;

    private static final String ADD = "Add";
    private static final String CREATE_LIST = "Create List";

    public static final GamePicker getInstance() {
        return ourInstance;
    }


    private GamePicker() {
        $$$setupUI$$$();
        addButton.addActionListener(this);
        createListButton.addActionListener(this);
        gamesList.addKeyListener(new KeyAdapter() {
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ADD)) {
            addGame();
        } else if (e.getActionCommand().equals(CREATE_LIST)) {
            createList();
        }
    }

    private void addGame() {
        int[] selected = gamesList.getSelectedIndices();
        for (int index : selected) {
            textArea.append(gamesList.getModel().getElementAt(index) + "\n");
        }
    }

    private void createList() {
        //String[] machines = textArea.getText().split(System.getProperty("line.separator"));
        // Usage of this regex protects from empty string entry
        String[] machines = textArea.getText().split("[\\r\\n]+");

        String name = nameTextField.getText();
        MFMListBuilder.createPlayList(name, machines, gamePickerPanel);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        gamesList = new JList(MFMListBuilder.getRunnableArray());
        gamesList.setVisibleRowCount(MFMListBuilder.getRunnableArray().length / 3 + 2);

        gamesList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addButton.doClick();
                }
            }
        });

        gamesList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    addButton.doClick();
                }
            }
        });

    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        gamePickerPanel = new JPanel();
        gamePickerPanel.setLayout(new BorderLayout(0, 0));
        gamePickerPanel.setMinimumSize(new Dimension(400, 550));
        gamePickerPanel.setPreferredSize(new Dimension(400, 550));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(275);
        splitPane1.setLastDividerLocation(256);
        gamePickerPanel.add(splitPane1, BorderLayout.CENTER);
        rightScrollPane = new JScrollPane();
        splitPane1.setRightComponent(rightScrollPane);
        textArea = new JTextArea();
        rightScrollPane.setViewportView(textArea);
        leftScrollPane = new JScrollPane();
        leftScrollPane.setMaximumSize(new Dimension(250, 32767));
        leftScrollPane.setMinimumSize(new Dimension(250, 450));
        splitPane1.setLeftComponent(leftScrollPane);
        gamesList.setLayoutOrientation(1);
        gamesList.setVisibleRowCount(0);
        leftScrollPane.setViewportView(gamesList);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        gamePickerPanel.add(buttonPanel, BorderLayout.SOUTH);
        nameTextField = new JTextField();
        nameTextField.setMinimumSize(new Dimension(100, 29));
        nameTextField.setPreferredSize(new Dimension(100, 29));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(nameTextField, gbc);
        createListButton = new JButton();
        createListButton.setText("Create List");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(createListButton, gbc);
        addButton = new JButton();
        addButton.setText("Add");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(addButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer1, gbc);
        gamesList.setNextFocusableComponent(gamesList);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return gamePickerPanel;
    }
}
