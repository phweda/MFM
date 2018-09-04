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
import Phweda.MFM.Utils.AnalyzeCategories;
import Phweda.MFM.Utils.MFM_DATmaker;
import Phweda.MFM.mame.Control;
import Phweda.MFM.mame.Device;
import Phweda.MFM.mame.Machine;
import Phweda.MFM.mame.softwarelist.Software;
import Phweda.utils.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import static Phweda.MFM.MFM_Constants.*;
import static Phweda.MFM.UI.MFMListActions.pickList;
import static Phweda.MFM.UI.MFMUI_Setup.getInstance;
import static Phweda.MFM.Utils.AnalyzeCategories.analyzeCategories;
import static Phweda.utils.FileUtils.*;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 1/22/12
 * Time: 5:11 AM
 */

/**
 * GUI Controller
 */
final class MFMController extends ClickListener implements ListSelectionListener, ChangeListener, KeyListener {
    static final DecimalFormat decimalFormater = new DecimalFormat("###,###");
    private static final int INT_50 = 50;
    private static final int INT_20 = 20;

    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    static JFrame mainFrame; // Not private so no synthetic accessor probably inconsequential
    private static MFM_Components components;
    private JTree folderTree;
    private static MachineListTable machineListTable;
    private JTabbedPane extrasTabbedPane;
    private JPopupMenu listPopupMenu;
    private JPopupMenu softwarelistPopupMenu;

    private static JLabel currentListLabel;
    private static String listName;
    private static MFMInformationPanel infoPanel;
    private StatusBar statusBar;
    private final JTextPane htmlTextPane = new MFMHTMLTextPane();

    private boolean firstLoad = true;
    // To differentiate lookup for images
    private boolean isSoftwarelist = false;
    private static final int SCROLLPANE_WIDTH = 800;
    private static final double DIVIDER_LOCATION = 0.16;
    private static final String LOADED = "loaded";

    @SuppressWarnings("WeakerAccess")
    static final MFMSettings mfmSettings = MFMSettings.getInstance();

    static void showListInfo(String listNameIn) {
        SortedSet<String> list = MFMPlayLists.getInstance().getPlayList(listNameIn);
        String output = decimalFormater.format(list.size());
        currentListLabel.setText(listNameIn + " - " + output);
        currentListLabel.setName(listNameIn);
    }

    public static JFrame getFrame() {
        return mainFrame;
    }

    static JTable getMachineListTable() {
        return machineListTable;
    }

    static MFMInformationPanel getInformationPanel() {
        return infoPanel;
    }

    static void showInformation(String title, String text) {
        if (text.isEmpty()) {
            return;
        }
        MFMOptionPane optionPane = new MFMOptionPane(title, text, mainFrame, 0, 0);
        optionPane.textArea.scrollRectToVisible(new Rectangle(0, 0, INT_50, INT_20));
    }

    static void setFontSize(Container container) {
        if ((mfmSettings.MFMFontSize() != null) && !mfmSettings.MFMFontSize().equals(NORMAL)) {
            int num = mfmSettings.MFMFontSize().equals(LARGE) ? LARGEINT : VERYLARGEINT;
            SwingUtils.changeFont(container, FONTSIZEINT + num);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mouseReleased(e);
        if (e.isPopupTrigger()) {
            showPopup(e);
        }
    }

    private void showPopup(MouseEvent e) {
        if ((isSoftwarelist || isSelectedSoftware()) && (e.getSource() == machineListTable)) {
            softwarelistPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        } else if (!isSoftwarelist && ((e.getSource() == machineListTable) || (e.getSource() == folderTree))) {
            listPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void singleClick(MouseEvent e) {
        if ((e.getSource() == extrasTabbedPane) && (e.getButton() == MouseEvent.BUTTON3)) {
            int i = JOptionPane.showConfirmDialog(extrasTabbedPane, "Open the Image?");
            if (i == JOptionPane.YES_OPTION) {
                openImage();
            }
        } else if ((folderTree != null) && (e.getSource() == folderTree) && (e.getButton() == MouseEvent.BUTTON1)) {
            // GOTO selected game
            gotoItem(getMouseSelectedGame());
        }
    }

    @Override
    public void doubleClick(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (e.getSource() == machineListTable) {
                int row = machineListTable.getSelectedRow();
                // If no row is selected return ** row == -1
                if (row < 0) {
                    return;
                }
                runGame("");
            } else if (e.getSource() == extrasTabbedPane) {
                openImage();
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            int i = e.getWheelRotation();

            int row = machineListTable.getSelectedRow();

            // If no row is selected return ** row == -1
            if (row < 0) {
                // Dispatch to grandparent JScrollPane for scrolling
                machineListTable.getParent().getParent().dispatchEvent(e);
                return;
            }
            int nextRow = row + i;

            // Stop at top or bottom of list
            // Could do this with a nested Ternary
            if (nextRow < 0) {
                nextRow = 0;
            } else if (nextRow > (machineListTable.getRowCount() - 1)) {
                nextRow = machineListTable.getRowCount() - 1;
            }

            machineListTable.getSelectionModel().setSelectionInterval(0, nextRow);
            machineListTable.scrollRectToVisible(
                    new Rectangle(machineListTable.getCellRect(nextRow, 0, true)));
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!MFM.isListOnly()) {
            showImage();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (!MFM.isListOnly()) {
            showImage();
        }
    }

    void changeList(String updateListName) {

        SortedSet<String> list = MFMPlayLists.getInstance().getPlayList(updateListName);
        if ((list == null) || (list.isEmpty())) {
            showError(updateListName + " is empty");
            listName = MFMListBuilder.ALL;
            list = MFMPlayLists.getInstance().getALLPlayListsTree().get(MFMListBuilder.ALL);
        } else {
            listName = updateListName;
        }
        // fixme possible naming collisions we need to add check to User list creation to prevent usage of Softwarelist names
        isSoftwarelist = MAMEInfo.isSoftwareList(listName);

        MachineListTableModel gltm = (MachineListTableModel) machineListTable.getModel();
        // We now pass in list name to handle softwarelists
        gltm.setData(list, listName);
        showList(listName);
    }

    static boolean checkRunnableSoftware(String name) {
        // System.out.println("Software name : " + name); // For Dev debug too noisy
        Software software = MAMEInfo.getSoftware(name, listName);
        return (software != null) && software.getSupported().equalsIgnoreCase(Machine.YES);
    }

    static boolean checkRunnableSoftware(String name, String systemName) {
        // System.out.println("Software name : " + name); // For Dev debug too noisy
        Software software = MAMEInfo.getSoftware(name, systemName);
        return (software != null) && software.getSupported().equalsIgnoreCase(Machine.YES);
    }

    private void showList(String listNameIn) {
        showListInfo(listNameIn);
        updateUI();
        mfmSettings.MFMCurrentList(listNameIn);
    }

    static void commandDialog() {
        CommandDialog.showCommands(mainFrame);
    }

    private String getMouseSelectedGame() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
        if (node == null) {
            return null;
        }
        Object nodeInfo = node.getUserObject();
        return nodeInfo.toString();
    }

    /**
     * Gets the name of the selected Machine or Software
     * Software gets reported as "List Name" _ "name"
     * NOTE what about mixed list - think I have this handled?
     *
     * @return selected Machine or Software name
     */
    private String getSelectedItem() {
        String itemName;
        if ((folderTree != null) && (listPopupMenu.getInvoker() == folderTree)) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
            if (node == null) {
                return null;
            }
            Object nodeInfo = node.getUserObject();
            itemName = nodeInfo.toString();
        } else { // if (getListPopupMenu.getInvoker() == MachineListTable || MachineListTable.hasFocus())

            int row = machineListTable.getSelectedRow();
            // If no row is selected return ** row == -1
            if (row < 0) {
                return null;
            }
            row = machineListTable.convertRowIndexToModel(row);
            itemName = (String) machineListTable.getModel().getValueAt(row, MachineListTable.KEY_COLUMN);
            if (isSoftwarelist) {
                itemName = listName + MFM_Constants.SOFTWARE_LIST_SEPARATER +
                        (String) machineListTable.getModel().getValueAt(row, MachineListTable.KEY_COLUMN);
            } else if (MAMEInfo.getMachine(itemName) == null) {
                itemName = (String) machineListTable.getModel().getValueAt(row, machineListTable.categoryColumn)
                        + MFM_Constants.SOFTWARE_LIST_SEPARATER + itemName;
            }
            if (MFM.isDebug()) {
                MFM.getLogger().addToList(itemName);
            }
        }
        return itemName;
    }

    private static boolean isSelectedSoftware() {
        int row = machineListTable.getSelectedRow();
        // If no row is selected return ** row == -1
        if (row < 0) {
            return false;
        }
        row = machineListTable.convertRowIndexToModel(row);
        String categoryEntry = (String) machineListTable.getModel().getValueAt(row, machineListTable.categoryColumn);
        return MAMEInfo.isSoftwareList(categoryEntry);
    }

    private static void gotoItem(String itemName) {
        int row = 0;
        int max = machineListTable.getRowCount();
        while (row < max) {
            if (machineListTable.getModel().getValueAt(row, MachineListTable.KEY_COLUMN).equals(itemName)) {
                row = machineListTable.convertRowIndexToView(row);
                machineListTable.getSelectionModel().setSelectionInterval(row, row);
                machineListTable.scrollRectToVisible(new Rectangle(machineListTable.getCellRect(row, 0, true)));
                break;
            }
            row++;
        }
    }

    void addtoList() {
        String item = getSelectedItem();
        MFMListActions.addtoList(item);
    }

    void removefromList() {
        MFMListActions.removefromList(getSelectedItem(), listName);
    }

    void removeList() {
        String result = MFMListActions.removeList();
        if (listName.equals(result)) {
            changeList(result);
        }
        updateUI();
    }

    static void showListEditor() {
        MFMListActions.showListEditor();
    }

    void dattoList() {
        String newListName = MFMListActions.datafiletoList();
        if (!newListName.isEmpty()) {
            changeList(newListName);
        }
    }

    static void validateXML() {
        JFileChooser fileChooser = new JFileChooser(MFM.getMfmListsDir());
        int returnValue = fileChooser.showOpenDialog(mainFrame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            boolean answer = MFMListActions.validateXML((fileChooser.getSelectedFile()));
            String message;
            message = answer ? "File is valid XML" : "File is NOT valid XML - see error log for the exception.";
            showMessage(message);
        }
    }

    void importList() {
        String listName2 = MFMListActions.importList(mainFrame);
        changeList(listName2);
        getInstance().updateMenuBar(listName2);
    }

    void runGame(String command) {
        String gameName = getSelectedItem();
        if (MFM.isDebug()) {
            MFM.getLogger().out("Machine is: " + gameName);
        }
        List<String> args;
        switch (command) {

            case MAMECommands.RECORD:
                args = MAMECommands.recordGame(gameName);
                break;
            case MAMECommands.PLAYBACK:
                args = MAMECommands.playbackGame(gameName);
                break;
            case MAMECommands.PLAYBACK_TO_AVI:
                args = MAMECommands.createAVIfromPlayback(gameName);
                break;
            case MAMECommands.AVIWRITE:
                args = MAMECommands.playGametoAVI(gameName);
                break;

            default:
                args = new ArrayList<>(1);
                args.add(gameName);
        }

        try {
            if (args == null) {
                // NOTE Should never get here
                return;
            }
            MAMEexe.run(args, MFM.getMameout());
        } catch (RuntimeException e) {
            infoPanel.showMessage(gameName + " had errors");
            //        showInformation("MAME error", e.getError());
        }
    }

    void showHelp(String text) {

        switch (text) {
            case ABOUT:
                showInformation(MFM.MAME_FILE_MANAGER,
                        "Created December 2011" + NEWLINE +
                                "Coded by phweda" + NEWLINE +
                                "phweda1@yahoo.com" + NEWLINE +
                                MFM.getVERSION() + TAB + MFM.getReleaseDate());
                break;
            case MFM_USER_GUIDE:
                FileUtils.openFileFromOS(Paths.get(MFM.getMfmDir() + MFM.getMFMUserGuide()));
                break;
            case MFM_COPYRIGHT:
                showHTML(MFM_COPYRIGHT, MFMUI_Resources.MFM_COPYRIGHT_HTML);
                break;
            case GNU_GPL:
                showHTML(GNU_GPL, MFMUI_Resources.GNU_GPL_V3);
                break;
            case HOT_KEYS:
                showHTML(HOT_KEYS, MFMUI_Resources.HOT_KEYS_HTML);
                break;
            default:
                break;
        }
    }

    void showHistory() {
        String gameName = getSelectedItem();
        String history = MAMEInfo.getMachine(gameName).getHistory();
        if (history != null) {
            showInformation(gameName, history);
        } else {
            showInformation(gameName, "No history found for " + gameName);
        }
    }

    void showInfo() {
        String gameName = getSelectedItem();
        String info = MAMEInfo.getMachine(gameName).getInfo();
        if (info != null) {
            showInformation(gameName, info);
        } else {
            showInformation(gameName, "No MAME info found for " + gameName);
        }
    }

    // Todo move/refactor
    void showControlsDevices() {
        String newlineTab = NEWLINE + TAB;

        String machineName = getSelectedItem();
        Machine machine = MAMEInfo.getMachine(machineName);
        if (machine.getIsdevice().equals(Machine.YES) || machine.getIsbios().equals(Machine.YES)) {
            return;
        }


        StringBuilder info = new StringBuilder(200);
        List<Control> controls = machine.getInput().getControl();
        info.append("Controls:");
        info.append(newlineTab);

        for (Control control : controls) {
            info.append(MachineControllers.getControllerMAMEtoLabel().get(control.getType()));
            info.append(newlineTab);
        }

        info.append(NEWLINE);
        info.append("Players:");
        info.append(TAB);
        info.append(machine.getInput().getPlayers());
        info.append(NEWLINE);
        info.append("Buttons:");
        info.append(TAB);
        info.append(machine.getButtons());
        info.append(NEWLINE);
        info.append(NEWLINE);
        info.append("DEVICES:");
        info.append(NEWLINE);
        List<Device> list = machine.getDevice();
        for (Device device : list) {
            info.append(TAB);
            info.append(device.getTag());
            info.append(NEWLINE);
        }
        info.trimToSize();
        showInformation(machineName, info.toString());
    }

    private void showHTML(String title, String path) {
        JScrollPane scrollPane = new JScrollPane();

/*  fixme Bug with HTML embedded URL Link
        htmlTextPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                // createFontResizePopup().setVisible(true);
                createFontResizePopup().show(htmlTextPane, 10, 10);
            }
        });
*/
        //    htmlTextPane.add(createFontResizePopup());
        URL url;
        url = MFMUI_Resources.getInstance().resourceURLs().get(path);
        try {
            htmlTextPane.setPage(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        htmlTextPane.setPreferredSize(new Dimension(850, 800));
        scrollPane.setViewportView(htmlTextPane);
        // NOTE we do all of this to have a resizable dialog.
        Object[] array = {
                new JLabel(title),
                scrollPane,
        };
        JOptionPane pane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = pane.createDialog(mainFrame, "");
        dialog.setLocation(150, INT_50);
        dialog.setResizable(true);
        dialog.setVisible(true);
    }

    void showMFMmessage(String message) {
        infoPanel.showMessage(message);
    }

    static void showMessage(String message) {
        JOptionPane.showMessageDialog(mainFrame, message);
    }

    void openImage() {
        ImagePanel imagePanel = (ImagePanel) extrasTabbedPane.getSelectedComponent();
        Path fullPath = imagePanel.getImagePath();
        FileUtils.openFileFromOS(fullPath);
    }

    void gotoCloneof() {
        if (isSoftwarelist) {
            Software software = MAMEInfo.getSoftware(getSelectedItem(), currentListLabel.getName());
            if ((software != null) && (software.getCloneof() != null) && !software.getCloneof().isEmpty()) {
                gotoItem(software.getCloneof());
            }
        } else {
            // GOTO selected game's parent(cloneof)
            Machine machine = MAMEInfo.getMachine(getSelectedItem());
            if ((machine != null) && (machine.getCloneof() != null) && !machine.getCloneof().isEmpty()) {
                gotoItem(machine.getCloneof());
            }
        }
    }

    // Todo needs to be refactored ?? and moved??
    private void showImage() {
        ImagePanel imagePanel = (ImagePanel) extrasTabbedPane.getSelectedComponent();
        int row = machineListTable.getSelectedRow();
        // If no row is selected return. ** row == -1
        if (row < 0) {
            imagePanel.imageReset();
            return;
        }
        try {
            row = machineListTable.convertRowIndexToModel(row);
        } catch (RuntimeException e) {
            imagePanel.imageReset();
            return;
        }

        String gameName = (String) machineListTable.getModel().getValueAt(row, MachineListTable.KEY_COLUMN);
        int index = extrasTabbedPane.getSelectedIndex();
        // toLowerCase so we can Capitalize in the UI
        String folder = extrasTabbedPane.getTitleAt(index).toLowerCase();

        // NOTE hack for lists with mixed Machine/Software if not isSoftwarelist but Machine not found assume Software
        if (isSoftwarelist || (MAMEInfo.getMachine(gameName) == null)) {
            switch (folder) {

                case "cabinet/cover":
                    folder = COVERS_SL;
                    break;

                case "snap":
                    folder = SNAP_SL;
                    break;

                case TITLES:
                    folder = TITLES_SL;
                    break;
                default:
                    break;
            }
        } else if (folder.contains(String.valueOf(SLASH))) {
            folder = CABINETS;
        }

        String fileName = gameName + PNG_EXT;

        // Keep but leave turned off too much noise
        if (MFM.isSystemDebug()) {
            // System.out.println("showImage() - Opening : " + folder + FileUtils.DIRECTORY_SEPARATOR + fileName);
        }

        if (new File(mfmSettings.PlaySetDirectories().get(folder) +
                FileUtils.DIRECTORY_SEPARATOR + fileName).exists()) {
            imagePanel.image(mfmSettings.PlaySetDirectories().get(folder) +
                    FileUtils.DIRECTORY_SEPARATOR + fileName);
        } else if (new File(mfmSettings.FullSetDirectories().get(folder) + fileName).exists()) {
            imagePanel.image(mfmSettings.FullSetDirectories().get(folder) + fileName);
        } else if (mfmSettings.getExtrasZipFilesMap().containsKey(folder)) {
            StringBuilder sb = new StringBuilder(MFM.getTempdir());
            sb.append(folder);
            sb.append('-');
            sb.append(fileName);
            File tempImage = new File(sb.toString()); // temp file name needs to be unique
            // if it doesn't exist try in the Zip
            if (!tempImage.exists()) {
                if (folder.contains("_SL")) {
                    fileName = listName + FileUtils.DIRECTORY_SEPARATOR + fileName;
                }
                try {
                    ZipUtils.extractFile(mfmSettings.getExtrasZipFilesMap().get(folder).toPath(),
                            fileName, Paths.get(sb.toString()));
                } catch (UnsupportedOperationException exc) {
                    if (MFM.isSystemDebug()) {
                        System.out.println("UnsupportedOperationException");
                    }
                }
            }
            // if it still doesn't exist we do not have it
            if (!tempImage.exists()) {
                imagePanel.imageReset();
                return;
            }
            imagePanel.image(tempImage);
            tempImage.deleteOnExit();

        } else {
            imagePanel.imageReset();
        }

    }

    private static void showError(String text) {
        JOptionPane.showMessageDialog(mainFrame, text, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    void showManual() {
        String selectedItem = getSelectedItem();
        // NOTE hack for lists with mixed Machine/Software if not isSoftwarelist but Machine not found assume Software
        String manualFolder = (isSoftwarelist || (MAMEInfo.getMachine(selectedItem) == null)) ? MANUALS_SL : MANUALS;
        String fullPath = null;
        File tempManual = null;
        String dir = mfmSettings.PlaySetDirectories().get(manualFolder);
        if ((dir != null) && FileUtils.fileExists(dir, selectedItem + PDF_EXT)) {
            fullPath = mfmSettings.PlaySetDirectories().get(manualFolder) + FileUtils.DIRECTORY_SEPARATOR
                    + selectedItem + PDF_EXT;
        }
        // Can't be guaranteed this exists either!
        else if ((mfmSettings.FullSetDirectories().get(manualFolder) != null) &&
                FileUtils.fileExists(mfmSettings.FullSetDirectories().get(manualFolder),
                        selectedItem + PDF_EXT)) {
            fullPath = mfmSettings.FullSetDirectories().get(manualFolder) + FileUtils.DIRECTORY_SEPARATOR
                    + selectedItem + PDF_EXT;
        } else if (mfmSettings.getExtrasZipFilesMap().containsKey(manualFolder)) {
            StringBuilder sb = new StringBuilder(MFM.getTempdir());
            sb.append(manualFolder);
            sb.append("-");
            sb.append(selectedItem);
            sb.append(PDF_EXT);
            tempManual = new File(sb.toString()); // temp file name needs to be unique

            // if it doesn't exist already now in temp try in the Zip
            if (!tempManual.exists()) {

                if (isSoftwarelist) {
                    // prepend the Softwarelist name to the filename
                    selectedItem = currentListLabel.getName() + FileUtils.DIRECTORY_SEPARATOR + selectedItem;
                }
                ZipUtils.extractFile(mfmSettings.getExtrasZipFilesMap().get(manualFolder).toPath(),
                        selectedItem + PDF_EXT, Paths.get(sb.toString()));
            }
        }

        if (fullPath != null) {
            FileUtils.openFileFromOS(Paths.get(fullPath));
        } else if ((tempManual != null) && tempManual.exists()) {
            FileUtils.openFileFromOS(Paths.get(tempManual.getAbsolutePath()));
        } else {
            showInformation(selectedItem, "No manual found for " + selectedItem);
        }
    }

    void showVideo() {
        String gameName = getSelectedItem();
        MFMVideoActions.showVideo(gameName);
    }

    static void updateUI() {
        SwingUtilities.updateComponentTreeUI(mainFrame);
        mainFrame.validate();
    }

    @Override
    public void keyTyped(KeyEvent e) {
//        System.out.println("Typed: " + e.getKeyCode() + "\t" + e.getKeyChar());
        // If ALT then it is a mnemonic skip
        if (!e.isAltDown()) {
            if (e.getKeyChar() == KeyEvent.VK_A) {
                changeList(MFMListBuilder.ALL);
            }

            if (e.getKeyChar() == KeyEvent.VK_B) {
                changeList(MFMListBuilder.BIOS);
            }

            if (e.getKeyChar() == KeyEvent.VK_C) {
                changeList(MFMListBuilder.CLONE);
            }

            if (e.getKeyChar() == KeyEvent.VK_D) {
                changeList(MFMListBuilder.DEVICES);
            }

            if (e.getKeyChar() == KeyEvent.VK_H) {
                changeList(MFMListBuilder.HORIZONTAL);
            }

            if (e.getKeyChar() == KeyEvent.VK_N) {
                changeList(MFMListBuilder.NO_CLONE);
            }

            if ((e.getKeyChar() == KeyEvent.VK_R) && MFM_Data.getInstance().getDataVersion().contains(MFMListBuilder.ALL)) {
                changeList(MFMListBuilder.RUNNABLE);
            }

            if (e.getKeyChar() == KeyEvent.VK_S) {
                changeList(MFMListBuilder.SYSTEMS);
            }

            if (e.getKeyChar() == KeyEvent.VK_V) {
                changeList(MFMListBuilder.VERTICAL);
            }
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if ((e.getKeyCode() == KeyEvent.VK_A) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
            changeList(MFMListBuilder.ARCADE);
        }

        if ((e.getKeyCode() == KeyEvent.VK_N) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
            changeList(MFMListBuilder.NO_IMPERFECT);
        }

        if ((e.getKeyCode() == KeyEvent.VK_O) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
            openFile();
        }

        if ((e.getKeyCode() == KeyEvent.VK_R) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
            changeList(MFMListBuilder.RASTER);
        }

        if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) && !e.isShiftDown()) {
            changeList(MFMListBuilder.SIMULTANEOUS);
        }

        if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
            changeList(MFMListBuilder.VECTOR);
        }

        if ((e.getKeyCode() == KeyEvent.VK_X) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
            showItemXML();
        }

        // Overloaded CNTRL Z below in Special Functions
        if ((e.getKeyCode() == KeyEvent.VK_Z) && e.isControlDown() && !e.isAltDown() && !e.isShiftDown()) {
            zipLogs();
        }

        if ((e.getKeyCode() == KeyEvent.VK_RIGHT) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
            showNextList(true, true);
        }

        if ((e.getKeyCode() == KeyEvent.VK_LEFT) && e.isControlDown()) {
            showNextList(true, false);
        }

        if ((e.getKeyCode() == KeyEvent.VK_RIGHT) && ((e.getModifiers() & InputEvent.ALT_MASK) != 0)) {
            showNextList(false, true);
        }

        if ((e.getKeyCode() == KeyEvent.VK_LEFT) && ((e.getModifiers() & InputEvent.ALT_MASK) != 0)) {
            showNextList(false, false);
        }

        //================== SPECIAL FUNCTIONS  =================

        if ((e.getKeyCode() == KeyEvent.VK_D) && e.isControlDown() && e.isShiftDown()) {
            MFM_DATmaker.saveBuiltinListsDATs();
        }

        if ((e.getKeyCode() == KeyEvent.VK_J) && e.isControlDown() && e.isShiftDown()) {
            listMachinesToJSON(false);
        }

        if ((e.getKeyCode() == KeyEvent.VK_K) && e.isControlDown() && e.isShiftDown()) {
            listMachinesToJSON(true);
        }

        if ((e.getKeyCode() == KeyEvent.VK_M) && e.isControlDown() && e.isShiftDown()) {
            MAMEInfo.dumpManuDriverList();
        }

        if ((e.getKeyCode() == KeyEvent.VK_S) && e.isControlDown() && e.isShiftDown()) {
            new MAME_Stats().saveStats();
        }

        if ((e.getKeyCode() == KeyEvent.VK_T) && e.isControlDown() && e.isShiftDown()) {
            MFM_Wiki.listtoWikiTable(currentListLabel.getText().substring(0,
                    currentListLabel.getText().indexOf(SPACE_CHAR)));
        }

        if ((e.getKeyCode() == KeyEvent.VK_U) && e.isControlDown() && e.isShiftDown()) {
            MAME_Stats stats = new MAME_Stats();
            MFM_Wiki.statstoWikiTable(stats.getStatsArray(), stats.getStatsHeaders());
        }

        if ((e.getKeyCode() == KeyEvent.VK_Z) && e.isControlDown() && e.isShiftDown()) {
            analyzeCategories();
        }

        if ((e.getKeyCode() == KeyEvent.VK_Z) && e.isControlDown() && e.isAltDown()) {
            JFileChooser jfc = new JFileChooser(MFM.getMfmCategoryDir());
            jfc.setFileFilter(FileUtils.csvFileFilter);
            jfc.showOpenDialog(mainFrame);
            File file = jfc.getSelectedFile();
            String fileName = JOptionPane.showInputDialog("Enter new Categories File Name");
            if (file.exists() && !fileName.isEmpty()) {
                AnalyzeCategories.enterNewCategories(file, fileName);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Name is empty or File selected does not exist",
                        "Category input Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // no entries
    }

    private void showNextList(boolean all, boolean next) {
        if (all) {
            changeList(MFMPlayLists.getInstance().getNextListName(currentListLabel.getName(), next));
        } else {
            changeList(MFMPlayLists.getInstance().getNextMyListName(currentListLabel.getName(), next));
        }
    }

    static void zipLogs() {
        ZipUtils zipUtils = new ZipUtils();
        String sb = MFM.getMfmDir() + DIRECTORY_SEPARATOR + "MFM_Logs_" + MFM.LOG_NUMBER + ".zip";
        zipUtils.zipIt(sb, new File(MFM.getMfmLogsDir()), MFM.getMfmLogsDir());
        if (Desktop.isDesktopSupported()) {
            File dir = new File(MFM.getMfmDir());
            try {
                Desktop.getDesktop().open(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void postToPastie() {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(MFM.getErrorLog().toPath(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (lines == null) {
            MFM.getLogger().out("No error log entires to post to Pastie.");
            return;
        }
        String[] strings = lines.toArray(new String[0]);
        String[] strings2 = new String[100];
        if (strings.length > 100) {
            System.arraycopy(strings, strings.length - 101, strings2, 0, 100);
        }

        StringBuilder builder = new StringBuilder(250);
        for (String s : strings2) {
            if (s != null) {
                builder.append(s);
                builder.append(NEWLINE);
            }
        }

        if (builder.length() > 0) {
            Pastie pastie = new Pastie();
            //    String result = pastie.postFile(new File(sourceFolder + "\\MFM-logs.zip"));
            String result = null;
            try {
                result = pastie.postText(builder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if ((result != null) && !result.isEmpty()) {
                System.out.println(result);
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable stringSelection = new StringSelection(result);
                clpbrd.setContents(stringSelection, null);
                JOptionPane.showMessageDialog(mainFrame, "Pastie link is in your Clipboard" +
                        NEWLINE + result);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Error log is empty");
        }

    }

    @SuppressWarnings("squid:S2696")
    final void init() {

        mainFrame = getInstance().getFrame();
        mainFrame.addMouseListener(this);

        components = MFMUI_Setup.getInstance().getMfmComponents();
        machineListTable = components.getMachineListTable();
        folderTree = components.getTree();

        extrasTabbedPane = components.extrasTabbedPane();
        extrasTabbedPane.addMouseListener(this);

        extrasTabbedPane.addKeyListener(this);
        machineListTable.addKeyListener(this);

        listPopupMenu = components.getListPopupMenu();
        softwarelistPopupMenu = components.getSoftwarelistPopupMenuPopupMenu();
        currentListLabel = components.currentListLabel();
        listName = currentListLabel.getName();
        infoPanel = MFM_Components.infoPanel();
        statusBar = components.statusBar();

        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.repaint();

        // Note order must be last!
        loadState();
        updateUI();
        MFM.getLogger().out(MFM_Data.getInstance().rescanSets());
    }

    private void loadState() {

        // NOTE added 9/20/2016 removing mfmSettings call from MAME class and class rename
        MAMEexe.setBaseArgs(mfmSettings.fullMAMEexePath());

        // NOTE Oh Well Obi complained: I personally hate it when apps grab the whole screen on startup
        mainFrame.setPreferredSize(new Dimension(screenSize.width, screenSize.height));
        mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        mainFrame.addWindowListener(new MFMController.MFMWindow());
        setFontSize(mainFrame);

        String list = mfmSettings.MFMCurrentList();
        if ((list != null) && (!list.isEmpty())) {
            changeList(list);
        } else {
            changeList(MFMListBuilder.ALL);
        }

        Integer tabIndex = mfmSettings.selectedTab();
        if ((tabIndex != null) && (tabIndex > 0)) {
            components.extrasTabbedPane().setSelectedIndex(tabIndex);
        }

        String lnF = mfmSettings.MFMLookAndFeel();
        if ((lnF != null) && (!lnF.isEmpty())) {
            changeLnF(lnF);
        }
    }

    static void changeLnF(String lnF) {
        SwingUtils.changeLandF(lnF, mainFrame);
        SwingUtilities.updateComponentTreeUI(mainFrame);
    }

    void vDUB() {
        final String selectedGame = getSelectedItem();
        VideoUtils.runVirtualDub(mfmSettings.VIDsFullSetDir() + FileUtils.DIRECTORY_SEPARATOR + selectedGame + ".avi");
    }

    void showGameVideoInfo() {
        Machine machine = MAMEInfo.getMachine(getSelectedItem());
        String orientation = machine.getIsVertical();
        showInformation(machine.getName() + " video information",
                orientation + NEWLINE + "--------------------------" + NEWLINE +
                        "Width : " + machine.getWidth() + NEWLINE +
                        "Height : " + machine.getHeight() +
                        NEWLINE + "--------------------------" + NEWLINE +
                        "Aspect Ratio: " + MAMECommands.aspectRatio(machine)
        );
    }

    static void videoAction(final String action) {
        MFMVideoActions.videoAction(action, infoPanel);
    }

    static void ffmpegSettings() {
        final FFMPEG_Panel ffmpegPanel = new FFMPEG_Panel();
        ffmpegPanel.showSettingsPanel(mainFrame);
    }

    void cropAVI() {
        final String machine = getSelectedItem();
        MFMVideoActions.cropAVI(machine, infoPanel);
    }

    void refreshVersion() {
        if (!MFM.isFirstRun()) {
            ((JLabel) statusBar.getZone(VERSION)).setText("MAME " + mfmSettings.getMAMEVersion() +
                    " : DATA " + MFM_Data.getInstance().getDataVersion());
        }
    }

    private void refreshRunnable() {
        ((JLabel) statusBar.getZone(WORKING)).setText(RUNNABLE + MAMEInfo.getRunnable());
    }

/*  Retain for debugging
    void MAMEControlsDUMP() {
        MachineControllers.dumpAllWaysControls();
        JOptionPane.showMessageDialog(mainFrame, "Files are in the MFM/Lists folder");
    }
*/

    static void listMachinesToCSV() {
        MFMListActions.listDataToCSV(pickList(true, "Pick list to export MAME data to CSV"));
        JOptionPane.showMessageDialog(mainFrame, FILES_ARE_IN_THE_MFM_LISTS_FOLDER);
    }

    private static void listMachinesToJSON(boolean everything) {

        if (everything) {
            MFMListActions.listDataToJSON(MFMPlayLists.EVERYTHING);
            return;
        }

        String list = pickList(true, "Pick list to export MAME data to JSON");
        if (list != null) {
            MFMListActions.listDataToJSON(list);
            JOptionPane.showMessageDialog(mainFrame, FILES_ARE_IN_THE_MFM_LISTS_FOLDER);
        }
    }

    // Todo move the tree display methods out of controller
    static void showMAMEXML() {
        // we know it is a JSplitPane see MAMEUI_Setup
        //    Container container = folderTree.getParent().getParent().getParent(); // JViewPort -> JScrollPane -> JSplitPane
        //    container.remove(folderTree);
        Container container = (Container) mainFrame.getContentPane().getComponents()[0];
        if (mfmSettings.isShowXML()) {
            ((JSplitPane) container).setLeftComponent(new JScrollPane(MAMEtoJTree.getInstance(false).getMAMEjTree()));
            ((JSplitPane) container).setDividerLocation(DIVIDER_LOCATION);
        } else {
            MFMUI_Setup.getInstance().refreshLeftPane();
        }
        updateUI();
    }

    void showItemXML() {
        String selectedItem = getSelectedItem();
        JDialog dialog = new JDialog(mainFrame, selectedItem);
        JScrollPane scrollPane = new JScrollPane();

        // NOTE hack for lists with mixed Machine/Software if not isSoftwarelist but Machine not found assume Software
        // fixme ouch nested ternary for mixed lists
        DefaultMutableTreeNode itemNode = (isSoftwarelist || (MAMEInfo.getMachine(selectedItem) == null)) ?
                (MAMEInfo.isSoftwareList(currentListLabel.getName()) ?
                        SoftwareListtoJTree.getInstance(currentListLabel.getName()).getSoftwareNode(selectedItem) :
                        SoftwareListtoJTree.getInstance(selectedItem.split(MFM_Constants.SOFTWARE_LIST_SEPARATER)[0])
                                .getSoftwareNode(selectedItem))
                : MAMEtoJTree.getInstance(false).getMachineNode(selectedItem);


        if (itemNode == null) {
            return; // probably should notify user but we should never get here
        }

        JTree tree = new JTree(itemNode);

        // Duplicative of MAMEtoJTree & SoftwareListtoJTree fixme
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON3) && (selPath != null)) {
                    MAMEtoJTree.getInstance(false).copytoClipboard(selPath.getLastPathComponent().toString());
                }
            }
        });

        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK), MAMEtoJTree.COPY);
        tree.getActionMap().put(MAMEtoJTree.COPY,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DefaultMutableTreeNode node
                                = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                        String value = node.getUserObject().toString();
                        MAMEtoJTree.getInstance(false).copytoClipboard(value);
                    }
                });
        // Expand all if softwarelist -> NOTE my guess that if looking at this XML will be easier if expanded
        if (isSoftwarelist) {
            int max = tree.getRowCount();
            for (int i = 0; i < max; i++) {
                tree.expandRow(i);
            }
        }

        SwingUtils.changeFont(tree, mainFrame.getFont().getSize());
        SwingUtils.updateIcons(tree);

        scrollPane.getViewport().add(tree);
        scrollPane.setPreferredSize(new Dimension(SCROLLPANE_WIDTH, mainFrame.getHeight() - 100));
        dialog.getContentPane().add(scrollPane);
        dialog.pack();
        dialog.setLocation(100, INT_50);
        dialog.setVisible(true);
    }

    static void openFile() {
        JFileChooser fileChooser = new JFileChooser(MFM.getMfmListsDir());
        int returnValue = fileChooser.showOpenDialog(mainFrame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            openFileFromOS(fileChooser.getSelectedFile().toPath());
        }
    }

    void loadDataSet(boolean pickList) {
        String dataSet = mfmSettings.getDataVersion();
        if (pickList || (dataSet == null)) {
            // Special case first run no Data Sets found!
            int dSets = MFM_Data.getInstance().getDataSets().length;
            if (dSets < 1) {
                if (MFM.isSystemDebug()) {
                    System.out.println("In loadDataSet dSets is : " + dSets);
                }
                //    REMOVED FIRST RUN PARSE OPTION Oct 2017
                showDataSetsURL();
            } else if ((dSets == 1) && (mainFrame != null)) {
                return; // Already loaded NOTE look for a better way to do this
            } else if (dSets == 1) {
                dataSet = MFM_Data.getInstance().getDataSets()[0];
            } else {
                dataSet = mfmSettings.pickVersion();
            }
        }

        if ((dataSet != null) && !dataSet.isEmpty() && !"null".equals(dataSet)) {
            mfmSettings.setDataVersion(dataSet);
        } else {
            return;
        }

        if (mainFrame != null) {
            infoPanel.showProgress("Loading Data");
        } else {
            MFMUI.showBusy(true, true);
        }
        this.loadDataSet(dataSet);
    }

    private void loadDataSet(String dataSet) {
        SwingWorker sw = new SwingWorker() {
            @SuppressWarnings("RedundantThrows")
            @Override
            protected Object doInBackground() throws Exception {
                // Load this Data Set
                MFM_Data.getInstance().loadDataSet(dataSet);
                // Refresh MameInfo
                MAMEInfo.getInstance(true, false, false);
                MFM_Data.getInstance().setLoaded();
                return null;
            }

            @Override
            protected void done() {
                updateuiData(dataSet);
            }
        };
        Thread loadDataSet = new Thread(sw);
        loadDataSet.start();
    }

    void updateuiData(String dataSet) {
        if ((mainFrame != null) && mainFrame.isVisible() && !firstLoad) {
            // Refresh MAME JTree
            MAMEtoJTree.getInstance(true);
            MFMUI_Setup.getInstance().refreshLeftPane();

            // Force table refresh if needed
            changeList(currentListLabel.getName());

            updateUI();

            // Change UI display to new version
            refreshVersion();
            refreshRunnable();
            MFMUI_Setup.getInstance().updateMenuBar("");
            infoPanel.showMessage(dataSet + LOADED);
        } else {
            firstLoad = false;
            // Hack for Parsing on first run load
            if (mainFrame != null) {
                updateUI();
                infoPanel.showMessage(dataSet + LOADED);
            }
        }
    }

    /**
     * First run hack No Data Set
     *
     * @deprecated
     **/
    @Deprecated
    private static void showDataSetsURL() {
        JDialog urlDialog = new JDialog(mainFrame, "No Data Set");
        urlDialog.getContentPane().add(LinkEditorPane.getLinkPane("Download a Data Set",
                "https://github.com/phweda/MFM/releases"));

        urlDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        urlDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                System.out.println("Exit No Data Set");
                MFM.exit(10);
            }
        });

        urlDialog.setLocation(MFMUI.screenCenterPoint);
        urlDialog.setPreferredSize(new Dimension(225, 80));
        urlDialog.setMinimumSize(new Dimension(225, 80));
        urlDialog.setIconImage(MFMUI_Setup.getMFMIcon().getImage());
        urlDialog.pack();
        urlDialog.setVisible(true);

        try {
            System.out.println("Waiting to exit No Data Set");
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        urlDialog.dispose();
        MFM.exit(10);
    }

    /**
     * Parses the currently set MAME exe
     *
     * @param all flag if false just parse and save Runnable machines
     */
    void parseMAME(boolean all) {
        infoPanel.showProgress("Parsing MAME :  " + mfmSettings.getMAMEVersion());
        SwingWorker sw = new SwingWorker() {
            @SuppressWarnings("RedundantThrows")
            @Override
            protected Object doInBackground() throws Exception {
                synchronized (mainFrame) {
                    MAMEInfo.getInstance(true, true, all);
                }
                return null;
            }

            @Override
            protected void done() {
                MFM_Data.getInstance().rescanSets();
                String dataSet = MFM_Data.getInstance().getDataVersion();
                mfmSettings.setDataVersion(dataSet);
                // Data already loaded during Parse operation just refresh the UI
                updateuiData(dataSet);
            }
        };
        Thread parseMAME = new Thread(sw);
        parseMAME.start();
    }

    private static final class MFMWindow extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            MFM.getLogger().addToList("MFM Closing on frame closing command", true);
            MFM.exit();
        }
    }
}
