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

package Phweda.MFM.UI;

import Phweda.MFM.*;
import Phweda.utils.QuadState;
import Phweda.utils.SwingUtils;
import Phweda.utils.TriState;

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

import static Phweda.MFM.UI.MFMAction.LIST_EDITOR;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/19/2015
 * Time: 1:21 PM
 */
@SuppressWarnings({"squid:S1450", "DuplicateStringLiteralInspection"})
public final class ListBuilderUI implements ActionListener, ItemListener, Serializable {

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

            $$$setupUI$$$();

            setLabelsText();
            addActionListeners(listBuilderPanel);
            addItemListeners();
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

    private void addItemListeners() {
        gameButtonsNum.addItemListener(this);
        gamePlayersNum.addItemListener(this);

        joyComboBox.addItemListener(this);
        doubleJoyComboBox.addItemListener(this);

        yearComboBox.addItemListener(this);
        baseListComboBox.addItemListener(this);
        languagesComboBox.addItemListener(this);
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

            case ANY_CONTROLS_COMMAND:
                MFMListBuilder.Builder.ControlsFilterType.setState(MFMListBuilder.ANY_CONTROLS);
                break;

            case ALL_CONTROLS_COMMAND:
                MFMListBuilder.Builder.ControlsFilterType.setState(MFMListBuilder.ALL_CONTROLS);
                break;

            case EXACT_CONTROLS_COMMAND:
                MFMListBuilder.Builder.ControlsFilterType.setState(MFMListBuilder.EXACT_CONTROLS);
                break;
            default:
                break;

        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (MFM.isDebug()) {
            if (MFM.isSystemDebug()) {
                System.out.println("ListBuilderUI itemState is: " + e.paramString());
            }
            MFM.getLogger().addToList("ListBuilderUI itemState is: " + e.paramString(), true);
        }

        if (e.getSource() == languagesComboBox) {
            MFMListBuilder.Builder.setLanguage((String) languagesComboBox.getSelectedItem());
        } else if (e.getSource() == joyComboBox) {
            if (!((String) Objects.requireNonNull(
                    joyComboBox.getSelectedItem())).equalsIgnoreCase(ALL_ORIENTATIONS_COMMAND)) {
                ((DynamicCBpanel) controlsCBPanel).getJCheckBoxbyText(
                        MachineControllers.getMAMEControllerLabelName("joy")).setSelected(true);
                MFMListBuilder.Builder.setWays((String) joyComboBox.getSelectedItem());
            }
        } else if (e.getSource() == doubleJoyComboBox) {
            if (!((String) Objects.requireNonNull(
                    doubleJoyComboBox.getSelectedItem())).equalsIgnoreCase(ALL_ORIENTATIONS_COMMAND)) {
                ((DynamicCBpanel) controlsCBPanel).getJCheckBoxbyText(
                        MachineControllers.getMAMEControllerLabelName("doublejoy")).setSelected(true);
                MFMListBuilder.Builder.setWays2((String) doubleJoyComboBox.getSelectedItem());
            }
        } else if (e.getSource() == gameButtonsNum) {
            String selected = (String) gameButtonsNum.getSelectedItem();
            // if it is 9+
            assert selected != null;
            if (selected.contains("+")) {
                selected = "9";
            }
            MFMListBuilder.Builder.setButtons(selected);
        } else if (e.getSource() == gamePlayersNum) {
            MFMListBuilder.Builder.setPlayers((int) gamePlayersNum.getSelectedItem());
        } else if (e.getSource() == yearComboBox) {
            MFMListBuilder.Builder.setYear((String) yearComboBox.getSelectedItem());
        } else if (e.getSource() == baseListComboBox) {
            MFMListBuilder.Builder.setBaseListName((String) baseListComboBox.getSelectedItem());
        }
    }

    private void createList(String listName) {
        builder.setCategories(((DynamicCBpanel) categoriesCBpanel).getChecked());
        builder.setControls(MachineControllers.getMAMEControllerNames(((DynamicCBpanel) controlsCBPanel).getChecked()));

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


    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        listBuilderPanel.setLayout(new GridBagLayout());
        listBuilderPanel.setAlignmentX(1.0f);
        listBuilderPanel.setAlignmentY(1.0f);
        listBuilderPanel.setMaximumSize(new Dimension(1280, 960));
        listBuilderPanel.setMinimumSize(new Dimension(1080, 750));
        listBuilderPanel.setPreferredSize(new Dimension(1280, 920));
        listBuilderPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setMaximumSize(new Dimension(1000, 100));
        buttonPanel.setMinimumSize(new Dimension(700, 70));
        buttonPanel.setPreferredSize(new Dimension(700, 70));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        listBuilderPanel.add(buttonPanel, gbc);
        buttonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null));
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.weightx = 0.025;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer2, gbc);
        createListButton = new JButton();
        createListButton.setLabel("Create List");
        createListButton.setMargin(new Insets(2, 16, 2, 16));
        createListButton.setMaximumSize(new Dimension(140, 33));
        createListButton.setMinimumSize(new Dimension(140, 33));
        createListButton.setPreferredSize(new Dimension(140, 33));
        createListButton.setText("Create List");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(createListButton, gbc);
        listNameLabel = new JLabel();
        listNameLabel.setEnabled(true);
        listNameLabel.setHorizontalAlignment(0);
        listNameLabel.setHorizontalTextPosition(0);
        listNameLabel.setText("List Name");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        buttonPanel.add(listNameLabel, gbc);
        listNameTF = new JTextField();
        listNameTF.setMargin(new Insets(3, 6, 3, 6));
        listNameTF.setMaximumSize(new Dimension(255, 33));
        listNameTF.setMinimumSize(new Dimension(255, 33));
        listNameTF.setOpaque(true);
        listNameTF.setPreferredSize(new Dimension(255, 33));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(listNameTF, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer3, gbc);
        importListButton = new JButton();
        importListButton.setMargin(new Insets(2, 16, 2, 16));
        importListButton.setMaximumSize(new Dimension(140, 33));
        importListButton.setMinimumSize(new Dimension(140, 33));
        importListButton.setPreferredSize(new Dimension(140, 33));
        importListButton.setText("Import List");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(importListButton, gbc);
        saveListToFileButton.setMargin(new Insets(2, 16, 2, 16));
        saveListToFileButton.setMaximumSize(new Dimension(140, 33));
        saveListToFileButton.setMinimumSize(new Dimension(140, 33));
        saveListToFileButton.setPreferredSize(new Dimension(140, 33));
        saveListToFileButton.setText("Save List to File");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(saveListToFileButton, gbc);
        baseListComboBox.setMinimumSize(new Dimension(135, 33));
        baseListComboBox.setPreferredSize(new Dimension(135, 33));
        baseListComboBox.setToolTipText("List to filter");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        gbc.weighty = 0.8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(baseListComboBox, gbc);
        baseListLabel = new JLabel();
        baseListLabel.setEnabled(true);
        baseListLabel.setHorizontalAlignment(0);
        baseListLabel.setHorizontalTextPosition(2);
        baseListLabel.setMaximumSize(new Dimension(135, 21));
        baseListLabel.setMinimumSize(new Dimension(135, 21));
        baseListLabel.setPreferredSize(new Dimension(135, 21));
        baseListLabel.setText("Set Base List");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        buttonPanel.add(baseListLabel, gbc);
        uileftPanel = new JPanel();
        uileftPanel.setLayout(new GridBagLayout());
        uileftPanel.setMaximumSize(new Dimension(32767, 32767));
        uileftPanel.setMinimumSize(new Dimension(235, 660));
        uileftPanel.setPreferredSize(new Dimension(235, 660));
        uileftPanel.setRequestFocusEnabled(true);
        uileftPanel.setVerifyInputWhenFocusTarget(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 6;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        listBuilderPanel.add(uileftPanel, gbc);
        uileftPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        mamePanel = new JPanel();
        mamePanel.setLayout(new GridBagLayout());
        mamePanel.setMaximumSize(new Dimension(2147483647, 2147483647));
        mamePanel.setMinimumSize(new Dimension(130, 120));
        mamePanel.setPreferredSize(new Dimension(130, 120));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        uileftPanel.add(mamePanel, gbc);
        mamePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "MAME", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        arcadeOnlyRadioButton.setActionCommand("Arcade Only");
        arcadeOnlyRadioButton.setLabel("Arcade Only");
        arcadeOnlyRadioButton.setMaximumSize(new Dimension(220, 30));
        arcadeOnlyRadioButton.setMinimumSize(new Dimension(220, 30));
        arcadeOnlyRadioButton.setPreferredSize(new Dimension(220, 30));
        arcadeOnlyRadioButton.setSelected(false);
        arcadeOnlyRadioButton.setText("Arcade Only");
        arcadeOnlyRadioButton.setToolTipText("Original MAME Arcade games");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mamePanel.add(arcadeOnlyRadioButton, gbc);
        systemsOnlyRadioButton.setActionCommand("Systems Only");
        systemsOnlyRadioButton.setLabel("Systems Only");
        systemsOnlyRadioButton.setMaximumSize(new Dimension(230, 30));
        systemsOnlyRadioButton.setMinimumSize(new Dimension(230, 30));
        systemsOnlyRadioButton.setPreferredSize(new Dimension(230, 30));
        systemsOnlyRadioButton.setText("Systems Only");
        systemsOnlyRadioButton.setToolTipText("MESS Systems");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mamePanel.add(systemsOnlyRadioButton, gbc);
        bothRadioButton.setMaximumSize(new Dimension(220, 30));
        bothRadioButton.setMinimumSize(new Dimension(220, 30));
        bothRadioButton.setPreferredSize(new Dimension(220, 30));
        bothRadioButton.setSelected(true);
        bothRadioButton.setText("Both");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mamePanel.add(bothRadioButton, gbc);
        orientationPanel = new JPanel();
        orientationPanel.setLayout(new GridBagLayout());
        orientationPanel.setMaximumSize(new Dimension(2147483647, 165));
        orientationPanel.setMinimumSize(new Dimension(200, 150));
        orientationPanel.setPreferredSize(new Dimension(200, 150));
        orientationPanel.setRequestFocusEnabled(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        uileftPanel.add(orientationPanel, gbc);
        orientationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Orientation", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        horizontalRadioButton.setEnabled(true);
        horizontalRadioButton.setMaximumSize(new Dimension(200, 30));
        horizontalRadioButton.setMinimumSize(new Dimension(200, 30));
        horizontalRadioButton.setPreferredSize(new Dimension(200, 30));
        horizontalRadioButton.setText("Horizontal");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        orientationPanel.add(horizontalRadioButton, gbc);
        verticalRadioButton.setMaximumSize(new Dimension(200, 30));
        verticalRadioButton.setMinimumSize(new Dimension(200, 30));
        verticalRadioButton.setPreferredSize(new Dimension(200, 30));
        verticalRadioButton.setText("Vertical");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        orientationPanel.add(verticalRadioButton, gbc);
        cocktailRadioButton.setActionCommand("Cocktail");
        cocktailRadioButton.setMaximumSize(new Dimension(200, 30));
        cocktailRadioButton.setMinimumSize(new Dimension(200, 30));
        cocktailRadioButton.setPreferredSize(new Dimension(200, 30));
        cocktailRadioButton.setText("Cocktail");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        orientationPanel.add(cocktailRadioButton, gbc);
        allRadioButton.setMaximumSize(new Dimension(200, 30));
        allRadioButton.setMinimumSize(new Dimension(200, 30));
        allRadioButton.setPreferredSize(new Dimension(200, 30));
        allRadioButton.setText("All");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        orientationPanel.add(allRadioButton, gbc);
        displayTypePanel = new JPanel();
        displayTypePanel.setLayout(new GridBagLayout());
        displayTypePanel.setMaximumSize(new Dimension(2147483647, 160));
        displayTypePanel.setMinimumSize(new Dimension(124, 130));
        displayTypePanel.setPreferredSize(new Dimension(124, 130));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        uileftPanel.add(displayTypePanel, gbc);
        displayTypePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Display Type", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        rasterRadioButton.setActionCommand("Raster");
        rasterRadioButton.setMaximumSize(new Dimension(200, 30));
        rasterRadioButton.setMinimumSize(new Dimension(200, 30));
        rasterRadioButton.setPreferredSize(new Dimension(200, 30));
        rasterRadioButton.setText("Raster");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        displayTypePanel.add(rasterRadioButton, gbc);
        lcdRadioButton.setMaximumSize(new Dimension(200, 30));
        lcdRadioButton.setMinimumSize(new Dimension(200, 30));
        lcdRadioButton.setPreferredSize(new Dimension(200, 30));
        lcdRadioButton.setText("LCD");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        displayTypePanel.add(lcdRadioButton, gbc);
        vectorRadioButton.setMaximumSize(new Dimension(200, 30));
        vectorRadioButton.setMinimumSize(new Dimension(200, 30));
        vectorRadioButton.setPreferredSize(new Dimension(200, 30));
        vectorRadioButton.setText("Vector");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        displayTypePanel.add(vectorRadioButton, gbc);
        allDisplaysRadioButton.setMaximumSize(new Dimension(200, 30));
        allDisplaysRadioButton.setMinimumSize(new Dimension(200, 30));
        allDisplaysRadioButton.setPreferredSize(new Dimension(200, 30));
        allDisplaysRadioButton.setText("All Displays");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        displayTypePanel.add(allDisplaysRadioButton, gbc);
        filterJPanel = new JPanel();
        filterJPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        filterJPanel.setMinimumSize(new Dimension(400, 110));
        filterJPanel.setPreferredSize(new Dimension(400, 110));
        filterJPanel.setVerifyInputWhenFocusTarget(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        uileftPanel.add(filterJPanel, gbc);
        filterJPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        noClonesCheckBox = new JCheckBox();
        noClonesCheckBox.setMaximumSize(new Dimension(235, 20));
        noClonesCheckBox.setMinimumSize(new Dimension(200, 20));
        noClonesCheckBox.setPreferredSize(new Dimension(200, 20));
        noClonesCheckBox.setText("No Clones");
        filterJPanel.add(noClonesCheckBox);
        noMatureCB = new JCheckBox();
        noMatureCB.setLabel("No Mature");
        noMatureCB.setMaximumSize(new Dimension(200, 20));
        noMatureCB.setMinimumSize(new Dimension(200, 20));
        noMatureCB.setPreferredSize(new Dimension(200, 20));
        noMatureCB.setSelected(false);
        noMatureCB.setText("No Mature");
        noMatureCB.setToolTipText("Eliminates any category containing Mature");
        filterJPanel.add(noMatureCB);
        noImperfectCheckBox = new JCheckBox();
        noImperfectCheckBox.setHorizontalAlignment(2);
        noImperfectCheckBox.setMaximumSize(new Dimension(200, 20));
        noImperfectCheckBox.setMinimumSize(new Dimension(200, 20));
        noImperfectCheckBox.setOpaque(false);
        noImperfectCheckBox.setPreferredSize(new Dimension(200, 20));
        noImperfectCheckBox.setText("No Imperfect");
        filterJPanel.add(noImperfectCheckBox);
        noMechanicalCheckBox = new JCheckBox();
        noMechanicalCheckBox.setHorizontalAlignment(2);
        noMechanicalCheckBox.setMaximumSize(new Dimension(200, 20));
        noMechanicalCheckBox.setMinimumSize(new Dimension(200, 20));
        noMechanicalCheckBox.setPreferredSize(new Dimension(200, 20));
        noMechanicalCheckBox.setText("No Mechanical");
        filterJPanel.add(noMechanicalCheckBox);
        gameButtonsPanel = new JPanel();
        gameButtonsPanel.setLayout(new GridBagLayout());
        gameButtonsPanel.setMaximumSize(new Dimension(2147483647, 130));
        gameButtonsPanel.setMinimumSize(new Dimension(118, 50));
        gameButtonsPanel.setPreferredSize(new Dimension(118, 50));
        gameButtonsPanel.setRequestFocusEnabled(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        uileftPanel.add(gameButtonsPanel, gbc);
        gameButtonsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Game Buttons", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        gameButtonsNum.setActionCommand("GameButtons");
        gameButtonsNum.setMinimumSize(new Dimension(60, 30));
        gameButtonsNum.setPreferredSize(new Dimension(60, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gameButtonsPanel.add(gameButtonsNum, gbc);
        orLessButtonsCB = new JCheckBox();
        orLessButtonsCB.setActionCommand("GameButtonsorless");
        orLessButtonsCB.setSelected(true);
        orLessButtonsCB.setText("or less");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gameButtonsPanel.add(orLessButtonsCB, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setMaximumSize(new Dimension(2147483647, 90));
        panel1.setMinimumSize(new Dimension(127, 80));
        panel1.setPreferredSize(new Dimension(127, 80));
        panel1.setRequestFocusEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 0);
        uileftPanel.add(panel1, gbc);
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Players", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        gamePlayersNum.setActionCommand("Players");
        gamePlayersNum.setMinimumSize(new Dimension(60, 33));
        gamePlayersNum.setPreferredSize(new Dimension(60, 33));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(gamePlayersNum, gbc);
        orLessPlayersCB = new JCheckBox();
        orLessPlayersCB.setActionCommand("Playersorless");
        orLessPlayersCB.setEnabled(true);
        orLessPlayersCB.setInheritsPopupMenu(false);
        orLessPlayersCB.setLabel("or less");
        orLessPlayersCB.setSelected(true);
        orLessPlayersCB.setText("or less");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(orLessPlayersCB, gbc);
        simultaneousCB = new JCheckBox();
        simultaneousCB.setText("Simultaneous");
        simultaneousCB.setToolTipText("Games that offer simultaneous mode");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(simultaneousCB, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer4, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel2.setMaximumSize(new Dimension(32767, 32767));
        panel2.setMinimumSize(new Dimension(200, 110));
        panel2.setOpaque(false);
        panel2.setPreferredSize(new Dimension(200, 110));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;
        uileftPanel.add(panel2, gbc);
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Joysticks", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        joyComboBox.setActionCommand("joyCBChanged");
        joyComboBox.setMinimumSize(new Dimension(80, 25));
        joyComboBox.setPreferredSize(new Dimension(80, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(joyComboBox, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer5, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Joy");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(label1, gbc);
        doubleJoyComboBox.setActionCommand("doublejoyCBCommand");
        doubleJoyComboBox.setMinimumSize(new Dimension(80, 25));
        doubleJoyComboBox.setPreferredSize(new Dimension(80, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(doubleJoyComboBox, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("DBL Joy");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(label2, gbc);
        exactMatchOnlyCheckBox = new JCheckBox();
        exactMatchOnlyCheckBox.setMaximumSize(new Dimension(200, 25));
        exactMatchOnlyCheckBox.setMinimumSize(new Dimension(200, 25));
        exactMatchOnlyCheckBox.setPreferredSize(new Dimension(200, 25));
        exactMatchOnlyCheckBox.setText("Exact Match Only");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(exactMatchOnlyCheckBox, gbc);
        languagesComboBox.setToolTipText("Languages Filter");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        uileftPanel.add(languagesComboBox, gbc);
        yearComboBox.setMinimumSize(new Dimension(235, 33));
        yearComboBox.setToolTipText("Year Filter");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        uileftPanel.add(yearComboBox, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.VERTICAL;
        uileftPanel.add(spacer6, gbc);
        categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BorderLayout(0, 0));
        categoriesPanel.setMaximumSize(new Dimension(32767, 32767));
        categoriesPanel.setMinimumSize(new Dimension(800, 800));
        categoriesPanel.setOpaque(true);
        categoriesPanel.setPreferredSize(new Dimension(900, 960));
        categoriesPanel.setRequestFocusEnabled(false);
        categoriesPanel.setVerifyInputWhenFocusTarget(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        listBuilderPanel.add(categoriesPanel, gbc);
        categoriesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Categories", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        categoriesPanel.add(panel3, BorderLayout.NORTH);
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        viewAllCategoriesCheckBox = new JCheckBox();
        viewAllCategoriesCheckBox.setActionCommand("View All Categories");
        viewAllCategoriesCheckBox.setLabel("View All Categories");
        viewAllCategoriesCheckBox.setText("View All Categories");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        panel3.add(viewAllCategoriesCheckBox, gbc);
        selectAllCheckBox = new JCheckBox();
        selectAllCheckBox.setActionCommand("Select All Categories");
        selectAllCheckBox.setLabel("(un)Select All Categories");
        selectAllCheckBox.setText("(un)Select All Categories");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel3.add(selectAllCheckBox, gbc);
        categoriesScrollPane.setPreferredSize(new Dimension(600, 600));
        categoriesScrollPane.setVerticalScrollBarPolicy(22);
        categoriesPanel.add(categoriesScrollPane, BorderLayout.CENTER);
        categoriesCBpanel.setMinimumSize(new Dimension(520, 580));
        categoriesCBpanel.setPreferredSize(new Dimension(560, 600));
        categoriesCBpanel.setRequestFocusEnabled(false);
        categoriesScrollPane.setViewportView(categoriesCBpanel);
        uiRightPanel = new JPanel();
        uiRightPanel.setLayout(new BorderLayout(0, 0));
        uiRightPanel.setMinimumSize(new Dimension(210, 645));
        uiRightPanel.setPreferredSize(new Dimension(210, 595));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 5;
        gbc.fill = GridBagConstraints.BOTH;
        listBuilderPanel.add(uiRightPanel, gbc);
        uiRightPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        uiControlsScrollPanel = new JPanel();
        uiControlsScrollPanel.setLayout(new BorderLayout(0, 0));
        uiRightPanel.add(uiControlsScrollPanel, BorderLayout.CENTER);
        uiControlsScrollPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        controlsScrollPane.setMaximumSize(new Dimension(175, 32767));
        controlsScrollPane.setMinimumSize(new Dimension(175, 600));
        controlsScrollPane.setPreferredSize(new Dimension(175, 550));
        uiControlsScrollPanel.add(controlsScrollPane, BorderLayout.CENTER);
        controlsCBPanel.setAlignmentX(0.0f);
        controlsCBPanel.setAlignmentY(0.0f);
        controlsCBPanel.setMaximumSize(new Dimension(32767, 32767));
        controlsCBPanel.setMinimumSize(new Dimension(175, 500));
        controlsCBPanel.setOpaque(true);
        controlsCBPanel.setPreferredSize(new Dimension(175, 550));
        controlsCBPanel.setRequestFocusEnabled(true);
        controlsScrollPane.setViewportView(controlsCBPanel);
        controlsCBPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Controls", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        controlsRBPanel = new JPanel();
        controlsRBPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        uiRightPanel.add(controlsRBPanel, BorderLayout.SOUTH);
        anyControlsRB.setText("Any");
        controlsRBPanel.add(anyControlsRB);
        allControlsRB.setText("All");
        controlsRBPanel.add(allControlsRB);
        exactControlsRB.setText("Exact");
        controlsRBPanel.add(exactControlsRB);
        listNameLabel.setLabelFor(listNameTF);
        baseListLabel.setLabelFor(listNameTF);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return listBuilderPanel;
    }
}
