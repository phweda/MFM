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
import Phweda.MFM.MFMSettings;
import Phweda.MFM.UI.ImagesViewer;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

// import javax.media.format.VideoFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 3/30/2015
 * Time: 9:41 PM
 */
public class VideoUtils {

    private static MFMSettings mfmSettings = MFMSettings.getInstance();

    public static final FileFilter GIFfilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            return f.isDirectory() | f.getName().toLowerCase().endsWith(".gif");
        }

        @Override
        public String getDescription() {
            return "GIF files";
        }
    };
    public static final FileFilter AVIfilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            return f.isDirectory() | f.getName().toLowerCase().endsWith(".avi");
        }

        @Override
        public String getDescription() {
            return "AVI files";
        }
    };

    public static void showGIFimages(File file) {
        if (file.exists()) {
            try {
                final ImagesViewer imagesViewer = new ImagesViewer(getFrames(file), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    static ArrayList<BufferedImage> getFrames(File gif) throws IOException {
        ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
        ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
        ir.setInput(ImageIO.createImageInputStream(gif));
        for (int i = 0; i < ir.getNumImages(true); i++) {
            frames.add(ir.read(i));
        }
        // Release resources for Garbage Collection
        ir.dispose();
        return frames;
    }


    //TODO Separate thread!!! WELL maybe not
    public static void showAVIimages(File file) {
        if (file.exists()) {
            String URLpath = "file://" + file.getAbsolutePath();
            System.out.println(URLpath);
            VideoSource vs = new VideoSource(URLpath);
            vs.initialize();
            ArrayList<BufferedImage> frames = null;
            try {
                frames = vs.getFrames();
            } catch (Error error) {
                error.printStackTrace();
                if (error.getMessage().contains("OutOfMemoryError")) {
                    JOptionPane.showMessageDialog(null, "Not enough Memory!\n"
                            + "Try a smaller file or increase the heap size,\n" +
                            "-Xmx256m to -Xmx2048m, in your MFM.bat file?");
                }
            }
            if (frames == null) {
                JOptionPane.showMessageDialog(null, "AVI failed to load try again.\n"
                        + "Try a smaller file and did you increase the heap size,\n" +
                        "-Xmx256m to -Xmx2048m (or more), in your MFM.bat file?");
                return;
            }
            frames.trimToSize();
            if (frames.size() > 0) {
                final ImagesViewer imagesViewer = new ImagesViewer(frames, file);
            }
            // Release for Garbage Collection
            // NOTE should we add a dispose method to VideoSource?
            vs = null;
        }
    }

    public static void runVirtualDub(String filePath) {
        if (mfmSettings.VDubexe() == null || mfmSettings.VDubexe().equals("")) {
            // JOptionPane.showMessageDialog(null, "MFM needs your VirtualDub executable");

            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setDialogTitle("Select VirtualDub executable");
            int returnValue = fc.showDialog(null, "OK");

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                mfmSettings.VDubexe(file.getAbsolutePath());
            } else {
                return;
            }
        }
        MFM.logger.addToList("VDub file to run is : " + filePath);
        File aviFile = new File(filePath);
        ProcessBuilder pb;
        if (!aviFile.exists()) {
            //  JOptionPane.showMessageDialog(null, "File not found: " + file);
            pb = new ProcessBuilder(mfmSettings.VDubexe());
        } else {
            pb = new ProcessBuilder(mfmSettings.VDubexe(), filePath);
        }
        try {
            pb.start();
        } catch (IOException e) {
            // e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}
