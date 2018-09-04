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

package Phweda.utils;

import Phweda.MFM.MFM;
import Phweda.MFM.MFM_Constants;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by IntelliJ IDEA.
 * User: phweda
 * Date: 11/28/11
 * Time: 5:41 PM
 */
@SuppressWarnings({"SameParameterValue", "unused", "WeakerAccess"})
public class FileUtils {
    // Slash for Resource URLs
    public static final char SLASH = '/';
    public static final String DIRECTORY_SEPARATOR = File.separator;
    public static final String NEWLINE = System.getProperty("line.separator");
    public static final char TAB = '\t';

    public static final String ZIPSUFFIX = ".zip";

    /* Used to limit search depth. 20 should be more than sufficient*/
    private static final int MAX_DEPTH = 25;
    private static int maxDepth = MAX_DEPTH;

    private static final HashMap<String, String> videoExtensionsMap = new HashMap<>();

    private FileUtils() {
    }

    static {
        videoExtensionsMap.put(".avi", ".avi");
        videoExtensionsMap.put(".flv", ".flv");
        videoExtensionsMap.put(".mov", ".mov");
        videoExtensionsMap.put(".mp4", ".mp4");
        videoExtensionsMap.put(".mpeg", ".mpeg");
        videoExtensionsMap.put(".mpg", ".mpg");
        //    videoExtensionsMap.put(".", ".");
    }

    // This filter only returns directories
    public static final FilenameFilter directoryFilenameFilter = (file, name) -> file.isDirectory();

    // This filter only returns .zip files
    public static final FilenameFilter zipFilenameFilter = (file, name) -> name.toLowerCase().endsWith(".zip");

    // This filter only returns .jar files
    public static final FilenameFilter jarFilenameFilter = (file, name) -> name.toLowerCase().endsWith(".jar");

    // This filter only returns .ini files
    public static final FilenameFilter iniFilenameFilter = (file, name) -> name.toLowerCase().endsWith(".ini");

    // This filter only returns .gif files
    public static final FilenameFilter GIFFilenameFilter = (file, name) -> name.toLowerCase().endsWith(".gif");

    // This filter only returns .avi files
    public static final FilenameFilter AVIFilenameFilter = (file, name) -> name.toLowerCase().endsWith(".avi");

    // This filter only returns .csv files
    public static final javax.swing.filechooser.FileFilter csvFileFilter = new javax.swing.filechooser.FileFilter() {
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().toLowerCase().endsWith(".csv");
        }

        @Override
        public String getDescription() {
            return "*.csv";
        }
    };

    // This filter only returns video files
    public static final FilenameFilter VideoFilenameFilter = new FilenameFilter() {
        private final Map<String, String> videoExtensions = Collections.unmodifiableMap(videoExtensionsMap);

        public boolean accept(File file, String name) {
            return name.contains(".") && videoExtensions.containsKey(name.toLowerCase().substring(name.lastIndexOf('.')));
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

        if (file == null || !file.exists()) {
            return null;
        }

        StringBuilder fileContents = new StringBuilder((int) file.length());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileContents.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace(MFM.getLogger().writer());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContents.toString();
    }

    /* TODO  java.nio version */
    public static void copyDirectoryTree(File source, File destination) {

        /* We only want a Directory this is just a sanity check */
        if (source.isDirectory()) {

            //if directory does not exists, create it
            if (!destination.exists()) {
                boolean success = destination.mkdir();
                if (success) {
                    System.out.println("Directory copied from " + source + "  to " + destination);
                }
            }

            //list all the directory child directories
            String[] files = source.list(directoryFilenameFilter);

            if (files != null) {
                for (String file : files) {
                    //construct the src and dest file structure
                    File srcFile = new File(source, file);
                    File destFile = new File(destination, file);
                    //recursive copy
                    copyDirectoryTree(srcFile, destFile);
                }
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
        if (replace) {
            Files.copy(file, destinationDirPath, REPLACE_EXISTING);
        } else {
            Files.copy(file, destinationDirPath);
        }
    }

    public static void copyDirectory(Path directory, String destinationDir, boolean replace) throws IOException {
        if (directory.toFile().isDirectory()) {
            File newDir = new File(destinationDir + DIRECTORY_SEPARATOR + directory.getFileName());
            if (!newDir.exists()) {
                boolean success = newDir.mkdir();
                if (!success) {
                    throw new FileNotFoundException("Failed to create directory : " + directory.toString());
                }
            }
            String newDestDir = newDir.getAbsolutePath();
            File[] files = directory.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    copyFile(file.toPath(), newDestDir, replace);
                }
            }
        }
    }

    public static void moveFile(File file, String destinationDir, boolean replace) throws IOException {
        Files.move(file.toPath(), Paths.get(destinationDir, file.toPath().getFileName().toString()));
    }


    /* TODO  java.nio version */
    /*
     * Searches for this fileName from this directory path
     * returns false if this directory does not exist
     */

    public static boolean fileExists(String path, String name) {
        return new FileExists().find(path, name);
    }

    public static String stripSuffix(String fileName) {
        // Strip suffix
        int pos = fileName.lastIndexOf('.');
        if (pos > 0) {
            fileName = fileName.substring(0, pos);
        }
        return fileName;
    }

    /**
     * Double quotes a String
     * NOTE replaces double quotes in input string with 2 single quotes
     *
     * @param input a String
     * @return converted string
     */
    public static String doubleQuoteString(String input) {
        StringBuilder stringBuilder = new StringBuilder("\"");
        if (input.contains("\"")) {
            input = input.replace("\"", "''");
        }
        stringBuilder.append(input);
        stringBuilder.append("\"");
        return stringBuilder.toString();
    }

    public static Set<String> listFromFile(File file) {
        Set<String> list = null;
        try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
            list = stream.collect(Collectors.toSet());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
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

        private boolean find(String path, String fileName) {
            File testFile = new File(path);
            // TODO can we simplify the logic? We're testing isDirectory() twice!!
            if (!testFile.isDirectory() && testFile.getName().equalsIgnoreCase(fileName)) {
                return true;
            } else if (testFile.isDirectory()) {
                // System.out.println("Searching through Directory " + path);
                //list all the directory children
                String[] files = testFile.list();
                if (files != null) {
                    for (String file : files) {
                        // We found it break out
                        if (exists) {
                            break;
                        }
                        find(testFile.getAbsolutePath() + DIRECTORY_SEPARATOR + file, fileName);
                    }
                }
            }
            return exists;
        }
    }

    @SuppressWarnings({"squid:S2696", "RedundantThrows"})
    public static class MFMcacheResourceFiles extends SimpleFileVisitor<Path> {

        static final TreeSet<String> extrasDirectories = new TreeSet<>(
                Arrays.asList(MFM_Constants.MAME_FOLDER_NAMES_ARRAY));
        static NavigableMap<String, File> cache;
        static SortedMap<String, SortedMap<String, File>> extrasCache;
        static boolean directoryOnly = false;
        static boolean extras = false;
        static boolean softwarelistCHDs = false;

        // NO Directories?
        public void cacheAllFiles(Path root, NavigableMap<String, File> cacheIn, boolean directoryOnlyIn) {
            if (root.toString().isEmpty()) {
                return;
            }
            cache = cacheIn;
            directoryOnly = directoryOnlyIn;
            walkTree(root);
        }

        public void cacheSoftwarelistCHDsFiles(Path root, NavigableMap<String, File> cacheIn) {
            softwarelistCHDs = true;
            cacheAllFiles(root, cacheIn, true);
            softwarelistCHDs = false;
        }

        public SortedMap<String, SortedMap<String, File>> cacheExtrasFiles(Path root) {
            extras = true;
            directoryOnly = false;
            extrasCache = new TreeMap<>();

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
                        FileUtils.maxDepth, this);
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
                String key;
                if (softwarelistCHDs) {
                    key = directory.getName() + ':' + directory.getParentFile().getName();
                } else {
                    return CONTINUE;
                }
                cache.put(key, directory);
            } else if (!extras) {
                MFM.getLogger().addToList("FileUtils 363: " + dir + " does not exist.");
            }
            return CONTINUE;
        }

        private void cacheExtraFile(File file) {
            if (file.isDirectory()) {
                String name = file.getName().toLowerCase(); // Just in case somebody plays with Capitals
                if (extrasDirectories.contains(name) && !extrasCache.containsKey(name)) {
                    extrasCache.put(name, new TreeMap<>());
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
                    if (folderName.equalsIgnoreCase(MFM_Constants.SNAPS)) {
                        continue;
                    }
                    if (filePath.contains(folderName) && !filePath.contains(MFM_Constants.SNAPS)) {
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
    }

    /*For java.nio mfm only!! */
    @SuppressWarnings({"squid:S2696", "squid:S3010"})
    public static final class MFMFindFile extends SimpleFileVisitor<Path> {
        /*    For mfm :
         * We want to support finding multiple files of one name - Extras for a single Machine
         * And finding multiple Directories of multiple names on a single pass
         * Try to do everything on a single pass of a directory tree
         */

        private static Path fileName = null;
        private static String[] directoryNames = null;
        private static boolean matchWithoutSuffix = false;
        private static boolean matchDirectory = false;

        private static boolean exists = false;
        private static Path file = null;

        /*
         * For MAME extras we will have multiple
         * files with the same rootName
         */
        private static ArrayList<Path> files = new ArrayList<>(15);
        private static HashMap<String, String> directories = new HashMap<>(15);

        /*
         * Use with true matchWithoutSuffix for getting more than one file result
         * Use with true matchDirectory for getting Directory(ies)
         */
        public MFMFindFile(Path directory, Path fileName, boolean matchWithoutSuffix,
                           boolean matchDirectory) throws IOException {
            MFMFindFile.fileName = fileName;
            if (matchDirectory) {
                MFMFindFile.directoryNames = new String[]{fileName.getFileName().toString()};
            }
            MFMFindFile.matchWithoutSuffix = matchWithoutSuffix;
            MFMFindFile.matchDirectory = matchDirectory;
            Files.walkFileTree(directory, EnumSet.noneOf(FileVisitOption.class), FileUtils.maxDepth, this);
        }

        public MFMFindFile(Path directory, String[] fileNames, boolean matchWithoutSuffix,
                           boolean matchDirectory) throws IOException {
            MFMFindFile.directoryNames = fileNames;
            MFMFindFile.matchWithoutSuffix = matchWithoutSuffix;
            MFMFindFile.matchDirectory = matchDirectory;
            Files.walkFileTree(directory, EnumSet.noneOf(FileVisitOption.class), FileUtils.maxDepth, this);
        }

        public MFMFindFile(Path directory, Path fileName) throws IOException {
            new MFMFindFile(directory, fileName, matchWithoutSuffix, false);
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

        public static boolean exists() {
            return exists;
        }

        public static Path file() {
            return file;
        }

        @SuppressWarnings("unchecked")
        public static ArrayList<Path> files() {
            return (ArrayList<Path>) files.clone();
        }

        @SuppressWarnings("unchecked")
        public static HashMap<String, String> directories() {
            return (HashMap<String, String>) directories.clone();
        }

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
                    MFMFindFile.file = file;
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
            System.err.println(exc.getMessage());
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
            file = null;
        }
    }

}
