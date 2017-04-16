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

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/7/11
 * Time: 1:16 PM
 */

import Phweda.MFM.MAMEInfo;
import Phweda.MFM.MFMListBuilder;
import Phweda.MFM.MFMSettings;
import Phweda.MFM.MFM_Data;
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

/**
 * Build UI components
 * Major refactor 9/30/2016 moved Menubar creation to separate class MFM_Menubar
 */
public class MFM_Components {
    private static final JScrollPane leftTreeScrollPane = new JScrollPane();
    static MFMUI_Resources resources = MFMUI_Resources.getInstance();
    private static MFMController MFMController;
    private static JPopupMenu MFMPopupMenu;
    private static JPanel MFMListPanel;
    private static JTabbedPane ExtrasTabbedPane;
    private static JTable machineListTable;
    private static JScrollPane MFMFolderTreePane;
    private static JMenuBar menuBar;
    private static StatusBar statusBar;
    private static JLabel currentListName;
    private static MFMInformationPanel infoPanel;
    private static JPanel fillPanel;
    private static String[][] data;
    private JTree tree;

    MFM_Components(MFMController controller) {
        MFMController = controller;
        createUIComponents();
    }

    public static MFMInformationPanel InfoPanel() {
        return infoPanel;
    }

    static JPopupMenu MFMPopupMenu() {
        return MFMPopupMenu;
    }

    static JPanel MFMFolderTreePane() {
        if (MFMFolderTreePane == null) {
            return null;
        }

        JPanel treePanel = new JPanel();
        BorderLayout borderLayout = new BorderLayout();
        treePanel.setLayout(borderLayout);
        treePanel.add(getTreeButton(), BorderLayout.NORTH);
        leftTreeScrollPane.getViewport().add(MFMFolderTreePane);
        treePanel.add(leftTreeScrollPane, BorderLayout.CENTER);
        return treePanel;
    }

    static JButton getTreeButton() {
        final String showXML = "Show MAME XML";
        final String showINIs = "Show Folder INIs";

        final JButton treeButton = new JButton(showXML);

        treeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (((AbstractButton) e.getSource()).getText().equals(showXML)) {
                    treeButton.setText(showINIs);
                    leftTreeScrollPane.getViewport().add(MAMEtoJTree.getInstance().getMAMEjTree());
                } else {
                    treeButton.setText(showXML);
                    leftTreeScrollPane.getViewport().add(MFMFolderTreePane);
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

    public static JPanel getMFMListPanel() {
        return MFMListPanel;
    }

    protected static MFMUI_Resources getResources() {
        return resources;
    }

    Component createStatusBar(int width) {
        statusBar = new StatusBar();
        ClockPanel clock = new ClockPanel();
        clock.start();

        JLabel versionJL = new JLabel(MFMSettings.getInstance().getMAMEVersion() + "   :   DATA " +
                MFM_Data.getInstance().getDataVersion(), SwingConstants.CENTER);
        currentListName = new JLabel(MFMListBuilder.ALL,
                getResources().getImageIcon(MFMUI_Resources.UPARROW_PNG), SwingConstants.LEFT);
        infoPanel = new MFMInformationPanel();
        infoPanel.showMessage("Main View");
        JLabel workingJL = new JLabel("Runnable " + MAMEInfo.getRunnable(), SwingConstants.CENTER);
        statusBar.setZones(new String[]{"Version", "currentListName", "Information", "Working", "Clock"},
                new JComponent[]{versionJL, currentListName, infoPanel, workingJL, clock},
                new String[]{"20%", "24%", "*", "12%", "6%"});
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
        MFM_Menubar.getInstance().updateListMenu();
    }

    private void createUIComponents() {

        setMachineListTable();
        createExtrasTabbedPane();
        menuBar = MFM_Menubar.getJMenubar();
        createFolderTree();
        createFillPanel();
        createListChooserPanel();
        // What order?? Popup needs machineListTable
        createPopup();
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
        MFMFolderTreePane = new JScrollPane(tree);
        // Expand root folder
        tree.expandRow(0);
    }

    private void createExtrasTabbedPane() {
        ExtrasTabbedPane = new JTabbedPane();
        Image defaultImage = getResources().getImageIcon(MFMUI_Resources.MFM_Image_PNG).getImage();
        ExtrasTabbedPane.addTab("Cabinets", new ImagePanel(defaultImage)); // Cabs/Systems
        ExtrasTabbedPane.addTab("CPanel", new ImagePanel(defaultImage));
        ExtrasTabbedPane.addTab("Flyers", new ImagePanel(defaultImage));
        ExtrasTabbedPane.addTab("Marquees", new ImagePanel(defaultImage));
        ExtrasTabbedPane.addTab("PCB", new ImagePanel(defaultImage));
        ExtrasTabbedPane.addTab("Snap", new ImagePanel(defaultImage));
        ExtrasTabbedPane.addTab("Titles", new ImagePanel(defaultImage));

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

    private void createPopup() {
        MFMPopupMenu = new JPopupMenu(null);
        MFMPopupMenu.setBorder(new BevelBorder(BevelBorder.LOWERED,
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
        MFMPopupMenu.add(new MFMAction(MFMAction.AddtoListAction, null));
        MFMPopupMenu.add(new MFMAction(MFMAction.RemovefromListAction, null));
        MFMPopupMenu.add(separator);
        MFMPopupMenu.add(new MFMAction("Record", null));
        MFMPopupMenu.add(new MFMAction(MFMAction.PlayVideoAction, null));
        MFMPopupMenu.add(getVideoOps());

        MFMPopupMenu.add(separator2);
        MFMPopupMenu.add(new MFMAction("Show History", null));
        MFMPopupMenu.add(new MFMAction("Show Manual", null));
        MFMPopupMenu.add(new MFMAction("Show Info", null));
        MFMPopupMenu.add(new MFMAction(MFMAction.MACHINE_TREEAction, null));
        MFMPopupMenu.add(new MFMAction(MFMAction.ShowControlsDevicesAction, null));
        MFMPopupMenu.add(new MFMAction(MFMAction.GOTOcloneofAction, null));
        MFMPopupMenu.add(new MFMAction("Open Image", null));
        MFMPopupMenu.add(separator3);
        MFMPopupMenu.add(new MFMAction("Exit", null));
    }

    private JMenu getVideoOps() {
        JMenu videoOps = new JMenu("VideoOps");
        videoOps.add(new MFMAction(MFMAction.PlayGametoAVIAction, null));
        videoOps.add(new MFMAction(MFMAction.PlaybacktoAVIAction, null));
        videoOps.add(new MFMAction(MFMAction.EditVideoAction, null));
        videoOps.add(new MFMAction(MFMAction.CropAVIAction, null));
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
