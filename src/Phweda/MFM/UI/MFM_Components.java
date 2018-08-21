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

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/7/11
 * Time: 1:16 PM
 */

import Phweda.MFM.*;
import Phweda.utils.ClockPanel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import static Phweda.MFM.UI.MFMAction.*;

/**
 * Build UI components
 * Major refactor 9/30/2016 moved Menubar creation to separate class MFM_Menubar
 */
public class MFM_Components {
    private static MFM_Components ourInstance;
    private static final JScrollPane leftTreeScrollPane = new JScrollPane();
    static MFMUI_Resources resources = MFMUI_Resources.getInstance();
    private MFMController mfmController;
    private JPopupMenu machinePopupMenu;
    private JPopupMenu softwarePopupMenu;
    private JPanel mfmListPanel;
    private JTabbedPane extrasTabbedPane;
    private MachineListTable machineListTable;
    private JScrollPane mfmFolderTreeScrollPane;
    private JMenuBar menuBar;
    private static StatusBar statusBar;
    private static JLabel currentListName;
    private static MFMInformationPanel infoPanel;
    private JPanel fillPanel;
    private JTree tree;

    // 0.9.5 adding Strings for Tab Titles - used for discovering images for extrasTabbedPane pane
    private static final String CABINET_COVER = "Cabinet/Cover";
    private static final String CPANEL = "CPanel";
    private static final String FLYERS = "Flyers";
    private static final String MARQUEES = "Marquees";
    private static final String PCB = "PCB";
    private static final String SNAP = "Snap";
    private static final String TITLES = "Titles";

    private MFM_Components(MFMController controller) {
        mfmController = controller;
        createUIComponents();
    }

    public static MFM_Components getInstance(MFMController controller) {
        if (ourInstance == null) {
            ourInstance = new MFM_Components(controller);
        }
        return ourInstance;
    }

    public static MFMInformationPanel infoPanel() {
        return infoPanel;
    }

    JPopupMenu getListPopupMenu() {
        return machinePopupMenu;
    }

    JPopupMenu getSoftwarelistPopupMenuPopupMenu() {
        return softwarePopupMenu;
    }

    JPanel getMFMFolderTreeScrollPane() {
        if (mfmFolderTreeScrollPane == null) {
            return null;
        }

        JPanel treePanel = new JPanel();
        BorderLayout borderLayout = new BorderLayout();
        treePanel.setLayout(borderLayout);
        treePanel.add(getTreeButton(), BorderLayout.NORTH);
        leftTreeScrollPane.getViewport().add(mfmFolderTreeScrollPane);
        treePanel.add(leftTreeScrollPane, BorderLayout.CENTER);
        return treePanel;
    }

    private JButton getTreeButton() {
        final String showXML = "Show MAME XML";
        final String showINIs = "Show Folder INIs";

        final JButton treeButton = new JButton(showXML);

        treeButton.addActionListener(event -> {
            if (((AbstractButton) event.getSource()).getText().equals(showXML)) {
                treeButton.setText(showINIs);
                leftTreeScrollPane.getViewport().add(MAMEtoJTree.getInstance(false).getMAMEjTree());
            } else {
                treeButton.setText(showXML);
                leftTreeScrollPane.getViewport().add(mfmFolderTreeScrollPane);
            }
        });
        return treeButton;
    }

    StatusBar statusBar() {
        return statusBar;
    }

    JPanel getFillPanel() {
        return fillPanel;
    }

    public static String dataSetPicker() {
        String[] dataSets = MFM_Data.getInstance().getDataSets();
        return (String) JOptionPane.showInputDialog(null,
                "Pick a Data Set",
                "Available Data Sets",
                JOptionPane.QUESTION_MESSAGE,
                null,
                dataSets,
                dataSets[0]);
    }

    public JPanel getMfmListPanel() {
        return mfmListPanel;
    }

    protected static MFMUI_Resources getResources() {
        return resources;
    }

    static Component createStatusBar(int width) {
        statusBar = new StatusBar();
        ClockPanel clock = null;
        ImageIcon flagIcon = MFMUI_Resources.getInstance().getFlagImageIcon(MFM.getLocalCountry());
        JLabel flag = null;
        if (flagIcon != null) {
            flag = new JLabel(flagIcon);
        } else {
            clock = new ClockPanel();
            clock.start();
        }

        JLabel versionJL = new JLabel("MAME " + MFMSettings.getInstance().getMAMEVersion() + "   :   DATA " +
                MFM_Data.getInstance().getDataVersion(), SwingConstants.CENTER);
        currentListName = new JLabel(MFMListBuilder.ALL,
                getResources().getImageIcon(MFMUI_Resources.UPARROW_PNG), SwingConstants.LEFT);
        infoPanel = new MFMInformationPanel();
        infoPanel.showMessage("Main View");
        JLabel workingJL = new JLabel("Runnable " + MAMEInfo.getRunnable(), SwingConstants.CENTER);

        if (flag == null) {
            statusBar.setZones(new String[]{"Version", "currentListName", "Information", "Working", "Clock"},
                    new JComponent[]{versionJL, currentListName, infoPanel, workingJL, clock},
                    new String[]{"20%", "24%", "*", "12%", "6%"});
        } else {
            statusBar.setZones(new String[]{"Version", "currentListName", "Information", "Working", "Flag"},
                    new JComponent[]{versionJL, currentListName, infoPanel, workingJL, flag},
                    new String[]{"20%", "24%", "*", "12%", "6%"});
        }

        statusBar.setPreferredSize(new Dimension(width, 35));
        return statusBar;
    }

    MachineListTable getMachineListTable() {
        return machineListTable;
    }

    JTabbedPane extrasTabbedPane() {
        return extrasTabbedPane;
    }

    JTree getTree() {
        return tree;
    }

    JLabel currentListName() {
        return currentListName;
    }

    JMenuBar getMenuBar() {
        return menuBar;
    }

    void updateListMenu() {
        MFM_MenuBar.getInstance().updateListMenu();
    }

    private void createUIComponents() {

        setMachineListTable();
        createExtrasTabbedPane();
        menuBar = MFM_MenuBar.getJMenubar();
        createFolderTree();
        createFillPanel();
        createListChooserPanel();

        createMachinePopup();
        createSoftwarePopup();
    }

    private void createFillPanel() {
        fillPanel = new JPanel();
        fillPanel.setBackground(MFMUI.getMFMSettingsBGcolor());
        fillPanel.setPreferredSize(new Dimension(100, 50));
        fillPanel.setMinimumSize(new Dimension(100, 50));
    }

    private void createFolderTree() {
        if (null == MAMEInfo.getInifiles() || MAMEInfo.getInifiles().isEmpty()) {
            return;
        }
        DefaultMutableTreeNode root;

        TreeMap<String, Map<String, String>> iniFiles = new TreeMap<>(MAMEInfo.getInifiles());
        root = new DefaultMutableTreeNode("INIfiles");
        tree = new JTree(root);

        for (Object ini : iniFiles.keySet()) {
            DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(ini);
            Map categories = iniFiles.get(ini);
            for (Object category : categories.keySet()) {
                DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(category);
                for (Object game : (TreeSet) categories.get(category)) {
                    DefaultMutableTreeNode gameNode = new DefaultMutableTreeNode(game);
                    categoryNode.add(gameNode);
                }
                folderNode.add(categoryNode);
            }
            root.add(folderNode);
        }
        tree.addMouseListener(mfmController);
        mfmFolderTreeScrollPane = new JScrollPane(tree);
        // Expand root folder
        tree.expandRow(0);
    }

    private void createExtrasTabbedPane() {
        extrasTabbedPane = new JTabbedPane();
        Image defaultImage = getResources().getImageIcon(MFMUI_Resources.MFM_IMAGE_PNG).getImage();
        extrasTabbedPane.addTab(CABINET_COVER, new ImagePanel(defaultImage)); // Cabs/Systems
        extrasTabbedPane.addTab(CPANEL, new ImagePanel(defaultImage));
        extrasTabbedPane.addTab(FLYERS, new ImagePanel(defaultImage));
        extrasTabbedPane.addTab(MARQUEES, new ImagePanel(defaultImage));
        extrasTabbedPane.addTab(PCB, new ImagePanel(defaultImage));
        extrasTabbedPane.addTab(SNAP, new ImagePanel(defaultImage));
        extrasTabbedPane.addTab(TITLES, new ImagePanel(defaultImage));

        extrasTabbedPane.addChangeListener(mfmController);

/*
        JAVA ImageIO only supports the following formats
        BMP  bmp  jpg  JPG  jpeg  JPEG
        wbmp  png  PNG  WBMP  GIF  gif
*/
    }

    private void setMachineListTable() {
        machineListTable = MachineListTable.getInstance();
        machineListTable.addMouseListener(mfmController);
        machineListTable.addMouseWheelListener(mfmController);
        machineListTable.getSelectionModel().addListSelectionListener(mfmController);
    }

    private void createMachinePopup() {
        machinePopupMenu = new JPopupMenu(null);
        machinePopupMenu.setBorder(new BevelBorder(BevelBorder.LOWERED,
                new Color(74, 37, 19), new Color(117, 70, 53)));
//==================================================================
        JSeparator separator = new JSeparator();
        separator.setPreferredSize(new Dimension(50, 2));
        separator.setBorder(new BevelBorder(BevelBorder.RAISED));

        JSeparator separator2 = new JSeparator();
        separator2.setPreferredSize(new Dimension(50, 2));
        separator2.setBorder(new BevelBorder(BevelBorder.RAISED));

        JSeparator separator3 = new JSeparator();
        separator3.setPreferredSize(new Dimension(50, 2));
        separator3.setBorder(new BevelBorder(BevelBorder.RAISED));
//==================================================================
        machinePopupMenu.add(new MFMAction(ADD_TO_LIST, null));
        machinePopupMenu.add(new MFMAction(REMOVE_FROM_LIST, null));
        machinePopupMenu.add(separator);
        machinePopupMenu.add(new MFMAction(RECORD_MACHINE, null));
        machinePopupMenu.add(new MFMAction(PLAY_VIDEO, null));
        machinePopupMenu.add(getVideoOps());

        machinePopupMenu.add(separator2);
        machinePopupMenu.add(new MFMAction(SHOW_HISTORY, null));
        machinePopupMenu.add(new MFMAction(SHOW_MANUAL, null));
        machinePopupMenu.add(new MFMAction(SHOW_INFO, null));
        machinePopupMenu.add(new MFMAction(SHOW_MACHINE_XML, null));

        machinePopupMenu.add(separator3);
        machinePopupMenu.add(new MFMAction(SHOW_CONTROLS_DEVICES, null));
        machinePopupMenu.add(new MFMAction(GOTO_CLONEOF, null));
        machinePopupMenu.add(new MFMAction(OPEN_IMAGE, null));
    }

    private void createSoftwarePopup() {
        softwarePopupMenu = new JPopupMenu(null);
        softwarePopupMenu.setBorder(new BevelBorder(BevelBorder.LOWERED,
                new Color(74, 37, 19), new Color(117, 70, 53)));
//==================================================================
        JSeparator separator = new JSeparator();
        separator.setPreferredSize(new Dimension(50, 2));
        separator.setBorder(new BevelBorder(BevelBorder.RAISED));
//==================================================================


        softwarePopupMenu.add(new MFMAction(ADD_TO_LIST, null));
        softwarePopupMenu.add(new MFMAction(REMOVE_FROM_LIST, null));
        softwarePopupMenu.add(separator);
        softwarePopupMenu.add(new MFMAction(SHOW_MANUAL, null));
        softwarePopupMenu.add(new MFMAction(SHOW_SOFTWARE_XML, null));
        softwarePopupMenu.add(new MFMAction(GOTO_CLONEOF, null));
        softwarePopupMenu.add(new MFMAction(OPEN_IMAGE, null));
    }

    private JMenu getVideoOps() {
        JMenu videoOps = new JMenu("VideoOps");
        videoOps.add(new MFMAction(PLAY_RECORD_TO_AVI, null));
        videoOps.add(new MFMAction(PLAYBACK_TO_AVI, null));
        videoOps.add(new MFMAction(EDIT_VIDEO, null));
        videoOps.add(new MFMAction(CROP_AVI, null));
        return videoOps;
    }

    //  NOTE replaced by ListBuilder
    private void createListChooserPanel() {

        mfmListPanel = new JPanel(new BorderLayout());
        //    mfmListPanel = new JPanel(new GridBagLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));
        panel.setMinimumSize(new Dimension(250, 150));
        panel.setPreferredSize(new Dimension(250, 150));

        mfmListPanel.add(panel, BorderLayout.SOUTH);

        mfmListPanel.setPreferredSize(new Dimension(250, 600));
        mfmListPanel.setMaximumSize(new Dimension(250, 1100));

        mfmListPanel.validate();
    }
}
