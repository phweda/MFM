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

package Phweda.MFM;

import Phweda.MFM.UI.MAMEtoJTree;
import Phweda.MFM.Utils.ParseCommandList;
import Phweda.MFM.mame.Machine;
import Phweda.MFM.mame.Mame;
import Phweda.MFM.mame.ParseAllMachinesInfo;
import Phweda.utils.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.text.DecimalFormat;
import java.util.*;

import static javax.swing.JOptionPane.YES_NO_OPTION;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/25/11
 * Time: 2:04 PM
 */
public class MAMEInfo // We'll just do the individual objects  ** implements Serializable
{
    public static final String MAME_INI = "mame.ini";
    private static final String MachineObjectMap = "Machines";
    private static final String MAME = "MAME";
    private static final String RUNNABLE_MACHINES = "RunnableMachines";
    private static final String MACHINELIST = "MachineList";
    private static final String COMMANDS_FILENAME = "Commands";
    private static final String INIFILES = "INIfiles";
    private static final String CATEGORIES = "Categories";
    private static final String CATEGORYMACHINES = "CategoryMachines";
    private static final String CATEGORYHIERARCHY = "CategoryHierarchy";
    private static final String CONTROLLERS = "Controllers";
    private static final String CONTROLLERSMACHINES = "ControllerMachines";
    private static final String ALLROOTS = "CategoryRoots";
    private static final String ARCADEROOTS = "ArcadeCategoryRoots";
    private static final String MAME_VERSION = "MAME Version";
    private static final String NUM_BUTTONS = "Max_buttons";
    private static final String NUM_PLAYERS = "Max players";
    private static final String RUNNABLE = "Runnable";
    private static final MAMEtoJTree mameJTreePanel;
    private static String version;  // From -listxml
    //    private static TreeMap machineList; // extracted From allMachinesObjectMap
    private static int runnable;
    private static HashMap<String, HashMap<String, String>> commands; // From -showusage
    private static ArrayList<String> allCategories;  // From catver_full.ini or catver.ini
    private static HashMap<String, ArrayList<String>> categoryMachines; // From catver_full.ini or catver.ini
    private static TreeMap<String, ArrayList<String>> categoryHierarchy; // MFM generated for root allCategories
    private static Controllers controllers;
    // Decided to just pre-populate these should really do a doublecheck
    private static String[] numButtons = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9+"};
    // Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9+"));
    private static TreeSet<Integer> numPlayers = new TreeSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 8));

    // INIfiles containing: Catver, catlist, custom(GoldenAge), favorites, Genre, Mature, Multimonitor,version
    // - persisted as XML is ~5MB
    private static HashMap<String, Map> INIfiles = new HashMap<String, Map>(); // Parsed from MAME INIfiles directory

    // Added 10/14/15 to differentiate not runnable for those who pull in all
    private static TreeSet<String> runnableMachines;

    // NOTE serialized object is more efficient see below. We'll use that format for the huge objects
    // NOTE the others we'll put as XML for readability outside the application although XML is slower than .ser
    // NOTE : We only load Playable games unless -all flag is passed in
    // NOTE with addition of ALL MAME data 10/2016 we needed to revert to XML via JAXB
//    private static transient Map<String, Machine> allMachinesObjectMap;  // From -listxml now all Machines 10/14/15
    private static transient Mame mame;  // From -listxml now all Machines 10/14/15

    /* We should test the above for size and speed
    *  Is it faster to load by a serialized object or by running -listxml ?
    *  Initial test 12/04/11 shows serialized object loads at least 10 times faster
    *  If serializing is significantly faster what is the size of the file?
    *  ~17MB for MAME 144 - perhaps 35-40MB if we assumed ALL current games become playable
    *  19,640 KB for MAME 151    8,471 playable
    *  20,272 KB for MAME 154    8,841
    *  20,255 KB for MAME 155    8,921
    *  20,416 KB for MAME 156    8,959
    *  ==============================
    *  ADDED MAMEInfo DEC 2014
    *  ****************
    *  27,994 KB for MAME 156    8,959
    *  28,009 KB for MAME 157    8,982
    *  27,980 KB for MAME 158    8,997
    *  27,982 KB for MAME 159    9,007
    *  27,995 KB for MAME 160    9,047
    *  27,787 KB for MAME 161    9,075
    *  28,703 KB for MAME 162   10,258
    *  28,891 KB for MAME 163   10,313
    *  29,368 KB for MAME 164   10,352  Size a little skewed run with a newer MFM version
    *  29,434 KB for MAME 165   10,383
    *  ==============================
    *  ADDED MESSInfo, SYSINFO & with flag non-runnable systems OCT 2015
    *  ****************
    *  43,060 KB for MAME 166   10,494      70,663 KB for MAME 166   32,543 - 22,049 non-runnable
    *
    *
    *
    *
    *
    */

    /*
         * Load persisted objects or if not available generate from MAME
         * TODO break this up
         */
    static {
        try {
            mame = MFM_Data.getInstance().getMame();
            INIfiles = (HashMap<String, Map>) MFM_Data.getInstance().getStaticData(INIFILES);
            if (mame != null) {
                runnableMachines = (TreeSet<String>) MFM_Data.getInstance().getStaticData(RUNNABLE_MACHINES);
                allCategories = (ArrayList<String>) MFM_Data.getInstance().getStaticData(CATEGORIES);
                categoryMachines = (HashMap<String, ArrayList<String>>) MFM_Data.getInstance().
                        getStaticData(CATEGORYMACHINES);
                categoryHierarchy = (TreeMap<String, ArrayList<String>>)
                        MFM_Data.getInstance().getStaticData(CATEGORYHIERARCHY);

                controllers = Controllers.getInstance();
                Controllers.setControls((TreeMap<Integer, Phweda.MFM.mame.Control>)
                        MFM_Data.getInstance().getStaticData(CONTROLLERS));

                Controllers.setControlMachinesList((TreeMap<Integer, TreeSet<String>>)
                        MFM_Data.getInstance().getStaticData(CONTROLLERSMACHINES));
            }

        } catch (Exception exc) {
            if (MFM.isDebug()) {
                MFM.logger.addToList("Exception loading MachineObjectMap : " + exc.getClass().getCanonicalName());
            }
            if (exc instanceof InvalidClassException) {
                if (exc.getMessage().contains("Phweda.MFM.Machine")) {
                    int answer = JOptionPane.showConfirmDialog(null, "Detected an old version of MFM cache file" +
                                    "\nMFM will reparse MAME information please be patient",
                            "Reload MAME Information",
                            YES_NO_OPTION);
                    if (answer == JOptionPane.NO_OPTION) {
                        MFM.logger.separateLine();
                        MFM.logger.addToList("User chose to exit on an upgrade MAME reload.");
                        System.exit(2);
                    }
                }
            }
        }

        // Note if it is still null could also be we changed Machine/Game class
        if (mame == null) {
            // TODO right place to start? End it in MFMUI_Setup getFrame
            MFM.showBusy(true, false);
            // START with INI files used in parsing Games 9/20/15
            // NOTE now switched where we do catver and nplayers 10/14/15
            loadINIs();
            // NOTE order makes a difference!!
            mame = ParseAllMachinesInfo.loadAllMachinesInfo();

            runnableMachines = ParseAllMachinesInfo.getRunnable();

            allCategories = ParseAllMachinesInfo.getCategoriesList();
            if (allCategories != null && !allCategories.isEmpty()) {
                createCatHierarchy(allCategories);
            }
            categoryMachines = ParseAllMachinesInfo.getCategoryGamesList();
            controllers = ParseAllMachinesInfo.getControllers();

            if (mame != null) {
                // Persist it
                MFM_Data.getInstance().setMame(mame);
                MFM_Data.getInstance().setStaticData(RUNNABLE_MACHINES, runnableMachines);
                MFM_Data.getInstance().setStaticData(CATEGORIES, allCategories);
                MFM_Data.getInstance().setStaticData(CATEGORYHIERARCHY, categoryHierarchy);

                if (controllers != null) {
                    MFM_Data.getInstance().setStaticData(CONTROLLERS, controllers.getControls());
                    MFM_Data.getInstance().setStaticData(CONTROLLERSMACHINES, controllers.getControlMachinesList());
                }

                if (categoryMachines != null) {
                    MFM_Data.getInstance().setStaticData(CATEGORYMACHINES, categoryMachines);
                }

            } else {
                //TODo how do we punt? Something's really wrong if we get here!!!
                MFM.logger.separateLine();
                MFM.logger.addToList(
                        "EXITING on FATAL error. Unable to load games Information.\n" +
                                "Check your MAME setup and ensure it is running properly", true);
                System.exit(3);
            }
        }

        if (INIfiles == null) {
            loadINIs();
        }

        /*
         * Get MAME Command list and descriptions
         * >mame64 -showusage
         */
        commands = (HashMap<String, HashMap<String, String>>) MFM_Data.getInstance().getStaticData(MFM_Constants.COMMANDS);

        if (commands == null || commands.isEmpty()) {
            commands = new HashMap<String, HashMap<String, String>>(75, 0.5f);
            // Get it
            ArrayList<String> fullListargs = new ArrayList<String>();
            fullListargs.add("-showusage");

            // TODO in memory only File via Path from Files.createtempfile
            File showusageFile = new File(MFM.MFM_SETTINGS_DIR + "commands.txt");
            try {
                // Added to overwrite if this text file previously existed
                showusageFile.createNewFile();
                Process process = MAMEexe.run(fullListargs, showusageFile);
                process.waitFor();
            } catch (MAMEexe.MAME_Exception e) {
                e.printStackTrace(MFM.logger.Writer());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            // TODO Refactor we're passing in using then resetting commands Hashmap to itself
            // TODO DEBUG
            ParseCommandList pcl = new ParseCommandList(MFM.MFM_SETTINGS_DIR + "commands.txt", commands);
            try {
                //noinspection unchecked
                commands = (HashMap<String, HashMap<String, String>>) pcl.processFile();
                // Persist it
                MFM_Data.getInstance().setStaticData(MFM_Constants.COMMANDS, commands);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            showusageFile.delete();
        }

        if (runnableMachines != null) {
            setRunnable(runnableMachines.size());
        } else {
            setRunnable(mame.getMachineMap().size());
        }

        mameJTreePanel = MAMEtoJTree.getInstance();
    }

    public MAMEInfo() {
        if (MFM.isDebug() && allCategories != null) {
            MFM.logger.addToList("Total categories is : " + allCategories.size());
        }
    }

    static TreeSet<String> getAllCategories() {
        return new TreeSet<String>(allCategories);
    }

    /**
     * Mame Exe version
     *
     * @return MFMUser's exe version
     */
    public static String getVersion() {
        // Bug with this older Mame versions do not have the build Attribute
        //    return MFMSettings.trimMAMEVersion(MFM_Data.getInstance().getMame().getBuild());
        return MFMSettings.getDataVersion();
    }

/*  Unused probably never needed
    public static void setVersion(String version) {
        MFMSettings.setDataVersion(MFMSettings.trimMAMEVersion(version));
    }
*/

    public static double getVersionDouble() {
        String str = MFMSettings.getDataVersion().replaceAll("[^\\d.]", "");
        return Double.parseDouble(str);
    }

    public static Integer[] getNumPlayers() {
        return numPlayers.toArray(new Integer[numPlayers.size()]);
    }

    // Future when we read live
    public static void setNumPlayers(TreeSet<Integer> set) {
        numPlayers = set;
    }

    public static String[] getNumButtons() {
        return numButtons;
    }

/*
    public static TreeMap getMachineList() {
        return machineList;
    }

    public static void setMachineList(TreeMap gameListMap) {
        MAMEInfo.machineList = gameListMap;
    }
*/

    // Future when we read live
    public static void setNumButtons(String[] buttons) {
        numButtons = buttons;
    }

    public static HashMap Commands() {
        return MAMEInfo.commands;
    }

    public static void Commands(HashMap<String, HashMap<String, String>> commands) {
        MAMEInfo.commands = commands;
    }

    /*   public static Map<String, Machine> getAllMachinesInfo() {
           return allMachinesObjectMap;
       }
    */
    public static Mame getMame() {
        return mame;
    }

    public static TreeSet<String> getRunnableMachines() {
        return runnableMachines;
    }

    public static String getRunnable() {
        return new DecimalFormat("###,###").format(runnable);
    }

    public static void setRunnable(int runnableIn) {
        runnable = runnableIn;
    }

/*
    public static ArrayList<String> getArcadeCategoryRoots() {
        return arcadeCategoryRoots;
    }

    public static ArrayList<String> getAllCategoryRoots() {
        return categoryRoots;
    }
*/

    public static HashMap<String, ArrayList<String>> getCategoryMachines() {
        return categoryMachines;
    }

    public static Controllers getControllers() {
        return controllers;
    }

    static TreeMap<String, ArrayList<String>> getCategoryHierarchy() {
        return categoryHierarchy;
    }

    public static Machine getMachine(String machineName) {
        return mame.getMachineMap().get(machineName);
    }

    public static HashMap<String, Map> getINIfiles() {
        return INIfiles;
    }

    public static MAMEtoJTree getMameJTreePanel() {
        return mameJTreePanel;
    }

    private static void createCatHierarchy(ArrayList<String> categories) {
        if (MFM.isDebug()) {
            MFM.logger.addToList("Categories count is : " + categories.size());
        }
        categoryHierarchy = new TreeMap<String, ArrayList<String>>();
        for (String entry : categories) {
            if (MFM.isDebug()) {
                MFM.logger.addToList("Entry is : " + entry);
            }
            if (entry.contains("/")) {
                String key = entry.substring(0, entry.indexOf(' '));
                int index = entry.indexOf("/") + 1;
                // String subKey = entry.substring(index);
                if (MFM.isDebug()) {
                    MFM.logger.addToList("Key : " + key + "\tSub category : " + entry);
                }
                if (categoryHierarchy.containsKey(key)) {
                    // Shouldn't need but guess there could be duplicates
                    if (!categoryHierarchy.get(key).contains(entry)) {
                        categoryHierarchy.get(key).add(entry);
                    }
                } else {
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(entry);
                    categoryHierarchy.put(key, list);
                }
            } else {
                // If we haven't previously added it
                if (!categoryHierarchy.containsKey(entry)) {
                    categoryHierarchy.put(entry, new ArrayList<String>());
                }
            }
        }
    }

    public static void loadINIs() {
        HashSet<File> files;
        String folderDir = MFMSettings.getMAMEFoldersDir();
        if (folderDir != null) {
            File folder = new File(folderDir);
            // Quit if it is not a directory
            if (folder.isDirectory()) {
                files = new HashSet<File>(Arrays.asList(folder.listFiles(FileUtils.iniFilenameFilter)));
                if (MFM.isSystemDebug()) {
                    System.out.println("Folders files are : " + files.toString());
                }
                INIfiles = ParseAllMachinesInfo.INIfiles(files);
                // Persist it
                MFM_Data.getInstance().setStaticData(INIFILES, INIfiles);
                MFM_Data.persistStaticData();
            }
        }
    }

}
