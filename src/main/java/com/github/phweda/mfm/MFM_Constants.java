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
import com.github.phweda.mfm.mame.Machine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/25/11
 * Time: 5:33 PM
 */
@SuppressWarnings("WeakerAccess")
public final class MFM_Constants {

    public static final String SOFTWARE_LISTS = "Software Lists";
    public static final String FILES_ARE_IN_THE_MFM_LISTS_FOLDER = "Files are in the MFM/Lists folder";
    public static final String COVERS_SL = "covers_SL";
    public static final String SNAP_SL = "snap_SL";
    public static final String TITLES_SL = "titles_SL";
    public static final String TITLES = "titles";
    public static final String CABINETS = "cabinets";
    public static final String WORKING = "Working";
    public static final String RUNNABLE = "Runnable ";
    public static final String VERSION = "Version";
    public static final String CREATE_LIST = "Create List"; // Not usable in UI widget bound to xml code
    public static final String CATEGORY = "Category";
    public static final String NULL_STRING = "null";
    public static final String VECTOR = "vector";
    public static final String RASTER = "raster";
    public static final String SPACE_STRING = " ";
    public static final String HYPHEN = "-";
    public static final char SPACE_CHAR = ' ';

    public static final String MAME_EXE_DIRECTORY = "MAMEexeDir";
    public static final String MAME_EXE_NAME = "MAMEexeName";
    public static final String MAME_EXE_VERSION = "MAMEexeVersion";
    public static final String ROMS_FULL_SET_DIRECTORY = "RomsFullSetDir";
    public static final String ROMS_PLAY_SET_DIRECTORY = "RomsPlaySetDir";
    public static final String CHDS_FULL_SET_DIRECTORY = "CHDsFullSetDir";
    public static final String CHDS_PLAY_SET_DIRECTORY = "CHDsPlaySetDir";
    public static final String SOFTWARELIST_ROMS_FULL_SET_DIRECTORY = "SoftwareListROMsFullSetDir";
    public static final String SOFTWARELIST_CHDS_FULL_SET_DIRECTORY = "SoftwareListCHDsFullSetDir";

    public static final String EXTRAS_FULL_SET_DIRECTORY = "ExtrasFullSetDir";
    public static final String PLAYSET_ROOT_DIRECTORY = "PlaySetRootDir";
    // MFM external resource files - catver, languages, and nplayers included with MFM package
    public static final String CATVER_FULL_INI_FILENAME = "catver_full.ini";
    public static final String CATVER_INI_FILENAME = "catver.ini";
    public static final String ARCADE_INI_FILENAME = "arcade.ini"; // added for parsing categories/updating categories map
    public static final String NPLAYERS_INI_FILENAME = "nplayers.ini";
    public static final String LANGUAGES_INI_FILENAME = "languages.ini";
    public static final String MAME_FOLDER_DIRECTORY = "MAMEFoldersDir";
    public static final String MAME_VIDS_DIRECTORY = "MAMEVidsDirectory";
    public static final String VIRTUALDUB_EXE = "VirtualDub exe";
    public static final String FFMPEG_EXE = "ffmpeg exe";
    public static final String FFMPEG_EXE_DIRECTORY = "FFMPEGexeDir";
    public static final String FFMPEG_INPUT_FOLDER = "FFmpeg Input Folder";
    public static final String FFMPEG_OUTPUT_FOLDER = "FFmpeg Output Folder";
    public static final String FFMPEG_MOVE_AVI_TO_FOLDER = "FFmpeg Move AVI to Folder";
    public static final String MFM_SETTINGS = "MFM Settings";
    public static final String NONMERGED = "Non-Merged";
    public static final String FFMPEG_SETTINGS = "FFmpeg Settings";
    public static final String COMPATIBLE = "compatible";
    public static final String DATA_VERSION = "Data version";
    public static final String NEW_LIST = "-NEW-";
    static final char[] ALPHANUM = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };
    public static final String PDF_EXT = ".pdf";
    public static final String PNG_EXT = ".png";
    public static final String FLV_EXT = ".flv";
    public static final String MANUALS = "manuals";
    public static final String MANUALS_SL = "manuals_SL";
    public static final String VERYLARGE = "Very Large";
    public static final String LARGE = "Large";
    public static final String NORMAL = "Normal";
    public static final int VERYLARGEINT = 8;
    public static final int LARGEINT = 4;
    public static final int NORMALINT = 0;
    public static final int FONTSIZEINT = 12;
    public static final String LANGUAGESLISTS = "Languages Lists";
    public static final String SNAPS = "snaps";
    public static final String ROMS = "roms";
    public static final String SAMPLES = "samples";
    public static final String CHDS = "chds";
    static final String FULL_SET_DIRECTORIES_MAP = "FullSetDirectories MAP";
    static final String PLAY_SET_DIRECTORIES_MAP = "PlaySetDirectories";
    static final String HISTORY_DAT_FILE = "HistoryDATFILENAME";
    static final String MAMEINFO_DAT_FILE = "MAMEInfoDAT";
    static final String MESSINFO_DAT_FILE = "MESSInfoDAT";
    static final String SYSINFO_DAT_FILE = "SYSInfoDAT";
    static final String CATVER_INI_FILE = "CatverINI";
    static final String NPLAYERS_INI_FILE = "NplayersINI";
    static final String LANGUAGES_INI_FILE = "LanguagesINI";
    static final String HISTORY_DAT_FILENAME = "history.dat";
    static final String MAMEINFO_DAT_FILENAME = "mameinfo.dat";
    static final String MESSINFO_DAT_FILENAME = "messinfo.dat";
    static final String SYSINFO_DAT_FILENAME = "sysinfo.dat";
    static final String EXTRAS_ZIPS = "Extras Zip Files";
    static final String VIDEOS = "vids";
    static final String FONTSIZE = "fontSize";
    static final String CURRENTLIST = "currentList";
    static final String LOOKANDFEEL = "lookAndFeel";
    static final String SELECTEDTAB = "Selected Tab";
    static final String FOLDERS = "folders";
    static final String SETTINGS = MFMAction.SETTINGS;
    static final String DATA = "data";
    static final String JARS = "jars";
    static final String LISTS = "lists";
    static final String LOGS = "logs";
    public static final String COMMANDS = "Commands";
    public static final String ARTWORK = "artwork";
    public static final String SOFTWARE_LIST_SEPARATER = "\u00A8";
    static final String SHOW_XML = "Show XML";
    public static final String ALL = "All";
    // Help items
    public static final String ABOUT = "About";
    public static final String MFM_USER_GUIDE = "MFM User Guide";
    public static final String MFM_COPYRIGHT = "MFM Copyright";
    public static final String GNU_GPL = "GNU GPL";
    public static final String HOT_KEYS = "Hot Keys";

    private static final ArrayList<String> folderNames = new ArrayList<>(Arrays.asList(
            ARTWORK, "artwork preview", "bkground", "bosses", "cabdevs", CABINETS, "chds",
            COVERS_SL, "cpanel", "ctrlr", "devices", "ends", "flyers", FOLDERS, "gameover", "howto",
            "icons", "ini", "logo", MANUALS, MANUALS_SL, "marquees",
            "pcb", "roms", SAMPLES, "scores", "select", "snap", SNAP_SL, SNAPS,
            SOFTWARE_LISTS, TITLES, TITLES_SL, "versus", Machine.VIDEO, "videosnaps"));

    public static String[] MAME_FOLDER_NAMES_ARRAY; // TODO now this is dynamic should move WHERE?
    public static final String[] yearsList;

    static {
        File folderNamesFile = MFM_Data.getInstance().getFolderNamesFile();
        if (folderNamesFile != null && folderNamesFile.exists()) {
            try {
                Object[] folders = Files.readAllLines(folderNamesFile.toPath()).toArray();
                MAME_FOLDER_NAMES_ARRAY = Arrays.copyOf(folders, folders.length, String[].class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            MAME_FOLDER_NAMES_ARRAY = folderNames.toArray(new String[0]);
        }

        int offset = 4;
        int finalYear = Calendar.getInstance().get(Calendar.YEAR) - offset;
        yearsList = new String[finalYear - 1974];
        yearsList[0] = MFMListBuilder.ALL_YEARS;
        int k = 1;
        for (int i = 1975; i < finalYear; i++) {
            yearsList[k++] = String.valueOf(i);
        }
    }

    private MFM_Constants() { // To cover implicit public constructor
    }

}
