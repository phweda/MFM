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

package Phweda.MFM;

import Phweda.MFM.Utils.ParseCommandList;
import Phweda.MFM.mame.Machine;
import Phweda.MFM.mame.Mame;
import Phweda.MFM.mame.ParseMAMElistInfo;
import Phweda.MFM.mame.ParseMAMElistXML;
import Phweda.MFM.mame.softwarelist.Software;
import Phweda.MFM.mame.softwarelist.Softwarelist;
import Phweda.MFM.mame.softwarelist.Softwarelists;
import Phweda.utils.FileUtils;
import Phweda.utils.PersistUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static Phweda.MFM.MFMListBuilder.CATEGORY_LISTS_HASHMAP;
import static Phweda.utils.FileUtils.COMMA;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/25/11
 * Time: 2:04 PM
 */
public final class MAMEInfo {
    private static MAMEInfo ourInstance = null;

    private static final String RUNNABLE_MACHINES = "RunnableMachines";
    private static final String CATEGORIES = "Categories";
    private static final String CATEGORYMACHINES = "CategoryMachines";
    private static final String CATEGORYHIERARCHY = "CategoryHierarchy";
    private static final String CONTROLLERSMACHINES = "ControllerMachines";

    private static int runnable;
    private static HashMap<String, HashMap<String, String>> commands; // From -showusage LEGACY
    private static List<String> allCategories;  // From catver.ini
    private static Map<String, ArrayList<String>> categoryMachinesMap; // From catver.ini
    private static Map<String, ArrayList<String>> categoryHierarchyMap; // MFM generated for root allCategories
    private static MachineControllers machineControllers;
    // Decided to just pre-populate these should really do a doublecheck
    private static String[] numButtons = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9+"};
    private static Set<Integer> numPlayers = new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 8));

    // inifiles containing: Catver, catlist, custom(GoldenAge), favorites, Genre, Mature, Multimonitor,version
    private static Map<String, Map<String, String>> inifiles = new HashMap<>(10); // Parsed from MAME inifiles directory
    private static Set<String> runnableMachines;

    private static Map<String, ArrayList<String>> categoryListsMap;

    // NOTE with addition of ALL MAME data 10/2016 reverted to XML via JAXB
    private static Mame mame;  // From -listxml now all Machines 10/14/15

    private static Softwarelists softwareLists = null;
    private static boolean processAll = false; //hold the current state selected by user
    private static boolean parsing = false; // Added for first run Parsing with no Data Sets

    /**
     * Load persisted objects or if not available generate from MAME
     */
    private MAMEInfo(boolean parse, boolean all, boolean isListInfo) {
        parsing = parse;
        processAll = all;
        if (MFM.isDebug() && (allCategories != null)) {
            MFM.getLogger().addToList("Total categories is : " + allCategories.size());
        }
        try {
            if (parse) {
                MFM_Data.getInstance().reset(); // Does this really help memory? Is GC happening in time to help during parsing?
                categoryListsMap = null; // Force reload of <MFM Root>/Category/CategoryListsMap.xml
            } else {
                mame = loadMame();
                softwareLists = loadSoftwareLists();
                // Shouldn't happen but just in case
                if (mame == null) {
                    MFM.exit(9);
                }
            }
            if (parse && !isListInfo) {
                System.out.println("**** Parsing MAME ****");
                // With 0.85 Parse MAME if one is set, or Quit
                MAMEexe.setBaseArgs(MFMSettings.getInstance().fullMAMEexePath());
                boolean success = generateAllMameData(isListInfo);
                if (!success) {
                    JOptionPane.showMessageDialog(null, "MAME Parsing failed check the error log"
                            , "Parsing Failed!!", JOptionPane.ERROR_MESSAGE);
                    return; // Try to recover to previous state
                }
                mame = loadMame();
                softwareLists = loadSoftwareLists();
            } else if (isListInfo) {
                System.out.println("**** Parsing MAME listInfo file ****");
                boolean success = generateAllMameData(isListInfo);
            }

            loadCaches();
            loadINIs();
            loadCategoriesMap();
            MFMListBuilder.initLists(parse);
            MFM_Data.getInstance().persistStaticData(MFM.getMfmDataDir(), true);
        } catch (HeadlessException e) {
            e.printStackTrace();
        } catch (RuntimeException exc) {
            if (MFM.isDebug()) {
                if (parse) {
                    MFM.getLogger().addToList("Exception parsing Mame : " +
                            exc.getClass().getCanonicalName(), true);
                } else {
                    MFM.getLogger().addToList("Exception loading Mame and data objects : " +
                            exc.getClass().getCanonicalName(), true);
                }
                exc.printStackTrace();
                MFM.exit(9);
            }
        }
        if (runnableMachines != null) {
            setRunnable(runnableMachines.size());
        } else {
            setRunnable(mame.getMachineMap().size());
        }
        // Parse MAME special case
        MFM_Data.getInstance().setLoaded();
        loadMameResources();
        parsing = false;
    }

    public static MAMEInfo getInstance(boolean reset, boolean parse, boolean all) {
        if (MFM.isSystemDebug()) {
            System.out.println("MAMEInfo getInstance " + reset + " : " + parse);
        }
        if ((ourInstance == null) || reset) {
            ourInstance = new MAMEInfo(parse, all, false);
        }
        return ourInstance;
    }

    public static void parseListInfo() {
        ourInstance = new MAMEInfo(true, true, true);
    }

    public static boolean isParsing() {
        return parsing;
    }

    static boolean isProcessAll() {
        return processAll;
    }

    /**
     * Load the Mame data set from file
     *
     * @return Mame data set
     */
    private static Mame loadMame() {
        return MFM_Data.getInstance().getMame();
    }

    private static Softwarelists loadSoftwareLists() {
        return MFM_Data.getInstance().getSoftwarelists();
    }

    /**
     * Parse the currently set Mame executable
     *
     * @return Mame XML
     * @see Phweda.MFM.MAMEexe
     */
    private static Mame generateMame(boolean all, boolean isListInfo) {
        // By design listInfo is used for pre 0.70 addition of listXML and is All
        return isListInfo ? ParseMAMElistInfo.loadAllMAME() : ParseMAMElistXML.loadAllMachinesInfo(all);
    }

    private static boolean generateAllMameData(boolean isListInfo) {
        MFMSettings.getInstance();// Ensure it is loaded. ?? TODO needed?

        StringBuilder message = new StringBuilder("Parsing MAME: ");
        message.append(MFMSettings.getInstance().getMAMEVersion());
        message.append(" - All flag is ");
        message.append(processAll);

        MFM.getLogger().addToList(message.toString(), true);
        if (MFM.isSystemDebug()) {
            System.out.println(message);
        }

        loadINIs();
        // NOTE order makes a difference!!
        mame = generateMame(processAll, isListInfo);
        if (mame == null) {
            return false;
        }
        getParsedData(isListInfo);
        MFM_Data.getInstance().setSoftwarelists(softwareLists);

        if (mame != null) {
            // Persist it
            MFM_Data.getInstance().setMame(mame);
            MFM_Data.getInstance().setStaticData(RUNNABLE_MACHINES, runnableMachines);
            MFM_Data.getInstance().setStaticData(CATEGORIES, allCategories);
            MFM_Data.getInstance().setStaticData(CATEGORYHIERARCHY, categoryHierarchyMap);

            if (machineControllers != null) {
                // NOTE removing the Controls from being persisted.
                //    MFM_Data.getInstance().setStaticData(CONTROLLERS, machineControllers.getControls());
                MFM_Data.getInstance().setStaticData(CONTROLLERSMACHINES, machineControllers.getControlMachinesList());
            }

            if (categoryMachinesMap != null) {
                MFM_Data.getInstance().setStaticData(CATEGORYMACHINES, categoryMachinesMap);
            }
        } else {
            MFM.getLogger().separateLine();
            MFM.getLogger().addToList(
                    "EXITING on FATAL error. Unable to load Machines information." + FileUtils.NEWLINE +
                            "Check your MAME setup and ensure it is running properly", true);
            System.exit(3);
        }
        return true;
    }

    /**
     * Extract parsed Runnable list, Categories info, MachineControllers
     */
    private static void getParsedData(boolean isListInfo) {
        if (isListInfo) {
            runnableMachines = ParseMAMElistInfo.getRunnable();
            allCategories = ParseMAMElistInfo.getCategoriesList();
            categoryMachinesMap = ParseMAMElistInfo.getCategoryMachineListMap();
            machineControllers = ParseMAMElistInfo.getMachineControllers();
            // make sure previous set is removed - create empty one for backward compatibility
            softwareLists = new Softwarelists();
        } else {
            runnableMachines = ParseMAMElistXML.getRunnable();
            allCategories = ParseMAMElistXML.getCategoriesList();
            categoryMachinesMap = ParseMAMElistXML.getCategoryMachineListMap();
            machineControllers = ParseMAMElistXML.getMachineControllers();
            softwareLists = ParseMAMElistXML.getSoftwarelists();
        }

        if ((allCategories != null) && !allCategories.isEmpty()) {
            createCatHierarchy(allCategories);
        }
    }

    /**
     * Get MAME Command list and descriptions
     * <p>
     * Retain this code for possible future use
     * <p>
     * >mame64 -showusage
     */
    private static void loadCommands() {
        //noinspection unchecked
        commands = (HashMap<String, HashMap<String, String>>) MFM_Data.getInstance().getStaticData(MFM_Constants.COMMANDS);

        if ((commands == null) || commands.isEmpty()) {
            commands = new HashMap<>(75, 0.5f);
            // Get it
            List<String> fullListargs = new ArrayList<>(5);
            fullListargs.add("-showusage");

            // TODO in memory only File via Path from Files.createtempfile
            File showusageFile = new File(MFM.getMfmSettingsDir() + "commands.txt");
            try {
                // Added to overwrite if this text file previously existed
                //noinspection ResultOfMethodCallIgnored
                showusageFile.createNewFile();
                Process process = MAMEexe.run(fullListargs, showusageFile);
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            // TODO Refactor we're passing in using then resetting commands Hashmap to itself
            // TODO DEBUG
            ParseCommandList pcl = new ParseCommandList(MFM.getMfmSettingsDir() + "commands.txt", commands);
            try {
                //noinspection unchecked
                commands = (HashMap<String, HashMap<String, String>>) pcl.processFile();
                // Persist it
                MFM_Data.getInstance().setStaticData(MFM_Constants.COMMANDS, commands);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //noinspection ResultOfMethodCallIgnored
            showusageFile.delete();
        }
    }

    /**
     * Load cached Mame Resources - User's Mame sets
     */
    private static void loadMameResources() {
        Thread loadMameResources = new Thread(() -> {
            long millis = System.currentTimeMillis();
            if (MFM.isSystemDebug()) {
                System.out.println(FileUtils.NEWLINE + "MAME Resources load starting: " + new Date(millis));
            }
            // TODO why is this taking so long?
            //noinspection ResultOfMethodCallIgnored
            MAME_Resources.getInstance(); // load and initialize
            if (MFM.isSystemDebug()) {
                System.out.println("MAME Resources load took: " +
                        TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - millis));
            }
        });
        loadMameResources.start();
    }

    @SuppressWarnings("unchecked")
    private static void loadCaches() {
        inifiles = (Map<String, Map<String, String>>) MFM_Data.getInstance().getUserInis();
        runnableMachines = (Set<String>) MFM_Data.getInstance().getStaticData(RUNNABLE_MACHINES);
        allCategories = (List<String>) MFM_Data.getInstance().getStaticData(CATEGORIES);
        categoryMachinesMap = (Map<String, ArrayList<String>>) MFM_Data.getInstance().
                getStaticData(CATEGORYMACHINES);
        categoryHierarchyMap = (Map<String, ArrayList<String>>)
                MFM_Data.getInstance().getStaticData(CATEGORYHIERARCHY);
        categoryListsMap =
                (Map<String, ArrayList<String>>) MFM_Data.getInstance().getStaticData(CATEGORY_LISTS_HASHMAP);

        machineControllers = MachineControllers.getInstance();
        MachineControllers.setControlMachinesList((TreeMap<Integer, TreeSet<String>>)
                MFM_Data.getInstance().getStaticData(CONTROLLERSMACHINES));
    }

    static TreeSet<String> getAllCategories() {
        return new TreeSet<>(allCategories);
    }

    @SuppressWarnings("unchecked")
    private static void loadCategoriesMap() {
        if (categoryListsMap == null) {
            MFM.getLogger().addToList("MAMEInfo reloading categoryListsMap", true);
            try {
                categoryListsMap = (HashMap<String, ArrayList<String>>)
                        PersistUtils.loadAnObjectXML(MFM.getMfmCategoryDir() + MFM.MFM_CATEGORY_DATA_FILE);
                MFM_Data.getInstance().setStaticData(CATEGORY_LISTS_HASHMAP, categoryListsMap);
            } catch (FileNotFoundException e) {
                MFM.getLogger().addToList("categoryListsMap FAILED to load from file", true);
                e.printStackTrace();
            }
        }
    }

    public static double getVersionDouble() {
        String str = MFMSettings.getInstance().getDataVersion().replaceAll("[^\\d.]", "");
        return Double.parseDouble(str);
    }

    public static Integer[] getNumPlayers() {
        return numPlayers.toArray(new Integer[0]);
    }

    // Future when we read live
    public static void setNumPlayers(SortedSet<Integer> set) {
        numPlayers = set;
    }

    public static String[] getNumButtons() {
        return numButtons.clone();
    }

    // Future when we read live
    public static void setNumButtons(String[] buttons) {
        numButtons = buttons;
    }

    public static HashMap Commands() {
        return commands;
    }

    public static void Commands(HashMap<String, HashMap<String, String>> commands) {
        MAMEInfo.commands = commands;
    }

    public static Mame getMame() {
        return mame;
    }

    public static Set<String> getRunnableMachines() {
        return Collections.unmodifiableSet(runnableMachines);
    }

    public static String getRunnable() {
        return new DecimalFormat("###,###").format(runnable);
    }

    public static void setRunnable(int runnableIn) {
        runnable = runnableIn;
    }

    static Map<String, ArrayList<String>> getCategoryMachinesMap() {
        return Collections.unmodifiableMap(categoryMachinesMap);
    }

    public static MachineControllers getMachineControllers() {
        return machineControllers;
    }

    static Map<String, ArrayList<String>> getCategoryHierarchyMap() {
        return Collections.unmodifiableMap(categoryHierarchyMap);
    }

    public static Machine getMachine(String machineName) {
        if ((mame.getMachineMap().get(machineName) == null) && MFM.isSystemDebug()) {
            System.out.println("MAMEInfo null Machine in getMachine is: " + machineName);
        }
        return mame.getMachineMap().get(machineName);
    }

    public static Software getSoftware(String softwareName, String softwareListName) {
        if (softwareLists == null) {
            return null;
        }
        if (softwareLists.getSoftwarelistsMap().containsKey(softwareListName)) {
            return getSoftware(softwareLists.getSoftwarelistsMap().get(softwareListName), softwareName);
        }
        return null;
    }

    private static Software getSoftware(Softwarelist softwareList, String softwareName) {
        List<Software> softwares = softwareList.getSoftware();
        for (Software software : softwares) {
            if (software.getName().equalsIgnoreCase(softwareName)) {
                return software;
            }
        }
        return null;
    }

    public static Softwarelists getSoftwareLists() {
        return softwareLists;
    }

    public static boolean isSoftwareList(String listName) {
        return softwareLists == null ? false : softwareLists.getSoftwarelistsMap().containsKey(listName);
    }

    public static Map<String, Map<String, String>> getInifiles() {
        return inifiles;
    }

    static Map<String, ArrayList<String>> getCategoryListsMap() {
        return Collections.unmodifiableMap(categoryListsMap);
    }

    // TODO should we replace this with the newer AnalyzeCategories class??
    private static void createCatHierarchy(List<String> categories) {
        if (MFM.isDebug()) {
            MFM.getLogger().addToList("Categories count is : " + categories.size());
        }
        categoryHierarchyMap = new TreeMap<>();
        for (String entry : categories) {
            if (MFM.isDebug()) {
                MFM.getLogger().addToList("Entry is : " + entry);
            }
            if (entry.contains(String.valueOf(FileUtils.SLASH))) {
                String key = entry.substring(0, entry.indexOf(FileUtils.SLASH) - 1);
                // String subKey = entry.substring(index);
                if (MFM.isDebug()) {
                    MFM.getLogger().addToList("Key : " + key + "\tSub category : " + entry);
                }
                if (categoryHierarchyMap.containsKey(key)) {
                    // Shouldn't need but guess there could be duplicates
                    if (!categoryHierarchyMap.get(key).contains(entry)) {
                        categoryHierarchyMap.get(key).add(entry);
                    }
                } else {
                    ArrayList<String> list = new ArrayList<>(50);
                    list.add(entry);
                    categoryHierarchyMap.put(key, list);
                }
            } else {
                // If we haven't previously added it
                if (!categoryHierarchyMap.containsKey(entry)) {
                    categoryHierarchyMap.put(entry, new ArrayList<>(50));
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
        Set<File> files;
        String folderDir = MFMSettings.getInstance().getMAMEFoldersDir();
        if (folderDir != null) {
            File folder = new File(folderDir);
            // Quit if it is not a directory
            if (folder.isDirectory()) {
                files = new HashSet<>(Arrays.asList(folder.listFiles(FileUtils.iniFilenameFilter)));
                if (MFM.isSystemDebug()) {
                    System.out.println("Folders files are : " + files.toString());
                }

                if (!files.isEmpty()) {
                    inifiles = ParseMAMElistXML.iniFiles(files);
                    // Persist it
                    MFM_Data.getInstance().persistUserInis(inifiles);
                }
            }
        }
    }


    public static void dumpManuDriverList() {
        TreeMap<String, TreeSet<String>> driversToManufacturers = new TreeMap<>();
        Map<String, Machine> map = mame.getMachineMap();

        map.values().forEach(machine -> {
            String manufacturer = machine.getManufacturer();
            String driver = machine.getSourcefile(); // Driver file
            if ((manufacturer == null) || manufacturer.isEmpty()
                    || (driver == null) || driver.isEmpty()) {
                return;
            }
            if (driversToManufacturers.containsKey(driver)) {
                driversToManufacturers.get(driver).add("\"" + manufacturer + "\"," + machine.getName());
            } else {
                TreeSet<String> treeSet = new TreeSet<>();
                treeSet.add(manufacturer + COMMA + machine.getName());
                driversToManufacturers.put(driver, treeSet);
            }
        });
        PrintWriter pw;
        try {
            pw = new PrintWriter(new File("E:\\test\\CategoryMFM\\DriverToManufacturer.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // TODO sort by Driver Name
        final String header = "Driver,Manufacturer,MachineName";
        pw.println(header);
        driversToManufacturers.keySet().forEach(driver -> {
            TreeSet<String> manufacturers = driversToManufacturers.get(driver);
            manufacturers.forEach(manufacturer -> pw.println(driver + COMMA + manufacturer));
        });
        pw.close();
    }
}
