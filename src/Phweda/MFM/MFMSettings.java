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

import Phweda.MFM.UI.MFMAction;
import Phweda.MFM.UI.MFM_Components;
import Phweda.MFM.Utils.MFMFileOps;
import Phweda.utils.FileUtils;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/22/11
 * Time: 2:19 PM
 */
//TODO fixme mixed static with singleton
public class MFMSettings {
    private static MFMSettings ourInstance = null;

    private static HashMap<String, String> fullSetExtrasDirectories;
    private static HashMap<String, String> playSetDirectories;
    // key == extras folder name, value zip file path
    private static HashMap<String, File> extrasZipFilesMap; // Added 9/2016 to handle zipped extras
    private static HashMap<String, String> extrasZips;

    private static boolean exeChanged = false;
    private static boolean fullXMLcompatible = false; // hopefully guaranteed to work with old code
    private static boolean loaded = false;
    // Special case logic
    private static final double DBL_143 = 143d;// removed the decimal for correct comparison e.g. 0.70 is > 0.143
    public static final String ALL_ = "ALL_";
    // Use this guy to persist
    private static HashMap<String, Object> mfmSettings;

    public static MFMSettings getInstance() {
        if (ourInstance == null) {
            ourInstance = new MFMSettings();
            ourInstance.loadSettings();
        }
        return ourInstance;
    }

    TreeMap<String, String> getResourceRoots() {
        TreeMap<String, String> roots = new TreeMap<String, String>();

        if (mfmSettings.get(MFM_Constants.ROMS_FULL_SET_DIRECTORY) != null) {
            roots.put(MFM_Constants.ROMS_FULL_SET_DIRECTORY,
                    (String) mfmSettings.get(MFM_Constants.ROMS_FULL_SET_DIRECTORY));
        }

        if (mfmSettings.get(MFM_Constants.CHDS_FULL_SET_DIRECTORY) != null) {
            roots.put(MFM_Constants.CHDS_FULL_SET_DIRECTORY,
                    (String) mfmSettings.get(MFM_Constants.CHDS_FULL_SET_DIRECTORY));
        }

        if (mfmSettings.get(MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY) != null) {
            roots.put(MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY,
                    (String) mfmSettings.get(MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY));
        }

        if (mfmSettings.get(MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY) != null) {
            roots.put(MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY,
                    (String) mfmSettings.get(MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY));
        }

        if (mfmSettings.get(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY) != null) {
            roots.put(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY,
                    (String) mfmSettings.get(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY));
        }

        if (mfmSettings.get(MFM_Constants.MAME_VIDS_DIRECTORY) != null) {
            roots.put(MFM_Constants.MAME_VIDS_DIRECTORY,
                    (String) mfmSettings.get(MFM_Constants.MAME_VIDS_DIRECTORY));
        }
        return roots;
    }

    public String getPlaySetDir() {
        return (String) mfmSettings.get(MFM_Constants.PLAYSET_ROOT_DIRECTORY);
    }

    public void setPlaySetDir(String playSetDir) {
        mfmSettings.put(MFM_Constants.PLAYSET_ROOT_DIRECTORY, playSetDir);
    }

    public String VIDsFullSetDir() {
        return (String) mfmSettings.get(MFM_Constants.MAME_VIDS_DIRECTORY);
    }

    public void VIDsFullSetDir(String VIDsFullSetDir) {
        mfmSettings.put(MFM_Constants.MAME_VIDS_DIRECTORY, VIDsFullSetDir);
    }

    public String VDubexe() {
        return (String) mfmSettings.get(MFM_Constants.VIRTUALDUB_EXE);
    }

    public void VDubexe(String VDubdir) {
        mfmSettings.put(MFM_Constants.VIRTUALDUB_EXE, VDubdir);
    }

    public String FFMPEGexe() {
        return (String) mfmSettings.get(MFM_Constants.FFMPEG_EXE);
    }

    public void FFMPEGexe(String FFmpegEXE) {
        mfmSettings.put(MFM_Constants.FFMPEG_EXE, FFmpegEXE);
    }

    public String FFmpegEXEdir() {
        return (String) mfmSettings.get(MFM_Constants.FFMPEG_EXE_DIRECTORY);
    }

    public void FFmpegEXEdir(String FFmpegEXEdir) {
        mfmSettings.put(MFM_Constants.FFMPEG_EXE_DIRECTORY, FFmpegEXEdir);
    }

    public String FFmpegInputFolder() {
        return (String) mfmSettings.get(MFM_Constants.FFmpeg_INPUT_FOLDER);
    }

    public void FFmpegInputFolder(String FFmpegInputFolder) {
        mfmSettings.put(MFM_Constants.FFmpeg_INPUT_FOLDER, FFmpegInputFolder);
    }

    public String FFmpegOutputFolder() {
        return (String) mfmSettings.get(MFM_Constants.FFmpeg_OUTPUT_FOLDER);
    }

    public void FFmpegOutputFolder(String FFmpegOutputFolder) {
        mfmSettings.put(MFM_Constants.FFmpeg_OUTPUT_FOLDER, FFmpegOutputFolder);
    }

    public String MAMEexeDir() {
        return (String) mfmSettings.get(MFM_Constants.MAME_EXE_DIRECTORY);
    }

    public void MAMEexeDir(String MAMEexeDir) {
        mfmSettings.put(MFM_Constants.MAME_EXE_DIRECTORY, MAMEexeDir);
    }

    private void initMySettings() {
        mfmSettings = new HashMap<String, Object>(15);
        MFMFontSize(MFM_Constants.NORMAL);
    }

    public String MAMEexeName() {
        return (String) mfmSettings.get(MFM_Constants.MAME_EXE_NAME);
    }

    public void MAMEexeName(String MAMEexeName) {
        exeChanged = true;
        mfmSettings.put(MFM_Constants.MAME_EXE_NAME, MAMEexeName);
    }

    public String fullMAMEexePath() {
        return MAMEexeDir() + FileUtils.DIRECTORY_SEPARATOR + MAMEexeName();
    }

    public String RomsFullSetDir() {
        return (String) mfmSettings.get(MFM_Constants.ROMS_FULL_SET_DIRECTORY);
    }

    public void RomsFullSetDir(String romsFullSetDir) {
        mfmSettings.put(MFM_Constants.ROMS_FULL_SET_DIRECTORY, romsFullSetDir);
    }

    public void nonMerged(boolean mergedIn) {
        mfmSettings.put(MFM_Constants.NONMERGED, mergedIn);
    }

    public boolean isnonMerged() {
        return mfmSettings.get(MFM_Constants.NONMERGED) != null && (boolean) mfmSettings.get(MFM_Constants.NONMERGED);
    }

    public String CHDsFullSetDir() {
        return (String) mfmSettings.get(MFM_Constants.CHDS_FULL_SET_DIRECTORY);
    }

    public void CHDsFullSetDir(String CHDsFullSetDir) {
        mfmSettings.put(MFM_Constants.CHDS_FULL_SET_DIRECTORY, CHDsFullSetDir);
    }

    public String SoftwareListRomsFullSetDir() {
        return (String) mfmSettings.get(MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY);
    }

    public void SoftwareListRomsFullSetDir(String SoftwareListROMsFullSetDir) {
        mfmSettings.put(MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY, SoftwareListROMsFullSetDir);
    }

    public String SoftwareListCHDsFullSetDir() {
        return (String) mfmSettings.get(MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY);
    }

    public void SoftwareListCHDsFullSetDir(String SoftwareListCHDsFullSetDir) {
        mfmSettings.put(MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY, SoftwareListCHDsFullSetDir);
    }

    public String getExtrasFullSetDir() {
        return (String) mfmSettings.get(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY);
    }

    public void setExtrasFullSetDir(String extrasFullSetDir) {
        mfmSettings.put(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY, extrasFullSetDir);
    }

    public String getHistoryDAT() {
        return (String) mfmSettings.get(MFM_Constants.HISTORY_DAT_FILE);
    }

    private void setHistoryDAT(String historyDAT) {
        mfmSettings.put(MFM_Constants.HISTORY_DAT_FILE, historyDAT);
    }

    public String getMAMEInfoDAT() {
        return (String) mfmSettings.get(MFM_Constants.MAMEINFO_DAT_FILE);
    }

    private void setMAMEInfoDAT(String MAMEInfoDAT) {
        mfmSettings.put(MFM_Constants.MAMEINFO_DAT_FILE, MAMEInfoDAT);
    }

    public String getCatverINI() {
        return (String) mfmSettings.get(MFM_Constants.CATVER_INI_FILE);
    }

    private void setCatverINI(String catverINI) {
        mfmSettings.put(MFM_Constants.CATVER_INI_FILE, catverINI);
    }

    public HashMap<String, File> getExtrasZipFilesMap() {
        extrasZipFilesMap = new HashMap<String, File>();
        for (String key : extrasZips.keySet()) {
            extrasZipFilesMap.put(key, new File(extrasZips.get(key)));
        }
        return extrasZipFilesMap;
    }

    String getMAMEFoldersDir() {
        // TODO fixme me. How are we getting here with a null? We got here before it was set?
        String answer = (String) mfmSettings.get(MFM_Constants.MAME_FOLDER_DIRECTORY);
        return (answer == null) ? "" : answer;
    }

    private void setMAMEFoldersDir(String MAMEFoldersDir) {
        if (MAMEFoldersDir != null) {
            mfmSettings.put(MFM_Constants.MAME_FOLDER_DIRECTORY, MAMEFoldersDir);
        } else {
            mfmSettings.put(MFM_Constants.MAME_FOLDER_DIRECTORY, "");
        }
    }

    public String MFMFontSize() {
        if (mfmSettings == null) {
            return MFM_Constants.NORMAL;
        }
        return (String) mfmSettings.get(MFM_Constants.FONTSIZE);
    }

    public void MFMFontSize(String MFMFontSize) {
        mfmSettings.put(MFM_Constants.FONTSIZE, MFMFontSize);
    }

    public String MFMCurrentList() {
        return (String) mfmSettings.get(MFM_Constants.CURRENTLIST);
    }

    public void MFMCurrentList(String MFMCurrentList) {
        mfmSettings.put(MFM_Constants.CURRENTLIST, MFMCurrentList);
    }

    public String MFMLookAndFeel() {
        return (String) mfmSettings.get(MFM_Constants.LOOKANDFEEL);
    }

    public void MFMLookAndFeel(String MFMLookAndFeel) {
        mfmSettings.put(MFM_Constants.LOOKANDFEEL, MFMLookAndFeel);
    }

    public HashMap<String, String> FullSetDirectories() {
        return fullSetExtrasDirectories;
    }

    public Integer selectedTab() {
        return (Integer) mfmSettings.get(MFM_Constants.SELECTEDTAB);
    }

    public void selectedTab(Integer index) {
        mfmSettings.put(MFM_Constants.SELECTEDTAB, index);
    }

    public HashMap<String, String> PlaySetDirectories() {
        return playSetDirectories;
    }

    public void isLoaded(boolean bool) {
        loaded = bool;
    }

    /*
    * We require these folders to exist. Create them if missing
    */
    private void mkDirs() {
        String[] folders = MFM_Constants.MAME_FOLDER_NAMES_ARRAY;

        for (String folderName : folders) {
            // If it is null create it unless it is ini - that are optional
            if (playSetDirectories.get(folderName) == null || !folderName.equals("ini")) {
                if (folderName.equals("roms") || folderName.equals("chds")) {
                    File newDir = new File(getPlaySetDir() + FileUtils.DIRECTORY_SEPARATOR + folderName);
                    if (!newDir.exists()) {
                        boolean success = newDir.mkdir();
                        if (!success) {
                            MFM.logger.addToList("FAILED to create directory: " + newDir.getAbsolutePath());
                        }
                    }
                    playSetDirectories.put(folderName, newDir.getAbsolutePath());
                } else if (!folderName.equals("ini") && fullSetExtrasDirectories.containsKey(folderName)) {
                    File newDir = new File(getPlaySetDir() + FileUtils.DIRECTORY_SEPARATOR + folderName);
                    if (!newDir.exists()) {
                        boolean success = newDir.mkdir();
                        if (!success) {
                            MFM.logger.addToList("FAILED to create directory: " + newDir.getAbsolutePath());
                        }
                    }
                    playSetDirectories.put(folderName, newDir.getAbsolutePath());
                }
            }
        }
    }

    public String getMESSInfoDAT() {
        return (String) mfmSettings.get(MFM_Constants.MESSINFO_DAT_FILE);
    }

    private void setMESSInfoDAT(String MESSInfoDAT) {
        mfmSettings.put(MFM_Constants.MESSINFO_DAT_FILE, MESSInfoDAT);
    }

    public String getSYSInfoDAT() {
        return (String) mfmSettings.get(MFM_Constants.SYSINFO_DAT_FILE);
    }

    private void setSYSInfoDAT(String SYSInfoDAT) {
        mfmSettings.put(MFM_Constants.SYSINFO_DAT_FILE, SYSInfoDAT);
    }

    public String getnPlayerINI() {
        return (String) mfmSettings.get(MFM_Constants.NPLAYERS_INI_FILE);
    }

    private void setnPlayerINI(String nPlayerINI) {
        mfmSettings.put(MFM_Constants.NPLAYERS_INI_FILE, nPlayerINI);
    }

    String getLanguageINI() {
        return (String) mfmSettings.get(MFM_Constants.LANGUAGES_INI_FILE);
    }

    private void setLanguageINI(String languagesINI) {
        mfmSettings.put(MFM_Constants.LANGUAGES_INI_FILE, languagesINI);
    }

    public String getFFmpegMoveAVItoFolder() {
        return (String) mfmSettings.get(MFM_Constants.FFmpeg_MoveAVIto_Folder);
    }

    public void setFFmpegMoveAVItoFolder(String folder) {
        mfmSettings.put(MFM_Constants.FFmpeg_MoveAVIto_Folder, folder);
    }

    HashMap<String, String> getFullSetExtrasDirectories() {
        return fullSetExtrasDirectories;
    }

    public HashMap<String, String> getPlaySetDirectories() {
        return playSetDirectories;
    }

    public String getMAMEVersion() {
        return (String) mfmSettings.get(MFM_Constants.MAME_EXE_VERSION);
    }

    /**
     * Updated to handle oldest versions
     *
     * @param version
     * @return
     */
    private String trimMAMEVersion(String version) {
        if (version.contains("M.A.M.E.") || version.contains("MAME")) {
            int start = version.indexOf('v');
            return version.substring(start + 1, start + 6).trim();
        }
        return version.contains("(") ? version.substring(0, version.indexOf('(')).trim() : version.trim();
    }

    public String getDataVersion() {
        return (String) mfmSettings.get(MFM_Constants.DATA_VERSION);
    }

    public void setDataVersion(String dataVersion) {
        mfmSettings.put(MFM_Constants.DATA_VERSION, dataVersion);
    }

    public void generateDataVersion(String dataVersion) {
        if (MFM.isSystemDebug()) {
            System.out.println("MFMSettings.setDataVersion dataVersion IN is: " + dataVersion);
        }
        dataVersion = trimMAMEVersion(dataVersion);
        // strip *. for Double comparison
        Double dataVersionDouble = Double.valueOf(dataVersion.substring(dataVersion.indexOf('.') + 1));

        if (!dataVersion.contains(ALL_) && (MFM.isProcessAll() || Double.valueOf(dataVersionDouble) <= DBL_143)) {
            mfmSettings.put(MFM_Constants.DATA_VERSION, ALL_ + dataVersion);
        } else {
            mfmSettings.put(MFM_Constants.DATA_VERSION, dataVersion);
        }
        if (MFM.isSystemDebug()) {
            System.out.println("MFMSettings.setDataVersion dataVersion OUT is: " + dataVersion);
        }
    }

    // Supports parsing
    boolean isPreMAME143exe() {
        String version = (String) mfmSettings.get(MFM_Constants.MAME_EXE_VERSION);
        // Not sure we need this but ...
        if (version == null || version.trim().isEmpty()) {
            setMAMEexeVersion();
            version = (String) mfmSettings.get(MFM_Constants.MAME_EXE_VERSION);
        }
        version = version.substring(version.indexOf('.') + 1);// remove "0." for comparison
        // Edge 'bad user' case
        try {
            return Double.valueOf(version) <= DBL_143;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            MFM.logger.out("Version is not a number : " + version);
        }
        return false;
    }

    public boolean isFullXMLcompatible() {
        return (boolean) mfmSettings.get(MFM_Constants.COMPATIBLE);
    }

    public void persistMySettings() {
        if (exeChanged) {
            // Set MAME exe for running
            MAMEexe.setBaseArgs(this.fullMAMEexePath());

            // get EXE version
            setMAMEexeVersion();
            exeChanged = false;
        }
        MFM_Data.getInstance().setObject(MFM_Constants.MFM_SETTINGS, mfmSettings);
    }

    public boolean isLoaded() {
        return loaded;
    }

    @SuppressWarnings("unchecked")
    private void loadSettings() {
        MFM_Data data = MFM_Data.getInstance();
        mfmSettings = (HashMap<String, Object>) data.getObject(MFM_Constants.MFM_SETTINGS);
        //
        if (mfmSettings == null || mfmSettings.size() <= 2) {
            MFM.logger.separateLine();
            MFM.logger.addToList("NO SETTINGS FOUND", true);
            MFM.logger.separateLine();
            initMySettings();
        }

        // Need a better check than this - TODO test this thing is it correct just look for settings
        if ((mfmSettings.size() <= 1) && (getPlaySetDir() != null && RomsFullSetDir() != null)) {
            updateDirectoriesResourceFiles();
            loaded = true;
        } else if (getPlaySetDir() != null && RomsFullSetDir() != null) {
            fullSetExtrasDirectories = (HashMap<String, String>) mfmSettings.get(
                    MFM_Constants.FULL_SET_DIRECTORIES_MAP);
            playSetDirectories = (HashMap<String, String>) mfmSettings.get(MFM_Constants.PLAY_SET_DIRECTORIES_MAP);
            extrasZips = (HashMap<String, String>) mfmSettings.get(MFM_Constants.EXTRAS_ZIPS);
            loaded = true;
        }

        // Get version first run special case
        if (this.getDataVersion() == null) {
            // First run special case
            // Wait for Data Set scan
            while (MFM_Data.isScanningDataSets()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Zero and 1 are special cases handled by MFMController
            if (MFM_Data.getInstance().getDataSets().length == 0) {
                System.out.println("NO DATA SETS");
            } else if (MFM_Data.getInstance().getDataSets().length > 1) {
                setDataVersion(pickVersion());
            }
        }
    }

    public String pickVersion() {
        return MFM_Components.dataSetPicker();
    }

    public void updateDirectoriesResourceFiles() {

        fullSetExtrasDirectories = new HashMap<String, String>();
        fullSetExtrasDirectories.putAll(MFMFileOps.findMAMEdirectories(Paths.get(getExtrasFullSetDir()),
                MFM_Constants.MAME_FOLDER_NAMES_ARRAY));
        // Add it for persistence
        mfmSettings.put(MFM_Constants.FULL_SET_DIRECTORIES_MAP, fullSetExtrasDirectories);

        playSetDirectories = MFMFileOps.findMAMEdirectories(Paths.get(getPlaySetDir()),
                MFM_Constants.MAME_FOLDER_NAMES_ARRAY);
        // Add it for persistence
        mfmSettings.put(MFM_Constants.PLAY_SET_DIRECTORIES_MAP, playSetDirectories);

        setMAMEFoldersDir(playSetDirectories.get(MFM_Constants.FOLDERS));

        setHistoryDAT(MFMFileOps.findMAMEfile(Paths.get(getExtrasFullSetDir()),
                Paths.get(MFM_Constants.HISTORY_DAT_FILENAME)));

        setMAMEInfoDAT(MFMFileOps.findMAMEfile(Paths.get(getExtrasFullSetDir()),
                Paths.get(MFM_Constants.MAMEINFO_DAT_FILENAME)));

        setMESSInfoDAT(MFMFileOps.findMAMEfile(Paths.get(getExtrasFullSetDir()),
                Paths.get(MFM_Constants.MESSINFO_DAT_FILENAME)));

        setSYSInfoDAT(MFMFileOps.findMAMEfile(Paths.get(getExtrasFullSetDir()),
                Paths.get(MFM_Constants.SYSINFO_DAT_FILENAME)));

        extrasZips = findExtrasZips();
        mfmSettings.put(MFM_Constants.EXTRAS_ZIPS, extrasZips);

        // TODO change the logic
        // going forward it should be: Extras, Playset folders, MFM folders
        try {   // Get the categories file
            if (playSetDirectories.get(MFM_Constants.FOLDERS) != null &&
                    FileUtils.fileExists(playSetDirectories.get(MFM_Constants.FOLDERS),
                            MFM_Constants.CATVER_FULL_INI_FILENAME)) {
                setCatverINI(MFMFileOps.findMAMEfile(Paths.get(playSetDirectories.get(MFM_Constants.FOLDERS)),
                        Paths.get(MFM_Constants.CATVER_FULL_INI_FILENAME)));
            } else if (playSetDirectories.get(MFM_Constants.FOLDERS) != null &&
                    FileUtils.fileExists(playSetDirectories.get(MFM_Constants.FOLDERS),
                            MFM_Constants.CATVER_INI_FILENAME)) {
                setCatverINI(MFMFileOps.findMAMEfile(Paths.get(playSetDirectories.get(MFM_Constants.FOLDERS)),
                        Paths.get(MFM_Constants.CATVER_INI_FILENAME)));
            } else {
                setCatverINI(MFM.MFM_FOLDERS_DIR + MFM_Constants.CATVER_INI_FILENAME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {   // Get the nplayers file
            if (playSetDirectories.get(MFM_Constants.FOLDERS) != null &&
                    FileUtils.fileExists(playSetDirectories.get(MFM_Constants.FOLDERS),
                            MFM_Constants.NPLAYERS_INI_FILENAME)) {
                setnPlayerINI(MFMFileOps.findMAMEfile(Paths.get(playSetDirectories.get(MFM_Constants.FOLDERS)),
                        Paths.get(MFM_Constants.NPLAYERS_INI_FILENAME)));
            } else {
                setnPlayerINI(MFM.MFM_FOLDERS_DIR + MFM_Constants.NPLAYERS_INI_FILENAME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {   // Get the languages file
            if (playSetDirectories.get(MFM_Constants.FOLDERS) != null &&
                    FileUtils.fileExists(playSetDirectories.get(MFM_Constants.FOLDERS),
                            MFM_Constants.LANGUAGES_INI_FILENAME)) {
                setLanguageINI(MFMFileOps.findMAMEfile(Paths.get(playSetDirectories.get(MFM_Constants.FOLDERS)),
                        Paths.get(MFM_Constants.LANGUAGES_INI_FILENAME)));
            } else {
                setLanguageINI(MFM.MFM_FOLDERS_DIR + MFM_Constants.LANGUAGES_INI_FILENAME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mkDirs();
        persistMySettings();
/*
            }
        };
        thread.start();
*/
    }

    private HashMap<String, String> findExtrasZips() {
        HashMap<String, String> map = new HashMap<String, String>();
        File extrasDir = new File(this.getExtrasFullSetDir());
        if (extrasDir.exists()) {
            TreeSet<String> extrasFolderNames =
                    new TreeSet<String>(Arrays.asList(MFM_Constants.MAME_FOLDER_NAMES_ARRAY));
            File[] files = extrasDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    if (file.isFile() && fileName.endsWith(FileUtils.ZIPSUFFIX)
                            && extrasFolderNames.contains(fileName.substring(0, fileName.lastIndexOf(".")))) {
                        map.put(fileName.substring(0, fileName.lastIndexOf(".")), file.getAbsolutePath());
                    }
                }
            }
        }
        return map;
    }

    private void setMAMEexeVersion() {
        // Seems newer MAME has the (build date) and older do not
        String MAMEVersion = trimMAMEVersion(MAMEexe.getMAMEexeVersion());
        mfmSettings.put(MFM_Constants.MAME_EXE_VERSION, MAMEVersion);

        if (MFM.isSystemDebug()) {
            System.out.println(MAMEVersion);
        }
        // Throw event to update UI
        final MFMAction updateVersion = new MFMAction(MFMAction.UpdateVersionAction, null);
        updateVersion.actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, MFMAction.UpdateVersionAction));
    }

    /**
     * Parse version to split on 172/173 - -listxml changed there and with going to full XML import
     * we need to differentiate for method of data extraction and access
     *
     * @Deprecated 0.85 no longer needed
     */
    @Deprecated
    private void checkFullXMLCompatible() {
        fullXMLcompatible = MAME_Compatible.versionNew(getMAMEVersion());
        mfmSettings.put(MFM_Constants.COMPATIBLE, fullXMLcompatible);
    }
}
