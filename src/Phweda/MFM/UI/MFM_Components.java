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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import static Phweda.MFM.UI.MFMAction.*;

/**
 * Build UI components
 * Major refactor 9/30/2016 moved Menubar creation to separate class MFM_Menubar
 */
public class MFM_Components {
    private static final JScrollPane leftTreeScrollPane = new JScrollPane();
    static MFMUI_Resources resources = MFMUI_Resources.getInstance();
    private static MFMController MFMController;
    private static JPopupMenu MachinePopupMenu;
    private static JPopupMenu SoftwarePopupMenu;
    private static JPanel MFMListPanel;
    private static JTabbedPane ExtrasTabbedPane;
    private static JTable machineListTable;
    private static JScrollPane mfmFolderTreeScrollPane;
    private static JMenuBar menuBar;
    private static StatusBar statusBar;
    private static JLabel currentListName;
    private static MFMInformationPanel infoPanel;
    private static JPanel fillPanel;
    private JTree tree;

    // 0.9.5 adding Strings for Tab Titles - used for discovering images for ExtrasTabbedPane pane
    static final String CABINET_COVER = "Cabinet/Cover";
    static final String CPANEL = "CPanel";
    static final String FLYERS = "Flyers";
    static final String MARQUEES = "Marquees";
    static final String PCB = "PCB";
    static final String SNAP = "Snap";
    static final String TITLES = "Titles";

    MFM_Components(MFMController controller) {
        MFMController = controller;
        createUIComponents();
    }

    public static MFMInformationPanel InfoPanel() {
        return infoPanel;
    }

    static JPopupMenu getListPopupMenu() {
        return MachinePopupMenu;
    }

    static JPopupMenu getSoftwarelistPopupMenuPopupMenu() {
        return SoftwarePopupMenu;
    }

    static JPanel getMFMFolderTreeScrollPane() {
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

    private static JButton getTreeButton() {
        final String showXML = "Show MAME XML";
        final String showINIs = "Show Folder INIs";

        final JButton treeButton = new JButton(showXML);

        treeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (((AbstractButton) e.getSource()).getText().equals(showXML)) {
                    treeButton.setText(showINIs);
                    leftTreeScrollPane.getViewport().add(MAMEtoJTree.getInstance(false).getMAMEjTree());
                } else {
                    treeButton.setText(showXML);
                    leftTreeScrollPane.getViewport().add(mfmFolderTreeScrollPane);
                }
            }
        });
        return treeButton;
    }

    static StatusBar StatusBar() {
        return statusBar;
    }

    static JPanel getFillPanel() {
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

    public static JPanel getMFMListPanel() {
        return MFMListPanel;
    }

    protected static MFMUI_Resources getResources() {
        return resources;
    }

    Component createStatusBar(int width) {
        statusBar = new StatusBar();
        ClockPanel clock = null;
        ImageIcon flagIcon = MFMUI_Resources.getInstance().getFlagImageIcon(MFM.LOCAL_COUNTRY);
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

    JTable getMachineListTable() {
        return machineListTable;
    }

    JTabbedPane ExtrasTabbedPane() {
        return ExtrasTabbedPane;
    }

    JTree getTree() {
        return tree;
    }

    JLabel CurrentListName() {
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
        if (null == MAMEInfo.getINIfiles() || MAMEInfo.getINIfiles().isEmpty()) {
            return;
        }
        DefaultMutableTreeNode root;

        TreeMap<String, Map> INIfiles = new TreeMap<String, Map>(MAMEInfo.getINIfiles());
        root = new DefaultMutableTreeNode("INIfiles");
        tree = new JTree(root);

        // TODO maybe we should simplify/explicate by having our own object - see ParseAllMachinesInfo too
        for (Object ini : INIfiles.keySet()) {
            DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(ini);
            Map categories = INIfiles.get(ini);
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
        tree.addMouseListener(MFMController);
        mfmFolderTreeScrollPane = new JScrollPane(tree);
        // Expand root folder
        tree.expandRow(0);
    }

    private void createExtrasTabbedPane() {
        ExtrasTabbedPane = new JTabbedPane();
        Image defaultImage = getResources().getImageIcon(MFMUI_Resources.MFM_Image_PNG).getImage();
        ExtrasTabbedPane.addTab(CABINET_COVER, new ImagePanel(defaultImage)); // Cabs/Systems
        ExtrasTabbedPane.addTab(CPANEL, new ImagePanel(defaultImage));
        ExtrasTabbedPane.addTab(FLYERS, new ImagePanel(defaultImage));
        ExtrasTabbedPane.addTab(MARQUEES, new ImagePanel(defaultImage));
        ExtrasTabbedPane.addTab(PCB, new ImagePanel(defaultImage));
        ExtrasTabbedPane.addTab(SNAP, new ImagePanel(defaultImage));
        ExtrasTabbedPane.addTab(TITLES, new ImagePanel(defaultImage));

        ExtrasTabbedPane.addChangeListener(MFMController);

/*
        JAVA ImageIO only supports the following formats
        BMP  bmp  jpg  JPG  jpeg  JPEG
        wbmp  png  PNG  WBMP  GIF  gif
*/
    }

    private void setMachineListTable() {
        machineListTable = MachineListTable.getInstance();
        machineListTable.addMouseListener(MFMController);
        machineListTable.addMouseWheelListener(MFMController);
        machineListTable.getSelectionModel().addListSelectionListener(MFMController);
    }

    private void createMachinePopup() {
        MachinePopupMenu = new JPopupMenu(null);
        MachinePopupMenu.setBorder(new BevelBorder(BevelBorder.LOWERED,
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
        MachinePopupMenu.add(new MFMAction(AddtoListAction, null));
        MachinePopupMenu.add(new MFMAction(RemovefromListAction, null));
        MachinePopupMenu.add(separator);
        MachinePopupMenu.add(new MFMAction(RecordMachineAction, null));
        MachinePopupMenu.add(new MFMAction(PlayVideoAction, null));
        MachinePopupMenu.add(getVideoOps());

        MachinePopupMenu.add(separator2);
        MachinePopupMenu.add(new MFMAction(ShowHistoryAction, null));
        MachinePopupMenu.add(new MFMAction(ShowManualAction, null));
        MachinePopupMenu.add(new MFMAction(ShowInfoAction, null));
        MachinePopupMenu.add(new MFMAction(MACHINE_XMLAction, null));

        MachinePopupMenu.add(separator3);
        MachinePopupMenu.add(new MFMAction(ShowControlsDevicesAction, null));
        MachinePopupMenu.add(new MFMAction(GOTOcloneofAction, null));
        MachinePopupMenu.add(new MFMAction(OpenImageAction, null));
    }

    private void createSoftwarePopup() {
        SoftwarePopupMenu = new JPopupMenu(null);
        SoftwarePopupMenu.setBorder(new BevelBorder(BevelBorder.LOWERED,
                new Color(74, 37, 19), new Color(117, 70, 53)));
//==================================================================
        JSeparator separator = new JSeparator();
        separator.setPreferredSize(new Dimension(50, 2));
        separator.setBorder(new BevelBorder(BevelBorder.RAISED));
//==================================================================


        SoftwarePopupMenu.add(new MFMAction(AddtoListAction, null));
        SoftwarePopupMenu.add(new MFMAction(RemovefromListAction, null));
        SoftwarePopupMenu.add(separator);
        SoftwarePopupMenu.add(new MFMAction(ShowManualAction, null));
        SoftwarePopupMenu.add(new MFMAction(SOFTWARE_XMLAction, null));
        SoftwarePopupMenu.add(new MFMAction(GOTOcloneofAction, null));
        SoftwarePopupMenu.add(new MFMAction(OpenImageAction, null));
    }

    private JMenu getVideoOps() {
        JMenu videoOps = new JMenu("VideoOps");
        videoOps.add(new MFMAction(PlayGametoAVIAction, null));
        videoOps.add(new MFMAction(PlaybacktoAVIAction, null));
        videoOps.add(new MFMAction(EditVideoAction, null));
        videoOps.add(new MFMAction(CropAVIAction, null));
        return videoOps;
    }

    //  NOTE replaced by ListBuilder
    private void createListChooserPanel() {

        MFMListPanel = new JPanel(new BorderLayout());
        //    MFMListPanel = new JPanel(new GridBagLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));
        panel.setMinimumSize(new Dimension(250, 150));
        panel.setPreferredSize(new Dimension(250, 150));

        MFMListPanel.add(panel, BorderLayout.SOUTH);

        MFMListPanel.setPreferredSize(new Dimension(250, 600));
        MFMListPanel.setMaximumSize(new Dimension(250, 1100));

        MFMListPanel.validate();
    }
}
