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

package com.github.phweda.mfm.utils;

import com.github.phweda.mfm.*;
import com.github.phweda.mfm.mame.Machine;
import com.github.phweda.utils.FileUtils;
import com.github.phweda.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.github.phweda.mfm.MFM_Constants.ROMS;
import static com.github.phweda.mfm.MFM_Constants.SAMPLES;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/22/11
 * Time: 11:58 AM
 */

/*
 * Provides methods for performing MFM File operations
 */
public final class MFMFileOps {

    private static final MFMSettings mfmSettings = MFMSettings.getInstance();
    private static final String COPYING_FILE = "Copying File : ";

    private MFMFileOps() { // cover implicit public constructor
    }

    /*
     * Scans and updates files for a playlist. After a MAME and/or
     * Extras update should update all Machine related files if they
     * have been added or changed
     *
     * NOTE are we OK assuming existing files can be determined to have changed
     * based solely on file timestamp???
     */
    // TODO implement
    protected static boolean updatePlayListResources(String listName) {

        return true;
    }

    /*
     * Find all files for each game in a playlist.
     * Copy them to the appropriate Play directory if it exists
     */
    public static boolean populatePlayList(Set treeSet) {

        Object[] machines = treeSet.toArray();
        FileUtils.MFMFindFile ff = null;
        for (Object machineName : machines) {
            // Find ROM and or CHD
            // Find all Extras
            String name = machineName.toString();
            try {
                ff = new FileUtils.MFMFindFile(Paths.get(mfmSettings.RomsFullSetDir()), Paths.get(name),
                        true, false);

                if (mfmSettings.CHDsFullSetDir() != null && mfmSettings.CHDsFullSetDir().length() > 0 &&
                        !mfmSettings.RomsFullSetDir().equals(mfmSettings.CHDsFullSetDir())) {
                    ff = new FileUtils.MFMFindFile(Paths.get(mfmSettings.CHDsFullSetDir()),
                            Paths.get(name), true, true);
                }

                ff = new FileUtils.MFMFindFile(Paths.get(mfmSettings.getExtrasFullSetDir()), Paths.get(name),
                        true, true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ArrayList<Path> fileList = FileUtils.MFMFindFile.files();
        // Note with MESS need to get a Directory for Extras
        if (!FileUtils.MFMFindFile.directories().isEmpty()) {
            HashMap<String, String> dirs = FileUtils.MFMFindFile.directories();
            for (Map.Entry<String, String> entry : dirs.entrySet()) {
                fileList.add(Paths.get(entry.getValue()));
            }
        }
        if (ff != null) {
            ff.clear();
        }
        moveFilesOLD(fileList);

        for (Object machineName : machines) {
            // Check for and load other required resources
            loadRequiredResources(machineName.toString());
        }
        return true;
    }

    private static void loadRequiredResources(String machineName) {
        // Get the getMachine object and check for romof and device_ref
        // Find and move those
        MFM.getLogger().out("Checking for required parent or device ROMs : " + machineName);
        Machine machine = MAMEInfo.getMachine(machineName);

        List deviceRef;
        if (machine != null) {
            deviceRef = machine.getDeviceRef();
        } else {
            return;
        }

        if ((machine.getRomof() != null) && (machine.getRomof().length() > 0)) {
            if (deviceRef != null) {
                deviceRef.add(machine.getRomof());
            } else {
                deviceRef = new ArrayList<String>(1);
                deviceRef.add(machine.getRomof());
            }
        }

        if ((deviceRef != null) && !deviceRef.isEmpty()) {
            FileUtils.MFMFindFile ff = null;
            for (Object name : deviceRef) {
                try {
                    ff = new FileUtils.MFMFindFile(Paths.get(mfmSettings.RomsFullSetDir()),
                            Paths.get(name.toString()), true, false);
                    if (mfmSettings.CHDsFullSetDir() != null && mfmSettings.CHDsFullSetDir().length() > 0 &&
                            !mfmSettings.RomsFullSetDir().equals(mfmSettings.CHDsFullSetDir())) {
                        ff = new FileUtils.MFMFindFile(Paths.get(mfmSettings.CHDsFullSetDir()),
                                Paths.get(name.toString()), true, false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Iterable<Path> fileList = FileUtils.MFMFindFile.files();
            // Ensure search results are cleared for succeeding calls
            if (ff != null) {
                ff.clear();
            }
            moveFilesOLD(fileList);
        }
    }

    /**
     * 3/11/2016
     * Updated methodology
     *
     * @param files Map of files to move
     */
    public static void moveFiles(SortedMap<String, Object> files) {


        /**     FROM MAME_Resources
         * TreeMap is:
         *
         * "roms" -> ArrayList of rom files
         * "chds" -> ArrayList of CHD files
         *
         * "extras" -> TreeMap<String, ArrayList<File>> keys from MFMSettings.getFullSetExtrasDirectories()
         *          "artwork" ...
         *          "flyers"
         *          "icons"  ...
         *          "snap"
         *          ......
         *
         * "zipextras" -> TreeMap<String, TreeSet<String>> keys from MFMSettings.getExtrasZipFilesMap()
         *          "artpreview" ...
         *          "flyers"
         *          "icons"  ...
         *          "snap"
         *          ......
         *
         **/

        // ROMs
        String romsDir = mfmSettings.getPlaySetDirectories().get(ROMS);
        Iterable<File> romsList = (Iterable<File>) files.get(ROMS);
        for (File file : romsList) {
            Path path = Paths.get(file.getAbsolutePath());
            MFM.getLogger().out(COPYING_FILE + path.toString() + " -> " + romsDir);
            try {
                if (romsDir != null) {
                    if (path.toFile().isDirectory()) {
                        FileUtils.copyDirectory(path, romsDir, true);
                    } else {
                        FileUtils.copyFile(path, romsDir, true);
                    }
                } else {
                    MFM.getLogger().out("!!!!! copying resources found no 'roms' directory in your Playset location");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // CHDs
        String chdsDir = mfmSettings.getPlaySetDirectories().get(MFM_Constants.CHDS);
        if (chdsDir == null) {
            chdsDir = mfmSettings.getPlaySetDirectories().get(ROMS);
        }

        File chdFile = new File(chdsDir);
        if (!chdFile.exists()) {
            try {
                Files.createDirectory(chdFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Iterable<File> chdList = (Iterable<File>) files.get(MFM_Constants.CHDS);
        for (File file : chdList) {
            Path path = Paths.get(file.getAbsolutePath());
            MFM.getLogger().out(COPYING_FILE + path.toString() + " -> " + chdsDir);
            try {
                if (!path.toFile().isDirectory()) {
                    // Create directory to maintain Folder/CHDfile structure
                    String tempDir = chdsDir + FileUtils.DIRECTORY_SEPARATOR + file.getParentFile().getName();
                    File dir = new File(tempDir);
                    if (!dir.exists()) {
                        Files.createDirectory(dir.toPath());
                    }
                    FileUtils.copyFile(path, tempDir, true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // zipped extras
        for (String folder : ((TreeMap<String, TreeSet<String>>) files.get(MAME_Resources.ZIPEXTRAS)).keySet()) {
            TreeSet<String> zippedExtraFiles =
                    ((TreeMap<String, TreeSet<String>>) files.get(MAME_Resources.ZIPEXTRAS)).get(folder);
            File zipFile = mfmSettings.getExtrasZipFilesMap().get(folder);
            String extrasDir = mfmSettings.getPlaySetDirectories().get(folder);
            if (extrasDir == null) {
                extrasDir = mfmSettings.getPlaySetDir() + FileUtils.DIRECTORY_SEPARATOR + folder;
                File extrasDirFile = new File(extrasDir);
                boolean created = extrasDirFile.mkdir();
                if (created) {
                    mfmSettings.getPlaySetDirectories().put(folder, extrasDirFile.getAbsolutePath());
                } else {
                    MFM.getLogger().out("Failed to create Playset directory while copying resources");
                }
            }
            for (String zipentry : zippedExtraFiles) {
                MFM.getLogger().out("Copying zipped File : " + zipentry + " -> " + extrasDir);

                boolean copied = ZipUtils.extractFile(zipFile.toPath(), zipentry,
                        Paths.get(extrasDir + FileUtils.DIRECTORY_SEPARATOR + zipentry));
                if (!copied) {
                    MFM.getLogger().out("FAILED copying zipped File : " + zipentry + " -> " + extrasDir);
                }
            }
        }
        // Extras - we do these last will overwrite any zipped files
        // NOTE assumption is unzipped dupes are newest - we will not support any other scheme
        for (String folder : ((TreeMap<String, TreeSet<File>>) files.get(MAME_Resources.EXTRAS)).keySet()) {
            TreeSet<File> extraFiles = ((TreeMap<String, TreeSet<File>>) files.get(MAME_Resources.EXTRAS)).get(folder);
            String extrasDir = mfmSettings.getPlaySetDirectories().get(folder);

            for (File file : extraFiles) {
                Path path = Paths.get(file.getAbsolutePath());
                MFM.getLogger().out(COPYING_FILE + path.toString() + " -> " + extrasDir);
                try {
                    if (extrasDir != null) {
                        if (path.toFile().isDirectory()) {
                            FileUtils.copyDirectory(path, extrasDir, true);
                        } else {
                            FileUtils.copyFile(path, extrasDir, true);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // TODO clean up this old mess
    private static void moveFilesOLD(Iterable<? extends Path> filesList) {
        for (Path path : filesList) {
            MFM.getLogger().out("Copying File source path is  : " + path.toString());
            String dir = null;
            // If ZIP it could be a sample or artwork too!!
            if (path.toString().endsWith(".zip") || path.toString().endsWith(".7z")) {

                if (path.toString().toLowerCase().contains(ROMS)) {
                    dir = mfmSettings.PlaySetDirectories().get(ROMS);
                } else if (path.toString().toLowerCase().contains(SAMPLES)) {
                    dir = mfmSettings.PlaySetDirectories().get(SAMPLES);
                } else if (path.toString().toLowerCase().contains(MFM_Constants.ARTWORK)) {
                    dir = mfmSettings.PlaySetDirectories().get(MFM_Constants.ARTWORK);
                } else {
                    // TODO how to punt here??
                    // dir = MAMESettings.PlaySetDirectories().get("roms");
                    System.out.println(path + " : " + path.toString().toLowerCase());
                }
                // Check for and load other required resources
            }
            // Now the Extras for Arcades
            else {

                // This is where we hook for CHD

                // Parse the path to get the parent folder name and load the corresponding PlaySetDir
                dir = mfmSettings.PlaySetDirectories().get(path.getParent().getFileName().toString());

                /**
                 * With MESS merge we will have bunches of files to NOT move
                 * They are filtered out because their parent does NOT have a Playset Directory
                 */
                try {
                    if (dir != null) {
                        if (path.toFile().isDirectory()) {
                            FileUtils.copyDirectory(path, dir, true);
                        } else {
                            FileUtils.copyFile(path, dir, true);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * Find all MAME directories i.e. roms, chds, samples, snaps etc
     */
    public static HashMap<String, String> findMAMEdirectories(Path root, String[] dirNames) {
        HashMap<String, String> map = null;
        try {
            FileUtils.MFMFindFile findFile = new FileUtils.MFMFindFile(root, dirNames, false, true);
            map = FileUtils.MFMFindFile.directories();
            // Ensure search results are cleared for succeeding calls
            findFile.clear();
        } catch (IOException e) {
            e.printStackTrace(MFM.getLogger().writer());
        }
        return map;
    }

    /*
     * Find a single file
     */
    public static String findMAMEfile(Path root, Path name) {
        String path = null;
        try {
            FileUtils.MFMFindFile findFile = new FileUtils.MFMFindFile(root, name);
            if (FileUtils.MFMFindFile.file() != null) {
                path = FileUtils.MFMFindFile.file().toString();
                // Ensure search results are cleared for succeeding calls
                findFile.clear();
            }
        } catch (IOException e) {
            e.printStackTrace(MFM.getLogger().writer());
        }
        return path;
    }

}
