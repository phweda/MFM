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

import Phweda.MFM.UI.MFMAction;
import Phweda.MFM.UI.MFMUI;
import Phweda.MFM.UI.MFM_Components;
import Phweda.MFM.Utils.MFMFileOps;
import Phweda.utils.FileUtils;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/22/11
 * Time: 2:19 PM
 */
@SuppressWarnings({"squid:S00117", "squid:S00100", "WeakerAccess"})
public class MFMSettings {
    private static MFMSettings ourInstance = null;

    private HashMap<String, String> fullSetExtrasDirectories;
    private HashMap<String, String> playSetDirectories;
    private HashMap<String, String> extrasZips;
    private HashMap<String, File> extrasZipFilesMap;

    private boolean exeChanged = false;
    private boolean loaded = false;
    // Special case logic
    private static final double DBL_143 = 143d;// removed the decimal for correct comparison e.g. 0.70 is > 0.143
    public static final String ALL_UNDERSCORE = "ALL_";
    public static final String PLAYABLE = "Playable";
    public static final String PLAYABLE_UNDERSCORE = "Playable_";

    // Use this guy to persist
    private HashMap<String, Object> ourSettings = new HashMap<>(15);

    public static MFMSettings getInstance() {
        if (ourInstance == null) {
            ourInstance = new MFMSettings();
            ourInstance.loadSettings();
        }
        return ourInstance;
    }

    TreeMap<String, String> getResourceRoots() {
        TreeMap<String, String> roots = new TreeMap<>();

        if (ourSettings.get(MFM_Constants.ROMS_FULL_SET_DIRECTORY) != null) {
            roots.put(MFM_Constants.ROMS_FULL_SET_DIRECTORY,
                    (String) ourSettings.get(MFM_Constants.ROMS_FULL_SET_DIRECTORY));
        }

        if (ourSettings.get(MFM_Constants.CHDS_FULL_SET_DIRECTORY) != null) {
            roots.put(MFM_Constants.CHDS_FULL_SET_DIRECTORY,
                    (String) ourSettings.get(MFM_Constants.CHDS_FULL_SET_DIRECTORY));
        }

        if (ourSettings.get(MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY) != null) {
            roots.put(MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY,
                    (String) ourSettings.get(MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY));
        }

        if (ourSettings.get(MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY) != null) {
            roots.put(MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY,
                    (String) ourSettings.get(MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY));
        }

        if (ourSettings.get(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY) != null) {
            roots.put(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY,
                    (String) ourSettings.get(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY));
        }

        if (ourSettings.get(MFM_Constants.MAME_VIDS_DIRECTORY) != null) {
            roots.put(MFM_Constants.MAME_VIDS_DIRECTORY,
                    (String) ourSettings.get(MFM_Constants.MAME_VIDS_DIRECTORY));
        }
        return roots;
    }

    public String getPlaySetDir() {
        return (String) ourSettings.get(MFM_Constants.PLAYSET_ROOT_DIRECTORY);
    }

    public void setPlaySetDir(String playSetDir) {
        ourSettings.put(MFM_Constants.PLAYSET_ROOT_DIRECTORY, playSetDir);
    }

    public String VIDsFullSetDir() {
        return (String) ourSettings.get(MFM_Constants.MAME_VIDS_DIRECTORY);
    }

    public void VIDsFullSetDir(String VIDsFullSetDir) {
        ourSettings.put(MFM_Constants.MAME_VIDS_DIRECTORY, VIDsFullSetDir);
    }

    public String VDubexe() {
        return (String) ourSettings.get(MFM_Constants.VIRTUALDUB_EXE);
    }

    public void VDubexe(String VDubdir) {
        ourSettings.put(MFM_Constants.VIRTUALDUB_EXE, VDubdir);
    }

    public String FFMPEGexe() {
        return (String) ourSettings.get(MFM_Constants.FFMPEG_EXE);
    }

    public void FFMPEGexe(String FFmpegEXE) {
        ourSettings.put(MFM_Constants.FFMPEG_EXE, FFmpegEXE);
    }

    public String FFmpegEXEdir() {
        return (String) ourSettings.get(MFM_Constants.FFMPEG_EXE_DIRECTORY);
    }

    public void FFmpegEXEdir(String FFmpegEXEdir) {
        ourSettings.put(MFM_Constants.FFMPEG_EXE_DIRECTORY, FFmpegEXEdir);
    }

    public String FFmpegInputFolder() {
        return (String) ourSettings.get(MFM_Constants.FFMPEG_INPUT_FOLDER);
    }

    public void FFmpegInputFolder(String FFmpegInputFolder) {
        ourSettings.put(MFM_Constants.FFMPEG_INPUT_FOLDER, FFmpegInputFolder);
    }

    public String FFmpegOutputFolder() {
        return (String) ourSettings.get(MFM_Constants.FFMPEG_OUTPUT_FOLDER);
    }

    public void FFmpegOutputFolder(String FFmpegOutputFolder) {
        ourSettings.put(MFM_Constants.FFMPEG_OUTPUT_FOLDER, FFmpegOutputFolder);
    }

    public String MAMEexeDir() {
        return (String) ourSettings.get(MFM_Constants.MAME_EXE_DIRECTORY);
    }

    public void MAMEexeDir(String MAMEexeDir) {
        ourSettings.put(MFM_Constants.MAME_EXE_DIRECTORY, MAMEexeDir);
    }

    public String MAMEexeName() {
        return (String) ourSettings.get(MFM_Constants.MAME_EXE_NAME);
    }

    public void MAMEexeName(String MAMEexeName) {
        exeChanged = true;
        // How in the hell did I get null mfmsettings here? Original obj GCed? But Settings Panel has a ref??
        if (ourSettings == null) {
            ourSettings = new HashMap<>(15);
        }
        ourSettings.put(MFM_Constants.MAME_EXE_NAME, MAMEexeName);
    }

    public String fullMAMEexePath() {
        return MAMEexeDir() + FileUtils.DIRECTORY_SEPARATOR + MAMEexeName();
    }

    public String RomsFullSetDir() {
        return (String) ourSettings.get(MFM_Constants.ROMS_FULL_SET_DIRECTORY);
    }

    public void RomsFullSetDir(String romsFullSetDir) {
        ourSettings.put(MFM_Constants.ROMS_FULL_SET_DIRECTORY, romsFullSetDir);
    }

    public void nonMerged(boolean mergedIn) {
        ourSettings.put(MFM_Constants.NONMERGED, mergedIn);
    }

    public boolean isnonMerged() {
        return ourSettings.get(MFM_Constants.NONMERGED) != null &&
                Boolean.parseBoolean(ourSettings.get(MFM_Constants.NONMERGED).toString());
    }

    public String CHDsFullSetDir() {
        return (String) ourSettings.get(MFM_Constants.CHDS_FULL_SET_DIRECTORY);
    }

    public void CHDsFullSetDir(String CHDsFullSetDir) {
        ourSettings.put(MFM_Constants.CHDS_FULL_SET_DIRECTORY, CHDsFullSetDir);
    }

    public String SoftwareListRomsFullSetDir() {
        return (String) ourSettings.get(MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY);
    }

    public void SoftwareListRomsFullSetDir(String SoftwareListROMsFullSetDir) {
        ourSettings.put(MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY, SoftwareListROMsFullSetDir);
    }

    public String SoftwareListCHDsFullSetDir() {
        return (String) ourSettings.get(MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY);
    }

    public void SoftwareListCHDsFullSetDir(String SoftwareListCHDsFullSetDir) {
        ourSettings.put(MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY, SoftwareListCHDsFullSetDir);
    }

    public String getExtrasFullSetDir() {
        return (String) ourSettings.get(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY);
    }

    public void setExtrasFullSetDir(String extrasFullSetDir) {
        ourSettings.put(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY, extrasFullSetDir);
    }

    public String getHistoryDAT() {
        return (String) ourSettings.get(MFM_Constants.HISTORY_DAT_FILE);
    }

    private void setHistoryDAT(String historyDAT) {
        ourSettings.put(MFM_Constants.HISTORY_DAT_FILE, historyDAT);
    }

    public String getMAMEInfoDAT() {
        return (String) ourSettings.get(MFM_Constants.MAMEINFO_DAT_FILE);
    }

    private void setMAMEInfoDAT(String MAMEInfoDAT) {
        ourSettings.put(MFM_Constants.MAMEINFO_DAT_FILE, MAMEInfoDAT);
    }

    public String getCatverINI() {
        return (String) ourSettings.get(MFM_Constants.CATVER_INI_FILE);
    }

    private void setCatverINI(String catverINI) {
        ourSettings.put(MFM_Constants.CATVER_INI_FILE, catverINI);
    }

    public boolean isShowXML() {
        return ourSettings.get(MFM_Constants.SHOW_XML) != null &&
                Boolean.valueOf(ourSettings.get(MFM_Constants.SHOW_XML).toString());
    }

    public void setShowXML(boolean showXML) {
        ourSettings.put(MFM_Constants.SHOW_XML, showXML);
    }

    public Map<String, File> getExtrasZipFilesMap() {
        // key == extras folder name, value zip file path
        // Added 9/2016 to handle zipped extras
        if (extrasZipFilesMap == null) {
            extrasZipFilesMap = new HashMap<>();
            for (Map.Entry<String, String> entry : extrasZips.entrySet()) {
                extrasZipFilesMap.put(entry.getKey(), new File(entry.getValue()));
            }
        }
        return extrasZipFilesMap;
    }

    String getMAMEFoldersDir() {
        String answer = (String) ourSettings.get(MFM_Constants.MAME_FOLDER_DIRECTORY);
        return (answer == null) ? "" : answer;
    }

    private void setMAMEFoldersDir(String MAMEFoldersDir) {
        if (MAMEFoldersDir != null) {
            ourSettings.put(MFM_Constants.MAME_FOLDER_DIRECTORY, MAMEFoldersDir);
        } else {
            ourSettings.put(MFM_Constants.MAME_FOLDER_DIRECTORY, "");
        }
    }

    public String MFMFontSize() {
        if (ourSettings == null || ourSettings.get(MFM_Constants.FONTSIZE) == null) {
            return MFM_Constants.NORMAL;
        }
        return (String) ourSettings.get(MFM_Constants.FONTSIZE);
    }

    public void MFMFontSize(String MFMFontSize) {
        ourSettings.put(MFM_Constants.FONTSIZE, MFMFontSize);
    }

    public String MFMCurrentList() {
        return (String) ourSettings.get(MFM_Constants.CURRENTLIST);
    }

    public void MFMCurrentList(String MFMCurrentList) {
        ourSettings.put(MFM_Constants.CURRENTLIST, MFMCurrentList);
    }

    public String MFMLookAndFeel() {
        return (String) ourSettings.get(MFM_Constants.LOOKANDFEEL);
    }

    public void MFMLookAndFeel(String MFMLookAndFeel) {
        ourSettings.put(MFM_Constants.LOOKANDFEEL, MFMLookAndFeel);
    }

    public Map<String, String> FullSetDirectories() {
        return fullSetExtrasDirectories;
    }

    public Integer selectedTab() {
        return (Integer) ourSettings.get(MFM_Constants.SELECTEDTAB);
    }

    public void selectedTab(Integer index) {
        ourSettings.put(MFM_Constants.SELECTEDTAB, index);
    }

    public Map<String, String> PlaySetDirectories() {
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
                if (folderName.equals("roms") || folderName.equals("chds") ||
                        (!folderName.equals("ini") && fullSetExtrasDirectories.containsKey(folderName))) {
                    File newDir = new File(getPlaySetDir() + FileUtils.DIRECTORY_SEPARATOR + folderName);
                    if (!newDir.exists()) {
                        boolean success = newDir.mkdir();
                        if (!success) {
                            MFM.getLogger().addToList("FAILED to create directory: " + newDir.getAbsolutePath());
                        }
                    }
                    playSetDirectories.put(folderName, newDir.getAbsolutePath());
                }
            }
        }
    }

    public String getMESSInfoDAT() {
        return (String) ourSettings.get(MFM_Constants.MESSINFO_DAT_FILE);
    }

    private void setMESSInfoDAT(String MESSInfoDAT) {
        ourSettings.put(MFM_Constants.MESSINFO_DAT_FILE, MESSInfoDAT);
    }

    public String getSYSInfoDAT() {
        return (String) ourSettings.get(MFM_Constants.SYSINFO_DAT_FILE);
    }

    private void setSYSInfoDAT(String SYSInfoDAT) {
        ourSettings.put(MFM_Constants.SYSINFO_DAT_FILE, SYSInfoDAT);
    }

    public String getnPlayerINI() {
        return (String) ourSettings.get(MFM_Constants.NPLAYERS_INI_FILE);
    }

    private void setnPlayerINI(String nPlayerINI) {
        ourSettings.put(MFM_Constants.NPLAYERS_INI_FILE, nPlayerINI);
    }

    String getLanguageINI() {
        return (String) ourSettings.get(MFM_Constants.LANGUAGES_INI_FILE);
    }

    private void setLanguageINI(String languagesINI) {
        ourSettings.put(MFM_Constants.LANGUAGES_INI_FILE, languagesINI);
    }

    public String getFFmpegMoveAVItoFolder() {
        return (String) ourSettings.get(MFM_Constants.FFMPEG_MOVE_AVI_TO_FOLDER);
    }

    public void setFFmpegMoveAVItoFolder(String folder) {
        ourSettings.put(MFM_Constants.FFMPEG_MOVE_AVI_TO_FOLDER, folder);
    }

    HashMap<String, String> getFullSetExtrasDirectories() {
        return fullSetExtrasDirectories;
    }

    public Map<String, String> getPlaySetDirectories() {
        return playSetDirectories;
    }

    public String getMAMEVersion() {
        return (String) ourSettings.get(MFM_Constants.MAME_EXE_VERSION);
    }

    /**
     * Updated to handle oldest versions
     *
     * @param version MAME version
     * @return String version number only
     */
    private String trimMAMEVersion(String version) {
        if (version.contains("M.A.M.E.") || version.contains("MAME")) {
            int start = version.indexOf('v');
            return version.substring(start + 1, start + 6).trim();
        }
        return version.contains("(") ? version.substring(0, version.indexOf('(')).trim() : version.trim();
    }

    public String getDataVersion() {
        return (String) ourSettings.get(MFM_Constants.DATA_VERSION);
    }

    public void setDataVersion(String dataVersion) {
        ourSettings.put(MFM_Constants.DATA_VERSION, dataVersion);
    }

    public void generateDataVersion(String dataVersion) {
        if (MFM.isSystemDebug()) {
            System.out.println("MFMSettings.generateDataVersion dataVersion IN is: " + dataVersion);
        }
        dataVersion = trimMAMEVersion(dataVersion);
        // strip *. for Double comparison
        Double dataVersionDouble = Double.valueOf(dataVersion.substring(dataVersion.indexOf('.') + 1));

        if (!dataVersion.contains(ALL_UNDERSCORE) && (MAMEInfo.isProcessAll() || dataVersionDouble <= DBL_143)) {
            ourSettings.put(MFM_Constants.DATA_VERSION, ALL_UNDERSCORE + dataVersion);
        } else {
            ourSettings.put(MFM_Constants.DATA_VERSION, dataVersion);
        }
        if (MFM.isSystemDebug()) {
            System.out.println("MFMSettings.generateDataVersion dataVersion OUT is: " + dataVersion);
        }
    }

    public boolean isFullXMLcompatible() {
        return (boolean) ourSettings.get(MFM_Constants.COMPATIBLE);
    }

    public void persistMySettings() {
        if (exeChanged) {
            // Set MAME exe for running
            MAMEexe.setBaseArgs(this.fullMAMEexePath());

            // get EXE version
            setMAMEexeVersion();
            exeChanged = false;
        }
        MFM_Data.getInstance().setObject(MFM_Constants.MFM_SETTINGS, ourSettings);
    }

    public boolean isLoaded() {
        return loaded;
    }

    @SuppressWarnings("unchecked")
    private void loadSettings() {
        MFM_Data data = MFM_Data.getInstance();
        ourSettings = (HashMap<String, Object>) data.getObject(MFM_Constants.MFM_SETTINGS);
        //
        if (ourSettings == null || ourSettings.size() <= 2) {
            MFM.getLogger().separateLine();
            MFM.getLogger().addToList("NO SETTINGS FOUND", true);
            MFM.getLogger().separateLine();
            return;
        }

        if ((ourSettings.size() <= 1) && (getPlaySetDir() != null && RomsFullSetDir() != null)) { // TODO figure this out
            updateDirectoriesResourceFiles();
            loaded = true;
        } else if (getPlaySetDir() != null && RomsFullSetDir() != null) {
            fullSetExtrasDirectories = (HashMap<String, String>) ourSettings.get(
                    MFM_Constants.FULL_SET_DIRECTORIES_MAP);
            playSetDirectories = (HashMap<String, String>) ourSettings.get(MFM_Constants.PLAY_SET_DIRECTORIES_MAP);
            extrasZips = (HashMap<String, String>) ourSettings.get(MFM_Constants.EXTRAS_ZIPS);
            loaded = true;
        }

        // Get version first run special case
        if (this.getDataVersion() == null && !MFM.isDoParse()) {
            // First run special case
            // Wait for Data Set scan
            while (MFM_Data.isScanningDataSets()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            // 0 is an error unless bootstrap parse is set
            // 1 is a special cases handled by MFMController
            if (MFM_Data.getInstance().getDataSets().length == 0) {
                System.out.println("NO DATA SETS");
            } else if (MFM_Data.getInstance().getDataSets().length > 1) {
                setDataVersion(pickVersion());
            }
        } else if (MFM.isDoParse()) {
            // NOTE maybe not needed
            if (ourSettings == null) {
                // first run must acquire base settings before continuing
                MFMUI.getSettings();
            }
            MAMEInfo.getInstance(true, true, false);
        }
    }

    public String pickVersion() {
        return MFM_Components.dataSetPicker();
    }

    public void updateDirectoriesResourceFiles() {

        fullSetExtrasDirectories = new HashMap<>();
        fullSetExtrasDirectories.putAll(MFMFileOps.findMAMEdirectories(Paths.get(getExtrasFullSetDir()),
                MFM_Constants.MAME_FOLDER_NAMES_ARRAY));
        // Add it for persistence
        ourSettings.put(MFM_Constants.FULL_SET_DIRECTORIES_MAP, fullSetExtrasDirectories);

        playSetDirectories = MFMFileOps.findMAMEdirectories(Paths.get(getPlaySetDir()),
                MFM_Constants.MAME_FOLDER_NAMES_ARRAY);
        // Add it for persistence
        ourSettings.put(MFM_Constants.PLAY_SET_DIRECTORIES_MAP, playSetDirectories);

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
        ourSettings.put(MFM_Constants.EXTRAS_ZIPS, extrasZips);

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
                setCatverINI(MFM.getMfmFoldersDir() + MFM_Constants.CATVER_INI_FILENAME);
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
                setnPlayerINI(MFM.getMfmFoldersDir() + MFM_Constants.NPLAYERS_INI_FILENAME);
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
                setLanguageINI(MFM.getMfmFoldersDir() + MFM_Constants.LANGUAGES_INI_FILENAME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mkDirs();
        persistMySettings();
    }

    private HashMap<String, String> findExtrasZips() {
        HashMap<String, String> map = new HashMap<>();
        File extrasDir = new File(this.getExtrasFullSetDir());
        if (extrasDir.exists()) {
            TreeSet<String> extrasFolderNames =
                    new TreeSet<>(Arrays.asList(MFM_Constants.MAME_FOLDER_NAMES_ARRAY));
            File[] files = extrasDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    if (file.isFile() && fileName.endsWith(FileUtils.ZIPSUFFIX)
                            && extrasFolderNames.contains(fileName.substring(0, fileName.lastIndexOf('.')))) {
                        map.put(fileName.substring(0, fileName.lastIndexOf('.')), file.getAbsolutePath());
                    }
                }
            }
        }
        return map;
    }

    private void setMAMEexeVersion() {
        // Seems newer MAME has the (build date) and older do not
        String MAMEVersion = trimMAMEVersion(MAMEexe.getMAMEexeVersion());
        ourSettings.put(MFM_Constants.MAME_EXE_VERSION, MAMEVersion);

        if (MFM.isSystemDebug()) {
            System.out.println(MAMEVersion);
        }
        // Throw event to update UI
        final MFMAction updateVersion = new MFMAction(MFMAction.UPDATE_VERSION, null);
        updateVersion.actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, MFMAction.UPDATE_VERSION));
    }
}
