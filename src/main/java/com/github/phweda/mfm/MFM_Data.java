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

import com.github.phweda.mfm.ui.MFMAction;
import com.github.phweda.mfm.ui.MFMUI;
import com.github.phweda.mfm.mame.Mame;
import com.github.phweda.mfm.mame.softwarelist.Softwarelists;
import com.github.phweda.utils.Hasher;
import com.github.phweda.utils.PersistUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipOutputStream;

/*
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 10/19/2015
 * Time: 6:23 PM
 */

/**
 * Manages persistence of all MFM Data
 */
public class MFM_Data {
    private static MFM_Data ourInstance;
    private static MFM_Data_Sets datasets;

    private static HashMap<String, Object> settings = new HashMap<>();
    // Provides common name mapping for MachineControllers
    private static File controllersLabelsFile;
    // Contains list of Mame folders
    private static File folderNamesFile;

    // Machine:built-in(catver,nplayers,category roots, category/arcade:system):
    private HashMap<String, Object> permData = new HashMap<>();
    private Mame mame;
    private Softwarelists softwarelists;
    private String dataVersion;

    /*
     * File naming for Data Sets incorporating version
        180 - Each ZIP file contains MFM_MAME.xml & MFM_cache.ser
        MFM_MAME_ALL_180.zip    Everything
        MFM_MAME_180.zip        Runnable Machines only
     */
    private static final String MFM_CACHE = "MFM_cache";
    private static final String SER_SUFFIX = ".ser";
    private static final String MFM_MAME = "MFM_MAME";
    private static final String MFM_SOFTWARELISTS = "MFM_SOFTWARELISTS";

    private static final String XML_SUFFIX = ".xml";
    private static final String ZIP_SUFFIX = ".zip";

    private static final String MFM_CACHE_SER = MFM_CACHE + SER_SUFFIX;
    private static final String MFM_MAME_XML = MFM_MAME + XML_SUFFIX;
    private static final String MFM_SOFTWARELISTS_XML = MFM_SOFTWARELISTS + XML_SUFFIX;
    private static final String MFM_USER_INI_FILE = MFM.getMfmSettingsDir() + "UserIniData.xml";
    private static final String MFM_DATA_SETS_FILE = MFM.getMfmSettingsDir() + "DataSets.ser";

    private boolean staticChanged = false;
    private static boolean scanningDataSets = false;
    private volatile boolean loaded = false;
    private static boolean persisting = false;

    private MFM_Data() {
        loadSettingsFiles();
        findDataSets();
    }

    public static MFM_Data getInstance() {
        if (ourInstance == null) {
            ourInstance = new MFM_Data();
        }
        return ourInstance;
    }

    public boolean notLoaded() {
        return !loaded;
    }

    public void setLoaded() {
        loaded = true;
    }

    static boolean isScanningDataSets() {
        return scanningDataSets;
    }

    private String getZipPathString(String directory) {
        if (MFM.isSystemDebug()) {
            System.out.println("In getZipPathString data version is " + getDataVersion());
        }
        return directory + MFM_MAME + "_" + getDataVersion() + ZIP_SUFFIX;
    }

    boolean isPersisting() {
        return persisting;
    }

    @SuppressWarnings("SameParameterValue")
    void persistStaticData(String saveDirectory, boolean all) {
        if (!staticChanged) {
            return;
        }
        Thread persist = new Thread() {
            @Override
            public void run() {
                if (MFM.isSystemDebug()) {
                    System.out.println("## Static data persist thread");
                }

                synchronized (this) {
                    persisting = true;
                    String logMessage = "persistStaticData: " + permData.size() + " : " +
                            permData.keySet() + "\n\n";
                    System.out.println(logMessage);
                    MFM.getLogger().addToList(logMessage);

                    try (ZipOutputStream zipOutputStream =
                                 new ZipOutputStream(new FileOutputStream(getZipPathString(saveDirectory)))) {
                        PersistUtils.saveAnObjecttoZip(permData, zipOutputStream, MFM_CACHE_SER);

                        if (mame != null) {
                            PersistUtils.saveJAXBtoZip(mame, zipOutputStream, MFM_MAME_XML, Mame.class, false);
                        }
                        if (softwarelists != null) {
                            PersistUtils.saveJAXBtoZip(softwarelists, zipOutputStream, MFM_SOFTWARELISTS_XML,
                                    Softwarelists.class, false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setStaticChanged(false);
                    persisting = false;
                }
            }
        };
        persist.start();
    }

    public boolean contains(String key) {
        return (settings.containsKey(key) || permData.containsKey(key));
    }

    @SuppressWarnings("unchecked")
    private static void loadSettingsFiles() {
        // Load files first - required as of 0.85 for first run special case
        if (new File(MFM.getMfmSettingsDir() + MFM.MFM_SETTINGS_FILE).exists()) {
            try {
                settings = (HashMap<String, Object>)
                        PersistUtils.loadAnObjectXML(MFM.getMfmSettingsDir() + MFM.MFM_SETTINGS_FILE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                MFM.exit(11);
            }
        }

        if (new File(MFM.getMfmSettingsDir() + MFM.MAME_CONTROLLERS).exists()) {
            controllersLabelsFile = new File(MFM.getMfmSettingsDir() + MFM.MAME_CONTROLLERS);
        }

        if (new File(MFM.getMfmSettingsDir() + MFM.MAME_FOLDER_NAMES_FILE).exists()) {
            folderNamesFile = new File(MFM.getMfmSettingsDir() + MFM.MAME_FOLDER_NAMES_FILE);
        }
    }

    private static void findDataSets() {
        if (Paths.get(MFM_DATA_SETS_FILE).toFile().exists()) {
            try {
                datasets = (MFM_Data_Sets) PersistUtils.loadAnObject(MFM_DATA_SETS_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (datasets == null || datasets.newSets()) {
            scanningDataSets = true;
            datasets = new MFM_Data_Sets();
            Thread scanDataSets = new Thread() {
                @Override
                public void run() {
                    synchronized (this) {
                        datasets.scanSets();
                        int sets = datasets.getAvailableVersions().size();
                        MFM.getLogger().addToList("Scanned for Data Sets and found: " + sets);
                        System.out.println("Scanned for Data Sets and found: " + sets);
                        scanningDataSets = false;
                        if (sets > 0) {
                            PersistUtils.saveAnObject(datasets, MFM_DATA_SETS_FILE);
                        }
                    }
                }
            };
            scanDataSets.start();
        }
    }

    @SuppressWarnings("all")
    private void loadData(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            long millis = System.currentTimeMillis();
            if (MFM.isSystemDebug()) {
                System.out.println("\nMAME load starting: " + new Date(millis));
            }

            // Wait for write to Zip if we just Parsed
            while (MFM_Data.getInstance().isPersisting()) {
            }

            mame = (Mame) PersistUtils.retrieveJAXBfromZip(filePath, MFM_MAME_XML, Mame.class);
            softwarelists = (Softwarelists) PersistUtils.retrieveJAXBfromZip(filePath,
                    MFM_SOFTWARELISTS_XML, Softwarelists.class);

            if (MFM.isSystemDebug()) {
                System.out.println("MAME load took: " +
                        TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - millis));
            }

            try {
                permData = (HashMap<String, Object>) PersistUtils.loadAnObjectFromZip(filePath, MFM_CACHE_SER);
            } catch (Exception e) {
                e.printStackTrace();
            }
            loaded = true;
        }
    }

    @SuppressWarnings("all")
    public void loadDataSet(String dataSet) {
        if (dataSet == null) {
            return;
        }
        final MFM_Data_Sets.Data_Set dataSet1 = datasets.getDataSet(dataSet);
        if (dataSet1 == null) {
            rescanSets();
            while (isScanningDataSets()) {
            }
            final MFM_Data_Sets.Data_Set dataSet2 = datasets.getDataSet(dataSet);
            if (dataSet2 == null) {
                // If there are Data Set(s) available pick one
                if (!datasets.getAvailableVersions().isEmpty()) {
                    MFMAction pickDataSet = new MFMAction(MFMAction.LOAD_DATA_SET, null);
                    pickDataSet.actionPerformed(
                            new ActionEvent(this, ActionEvent.ACTION_PERFORMED, MFMAction.LOAD_DATA_SET));
                } else {
                    MFM.getLogger().addToList("Process finished with exit code 6", true);
                    MFM.exit(6);
                }
            }
            loadData(dataSet2.getFilePath());
        } else {
            loadData(dataSet1.getFilePath());
        }
        setDataVersion(dataSet);
    }

    public void loadDataSet(String dataSet, boolean showProgress) {
        if (showProgress) {
            MFMUI.showBusy(true, true);
        }
        loadDataSet(dataSet);
    }

    File getControllerLabelsFile() {
        return controllersLabelsFile;
    }

    File getFolderNamesFile() {
        return folderNamesFile;
    }

    final void persistUserInis(Map<String, Map<String, String>> inifiles) {
        PersistUtils.saveAnObjectXML(inifiles, MFM_USER_INI_FILE);
    }

    final Object getUserInis() {
        try {
            if (Paths.get(MFM_USER_INI_FILE).toFile().exists()) {
                return PersistUtils.loadAnObjectXML(MFM_USER_INI_FILE);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    final void setObject(String key, Object obj) {
        settings.put(key, obj);
        persistSettings();
    }

    final Object getObject(String key) {
        return settings.get(key);
    }

    final void setStaticData(String key, Object obj) {
        if (MFM.isSystemDebug()) {
            System.out.println("setStaticData : " + key);
        }
        permData.put(key, obj);
        setStaticChanged(true);
    }

    final Object getStaticData(String key) {
        if (MFM.isSystemDebug()) {
            System.out.println("Get static data " + key);
        }
        return permData.get(key);
    }

    public boolean isStaticChanged() {
        return staticChanged;
    }

    private void setStaticChanged(boolean staticChanged) {
        this.staticChanged = staticChanged;
    }

    public Map<String, Object> getPermanentData() {
        return permData;
    }

    public Mame getMame() {
        return mame;
    }

    public void setMame(Mame mame) {
        this.mame = mame;
        MFMSettings.getInstance().generateDataVersion(mame.getBuild());
        setDataVersion(MFMSettings.getInstance().getDataVersion());
    }

    public Softwarelists getSoftwarelists() {
        return softwarelists;
    }

    public void setSoftwarelists(Softwarelists softwarelists) {
        this.softwarelists = softwarelists;
    }

    private void setDataVersion(String dataVersion1) {
        dataVersion = dataVersion1;
    }

    public String getDataVersion() {
        return dataVersion;
    }

    public String getPublishableDataVersion() {
        return dataVersion.contains(MFMSettings.ALL_UNDERSCORE) ? dataVersion :
                MFMSettings.PLAYABLE_UNDERSCORE + dataVersion;
    }

    public String[] getDataSets() {
        TreeSet<String> versions = datasets.getAvailableVersions();
        return versions.toArray(new String[0]);
    }

    void persistSettings() {
        PersistUtils.saveAnObjectXML(settings, MFM.getMfmSettingsDir() + MFM.MFM_SETTINGS_FILE);
    }

    void reset() {
        permData = new HashMap<>();
        mame = null;
    }

    public String rescanSets() {

        try {
            SwingWorker<String, String> sw = new SwingWorker<String, String>() {
                @SuppressWarnings("RedundantThrows")
                @Override
                protected String doInBackground() throws Exception {
                    datasets.rescanSets();
                    PersistUtils.saveAnObject(datasets, MFM_DATA_SETS_FILE);
                    return "Data Sets scan completed.";
                }
            };
            sw.execute();
            return sw.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return "Data Scan failed.";
        }
    }

    public boolean hasDataSet(String dataSet) {
        return datasets.getDataSet(dataSet)!=null;
    }

    // Nested classes to just keep all the logic and data related storage in the same place
    private static final class MFM_Data_Sets implements Serializable {
        private static final long serialVersionUID = 6923846781578691002L;

        transient Path dataDirPath = Paths.get(MFM.getMfmDataDir());
        transient PathMatcher filter =
                dataDirPath.getFileSystem().getPathMatcher("glob:**/MFM_MAME*.zip");
        private HashMap<String, Data_Set> dataSets;
        private boolean scanning = false;

        MFM_Data_Sets() {
        }

        @SuppressWarnings("all")
        TreeSet<String> getAvailableVersions() {
            while (scanning) {
            }
            return new TreeSet<>(dataSets.keySet());
        }

        Data_Set getDataSet(String key) {
            return dataSets.get(key);
        }

        boolean containsDataSet(String version) {
            return dataSets.containsKey(version);
        }

        private void rescanSets() {
            if (newSets()) {
                scanSets();
            }
        }

        private void scanSets() {
            scanning = true;
            try {
                dataSets = new HashMap<>();
                Files.find(dataDirPath, 5, (filePath, fileAttr) -> fileAttr.isRegularFile())
                        .filter(filter::matches)
                        .forEach(this::createDataSet); // OK so how do you ensure closure of Files.find?
            } catch (IOException e) {
                e.printStackTrace();
            }
            scanning = false;
        }

        /**
         * @return true if cached sets is not all
         */
        private boolean newSets() {
            long setsNumber = 0;
            try {
                setsNumber = Files.find(dataDirPath, 5, (filePath, fileAttr) -> fileAttr.isRegularFile())
                        .filter(filter::matches)
                        .count(); // OK so how do you ensure closure of Files.find?
            } catch (IOException e) {
                e.printStackTrace();
            }
            return dataSets.size() != setsNumber;
        }

        private void createDataSet(Path path) {
            Data_Set dataSet = new Data_Set(getVersion(path.getFileName().toString()), path.toString(),
                    calculateSHA1(path));
            dataSets.put(dataSet.getVersion(), dataSet);
        }

        private String getVersion(String path) {
            // example name is MFM_MAME_0.180.zip
            return path.substring(9, path.lastIndexOf('.'));
        }

        private String calculateSHA1(Path path) {
            return Hasher.getSHA1(path.toFile());
        }

        /**
         * @param in the ObjectInputStream
         * @throws IOException            if inputstream throws it
         * @throws ClassNotFoundException if the class isn't found
         */
        private void readObject(java.io.ObjectInputStream in)
                throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            dataDirPath = Paths.get(MFM.getMfmDataDir());
            filter = dataDirPath.getFileSystem().getPathMatcher("glob:**/MFM_MAME*.zip");
        }

        final class Data_Set implements Serializable {
            private static final long serialVersionUID = -2151579106718487671L;

            // Version is extracted or equal to the fileName substring '0.180' or 'ALL_0.180'
            private String version;
            private String filePath;
            private String fileSHA1;

            Data_Set(String version, String filePath, String fileSHA1) {
                this.version = version;
                this.filePath = filePath;
                this.fileSHA1 = fileSHA1;
            }

            /**
             * For User created Data Sets
             *
             * @param name     Data Set name
             * @param version  MAME version for this data set
             * @param filePath Path to this Data Sets file
             * @param fileSHA1 File's SHA1
             */
            Data_Set(String name, String version, String filePath, String fileSHA1) {
                this.version = name + ':' + version;
                this.filePath = filePath;
                this.fileSHA1 = fileSHA1;
            }

            public String getVersion() {
                return version;
            }

            String getFilePath() {
                return filePath;
            }

            public String getFileSHA1() {
                return fileSHA1;
            }
        }
    }
}
