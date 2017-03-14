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

import Phweda.MFM.MFMSettings;
import Phweda.MFM.MFM_Constants;
import Phweda.utils.ClickListener;
import Phweda.utils.FileUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 5/29/2015
 * Time: 7:25 PM
 */
class FFMPEG_Panel extends JPanel {

    private static JDialog dialog = null;
    private JFileChooser fileChooser = new JFileChooser();
    private SettingsController controller = new SettingsController();
    private JTextField FFmpegEXE = new JTextField();
    private JTextField FFmpegInputFolder = new JTextField();
    private JTextField FFmpegOutputFolder = new JTextField();
    private JTextField FFmpegMoveAVItoFolder = new JTextField();
    private Font font = new Font("Arial", Font.BOLD, 16);
    private JButton runCommandButton;

    FFMPEG_Panel() {
        this.setLayout(new GridLayout(10, 2, 10, 10));
        createPanel();
        this.setBackground(MFMUI.getMFMSettingsBGcolor());
        this.setComponentsFont(this.getComponents());
    }

    private void createPanel() {
        fileChooser.setPreferredSize(new Dimension(600, 400));
        setComponentsFont(fileChooser.getComponents());
        this.setName(MFM_Constants.FFmpeg_SETTINGS);

        /* NOTE using the <HTML> tags to trick JLabel into text wrap */
        JLabel label1 = new JLabel("<HTML>Select FFmpeg Executable</HTML>");
        FFmpegEXE.setName(MFM_Constants.FFMPEG_EXE);
        FFmpegEXE.addMouseListener(controller);
        FFmpegEXE.setToolTipText("Click here to select your FFmpeg executable file");

        JLabel label2 = new JLabel("<HTML>Select FFmpeg input folder</HTML>");
        FFmpegInputFolder.setName(MFM_Constants.FFmpeg_INPUT_FOLDER);
        FFmpegInputFolder.addMouseListener(controller);
        FFmpegInputFolder.setToolTipText("Click here to select FFmpeg input folder");

        JLabel label3 = new JLabel("<HTML>Select FFmpeg output folder</HTML>");
        FFmpegOutputFolder.setName(MFM_Constants.FFmpeg_OUTPUT_FOLDER);
        FFmpegOutputFolder.addMouseListener(controller);
        FFmpegOutputFolder.setToolTipText("Click here to select FFmpeg output folder");

        JLabel label4 = new JLabel("<HTML>Optional folder to move AVI file to</HTML>");
        FFmpegMoveAVItoFolder.setName(MFM_Constants.FFmpeg_MoveAVIto_Folder);
        FFmpegMoveAVItoFolder.addMouseListener(controller);
        FFmpegMoveAVItoFolder.setToolTipText("Click here to select folder to move AVI file to");

        JButton saveB = new JButton("<HTML>Save " + MFM_Constants.FFmpeg_SETTINGS + "</HTML>");
        saveB.setName("Save");
        JButton cancelB = new JButton("Cancel");
        cancelB.setName("Cancel");
        ActionListener settingsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (((JButton) e.getSource()).getName().equals("Save")) {
                    MFMSettings.FFmpegEXEdir(FFmpegEXE.getText().substring(0,
                            FFmpegEXE.getText().lastIndexOf(FileUtils.DIRECTORY_SEPARATOR)));
                    // Full Path
                    MFMSettings.FFMPEGexe(FFmpegEXE.getText());
                    MFMSettings.FFmpegOutputFolder(FFmpegOutputFolder.getText());
                    MFMSettings.FFmpegInputFolder(FFmpegInputFolder.getText());
                    MFMSettings.setFFmpegMoveAVItoFolder(FFmpegMoveAVItoFolder.getText());
                    runCommandButton.setEnabled(isConfigured());
                } else if (((JButton) e.getSource()).getName().equals("Cancel")) {
                    dialog.dispose();
                }
            }
        };

        saveB.addActionListener(settingsListener);
        cancelB.addActionListener(settingsListener);

        runCommandButton = new JButton(new MFMAction(MFMAction.ConvertCommandAction, null));
        runCommandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setEnabled(false);
                dialog.dispose();
            }
        });

        // Start disabled. Enable when we have 3 values where?? refreshSettings??
        runCommandButton.setEnabled(isConfigured());

        this.add(label1);
        this.add(FFmpegEXE);
        this.add(label2);
        this.add(FFmpegInputFolder);
        this.add(label3);
        this.add(FFmpegOutputFolder);
        this.add(label4);
        this.add(FFmpegMoveAVItoFolder);

        // NOTE Hack to trick GridLayout - should really do another layout manager
        this.add(MFMUI_Setup.getFillPanel());
        this.add(MFMUI_Setup.getFillPanel());
        JPanel buttonPanel = new JPanel();

        buttonPanel.add(saveB);
        buttonPanel.add(new JLabel("\t\t"));
        buttonPanel.add(runCommandButton);
        buttonPanel.add(new JLabel("\t\t"));
        buttonPanel.add(cancelB);
        buttonPanel.setBackground(MFMUI.getMFMSettingsBGcolor());

        this.add(buttonPanel);
        this.setPreferredSize(new Dimension(800, 500));
        this.setMinimumSize(new Dimension(800, 500));

        TitledBorder tb = BorderFactory.createTitledBorder(
                new BevelBorder(BevelBorder.RAISED, Color.RED,
                        Color.orange, Color.BLACK, Color.DARK_GRAY), MFM_Constants.FFmpeg_SETTINGS);
        tb.setTitleJustification(TitledBorder.CENTER);
        tb.setTitleFont(font);
        tb.setTitleColor(new Color(81, 222, 255));
        this.setBorder(tb);

        if (MFMSettings.getInstance().isLoaded()) {
            if (MFMSettings.FFmpegEXEdir() != null) {
                FFmpegEXE.setText(MFMSettings.FFMPEGexe());
                if (MFMSettings.FFmpegInputFolder() != null) {
                    FFmpegInputFolder.setText(MFMSettings.FFmpegInputFolder());
                }
                if (MFMSettings.FFmpegOutputFolder() != null) {
                    FFmpegOutputFolder.setText(MFMSettings.FFmpegOutputFolder());
                    runCommandButton.setEnabled(isConfigured());
                }
                if (MFMSettings.getFFmpegMoveAVItoFolder() != null) {
                    FFmpegMoveAVItoFolder.setText(MFMSettings.getFFmpegMoveAVItoFolder());
                }
            }
        }
    }

    private boolean isConfigured() {
        return (MFMSettings.FFMPEGexe() != null && MFMSettings.FFMPEGexe().length() > 3 &&
                MFMSettings.FFmpegInputFolder() != null && MFMSettings.FFmpegInputFolder().length() > 3 &&
                MFMSettings.FFmpegOutputFolder() != null && MFMSettings.FFmpegOutputFolder().length() > 3
        );
    }

    final void showSettingsPanel(JFrame frame) {
        dialog = new JDialog(frame, true);
        dialog.setContentPane(new FFMPEG_Panel());
        dialog.pack();
        dialog.setLocation(250, 250);
        dialog.setVisible(true);
    }

    // fixme why did I do this here??
    private void setComponentsFont(Component[] comp) {
        for (Component aComp : comp) {
            if (aComp instanceof Container) setComponentsFont(((Container) aComp).getComponents());
            try {
                aComp.setFont(font);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SettingsController extends ClickListener {

        @Override
        public void singleClick(MouseEvent e) {

        }

        @Override
        public void doubleClick(MouseEvent e) {
            String fieldName = ((JTextField) e.getSource()).getName();
            if (fieldName.equalsIgnoreCase(FFmpegEXE.getName())) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.showDialog(FFMPEG_Panel.this, JFileChooser.APPROVE_SELECTION);
            } else {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.showDialog(FFMPEG_Panel.this, JFileChooser.APPROVE_SELECTION);
            }
            File file = fileChooser.getSelectedFile();
            String path;
            switch (fieldName) {

                case MFM_Constants.FFMPEG_EXE:
                    FFmpegEXE.setText(file.getAbsolutePath());
                    break;

                case MFM_Constants.FFmpeg_INPUT_FOLDER:
                    FFmpegInputFolder.setText(file.getAbsolutePath());
                    break;

                case MFM_Constants.FFmpeg_OUTPUT_FOLDER:
                    path = file.getAbsolutePath();
                    FFmpegOutputFolder.setText(path);
                    break;

                case MFM_Constants.FFmpeg_MoveAVIto_Folder:
                    path = file.getAbsolutePath();
                    FFmpegMoveAVItoFolder.setText(path);
                    break;
            }
        }
    }
}


