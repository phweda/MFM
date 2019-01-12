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

package com.github.phweda.MFM.UI;

import com.github.phweda.MFM.*;
import com.github.phweda.utils.ClickListener;
import com.github.phweda.utils.FileUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/18/11
 * Time: 7:07 PM
 */
final class MFM_SettingsPanel extends JPanel {

    private static final transient Font myFont = new Font("Arial", Font.BOLD, 16);
    private static final Dimension size = new Dimension(1024, 768);
    private static JFrame frame;
    private JFileChooser fileChooser = new JFileChooser();
    private transient SettingsController controller = new SettingsController();
    private JTextField mameExePath = new JTextField();
    private JTextField mameRomsPath = new JTextField();
    private JTextField softwareListRomsPath = new JTextField();
    private JTextField mameCHDsPath = new JTextField();
    private JTextField softwareListCHDsPath = new JTextField();
    private JTextField mameExtrasPath = new JTextField();
    private JTextField mameVidsPath = new JTextField();
    private JTextField mamePlayPath = new JTextField();
    private JTextField vDubPath = new JTextField();
    private JTextField ffmpegPath = new JTextField();
    private JCheckBox nonmergedCB = new JCheckBox("MAME ROMs NON-MERGED?");
    private static final String CANCEL = "Cancel";

    private static MFMSettings mfmSettings = MFMSettings.getInstance();

    private MFM_SettingsPanel() {

        this.setLayout(new GridLayout(0, 2, 4, 10));
        this.setBorder(new EmptyBorder(10, 20, 10, 10));
        try {
            createPanel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setBackground(MFMUI.getMFMSettingsBGcolor());
    }

    static void showSettingsPanel(JFrame frameIn) {
        frame = frameIn;
        frame.setContentPane(new MFM_SettingsPanel());
        setComponentsFont(frame.getComponents());
        frame.setLocation(MFMUI.screenCenterPoint.x - size.width / 2, MFMUI.screenCenterPoint.y - size.height / 2);
        frame.pack();
        frame.setVisible(true);
        //    frame.setAlwaysOnTop(true);
    }

    private static void setComponentsFont(Component[] comp) {
        for (Component aComp : comp) {
            if (aComp instanceof Container) setComponentsFont(((Container) aComp).getComponents());
            try {
                aComp.setFont(myFont);
            } catch (Exception e) {
                e.printStackTrace();
            }//do nothing
        }
    }

    private void createPanel() {
        fileChooser.setPreferredSize(new Dimension(640, 480));
        fileChooser.setApproveButtonText("OK");

        this.setName(MFM_Constants.MFM_SETTINGS);
        /* NOTE using the <HTML> tags to trick JLabel into text wrap */
        JLabel mameExelabel = new JLabel("<HTML>Select MAME Executable</HTML>");
        mameExePath.setName(MFM_Constants.MAME_EXE_DIRECTORY);
        mameExePath.addMouseListener(controller);
        mameExePath.setToolTipText("Click here and select MAME executable file");

        JLabel mameRomsLabel = new JLabel(
                "<HTML>Select root Directory full MAME Rom set</HTML>");
        mameRomsPath.setName(MFM_Constants.ROMS_FULL_SET_DIRECTORY);
        mameRomsPath.addMouseListener(controller);
        mameRomsPath.setToolTipText("Click here and select your MAME ROMs full set root directory");

        JLabel mameCHDsLabel = new JLabel(
                "<HTML>Select root Directory full MAME CHD set</HTML>");
        mameCHDsPath.setName(MFM_Constants.CHDS_FULL_SET_DIRECTORY);
        mameCHDsPath.addMouseListener(controller);
        mameCHDsPath.setToolTipText("Click here to select MAME CHD full set root directory");

        JLabel mameExtraslabel = new JLabel(
                "<HTML>Select root Directory full MAME Extras set</HTML>");
        mameExtrasPath.setName(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY);
        mameExtrasPath.addMouseListener(controller);
        mameExtrasPath.setToolTipText("Click here and select your MAME Extras full set root directory");

        JLabel mameVideosLabel = new JLabel(
                "<HTML>Select Directory of your MAME videos</HTML>");
        mameVidsPath.setName(MFM_Constants.MAME_VIDS_DIRECTORY);
        mameVidsPath.addMouseListener(controller);
        mameVidsPath.setToolTipText("Click here and select the Directory of your MAME videos");

        JLabel mamePlayPathLabel = new JLabel("<HTML>Select Playset Directory to place MAME resources</HTML>");
        mamePlayPath.setName(MFM_Constants.PLAYSET_ROOT_DIRECTORY);
        mamePlayPath.addMouseListener(controller);
        mamePlayPath.setToolTipText("Click here to set the Directory where you want your Playset");

        JLabel softwarelistRomsLabel = new JLabel("<HTML>Select root Directory full Software List ROMs</HTML>");
        softwareListRomsPath.setName(MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY);
        softwareListRomsPath.addMouseListener(controller);
        softwareListRomsPath.setToolTipText("Click here and select your Software List ROMs directory");

        JLabel softwarelistCHDsLabel = new JLabel("<HTML>Select root Directory full Software List CHDs</HTML>");
        softwareListCHDsPath.setName(MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY);
        softwareListCHDsPath.addMouseListener(controller);
        softwareListCHDsPath.setToolTipText("Click here and select your Software List CHDs directory");

/*
        JLabel label9 = new JLabel("<HTML>Select VirtualDub Executable</HTML>");
        vDubPath.setName(MFM_Constants.VIRTUALDUB_EXE);
        vDubPath.addMouseListener(controller);
        vDubPath.setToolTipText("Click here and select your VirtualDub executable file");

        JLabel label10 = new JLabel("<HTML>Select FFmpeg Executable</HTML>");
        ffmpegPath.setName(MFM_Constants.FFMPEG_EXE);
        ffmpegPath.addMouseListener(controller);
        ffmpegPath.setToolTipText("Click here and select your VirtualDub executable file");
*/
        JButton saveB = new JButton("<HTML>Save MFM Settings</HTML>");
        saveB.setName("Save");
        JButton cancelB = new JButton(CANCEL);
        cancelB.setName(CANCEL);

//*************************** SAVE SETTINGS**********************************************************

        ActionListener settingsListener = event -> {
            if (((JButton) event.getSource()).getName().equals("Save")) {

                MFM.getLogger().separateLine();
                MFM.getLogger().addToList("Save Settings command received", true);
                MFM.getLogger().addToList("ROMs path: " + mameRomsPath.getText(), true);
                MFM.getLogger().addToList("Software List ROMs path: " + softwareListRomsPath.getText(), true);
                MFM.getLogger().addToList("Play path: " + mamePlayPath.getText(), true);
                MFM.getLogger().addToList("MAME EXE path: " + mameExePath.getText(), true);
                MFM.getLogger().addToList("CHD path: " + mameCHDsPath.getText(), true);
                MFM.getLogger().addToList("Software List CHDs path: " + softwareListCHDsPath.getText(), true);
                MFM.getLogger().addToList("Extras path: " + mameExtrasPath.getText(), true);
                MFM.getLogger().addToList("VIDs path: " + mameVidsPath.getText(), true);

                // NOTE maintain order!!! See MFMSettings.MAMEexeDir() first run for create config
                String mameexe = mameExePath.getText();
                if ((mameRomsPath.getText().isEmpty() && mameCHDsPath.getText().isEmpty() &&
                        softwareListRomsPath.getText().isEmpty() && softwareListCHDsPath.getText().isEmpty())
                        || mamePlayPath.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "1 Root ROMs/CHDs plus PlaySet Directory path required.");
                    return;
                }


                if (MFM_Data.getInstance().notLoaded() && (mameexe.length() < 6)) {
                    JOptionPane.showMessageDialog(frame,
                            "MFM Data not detected MAME.exe must be entered.");
                    return;
                }

                if (!mameexe.isEmpty() && mameexe.length() > 5 && mameexe.contains(FileUtils.DIRECTORY_SEPARATOR)) {
                    mfmSettings.MAMEexeName(mameexe.substring(
                            mameexe.lastIndexOf(FileUtils.DIRECTORY_SEPARATOR) + 1));
                    mfmSettings.MAMEexeDir(mameExePath.getText().substring(0,
                            mameExePath.getText().lastIndexOf(FileUtils.DIRECTORY_SEPARATOR)));

                    // NOTE added 9/20/2016 removing MFMSettings call from MAME class and class rename
                    // needed here for a first run
                    MAMEexe.setBaseArgs(mfmSettings.fullMAMEexePath());
                }

                mfmSettings.RomsFullSetDir(mameRomsPath.getText());
                mfmSettings.CHDsFullSetDir(mameCHDsPath.getText());
                mfmSettings.setExtrasFullSetDir(mameExtrasPath.getText());
                //    mfmSettings.RomsPlaySetDir(mamePlayPath.getText());
                mfmSettings.setPlaySetDir(mamePlayPath.getText());
                mfmSettings.VIDsFullSetDir(mameVidsPath.getText());
                mfmSettings.SoftwareListRomsFullSetDir(softwareListRomsPath.getText());
                mfmSettings.SoftwareListCHDsFullSetDir(softwareListCHDsPath.getText());
                mfmSettings.nonMerged(nonmergedCB.isSelected());

//*************************************************************************************************
                frame.setVisible(false);
                frame.dispose();
                if (mfmSettings.PlaySetDirectories() == null) {
                    MFM.getMFMSettings().updateDirectoriesResourceFiles();
                }

                mfmSettings.isLoaded(true);
                mfmSettings.updateDirectoriesResourceFiles();
                if (!MFM.isFirstRun()) {
                    MAMEInfo.loadINIs();
                }

                MFM.getMFMSettings().persistMySettings();

                // Linux debug
                MFM.getLogger().addToList("END Save Settings", true);
                MFM.getLogger().separateLine();
            } else if (((JButton) event.getSource()).getName().equals(CANCEL)) {
                frame.dispose();
                if (!mfmSettings.isLoaded()) {
                    MFM.getLogger().addToList("User canceled Settings", true);
                    System.exit(4);
                }
            }
        };

        saveB.addActionListener(settingsListener);
        cancelB.addActionListener(settingsListener);

        JLabel required = new JLabel("** REQUIRED **", SwingConstants.RIGHT);
        required.setBorder(new EmptyBorder(0, 20, 0, 0));
        this.add(required);
        this.add(new JLabel(""));

        this.add(mamePlayPathLabel);
        this.add(mamePlayPath);

        JLabel maybeRequired = new JLabel("========== ** Required if you run or parse MAME **", SwingConstants.TRAILING);
        maybeRequired.setToolTipText("Not required if you downloaded complete MFM Set " +
                "and only desire List Building capabilities.");
        maybeRequired.setVerticalAlignment(SwingConstants.BOTTOM);

        String longDivider = "===========================================";
        this.add(maybeRequired);
        JLabel divider3 = new JLabel(longDivider);
        divider3.setVerticalAlignment(SwingConstants.BOTTOM);
        this.add(divider3);

        this.add(mameExelabel);
        this.add(mameExePath);

        JLabel oneRequired = new JLabel("=============================== ** 1 required **");
        oneRequired.setVerticalAlignment(SwingConstants.BOTTOM);
        oneRequired.setHorizontalAlignment(SwingConstants.RIGHT);
        this.add(oneRequired);

        JLabel divider = new JLabel(longDivider);
        divider.setVerticalAlignment(SwingConstants.BOTTOM);
        this.add(divider);

        this.add(mameRomsLabel);
        this.add(mameRomsPath);

        JLabel split = new JLabel("Leave unchecked if you have a SPLIT or MERGED ROM set");
        split.setHorizontalAlignment(SwingConstants.RIGHT);
        this.add(split);
        this.add(nonmergedCB);

        this.add(mameCHDsLabel);
        this.add(mameCHDsPath);

        this.add(softwarelistRomsLabel);
        this.add(softwareListRomsPath);

        this.add(softwarelistCHDsLabel);
        this.add(softwareListCHDsPath);

        JLabel notRequired = new JLabel("============================ ** Not required **");
        notRequired.setVerticalAlignment(SwingConstants.BOTTOM);
        notRequired.setHorizontalAlignment(SwingConstants.RIGHT);
        this.add(notRequired);
        JLabel divider2 = new JLabel(longDivider);
        divider2.setVerticalAlignment(SwingConstants.BOTTOM);
        this.add(divider2);

        this.add(mameExtraslabel);
        this.add(mameExtrasPath);

        this.add(mameVideosLabel);
        this.add(mameVidsPath);

        // NOTE HAck to trick GridLayout - should really do another layout manager
        this.add(new JLabel());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveB);
        buttonPanel.add(new JLabel("           ")); // spacing hack
        buttonPanel.add(cancelB);
        buttonPanel.setBackground(MFMUI.getMFMSettingsBGcolor());
        this.add(buttonPanel);
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);

        if (mfmSettings.isLoaded()) {
            if (mfmSettings.MAMEexeDir() != null && !mfmSettings.MAMEexeDir().isEmpty()) {
                mameExePath.setText(
                        mfmSettings.MAMEexeDir() + FileUtils.DIRECTORY_SEPARATOR + mfmSettings.MAMEexeName());
            }
            mameRomsPath.setText(mfmSettings.RomsFullSetDir());
            mameCHDsPath.setText(mfmSettings.CHDsFullSetDir());
            mameExtrasPath.setText(mfmSettings.getExtrasFullSetDir());
            mamePlayPath.setText(mfmSettings.getPlaySetDir());
            mameVidsPath.setText(mfmSettings.VIDsFullSetDir());
            softwareListRomsPath.setText(mfmSettings.SoftwareListRomsFullSetDir());
            softwareListCHDsPath.setText(mfmSettings.SoftwareListCHDsFullSetDir());
            nonmergedCB.setSelected(mfmSettings.isnonMerged());
        }
    }

    private class SettingsController extends ClickListener {

        @Override
        public void singleClick(MouseEvent e) { // Ignore we just want double clicks
        }

        @Override
        public void doubleClick(MouseEvent e) {
            String fieldName = ((JTextField) e.getSource()).getName();

            if (fieldName.equalsIgnoreCase(mameExePath.getName()) ||
                    fieldName.equalsIgnoreCase(vDubPath.getName()) ||
                    fieldName.equalsIgnoreCase(ffmpegPath.getName())) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setDialogTitle("Select File");
            } else {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setDialogTitle("Select Folder");
            }

            int returnValue = fileChooser.showDialog(MFM_SettingsPanel.this, "OK");

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                switch (fieldName) {

                    case MFM_Constants.MAME_EXE_DIRECTORY:
                        mameExePath.setText(file.getAbsolutePath());
                        break;
                    case MFM_Constants.ROMS_FULL_SET_DIRECTORY:
                        String path = file.getAbsolutePath();
                        mameRomsPath.setText(path);
                        break;
                    case MFM_Constants.CHDS_FULL_SET_DIRECTORY:
                        mameCHDsPath.setText(file.getAbsolutePath());
                        break;
                    case MFM_Constants.EXTRAS_FULL_SET_DIRECTORY: //"mameExtrasPath":
                        mameExtrasPath.setText(file.getAbsolutePath());
                        break;
                    case MFM_Constants.MAME_VIDS_DIRECTORY:
                        mameVidsPath.setText(file.getAbsolutePath());
                        break;
                    case MFM_Constants.PLAYSET_ROOT_DIRECTORY:
                        mamePlayPath.setText(file.getAbsolutePath());
                        break;
                    case MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY:
                        softwareListRomsPath.setText(file.getAbsolutePath());
                        break;
                    case MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY:
                        softwareListCHDsPath.setText(file.getAbsolutePath());
                        break;

                    // These entries were removed from Settings UI in 0.7
                    case MFM_Constants.VIRTUALDUB_EXE:
                        vDubPath.setText(file.getAbsolutePath());
                        break;
                    case MFM_Constants.FFMPEG_EXE:
                        ffmpegPath.setText(file.getAbsolutePath());
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
