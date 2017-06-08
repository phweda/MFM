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

import Phweda.MFM.Utils.ParseCommandList;
import Phweda.MFM.mame.Machine;
import Phweda.MFM.mame.Mame;
import Phweda.MFM.mame.ParseAllMachinesInfo;
import Phweda.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/25/11
 * Time: 2:04 PM
 */
public class MAMEInfo // We'll just do the individual objects  ** implements Serializable
{
    private static MAMEInfo ourInstance = null;

    private static final String RUNNABLE_MACHINES = "RunnableMachines";
    private static final String CATEGORIES = "Categories";
    private static final String CATEGORYMACHINES = "CategoryMachines";
    private static final String CATEGORYHIERARCHY = "CategoryHierarchy";
    private static final String CONTROLLERS = "Controllers";
    private static final String CONTROLLERSMACHINES = "ControllerMachines";

    // TODO figure out if we now need this and start to eliminate static calls 3/30/2017
    private static final ParseAllMachinesInfo PAMI = new ParseAllMachinesInfo();

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

    /**
     * Load persisted objects or if not available generate from MAME
     */
    private MAMEInfo(boolean parse) {
        if (MFM.isDebug() && allCategories != null) {
            MFM.logger.addToList("Total categories is : " + allCategories.size());
        }
        try {
            if (parse) {
                MFM_Data.getInstance().reset(); // Does this really help memory? Is GC happening in time to help during parsing?
            } else {
                mame = loadMame();
            }
            if (mame == null || parse) {
                System.out.println("**** Parsing MAME ****");
                // With 0.85 Parse MAME if one is set, or Quit
                MAMEexe.setBaseArgs(MFMSettings.getInstance().fullMAMEexePath());
                generateAllMameData(isALL());
                mame = loadMame();
            }

            loadCaches();
            loadINIs();
            MFMListBuilder.initLists(parse);
            MFM_Data.getInstance().persistStaticData(MFM.MFM_DATA_DIR, true);
        } catch (Exception exc) {
            if (MFM.isDebug()) {
                if (parse) {
                    MFM.logger.addToList("Exception parsing Mame : " +
                            exc.getClass().getCanonicalName());
                } else {
                    MFM.logger.addToList("Exception loading Mame and data objects : " +
                            exc.getClass().getCanonicalName());
                }
                exc.printStackTrace();
                MFM.exit(9);
            }
            // Removed for 0.85 no longer needed
        }
        // loadCommands(); // Legacy functionality

        if (runnableMachines != null) {
            setRunnable(runnableMachines.size());
        } else {
            setRunnable(mame.getMachineMap().size());
        }
        // Parse MAME special case
        MFM_Data.getInstance().setLoaded();
        loadMameResources();
    }

    public static boolean isALL() {
        return MFM.isProcessAll() | MFMSettings.getInstance().isPreMAME143exe();
    }

    public static MAMEInfo getInstance(boolean reset, boolean parse) {
        if (ourInstance == null || reset) {
            ourInstance = new MAMEInfo(parse);
        }
        return ourInstance;
    }

    /**
     * Load the Mame data set from file
     *
     * @return Mame data set
     */
    private static Mame loadMame() {
        return MFM_Data.getInstance().getMame();
    }

    /**
     * Parse the currently set Mame executable
     *
     * @return Mame XML
     * @see Phweda.MFM.MAMEexe
     */
    private static Mame generateMame(boolean all) {
        return ParseAllMachinesInfo.loadAllMachinesInfo(all);
    }

    private void generateAllMameData(boolean all) {
        MFMSettings.getInstance();// Ensure it is loaded. ?? TODO needed?

        StringBuilder message = new StringBuilder("Parsing MAME: ");
        message.append(MFMSettings.getInstance().getMAMEVersion());
        message.append(" - All flag is ");
        message.append(all);

        MFM.logger.addToList(message.toString(), true);
        System.out.println(message.toString());

        loadINIs();
        // NOTE order makes a difference!!
        mame = generateMame(all);
        getParsedData();

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
            MFM.logger.separateLine();
            MFM.logger.addToList(
                    "EXITING on FATAL error. Unable to load Machines information.\n" +
                            "Check your MAME setup and ensure it is running properly", true);
            System.exit(3);
        }
    }

    /**
     * TODO further refactor needed this is messy
     * Extract parsed Runnable list, Categories info, Controllers
     */
    private static void getParsedData() {
        runnableMachines = ParseAllMachinesInfo.getRunnable();
        allCategories = ParseAllMachinesInfo.getCategoriesList();
        if (allCategories != null && !allCategories.isEmpty()) {
            createCatHierarchy(allCategories);
        }
        categoryMachines = ParseAllMachinesInfo.getCategoryGamesList();
        controllers = ParseAllMachinesInfo.getControllers();
    }

    /**
     * Get MAME Command list and descriptions
     * >mame64 -showusage
     */
    private static void loadCommands() {
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
    }

    /**
     * Load cached Mame Resources - User's Mame sets
     */
    private static void loadMameResources() {
        Thread loadMameResources = new Thread() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis();
                if (MFM.isSystemDebug()) {
                    System.out.println("\nMAME Resources load starting: " + new Date(millis));
                }
                // TODO why is this taking so long?
                MAME_Resources.getInstance(); // load and initialize
                if (MFM.isSystemDebug()) {
                    System.out.println("MAME Resources load took: " +
                            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - millis));
                }
            }
        };
        loadMameResources.start();
    }

    private static void loadCaches() {
        INIfiles = (HashMap<String, Map>) MFM_Data.getInstance().getUserInis();
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

    static TreeSet<String> getAllCategories() {
        return new TreeSet<String>(allCategories);
    }

    /**
     * Mame Data Set version
     *
     * @return MFM Data Set version
     * @see MFM_Data::getDataVersion()
     * @deprecated since 0.85 with multiple Data sets
     */
    public static String getVersion() {
        // Bug with this older Mame versions do not have the build Attribute
        //    return MFMSettings.trimMAMEVersion(MFM_Data.getInstance().getMame().getBuild());
        return MFMSettings.getInstance().getDataVersion();
    }

    public static double getVersionDouble() {
        String str = MFMSettings.getInstance().getDataVersion().replaceAll("[^\\d.]", "");
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

/*  TODO
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

    static HashMap<String, ArrayList<String>> getCategoryMachines() {
        return categoryMachines;
    }

    public static Controllers getControllers() {
        return controllers;
    }

    static TreeMap<String, ArrayList<String>> getCategoryHierarchy() {
        return categoryHierarchy;
    }

    public static Machine getMachine(String machineName) {
        if (mame.getMachineMap().get(machineName) == null) {
            System.out.println("MAMEInfo null Machine in getMachine is: " + machineName);
        }
        return mame.getMachineMap().get(machineName);
    }

    public static HashMap<String, Map> getINIfiles() {
        return INIfiles;
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

    /**
     * Parse and store any user <mame root>/folder/*.ini files<br>
     * Excludes Category Version and nPlayer ini files.<br>
     * User's ini files become UI left hand tree
     */
    public static void loadINIs() {
        HashSet<File> files;
        String folderDir = MFMSettings.getInstance().getMAMEFoldersDir();
        if (folderDir != null) {
            File folder = new File(folderDir);
            // Quit if it is not a directory
            if (folder.isDirectory()) {
                files = new HashSet<File>(Arrays.asList(folder.listFiles(FileUtils.iniFilenameFilter)));
                if (MFM.isSystemDebug()) {
                    System.out.println("Folders files are : " + files.toString());
                }

                if (!files.isEmpty()) {
                    INIfiles = ParseAllMachinesInfo.INIfiles(files);
                    // Persist it
                    MFM_Data.getInstance().persistUserInis(INIfiles);
                }
            }
        }
    }

}
