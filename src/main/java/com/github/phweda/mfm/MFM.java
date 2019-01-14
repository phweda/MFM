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

package com.github.phweda.mfm;

import com.github.phweda.mfm.ui.MFMUI;
import com.github.phweda.mfm.ui.MFMUI_Setup;
import com.github.phweda.utils.Debug;
import com.github.phweda.utils.FileUtils;
import com.github.phweda.utils.MemoryMonitor;
import com.github.phweda.utils.SwingUtils;

import javax.swing.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/24/11
 * Time: 2:06 PM
 */
@SuppressWarnings("WeakerAccess")
public final class MFM {

    public static final String MAME_FILE_MANAGER = "MAME File Manager";

    private static String mfmDir;
    private static String mfmSettingsDir;
    private static String mfmDataDir;
    private static String mfmResources;
    private static String mfmJarsDir;
    private static String mfmListsDir;
    private static String mfmLogsDir;
    private static String mfmFoldersDir;
    private static String mfmCategoryDir;
    private static final String MFM_USER_GUIDE = "MAME File Manager User Guide.pdf";
    // Update these with each release
    private static final String VERSION = "Version 0.9.6";
    private static final String BUILD = "BUILD 0.9.600";
    private static final String RELEASE_DATE = "Released : Sept 2018";
    private static final String LOCAL_COUNTRY = Locale.getDefault().getCountry();
    private static final String MFM_TITLE = MAME_FILE_MANAGER + "  :  " + VERSION;

    public static final char COLON = ':';

    // Gives us a 6 digit descending, over time, number
    public static final int LOG_NUMBER = (int) System.currentTimeMillis() >> 12;

    static final String MFM_SETTINGS_FILE = "MFM_SETTINGS.xml";
    static final String MFM_CATEGORY_DATA_FILE = "CategoryListsMap.xml";
    static final String MAME_RESOURCES_CACHE = "Resources_cache.ser";
    static final String MAME_CONTROLLERS = "MAME_Controllers.ini";
    static final String MAME_FOLDER_NAMES_FILE = "MAME_folders.ini";
    private static final String OS_VERSION = System.getProperty("os.name");
    private static final String M = "-m"; // Memory Monitor
    private static final String DO_DEBUG = "-d"; // Debug
    private static final String LIST = "-list"; // UI flag for List only view
    private static final String PARSE = "-p"; // Bootstrap parsing
    private static final String SYSTEM = "-s"; // System Out Debug

    private static String tempdir = System.getProperty("java.io.tmpdir");
    private static Debug logger;
    private static File log;
    private static File errorLog;
    private static File gcLog;
    private static File mameout;
    private static File fFmpegout;
    private static MFMSettings mfmSettings;

    // Flag for UI type
    private static boolean listOnly = false;
    private static boolean debug = false;
    private static boolean firstRun = false;
    private static boolean systemoutDebug = false;
    private static boolean doParse = false;

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

    MFM() {
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
            mfmSettings = MFMSettings.getInstance();
            // TODO should this be moved into MFMSettings?
            if (!mfmSettings.isLoaded()) {
                setFirstRun(); // Note needed to trigger .ini scan initiated in MFM_SettingsPanel
                // first run must acquire base settings before continuing
                MFMUI.getSettings();
            }
            // Wait for user settings input
            while (!mfmSettings.isLoaded()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }

            // Load Data Set
            MFMUI_Setup.getInstance().loadDataSet();

            // Wait for Data Set load
            MFM_Data mfmData = MFM_Data.getInstance();
            while (mfmData.notLoaded()) {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private static void loadSwitches(Collection<String> switches) {
        /*
          Command line switches
        */
        logger.out("MFM starting");
        logger.out("MFM switches are: " + switches);

        if (switches.contains(DO_DEBUG)) {
            logger.out("Running in debug mode.");
            debug = true;
        }

        if (switches.contains(LIST)) {
            logger.out("Running List Only view.");
            listOnly = true;
        }

        if (switches.contains(M)) {
            logger.out("Running with Memory Monitor.");
            // 10 minutes in milliseconds
            MemoryMonitor mm = new MemoryMonitor(600000, logger.getOutputStream());
            Thread mmThread = new Thread(mm);
            mmThread.start();
        }

        // Undocumented bootstrap doParse
        if (switches.contains(PARSE)) {
            logger.out("Running bootstrap parsing");
            doParse = true;
        }

        if (switches.contains(SYSTEM)) {
            logger.out("Running in system out debug mode.");
            systemoutDebug = true;
        }

    }

    private static void logEnvironment() {
        logger.out("Temp dir is " + tempdir);

        // Get the VM arguments and log
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMXBean.getInputArguments();
        logger.separateLine();
        logger.out(OS_VERSION + '\n');

        if (debug) {
            Map<String, String> env = System.getenv();
            for (String envName : env.keySet()) {
                if (envName.contains("PROCESSOR")) {
                    logger.out(envName + " : " + env.get(envName));
                } else if (systemoutDebug) {
                    // logger.out(envName + " : " + env.get(envName)); // NOTE Comment out to protect users' configs
                }
            }
        }
        logger.separateLine();
        String javaVersion = System.getProperty("java.version");
        String jvmVersion = ManagementFactory.getRuntimeMXBean().getVmVersion();
        logger.addToList("Java version : " + javaVersion);
        logger.addToList("JVM version : " + jvmVersion);
        for (String arg : arguments) {
            logger.addToList(arg);
            if (arg.startsWith("-Xloggc:")) {
                gcLog = new File(arg.substring(arg.indexOf(COLON) + 1));
            }
        }
        logger.separateLine();
    }

    @SuppressWarnings({"squid:S2178", "MethodWithMoreThanThreeNegations"})
    private static void setPathsandDirectories(String path) {
        // Path we are executing in
        mfmDir = path + FileUtils.DIRECTORY_SEPARATOR;
        // Folder we create on the local file system
        mfmSettingsDir = mfmDir + MFM_Constants.SETTINGS + FileUtils.DIRECTORY_SEPARATOR;
        // Folder we create on the local file system for Data Sets
        mfmDataDir = mfmDir + MFM_Constants.DATA + FileUtils.DIRECTORY_SEPARATOR;
        // Resources are all within the MFM.jar file
        mfmResources = FileUtils.DIRECTORY_SEPARATOR;
        // Added LnF Jars are placed here
        mfmJarsDir = mfmDir + MFM_Constants.JARS + FileUtils.DIRECTORY_SEPARATOR;
        // Lists output files are placed here
        mfmListsDir = mfmDir + MFM_Constants.LISTS + FileUtils.DIRECTORY_SEPARATOR;
        // Log files are placed here
        mfmLogsDir = mfmDir + MFM_Constants.LOGS + FileUtils.DIRECTORY_SEPARATOR;
        // Location of MFM distribution /folders/catver_full.ini
        mfmFoldersDir = mfmDir + MFM_Constants.FOLDERS + FileUtils.DIRECTORY_SEPARATOR;
        // Location of MFM distribution /Category/ root category files
        mfmCategoryDir = mfmDir + MFM_Constants.CATEGORY + FileUtils.DIRECTORY_SEPARATOR;

        boolean mfmnodirectories = false;
        File settingsDIR = new File(mfmSettingsDir);
        if (!settingsDIR.exists()) {
            mfmnodirectories |= !settingsDIR.mkdir();
        }

        File dataDIR = new File(mfmDataDir);
        if (!dataDIR.exists()) {
            mfmnodirectories |= !dataDIR.mkdir();
        }

        File jarsDIR = new File(mfmJarsDir);
        if (!jarsDIR.exists()) {
            mfmnodirectories |= !jarsDIR.mkdir();
        }

        File logsDIR = new File(mfmLogsDir);
        if (!logsDIR.exists()) {
            mfmnodirectories |= !logsDIR.mkdir();
        }

        File listDIR = new File(mfmListsDir);
        if (!listDIR.exists()) {
            mfmnodirectories |= !listDIR.mkdir();
        }

        if (tempdir == null) {
            tempdir = mfmDir + FileUtils.DIRECTORY_SEPARATOR + "temp" + FileUtils.DIRECTORY_SEPARATOR;
            File tempDir = new File(tempdir);
            if (!tempDir.exists()) {
                mfmnodirectories &= !tempDir.mkdir();
            }
        }

        if (mfmnodirectories) {
            System.out.println("MFM FATAL error cannot find or create MFM directories");
            System.exit(8);
        }
    }

    private static void createLogs() {
        try {
            log = new File(mfmLogsDir + "MFM_Debug_log-" + LOG_NUMBER + ".txt");
            errorLog = new File(mfmLogsDir + "MFM_ERRout-" + LOG_NUMBER + ".txt");

            logger = new Debug(new FileOutputStream(log));
            System.setErr(new PrintStream(new FileOutputStream(errorLog)));
            mameout = new File(mfmLogsDir + "MAME_OUT-" + LOG_NUMBER + ".txt");
            fFmpegout = new File(mfmLogsDir + "FFmpeg_OUT-" + LOG_NUMBER + ".txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static MFMSettings getMFMSettings() {
        return mfmSettings;
    }

    public static boolean isListOnly() {
        return listOnly;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static boolean isSystemDebug() {
        return systemoutDebug;
    }

    public static boolean isDoParse() {
        return doParse;
    }

    public static boolean isFirstRun() {
        return firstRun;
    }

    private static void setFirstRun() {
        MFM.firstRun = true;
    }

    public static String getMfmDir() {
        return mfmDir;
    }

    public static String getMfmSettingsDir() {
        return mfmSettingsDir;
    }

    public static String getMfmDataDir() {
        return mfmDataDir;
    }

    public static String getMfmResources() {
        return mfmResources;
    }

    public static String getMfmJarsDir() {
        return mfmJarsDir;
    }

    public static String getMfmListsDir() {
        return mfmListsDir;
    }

    public static String getMfmLogsDir() {
        return mfmLogsDir;
    }

    public static String getMfmFoldersDir() {
        return mfmFoldersDir;
    }

    public static String getMfmCategoryDir() {
        return mfmCategoryDir;
    }

    public static String getMFMUserGuide() {
        return MFM_USER_GUIDE;
    }

    public static String getVERSION() {
        return VERSION;
    }

    public static String getBUILD() {
        return BUILD;
    }

    public static String getReleaseDate() {
        return RELEASE_DATE;
    }

    public static String getMfmTitle() {
        return MFM_TITLE;
    }

    public static String getLocalCountry() {
        return LOCAL_COUNTRY;
    }

    public static String getTempdir() {
        return tempdir;
    }

    public static Debug getLogger() {
        return logger;
    }

    public static File getLog() {
        return log;
    }

    public static File getErrorLog() {
        return errorLog;
    }

    public static File getGcLog() {
        return gcLog;
    }

    public static File getMameout() {
        return mameout;
    }

    public static File getfFmpegout() {
        return fFmpegout;
    }

    private static void add3rdPartyLFs() {
        if (new File(mfmJarsDir).exists()) {
            SwingUtils.load3rdPartyLFs(mfmJarsDir);
        }
    }

    /**
     * Message                             Explanation
     * Process finished with exit code 2  User chose to not doParse MAME and has no Data Sets(MFMController.java)
     * Process finished with exit code 3  Total failure to load MAME info after Parsing attempt. Check MAME runs. (MAMEInfo.java)
     * Process finished with exit code 4  User canceled MFM Settings – cannot run without them(MFM_SettingsPanel.java)
     * Process finished with exit code 5  Data integrity issue. MFM_cache.ser missing or corrupt.(MFMListBuilder.java)
     * Process finished with exit code 6  Data integrity issue. Data Set MFM is set to load is not found.
     * Did you delete or alter a Data Set file?(MFM_Data.java)
     * Process finished with exit code 7  MFM failed to detect its running directory (MFM.java)
     * Process finished with exit code 8  MFM failed to find/create its required directories (MFM.java)
     * Process finished with exit code 9  MFM failed to load data set or doParse MAME (MFMInfo.java)
     * Process finished with exit code 10 MFM failed to find a data set(MFMController.java)
     * Process finished with exit code 11 MFM settings file corrupted?? MFM_Data.java)
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public static void exit(int status) {
        MFM_Data.getInstance().persistSettings(); // Capture and persist any user driven settings: UI & Current List
        // Wait for final exit if a Data Set is writing to disk
        while (MFM_Data.getInstance().isPersisting()) {
        }
        if (status != 0) {
            System.err.println("Process finished with exit code " + status);
            logger.addToList("Process finished with exit code " + status, true);
            JOptionPane.showMessageDialog(null, "Fatal error #" + status, "MFM Closing",
                    JOptionPane.ERROR_MESSAGE);
        }
        System.exit(status);
    }

    public static void exit() {
        exit(0);
    }

}
