/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2016.  Author phweda : phweda1@yahoo.com
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

package Phweda.MFM.Utils;

import Phweda.MFM.*;
import Phweda.MFM.mame.Machine;
import Phweda.utils.FileUtils;
import Phweda.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static Phweda.MFM.MFMSettings.*;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/22/11
 * Time: 11:58 AM
 */

/*
 * Provides methods for performing MFM File operations
 */
public class MFMFileOps {
    /* TODO determine if we need this here ??*/
    public static void fileOps(String opName) {
        try {
            // Just for logger
            // JOptionPane.showConfirmDialog(frame, opName);

            switch (opName) {

                case "Copy Directory Tree":

                    break;
                case "Update All Lists":

                    break;
                case "Update List":

                    break;
                case "MAME Update":

                    break;


            }
        } catch (Exception exc) {

        }

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
    * TODO refactor this - too big and needs tightening up
    * Find all files for each game in a playlist.
    * Copy them to the appropriate Play directory if it exists
    */
    // TODO Need to convert to a set of search roots see copyListResources
    // Done logic moved to MAME_Resources
    public static boolean populatePlayList(TreeSet treeSet) {

        Object[] machines = treeSet.toArray();
        FileUtils.MFM_FindFile ff = null;
        for (Object machineName : machines) {
            // Find ROM and or CHD
            // Find all Extras
            String name = machineName.toString();
            try {
                ff = new FileUtils.MFM_FindFile(Paths.get(RomsFullSetDir()), Paths.get(name), true, false);

                if (CHDsFullSetDir() != null && CHDsFullSetDir().length() > 0 && !RomsFullSetDir().equals
                        (CHDsFullSetDir())) {
                    ff = new FileUtils.MFM_FindFile(Paths.get(CHDsFullSetDir()),
                            Paths.get(name), true, true);
                }

                // With MESS merge we need to look for Systems' directory fixme??
                ff = new FileUtils.MFM_FindFile(Paths.get(getExtrasFullSetDir()), Paths.get(name), true, true);

            } catch (Exception e) {
                e.printStackTrace();
                // return false;
            }
        }

        ArrayList<Path> fileList = FileUtils.MFM_FindFile.Files();
        // Note with MESS need to get a Directory for Extras
        if (!FileUtils.MFM_FindFile.directories().isEmpty()) {
            HashMap<String, String> dirs = FileUtils.MFM_FindFile.directories();
            for (String dir : dirs.keySet()) {
                fileList.add(Paths.get(dirs.get(dir)));
            }
        }
        ff.clear();
        moveFilesOLD(fileList);

        for (Object machineName : machines) {
            // Check for and load other required resources
            loadRequiredResources(machineName.toString());
        }
        return true;
    }

    // NOTE fixed missing ancestor for Merged sets in MAME_Resources
    private static void loadRequiredResources(String machineName) {
        // Get the getMachine object and check for romof and device_ref
        // Find and move those
        MFM.logger.out("Checking for required parent or device ROMs : " + machineName);
        Machine machine = MAMEInfo.getMachine(machineName);
        // NOTE we could get a null Machine??
        List deviceRef = null;
        if (machine != null) {
            deviceRef = machine.getDeviceRef();
        }

        if (machine.getRomof() != null && machine.getRomof().length() > 0) {
            if (deviceRef != null) {
                deviceRef.add(machine.getRomof());
            } else {
                deviceRef = new ArrayList<String>(1);
                deviceRef.add(machine.getRomof());
            }
        }

        if (deviceRef != null && !deviceRef.isEmpty()) {
            FileUtils.MFM_FindFile ff = null;
            for (Object name : deviceRef) {
                try {
                    ff = new FileUtils.MFM_FindFile(Paths.get(RomsFullSetDir()),
                            Paths.get(name.toString()), true, false);
                    if (CHDsFullSetDir() != null && CHDsFullSetDir().length() > 0 && !RomsFullSetDir().equals
                            (CHDsFullSetDir())) {
                        ff = new FileUtils.MFM_FindFile(Paths.get(CHDsFullSetDir()),
                                Paths.get(name.toString()), true, false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ArrayList<Path> fileList = FileUtils.MFM_FindFile.Files();
            // Ensure search results are cleared for succeeding calls
            ff.clear();
            moveFilesOLD(fileList);
        }
    }

    /**
     * 3/11/2016
     * Updated methodology
     *
     * @param files
     */
    public static void moveFiles(TreeMap<String, Object> files) {


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
        String romsDir = MFMSettings.getPlaySetDirectories().get(MFM_Constants.ROMS);
        TreeSet<File> romsList = (TreeSet<File>) files.get(MFM_Constants.ROMS);
        for (File file : romsList) {
            Path path = Paths.get(file.getAbsolutePath());
            MFM.logger.out("Copying File : " + path.toString() + " -> " + romsDir);
            try {
                if (romsDir != null) {
                    if (Files.isDirectory(path)) {
                        FileUtils.copyDirectory(path, romsDir, true);
                    } else {
                        FileUtils.copyFile(path, romsDir, true);
                    }
                } else {
                    MFM.logger.out("!!!!! copying resources found no 'roms' directory in your Playset location");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // CHDs
        String CHDsDir = MFMSettings.getPlaySetDirectories().get(MFM_Constants.CHDS);
        if (CHDsDir == null) {
            CHDsDir = MFMSettings.getPlaySetDirectories().get(MFM_Constants.ROMS);
        }

        File CHDfile = new File(CHDsDir);
        if (!CHDfile.exists()) {
            CHDfile.mkdir();
        }

        TreeSet<File> chdList = (TreeSet<File>) files.get(MFM_Constants.CHDS);
        for (File file : chdList) {
            Path path = Paths.get(file.getAbsolutePath());
            MFM.logger.out("Copying File : " + path.toString() + " -> " + CHDsDir);
            try {
                if (CHDsDir != null) {
                    if (Files.isDirectory(path)) {
                        // NOTE Do nothing no longer copying all automatically 12/20/2016
                        //FileUtils.copyDirectory(path, CHDsDir, true);
                    } else {
                        // Create directory to maintain Folder/CHDfile structure
                        String tempDir = CHDsDir + FileUtils.DIRECTORY_SEPARATOR + file.getParentFile().getName();
                        File dir = new File(tempDir);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        FileUtils.copyFile(path, tempDir, true);
                    }
                } else {
                    MFM.logger.out("!!!!! copying resources found no 'chds' " +
                            "or 'roms' directory in your Playset location");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // zipped extras
        for (String folder : ((TreeMap<String, TreeSet<String>>) files.get(MAME_Resources.ZIPEXTRAS)).keySet()) {
            TreeSet<String> zippedExtraFiles =
                    ((TreeMap<String, TreeSet<String>>) files.get(MAME_Resources.ZIPEXTRAS)).get(folder);
            File zipFile = MFMSettings.getExtrasZipFilesMap().get(folder);
            String extrasDir = MFMSettings.getPlaySetDirectories().get(folder);
            if (extrasDir == null) {
                extrasDir = MFMSettings.getPlaySetDir() + FileUtils.DIRECTORY_SEPARATOR + folder;
                File extrasDirFile = new File(extrasDir);
                boolean created = extrasDirFile.mkdir();
                if (created) {
                    MFMSettings.getPlaySetDirectories().put(folder, extrasDirFile.getAbsolutePath());
                } else {
                    MFM.logger.out("Failed to create Playset directory while copying resources");
                }
            }
            for (String zipentry : zippedExtraFiles) {
                MFM.logger.out("Copying zipped File : " + zipentry + " -> " + extrasDir);
                try {
                    boolean copied = ZipUtils.extractFile(zipFile.toPath(), zipentry,
                            Paths.get(extrasDir + FileUtils.DIRECTORY_SEPARATOR + zipentry));
                    if (!copied) {
                        MFM.logger.out("FAILED copying zipped File : " + zipentry + " -> " + extrasDir);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // Extras - we do these last will overwrite any zipped files
        // NOTE assumption is unzipped dupes are newest - we will not support any other scheme
        for (String folder : ((TreeMap<String, TreeSet<File>>) files.get(MAME_Resources.EXTRAS)).keySet()) {
            TreeSet<File> extraFiles = ((TreeMap<String, TreeSet<File>>) files.get(MAME_Resources.EXTRAS)).get(folder);
            String extrasDir = MFMSettings.getPlaySetDirectories().get(folder);

            for (File file : extraFiles) {
                Path path = Paths.get(file.getAbsolutePath());
                MFM.logger.out("Copying File : " + path.toString() + " -> " + extrasDir);
                try {
                    if (extrasDir != null) {
                        if (Files.isDirectory(path)) {
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

    private static void moveFilesOLD(ArrayList<Path> filesList) {
        for (Path path : filesList) {
            MFM.logger.out("Copying File source path is  : " + path.toString());
            String dir = null;
            // If ZIP it could be a sample or artwork too!!
            if (path.toString().endsWith(".zip") || path.toString().endsWith(".7z")) {

                if (path.toString().toLowerCase().contains("roms")) {
                    dir = MFMSettings.PlaySetDirectories().get("roms");
                } else if (path.toString().toLowerCase().contains("samples")) {
                    dir = MFMSettings.PlaySetDirectories().get("samples");
                } else if (path.toString().toLowerCase().contains("artwork")) {
                    dir = MFMSettings.PlaySetDirectories().get("artwork");
                } else {
                    // TODO how to punt here??
                    // dir = MAMESettings.PlaySetDirectories().get("roms");
                    System.out.println(path + " : " + path.toString().toLowerCase());
                }
/*
                try {
                    if (Files.isDirectory(path)) {
                        FileUtils.copyDirectory(path, dir, true);
                    } else {
                        FileUtils.copyFile(path, dir, true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
*/
                // Check for and load other required resources

            } /*else if (path.toString().endsWith(".chd")) {
                // Have to add CHD dir here
                if ((dir = MFMSettings.PlaySetDirectories().get("chds")) != null) {

                } else {
                    dir = MFMSettings.PlaySetDirectories().get("roms");
                }
                // Strip off file name. For CHD we need to copy over the parent directory first
                Path parentPath = path.getParent();
                try {
                    FileUtils.copyFile(parentPath, dir, true);
                    FileUtils.copyFile(path,
                            (dir + FileUtils.DIRECTORY_SEPARATOR + parentPath.getFileName().toString()), true);
                } catch (IOException e) {
                    if (e instanceof DirectoryNotEmptyException) {
                        MFM.logger.addToList("Directory : " + path.getFileName() + " already exists"
                        );
                    } else {
                        e.printStackTrace();
                    }
                }

            }
*/
            // TODO extras folders mapping in ? MAMEInfo? fixme : Map all Extras folders directly
            // Now the Extras for Arcades
            else {

                // This is where we hook for CHD

                // Parse the path to get the parent folder name and load the corresponding PlaySetDir
                dir = MFMSettings.PlaySetDirectories().get(path.getParent().getFileName().toString());

                /**
                 * With MESS merge we will have bunches of files to NOT move
                 * They are filtered out because their parent does NOT have a Playset Directory
                 */
                try {
                    if (dir != null) {
                        if (Files.isDirectory(path)) {
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
        HashMap map = null;
        try {
            FileUtils.MFM_FindFile ff = new FileUtils.MFM_FindFile(root, dirNames, false, true);
            map = FileUtils.MFM_FindFile.directories();
            // Ensure search results are cleared for succeeding calls
            ff.clear();
        } catch (IOException e) {
            e.printStackTrace(MFM.logger.Writer());
        }
        return map;
    }

    /*
    * Find a single file
    */
    public static String findMAMEfile(Path root, Path name) {
        String path = null;
        try {
            FileUtils.MFM_FindFile ff = new FileUtils.MFM_FindFile(root, name);
            if (FileUtils.MFM_FindFile.File() != null) {
                path = FileUtils.MFM_FindFile.File().toString();
                // Ensure search results are cleared for succeeding calls
                ff.clear();
            }
        } catch (IOException e) {
            e.printStackTrace(MFM.logger.Writer());
        }
        return path;
    }

    /* TODO is this needed??
    * Find a single directory
    */
    protected static Path findMAMEdirectory(Path root, String name) {
        return Paths.get("");
    }

}
