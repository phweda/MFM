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

import Phweda.MFM.MFMListBuilder;
import Phweda.MFM.MFMPlayLists;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/15/2015
 * Time: 9:25 PM
 */
public class ListEditor implements ActionListener {

    private static final ListEditor ourInstance = new ListEditor();

    private JPanel listEditorPanel;
    private JPanel buttonPanel;
    private JTextField nameTextField;
    private JButton createListButton;
    private JButton saveListButton;
    private JScrollPane leftScrollPane;
    private JScrollPane rightScrollPane;
    private JList<String> machinesList;
    private JSplitPane listEditorSplitPane;
    private JList<String> editedList;
    private JComboBox<String> listComboBox;
    private JButton flipViewButton;

    private static final String SAVE_LIST = "Save List";
    private static final String CREATE_LIST = "Create List";
    private static final String LOAD_LIST = "Load List";
    private static final String FLIP_VIEW = "Flip View";
    private static final String CLEAR_LIST = "Clear List";

    public static ListEditor getInstance() {
        return ourInstance;
    }

    private ListEditor() {
        $$$setupUI$$$();
        addListeners();
    }

    private void addListeners() {
        saveListButton.addActionListener(this);
        createListButton.addActionListener(this);
        flipViewButton.addActionListener(this);
        listComboBox.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        switch (e.getActionCommand()) {
            case (SAVE_LIST):
                // TODO is this different than createList?
                break;

            case CREATE_LIST:
                createList();
                break;

            case LOAD_LIST:
                loadList();
                break;

            case FLIP_VIEW:
                flipOrientation();
                break;

            case AddRemoveDividerUI.ADD:
                addMachines();
                break;

            case AddRemoveDividerUI.REMOVE:
                removeMachines();
                break;
        }
    }

    private void addMachines() {
        ((ListEditorModel<String>) editedList.getModel()).addAll(machinesList.getSelectedValuesList());
    }

    private void removeMachines() {
        ((ListEditorModel<String>) editedList.getModel()).removeAll(machinesList.getSelectedValuesList());
    }

    private void loadList() {
        String list = listComboBox.getSelectedItem().toString();
        if (list.equals(CLEAR_LIST)) {
            ((ListEditorModel<String>) editedList.getModel()).clear();
        } else {
            ((ListEditorModel<String>) editedList.getModel()).addAll(
                    new ArrayList<String>(MFMPlayLists.getInstance().getPlayList(list)));
        }
    }

    private void flipOrientation() {
        if (listEditorSplitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            listEditorSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            listEditorSplitPane.setUI(getDivider());
        } else {
            listEditorSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            listEditorSplitPane.setUI(getDivider());
        }
        listEditorSplitPane.validate();
    }

    private void addGame() {
        int[] selected = machinesList.getSelectedIndices();
        for (int index : selected) {
            ((DefaultListModel<String>) editedList.getModel()).addElement(machinesList.getModel().getElementAt(index));
        }
    }

    private void createList() {
        Object[] list = editedList.getSelectedValuesList().toArray();
        MFMListBuilder.createPlayList(this.nameTextField.getText(), Arrays.copyOf(list, list.length, String[].class));
    }

    private AddRemoveDividerUI getDivider() {
        AddRemoveDividerUI divider = new AddRemoveDividerUI(listEditorSplitPane.getOrientation());
        divider.addActionListener(this);
        return divider;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        flipViewButton = new JButton(MFMUI_Resources.getInstance().getImageIcon(MFMUI_Resources.CIRCULAR_ARROW));
        flipViewButton.setContentAreaFilled(false);
        flipViewButton.setBorderPainted(false);
        flipViewButton.setActionCommand(FLIP_VIEW);

        Set<String> listKeys = MFMPlayLists.getInstance().getListBuilderPlaylistsKeys();
        listComboBox = new JComboBox<String>(listKeys.toArray(new String[listKeys.size()]));
        listComboBox.addItem(CLEAR_LIST);
        listComboBox.setSelectedItem(CLEAR_LIST);
        listComboBox.setActionCommand(LOAD_LIST);

        listEditorSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        listEditorSplitPane.setUI(getDivider());

        ListEditorModel<String> elm = new ListEditorModel<String>();
        editedList = new JList<String>();
        editedList.setModel(elm);

        machinesList = new JList<String>(MFMListBuilder.getRunnableArray());
        machinesList.setVisibleRowCount(MFMListBuilder.getRunnableList().size() / 3 + 2);

        editedList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    saveListButton.doClick();
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
        listEditorPanel = new JPanel();
        listEditorPanel.setLayout(new BorderLayout(0, 0));
        listEditorPanel.setMinimumSize(new Dimension(900, 900));
        listEditorPanel.setPreferredSize(new Dimension(900, 900));
        listEditorSplitPane.setDividerLocation(450);
        listEditorSplitPane.setLastDividerLocation(447);
        listEditorSplitPane.setMinimumSize(new Dimension(700, 850));
        listEditorPanel.add(listEditorSplitPane, BorderLayout.CENTER);
        rightScrollPane = new JScrollPane();
        listEditorSplitPane.setRightComponent(rightScrollPane);
        rightScrollPane.setViewportView(editedList);
        leftScrollPane = new JScrollPane();
        leftScrollPane.setMaximumSize(new Dimension(250, 32767));
        leftScrollPane.setMinimumSize(new Dimension(250, 450));
        listEditorSplitPane.setLeftComponent(leftScrollPane);
        machinesList.setLayoutOrientation(1);
        machinesList.setVisibleRowCount(0);
        leftScrollPane.setViewportView(machinesList);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        listEditorPanel.add(buttonPanel, BorderLayout.SOUTH);
        nameTextField = new JTextField();
        nameTextField.setMinimumSize(new Dimension(200, 29));
        nameTextField.setPreferredSize(new Dimension(100, 29));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(nameTextField, gbc);
        createListButton = new JButton();
        createListButton.setText("Create List");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(createListButton, gbc);
        saveListButton = new JButton();
        saveListButton.setText("Save List");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(saveListButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.weightx = 0.05;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer1, gbc);
        listComboBox.setMinimumSize(new Dimension(150, 33));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.15;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(listComboBox, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.05;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.weightx = 0.025;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer5, gbc);
        flipViewButton.setActionCommand("");
        flipViewButton.setText("");
        flipViewButton.setToolTipText("Flip View");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(flipViewButton, gbc);
        machinesList.setNextFocusableComponent(machinesList);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return listEditorPanel;
    }
}
