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

package Phweda.MFM.UI;

import Phweda.MFM.*;
import Phweda.utils.ClickListener;
import Phweda.utils.FileUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
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

    private static final Font font = new Font("Arial", Font.BOLD, 16);
    private static final Dimension size = new Dimension(1024, 768);
    private static JFrame frame;
    private JFileChooser fileChooser = new JFileChooser();
    private SettingsController controller = new SettingsController();
    private JTextField MAMEExePath = new JTextField();
    private JTextField MAMERomsPath = new JTextField();
    private JTextField SoftwareListRomsPath = new JTextField();
    private JTextField MAMECHDsPath = new JTextField();
    private JTextField SoftwareListCHDsPath = new JTextField();
    private JTextField MAMEExtrasPath = new JTextField();
    private JTextField MAMEVidsPath = new JTextField();
    private JTextField MAMEPlayPath = new JTextField();
    private JTextField VDubPath = new JTextField();
    private JTextField FFmpegPath = new JTextField();
    private JCheckBox nonmergedCB = new JCheckBox("MAME ROMs NON-MERGED?");

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
                aComp.setFont(font);
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
        JLabel MAMEXElabel = new JLabel("<HTML>Select MAME Executable</HTML>");
        MAMEExePath.setName(MFM_Constants.MAME_EXE_DIRECTORY);
        MAMEExePath.addMouseListener(controller);
        MAMEExePath.setToolTipText("Click here and select MAME executable file");

        JLabel MAMEROMSlabel = new JLabel(
                "<HTML>Select root Directory full MAME Rom set</HTML>");
        MAMERomsPath.setName(MFM_Constants.ROMS_FULL_SET_DIRECTORY);
        MAMERomsPath.addMouseListener(controller);
        MAMERomsPath.setToolTipText("Click here and select your MAME ROMs full set root directory");

        JLabel MAMECHDSlabel = new JLabel(
                "<HTML>Select root Directory full MAME CHD set</HTML>");
        MAMECHDsPath.setName(MFM_Constants.CHDS_FULL_SET_DIRECTORY);
        MAMECHDsPath.addMouseListener(controller);
        MAMECHDsPath.setToolTipText("Click here to select MAME CHD full set root directory");

        JLabel MAMEEXTRASlabel = new JLabel(
                "<HTML>Select root Directory full MAME Extras set</HTML>");
        MAMEExtrasPath.setName(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY);
        MAMEExtrasPath.addMouseListener(controller);
        MAMEExtrasPath.setToolTipText("Click here and select your MAME Extras full set root directory");

        JLabel MAMEVIDSlabel = new JLabel(
                "<HTML>Select Directory of your MAME videos</HTML>");
        MAMEVidsPath.setName(MFM_Constants.MAME_VIDS_DIRECTORY);
        MAMEVidsPath.addMouseListener(controller);
        MAMEVidsPath.setToolTipText("Click here and select the Directory of your MAME videos");

        JLabel MAMEPLAYPATHlabel = new JLabel("<HTML>Select Playset Directory to place MAME resources</HTML>");
        MAMEPlayPath.setName(MFM_Constants.PLAYSET_ROOT_DIRECTORY);
        MAMEPlayPath.addMouseListener(controller);
        MAMEPlayPath.setToolTipText("Click here to set the Directory where you want your Playset");

        JLabel SLROMSlabel = new JLabel("<HTML>Select root Directory full Software List ROMs</HTML>");
        SoftwareListRomsPath.setName(MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY);
        SoftwareListRomsPath.addMouseListener(controller);
        SoftwareListRomsPath.setToolTipText("Click here and select your Software List ROMs directory");

        JLabel SLCHDSlabel = new JLabel("<HTML>Select root Directory full Software List CHDs</HTML>");
        SoftwareListCHDsPath.setName(MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY);
        SoftwareListCHDsPath.addMouseListener(controller);
        SoftwareListCHDsPath.setToolTipText("Click here and select your Software List CHDs directory");

/*
        JLabel label9 = new JLabel("<HTML>Select VirtualDub Executable</HTML>");
        VDubPath.setName(MFM_Constants.VIRTUALDUB_EXE);
        VDubPath.addMouseListener(controller);
        VDubPath.setToolTipText("Click here and select your VirtualDub executable file");

        JLabel label10 = new JLabel("<HTML>Select FFmpeg Executable</HTML>");
        FFmpegPath.setName(MFM_Constants.FFMPEG_EXE);
        FFmpegPath.addMouseListener(controller);
        FFmpegPath.setToolTipText("Click here and select your VirtualDub executable file");
*/
        JButton saveB = new JButton("<HTML>Save MFM Settings</HTML>");
        saveB.setName("Save");
        JButton cancelB = new JButton("Cancel");
        cancelB.setName("Cancel");

//*************************** SAVE SETTINGS**********************************************************

        ActionListener settingsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (((JButton) e.getSource()).getName().equals("Save")) {

                    MFM.logger.separateLine();
                    MFM.logger.addToList("Save Settings command received", true);
                    MFM.logger.addToList("ROMs path: " + MAMERomsPath.getText(), true);
                    MFM.logger.addToList("Software List ROMs path: " + SoftwareListRomsPath.getText(), true);
                    MFM.logger.addToList("Play path: " + MAMEPlayPath.getText(), true);
                    MFM.logger.addToList("MAME EXE path: " + MAMEExePath.getText(), true);
                    MFM.logger.addToList("CHD path: " + MAMECHDsPath.getText(), true);
                    MFM.logger.addToList("Software List CHDs path: " + SoftwareListCHDsPath.getText(), true);
                    MFM.logger.addToList("Extras path: " + MAMEExtrasPath.getText(), true);
                    MFM.logger.addToList("VIDs path: " + MAMEVidsPath.getText(), true);

                    // NOTE maintain order!!! See MFMSettings.MAMEexeDir() first run for create config
                    String mameexe = MAMEExePath.getText();
                    if ((MAMERomsPath.getText().isEmpty() && MAMECHDsPath.getText().isEmpty() &&
                            SoftwareListRomsPath.getText().isEmpty() && SoftwareListCHDsPath.getText().isEmpty())
                            || MAMEPlayPath.getText().isEmpty()) {
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
                        mfmSettings.MAMEexeDir(MAMEExePath.getText().substring(0,
                                MAMEExePath.getText().lastIndexOf(FileUtils.DIRECTORY_SEPARATOR)));

                        // NOTE added 9/20/2016 removing MFMSettings call from MAME class and class rename
                        // needed here for a first run
                        MAMEexe.setBaseArgs(mfmSettings.fullMAMEexePath());
                    }

                    mfmSettings.RomsFullSetDir(MAMERomsPath.getText());
                    mfmSettings.CHDsFullSetDir(MAMECHDsPath.getText());
                    mfmSettings.setExtrasFullSetDir(MAMEExtrasPath.getText());
                    //    mfmSettings.RomsPlaySetDir(MAMEPlayPath.getText());
                    mfmSettings.setPlaySetDir(MAMEPlayPath.getText());
                    mfmSettings.VIDsFullSetDir(MAMEVidsPath.getText());
                    mfmSettings.SoftwareListRomsFullSetDir(SoftwareListRomsPath.getText());
                    mfmSettings.SoftwareListCHDsFullSetDir(SoftwareListCHDsPath.getText());
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
                    MFM.logger.addToList("END Save Settings", true);
                    MFM.logger.separateLine();
                } else if (((JButton) e.getSource()).getName().equals("Cancel")) {
                    frame.dispose();
                    if (!mfmSettings.isLoaded()) {
                        MFM.logger.addToList("User canceled Settings", true);
                        System.exit(4);
                    }
                }
            }
        };

        saveB.addActionListener(settingsListener);
        cancelB.addActionListener(settingsListener);

        JLabel required = new JLabel("** REQUIRED **", SwingConstants.RIGHT);
        required.setBorder(new EmptyBorder(0, 20, 0, 0));
        this.add(required);
        this.add(new JLabel(""));

        this.add(MAMEPLAYPATHlabel);
        this.add(MAMEPlayPath);

        JLabel maybeRequired = new JLabel("========== ** Required if you run or parse MAME **", SwingConstants.TRAILING);
        maybeRequired.setToolTipText("Not required if you downloaded complete MFM Set " +
                "and only desire List Building capabilities.");
        maybeRequired.setVerticalAlignment(SwingConstants.BOTTOM);

        this.add(maybeRequired);
        JLabel divider3 = new JLabel("===========================================");
        divider3.setVerticalAlignment(SwingConstants.BOTTOM);
        this.add(divider3);

        this.add(MAMEXElabel);
        this.add(MAMEExePath);

        JLabel oneRequired = new JLabel("=============================== ** 1 required **");
        oneRequired.setVerticalAlignment(SwingConstants.BOTTOM);
        oneRequired.setHorizontalAlignment(SwingConstants.RIGHT);
        this.add(oneRequired);

        JLabel divider = new JLabel("===========================================");
        divider.setVerticalAlignment(SwingConstants.BOTTOM);
        this.add(divider);

        this.add(MAMEROMSlabel);
        this.add(MAMERomsPath);

        JLabel split = new JLabel("Leave unchecked if you have a SPLIT or MERGED ROM set");
        split.setHorizontalAlignment(SwingConstants.RIGHT);
        this.add(split);
        this.add(nonmergedCB);

        this.add(MAMECHDSlabel);
        this.add(MAMECHDsPath);

        this.add(SLROMSlabel);
        this.add(SoftwareListRomsPath);

        this.add(SLCHDSlabel);
        this.add(SoftwareListCHDsPath);

        JLabel notRequired = new JLabel("============================ ** Not required **");
        notRequired.setVerticalAlignment(SwingConstants.BOTTOM);
        notRequired.setHorizontalAlignment(SwingConstants.RIGHT);
        this.add(notRequired);
        JLabel divider2 = new JLabel("===========================================");
        divider2.setVerticalAlignment(SwingConstants.BOTTOM);
        this.add(divider2);

        this.add(MAMEEXTRASlabel);
        this.add(MAMEExtrasPath);

        this.add(MAMEVIDSlabel);
        this.add(MAMEVidsPath);

        // NOTE HAck to trick GridLayout - should really do another layout manager
        this.add(new JLabel());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveB);
        //fixme get the spacing in an appropriate way NOTE believe would need to switch to GridBagLayout
        buttonPanel.add(new JLabel("           "));
        buttonPanel.add(cancelB);
        buttonPanel.setBackground(MFMUI.getMFMSettingsBGcolor());
        this.add(buttonPanel);
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);

        if (mfmSettings.isLoaded()) {
            if (mfmSettings.MAMEexeDir() != null && !mfmSettings.MAMEexeDir().isEmpty()) {
                MAMEExePath.setText(
                        mfmSettings.MAMEexeDir() + FileUtils.DIRECTORY_SEPARATOR + mfmSettings.MAMEexeName());
            }
            MAMERomsPath.setText(mfmSettings.RomsFullSetDir());
            MAMECHDsPath.setText(mfmSettings.CHDsFullSetDir());
            MAMEExtrasPath.setText(mfmSettings.getExtrasFullSetDir());
            MAMEPlayPath.setText(mfmSettings.getPlaySetDir());
            MAMEVidsPath.setText(mfmSettings.VIDsFullSetDir());
            SoftwareListRomsPath.setText(mfmSettings.SoftwareListRomsFullSetDir());
            SoftwareListCHDsPath.setText(mfmSettings.SoftwareListCHDsFullSetDir());
            nonmergedCB.setSelected(mfmSettings.isnonMerged());
        }
    }

    private class SettingsController extends ClickListener {

        @Override
        public void singleClick(MouseEvent e) {
        }

        @Override
        public void doubleClick(MouseEvent e) {
            String fieldName = ((JTextField) e.getSource()).getName();

            if (fieldName.equalsIgnoreCase(MAMEExePath.getName()) ||
                    fieldName.equalsIgnoreCase(VDubPath.getName()) ||
                    fieldName.equalsIgnoreCase(FFmpegPath.getName())) {
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
                        MAMEExePath.setText(file.getAbsolutePath());
                        break;
                    case MFM_Constants.ROMS_FULL_SET_DIRECTORY:
                        String path = file.getAbsolutePath();
                        MAMERomsPath.setText(path);
                        break;
                    case MFM_Constants.CHDS_FULL_SET_DIRECTORY:
                        MAMECHDsPath.setText(file.getAbsolutePath());
                        break;
                    case MFM_Constants.EXTRAS_FULL_SET_DIRECTORY: //"MAMEExtrasPath":
                        MAMEExtrasPath.setText(file.getAbsolutePath());
                        break;
                    case MFM_Constants.MAME_VIDS_DIRECTORY:
                        MAMEVidsPath.setText(file.getAbsolutePath());
                        break;
                    case MFM_Constants.PLAYSET_ROOT_DIRECTORY:
                        MAMEPlayPath.setText(file.getAbsolutePath());
                        break;
                    case MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY:
                        SoftwareListRomsPath.setText(file.getAbsolutePath());
                        break;
                    case MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY:
                        SoftwareListCHDsPath.setText(file.getAbsolutePath());
                        break;

                    // These entries were removed from Settings UI in 0.7
                    case MFM_Constants.VIRTUALDUB_EXE:
                        VDubPath.setText(file.getAbsolutePath());
                        break;
                    case MFM_Constants.FFMPEG_EXE:
                        FFmpegPath.setText(file.getAbsolutePath());
                        break;
                }
            }
        }
    }
}
