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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/25/2015
 * Time: 2:30 PM
 */

public class ZipUtils {

    List<String> fileList = new ArrayList<String>();
    String sourceFolder;

    /**
     * Extracts and copies single file from ZIP
     *
     * @param zipFile    the source
     * @param fileName   file to extract
     * @param outputFile destination
     * @throws IOException
     */
    public static boolean extractFile(Path zipFile, String fileName, Path outputFile) throws IOException {
        // Wrap the file system in a try-with-resources statement
        // to auto-close it when finished and prevent a memory leak
        try (FileSystem fileSystem = FileSystems.newFileSystem(zipFile, null)) {
            Path fileToExtract = fileSystem.getPath(fileName);
            if (!Files.exists(fileToExtract)) { // LinkOption.NOFOLLOW_LINKS
                if (MFM.isSystemDebug()) {
                    System.out.println("Zip file not found is: " + fileToExtract);
                }
                return false;
            }
            if (MFM.isSystemDebug()) {
                System.out.println("Zip file to extract is: " + fileToExtract);
                System.out.println("Zip file extracting to: " + outputFile);
            }
            Files.copy(fileToExtract, outputFile, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (NoSuchFileException exc) {
            exc.printStackTrace();
            return false;
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }
    }

    private static TreeMap<String, String> getZipEntries(String zip) {
        TreeMap<String, String> zipFileNames;
        try (ZipFile zipFile = new ZipFile(zip)) {
            Predicate<ZipEntry> isFile = ze -> !ze.isDirectory();

            List<ZipEntry> zipEntries = zipFile.stream()
                    .filter(isFile)
                    .collect(Collectors.toList());

            zipFileNames = new TreeMap<String, String>();
            zipEntries.forEach(entry -> zipFileNames.put(
                    entry.getName().substring(0, entry.getName().lastIndexOf('.')), entry.getName()));
            return zipFileNames;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TreeMap<String, TreeMap<String, String>> getZipEntryNames(HashMap<String, File> extrasZipFilesMap) {
        final TreeMap<String, TreeMap<String, String>> zipEntries = new TreeMap<String, TreeMap<String, String>>();
        extrasZipFilesMap.forEach((key, file) -> {
            zipEntries.put(key, getZipEntries(file.getAbsolutePath()));
        });
        return zipEntries;
    }

    /**
     * Zip it - recursive
     *
     * @param zipFile      output ZIP file location
     * @param filesToZip   file or folder to zip
     * @param sourceFolder folder of file(s)
     */
    public void zipIt(String zipFile, File filesToZip, String sourceFolder) {
        this.sourceFolder = sourceFolder + File.separator;
        generateFileList(filesToZip);

        byte[] buffer = new byte[1048576]; // 8192

        try {
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + zipFile);

            for (String file : this.fileList) {
                System.out.println("\t" + file);
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in = new FileInputStream(this.sourceFolder + file);
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
            }

            zos.closeEntry();
            //remember close it
            zos.close();
            System.out.println("Zipping Done");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Traverse a directory and get all files,
     * and add the file into fileList
     *
     * @param file file or directory
     */
    private void generateFileList(File file) {

        //add file only
        if (file.isFile()) {
            fileList.add(generateZipEntry(file.getAbsoluteFile().toString()));
        }

        if (file.isDirectory()) {
            String[] files = file.list();
            for (String filename : files) {
                if (!filename.endsWith(".zip")) {
                    generateFileList(new File(file, filename));
                }
            }
        }
    }

    /**
     * Format the file path for zip
     *
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file) {
        return file.substring(sourceFolder.length() - 1, file.length());
    }


// Java 8 zip folder untested
/*
    public static void zipFolder(String sourceDirPath, String zipFilePath) throws IOException {
        Path p = Files.createFile(Paths.get(zipFilePath));

        ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p));
        try {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        String sp = path.toAbsolutePath().toString().replace(pp.toAbsolutePath().toString(), "")
                                .replace(path.getFileName().toString(), "");
                        ZipEntry zipEntry = new ZipEntry(sp + "/" + path.getFileName().toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            zs.write(Files.readAllBytes(path));
                            zs.closeEntry();
                        } catch (Exception e) {
                            System.err.println(e);
                        }
                    });
        } finally {
            zs.close();
        }
    }

*/

}