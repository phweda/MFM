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

import Phweda.MFM.*;
import Phweda.MFM.Utils.MFM_DATmaker;
import Phweda.MFM.mame.Control;
import Phweda.MFM.mame.Device;
import Phweda.MFM.mame.Machine;
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
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static Phweda.MFM.MFM_Constants.*;
import static Phweda.MFM.UI.MFMListActions.pickList;
import static Phweda.MFM.UI.MFMUI_Setup.*;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 1/22/12
 * Time: 5:11 AM
 */

/**
 * GUI Controller
 */
class MFMController extends ClickListener implements ListSelectionListener, ChangeListener, KeyListener {
    static final DecimalFormat decimalFormater = new DecimalFormat("###,###");
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static JFrame mainFrame;
    private static JTree folderTree;
    private static JTable machineListTable;
    private static JTabbedPane extrasTabbedPane;
    private static JPopupMenu MFMPopupMenu;
    private static JLabel currentListName;
    private static MFMInformationPanel infoPanel;
    private static StatusBar statusBar;
    private final String PlayList_Mode = "playlist"; // Create, view and play games from playlists
    private final JTextPane HTMLtextPane = new MFMHTMLTextPane();
    private String currentMode = PlayList_Mode;
    private boolean firstLoad = true;

    private static MFMSettings mfmSettings = MFMSettings.getInstance();

    static void showListInfo(String listName) {
        TreeSet<String> list = MFMPlayLists.getInstance().getPlayList(listName);
        String output = decimalFormater.format(list.size());
        currentListName.setText(listName + " - " + output);
        currentListName.setName(listName);
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
        optionPane.textArea.scrollRectToVisible(new Rectangle(0, 0, 50, 20));
    }

    static void setFontSize(Container container) {
        if (mfmSettings.MFMFontSize() != null &&
                !mfmSettings.MFMFontSize().equals(MFM_Constants.NORMAL)) {

            int num = mfmSettings.MFMFontSize().equals(MFM_Constants.LARGE) ?
                    MFM_Constants.LARGEINT : MFM_Constants.VERYLARGEINT;

            SwingUtils.changeFont(container, MFM_Constants.FONTSIZEINT + num);
        }
    }

    /*
    * NOTE always call super when you override this method
    *      to ensure double click behavior
    */
    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        if (e.isPopupTrigger() && (e.getSource() == machineListTable || e.getSource() == folderTree)) {
            MFMPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void singleClick(MouseEvent e) {
        if (e.getSource() == extrasTabbedPane && e.getButton() == MouseEvent.BUTTON3) {
            int i = JOptionPane.showConfirmDialog(extrasTabbedPane, "Open the Image?");
            if (i == JOptionPane.YES_OPTION) {
                openImage();
            }
        } else if (folderTree != null && e.getSource() == folderTree && e.getButton() == MouseEvent.BUTTON1) {
            // GOTO selected game
            gotoGame(getMouseSelectedGame());
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
            } else if (nextRow > machineListTable.getRowCount() - 1) {
                nextRow = machineListTable.getRowCount() - 1;
            }

            machineListTable.getSelectionModel().setSelectionInterval(0, nextRow);
            machineListTable.scrollRectToVisible(new Rectangle(machineListTable.getCellRect(nextRow, 0, true)));
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

    void changeList(String listName) {
        TreeSet<String> list = MFMPlayLists.getInstance().getALLPlayListsTree().get(listName);
        if (list == null || (list.size() < 1)) {
            showError(listName + " is empty");
            listName = MFMListBuilder.ALL;
            list = MFMPlayLists.getInstance().getALLPlayListsTree().get(MFMListBuilder.ALL);
        }

        MachineListTableModel gltm = (MachineListTableModel) machineListTable.getModel();
        gltm.setData(list);
        // table change event now in tabelmodel
        showListInfo(listName);
        updateUI();
        mfmSettings.MFMCurrentList(listName);
    }

    void commandDialog() {
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

    private String getSelectedMachine() {
        String machineName = null;
        if (folderTree != null && MFMPopupMenu.getInvoker() == folderTree) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
            if (node == null) {
                return null;
            }
            Object nodeInfo = node.getUserObject();
            machineName = nodeInfo.toString();
        } else { // if (MFMPopupMenu.getInvoker() == MachineListTable || MachineListTable.hasFocus())

            int row = machineListTable.getSelectedRow();
            // If no row is selected return ** row == -1
            if (row < 0) {
                return null;
            }
            row = machineListTable.convertRowIndexToModel(row);
            machineName = (String) machineListTable.getModel().getValueAt(row, MachineListTable.keyColumn);
            if (MFM.isDebug()) {
                MFM.logger.addToList(machineName);
            }
        }
        return machineName;
    }

    private void gotoGame(String gameName) {
        int row = 0;
        while (row < machineListTable.getRowCount()) {
            if (machineListTable.getModel().getValueAt(row, MachineListTable.keyColumn).equals(gameName)) {
                row = machineListTable.convertRowIndexToView(row);
                machineListTable.getSelectionModel().setSelectionInterval(row, row);
                machineListTable.scrollRectToVisible(new Rectangle(machineListTable.getCellRect(row, 0, true)));
                break;
            }
            row++;
        }
    }

    void addtoList() {
        String machine = getSelectedMachine();
        MFMListActions.addtoList(machine);
    }

    void removefromList() {
        MFMListActions.removefromList(getSelectedMachine(), currentListName.getName());
    }

    void removeList() {
        String result = MFMListActions.removeList();
        if (currentListName.getText().equals(result)) {
            changeList(result);
        }
        updateUI();
    }

    void showListEditor() {
        MFMListActions.showListEditor();
    }

    void ListtoFile() {
        MFMListActions.ListtoFile();
    }

    void ListtoDAT() {
        MFMListActions.ListtoDAT();
    }

    void DATtoList() {
        changeList(MFMListActions.DATtoList());
    }

    void FilterDATbyList() {
        MFMListActions.filterDATbyList();
    }

    void FilterDATbyExternalList() {
        MFMListActions.filterDATbyExternalList();
    }

    void importList() {
        String listName = MFMListActions.importList(getFrame());
        changeList(listName);
        MFMUI_Setup.getInstance().updateMenuBar(listName);
    }

    void runGame(String command) {
        String gameName = getSelectedMachine();
        if (MFM.isDebug()) {
            MFM.logger.out("Machine is: " + gameName);
        }
        ArrayList<String> args = null;
        switch (command) {

            case MAMECommands.RECORD:
                args = MAMECommands.recordGame(gameName);
                break;
            case MAMECommands.PLAYBACK:
                args = MAMECommands.playbackGame(gameName);
                break;
            case MAMECommands.PLAYBACKtoAVI:
                args = MAMECommands.createAVIfromPlayback(gameName);
                break;
            case MAMECommands.AVIWRITE:
                args = MAMECommands.playGametoAVI(gameName);
                break;

            default:
                args = new ArrayList<String>(1);
                args.add(gameName);
        }

        try {
            if (args == null) {
                // NOTE Should never get here
                return;
            }
            Process mameProcess = MAMEexe.run(args, MFM.MAMEout);
        } catch (MAMEexe.MAME_Exception e) {
            infoPanel.showMessage(gameName + " had errors");
            //        showInformation("MAME error", e.getError());
        }
    }

    void showHelp(String text) {

        switch (text) {
            case "About":
                showInformation("MAME File Manager",
                        "Created December 2011\n" +
                                "Coded by phweda\n" +
                                "phweda1@yahoo.com\n" +
                                MFM.VERSION + "\t" + MFM.RELEASE_DATE
                );
                break;
            case "MFM User Guide":
                FileUtils.openFileFromOS(Paths.get(MFM.MFM_DIR + MFM.MFM_User_Guide));
                break;
            case "MFM Copyright":
                showHTML("MFM Copyright", MFMUI_Resources.MFM_COPYRIGHT_HTML);
                break;
            case "GNU GPL":
                showHTML("GNU GPL V3", MFMUI_Resources.GNU_GPL_V3);
                break;
        }
    }

    void showHistory() {
        String gameName = getSelectedMachine();
        // TODO do we need this? Think not
        if (gameName == null) {
            return;
        }
        String history = MAMEInfo.getMachine(gameName).getHistory();
        if (history != null) {
            showInformation(gameName, history);
        } else {
            showInformation(gameName, "No history found for " + gameName);
        }
    }

    void showInfo() {
        String gameName = getSelectedMachine();
        // TODO do we need this? Think not
        if (gameName == null) {
            return;
        }
        String info = MAMEInfo.getMachine(gameName).getInfo();
        if (info != null) {
            showInformation(gameName, info);
        } else {
            showInformation(gameName, "No MAME info found for " + gameName);
        }
    }

    void showControlsDevices() {
        String machineName = getSelectedMachine();
        Machine machine = MAMEInfo.getMachine(machineName);
        if (machine.getIsdevice().equals(Machine.YES) || machine.getIsbios().equals(Machine.YES)) {
            return;
        }

        StringBuilder info = new StringBuilder();
        List<Control> controls = machine.getInput().getControl();
        info.append("Controls:\n\t");
        for (Control control : controls) {
            info.append(Controllers.getControllerMAMEtoLabel().get(control.getType()));
            info.append("\n\t");
        }

        info.append("\nPlayers:\t");
        info.append(machine.getInput().getPlayers());
        info.append("\nButtons:\t");
        info.append(machine.getButtons());
        info.append("\n\nDEVICES:\n");
        List<Device> list = machine.getDevice();
        for (Device device : list) {
            info.append("\t");
            info.append(device.getTag());
            info.append("\n");
        }
        showInformation(machineName, info.toString());
    }

    private void showHTML(String title, String path) {
        JScrollPane scrollPane = new JScrollPane();

/*  fixme Bug with HTML embedded URL Link
        HTMLtextPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                // createFontResizePopup().setVisible(true);
                createFontResizePopup().show(HTMLtextPane, 10, 10);
            }
        });
*/
        //    HTMLtextPane.add(createFontResizePopup());
        URL url;
        url = MFMUI_Resources.getInstance().ResourceURLs().get(path);
        try {
            HTMLtextPane.setPage(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HTMLtextPane.setPreferredSize(new Dimension(850, 600));
        scrollPane.setViewportView(HTMLtextPane);
        // NOTE we do all of this to have a resizable dialog.
        Object[] array = {
                new JLabel(title),
                scrollPane,
        };
        JOptionPane pane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = pane.createDialog(mainFrame, "");
        dialog.setLocation(150, 150);
        dialog.setResizable(true);
        dialog.setVisible(true);
    }

    void showMFMmessage(String message) {
        infoPanel.showMessage(message);
    }

    void openImage() {
        ImagePanel imagePanel = (ImagePanel) extrasTabbedPane.getSelectedComponent();
        Path fullPath = imagePanel.getImagePath();
        FileUtils.openFileFromOS(fullPath);
    }

    void GOTOcloneof() {
        // GOTO selected game's parent(cloneof)
        Machine machine = MAMEInfo.getMachine(getSelectedMachine());
        if (machine != null && machine.getCloneof() != null && !machine.getCloneof().equals("")) {
            gotoGame(machine.getCloneof());
        }
    }

    private void showImage() {
        ImagePanel imagePanel = (ImagePanel) extrasTabbedPane.getSelectedComponent();
        int row = machineListTable.getSelectedRow();
        // If no row is selected return. ** row == -1
        if (row < 0) {
            imagePanel.ImageReset();
            return;
        }
        try {
            row = machineListTable.convertRowIndexToModel(row);
        } catch (Exception e) {
            imagePanel.ImageReset();
            return;
        }
        String gameName = (String) machineListTable.getModel().getValueAt(row, MachineListTable.keyColumn);
        int index = extrasTabbedPane.getSelectedIndex();
        // toLowerCase so we can Capitalize in the UI
        String folder = extrasTabbedPane.getTitleAt(index).toLowerCase();
        String fileName = gameName + PNG_EXT;

        // Keep but leave turned off too much noise
        if (MFM.isSystemDebug()) {
            //   System.out.println("showImage() - Opening : " + folder + fileName);
        }

        // TODO update for zipped Extras Done 11/17/16 needs testing
        if (new File(mfmSettings.PlaySetDirectories().get(folder) +
                FileUtils.DIRECTORY_SEPARATOR + fileName).exists()) {
            imagePanel.Image(mfmSettings.PlaySetDirectories().get(folder) + FileUtils.DIRECTORY_SEPARATOR + fileName);
        } else if (new File(mfmSettings.FullSetDirectories().get(folder) + fileName).exists()) {
            imagePanel.Image(mfmSettings.FullSetDirectories().get(folder) + fileName);
        } else if (mfmSettings.getExtrasZipFilesMap().containsKey(folder)) {
            try {
                StringBuilder sb = new StringBuilder(MFM.TEMP_DIR);
                sb.append(folder);
                sb.append("-");
                sb.append(fileName);
                File tempImage = new File(sb.toString()); // temp file name needs to be unique
                // if it doesn't exist try in the Zip
                if (!tempImage.exists()) {
                    ZipUtils.extractFile(mfmSettings.getExtrasZipFilesMap().get(folder).toPath(),
                            fileName, Paths.get(sb.toString()));
                }
                // if it still doesn't exist we do not have it
                if (!tempImage.exists()) {
                    imagePanel.ImageReset();
                    return;
                }
                imagePanel.Image(tempImage);
                tempImage.deleteOnExit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            imagePanel.ImageReset();
        }
    }

    private void showError(String text) {
        JOptionPane.showMessageDialog(mainFrame, text, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    void showManual() {
        String machineName = getSelectedMachine();
        String fullPath = null;
        File tempManual = null;
        String dir = mfmSettings.PlaySetDirectories().get(MANUALS);
        if (dir != null && FileUtils.fileExists(dir, machineName + PDF_EXT)) {
            fullPath = mfmSettings.PlaySetDirectories().get(MANUALS) + FileUtils.DIRECTORY_SEPARATOR
                    + machineName + PDF_EXT;
        }
        // Can't be guaranteed this exists either!
        else if (mfmSettings.FullSetDirectories().get(MANUALS) != null &&
                FileUtils.fileExists(mfmSettings.FullSetDirectories().get(MANUALS),
                        machineName + PDF_EXT)) {
            fullPath = mfmSettings.FullSetDirectories().get(MANUALS) + FileUtils.DIRECTORY_SEPARATOR
                    + machineName + PDF_EXT;
        } else if (mfmSettings.getExtrasZipFilesMap().containsKey(MANUALS)) {
            StringBuilder sb = new StringBuilder(MFM.TEMP_DIR);
            sb.append(MANUALS);
            sb.append("-");
            sb.append(machineName);
            sb.append(PDF_EXT);
            tempManual = new File(sb.toString()); // temp file name needs to be unique
            // if it doesn't exist try in the Zip
            if (!tempManual.exists()) {
                try {
                    ZipUtils.extractFile(mfmSettings.getExtrasZipFilesMap().get(MANUALS).toPath(),
                            machineName + PDF_EXT, Paths.get(sb.toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (fullPath != null) {
            FileUtils.openFileFromOS(Paths.get(fullPath));
        } else if (tempManual != null && tempManual.exists()) {
            FileUtils.openFileFromOS(Paths.get(tempManual.getAbsolutePath()));
        } else {
            showInformation(machineName, "No manual found for " + machineName);
        }
    }

    void showLog(String log) {
        switch (log) {
            case MFMAction.LogAction:
                FileUtils.openFileFromOS(Paths.get(MFM.Log.getAbsolutePath()));
                break;
            case MFMAction.MAME_OUTPUTAction:
                FileUtils.openFileFromOS(Paths.get(MFM.MAMEout.getAbsolutePath()));
                break;
            case MFMAction.ErrorLogAction:
                FileUtils.openFileFromOS(Paths.get(MFM.ErrorLog.getAbsolutePath()));
                break;
            case MFMAction.GCLogAction:
                FileUtils.openFileFromOS(Paths.get(MFM.MFM_LOGS_DIR + "MFM_GC_log.txt"));
                break;
        }
    }

    void showVideo() {
        String gameName = getSelectedMachine();
        MFMVideoActions.showVideo(gameName);
    }

    /**
     * Scan all resource roots for resources for this list.
     * Log and report results
     *
     * @param copy If true copy the files else just report results
     */
    void copyResources(final boolean copy) {
        MFMListActions.copyResources(copy);
    }

    void scanResources() {
        MFMListActions.scanResources();
    }

    void updateUI() {
        SwingUtilities.updateComponentTreeUI(mainFrame);
        mainFrame.validate();
    }

    void showSettings() {
        MFM_SettingsPanel.showSettingsPanel(MFMUI.getSettings());
    }

    private void update() {
        // fixme 11/12/16 can't we eliminate??
        switch (currentMode) {
            case PlayList_Mode:
                if (!mainFrame.isVisible()) {
                    mainFrame.setVisible(true);
                } else {
                    mainFrame.pack();
                    machineListTable.requestFocus();
                }
                break;
        }
        updateUI();
    }

    @Override
    public void keyTyped(KeyEvent e) {
//        System.out.println("Typed: " + e.getKeyCode() + "\t" + e.getKeyChar());

        if (e.getKeyChar() == 'a') {
            changeList(MFMListBuilder.ALL);
        }

        if (e.getKeyChar() == 'c') {
            changeList(MFMListBuilder.CLONE);
        }

        if (e.getKeyChar() == 'n') {
            changeList(MFMListBuilder.NO_CLONE);
        }

        if (e.getKeyChar() == 'h') {
            changeList(MFMListBuilder.HORIZONTAL);
        }

        if (e.getKeyChar() == 'r') {
            changeList(MFMListBuilder.RUNNABLE);
        }

        if (e.getKeyChar() == 's') {
            changeList(MFMListBuilder.SYSTEMS);
        }

        if (e.getKeyChar() == 'v') {
            changeList(MFMListBuilder.VERTICAL);
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if ((e.getKeyCode() == KeyEvent.VK_A) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            changeList(MFMListBuilder.ARCADE);
        }

        if ((e.getKeyCode() == KeyEvent.VK_N) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            changeList(MFMListBuilder.NO_IMPERFECT);
        }

        if ((e.getKeyCode() == KeyEvent.VK_O) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            openFile();
        }

        if ((e.getKeyCode() == KeyEvent.VK_R) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            changeList(MFMListBuilder.RASTER);
        }

        if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) && !e.isShiftDown()) {
            changeList(MFMListBuilder.SIMULTANEOUS);
        }

        if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            changeList(MFMListBuilder.VECTOR);
        }

        if ((e.getKeyCode() == KeyEvent.VK_X) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            showMachinetree();
        }

        if ((e.getKeyCode() == KeyEvent.VK_Z) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            zipLogs();
        }

        if ((e.getKeyCode() == KeyEvent.VK_RIGHT) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            showNextList(true, true);
        }

        if ((e.getKeyCode() == KeyEvent.VK_LEFT) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            showNextList(true, false);
        }

        if ((e.getKeyCode() == KeyEvent.VK_RIGHT) && ((e.getModifiers() & KeyEvent.ALT_MASK) != 0)) {
            showNextList(false, true);
        }

        if ((e.getKeyCode() == KeyEvent.VK_LEFT) && ((e.getModifiers() & KeyEvent.ALT_MASK) != 0)) {
            showNextList(false, false);
        }

        if ((e.getKeyCode() == KeyEvent.VK_S) && e.isControlDown() && e.isShiftDown()) {
            new MAME_Stats().saveStats();
        }

        if (e.getKeyCode() == KeyEvent.VK_D && e.isControlDown() && e.isShiftDown()) {
            MFM_DATmaker.saveBuiltinListsDATs();
        }

        if (e.getKeyCode() == KeyEvent.VK_J && e.isControlDown() && e.isShiftDown()) {
            listMachinesToJSON();
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void showNextList(boolean all, boolean next) {
        if (all) {
            changeList(MFMPlayLists.getInstance().getNextListName(currentListName.getName(), next));
        } else {
            changeList(MFMPlayLists.getInstance().getNextMyListName(currentListName.getName(), next));
        }
    }

    void zipLogs() {
        StringBuilder sb = new StringBuilder();
        sb.append(MFM.MFM_DIR);
        sb.append(FileUtils.DIRECTORY_SEPARATOR);
        sb.append("MFM_Logs_");
        sb.append(MFM.logNumber);
        sb.append(".zip");

        ZipUtils zipUtils = new ZipUtils();
        zipUtils.zipIt(sb.toString(), new File(MFM.MFM_LOGS_DIR), MFM.MFM_LOGS_DIR);
        if (Desktop.isDesktopSupported()) {
            File dir = new File(MFM.MFM_DIR);
            try {
                Desktop.getDesktop().open(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void postToPastie() {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(MFM.ErrorLog.toPath(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] strings = lines.toArray(new String[lines.size()]);
        String[] strings2 = new String[100];
        if (strings.length > 100) {
            System.arraycopy(strings, strings.length - 101, strings2, 0, 100);
        }

        StringBuilder builder = new StringBuilder();
        for (String s : strings2) {
            if (s != null) {
                builder.append(s + FileUtils.NEWLINE);
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
            if (null != result && !result.isEmpty()) {
                System.out.println(result);
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(result);
                clpbrd.setContents(stringSelection, null);
                JOptionPane.showMessageDialog(getFrame(), "Pastie link is in your Clipboard\n" + result);
            }
        } else {
            JOptionPane.showMessageDialog(getFrame(), "Error log is empty");
        }

    }

    void init() {

        mainFrame = getInstance().getFrame();
        mainFrame.addMouseListener(this);

        MFM_Components components = getComps();
        machineListTable = components.getMachineListTable();
        folderTree = components.getTree();

        extrasTabbedPane = components.ExtrasTabbedPane();
        extrasTabbedPane.addMouseListener(this);

        extrasTabbedPane.addKeyListener(this);
        machineListTable.addKeyListener(this);

        MFMPopupMenu = MFM_Components.MFMPopupMenu();
        currentListName = components.CurrentListName();
        infoPanel = MFM_Components.InfoPanel();
        statusBar = MFM_Components.StatusBar();

        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.repaint();
        // Note order must be last!
        loadState();
        update();
    }

    private void loadState() {

        // NOTE added 9/20/2016 removing mfmSettings call from MAME class and class rename
        MAMEexe.setBaseArgs(mfmSettings.fullMAMEexePath());

        // NOTE Oh Well Obi complained: I personally hate it when apps grab the whole screen on startup
        mainFrame.setPreferredSize(new Dimension(screenSize.width, screenSize.height));
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.addWindowListener(new MFMUI_Setup.MFMWindow());
        setFontSize(mainFrame);

        String list = mfmSettings.MFMCurrentList();
        if (list != null && list.length() > 0) {
            changeList(list);
        } else {
            changeList(MFMListBuilder.ALL);
        }

        Integer tabIndex = mfmSettings.selectedTab();
        if (tabIndex != null && tabIndex > 0) {
            comps.ExtrasTabbedPane().setSelectedIndex(tabIndex);
        }

        String LnF = mfmSettings.MFMLookAndFeel();
        if (LnF != null && LnF.length() > 0) {
            changeLnF(LnF);
        }
    }

    void changeLnF(String LnF) {
        SwingUtils.changeLandF(LnF, mainFrame);
        SwingUtilities.updateComponentTreeUI(mainFrame);
    }

    void VDUB() {
        final String selectedGame = getSelectedMachine();
        VideoUtils.runVirtualDub(mfmSettings.VIDsFullSetDir() + FileUtils.DIRECTORY_SEPARATOR + selectedGame + ".avi");
    }

    void showGameVideoInfo() {
        Machine machine = MAMEInfo.getMachine(getSelectedMachine());
        String orientation = machine.getIsVertical();
        showInformation(machine.getName() + " video information",
                orientation + "\n--------------------------\n" +
                        "Width : " + machine.getWidth() + "\n" +
                        "Height : " + machine.getHeight() +
                        "\n--------------------------\n" +
                        "Aspect Ratio: " + MAMECommands.aspectRatio(machine)
        );
    }

    void videoAction(final String action) {
        MFMVideoActions.videoAction(action, infoPanel);
    }

    void FFmpegSettings() {
        final FFMPEG_Panel ffmpeg_panel = new FFMPEG_Panel();
        ffmpeg_panel.showSettingsPanel(mainFrame);
    }

    void runFFmpeg() {
        MFMVideoActions.runFFmpeg();
    }

    void CropAVI() {
        final String machine = getSelectedMachine();
        MFMVideoActions.CropAVI(machine, infoPanel);
    }

    void showListBuilder(String baseList) {
        MFMListActions.openListBuilder(baseList);
    }

    void refreshVersion() {
        if (!MFM.isFirstRun()) {
            ((JLabel) statusBar.getZone("Version")).setText("MAME " + mfmSettings.getMAMEVersion() +
                    " : DATA " + MFM_Data.getInstance().getDataVersion());
        }
    }

    private void refreshRunnable() {
        ((JLabel) statusBar.getZone("Working")).setText("Runnable " + MAMEInfo.getRunnable());
    }

    void MAMEControlsDUMP() {
        Controllers.dumpAllWaysControls();
        JOptionPane.showMessageDialog(mainFrame, "Files are in the MFM/Lists folder");
    }

    void listMachinesToCSV() {
        MFMListActions.listDataToCSV(pickList(true, "Pick list to export MAME data to CSV"));
        JOptionPane.showMessageDialog(mainFrame, "Files are in the MFM/Lists folder");
    }

    void listMachinesToJSON() {
        MFMListActions.listDataToJSON(pickList(true, "Pick list to export MAME data to JSON"));
        JOptionPane.showMessageDialog(mainFrame, "Files are in the MFM/Lists folder");
    }

    void showMAMEtree() {
        // we know it is a JSplitPane see MAMEUI_Setup
        //    Container container = folderTree.getParent().getParent().getParent(); // JViewPort -> JScrollPane -> JSplitPane
        //    container.remove(folderTree);
        Container container = (Container) mainFrame.getContentPane().getComponents()[0];
        ((JSplitPane) container).setLeftComponent(new JScrollPane(MAMEtoJTree.getInstance(false).getMAMEjTree()));
        ((JSplitPane) container).setDividerLocation(.16);
        updateUI();
    }

    void showMachinetree() {
        String machineName = getSelectedMachine();
        JDialog dialog = new JDialog(getFrame(), machineName);
        JScrollPane scrollPane = new JScrollPane();

        DefaultMutableTreeNode machineNode = MAMEtoJTree.getInstance(false).getMachineNode(machineName);
        if (machineNode == null) {
            return; // probably should notify user but we should never get here
        }

        JTree tree = new JTree(machineNode);

        // Duplicative of MAMEtoJTree fixme maybe our own treelistener?
        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
                    Object obj = selPath.getLastPathComponent();
                    if (obj != null) {
                        MAMEtoJTree.getInstance(false).copytoClipboard(obj.toString());
                    }
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
        SwingUtils.changeFont(tree, mainFrame.getFont().getSize());
        SwingUtils.updateIcons(tree);

        scrollPane.getViewport().add(tree);
        scrollPane.setPreferredSize(new Dimension(350, getFrame().getHeight() - 100));
        dialog.getContentPane().add(scrollPane);
        dialog.pack();
        dialog.setLocation(100, 50);
        dialog.setVisible(true);
    }

    void openFile() {
        JFileChooser fileChooser = new JFileChooser(MFM.MFM_LISTS_DIR);
        int returnValue = fileChooser.showOpenDialog(mainFrame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            FileUtils.openFileFromOS(fileChooser.getSelectedFile().toPath());
        }
    }

    void loadDataSet(boolean pickList) {
        String dataSet = mfmSettings.getDataVersion();
        if (pickList || dataSet == null) {
            // Special case first run no Data Sets found!
            int dSets = MFM_Data.getInstance().getDataSets().length;
            if (dSets < 1) {
                if (MFM.isSystemDebug()) {
                    System.out.println("In loadDataSet dSets is : " + dSets);
                }
            //    REMOVED FIRST RUN PARSE OPTION Oct 2017
                showDataSetsURL();
            } else if (dSets == 1 && mainFrame != null) {
                return; // Already loaded NOTE look for a better way to do this
            } else if (dSets == 1 && mainFrame == null) {
                dataSet = MFM_Data.getInstance().getDataSets()[0];
            } else {
                dataSet = MFMSettings.getInstance().pickVersion();
            }
        }

        if (dataSet != null && !dataSet.isEmpty() && !dataSet.equals("null")) {
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
            @Override
            protected Object doInBackground() throws Exception {
                // Load this Data Set
                MFM_Data.getInstance().loadDataSet(dataSet);
                // Refresh MameInfo
                MAMEInfo.getInstance(true, false);
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

    private void updateuiData(String dataSet) {
        if (mainFrame != null && mainFrame.isVisible() && !firstLoad) {
            // Refresh MAME JTree
            MAMEtoJTree.getInstance(true);
            MFMUI_Setup.getInstance().refreshLeftPane();

            // Force table refresh if needed
            changeList(currentListName.getName());

            updateUI();

            // Change UI display to new version
            refreshVersion();
            refreshRunnable();
            MFMUI_Setup.getInstance().updateMenuBar("");
            infoPanel.showMessage(dataSet + " loaded");
        } else {
            firstLoad = false;
            // Hack for Parsing on first run load
            if (mainFrame != null) {
                updateUI();
                infoPanel.showMessage(dataSet + " loaded");
            }
        }
    }

    // First run hack No Data Set
    private void showDataSetsURL() {
        JDialog urlDialog = new JDialog(getFrame(), "No Data Set");
        urlDialog.getContentPane().add(LinkEditorPane.getLinkPane( "Download a Data Set",
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
        urlDialog.setPreferredSize(new Dimension(225,80));
        urlDialog.setMinimumSize(new Dimension(225,80));
        urlDialog.pack();
        urlDialog.setVisible(true);

        try {
            System.out.println("Waiting to exit No Data Set");
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MFM.exit(10);
    }

    private int getFirstRunNoDataSetsOption() {
        Object[] options = {"Parse Mame", "Exit"};
        return JOptionPane.showOptionDialog(mainFrame,
                "No MFM Data Sets detected. Download and place into <MFM Root>/Data/ directory.",
                "No MFM Data Sets",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[1]); //default button title
    }

    void parseMAME(boolean showing) {
        if (showing) {
            infoPanel.showProgress("Parsing MAME :  " + mfmSettings.getMAMEVersion());
        } else {
            MFMUI.showBusy(true, false);
        }
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                synchronized (this) {
                    MAMEInfo.getInstance(true, true);
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
}

