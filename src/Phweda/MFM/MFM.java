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

import Phweda.MFM.UI.MFMBusyPainter;
import Phweda.MFM.UI.MFMUI;
import Phweda.MFM.UI.MFMUI_Resources;
import Phweda.MFM.UI.MFM_SettingsPanel;
import Phweda.utils.Debug;
import Phweda.utils.FileUtils;
import Phweda.utils.MemoryMonitor;
import Phweda.utils.SwingUtils;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.icon.EmptyIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static Phweda.MFM.UI.MFMUI_Resources.MFM_Icon_PNG;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/24/11
 * Time: 2:06 PM
 */
public class MFM {

    public static final String APPLICATION_NAME = "MAME File Manager";

    public static final String MFM_DIR;
    public static final String MFM_SETTINGS_DIR;
    public static final String MFM_RESOURCES;
    public static final String MFM_JARS_DIR;
    public static final String MFM_LISTS_DIR;
    public static final String MFM_LOGS_DIR;
    public static final String MFM_FOLDERS_DIR;
    public static final String MFM_CATEGORY_DIR;
    public static final String MFM_CACHE_SER = "MFM_cache.ser";
    public static final String MFM_MAME_FILE = "MFM_MAME.xml";
    public static final String MFM_CACHE_XML = "MFM_cache.xml";
    public static final String MFM_User_Guide = "MAME File Manager User Guide.pdf";
    // Update these with each release
    public static final String VERSION = "Version 0.8.5";
    public static final String BUILD = "BUILD 0.8.103";
    public static final String RELEASE_DATE = "Released : March 2017";
    public static final String MFM_TITLE = MFM.APPLICATION_NAME + "  :  " + MFM.VERSION + "  :  " + MFM.BUILD;
    public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static final Point screenCenterPoint = new Point(screenSize.width / 2, screenSize.height / 2);
    public static final int time;
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
    private static boolean progressRunning = false;
    private static boolean systemoutDebug = false;

    private static JFrame settingsFrame;
    private static JDialog busyDialog = new JDialog(settingsFrame, MFM_TITLE);
    private static Thread busyThread = null;

    static {
        File dir = new File(".");
        String path = null;
        try {
            path = dir.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();

            System.out.println("MFM FATAL error failed to detect run directory");
            System.exit(7);
        }

        // Path we are executing in
        MFM_DIR = path + FileUtils.DIRECTORY_SEPARATOR;
        // Folder we create on the local file system
        MFM_SETTINGS_DIR = MFM_DIR + MFM_Constants.SETTINGS + FileUtils.DIRECTORY_SEPARATOR;
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
            MFMnoDirectories = MFMnoDirectories & !settingsDIR.mkdir();
        }

        File JarsDIR = new File(MFM_JARS_DIR);
        if (!JarsDIR.exists()) {
            MFMnoDirectories = MFMnoDirectories & !JarsDIR.mkdir();
        }

        File logsDIR = new File(MFM_LOGS_DIR);
        if (!logsDIR.exists()) {
            MFMnoDirectories = MFMnoDirectories & !logsDIR.mkdir();
        }

        File listDIR = new File(MFM_LISTS_DIR);
        if (!listDIR.exists()) {
            MFMnoDirectories = MFMnoDirectories & !listDIR.mkdir();
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

        int timeNow = (int) System.currentTimeMillis();
        // Gives us a 6 digit descending, over time, number
        time = timeNow >> 12;

        try {
            Log = new File(MFM_LOGS_DIR + "MFM_Debug_log-" + time + ".txt");
            ErrorLog = new File(MFM_LOGS_DIR + "MFM_ERRout-" + time + ".txt");

            logger = new Debug(new FileOutputStream(Log));
            System.setErr(new PrintStream(new FileOutputStream(ErrorLog)));
            MAMEout = new File(MFM_LOGS_DIR + "MAME_OUT-" + time + ".txt");
            FFmpegout = new File(MFM_LOGS_DIR + "FFmpeg_OUT-" + time + ".txt");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            logger.addToList("MFM is running in : " + path, true);
            logger.addToList("MFM " + MFM.VERSION + "  :  " + MFM.BUILD, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        /*
          Command line switches
        */
        List<String> switches = Arrays.asList(args);

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
            MemoryMonitor mm = new MemoryMonitor(600000);
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

        if (systemoutDebug) {
            System.out.println("Main 01");
        }
        try {
            // Add L&Fs
            add3rdPartyLFs();
            MS = MFMSettings.getInstance();
            MAMEInfo MI = null;
            if (MS.isLoaded()) {
                while (!MFM_Data.isLoaded()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                MI = new MAMEInfo();
            } else {
                setFirstRun(true);
                showBusy(false, false); // this is messy logic
                // first run must acquire base settings before continuing
                getSettings();
            }

            // TODO better way to wait?? Can we now eliminate since we distribute data .ser??
            while (!MS.isLoaded()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (MI == null) {
                MI = new MAMEInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread loadMameResources = new Thread() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis();
                if (MFM.isSystemDebug()) {
                    System.out.println("\nMAME Resources load starting: " + new Date(millis));
                }
                MAME_Resources.getInstance(); // load and initialize
                if (MFM.isSystemDebug()) {
                    System.out.println("MAME Resources load took: " +
                            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - millis));
                }
            }
        };
        loadMameResources.start();
        MFMUI.main(null);
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

    public static boolean isProgressRunning() {
        return progressRunning;
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

    public static JFrame getSettings() {
        MFM_SettingsPanel.showSettingsPanel(getSettingsFrame());
        return settingsFrame;
    }

    private static JFrame getSettingsFrame() {
        if (settingsFrame != null) {
            settingsFrame.dispose();
        }
        settingsFrame = new JFrame("MFM Settings");
        settingsFrame.setIconImage(MFMUI_Resources.getInstance().getImageIcon(MFM_Icon_PNG).getImage());

        return settingsFrame;
    }

    // TODO move this
    public static void showBusy(boolean start, boolean task) { // task false is empty startup. task true is loading data

        progressRunning = start;
        if (start) {
            busyThread = new Thread() {
                @Override
                public void run() {
                    busyDialog.setLocation(screenCenterPoint.x - 150, screenCenterPoint.y - 50);
                    JXBusyLabel busyLabel = createComplexBusyLabel();
                    if (task) {
                        busyLabel.setText("<HTML>DATA<br>LOADING</HTML>");
                        busyLabel.setToolTipText("MFM Data Loading");
                    } else {
                        busyLabel.setText("<HTML>Parsing<br>MAME data</HTML>");
                        busyLabel.setToolTipText("Parsing MAME Data");
                    }

                    busyDialog.add(busyLabel);
                    busyLabel.setBusy(true);
                    busyDialog.pack();
                    busyDialog.setVisible(true);
                }
            };
            busyThread.start();
        } else {
            busyDialog.dispose();
            busyDialog.setVisible(false);
            busyThread.interrupt();
        }
    }

    // TODO move this along with above
    private static JXBusyLabel createComplexBusyLabel() {
        JXBusyLabel label = new JXBusyLabel(new Dimension(325, 150));
        // default is 100
        label.setDelay(100);
        /*
        BusyPainter painter = new BusyPainter(
                new Rectangle2D.Float(0.0f, 0.0f, 8.0f, 8.0f),
                new Rectangle2D.Float(20.5f, 20.5f, 75.0f, 75.0f));
*/
        MFMBusyPainter painter = new MFMBusyPainter(
                new Ellipse2D.Double(0.0d, 0.0d, 15.0d, 15.0d),
                new Ellipse2D.Double(10.0d, 10.0d, 125.0d, 125.0d));

        painter.setTrailLength(64);
        painter.setPoints(192);
        painter.setFrame(-1);
        painter.setBaseColor(MFMUI.getMFMcolor());
        painter.setHighlightColor(Color.orange);

        label.setPreferredSize(new Dimension(325, 150));
        label.setMinimumSize(new Dimension(325, 150));
        label.setIcon(new EmptyIcon(150, 150));
        label.setBusyPainter(painter);
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, 24));
        return label;
    }

    // TODO move this
    public static String formatMillis(long nanos) {
        return String.format("%02d:%02d:%02d.%03d", TimeUnit.NANOSECONDS.toHours(nanos),
                TimeUnit.NANOSECONDS.toMinutes(nanos) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.NANOSECONDS.toSeconds(nanos) % TimeUnit.MINUTES.toSeconds(1),
                TimeUnit.NANOSECONDS.toMillis(nanos) % TimeUnit.SECONDS.toMillis(1));
    }

    protected void finalize() throws Throwable {
        super.finalize();
        //    logger.addToList("MFM Finalize complete.", true);
    }

}
