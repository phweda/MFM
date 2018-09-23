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
import Phweda.MFM.Utils.MFMFileOps;
import Phweda.MFM.Utils.MFM_DATmaker;
import Phweda.MFM.datafile.Datafile;
import Phweda.MFM.mame.Machine;
import Phweda.utils.Debug;
import Phweda.utils.FileUtils;
import Phweda.utils.PersistUtils;
import Phweda.utils.XMLUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import static Phweda.MFM.MFMListBuilder.createPlayList;
import static Phweda.MFM.MFM_Constants.SPACE_CHAR;
import static Phweda.utils.FileUtils.stripSuffix;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/5/2016
 * Time: 11:24 AM
 */
@SuppressWarnings("RedundantThrows")
final class MFMListActions {
    private static final Object lock = new Object(); // private final lock object
    private static MFMController controller;
    private static JDialog listbuilderDialog;
    private static final String LBUI_SER = "LBui.ser";

    private MFMListActions() { // Cover implicit public constructor
    }

    public static void setController(MFMController controller) {
        MFMListActions.controller = controller;
    }

    static String pickList(boolean all, String message) {
        Object[] data;
        if (all) {
            data = MFMPlayLists.getInstance().playListNames();
        } else {
            data = MFMPlayLists.getInstance().myPlayListNames();
        }

        @SuppressWarnings("MagicConstant") final String list = (String) JOptionPane.showInputDialog(
                controller.getFrame(), message,
                "List Picker", JOptionPane.OK_CANCEL_OPTION, MFMUI_Setup.getMFMIcon(), data, data[0]);

        return list;
    }

    /**
     * Add selected to list or create a new list with this item
     *
     * @param item selected item to add to list
     */
    static void addtoList(String item) {
        MFMPlayLists playLists = MFMPlayLists.getInstance();
        Object[] objs = Stream.concat(
                Arrays.stream(new Object[]{MFM_Constants.NEW_LIST}), Arrays.stream(playLists.myPlayListNames()))
                .toArray(Object[]::new);

        JComboBox jcb = new JComboBox(objs);

        // Hack so we can position Dialog where mouse is. Do not want to subclass JOptionPane
        JFrame frame = new JFrame();
        frame.setSize(0, 0);
        frame.setLocation(MFMController.getFrame().getMousePosition());
        frame.setVisible(true); // frame must be visible

        // now prompt for the list
        JOptionPane.showMessageDialog(frame, jcb);
        frame.dispose();  // get rid of it

        String listName = Objects.requireNonNull(jcb.getSelectedItem()).toString();
        if (listName.equals(MFM_Constants.NEW_LIST)) {
            String newName = JOptionPane.showInputDialog(null, "Enter List Name");
            if ((newName == null) || newName.isEmpty()) {
                return;
            }
            MFMPlayLists.getInstance().createPlayList(newName, new String[]{item});
            MFMUI_Setup.getInstance().updateMenuBar(newName);
            return;
        }
        playLists.addMachineToPlayList(listName, item);
    }

    static void removefromList(String item, String listName) {
        MFMPlayLists.getInstance().removeMachineFromPlayList(listName, item);
        int row = MFMController.getMachineListTable().getSelectedRow();

        MachineListTableModel gltm = (MachineListTableModel) MFMController.getMachineListTable().getModel();
        gltm.setData(MFMPlayLists.getInstance().getPlayList(listName), listName);
        gltm.fireTableDataChanged();
        MFMController.getMachineListTable().getSelectionModel().setSelectionInterval(row, row);
        MFMController.showListInfo(listName);
    }

    static String removeList() {
        Object[] data = MFMPlayLists.getInstance().myPlayListNames();

        @SuppressWarnings("MagicConstant") final String result = (String) JOptionPane.showInputDialog(
                MFMController.getFrame(), "Select list to Remove",
                "Remove", JOptionPane.OK_CANCEL_OPTION, null, data, data[0]);

        if (result != null) {
            MFM.getLogger().addToList(result + " is being removed", true);
            MFMPlayLists.getInstance().removePlayList(result);
            MFMUI_Setup.getInstance().updateMenuBar("");
        }
        return result;
    }

    static void showListEditor() {
        Dialog listEditorDialog = new JDialog(MFMController.getFrame(), MFMAction.LIST_EDITOR);
        listEditorDialog.add(ListEditor.getInstance().$$$getRootComponent$$$());
        listEditorDialog.pack();
        listEditorDialog.setLocationRelativeTo(MFMController.getFrame());
        listEditorDialog.setVisible(true);
    }

    static void listtoFile() {
        final String listName = pickList(true, "Select list to Save");

        if (listName != null) {
            MFM.getLogger().addToList(listName + " is being saved to file", true);
            SortedSet<String> playList = MFMPlayLists.getInstance().getPlayList(listName);
            File listFile = new File(MFM.getMfmListsDir() + listName + SPACE_CHAR +
                    MFM_Data.getInstance().getDataVersion() + ".txt");
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(listFile),
                    StandardCharsets.UTF_8))) {
                for (String machine : playList) {
                    pw.println(machine);
                }
            } catch (FileNotFoundException e) {
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

    static void listtoDAT() {
        final String listName = pickList(true, "Select list for DAT");
        if (listName != null) {
            MFM.getLogger().addToList("DAT for " + listName + " is being created and saved to file", true);
            SortedSet<String> list = MFMPlayLists.getInstance().getPlayList(listName);
            try {
                Datafile datafile = MFM_DATmaker.generateDAT(listName, list);
                PersistUtils.saveDATtoFile(datafile, MFM.getMfmListsDir() + listName +
                        '(' + MFM_Data.getInstance().getDataVersion() + ").dat");
            } catch (ParserConfigurationException | TransformerException | JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    static String datafiletoList() {
        File inputDATfile = pickValidateDAT();
        if (inputDATfile != null) {
            Datafile datafile = (Datafile) PersistUtils.retrieveJAXB(inputDATfile.getAbsolutePath(), Datafile.class);
            MFMListBuilder.createListfromDAT(inputDATfile.getName(), datafile);
            return inputDATfile.getName();
        }
        return "";
    }

    static File pickValidateDAT() {
        File file = pickDAT();
        if ((file != null) && validateDAT(file)) {
            return file;
        }
        return null;
    }

    static boolean validateXML(File file) {
        if (file != null) {
            return XMLUtils.validate(file);
        }
        return false;
    }

    private static boolean validateDAT(File inputFile) {
        String result = new MFM_DATmaker().validateDAT(inputFile);
        if (!result.equalsIgnoreCase(MFM_DATmaker.GOOD)) {
            JOptionPane.showMessageDialog(MFMController.getFrame(), "DAT file is invalid" +
                    FileUtils.NEWLINE + result, "Invalid DAT File", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            JOptionPane.showMessageDialog(MFMController.getFrame(), inputFile.getName() + " is a valid DAT file.");
        }
        return true;
    }


    static void filterDATbyList() {
        String list = pickList(true, "Select filter List");
        File inputDATfile = pickDAT();
        if (inputDATfile != null) {
            Datafile inputDAT = (Datafile) PersistUtils.retrieveJAXB(inputDATfile.getPath(), Datafile.class);
            if (inputDAT != null) {
                saveFilteredDAT(inputDAT, MFMPlayLists.getInstance().getPlayList(list), inputDATfile.getName() +
                        "-" + list);
            }
        }
    }

    static void filterDATbyExternalList() {
        File externalList = pickListFile();
        if (externalList != null) {
            File inputfile = pickDAT();
            if (inputfile == null) {
                return;
            }
            Datafile inputDAT = (Datafile) PersistUtils.retrieveJAXB(inputfile.getPath(), Datafile.class);
            if (inputDAT != null && inputfile.exists()) {
                Set<String> list = FileUtils.listFromFile(externalList);
                saveFilteredDAT(inputDAT, list, inputfile.getName() + "-" + externalList.getName());
            }
        }
    }

    private static void saveFilteredDAT(Datafile inputDAT, Set<String> list, String name) {
        try {
            PersistUtils.saveDATtoFile(filterDAT(inputDAT, list), MFM.getMfmListsDir() + name + "-filtered.xml");
        } catch (ParserConfigurationException |
                TransformerException | JAXBException e) {
            e.printStackTrace();
        }
    }

    private static Datafile filterDAT(Datafile inputDAT, Set<String> list) {
        return MFM_DATmaker.filterDATbyList(inputDAT, list);
    }

    private static File pickDAT() {
        JFileChooser fileChooser = new JFileChooser(MFM.getMfmDir());
        fileChooser.setPreferredSize(new Dimension(640, 480));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Select DAT File");

        int returnValue = fileChooser.showDialog(controller.getFrame(), "OK");
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    static void listDataToCSV(String list) {
        File newFile = new File(MFM.getMfmListsDir() + list +
                MFM_Data.getInstance().getDataVersion() + "_data.csv");
        SortedSet<String> machines = MFMPlayLists.getInstance().getPlayList(list);
        try (PrintWriter pw = new PrintWriter(newFile)) {
            pw.println(Machine.CSV_HEADER);
            for (String machine : machines) {
                if (MAMEInfo.getMachine(machine) != null) {
                    pw.println(MAMEInfo.getMachine(machine).toString());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static String importList(Container container) {
        JFileChooser fileChooser = new JFileChooser(MFM.getMfmListsDir());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.showDialog(null, JFileChooser.APPROVE_SELECTION);
        File file = fileChooser.getSelectedFile();

        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ((lines == null) || lines.isEmpty()) {
            MFM.getLogger().out("List to import was empty! File was " + file.getName());
            return null;
        }

        String fileName = stripSuffix(file.getName());
        boolean ok = checkListName(fileName, container);
        if (!ok) {
            return null;
        }

        String[] machines = lines.toArray(new String[0]);
        createPlayList(fileName, machines);
        return fileName;
    }

    private static boolean checkListName(String name, Component comp) {
        if (MFMPlayLists.getInstance().getALLPlayListsTree().containsKey(name)) {
            if (MFMPlayLists.getInstance().getMyPlayListsTree().containsKey(name)) {
                int result = JOptionPane.showConfirmDialog(comp,
                        "That list name already exists. Overwrite it?", "", JOptionPane.YES_NO_OPTION);
                return result == JOptionPane.NO_OPTION;
            } else {
                JOptionPane.showMessageDialog(comp, "Please rename that is a reserved list name.");
                return false;
            }
        }
        return true;
    }

    private static File pickListFile() {
        JFileChooser fileChooser = new JFileChooser(MFM.getMfmDir());
        fileChooser.setPreferredSize(new Dimension(640, 480));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Select filter List File");

        int returnValue = fileChooser.showDialog(controller.getFrame(), "OK");
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static void resourcestoFile(String list, SortedMap<String, Object> files) {
        MFM.getLogger().addToList(list + " resources are being saved to file", true);
        File listFile = new File(MFM.getMfmListsDir() + list + " " +
                MFM_Data.getInstance().getDataVersion() + "_Resources.txt");
        int counter = 1;
        try (final PrintWriter pw = new PrintWriter(new FileWriter(listFile))) {
            String format = "%,7d: ";
            Iterable<File> romsList = (Iterable<File>) files.get(MFM_Constants.ROMS);
            for (File file : romsList) {
                String path = file.getAbsolutePath();
                pw.format(format, counter++);
                pw.println(path);
            }
            pw.flush();

            Iterable<File> chdsList = (TreeSet<File>) files.get(MFM_Constants.CHDS);
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
        int open = JOptionPane.showConfirmDialog(controller.getFrame(), "Open List Resources?",
                "List", JOptionPane.OK_CANCEL_OPTION);
        if (open == JOptionPane.OK_OPTION) {
            FileUtils.openFileFromOS(Paths.get(listFile.getAbsolutePath()));
        }

    }

    /**
     * Transform Machine data to JSON
     *
     * @param list to create JSON for
     */
    static void listDataToJSON(String list) {

        File newFile = new File(MFM.getMfmListsDir() + list + "_" +
                MFM_Data.getInstance().getDataVersion() + "_data.json");
        final SortedSet<String> playList = MFMPlayLists.getInstance().getPlayList(list);
        final ObjectMapper objectMapper = new ObjectMapper();

        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                synchronized (lock) {
                    try {
                        final PrintWriter pw = new PrintWriter(newFile);
                        playList.forEach(machine -> {
                            // Convert Machine to JSON string and pretty print
                            String jsonInString = null;
                            try {
                                jsonInString = objectMapper.writerWithDefaultPrettyPrinter()
                                        .writeValueAsString(MAMEInfo.getMame().getMachineMap().get(machine));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            pw.println(jsonInString);
                            if (MFM.isSystemDebug()) {
                                System.out.println(jsonInString);
                            }
                        });
                        pw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                controller.showMessage(newFile.getName() + " is in the Lists directory.");
            }
        };
        Thread generateJSON = new Thread(sw);
        generateJSON.start();
        controller.getInformationPanel().showProgress("Generating JSON for " + list);
    }

    /**
     * Scan all resource roots for resources for this list.
     * Log and report results
     *
     * @param copy If true copy the files else just save results to file
     */
    static void copyResources(final boolean copy) {
        if (!MAME_Resources.getInstance().hasCache()) {
            int returnValue = JOptionPane.showConfirmDialog(controller.getFrame(), "Resource scan must be run first. Run now?",
                    "", JOptionPane.YES_NO_OPTION);
            if (returnValue == JOptionPane.YES_OPTION) {
                scanResources();
                return;
            } else {
                return;
            }
        }

        final String list = pickList(true, "Choose List");
        if (list == null) {
            return;
        }
        String message;
        if (copy) {
            message = "Copying Resources " + list;
        } else {
            message = "Generating Resources for " + list;
        }
        final String action = message;
        if (copy) {
            controller.getInformationPanel().showProgress(action);
        }
        MFM.getLogger().addToList(action + " started", true);
        SwingWorker sw = new SwingWorker() {
            long startTime;

            @Override
            protected Object doInBackground() throws Exception {
                // Resources are File obj except zipped extras which are String
                SortedMap<String, Object> resources = MAME_Resources.getInstance().generateListResources(list,
                        MFMPlayLists.getInstance().getPlayList(list));
                startTime = System.nanoTime();
                if (copy) {
                    // fixme bad design - move updateDirectories call?
                    MFMSettings.getInstance().updateDirectoriesResourceFiles();
                    MFMFileOps.moveFiles(resources);
                } else {
                    resourcestoFile(list, resources);
                }
                return true;
            }

            @Override
            protected void done() {
                super.done();
                if (copy) {
                    controller.getInformationPanel().showMessage(action + " completed");
                }
                long estimatedTime = (System.nanoTime() - startTime);
                // System.out.println("Estimated time long is : " + estimatedTime);
                MFM.getLogger().addToList(action + " completed in " + Debug.formatMillis(estimatedTime) +
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
        controller.getInformationPanel().showProgress(action);
        MFM.getLogger().addToList(action + " started", true);
        SwingWorker sw = new SwingWorker() {
            long startTime;

            @Override
            protected Object doInBackground() throws Exception {
                startTime = System.nanoTime();
                MAME_Resources.scan();
                return true;
            }

            @Override
            protected void done() {
                super.done();
                controller.getInformationPanel().showMessage(action + " completed");
                long estimatedTime = (System.nanoTime() - startTime);
                MFM.getLogger().addToList(action + " completed in " + Debug.formatMillis(estimatedTime) +
                        FileUtils.NEWLINE, true);
            }
        };

        Thread scanResourcesOps = new Thread(sw);
        scanResourcesOps.start();
    }

    static void openListBuilder(String baseList) {
        if (listbuilderDialog != null && listbuilderDialog.isVisible()) {
            listbuilderDialog.toFront();
            return;
        }

        Dimension maxsize = new Dimension(1280, 900);
        Dimension minsize = new Dimension(1180, 750);

        ListBuilderUI listBuilderUI;
        JDialog tempLBdialog = null;

        if (baseList.equals(ListBuilderUI.PREVIOUS) &&
                Paths.get(MFM.getMfmSettingsDir() + LBUI_SER).toFile().exists()) {
            try {
                tempLBdialog = (JDialog) PersistUtils.loadAnObject(MFM.getMfmSettingsDir() + LBUI_SER);
                ListBuilderUI.setController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (tempLBdialog == null) {
            tempLBdialog = new JDialog(controller.getFrame(), "MFM List Builder");
            listBuilderUI = ListBuilderUI.getInstance();
            ListBuilderUI.setController();
            listBuilderUI.setState(baseList);

            tempLBdialog.getContentPane().add(listBuilderUI.getListBuilderPanel());
            controller.setFontSize(tempLBdialog);

            tempLBdialog.setPreferredSize(maxsize);
            tempLBdialog.setMaximumSize(maxsize);
            tempLBdialog.setMinimumSize(minsize);
        }

        listbuilderDialog = tempLBdialog;
        listbuilderDialog.pack();
        listbuilderDialog.setResizable(true);
        listbuilderDialog.setLocationRelativeTo(controller.getFrame());

        listbuilderDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                PersistUtils.saveAnObject(listbuilderDialog, MFM.getMfmSettingsDir() + LBUI_SER);
            }
        });

        listbuilderDialog.setVisible(true);
    }
}
