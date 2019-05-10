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
import com.github.phweda.utils.QuadState;
import com.github.phweda.utils.SwingUtils;
import com.github.phweda.utils.TriState;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.*;

import static com.github.phweda.mfm.ui.MFMAction.LIST_EDITOR;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/19/2015
 * Time: 1:21 PM
 */
@SuppressWarnings({"squid:S1450", "DuplicateStringLiteralInspection"})
public final class ListBuilderUI implements ActionListener, Serializable {

    private static final ListBuilderUI ourInstance = new ListBuilderUI();

    private JPanel orientationPanel;
    private JPanel gameButtonsPanel;
    private JScrollPane categoriesScrollPane;
    private JPanel controlsCBPanel;
    private JPanel categoriesPanel;
    private JPanel listBuilderPanel;
    private JPanel buttonPanel;
    private JLabel listNameLabel;
    private JPanel uileftPanel;
    private JPanel mamePanel;
    private JPanel categoriesCBpanel;

    private JComboBox<String> gameButtonsNum;
    private JComboBox<Integer> gamePlayersNum;
    private JTextField listNameTF;

    private JButton createListButton;
    private JButton importListButton;

    private JRadioButton bothRadioButton;
    private JRadioButton arcadeOnlyRadioButton;
    private JRadioButton systemsOnlyRadioButton;

    private JRadioButton allRadioButton;
    private JRadioButton cocktailRadioButton;
    private JRadioButton horizontalRadioButton;
    private JRadioButton verticalRadioButton;

    private JCheckBox orLessButtonsCB;
    private JCheckBox noClonesCheckBox;
    private JCheckBox orLessPlayersCB;
    private JCheckBox simultaneousCB;
    private JCheckBox viewAllCategoriesCheckBox;
    private JCheckBox noMatureCB;
    private JCheckBox selectAllCheckBox;

    private JPanel displayTypePanel;
    private JRadioButton rasterRadioButton;
    private JRadioButton lcdRadioButton;
    private JRadioButton vectorRadioButton;
    private JRadioButton allDisplaysRadioButton;

    private JScrollPane controlsScrollPane;
    private JCheckBox noImperfectCheckBox;
    private JComboBox<String> joyComboBox;
    private JComboBox<String> doubleJoyComboBox;
    private JCheckBox exactMatchOnlyCheckBox;
    private JButton saveListToFileButton;
    private JPanel filterJPanel;
    private JComboBox<String> languagesComboBox;
    private JComboBox yearComboBox;
    private JCheckBox noMechanicalCheckBox;
    private JComboBox baseListComboBox;
    private JPanel uiControlsScrollPanel;
    private JPanel uiRightPanel;

    private JPanel controlsRBPanel;
    private JRadioButton anyControlsRB;
    private JRadioButton allControlsRB;
    private JRadioButton exactControlsRB;

    private JLabel baseListLabel;

    private static final String VIEW_ALL_CATEGORIES_COMMAND = "View All Categories";
    private static final String SELECT_ALL_CATEGORIES_COMMAND = "Select All Categories";
    private static final String ARCADE_ONLY_COMMAND = "Arcade Only";
    private static final String NO_MATURE_COMMAND = "No Mature";
    private static final String NO_MECHANICAL_COMMAND = "No Mechanical";
    private static final String SYSTEMS_ONLY_COMMAND = "Systems Only";
    private static final String BOTH_COMMAND = "Both";

    private static final String ALL_ORIENTATIONS_COMMAND = "All Orientations";
    private static final String VERTICAL_COMMAND = "Vertical";
    private static final String HORIZONTAL_COMMAND = "Horizontal";
    private static final String COCKTAIL_COMMAND = "Cocktail";

    private static final String GAMEBUTTONS_ORLESS_COMMAND = "GameButtonsorless";
    private static final String PLAYERS_ORLESS_COMMAND = "Playersorless";
    private static final String SIMULTANEOUS_COMMAND = "Simultaneous";
    private static final String NOCLONES_COMMAND = "No Clones";

    private static final String ALL_DISPLAY_TYPES_COMMAND = "All Displays";
    private static final String RASTER_COMMAND = "Raster";
    private static final String VECTOR_COMMAND = "Vector";
    private static final String LCD_COMMAND = "LCD";

    private static final String CREATE_LIST_COMMAND = MFM_Constants.CREATE_LIST;
    private static final String IMPORT_LIST_COMMAND = MFMAction.IMPORT_LIST;

    private static final String ANY_CONTROLS_COMMAND = "AnyControls";
    private static final String ALL_CONTROLS_COMMAND = "AllControls";
    private static final String EXACT_CONTROLS_COMMAND = "ExactControls";

    static final String PREVIOUS = "Previous";
    static final String NEW = "New";

    private static final MFMListBuilder.Builder builder = MFMListBuilder.Builder.getInstance();

    private static transient MFMController controller;

    private ListBuilderUI() {
        if (listBuilderPanel == null) {
            bothRadioButton = new JRadioButton();
            arcadeOnlyRadioButton = new JRadioButton();
            systemsOnlyRadioButton = new JRadioButton();
            allRadioButton = new JRadioButton();
            cocktailRadioButton = new JRadioButton();
            horizontalRadioButton = new JRadioButton();
            verticalRadioButton = new JRadioButton();
            rasterRadioButton = new JRadioButton();
            lcdRadioButton = new JRadioButton();
            vectorRadioButton = new JRadioButton();
            allDisplaysRadioButton = new JRadioButton();
            anyControlsRB = new JRadioButton();
            allControlsRB = new JRadioButton();
            exactControlsRB = new JRadioButton();
            setLabelsText();
            addActionListeners(listBuilderPanel);
        }
    }

    public static ListBuilderUI getInstance() {
        return ourInstance;
    }

    JPanel getListBuilderPanel() {
        return listBuilderPanel;
    }

    static void setController() {
        // This is bad design
        controller = MFMUI_Setup.getInstance().getController();
    }

    private void reset() {
        allRadioButton.setSelected(true);
        bothRadioButton.setSelected(true);
        allDisplaysRadioButton.setSelected(true);

        noClonesCheckBox.setSelected(false);
        noImperfectCheckBox.setSelected(false);
        noMatureCB.setSelected(false);
        noMechanicalCheckBox.setSelected(false);

        setSelectAllCategories(false);
        setSelectAllControls(false);

        gameButtonsNum.setSelectedIndex(gameButtonsNum.getModel().getSize() - 1);
        orLessButtonsCB.setSelected(true);
        gamePlayersNum.setSelectedIndex(gamePlayersNum.getModel().getSize() - 1);
        orLessPlayersCB.setSelected(true);

        languagesComboBox.setSelectedIndex(0);
        yearComboBox.setSelectedIndex(0);
    }

    void setState(String state) {
        reset();
        switch (state) {

            case MFMListBuilder.ARCADE:
                arcadeOnlyRadioButton.setSelected(true);
                break;

            case MFMListBuilder.SYSTEMS:
                systemsOnlyRadioButton.setSelected(true);
                break;

            case MFMListBuilder.VERTICAL:
                verticalRadioButton.setSelected(true);
                break;

            case MFMListBuilder.HORIZONTAL:
                horizontalRadioButton.setSelected(true);
                break;

            case MFMListBuilder.COCKTAIL:
                cocktailRadioButton.setSelected(true);
                break;

            case MFMListBuilder.RASTER:
                rasterRadioButton.setSelected(true);
                break;

            case MFMListBuilder.VECTOR:
                vectorRadioButton.setSelected(true);
                break;

            case MFMListBuilder.LCD:
                lcdRadioButton.setSelected(true);
                break;

            case MFMListBuilder.NO_CLONE:
                noClonesCheckBox.setSelected(true);
                break;

            case MFMListBuilder.NO_IMPERFECT:
                noImperfectCheckBox.setSelected(true);
                break;

            case MFMListBuilder.SIMULTANEOUS:
                simultaneousCB.setSelected(true);
                break;

            // Handle languages with default
            default:
                if (MFMPlayLists.getInstance().getLanguagesListMap().containsKey(state)) {
                    languagesComboBox.getModel().setSelectedItem(state);
                }
                break;
        }
    }

    @SuppressWarnings("ReuseOfLocalVariable")
    private void setLabelsText() {
        // with 0.85 we are NOT guaranteed to have these lists
        String number = (MFMListBuilder.getArcadeList() != null) ?
                MFMController.decimalFormater.format(MFMListBuilder.getArcadeList().size()) : "0";
        arcadeOnlyRadioButton.setText(ARCADE_ONLY_COMMAND + " - " + number);

        number = (MFMListBuilder.getSystemList() != null) ?
                MFMController.decimalFormater.format(MFMListBuilder.getSystemList().size()) : "0";
        systemsOnlyRadioButton.setText(SYSTEMS_ONLY_COMMAND + " - " + number);

        number = (MFMListBuilder.getVerticalsList() != null) ?
                MFMController.decimalFormater.format(MFMListBuilder.getVerticalsList().size()) : "0";
        verticalRadioButton.setText(VERTICAL_COMMAND + " - " + number);

        number = (MFMListBuilder.getHorizontalsList() != null) ?
                MFMController.decimalFormater.format(MFMListBuilder.getHorizontalsList().size()) : "0";
        horizontalRadioButton.setText(HORIZONTAL_COMMAND + " - " + number);

        number = (MFMListBuilder.getCocktailsList() != null) ?
                MFMController.decimalFormater.format(MFMListBuilder.getCocktailsList().size()) : "0";
        cocktailRadioButton.setText(COCKTAIL_COMMAND + " - " + number);

        number = (MFMListBuilder.getVectorDisplayList() != null) ?
                MFMController.decimalFormater.format(MFMListBuilder.getVectorDisplayList().size()) : "0";
        vectorRadioButton.setText(VECTOR_COMMAND + " - " + number);

        number = (MFMListBuilder.getRasterDisplayList() != null) ?
                MFMController.decimalFormater.format(MFMListBuilder.getRasterDisplayList().size()) : "0";
        rasterRadioButton.setText(RASTER_COMMAND + " - " + number);

        number = (MFMListBuilder.getLcdDisplayList() != null) ?
                MFMController.decimalFormater.format(MFMListBuilder.getLcdDisplayList().size()) : "0";
        lcdRadioButton.setText(LCD_COMMAND + " - " + number);

        number = (MFMListBuilder.getSimultaneousList() != null) ?
                MFMController.decimalFormater.format(MFMListBuilder.getSimultaneousList().size()) : "0";
        simultaneousCB.setText(SIMULTANEOUS_COMMAND + " - " + number);

    }

    /**
     * Adds listeners to all button types
     *
     * @param container List Builder container
     */
    private void addActionListeners(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof AbstractButton) {
                ((AbstractButton) component).addActionListener(this);
            } else if (component instanceof Container) {
                // System.out.println("ListBuilderUI adding actionListener to : " + component.getClass());
                addActionListeners((Container) component);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        // Need since we now add to the Label text
        if (command.contains(" -")) {
            command = command.substring(0, command.indexOf(MFM_Constants.SPACE_CHAR));
        }

        if (MFM.isDebug()) {
            if (MFM.isSystemDebug()) {
                System.out.println("ListBuilderUI command is: " + command);
            }
            MFM.getLogger().addToList("ListBuilderUI command is: " + command, true);
        }

        switch (command) {
            case SELECT_ALL_CATEGORIES_COMMAND:
                setSelectAllCategories(((AbstractButton) e.getSource()).isSelected());
                break;

            case VIEW_ALL_CATEGORIES_COMMAND:
                showCategories();
                break;

            case ARCADE_ONLY_COMMAND:
                showCategories();
                if ((((AbstractButton) e.getSource()).isSelected())) {
                    MFMListBuilder.Builder.MAME.setState(MFMListBuilder.ARCADE);
                }
                break;

            case SYSTEMS_ONLY_COMMAND:
                showCategories();
                if ((((AbstractButton) e.getSource()).isSelected())) {
                    MFMListBuilder.Builder.MAME.setState(MFMListBuilder.SYSTEMS);
                }
                break;

            case NO_MATURE_COMMAND:
                showCategories();
                MFMListBuilder.Builder.setNoMature(((AbstractButton) e.getSource()).isSelected());
                break;

            case NO_MECHANICAL_COMMAND:
                MFMListBuilder.Builder.setNoMechanical(((AbstractButton) e.getSource()).isSelected());
                break;

            case BOTH_COMMAND:
                showCategories();
                if ((((AbstractButton) e.getSource()).isSelected())) {
                    MFMListBuilder.Builder.MAME.setState(TriState.BOTH);
                }
                break;

            case ALL_ORIENTATIONS_COMMAND:
                if ((((AbstractButton) e.getSource()).isSelected())) {
                    MFMListBuilder.Builder.orientation.setState(QuadState.ALL);
                }
                break;

            case VERTICAL_COMMAND:
                if ((((AbstractButton) e.getSource()).isSelected())) {
                    MFMListBuilder.Builder.orientation.setState(MFMListBuilder.VERTICAL);
                }
                break;

            case HORIZONTAL_COMMAND:
                if ((((AbstractButton) e.getSource()).isSelected())) {
                    MFMListBuilder.Builder.orientation.setState(MFMListBuilder.HORIZONTAL);
                }
                break;

            case COCKTAIL_COMMAND:
                if ((((AbstractButton) e.getSource()).isSelected())) {
                    MFMListBuilder.Builder.orientation.setState(MFMListBuilder.COCKTAIL);
                }
                break;

            case ALL_DISPLAY_TYPES_COMMAND:
                if ((((AbstractButton) e.getSource()).isSelected())) {
                    MFMListBuilder.Builder.displayType.setState(QuadState.ALL);
                }
                break;

            case RASTER_COMMAND:
                if ((((AbstractButton) e.getSource()).isSelected())) {
                    MFMListBuilder.Builder.displayType.setState(MFMListBuilder.RASTER);
                }
                break;

            case VECTOR_COMMAND:
                if ((((AbstractButton) e.getSource()).isSelected())) {
                    MFMListBuilder.Builder.displayType.setState(MFMListBuilder.VECTOR);
                }
                break;

            case LCD_COMMAND:
                if ((((AbstractButton) e.getSource()).isSelected())) {
                    MFMListBuilder.Builder.displayType.setState(MFMListBuilder.LCD);
                }
                break;

            case GAMEBUTTONS_ORLESS_COMMAND:
                MFMListBuilder.Builder.setOrLessButtons((((AbstractButton) e.getSource()).isSelected()));
                break;

            case PLAYERS_ORLESS_COMMAND:
                MFMListBuilder.Builder.setOrLessPlayers(((AbstractButton) e.getSource()).isSelected());
                break;

            case SIMULTANEOUS_COMMAND:
                MFMListBuilder.Builder.setSimultaneousOnly(((AbstractButton) e.getSource()).isSelected());
                break;

            case NOCLONES_COMMAND:
                MFMListBuilder.Builder.setNoClones(((AbstractButton) e.getSource()).isSelected());
                break;

            case CREATE_LIST_COMMAND:
                createList(listNameTF.getText());
                break;

            case IMPORT_LIST_COMMAND:
                MFMController.importList();
                break;

            case LIST_EDITOR:
                MFMController.showListEditor();
                break;
            default:
                break;

        }
    }

    private void createList(String listName) {
        builder.setCategories(((DynamicCBpanel) categoriesCBpanel).getChecked());
        builder.setControls(MachineControllers.getMAMEControllerNames(((DynamicCBpanel) controlsCBPanel).getChecked()));
        //this is an example of wrong implementation. When ui open, action based values not set at all... so i fix some
        builder.setNoImperfect(noImperfectCheckBox.isSelected());
        builder.setNoMature(noMatureCB.isSelected());
        builder.setNoClones(noClonesCheckBox.isSelected());
        builder.setNoMechanical(noMechanicalCheckBox.isSelected());
        MFMListBuilder.Builder.setLanguage((String) languagesComboBox.getSelectedItem());
        MFMListBuilder.Builder.setWays((String) joyComboBox.getSelectedItem());
        MFMListBuilder.Builder.setWays2((String) doubleJoyComboBox.getSelectedItem());
        MFMListBuilder.Builder.setSimultaneousOnly(simultaneousCB.isSelected());
        if (anyControlsRB.isSelected()) {
            MFMListBuilder.Builder.ControlsFilterType.setState(MFMListBuilder.ANY_CONTROLS);
        } else if (exactControlsRB.isSelected()) {
            MFMListBuilder.Builder.ControlsFilterType.setState(MFMListBuilder.EXACT_CONTROLS);
        } else {
            MFMListBuilder.Builder.ControlsFilterType.setState(MFMListBuilder.ALL_CONTROLS);
        }
        String selected = (String) gameButtonsNum.getSelectedItem();
        MFMListBuilder.Builder.setButtons(selected);
        MFMListBuilder.Builder.setPlayers((int) gamePlayersNum.getSelectedItem());
        MFMListBuilder.Builder.setYear((String) yearComboBox.getSelectedItem());
        MFMListBuilder.Builder.setBaseListName((String) baseListComboBox.getSelectedItem());

        SortedSet<String> list = builder.generateList();
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(listBuilderPanel, listName + " : list is empty");
            return;
        }

        String[] machines = new String[list.size()];
        Arrays.asList(list.toArray()).toArray(machines);
        MFMListBuilder.createPlayList(listName, machines);
        listBuilderPanel.getTopLevelAncestor().requestFocus();
    }

    private void setSelectAllCategories(boolean isSelected) {
        Iterable<JCheckBox> checkBoxes = ((DynamicCBpanel) categoriesCBpanel).getCheckBoxes();
        for (JCheckBox cb : checkBoxes) {
            cb.setSelected(isSelected);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void setSelectAllControls(boolean isSelected) {
        Iterable<JCheckBox> checkBoxes = ((DynamicCBpanel) controlsCBPanel).getCheckBoxes();
        for (JCheckBox cb : checkBoxes) {
            cb.setSelected(isSelected);
        }
    }

    private void showCategories() {

        Container container = categoriesCBpanel.getParent();
        container.remove(categoriesCBpanel);

/*
    Four boolean conditions

    Root only ( viewAllCategoriesCheckBox )
    System only ( systemsOnlyRadioButton )
    Arcade Only ( arcadeOnlyRadioButton )
    No Mature ( noMatureCB )
 */
        categoriesCBpanel = new DynamicCBpanel(MFMListBuilder.getCategoriesList(
                viewAllCategoriesCheckBox.isSelected(),
                arcadeOnlyRadioButton.isSelected(),
                systemsOnlyRadioButton.isSelected(),
                noMatureCB.isSelected()), 2);

        if (selectAllCheckBox.isSelected()) {
            setSelectAllCategories(true);
        }
        SwingUtils.changeFont(categoriesCBpanel, container.getFont().getSize());
        container.add(categoriesCBpanel);
        refresh();
    }

    private void refresh() {
        listBuilderPanel.validate();
        listBuilderPanel.repaint();
    }

    private void createUIComponents() {
        listBuilderPanel = new JPanel();

        gameButtonsNum = new JComboBox<>(MAMEInfo.getNumButtons());
        gameButtonsNum.setSelectedIndex(4);
        gamePlayersNum = new JComboBox<>(MAMEInfo.getNumPlayers());

        categoriesCBpanel = new DynamicCBpanel(MFMListBuilder.getAllNoMatureCategoryRoots(), 2);

        // Systems/Arcade
        ButtonGroup bg = new ButtonGroup();
        bg.add(bothRadioButton);
        bg.add(arcadeOnlyRadioButton);
        bg.add(systemsOnlyRadioButton);
        bothRadioButton.setSelected(true);

        // Orientation
        ButtonGroup bg2 = new ButtonGroup();
        bg2.add(allRadioButton);
        bg2.add(horizontalRadioButton);
        bg2.add(verticalRadioButton);
        bg2.add(cocktailRadioButton);
        allRadioButton.setSelected(true);

        // Display Type
        ButtonGroup bg3 = new ButtonGroup();
        bg3.add(allDisplaysRadioButton);
        bg3.add(rasterRadioButton);
        bg3.add(vectorRadioButton);
        bg3.add(lcdRadioButton);
        allDisplaysRadioButton.setSelected(true);

        // Controls filtering type
        ButtonGroup bg4 = new ButtonGroup();
        bg4.add(anyControlsRB);
        bg4.add(allControlsRB);
        bg4.add(exactControlsRB);
        anyControlsRB.setSelected(true);

//****************************************************
        // TreeSet to apply natural ordering
        TreeSet<String> controllersLabels;
        if (MachineControllers.getControllerMAMEtoLabel() != null) {
            controllersLabels = new TreeSet<>(MachineControllers.getControllerMAMEtoLabel().values());
        } else {
            JOptionPane.showMessageDialog(null, "MAME_Controllers.ini file missing!");
            return;
        }

        controlsCBPanel = new DynamicCBpanel(new ArrayList<>(controllersLabels), 1);

        Map<String, String> tooltips = new HashMap<>(20);
        for (String label : controllersLabels) {
            String revLabel = MachineControllers.getControllerLabeltoMAME().get(label);
            if ((revLabel != null) && !MFMUI_Resources.getInstance().getLabelLocation(revLabel).isEmpty()) {
                tooltips.put(label,
                        "<html><img src=\"" + MFMUI_Resources.getInstance().getLabelLocation(revLabel));
            }
        }
        ((DynamicCBpanel) controlsCBPanel).setToolTips(tooltips);
//****************************************************

        controlsScrollPane = new JScrollPane();
        controlsScrollPane.getVerticalScrollBar().setUnitIncrement(24);
        controlsScrollPane.getHorizontalScrollBar().setUnitIncrement(24);

        categoriesScrollPane = new JScrollPane();
        categoriesScrollPane.getVerticalScrollBar().setUnitIncrement(24);
        categoriesScrollPane.getHorizontalScrollBar().setUnitIncrement(24);

        saveListToFileButton = new JButton(new MFMAction(MFMAction.SAVE_LIST_TO_FILE, null));

        joyComboBox = new JComboBox<>(MachineControllers.getJoysticks());
        doubleJoyComboBox = new JComboBox<>(MachineControllers.getDoubleJoysticks());

        languagesComboBox = new JComboBox<>(MFMPlayLists.getInstance().getLanguagesPlayListsKeys());
        languagesComboBox.setBorder(new BevelBorder(BevelBorder.RAISED, Color.gray, Color.gray));

        yearComboBox = new JComboBox<>(MFM_Constants.yearsList);
        yearComboBox.setBorder(new BevelBorder(BevelBorder.RAISED, Color.gray, Color.gray));

        baseListComboBox = new JComboBox<>(MFMPlayLists.getInstance().getListBuilderNames());
    }


}
