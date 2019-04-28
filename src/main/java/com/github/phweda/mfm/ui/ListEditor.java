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

import com.github.phweda.mfm.*;
import com.github.phweda.mfm.mame.Machine;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/15/2015
 * Time: 9:25 PM
 */
@SuppressWarnings("squid:S1450")
public class ListEditor implements ActionListener {

    private static final ListEditor ourInstance = new ListEditor();

    private JPanel listEditorPanel;
    private JPanel buttonPanel;
    private JTextField nameTextField;
    private JButton createListButton;
    private JScrollPane leftScrollPane;
    private JScrollPane rightScrollPane;
    private JList<String> machinesList;
    private JSplitPane listEditorSplitPane;
    private JList<String> workingList;
    private JComboBox<String> listComboBox;
    private JButton flipViewButton;
    private JPanel combinationPanel;
    private JCheckBox unionCB;
    private JCheckBox intersectionCB;
    private JCheckBox exclusionCB;
    private JTextField countTextField;

    private static final String CREATE_LIST = MFM_Constants.CREATE_LIST;
    private static final String EDIT_WORKING_LIST = "Edit List";
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
        createListButton.addActionListener(this);
        flipViewButton.addActionListener(this);
        listComboBox.addActionListener(this);

        workingList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    if (MFM.isSystemDebug()) {
                        System.out.println("Adding from clipboard");
                    }
                    addFromClipboard();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    createListButton.doClick();
                }
            }
        });

        // Show Machine description using parent tooltip
        machinesList.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                JList list = (JList) e.getSource();
                ListModel listModel = list.getModel();
                int index = list.locationToIndex(e.getPoint());
                if (index > -1) {
                    Machine machine = MAMEInfo.getMachine(listModel.getElementAt(index).toString());
                    list.setToolTipText(machine.getDescription());
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        switch (e.getActionCommand()) {

            case CREATE_LIST:
                createList();
                break;

            case EDIT_WORKING_LIST:
                editWorkingList();
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
            default:
                break;
        }
    }

    private void addFromClipboard() {
        try {
            String input = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
            ArrayList<String> inputList = new ArrayList<>(Arrays.asList(input.split("\n")));
            ((ListEditorModel<String>) workingList.getModel()).addAll(inputList);
            displayCurrentCount();
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addMachines() {
        ((ListEditorModel<String>) workingList.getModel()).addAll(machinesList.getSelectedValuesList());
        displayCurrentCount();
    }

    private void removeMachines() {
        ((ListEditorModel<String>) workingList.getModel()).removeAll(workingList.getSelectedValuesList());
        displayCurrentCount();
    }

    private void editWorkingList() {
        String list = Objects.requireNonNull(listComboBox.getSelectedItem()).toString();
        if (list.equals(CLEAR_LIST)) {
            ((ListEditorModel<String>) workingList.getModel()).clear();
        } else {
            if (unionCB.isSelected()) {
                ((ListEditorModel<String>) workingList.getModel()).addAll(
                        new ArrayList<>(MFMPlayLists.getInstance().getPlayList(list)));
            } else if (intersectionCB.isSelected()) {
                // Gotta be a more direct way of doing this
                Object[] elements = ((ListEditorModel<String>) workingList.getModel()).toArray();
                String[] strings = Arrays.copyOf(elements, elements.length, String[].class);
                ArrayList<String> currentList = new ArrayList<>(Arrays.asList(strings));
                currentList.retainAll(new ArrayList<>(MFMPlayLists.getInstance().getPlayList(list)));
                ((ListEditorModel<String>) workingList.getModel()).refreshList(currentList);
            } else if (exclusionCB.isSelected()) {
                ((ListEditorModel<String>) workingList.getModel()).removeAll(
                        new ArrayList<>(MFMPlayLists.getInstance().getPlayList(list)));
            }
        }
        displayCurrentCount();
    }

    private void displayCurrentCount() {
        countTextField.setText("Count " + MFMController.decimalFormater.format(workingList.getModel().getSize()));
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

    private void createList() {
        Object[] list = new Object[workingList.getModel().getSize()];
        for (int i = 0; i < workingList.getModel().getSize(); i++) {
            list[i] = workingList.getModel().getElementAt(i);
        }
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

        Set<String> listKeys = MFMPlayLists.getInstance().getListEditorKeys();
        listComboBox = new JComboBox<>(listKeys.toArray(new String[0]));
        listComboBox.addItem(CLEAR_LIST);
        listComboBox.setSelectedItem(CLEAR_LIST);
        listComboBox.setActionCommand(EDIT_WORKING_LIST);

        Font cbFont = UIManager.getDefaults().getFont("CheckBox.font");
        Font newCBFont = new Font(cbFont.getName(), cbFont.getStyle(), 20);
        ButtonGroup bg = new ButtonGroup();
        unionCB = new ListEditorJCheckBox("\u222a");
        unionCB.setFont(newCBFont);
        bg.add(unionCB);
        intersectionCB = new ListEditorJCheckBox("\u2229");
        bg.add(intersectionCB);
        intersectionCB.setFont(newCBFont);
        exclusionCB = new ListEditorJCheckBox("\u2212");// new JCheckBox("\u2212");
        bg.add(exclusionCB);
        exclusionCB.setFont(newCBFont);
        unionCB.setSelected(true);

        listEditorSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        listEditorSplitPane.setUI(getDivider());

        ListEditorModel<String> elm = new ListEditorModel<>();
        workingList = new JList<>();
        workingList.setModel(elm);

        machinesList = new JList<>(MFMListBuilder.getRunnableArray());
        machinesList.setVisibleRowCount(MFMListBuilder.getRunnableList().size() / 3 + 2);

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
        rightScrollPane.setViewportView(workingList);
        leftScrollPane = new JScrollPane();
        leftScrollPane.setMaximumSize(new Dimension(250, 32767));
        leftScrollPane.setMinimumSize(new Dimension(250, 450));
        listEditorSplitPane.setLeftComponent(leftScrollPane);
        machinesList.setLayoutOrientation(1);
        machinesList.setVisibleRowCount(0);
        leftScrollPane.setViewportView(machinesList);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setAlignmentX(1.0f);
        buttonPanel.setAlignmentY(1.0f);
        buttonPanel.setMinimumSize(new Dimension(900, 60));
        buttonPanel.setPreferredSize(new Dimension(900, 60));
        listEditorPanel.add(buttonPanel, BorderLayout.SOUTH);
        nameTextField = new JTextField();
        nameTextField.setMinimumSize(new Dimension(200, 29));
        nameTextField.setPreferredSize(new Dimension(100, 29));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(nameTextField, gbc);
        createListButton = new JButton();
        createListButton.setActionCommand("Create List");
        createListButton.setText("Create List");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(createListButton, gbc);
        listComboBox.setEnabled(true);
        listComboBox.setMinimumSize(new Dimension(150, 33));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.15;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(listComboBox, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0.01;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.05;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 0;
        gbc.weightx = 0.025;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer3, gbc);
        flipViewButton.setToolTipText("Flip View");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(flipViewButton, gbc);
        combinationPanel = new JPanel();
        combinationPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        buttonPanel.add(combinationPanel, gbc);
        Font unionCBFont = this.$$$getFont$$$(null, -1, -1, unionCB.getFont());
        if (unionCBFont != null) unionCB.setFont(unionCBFont);
        unionCB.setHorizontalTextPosition(0);
        unionCB.setToolTipText("Add to List");
        unionCB.setVerticalTextPosition(1);
        combinationPanel.add(unionCB);
        intersectionCB.setHorizontalTextPosition(0);
        intersectionCB.setToolTipText("Retain those in both lists");
        intersectionCB.setVerticalTextPosition(1);
        combinationPanel.add(intersectionCB);
        exclusionCB.setHorizontalTextPosition(0);
        exclusionCB.setToolTipText("Remove from list");
        exclusionCB.setVerticalTextPosition(1);
        combinationPanel.add(exclusionCB);
        countTextField = new JTextField();
        countTextField.setEditable(false);
        countTextField.setMinimumSize(new Dimension(125, 31));
        countTextField.setPreferredSize(new Dimension(125, 31));
        countTextField.setText("Count");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(countTextField, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer4, gbc);
        machinesList.setNextFocusableComponent(machinesList);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return listEditorPanel;
    }

    @SuppressWarnings("squid:MaximumInheritanceDepth")
    class ListEditorJCheckBox extends JCheckBox {

        ListEditorJCheckBox(String text) {
            super(text);
        }

        @Override
        public JToolTip createToolTip() {
            return (new ListEditorToolTip(this));
        }
    }

    class ListEditorToolTip extends JToolTip {
        ListEditorToolTip(JComponent component) {
            super();
            Font parentFont = component.getParent().getFont();
            Font zipFont = new Font(parentFont.getName(), parentFont.getStyle(), 16); //parentFont.getSize() + 2);
            setFont(zipFont);
            setComponent(component);
            setBackground(Color.yellow);
            setForeground(Color.red);
        }
    }
}
