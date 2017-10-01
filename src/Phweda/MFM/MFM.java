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

import Phweda.MFM.UI.MFMUI;
import Phweda.MFM.UI.MFMUI_Setup;
import Phweda.utils.Debug;
import Phweda.utils.FileUtils;
import Phweda.utils.MemoryMonitor;
import Phweda.utils.SwingUtils;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/24/11
 * Time: 2:06 PM
 */
public class MFM {

    public static final String APPLICATION_NAME = "MAME File Manager";

    public static String MFM_DIR;
    public static String MFM_SETTINGS_DIR;
    public static String MFM_DATA_DIR;
    public static String MFM_RESOURCES;
    public static String MFM_JARS_DIR;
    public static String MFM_LISTS_DIR;
    public static String MFM_LOGS_DIR;
    public static String MFM_FOLDERS_DIR;
    public static String MFM_CATEGORY_DIR;
    public static final String MFM_User_Guide = "MAME File Manager User Guide.pdf";
    // Update these with each release
    public static final String VERSION = "Version 0.9";
    public static final String BUILD = "BUILD 0.9.100";
    public static final String RELEASE_DATE = "Released : July 2017";
    public static final String LOCAL_COUNTRY = Locale.getDefault().getCountry();
    public static final String MFM_TITLE = MFM.APPLICATION_NAME + "  :  " + MFM.VERSION;

    public static int logNumber;
    static final String MFM_SETTINGS_FILE = "MFM_SETTINGS.xml";
    static final String MFM_CATEGORY_DATA_FILE = "CategoryListsMap.xml";
    static final String MAME_RESOURCES_CACHE = "Resources_cache.ser";
    static final String MAME_CONTROLLERS = "MAME_Controllers.ini";
    static final String MAME_FOLDER_NAMES_FILE = "MAME_folders.ini";
    private static final String OS_version = System.getProperty("os.name");
    private static final String m = "-m"; // Memory Monitor
    private static final String doDebug = "-d"; // Debug
    private static final String list = "-list"; // UI flag for List only view
    private static final String allMachines = "-all"; // Parse and cache all Machines -- include not Playable
    private static final String system = "-s"; // System Out Debug
    public static String TEMP_DIR = System.getProperty("java.io.tmpdir");
    public static Debug logger;
    public static File Log;
    public static File ErrorLog;
    public static File GCLog;
    public static File MAMEout;
    public static File FFmpegout;
    private static MFMSettings MS;

    // Flag for UI type
    private static boolean listOnly = false;
    // Flag to process ALL machines as opposed to just Playable
    private static boolean processAll = false;
    private static boolean debug = false;
    private static boolean firstRun = false;
    private static boolean systemoutDebug = false;

    static {
        File dir = new File(".");
        String path = null;
        try {
            path = dir.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("MFM FATAL error failed to detect run directory");
            exit(7);
        }

        setPathsandDirectories(path);
        createLogs();

        try {
            logger.addToList("MFM is running in : " + path, true);
            logger.addToList("MFM " + MFM.VERSION + "  :  " + MFM.BUILD, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        loadSwitches(Arrays.asList(args));
        logEnvironment();
        // Add LookandFeels
        add3rdPartyLFs();
        // Get settings and Mame Information
        loadSettingsAndInfo();

        MAMEexe.setBaseArgs(MFMSettings.getInstance().fullMAMEexePath());
        // Start the GUI
        MFMUI.main(null);

        // End progress Dialog here
        if (MFMUI.isProgressRunning()) {
            MFMUI.showBusy(false, false);
        }
    }

    private static void loadSettingsAndInfo() {
        try {
            // MFMSettings initiates MFM_Data and Data Set scan if needed
            MS = MFMSettings.getInstance();
            if (!MS.isLoaded()) {
                setFirstRun(true); // Note needed to trigger .ini scan initiated in MFM_SettingsPanel
                // first run must acquire base settings before continuing
                MFMUI.getSettings();
            }
            // Wait for user settings input
            while (!MS.isLoaded()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Load Data Set
            MFMUI_Setup.getInstance().loadDataSet();
            // Wait for Data Set load
            while (!MFM_Data.getInstance().isLoaded()) {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadSwitches(List switches) {
        /*
          Command line switches
        */
        logger.out("MFM starting");
        logger.out("MFM switches are: " + switches);

        if (switches.contains(doDebug)) {
            logger.out("Running in debug mode.");
            debug = true;
        }

        if (switches.contains(list)) {
            logger.out("Running List Only view.");
            listOnly = true;
        }

        if (switches.contains(m)) {
            logger.out("Running with Memory Monitor.");
            // 10 minutes in milliseconds
            MemoryMonitor mm = new MemoryMonitor(600000, logger.getOutputStream());
            Thread mmThread = new Thread(mm);
            mmThread.start();
        }

        if (switches.contains(allMachines)) {
            logger.out("Running with -all machines set. Only applies if you parse your own MAME");
            processAll = true;
        }

        if (switches.contains(system)) {
            logger.out("Running in system out debug mode.");
            systemoutDebug = true;
        }
    }

    private static void logEnvironment() {
        logger.out("Temp dir is " + TEMP_DIR);

        // Get the VM arguments and log
        RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = RuntimemxBean.getInputArguments();
        logger.separateLine();
        logger.out(OS_version + "\n");

        if (isDebug()) {
            Map<String, String> env = System.getenv();
            for (String envName : env.keySet()) {
                if (envName.contains("PROCESSOR")) {
                    logger.out(envName + " : " + env.get(envName));
                }
            }
        }
        logger.separateLine();
        String JAVA_version = System.getProperty("java.version");
        String JVM_version = ManagementFactory.getRuntimeMXBean().getVmVersion();
        logger.addToList("Java version : " + JAVA_version);
        logger.addToList("JVM version : " + JVM_version);
        for (String arg : arguments) {
            logger.addToList(arg);
            if (arg.startsWith("-Xloggc:")) {
                GCLog = new File(arg.substring(arg.indexOf(':') + 1));
            }
        }
        logger.separateLine();
    }

    private static void setPathsandDirectories(String path) {
        // Path we are executing in
        MFM_DIR = path + FileUtils.DIRECTORY_SEPARATOR;
        // Folder we create on the local file system
        MFM_SETTINGS_DIR = MFM_DIR + MFM_Constants.SETTINGS + FileUtils.DIRECTORY_SEPARATOR;
        // Folder we create on the local file system for Data Sets
        MFM_DATA_DIR = MFM_DIR + MFM_Constants.DATA + FileUtils.DIRECTORY_SEPARATOR;
        // Resources are all within the MFM.jar file
        MFM_RESOURCES = FileUtils.DIRECTORY_SEPARATOR + MFM_Constants.RESOURCES + FileUtils.DIRECTORY_SEPARATOR;
        // Added LnF Jars are placed here
        MFM_JARS_DIR = MFM_DIR + MFM_Constants.JARS + FileUtils.DIRECTORY_SEPARATOR;
        // Lists output files are placed here
        MFM_LISTS_DIR = MFM_DIR + MFM_Constants.LISTS + FileUtils.DIRECTORY_SEPARATOR;
        // Log files are placed here
        MFM_LOGS_DIR = MFM_DIR + MFM_Constants.LOGS + FileUtils.DIRECTORY_SEPARATOR;
        // Location of MFM distribution /folders/catver_full.ini
        MFM_FOLDERS_DIR = MFM_DIR + MFM_Constants.FOLDERS + FileUtils.DIRECTORY_SEPARATOR;
        // Location of MFM distribution /Category/ root category files
        MFM_CATEGORY_DIR = MFM_DIR + MFM_Constants.CATEGORY + FileUtils.DIRECTORY_SEPARATOR;

        boolean MFMnoDirectories = false;
        File settingsDIR = new File(MFM_SETTINGS_DIR);
        if (!settingsDIR.exists()) {
            MFMnoDirectories = MFMnoDirectories | !settingsDIR.mkdir();
        }

        File dataDIR = new File(MFM_DATA_DIR);
        if (!dataDIR.exists()) {
            MFMnoDirectories = MFMnoDirectories | !dataDIR.mkdir();
        }

        File JarsDIR = new File(MFM_JARS_DIR);
        if (!JarsDIR.exists()) {
            MFMnoDirectories = MFMnoDirectories | !JarsDIR.mkdir();
        }

        File logsDIR = new File(MFM_LOGS_DIR);
        if (!logsDIR.exists()) {
            MFMnoDirectories = MFMnoDirectories | !logsDIR.mkdir();
        }

        File listDIR = new File(MFM_LISTS_DIR);
        if (!listDIR.exists()) {
            MFMnoDirectories = MFMnoDirectories | !listDIR.mkdir();
        }

        if (TEMP_DIR == null) {
            TEMP_DIR = MFM_DIR + FileUtils.DIRECTORY_SEPARATOR + "temp" + FileUtils.DIRECTORY_SEPARATOR;
            File tempDir = new File(TEMP_DIR);
            if (!tempDir.exists()) {
                MFMnoDirectories = MFMnoDirectories & !tempDir.mkdir();
            }
        }

        if (MFMnoDirectories) {
            System.out.println("MFM FATAL error cannot find or create MFM directories");
            System.exit(8);
        }
    }

    private static void createLogs() {
        int timeNow = (int) System.currentTimeMillis();
        // Gives us a 6 digit descending, over time, number
        logNumber = timeNow >> 12;
        try {
            Log = new File(MFM_LOGS_DIR + "MFM_Debug_log-" + logNumber + ".txt");
            ErrorLog = new File(MFM_LOGS_DIR + "MFM_ERRout-" + logNumber + ".txt");

            logger = new Debug(new FileOutputStream(Log));
            System.setErr(new PrintStream(new FileOutputStream(ErrorLog)));
            MAMEout = new File(MFM_LOGS_DIR + "MAME_OUT-" + logNumber + ".txt");
            FFmpegout = new File(MFM_LOGS_DIR + "FFmpeg_OUT-" + logNumber + ".txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static MFMSettings getMFMSettings() {
        return MS;
    }

    public static boolean isListOnly() {
        return listOnly;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static boolean isProcessAll() {
        return processAll;
    }

    public static boolean isSystemDebug() {
        return systemoutDebug;
    }

    public static boolean isFirstRun() {
        return firstRun;
    }

    private static void setFirstRun(boolean firstRun) {
        MFM.firstRun = firstRun;
    }

    private static void add3rdPartyLFs() {
        if (new File(MFM_JARS_DIR).exists()) {
            SwingUtils.load3rdPartyLFs(MFM_JARS_DIR);
        }
    }

    public static void exit(int status) {
        MFM_Data.getInstance().persistSettings(); // Capture and persist any user driven settings: UI & Current List
        // Wait for final exit if a Data Set is writing to disk
        while (MFM_Data.getInstance().isPersisting()) {
        }
        if (status != 0) {
            System.err.println("Process finished with exit code " + status);
            logger.addToList("Process finished with exit code " + status, true);
        }
        System.exit(status);
    }

    public static void exit() {
        exit(0);
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }
}
