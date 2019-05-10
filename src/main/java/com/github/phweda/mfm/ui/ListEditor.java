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

  public JPanel getListEditorPanel() {
    return listEditorPanel;
  }
}
