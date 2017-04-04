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

package Phweda.utils;

import Phweda.MFM.MFM;
import Phweda.MFM.MFM_Constants;
import Phweda.MFM.MFM_Data;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/28/11
 * Time: 5:41 PM
 */
public class FileUtils {
    // Slash for Resource URLs
    public static final char SLASH = '/';
    public static final String DIRECTORY_SEPARATOR = File.separator;
    public static final String NEWLINE = System.getProperty("line.separator");
    public static final String ZIPSUFFIX = ".zip";

    /* Used to limit search depth. 20 should be more than sufficient*/
    private static final int maxDepth = 25;
    public static int MaxDepth = maxDepth;

    // This filter only returns directories
    public static FilenameFilter directoryFilenameFilter = new FilenameFilter() {
        public boolean accept(File file, String name) {
            return file.isDirectory();
        }
    };

    // This filter only returns .zip files
    public static FilenameFilter zipFilenameFilter = new FilenameFilter() {
        public boolean accept(File file, String name) {
            return name.toLowerCase().endsWith(".zip");
        }
    };

    // This filter only returns .jar files
    public static FilenameFilter jarFilenameFilter = new FilenameFilter() {
        public boolean accept(File file, String name) {
            return name.toLowerCase().endsWith(".jar");
        }
    };

    // This filter only returns .ini files
    public static FilenameFilter iniFilenameFilter = new FilenameFilter() {
        public boolean accept(File file, String name) {
            return name.toLowerCase().endsWith(".ini");
        }
    };

    // This filter only returns .gif files
    public static FilenameFilter GIFFilenameFilter = new FilenameFilter() {
        public boolean accept(File file, String name) {
            return name.toLowerCase().endsWith(".gif");
        }
    };

    // This filter only returns .avi files
    public static FilenameFilter AVIFilenameFilter = new FilenameFilter() {
        public boolean accept(File file, String name) {
            return name.toLowerCase().endsWith(".avi");
        }
    };

    // This filter only returns video files
    // TODO really is not needed
    public static FilenameFilter VideoFilenameFilter = new FilenameFilter() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        private final Map<String, String> VIDEO_EXT = Collections.unmodifiableMap(tempMap);

        {
            tempMap.put(".avi", ".avi");
            tempMap.put(".flv", ".flv");
            tempMap.put(".mov", ".mov");
            tempMap.put(".mp4", ".mp4");
            tempMap.put(".mpeg", ".mpeg");
            tempMap.put(".mpg", ".mpg");
            //    tempMap.put(".", ".");
            //    tempMap.put(".", ".");
            //    tempMap.put(".", ".");
        }

        public boolean accept(File file, String name) {
            if (!name.contains(".")) {
                return false;
            }
            return VIDEO_EXT.containsKey(name.toLowerCase().substring(name.lastIndexOf('.')));
        }
    };

    /*
     *  @param Object path - allows us to handle both Strings and URLs
     */
    public static String getFileContents(Object path) {
        File file = null;
        if (path instanceof URL) {
            file = new File(((URL) path).getPath());
        } else if (path instanceof String) {
            file = new File((String) path);
        }

        if (!file.exists()) {
            return null;
        }

        StringBuilder fileContents = new StringBuilder((int) file.length());
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                fileContents.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace(MFM.logger.Writer());
        } catch (IOException e) {
            e.printStackTrace();
        } 
        return fileContents.toString();
    }

    /* TODO  java.nio version */
    public static void copyDirectoryTree(File source, File destination) throws IOException {

        /* We only want a Directory this is just a sanity check */
        if (source.isDirectory()) {

            //if directory does not exists, create it
            if (!destination.exists()) {
                destination.mkdir();
                System.out.println("Directory copied from "
                        + source + "  to " + destination);
            }

            //list all the directory child directories
            String files[] = source.list(directoryFilenameFilter);

            for (String file : files) {
                //construct the src and dest file structure
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);
                //recursive copy
                copyDirectoryTree(srcFile, destFile);
            }
        }
    }

    /*
    *
    *
    *
     */
    public static void copyFile(File file, String destinationDir, boolean replace) throws IOException {
        Path destinationDirPath = Paths.get(destinationDir, file.getName());
        if (replace) {
            Files.copy(file.toPath(), destinationDirPath, REPLACE_EXISTING);
        } else {
            Files.copy(file.toPath(), destinationDirPath);
        }
    }

    /*
    *
    *
    *
     */
    public static void copyFile(Path file, String destinationDir, boolean replace) throws IOException {
        Path destinationDirPath = Paths.get(destinationDir, file.getFileName().toString());
        try {
            if (replace) {
                Files.copy(file, destinationDirPath, REPLACE_EXISTING);
            } else {
                Files.copy(file, destinationDirPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyDirectory(Path directory, String destinationDir, boolean replace) throws IOException {
        if (Files.isDirectory(directory)) {
            File newDir = new File(destinationDir + DIRECTORY_SEPARATOR + directory.getFileName());
            if (!newDir.exists()) {
                newDir.mkdir();
            }
            String newDestDir = newDir.getAbsolutePath();
            File[] files = directory.toFile().listFiles();
            for (File file : files) {
                copyFile(file.toPath(), newDestDir, replace);
            }
        }
    }

    public static void moveFile(File file, String destinationDir, boolean replace) throws IOException {
        Files.move(file.toPath(), Paths.get(destinationDir, file.toPath().getFileName().toString()));
    }


    /* TODO  java.nio version */
    /*
   * Searches for this fileName from this directory path
   *
   *
   * returns false if this directory does not exist
    */

    public static boolean fileExists(String path, String name) {
        try {
            return new FileExists().find(path, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void openTextFileFromOS(Path file) {
        try {
            File file1 = file.toFile();
            if (file1.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.EDIT)) {
                        desktop.edit(file1);
                    }

                } else {
                    System.err.println("Awt Desktop is not supported!");
                }

            } else {
                System.err.println("File does not exist!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // TODO Maybe we need to throw an Exception to propagate message to the user?
    // Or change this to boolean??
    public static void openFileFromOS(Path file) {
        try {
            File file1 = file.toFile();
            if (file1.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file1);
                } else {
                    System.err.println("Awt Desktop is not supported!");
                }

            } else {
                System.err.println("File does not exist!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static class FileExists {
        boolean exists = false;

        private boolean find(String path, String fileName) throws IOException {
            File testFile = new File(path);
            // TODO can we simplify the logic? We're testing isDirectory() twice!!
            if (!testFile.isDirectory() && testFile.getName().equalsIgnoreCase(fileName)) {
                exists = true;
                return exists;
            } else if (testFile.isDirectory()) {
                // System.out.println("Searching through Directory " + path);
                //list all the directory children
                String files[] = testFile.list();
                for (String file : files) {
                    // We found it break out
                    if (exists) {
                        break;
                    }
                    find(testFile.getAbsolutePath() + DIRECTORY_SEPARATOR + file, fileName);
                }
            }
            return exists;
        }
    }

    public static class MFMcacheResourceFiles extends SimpleFileVisitor<Path> {

        static final TreeSet<String> extrasDirectories = new TreeSet<String>(
                Arrays.asList(MFM_Constants.MAME_FOLDER_NAMES_ARRAY));
        static TreeMap<String, File> cache;
        static TreeMap<String, TreeMap<String, File>> extrasCache;
        static boolean directoryOnly = false;
        static boolean extras = false;
        static boolean SLCHDs = false;

        // NO Directories?
        public void cacheAllFiles(Path root, TreeMap<String, File> cacheIn, boolean directoryOnlyIn) {
            if (root.toString().isEmpty()) {
                return;
            }
            cache = cacheIn;
            directoryOnly = directoryOnlyIn;
            walkTree(root);
        }

        public void cacheSoftwarelistCHDsFiles(Path root, TreeMap<String, File> cacheIn) {
            SLCHDs = true;
            cacheAllFiles(root, cacheIn, true);
            SLCHDs = false;
        }

        public TreeMap<String, TreeMap<String, File>> cacheExtrasFiles(Path root) {
            extras = true;
            directoryOnly = false;
            extrasCache = new TreeMap<String, TreeMap<String, File>>();

            try {
                walkTree(root);
            } finally {
                extras = false;
            }
            return extrasCache;
        }

        private void walkTree(Path root) {
            try {
                Files.walkFileTree(root, EnumSet.noneOf(FileVisitOption.class),
                        FileUtils.MaxDepth, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
            if (directoryOnly) {
                return CONTINUE;
            } else if (!extras) {
                File file = path.toFile();
                if (MFM.isSystemDebug()) {
                    System.out.println("Caching file : " + path.toString());
                }
                cache.put(file.getName().substring(0, file.getName().lastIndexOf('.')), file);
            } else {
                File file = path.toFile();
                cacheExtraFile(file);
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            System.out.println("visitFileFailed for file: " + file.toString() + '\n' + exc);
            System.err.println("visitFileFailed for file: " + file.toString() + '\n' + exc);
            return CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (extras) {
                cacheExtraFile(dir.toFile());
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            File directory = dir.toFile();
            if (!extras && directory.exists()) {
                String key = null;
                if (SLCHDs) {
                    key = directory.getName() + ':' + directory.getParentFile().getName();
                } else {
                    return CONTINUE;
                }
                cache.put(key, directory);
            } else if (!extras) {
                MFM.logger.addToList("FileUtils 363: " + dir + " does not exist.");
            }
            return CONTINUE;
        }

        private void cacheExtraFile(File file) {
            if (file.isDirectory()) {
                String name = file.getName().toLowerCase(); // Just in case somebody plays with Capitals
                if (extrasDirectories.contains(name)) {
                    if (!extrasCache.containsKey(name)) {
                        extrasCache.put(name, new TreeMap<String, File>());
                    }
                }
            } else {
                String fileName = file.getName();
                if (fileName.endsWith(".zip") &&
                        extrasDirectories.contains(fileName.substring(0, fileName.lastIndexOf('.')))) {
                    return;
                }

                String filePath = file.getAbsolutePath().substring(0,
                        file.getAbsolutePath().lastIndexOf(FileUtils.DIRECTORY_SEPARATOR));
                // Find which Extras folder is contained in the path and add file to that map
                for (String folderName : extrasDirectories) {
                    if (folderName.equalsIgnoreCase(MFM_Constants.MAME_FOLDER_SNAPS)) {
                        continue;
                    }
                    if (filePath.contains(folderName) && !filePath.contains(MFM_Constants.MAME_FOLDER_SNAPS)) {
                        try {
                            // TODO needs testing!!!! 3/14/16
                            // fixme is this conditional extraneous? Does it HAVE to be the parent?
                            if (folderName.equalsIgnoreCase(file.getParentFile().getName())) {
                                extrasCache.get(folderName).put(
                                        file.getName().substring(0, file.getName().lastIndexOf('.')), file);
                            }
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
            }
        }

        /**
         * Not needed? TODO
         *
         * @param file
         * @return
         */
        private String extrasKey(File file) {
            String path = file.getAbsolutePath();

            return null;
        }

    }

    /*
     * TODO convert to singleton??
     *
     * */
    /*For java.nio MFM only!! */
    public static class MFM_FindFile extends SimpleFileVisitor<Path> {
        /*    For MFM :
         * We want to support finding multiple files of one name - Extras for a single Machine
         * And finding multiple Directories of multiple names on a single pass
         * Try to do everything on a single pass of a directory tree
         */

        private static Path fileName = null;
        private static String[] directoryNames = null;
        private static boolean matchWithoutSuffix = false;
        private static boolean matchDirectory = false;

        private static boolean exists = false;
        private static Path startFile = null;
        private static Path file = null;

        /*
         * For MAME extras we will have multiple
         * files with the same rootName
         */
        private static ArrayList<Path> files = new ArrayList<>(15);
        private static HashMap<String, String> directories = new HashMap<String, String>(15);

        /*
         * Use with true matchWithoutSuffix for getting more than one file result
         * Use with true matchDirectory for getting Directory(ies)
         */
        public MFM_FindFile(Path directory, Path fileName, boolean matchWithoutSuffix,
                            boolean matchDirectory) throws IOException {
            MFM_FindFile.fileName = fileName;
            if (matchDirectory) {
                MFM_FindFile.directoryNames = new String[]{fileName.getFileName().toString()};
            }
            MFM_FindFile.matchWithoutSuffix = matchWithoutSuffix;
            MFM_FindFile.matchDirectory = matchDirectory;
            startFile = Files.walkFileTree(directory, EnumSet.noneOf(FileVisitOption.class),
                    FileUtils.MaxDepth, this);
        }

        public MFM_FindFile(Path directory, String[] fileNames, boolean matchWithoutSuffix,
                            boolean matchDirectory) throws IOException {
            MFM_FindFile.directoryNames = fileNames;
            MFM_FindFile.matchWithoutSuffix = matchWithoutSuffix;
            MFM_FindFile.matchDirectory = matchDirectory;
            startFile = Files.walkFileTree(directory, EnumSet.noneOf(FileVisitOption.class),
                    FileUtils.MaxDepth, this);
        }

        public MFM_FindFile(Path directory, Path fileName) throws IOException {
            new MFM_FindFile(directory, fileName, matchWithoutSuffix, false);
        }

        /*
                //Print information about each type of file.
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
                    if (attr.isSymbolicLink()) {
                        System.out.format("Symbolic link: %s ", file);
                    } else if (attr.isRegularFile()) {
                        System.out.format("Regular file: %s ", file);
                    } else {
                        System.out.format("Other: %s ", file);
                    }
                    return CONTINUE;
                }

        */

        // TODO Do not need?? See outerclass FileUtils
        public static boolean exists() {
            return exists;
        }

        public static Path File() {
            return file;
        }

        public static ArrayList<Path> Files() {
            return (ArrayList<Path>) files.clone();
        }

        public static HashMap<String, String> directories() {
            return (HashMap<String, String>) directories.clone();
        }

        // TODO break this up to multiple methods
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
            // NOTE if we are searching for Directories also file may be NULL ??
            if (file == null) {
                System.out.println("visitFile File: is NULL");
                return CONTINUE;
            }
            if (file.getFileName().equals(fileName)) {
                System.out.println("Located file: " + file);
                if (!matchWithoutSuffix) {
                    MFM_FindFile.file = file;
                    exists = true;
                    return TERMINATE;
                }
            }  // Do we have a base file name, extension not included, match??
            else if (compareFileBaseName(file.getFileName(), fileName)) {
                System.out.println("Located file: " + file);
                files.add(file);
                exists = true;
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            // System.out.println("postVisitDirectory Directory: " + dir);
            if (matchDirectory) {
                for (String dirName : directoryNames) {
                    if (dir.getFileName().toString().equals(dirName)) {
                        directories.put(dirName, dir.toString());
                        exists = true;
                        return CONTINUE;
                    }
                }
            }
            return CONTINUE;
        }

        //If there is some error accessing the file, let the user know.
        //If you don't override this method and an error occurs, an IOException
        //is thrown.
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            System.err.println(exc);
            return CONTINUE;
        }

        /*
         * Compares file names without extension
         * Used to find MAME Extras files using naming convention of all being
         * named after getMachine short name
         *
         */
        private boolean compareFileBaseName(Path fileIn, Path fileNameIn) {
            if (fileIn == null || fileNameIn == null) {
                return false;
            }
            // TODO This is ugly and wasteful - StringBuilder ??
            String name = fileNameIn.toString();
            String file;
            if (name.contains(".")) {
                file = name.substring(0, name.indexOf('.'));
            } else {
                file = name;
            }
            return file.equalsIgnoreCase(name);
        }


        public void clear() {
            files.clear();
            directories.clear();
            // TODO figure out if we need this
            file = null;
        }
    }

}
