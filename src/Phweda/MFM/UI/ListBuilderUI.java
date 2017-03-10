
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

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/19/2015
 * Time: 1:21 PM
 */
public class ListBuilderUI implements ActionListener, ItemListener, Serializable {

    private static final ListBuilderUI ourInstance = new ListBuilderUI();

    private JPanel OrientationPanel;
    private JPanel GameButtonsPanel;
    private JScrollPane CategoriesScrollPane;
    private JPanel controlsCBPanel;
    private JPanel CategoriesPanel;
    private JPanel ListBuilderPanel;
    private JPanel buttonPanel;
    private JLabel listNameLabel;
    private JPanel UILeftPanel;
    private JPanel MAMEPanel;
    private JPanel categoriesCBpanel;

    private static final JTextArea newListGames = new JTextArea();

    private JComboBox<String> gameButtonsNum;
    private JComboBox<Integer> gamePlayersNum;
    private JTextField listNameTF;

    private JButton createListButton;
    private JButton populateListButton;
    private JButton gamePickerButton;
    private JButton importListButton;

    private JRadioButton bothRadioButton = new JRadioButton();
    private JRadioButton arcadeOnlyRadioButton = new JRadioButton();
    private JRadioButton systemsOnlyRadioButton = new JRadioButton();

    private JRadioButton allRadioButton = new JRadioButton();
    private JRadioButton cocktailRadioButton = new JRadioButton();
    private JRadioButton horizontalRadioButton = new JRadioButton();
    private JRadioButton verticalRadioButton = new JRadioButton();

    private JCheckBox orLessButtonsCB;
    private JCheckBox noClonesCheckBox;
    private JCheckBox orLessPlayersCB;
    private JCheckBox simultaneousCB;
    private JCheckBox viewAllCategoriesCheckBox;
    private JCheckBox noMatureCB;
    private JCheckBox selectAllCheckBox;

    private JPanel displayTypePanel;
    private JRadioButton rasterRadioButton = new JRadioButton();
    private JRadioButton LCDRadioButton = new JRadioButton();
    private JRadioButton vectorRadioButton = new JRadioButton();
    private JRadioButton allDisplaysRadioButton = new JRadioButton();

    private JButton diffListsButton;
    private JScrollPane controlsScrollPane;
    private JCheckBox noImperfectCheckBox;
    private JComboBox<String> joyComboBox;
    private JComboBox<String> doubleJoyComboBox;
    private JCheckBox exactMatchOnlyCheckBox;
    private JButton saveListToFileButton;
    private JPanel filterJPanel;
    private JComboBox<String> languagesCombobox;

    private static final String VIEW_ALL_CATEGORIES_COMMAND = "View All Categories";
    private static final String SELECT_ALL_CATEGORIES_COMMAND = "Select All Categories";
    private static final String ARCADE_ONLY_COMMAND = "Arcade only";
    private static final String NO_MATURE_COMMAND = "No Mature";
    private static final String SYSTEMS_ONLY_COMMAND = "Systems only";
    private static final String BOTH_COMMAND = "Both";

    private static final String ALL_COMMAND = "All";
    private static final String VERTICAL_COMMAND = "Vertical";
    private static final String HORIZONTAL_COMMAND = "Horizontal";
    private static final String COCKTAIL_COMMAND = "Cocktail";

    private static final String GAMEBUTTONS_COMMAND = "GameButtons";
    private static final String GAMEBUTTONS_ORLESS_COMMAND = "GameButtonsorless";
    private static final String PLAYERS_COMMAND = "Players";
    private static final String PLAYERS_ORLESS_COMMAND = "Playersorless";
    private static final String SIMULTANEOUS_COMMAND = "Simultaneous";
    private static final String NOCLONES_COMMAND = "No Clones";

    private static final String ALL_DISPLAY_TYPES_COMMAND = "All Displays";
    private static final String RASTER_COMMAND = "Raster";
    private static final String VECTOR_COMMAND = "Vector";
    private static final String LCD_COMMAND = "LCD";

    private static final String JOY_WAYS_COMMAND = "joyCBCommand";
    private static final String DOUBLEJOY_WAYS_COMMAND = "doublejoyCBCommand";

    private static final String CREATE_LIST = "Create List";
    private static final String IMPORT_LIST = "Import List";
    private static final String GAME_PICKER = "Game Picker";
    private static final String DIFF_LISTS = "Diff Lists";

    static final String Previous = "Previous";
    static final String New = "New";

    private static final MFMListBuilder.Builder builder = MFMListBuilder.Builder.getInstance();

    private static transient MFMController controller;

    private ListBuilderUI() {
        if (ListBuilderPanel == null) {
            $$$setupUI$$$();

            setLabelsText();
            addActionListeners(ListBuilderPanel);
            addItemListeners();
        }
    }

    public static ListBuilderUI getInstance() {
        return ourInstance;
    }

    JPanel getListBuilderPanel() {
        return ListBuilderPanel;
    }

    void setController() {
        // This is bad design
        controller = MFMUI_Setup.getController();
    }

    private void reset() {
        allRadioButton.setSelected(true);
        bothRadioButton.setSelected(true);
        allDisplaysRadioButton.setSelected(true);

        noClonesCheckBox.setSelected(false);
        noImperfectCheckBox.setSelected(false);
        noMatureCB.setSelected(false);

        setSelectAllCategories(false);
        setSelectAllControls(false);

        gameButtonsNum.setSelectedIndex(gameButtonsNum.getModel().getSize() - 1);
        orLessButtonsCB.setSelected(true);
        gamePlayersNum.setSelectedIndex(gamePlayersNum.getModel().getSize() - 1);
        orLessPlayersCB.setSelected(true);

        languagesCombobox.setSelectedIndex(0);
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
                LCDRadioButton.setSelected(true);
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
/*
            case MFMListBuilder. :
                .setSelected(true);
                break;
*/
            // Handle languages with default
            default:
                if (MFMPlayLists.getInstance().getLanguagesListMap().containsKey(state)) {
                    languagesCombobox.getModel().setSelectedItem(state);
                }
                break;
        }
    }

    private void setLabelsText() {

        arcadeOnlyRadioButton.setText(ARCADE_ONLY_COMMAND + " - " +
                MFMController.decimalFormater.format(MFMListBuilder.getArcadeList().size()));
        systemsOnlyRadioButton.setText(SYSTEMS_ONLY_COMMAND + " - " +
                MFMController.decimalFormater.format(MFMListBuilder.getSystemList().size()));

        verticalRadioButton.setText(VERTICAL_COMMAND + " - " +
                MFMController.decimalFormater.format(MFMListBuilder.getVerticalsList().size()));
        horizontalRadioButton.setText(HORIZONTAL_COMMAND + " - " +
                MFMController.decimalFormater.format(MFMListBuilder.getHorizontalsList().size()));
        cocktailRadioButton.setText(COCKTAIL_COMMAND + " - " +
                MFMController.decimalFormater.format(MFMListBuilder.getCocktailsList().size()));

        vectorRadioButton.setText(VECTOR_COMMAND + " - " +
                MFMController.decimalFormater.format(MFMListBuilder.getVectorDisplayList().size()));
        rasterRadioButton.setText(RASTER_COMMAND + " - " +
                MFMController.decimalFormater.format(MFMListBuilder.getRasterDisplayList().size()));
        LCDRadioButton.setText(LCD_COMMAND + " - " +
                MFMController.decimalFormater.format(MFMListBuilder.getLcdDisplayList().size()));

        simultaneousCB.setText(SIMULTANEOUS_COMMAND + " - " +
                MFMController.decimalFormater.format(MFMListBuilder.getSimultaneousList().size()));

    }


    private void addItemListeners() {
        gameButtonsNum.addItemListener(this);
        gamePlayersNum.addItemListener(this);

        joyComboBox.addItemListener(this);
        doubleJoyComboBox.addItemListener(this);
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
            command = command.substring(0, command.indexOf(' '));
        }

        if (MFM.isDebug()) {
            MFM.logger.addToList("ListBuilderUI command is: " + command, true);
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

            case BOTH_COMMAND:
                showCategories();
                if ((((AbstractButton) e.getSource()).isSelected())) {
                    MFMListBuilder.Builder.MAME.setState(TriState.BOTH);
                }
                break;

            case ALL_COMMAND:
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

            // fixme comboboxes actioncommand not working should it be itemlistener???
            // NOTE action command is generated by the drop down arrow so guess itemlistener is correct.
            case GAMEBUTTONS_COMMAND:
                MFMListBuilder.Builder.buttons = Integer.getInteger(
                        (String) ((JComboBox<String>) e.getSource()).getSelectedItem());
                break;

            case GAMEBUTTONS_ORLESS_COMMAND:
                MFMListBuilder.Builder.orLessButtons = (((AbstractButton) e.getSource()).isSelected());
                break;

            case PLAYERS_COMMAND:
                MFMListBuilder.Builder.players = Integer.getInteger(
                        (String) ((JComboBox<String>) e.getSource()).getSelectedItem());
                break;

            case PLAYERS_ORLESS_COMMAND:
                MFMListBuilder.Builder.orLessPlayers = (((AbstractButton) e.getSource()).isSelected());
                break;

            case SIMULTANEOUS_COMMAND:
                MFMListBuilder.Builder.simultaneousOnly = (((AbstractButton) e.getSource()).isSelected());
                break;

            case NOCLONES_COMMAND:
                MFMListBuilder.Builder.noClones = (((AbstractButton) e.getSource()).isSelected());
                break;

            case CREATE_LIST:
                createList(listNameTF.getText());
                break;

            case IMPORT_LIST:
                String listName = MFMListBuilder.importList(getListBuilderPanel());
                MFMUI_Setup.updateMenuBar(listName);
                break;

            case GAME_PICKER:
                // replaced with MFMAction
                // showMachinePicker();
                break;

            case DIFF_LISTS:
                MFMListBuilder.diffLists(MFMController.getFrame());
                break;
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == joyComboBox) {
            if (!((String) joyComboBox.getSelectedItem()).equalsIgnoreCase(ALL_COMMAND)) {
                ((DynamicCBpanel) controlsCBPanel).getJCheckBoxbyText(
                        Controllers.getMAMEControllerLabelName("joy")).setSelected(true);
                MFMListBuilder.Builder.setWays((String) joyComboBox.getSelectedItem());
            }
        } else if (e.getSource() == doubleJoyComboBox) {
            if (!((String) doubleJoyComboBox.getSelectedItem()).equalsIgnoreCase(ALL_COMMAND)) {
                ((DynamicCBpanel) controlsCBPanel).getJCheckBoxbyText(
                        Controllers.getMAMEControllerLabelName("doublejoy")).setSelected(true);
                MFMListBuilder.Builder.setWays2((String) doubleJoyComboBox.getSelectedItem());
            }
        } else if (e.getSource() == gameButtonsNum) {
            String selected = (String) gameButtonsNum.getSelectedItem();
            // if it is 9+
            if (selected.contains("+")) {
                selected = "9";
            }
            MFMListBuilder.Builder.setButtons(selected);
        } else if (e.getSource() == gamePlayersNum) {
            MFMListBuilder.Builder.setPlayers((Integer) gamePlayersNum.getSelectedItem());
        }
    }

    private void createList(String listName) {

        builder.setCategories(((DynamicCBpanel) categoriesCBpanel).getChecked());
        // fixme reverse the Controller names Done test 12/24/2016
        builder.setControls(Controllers.getMAMEControllerNames(((DynamicCBpanel) controlsCBPanel).getChecked()));

        // Do here since Builder State is NOT retained in all circumstances
        MFMListBuilder.Builder.setButtons((String) gameButtonsNum.getSelectedItem());
        MFMListBuilder.Builder.setPlayers((Integer) gamePlayersNum.getSelectedItem());


        if (bothRadioButton.isSelected()) {
            MFMListBuilder.Builder.MAME.setState(TriState.BOTH);
        } else if (arcadeOnlyRadioButton.isSelected()) {
            MFMListBuilder.Builder.MAME.setState(MFMListBuilder.ARCADE);
        } else if (systemsOnlyRadioButton.isSelected()) {
            MFMListBuilder.Builder.MAME.setState(MFMListBuilder.SYSTEMS);
        }

        if (allDisplaysRadioButton.isSelected()) {
            MFMListBuilder.Builder.MAME.setState(QuadState.ALL);
        } else if (horizontalRadioButton.isSelected()) {
            MFMListBuilder.Builder.MAME.setState(MFMListBuilder.HORIZONTAL);
        } else if (verticalRadioButton.isSelected()) {
            MFMListBuilder.Builder.MAME.setState(MFMListBuilder.VERTICAL);
        } else if (LCDRadioButton.isSelected()) {
            MFMListBuilder.Builder.MAME.setState(MFMListBuilder.LCD);
        }


        MFMListBuilder.Builder.setOrLessButtons(orLessButtonsCB.isSelected());
        MFMListBuilder.Builder.setOrLessPlayers(orLessPlayersCB.isSelected());

        MFMListBuilder.Builder.setWays((String) joyComboBox.getSelectedItem());
        MFMListBuilder.Builder.setWays2((String) doubleJoyComboBox.getSelectedItem());
        MFMListBuilder.Builder.setExactMatch(exactMatchOnlyCheckBox.isSelected());

        MFMListBuilder.Builder.setNoMature(noMatureCB.isSelected());
        MFMListBuilder.Builder.setNoClones(noClonesCheckBox.isSelected());
        MFMListBuilder.Builder.setNoImperfect(noImperfectCheckBox.isSelected());
        MFMListBuilder.Builder.setLanguage(languagesCombobox.getSelectedItem().toString());

        TreeSet<String> list = builder.generateList();
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(getListBuilderPanel(), listName + " : list is empty");
            return;
        }

        String[] machines = new String[list.size()];
        Arrays.asList(list.toArray()).toArray(machines);
        MFMListBuilder.createPlayList(listName, machines, getListBuilderPanel());

        getListBuilderPanel().getTopLevelAncestor().requestFocus();
    }

    private void setSelectAllCategories(boolean isSelected) {
        ArrayList<JCheckBox> checkBoxes = ((DynamicCBpanel) categoriesCBpanel).getCheckBoxes();
        for (JCheckBox cb : checkBoxes) {
            cb.setSelected(isSelected);
        }
    }

    private void setSelectAllControls(boolean isSelected) {
        ArrayList<JCheckBox> checkBoxes = ((DynamicCBpanel) controlsCBPanel).getCheckBoxes();
        for (JCheckBox cb : checkBoxes) {
            cb.setSelected(isSelected);
        }
    }

    private void showCategories() {

        // TODO Should we do this in the DynamicPanel??
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
        ListBuilderPanel.validate();
        ListBuilderPanel.repaint();
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here

        ListBuilderPanel = new JPanel();

        gameButtonsNum = new JComboBox<String>(MAMEInfo.getNumButtons());
        gameButtonsNum.setSelectedIndex(4);
        gamePlayersNum = new JComboBox<Integer>(MAMEInfo.getNumPlayers());

        listNameLabel = new JLabel();
        listNameLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

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
        bg3.add(LCDRadioButton);
        allDisplaysRadioButton.setSelected(true);

//****************************************************

        // In case somebody deletes or renames the controllers.ini file
        // fixme a little convoluted
        // TreeSet to apply natural ordering
        TreeSet<String> controllersLabels = null;
        if (Controllers.getControllerMAMEtoLabel() != null) {
            controllersLabels = new TreeSet<String>(Controllers.getControllerMAMEtoLabel().values());
        } else {
            controllersLabels = new TreeSet<String>(MFMListBuilder.getControllersList());
        }

        controlsCBPanel = new DynamicCBpanel(new ArrayList<String>(controllersLabels), 1);

        HashMap<String, String> tooltips = new HashMap<String, String>();
        for (String label : controllersLabels) {
            String revLabel = Controllers.getControllerLabeltoMAME().get(label);
            if (revLabel != null && !MFMUI_Resources.getInstance().getLabelLocation(revLabel).isEmpty()) {
                tooltips.put(label,
                        "<html><img src=\"" + MFMUI_Resources.getInstance().getLabelLocation(revLabel));
            }
        }
        ((DynamicCBpanel) controlsCBPanel).setToolTips(tooltips);

//****************************************************

        controlsScrollPane = new JScrollPane();
        controlsScrollPane.getVerticalScrollBar().setUnitIncrement(24);
        controlsScrollPane.getHorizontalScrollBar().setUnitIncrement(24);

        CategoriesScrollPane = new JScrollPane();
        CategoriesScrollPane.getVerticalScrollBar().setUnitIncrement(24);
        CategoriesScrollPane.getHorizontalScrollBar().setUnitIncrement(24);


        saveListToFileButton = new JButton(new MFMAction(MFMAction.SaveListtoFileAction, null));
        populateListButton = new JButton(new MFMAction(MFMAction.CopyResourcesAction, null));
        populateListButton.addActionListener(controller);

        joyComboBox = new JComboBox<String>(Controllers.getJoysticks());
        doubleJoyComboBox = new JComboBox<String>(Controllers.getDoubleJoysticks());

        languagesCombobox = new JComboBox<String>(MFMPlayLists.getInstance().getLanguagesPLsKeys());
        languagesCombobox.setBorder(new BevelBorder(BevelBorder.RAISED, Color.gray, Color.gray));
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
        ListBuilderPanel.setLayout(new GridBagLayout());
        ListBuilderPanel.setAlignmentX(1.0f);
        ListBuilderPanel.setAlignmentY(1.0f);
        ListBuilderPanel.setMaximumSize(new Dimension(1280, 960));
        ListBuilderPanel.setMinimumSize(new Dimension(1080, 750));
        ListBuilderPanel.setPreferredSize(new Dimension(1280, 900));
        ListBuilderPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setMinimumSize(new Dimension(709, 70));
        buttonPanel.setPreferredSize(new Dimension(709, 70));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        ListBuilderPanel.add(buttonPanel, gbc);
        buttonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null));
        listNameTF = new JTextField();
        listNameTF.setMargin(new Insets(0, 20, 0, 30));
        listNameTF.setMaximumSize(new Dimension(255, 30));
        listNameTF.setMinimumSize(new Dimension(255, 30));
        listNameTF.setOpaque(true);
        listNameTF.setPreferredSize(new Dimension(255, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(listNameTF, gbc);
        createListButton = new JButton();
        createListButton.setLabel("Create List");
        createListButton.setMaximumSize(new Dimension(135, 32));
        createListButton.setMinimumSize(new Dimension(135, 32));
        createListButton.setPreferredSize(new Dimension(135, 32));
        createListButton.setText("Create List");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(createListButton, gbc);
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
        gamePickerButton = new JButton();
        gamePickerButton.setEnabled(false);
        gamePickerButton.setHideActionText(true);
        gamePickerButton.setMaximumSize(new Dimension(135, 32));
        gamePickerButton.setMinimumSize(new Dimension(135, 32));
        gamePickerButton.setPreferredSize(new Dimension(135, 32));
        gamePickerButton.setText("Game Picker");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(gamePickerButton, gbc);
        importListButton = new JButton();
        importListButton.setMaximumSize(new Dimension(135, 32));
        importListButton.setMinimumSize(new Dimension(135, 32));
        importListButton.setPreferredSize(new Dimension(135, 32));
        importListButton.setText("Import List");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(importListButton, gbc);
        populateListButton.setMaximumSize(new Dimension(135, 32));
        populateListButton.setMinimumSize(new Dimension(135, 32));
        populateListButton.setPreferredSize(new Dimension(135, 32));
        populateListButton.setText("Copy Resources");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(populateListButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer3, gbc);
        diffListsButton = new JButton();
        diffListsButton.setMaximumSize(new Dimension(135, 32));
        diffListsButton.setMinimumSize(new Dimension(135, 32));
        diffListsButton.setPreferredSize(new Dimension(135, 32));
        diffListsButton.setText("Diff Lists");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(diffListsButton, gbc);
        saveListToFileButton.setMaximumSize(new Dimension(135, 32));
        saveListToFileButton.setMinimumSize(new Dimension(135, 32));
        saveListToFileButton.setPreferredSize(new Dimension(135, 32));
        saveListToFileButton.setText("Save List to File");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(saveListToFileButton, gbc);
        UILeftPanel = new JPanel();
        UILeftPanel.setLayout(new GridBagLayout());
        UILeftPanel.setMaximumSize(new Dimension(32767, 32767));
        UILeftPanel.setMinimumSize(new Dimension(235, 660));
        UILeftPanel.setPreferredSize(new Dimension(235, 660));
        UILeftPanel.setRequestFocusEnabled(true);
        UILeftPanel.setVerifyInputWhenFocusTarget(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        ListBuilderPanel.add(UILeftPanel, gbc);
        MAMEPanel = new JPanel();
        MAMEPanel.setLayout(new GridBagLayout());
        MAMEPanel.setMaximumSize(new Dimension(2147483647, 2147483647));
        MAMEPanel.setMinimumSize(new Dimension(130, 120));
        MAMEPanel.setPreferredSize(new Dimension(130, 120));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        UILeftPanel.add(MAMEPanel, gbc);
        MAMEPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "MAME", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
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
        MAMEPanel.add(arcadeOnlyRadioButton, gbc);
        systemsOnlyRadioButton.setActionCommand("Systems");
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
        MAMEPanel.add(systemsOnlyRadioButton, gbc);
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
        MAMEPanel.add(bothRadioButton, gbc);
        OrientationPanel = new JPanel();
        OrientationPanel.setLayout(new GridBagLayout());
        OrientationPanel.setMaximumSize(new Dimension(2147483647, 165));
        OrientationPanel.setMinimumSize(new Dimension(200, 150));
        OrientationPanel.setPreferredSize(new Dimension(200, 150));
        OrientationPanel.setRequestFocusEnabled(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        UILeftPanel.add(OrientationPanel, gbc);
        OrientationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Orientation", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
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
        OrientationPanel.add(horizontalRadioButton, gbc);
        verticalRadioButton.setMaximumSize(new Dimension(200, 30));
        verticalRadioButton.setMinimumSize(new Dimension(200, 30));
        verticalRadioButton.setPreferredSize(new Dimension(200, 30));
        verticalRadioButton.setText("Vertical");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        OrientationPanel.add(verticalRadioButton, gbc);
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
        OrientationPanel.add(cocktailRadioButton, gbc);
        allRadioButton.setMaximumSize(new Dimension(200, 30));
        allRadioButton.setMinimumSize(new Dimension(200, 30));
        allRadioButton.setPreferredSize(new Dimension(200, 30));
        allRadioButton.setText("All");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        OrientationPanel.add(allRadioButton, gbc);
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
        UILeftPanel.add(displayTypePanel, gbc);
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
        LCDRadioButton.setMaximumSize(new Dimension(200, 30));
        LCDRadioButton.setMinimumSize(new Dimension(200, 30));
        LCDRadioButton.setPreferredSize(new Dimension(200, 30));
        LCDRadioButton.setText("LCD");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        displayTypePanel.add(LCDRadioButton, gbc);
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
        filterJPanel.setMinimumSize(new Dimension(400, 80));
        filterJPanel.setPreferredSize(new Dimension(400, 80));
        filterJPanel.setVerifyInputWhenFocusTarget(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        UILeftPanel.add(filterJPanel, gbc);
        filterJPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        noClonesCheckBox = new JCheckBox();
        noClonesCheckBox.setMaximumSize(new Dimension(200, 20));
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
        GameButtonsPanel = new JPanel();
        GameButtonsPanel.setLayout(new GridBagLayout());
        GameButtonsPanel.setMaximumSize(new Dimension(2147483647, 120));
        GameButtonsPanel.setMinimumSize(new Dimension(118, 50));
        GameButtonsPanel.setPreferredSize(new Dimension(118, 50));
        GameButtonsPanel.setRequestFocusEnabled(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        UILeftPanel.add(GameButtonsPanel, gbc);
        GameButtonsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Game Buttons", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        gameButtonsNum.setActionCommand("GameButtons");
        gameButtonsNum.setMinimumSize(new Dimension(60, 30));
        gameButtonsNum.setPreferredSize(new Dimension(60, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        GameButtonsPanel.add(gameButtonsNum, gbc);
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
        GameButtonsPanel.add(orLessButtonsCB, gbc);
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
        UILeftPanel.add(panel1, gbc);
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
        UILeftPanel.add(panel2, gbc);
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
        languagesCombobox.setToolTipText("Languages Filter");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        UILeftPanel.add(languagesCombobox, gbc);
        controlsScrollPane.setMaximumSize(new Dimension(175, 32767));
        controlsScrollPane.setMinimumSize(new Dimension(175, 600));
        controlsScrollPane.setPreferredSize(new Dimension(175, 550));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        ListBuilderPanel.add(controlsScrollPane, gbc);
        controlsCBPanel.setAlignmentX(0.0f);
        controlsCBPanel.setAlignmentY(0.0f);
        controlsCBPanel.setMaximumSize(new Dimension(32767, 32767));
        controlsCBPanel.setMinimumSize(new Dimension(175, 500));
        controlsCBPanel.setOpaque(true);
        controlsCBPanel.setPreferredSize(new Dimension(175, 550));
        controlsCBPanel.setRequestFocusEnabled(true);
        controlsScrollPane.setViewportView(controlsCBPanel);
        controlsCBPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Controls", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        CategoriesPanel = new JPanel();
        CategoriesPanel.setLayout(new BorderLayout(0, 0));
        CategoriesPanel.setMaximumSize(new Dimension(32767, 32767));
        CategoriesPanel.setMinimumSize(new Dimension(800, 800));
        CategoriesPanel.setOpaque(true);
        CategoriesPanel.setPreferredSize(new Dimension(900, 960));
        CategoriesPanel.setRequestFocusEnabled(false);
        CategoriesPanel.setVerifyInputWhenFocusTarget(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        ListBuilderPanel.add(CategoriesPanel, gbc);
        CategoriesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Categories", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        CategoriesPanel.add(panel3, BorderLayout.NORTH);
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
        CategoriesScrollPane.setPreferredSize(new Dimension(600, 600));
        CategoriesScrollPane.setVerticalScrollBarPolicy(22);
        CategoriesPanel.add(CategoriesScrollPane, BorderLayout.CENTER);
        categoriesCBpanel.setMinimumSize(new Dimension(520, 580));
        categoriesCBpanel.setPreferredSize(new Dimension(560, 600));
        categoriesCBpanel.setRequestFocusEnabled(false);
        CategoriesScrollPane.setViewportView(categoriesCBpanel);
        listNameLabel.setLabelFor(listNameTF);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return ListBuilderPanel;
    }
}