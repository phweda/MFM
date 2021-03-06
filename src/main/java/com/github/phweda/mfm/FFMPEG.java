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

import com.github.phweda.mfm.ui.MFM_Components;
import com.github.phweda.mfm.mame.Machine;
import com.github.phweda.utils.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 4/17/2015
 * Time: 10:09 PM
 */
@SuppressWarnings("squid:UnusedPrivateMethod")
public class FFMPEG {

    /*
    0 is EXE, 2 is AVI file name, 6 is Aspect Ratio value, 16 is output file name
     */
    private static final String[] baseArgs = new String[]{"", "-i", "", "-c:v",
            "libx264", "-aspect", "4:3", "-preset", "veryslow", "-qp", "0",
            "-c:a", "libfdk_aac", "-cutoff", "20000", "-y", ""
    };
    /*
    FFmpeg CROP COMMAND LINE
    ffmpeg -i <fileIn>.avi -c:a copy -c:v rawvideo -pix_fmt bgr24 -filter:v "crop=524:978:0:0" -y <fileOut>.avi
     */
    private static final String[] baseCropArgs = new String[]{"", "-i", "", "-c:a",
            "copy", "-c:v", "rawvideo", "-pix_fmt", "bgr24", "-filter:v", "", "-y", ""
    };
    static FFMPEG_Output ffmpegOutput = new FFMPEG_Output();
    private static FFMPEG ourInstance;
    private static ArrayList<String> args;
    private static Process process = null;
    private static String ffmpegexe;
    private static File inputFolder;

    private static MFMSettings mfmSettings = MFMSettings.getInstance();

    /*  COMMAND LINE
         ffmpeg.exe -i <fileIn>.avi -c:v libx264 -aspect 3:4 -preset veryslow
         -qp 0 -c:a libfdk_aac -cutoff 20000 -y <fileOut>.mp4
    */
    private static File outputFolder;
    private static File moveAVItoFolder;


    /* Ensure args has been initialized */
    private FFMPEG() {
        if (args == null) {
            args = new ArrayList<>(Arrays.asList(baseArgs));
        }
        refreshSettings(); // Ensures we have the exe and input/output folders
    }

    public static FFMPEG getInstance() {
        return ourInstance == null ? new FFMPEG() : ourInstance;
    }

    private static File getInputFolder() {
        return inputFolder;
    }

    private static void setInputFolder(File inputFolder) {
        FFMPEG.inputFolder = inputFolder;
    }

    private static File getOutputFolder() {
        return outputFolder;
    }

    private static void setOutputFolder(File outputFolder) {
        FFMPEG.outputFolder = outputFolder;
    }

    /**
     * Load FFmpeg EXE and input/output folders for conversion
     */
    private static void refreshSettings() {
        ffmpegexe = mfmSettings.FFMPEGexe();
        args.set(0, ffmpegexe);

        inputFolder = new File(mfmSettings.FFmpegInputFolder());
        outputFolder = new File(mfmSettings.FFmpegOutputFolder());

        String moveAVItoFolderPath = mfmSettings.getFFmpegMoveAVItoFolder();
        if (moveAVItoFolderPath != null && !moveAVItoFolderPath.isEmpty()) {
            moveAVItoFolder = new File(moveAVItoFolderPath);
        }
    }

    private static Process run(Object output) {

        ProcessBuilder pb = new ProcessBuilder(args);
        if (output instanceof File)

            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter((File) output, true)));
                pw.println("*************************************************");
                pw.println(pb.command().toString());
                // Must flush or close otherwise output will be blocked by the PB
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        try {
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo((File) output));
            pb.redirectError(ProcessBuilder.Redirect.appendTo((File) output));
            MFM.getLogger().addToList(pb.command().toString(), true);
            pb.directory(new File(mfmSettings.FFmpegEXEdir()));
            process = pb.start();
        } catch (ClassCastException | IOException exc) {
            exc.printStackTrace();

        }
        return process;
    }

    /**
     * Takes all .avi files in the input folder and
     * converts each with FFmpeg to the output folder
     */
    public void convertAll() {

        File[] inputFiles = getInputFolder().listFiles(FileUtils.AVIFilenameFilter);
        String outputFolderPath = getOutputFolder().getAbsolutePath();
        String inputFolderPath = getInputFolder().getAbsolutePath();

        if (inputFiles != null && inputFiles.length > 0) {
            // For each file convert
            for (File file : inputFiles) {
                String fileName = file.getName();
                String gameName = fileName.substring(0, fileName.lastIndexOf('.'));
                Machine machine = MAMEInfo.getMachine(gameName);

                args.set(2, inputFolderPath + FileUtils.DIRECTORY_SEPARATOR + fileName);
                args.set(6, MAMECommands.aspectRatio(machine));
                args.set(16, outputFolderPath + FileUtils.DIRECTORY_SEPARATOR + fileName.replace(".avi", ".mp4"));
                //    System.out.println(args);

                final Process process = run(MFM.getfFmpegout());

                /**
                 * Pause waiting for process to complete.
                 * This is to accomplish only starting FFmpeg once at a time
                 */
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                if (moveAVItoFolder != null && moveAVItoFolder.exists()) {
                    try {
                        FileUtils.moveFile(file, moveAVItoFolder.getAbsolutePath(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        MFM_Components.infoPanel().showMessage("Files Converted");
        MFM_Components.infoPanel().updateUI();
        MFM.getLogger().addToList("FFmpeg processing complete", true);
    }

    /**
     * MAME outputs some unusual AVI frame sizes i.e. not multiples of 2
     * VDub chokes on these. So added this to crop the AVI via FFmpeg
     * http://forum.pleasuredome.org.uk/index.php?showtopic=26823&p=228363
     */
    public static void cropAVI(File file, Machine machine) {
        // Backup input file
        try {
            Path source = file.toPath();
            Files.copy(source, source.resolveSibling(file.getName() + ".BAK"));
            // file = new File(String.valueOf(source) + ".BAK");
        } catch (IOException e) {
            e.printStackTrace();
        }


    /*
        FFmpeg CROP COMMAND LINE
        ffmpeg -i breakout.avi -c:a copy -c:v rawvideo -pix_fmt bgr24 -filter:v "crop=524:978:0:0" -y test.avi
     */
        ArrayList<String> cropArgs = new ArrayList<>(Arrays.asList(baseCropArgs));

    /*
        0 is EXE, 2 is AVI file name, 10 is the file specific cropping value, 12 is output file name
    */
        cropArgs.set(0, ffmpegexe);
        cropArgs.set(2, file.getAbsolutePath());
        cropArgs.set(10, aviCropArg(machine));
        cropArgs.set(12, file.getParent() + FileUtils.DIRECTORY_SEPARATOR + machine.getName() + "2.avi");

        // Set global args
        args = (ArrayList<String>) cropArgs.clone();
        final Process process;
        try {
            process = run(MFM.getfFmpegout());

            /**
             * Pause waiting for process to complete.
             * This is to accomplish only starting FFmpeg once at a time
             */
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // refresh to base args
        args = new ArrayList<>(Arrays.asList(baseArgs));
    }

    /**
     * Create FFmpeg crop argument String
     * "crop=524:978:0:0"
     *
     * @param machine whose avi is to be cropped
     * @return String constructed ffmpeg arg
     */
    private static String aviCropArg(Machine machine) {
        StringBuilder sb = new StringBuilder("crop=");
        int width = Integer.getInteger(machine.getWidth()) % 2 == 0 ?
                Integer.getInteger(machine.getWidth()) : Integer.getInteger(machine.getWidth()) - 1;
        int height = Integer.getInteger(machine.getHeight()) % 2 == 0 ?
                Integer.getInteger(machine.getHeight()) : Integer.getInteger(machine.getHeight()) - 1;

        if (machine.getIsVertical().equalsIgnoreCase("Vertical")) {
            sb.append(height);
            sb.append(":");
            sb.append(width);
            sb.append(":0:0");
        } else {
            sb.append(width);
            sb.append(":");
            sb.append(height);
            sb.append(":0:0");
        }

        return sb.toString();
    }

    private static class FFMPEG_Output {
        String args;
        private BufferedInputStream bis;
        private ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        private byte[] separator = new byte[]{'\n', '\t', 0x2a, 0x2a, 0x2a, 0x2a};
        private boolean error = false;

        void setInput(InputStream inputStream, String argsIn) throws FFMPEG_Exception {
            if (inputStream == null) {
                System.err.println("inputStream is NULL");
            }
            args = argsIn;
            if (inputStream == null) {
                return;
            }
            bis = new BufferedInputStream(inputStream);
            read();
        }

        private void read() throws FFMPEG_Exception {
            int read = 0;

            for (byte aSeparator : separator) {
                baos.write(aSeparator);
            }

            try {
                while ((read = bis.read()) != -1) {
                    if (!error) {
                        // System.out.println("read is '" + read + "'");
                        error = true;
                        baos.write(args.getBytes(), 0, args.getBytes().length);
                        baos.write('\n');
                    }
                    baos.write(read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (error) {
                error = false;
                throw new FFMPEG_Exception(baos.toString());
            }
        }
    }

    public static class FFMPEG_Exception extends Exception {
        String ffmpegError = "";

        FFMPEG_Exception() {
            super();
            ffmpegError = "Unknown FFmpeg error";
        }

        FFMPEG_Exception(String error) {
            super();
            ffmpegError = error.contains("*") ? error.substring(error.lastIndexOf('*') + 1) : error;
        }

        public String getError() {
            return ffmpegError;
        }
    }

}
