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

package Phweda.MFM.UI;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 10/25/2016
 * Time: 5:30 PM
 */

import Phweda.MFM.FFMPEG;
import Phweda.MFM.MAMEInfo;
import Phweda.MFM.MFM;
import Phweda.MFM.MFMSettings;
import Phweda.utils.FileUtils;
import Phweda.utils.VideoUtils;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;

import static Phweda.MFM.UI.MFMController.getFrame;
import static Phweda.MFM.UI.MFMController.showInformation;

/**
 * Factored out of MFMController Oct 2016
 */
class MFMVideoActions {

    static void showVideo(String gameName) {
        // TODO refactor so we can do the following efficiently
        Path fullPath = null;
        File snapFolder = new File(MFMSettings.getInstance().getPlaySetDir() +
                FileUtils.DIRECTORY_SEPARATOR + "snap" + FileUtils.DIRECTORY_SEPARATOR);

        File VIDsfolder = new File(MFMSettings.getInstance().VIDsFullSetDir() + FileUtils.DIRECTORY_SEPARATOR);

        if (MFM.isDebug()) {
            MFM.logger.addToList(snapFolder.getAbsolutePath() + " : " + VIDsfolder.getAbsolutePath()
                    , true);
            MFM.logger.addToList(gameName, true);
        }

        // Quit if it is not a directory
        if ((!VIDsfolder.exists() || !VIDsfolder.isDirectory()) &&
                (!snapFolder.exists() || !snapFolder.isDirectory())) {
            showInformation("Video INIfiles", "No snap or videos folder found check your MFM and MAME settings ");
            return;
        }
        File[] snapVIDfiles = snapFolder.listFiles(FileUtils.VideoFilenameFilter);

        // TODO fix with MFMFileOps to search sub folders
        File[] VIDfiles = VIDsfolder.listFiles(FileUtils.VideoFilenameFilter);
        if ((snapVIDfiles != null && snapVIDfiles.length > 0) ||
                (VIDfiles != null && VIDfiles.length > 0)) {
            if (snapVIDfiles != null && snapVIDfiles.length > 0) {
                for (File file : snapVIDfiles) {
                    if (file.getName().contains(gameName)) {
                        fullPath = file.toPath();
                        break;
                    }
                }
            } else if (VIDfiles != null && VIDfiles.length > 0) {
                for (File file : VIDfiles) {
                    if (file.getName().contains(gameName)) {
                        fullPath = file.toPath();
                        break;
                    }
                }
            }
        }

        if (fullPath != null) {
            FileUtils.openFileFromOS(fullPath);
        } else {
            showInformation(gameName, "No video found for " + gameName);
        }
    }

    static void runFFmpeg() {
        try {
            MFM_Components.InfoPanel().showProgress("Converting Files");
            MFM_Components.InfoPanel().updateUI();

            SwingWorker sw = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    /*    JOptionPane.showMessageDialog(frame, "We are in SW doInBackground" +
                          SwingUtilities.isEventDispatchThread());
                    */
                    FFMPEG.getInstance().convertAll();
                    return true;
                }

                @Override
                protected void done() {
                    super.done();
                    //    JOptionPane.showMessageDialog(frame, "We are past SW");
                }
            };
            Thread fileOps = new Thread(sw);
            fileOps.start();

            // dialog.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void CropAVI(String machine, MFMInformationPanel infoPanel) {
        final String path = MFMSettings.getInstance().getPlaySetDir() + FileUtils.DIRECTORY_SEPARATOR + "snap" +
                FileUtils.DIRECTORY_SEPARATOR + machine + ".avi";
        final File machineVideo = new File(path);
        if (machineVideo.exists()) {
            MFM.logger.addToList(path + " is being cropped", true);
            infoPanel.showProgress("Cropping Video");
            infoPanel.updateUI();

            SwingWorker sw = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    /*    JOptionPane.showMessageDialog(frame, "We are in SW doInBackground" +
                            SwingUtilities.isEventDispatchThread());
                    */
                    FFMPEG.getInstance().cropAVI(machineVideo, MAMEInfo.getMachine(machine));
                    return true;
                }

                @Override
                protected void done() {
                    super.done();
                    //    JOptionPane.showMessageDialog(frame, "We are past SW");
                    infoPanel.showMessage(path + " has been cropped");
                    infoPanel.updateUI();
                    MFM.logger.addToList("Cropping finished", true);
                }
            };
            Thread ffmpegOps = new Thread(sw);
            ffmpegOps.start();
        } else {
            JOptionPane.showMessageDialog(getFrame(), path + "\nNot found.");
        }
    }

    static void videoAction(final String action, MFMInformationPanel infoPanel) {
        String filePath = null;
        JFileChooser chooser = new JFileChooser();
        if (action.equals(MFMAction.AVIImagesAction)) {
            chooser.setFileFilter(VideoUtils.AVIfilter);
        } else if (action.equals(MFMAction.GIFImagesAction)) {
            chooser.setFileFilter(VideoUtils.GIFfilter);
        }

        int returnvalue = chooser.showOpenDialog(null);
        if (returnvalue == JFileChooser.APPROVE_OPTION) {
            filePath = chooser.getSelectedFile().getAbsolutePath();
        } else {
            return;
        }

        final File file = new File(filePath);
        infoPanel.showProgress(action);
        infoPanel.updateUI();
        MFM.logger.addToList(action + " started", true);
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                    /*    JOptionPane.showMessageDialog(frame, "We are in SW doInBackground" +
                            SwingUtilities.isEventDispatchThread());
                    */
                if (action.equals(MFMAction.GIFImagesAction)) {
                    VideoUtils.showGIFimages(file);
                } else if (action.equals(MFMAction.AVIImagesAction)) {
                    VideoUtils.showAVIimages(file);
                }
                return true;
            }

            @Override
            protected void done() {
                super.done();
                //    JOptionPane.showMessageDialog(frame, "We are past SW");
                infoPanel.showMessage(action + " completed");
                infoPanel.updateUI();
                MFM.logger.addToList(action + " completed", true);
            }
        };
        Thread videoOps = new Thread(sw);
        videoOps.start();
    }
}
