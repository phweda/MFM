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
import Phweda.MFM.Utils.MFMFileOps;
import Phweda.MFM.Utils.MFM_DATmaker;
import Phweda.MFM.datafile.Datafile;
import Phweda.utils.Debug;
import Phweda.utils.FileUtils;
import Phweda.utils.PersistUtils;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import static Phweda.MFM.UI.MFMController.*;
import static Phweda.MFM.UI.MFMUI_Setup.updateMenuBar;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/5/2016
 * Time: 11:24 AM
 */
class MFMListActions {
    private static MFMInformationPanel infoPanel = getInformationPanel();
    private static JFrame mainFrame = getFrame();
    private static JDialog LBdialog;

    static String pickList(boolean all, String message) {
        Object[] data = null;
        if (all) {
            data = MFMPlayLists.getInstance().PlayListNames();
        } else {
            data = MFMPlayLists.getInstance().myPlayListNames();
        }

        @SuppressWarnings("MagicConstant")
        final String list = (String) JOptionPane.showInputDialog(
                mainFrame, message,
                "List Picker", JOptionPane.OK_CANCEL_OPTION, MFMUI_Setup.getMFMIcon(), data, data[0]);

        return list;
    }

    /**
     * Add selected to list or create a new list with this machine
     *
     * @param machine selected machine to add to listg
     */
    static void addtoList(String machine) {
        MFMPlayLists playLists = MFMPlayLists.getInstance();
        Object[] objs = Stream.concat(
                Arrays.stream(new Object[]{MFM_Constants.NEW_LIST}), Arrays.stream(playLists.myPlayListNames()))
                .toArray(Object[]::new);

        JComboBox jcb = new JComboBox(objs);

        // Hack so we can position Dialog where mouse is. Do not want to subclass JOptionPane
        JFrame frame = new JFrame();
        frame.setSize(0,0);
        frame.setLocation(mainFrame.getMousePosition());
        frame.setVisible(true); // frame must be visible

        // now prompt for the list
        JOptionPane.showMessageDialog(frame, jcb);
        frame.dispose();  // get rid of it

        String listName = jcb.getSelectedItem().toString();
        if (listName.equals(MFM_Constants.NEW_LIST)) {
            String newName = JOptionPane.showInputDialog(null, "Enter List Name");
            if (newName == null || newName.isEmpty()) {
                return;
            }
            MFMPlayLists.getInstance().createPlayList(newName, new String[]{machine});
            MFMUI_Setup.updateMenuBar(newName);
            return;
        }
        playLists.addGameToPlayList(listName, machine);
    }

    static void removefromList(String machine, String listName) {
        MFMPlayLists.getInstance().removeGameFromPlayList(listName, machine);
        int row = getMachineListTable().getSelectedRow();

        // TODO Refactor with  changeList(listName)
        MachineListTableModel gltm = (MachineListTableModel) getMachineListTable().getModel();
        gltm.setData(MFMPlayLists.getInstance().getPlayList(listName));
        gltm.fireTableDataChanged();
        // TODO test removal of first and last rows
        getMachineListTable().getSelectionModel().setSelectionInterval(row, row);
        showListInfo(listName);
    }

    static String removeList() {
        Object[] data = MFMPlayLists.getInstance().myPlayListNames();

        @SuppressWarnings("MagicConstant")
        final String result = (String) JOptionPane.showInputDialog(
                mainFrame, "Select list to Remove",
                "Remove", JOptionPane.OK_CANCEL_OPTION, null, data, data[0]);

        if (result != null) {
            MFM.logger.addToList(result + " is being removed", true);
            MFMPlayLists.getInstance().removePlayList(result);
            updateMenuBar("");
        }
        return result;
    }

    static void ListtoFile() {
        Object[] data = MFMPlayLists.getInstance().PlayListNames();
        final String listName = pickList(true, "Select list to Save");

        if (listName != null) {
            MFM.logger.addToList(listName + " is being saved to file", true);
            TreeSet<String> ts = MFMPlayLists.getInstance().getPlayList(listName);
            File listFile = new File(MFM.MFM_LISTS_DIR + listName + " " + MAMEInfo.getVersion().substring(0, 5) + ".txt");
            try {
                PrintWriter pw = new PrintWriter(new FileWriter(listFile));
                for (String game : ts) {
                    pw.println(game);
                }
                pw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

/*  Bug with Desktop.edit
            int open = JOptionPane.showConfirmDialog(mainFrame, "Open List?", "List", JOptionPane.OK_CANCEL_OPTION);
            if (open == JOptionPane.OK_OPTION) {
                FileUtils.openTextFileFromOS(Paths.get(listFile.getAbsolutePath()));
            }
*/
        }
    }

    static void ListtoDAT() {
        final String listName = pickList(true, "Select list for DAT");
        if (listName != null) {
            MFM.logger.addToList("DAT for " + listName + " is being created and saved to file", true);
            TreeSet<String> ts = MFMPlayLists.getInstance().getPlayList(listName);
            //    File DATFile = new File(MFM.MFM_LISTS_DIR + list + " " + MAMEInfo.getVersion().substring(0, 5) + ".DAT");
            try {
                Datafile DATFile = MFM_DATmaker.generateDAT(listName, ts);
                PersistUtils.saveDATtoFile(DATFile, MFM.MFM_LISTS_DIR + listName +
                        "(" + MAMEInfo.getVersion() + ").dat");
            } catch (ParserConfigurationException | TransformerException | JAXBException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static void resourcestoFile(String list, TreeMap<String, Object> files) {
        MFM.logger.addToList(list + " resources are being saved to file", true);
        File listFile = new File(MFM.MFM_LISTS_DIR + list + " " +
                MAMEInfo.getVersion().substring(0, 5) + "_Resources.txt");
        int counter = 1;
        try {

            final PrintWriter pw = new PrintWriter(new FileWriter(listFile));
            String format = "%,7d: ";
            TreeSet<File> romsList = (TreeSet<File>) files.get(MFM_Constants.ROMS);
            for (File file : romsList) {
                String path = file.getAbsolutePath();
                pw.format(format, counter++);
                pw.println(path);
            }
            pw.flush();

            TreeSet<File> chdsList = (TreeSet<File>) files.get(MFM_Constants.CHDS);
            for (File file : chdsList) {
                String path = file.getAbsolutePath();
                pw.format(format, counter++);
                pw.println(path);
            }
            pw.flush();

            for (String folder : ((TreeMap<String, TreeSet<File>>) files.get(MAME_Resources.EXTRAS)).keySet()) {
                TreeSet<File> extraFiles =
                        ((TreeMap<String, TreeSet<File>>) files.get(MAME_Resources.EXTRAS)).get(folder);
                for (File file : extraFiles) {
                    String path = file.getAbsolutePath();
                    pw.format(format, counter++);
                    pw.println(path);
                }
            }
            pw.flush();

            for (String entry : ((TreeMap<String, TreeSet<String>>) files.get(MAME_Resources.ZIPEXTRAS)).keySet()) {
                TreeSet<String> extraFiles =
                        ((TreeMap<String, TreeSet<String>>) files.get(MAME_Resources.ZIPEXTRAS)).get(entry);
                for (String zipEntry : extraFiles) {
                    pw.format(format, counter++);
                    pw.println(entry + ".zip -> " + zipEntry);
                }
            }
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int open = JOptionPane.showConfirmDialog(mainFrame, "Open List Resources?",
                "List", JOptionPane.OK_CANCEL_OPTION);
        if (open == JOptionPane.OK_OPTION) {
            FileUtils.openFileFromOS(Paths.get(listFile.getAbsolutePath()));
        }

    }

    /**
     * Scan all resource roots for resources for this list.
     * Log and report results
     *
     * @param copy If true copy the files else just save results to file
     */
    static void copyResources(final boolean copy) {
        if (!MAME_Resources.getInstance().hasCache()) {
            int returnValue = JOptionPane.showConfirmDialog(mainFrame, "Resource scan must be run first. Run now?",
                    "", JOptionPane.YES_NO_OPTION);
            if (returnValue == JOptionPane.YES_OPTION) {
                scanResources();
                return;
            } else {
                return;
            }
        }

        final String list = pickList(true, "Choose List");
        String message = null;
        if (copy) {
            message = "Copying Resources " + list;
        } else {
            message = "Generating Resources for " + list;
        }
        final String action = message;
        if (copy) {
            infoPanel.showProgress(action);
            infoPanel.updateUI();
        }
        MFM.logger.addToList(action + " started", true);
        SwingWorker sw = new SwingWorker() {
            long startTime;

            @Override
            protected Object doInBackground() throws Exception {
                // Resources are File obj except zipped extras which are String
                TreeMap<String, Object> resources = MAME_Resources.getInstance().generateListResources(list,
                        MFMPlayLists.getInstance().getPlayList(list));
                startTime = System.nanoTime();
                if (copy) {
                    try {
                        // fixme bad design - move updateDirectories call?
                        MFMSettings.getInstance().updateDirectoriesResourceFiles();
                        MFMFileOps.moveFiles(resources);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    resourcestoFile(list, resources);
                }
                return true;
            }

            @Override
            protected void done() {
                super.done();
                if (copy) {
                    infoPanel.showMessage(action + " completed");
                    infoPanel.updateUI();
                }
                long estimatedTime = (System.nanoTime() - startTime);
                // System.out.println("Estimated time long is : " + estimatedTime);
                MFM.logger.addToList(action + " completed in " + Debug.formatMillis(estimatedTime) +
                        FileUtils.NEWLINE, true);
            }
        };
        Thread copyFilesOps = new Thread(sw);
        copyFilesOps.start();
    }

    static void scanResources() {
        // fixme bad design - move updateDirectories call?
        MFMSettings.getInstance().updateDirectoriesResourceFiles();

        final String action = "Scanning Resources";
        infoPanel.showProgress(action);
        infoPanel.updateUI();
        MFM.logger.addToList(action + " started", true);
        SwingWorker sw = new SwingWorker() {
            long startTime;

            @Override
            protected Object doInBackground() throws Exception {
                startTime = System.nanoTime();
                MAME_Resources.getInstance().scan();
                return true;
            }

            @Override
            protected void done() {
                super.done();
                infoPanel.showMessage(action + " completed");
                infoPanel.updateUI();
                long estimatedTime = (System.nanoTime() - startTime);
                //    System.out.println("Estimated time long is : " + estimatedTime);
                MFM.logger.addToList(action + " completed in " + Debug.formatMillis(estimatedTime) +
                        FileUtils.NEWLINE, true);
            }
        };

        Thread scanResourcesOps = new Thread(sw);
        scanResourcesOps.start();
    }

    static void openListBuilder(String baseList) {
        if (LBdialog != null && LBdialog.isVisible()) {
            LBdialog.toFront();
            return;
        }

        Dimension maxsize = new Dimension(1280, 900);
        Dimension minsize = new Dimension(1180, 750);

        ListBuilderUI LBui;
        JDialog tempLBdialog = null;

        if (baseList.equals(ListBuilderUI.Previous) && Files.exists(Paths.get(MFM.MFM_SETTINGS_DIR + "LBui.ser"))) {
            try {
                tempLBdialog = (JDialog) PersistUtils.loadAnObject(MFM.MFM_SETTINGS_DIR + "LBui.ser");
                ListBuilderUI.getInstance().setController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (tempLBdialog == null) {
            tempLBdialog = new JDialog(mainFrame, "MFM List Builder");
            LBui = ListBuilderUI.getInstance();
            LBui.setController();
            LBui.setState(baseList);

            tempLBdialog.getContentPane().add(LBui.getListBuilderPanel());
            setFontSize(tempLBdialog);

            tempLBdialog.setPreferredSize(maxsize);
            tempLBdialog.setMaximumSize(maxsize);
            tempLBdialog.setMinimumSize(minsize);
        }

        LBdialog = tempLBdialog;
        LBdialog.pack();
        LBdialog.setResizable(true);
        LBdialog.setLocationRelativeTo(mainFrame);

        LBdialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                PersistUtils.saveAnObject(LBdialog, MFM.MFM_SETTINGS_DIR + "LBui.ser");
            }
        });

        LBdialog.setVisible(true);
    }
}
