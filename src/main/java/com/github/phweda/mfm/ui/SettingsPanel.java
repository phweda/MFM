package com.github.phweda.mfm.ui;

import com.github.phweda.mfm.*;
import com.github.phweda.mfm.ui.component.ValidatedComboBox;
import com.github.phweda.mfm.ui.component.ValidatedFSPathInputBox;
import com.github.phweda.mfm.ui.component.validation.UiMessageSeverity;
import com.github.phweda.mfm.ui.component.validation.ValidationResult;
import com.github.phweda.utils.FileUtils;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class SettingsPanel {
  private JButton saveButton;

  private static MFMSettings mfmSettings = MFMSettings.getInstance();
  private ValidatedFSPathInputBox fsMameRom;
  private ValidatedFSPathInputBox fsMameChd;
  private ValidatedFSPathInputBox fsMameSRoms;
  private ValidatedFSPathInputBox fsMameSChd;
  private ValidatedFSPathInputBox fsMameExtra;
  private ValidatedFSPathInputBox fsMameMultimedia;
  private ValidatedFSPathInputBox fsMameExec;
  private ValidatedFSPathInputBox fsOutput;
  private JCheckBox chkCheckUpdates;
  private JButton btnSave;
  private JButton btnCancel;
  private JPanel pnlSettings;
  private ValidatedComboBox cbMameRomType;

  /**
   * Settings holder panel
   */
  private static JFrame frame;

  public SettingsPanel() {
    $$$setupUI$$$();
    btnSave.addActionListener(e -> {
      ValidationResult validationResult = new ValidationResult();
      boolean mameRomPathOk = fsMameRom.validate(validationResult);
      fsMameChd.validate(validationResult);
      fsMameSRoms.validate(validationResult);
      fsMameSChd.validate(validationResult);
      fsMameExtra.validate(validationResult);
      fsMameMultimedia.validate(validationResult);

      boolean mameRomPathDefined = !fsMameRom.getPath().isEmpty();
      if (!mameRomPathDefined && fsMameChd.getPath().isEmpty() &&
          fsMameSRoms.getPath().isEmpty() && fsMameSRoms.getPath().isEmpty()) {
        fsMameRom.setMessage("One mame rom or chd path definition is mandatory", UiMessageSeverity.ERROR);
        validationResult.updateMessageSeverity(UiMessageSeverity.ERROR);
      } else if (mameRomPathOk && mameRomPathDefined) {
        String mameRomDir = fsMameRom.getPath();
        String romPath = FileUtils.constructFilePath(mameRomDir, "1941.zip");
        //check main rom exists
        if (!(new File(romPath).isFile())) {
          fsMameRom.setMessage("1941.zip not found. It's not a full romset?", UiMessageSeverity.WARNING);
          validationResult.updateMessageSeverity(UiMessageSeverity.WARNING);
        } else {
          //try to determine romset type
          romPath = FileUtils.constructFilePath(mameRomDir, "1941r1.zip");
          File extRom = new File(romPath);
          if (!extRom.isFile()) {
            //not exists -> MERGED case
            if (cbMameRomType.getSelectedIndex() != 0) {
              cbMameRomType.setMessage("1941r1.zip not found. Romset type is MERGED?", UiMessageSeverity.WARNING);
              validationResult.updateMessageSeverity(UiMessageSeverity.WARNING);
            }
          } else {
            //check file size. If < 1 megabyte its SPLITTED, otherwise NON-MERGED case
            if (extRom.length() < 1024 * 1024) {
              //SPLITTED
              if (cbMameRomType.getSelectedIndex() != 0) {
                cbMameRomType.setMessage("1941r1.zip size is too small. Romset type is SPLITTED?", UiMessageSeverity.WARNING);
                validationResult.updateMessageSeverity(UiMessageSeverity.WARNING);
              }
            } else {
              //NON-MERGED
              if (cbMameRomType.getSelectedIndex() != 1) {
                cbMameRomType.setMessage("1941r1.zip size is too big. Romset type is NON-MERGED?", UiMessageSeverity.WARNING);
                validationResult.updateMessageSeverity(UiMessageSeverity.WARNING);
              }
            }

          }
        }
      }


      String mameexe = fsMameExec.getPath();
      if (fsMameExec.validate(validationResult)) {
        if (MFM_Data.getInstance().notLoaded() && (mameexe.isEmpty())) {
          fsMameExec.setMessage("MFM Data not detected MAME.exe must be entered.", UiMessageSeverity.ERROR);
          validationResult.updateMessageSeverity(UiMessageSeverity.ERROR);
        }
      }

      fsOutput.validate(validationResult);
      frame.pack();

      UiMessageSeverity mesasgeSeverity = validationResult.getMesasgeSeverity();
      if (mesasgeSeverity == UiMessageSeverity.ERROR) {
        return;
      } else if (mesasgeSeverity == UiMessageSeverity.WARNING) {
        int dialogResult = JOptionPane.showConfirmDialog(SettingsPanel.frame, "Warnings detected. Do you want to continue?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.NO_OPTION) {
          return;
        }
      }

      if (!mameexe.isEmpty() && mameexe.length() > 5 && mameexe.contains(FileUtils.DIRECTORY_SEPARATOR)) {
        mfmSettings.MAMEexeName(mameexe.substring(
            mameexe.lastIndexOf(FileUtils.DIRECTORY_SEPARATOR) + 1));
        mfmSettings.MAMEexeDir(mameexe.substring(0,
            mameexe.lastIndexOf(FileUtils.DIRECTORY_SEPARATOR)));

        // NOTE added 9/20/2016 removing MFMSettings call from MAME class and class rename
        // needed here for a first run
        MAMEexe.setBaseArgs(mfmSettings.fullMAMEexePath());
      }
      mfmSettings.initSettings();
      mfmSettings.RomsFullSetDir(fsMameRom.getPath());
      mfmSettings.CHDsFullSetDir(fsMameChd.getPath());
      mfmSettings.setExtrasFullSetDir(fsMameExtra.getPath());
      mfmSettings.setPlaySetDir(fsOutput.getPath());
      mfmSettings.VIDsFullSetDir(fsMameMultimedia.getPath());
      mfmSettings.SoftwareListRomsFullSetDir(fsMameSRoms.getPath());
      mfmSettings.SoftwareListCHDsFullSetDir(fsMameSChd.getPath());
      mfmSettings.nonMerged(cbMameRomType.getSelectedIndex() == 1);

      //maybe this is redundant at this point
      String settingsValid = mfmSettings.settingsValid();
      if (settingsValid != null) {
        JOptionPane.showMessageDialog(frame,
            settingsValid);
        return;
      }

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
    });

    if (mfmSettings.isLoaded()) {
      if (mfmSettings.MAMEexeDir() != null && !mfmSettings.MAMEexeDir().isEmpty()) {
        fsMameExec.setPath(
            mfmSettings.MAMEexeDir() + FileUtils.DIRECTORY_SEPARATOR + mfmSettings.MAMEexeName());
      }
      fsMameRom.setPath(mfmSettings.RomsFullSetDir());
      fsMameChd.setPath(mfmSettings.CHDsFullSetDir());
      fsMameExtra.setPath(mfmSettings.getExtrasFullSetDir());
      fsMameMultimedia.setPath(mfmSettings.VIDsFullSetDir());
      fsMameSRoms.setPath(mfmSettings.SoftwareListRomsFullSetDir());
      fsMameSChd.setPath(mfmSettings.SoftwareListCHDsFullSetDir());
      if (mfmSettings.isnonMerged()) {
        cbMameRomType.setSelectedIndex(1);
      } else {
        cbMameRomType.setSelectedIndex(0);
      }
    }

    btnCancel.addActionListener(e -> cancelSettingsDialog());
  }

  private void cancelSettingsDialog() {
    boolean noSettingsLoaded = !mfmSettings.isLoaded();
    if (noSettingsLoaded) {
      int dialogResult = JOptionPane.showConfirmDialog(SettingsPanel.frame, "No valid settings found. If you continue, program terminated. Continue?", "Warning", JOptionPane.YES_NO_OPTION);
      if (dialogResult == JOptionPane.NO_OPTION) {
        return;
      }
    }
    frame.dispose();
    if (noSettingsLoaded) {
      MFM.getLogger().addToList("User canceled initial Setup", true);
      System.exit(4);
    }
  }

  private void createUIComponents() {
    fsMameRom = new ValidatedFSPathInputBox("Mame Full Rom set", "Directory, containing full mame arcade emulation romset", true);
    cbMameRomType = new ValidatedComboBox("Mame romset type", "", "SPLIT or MERGED romset", "NON-MERGED");
    fsMameChd = new ValidatedFSPathInputBox("Mame CHD set", "Mame CHD files required for arcade emulation (newer games)", true);
    fsMameSRoms = new ValidatedFSPathInputBox("Software list rom set", "Path to directory containing romset for Mame software emulation", true);
    fsMameSChd = new ValidatedFSPathInputBox("Software list CHD set", "CHD files for mame software emulator functionality", true);
    fsMameExtra = new ValidatedFSPathInputBox("Extras set", "Mame extras, containing snapshots, images, etc., that can be used in MAME GUIs.", true);
    fsMameMultimedia = new ValidatedFSPathInputBox("Multimedia set", "MAME Multimedia folder, contains VideoSnaps and Soundtracks ", true);
    fsMameExec = new ValidatedFSPathInputBox("Mame executable", "MAME executable file. Optional, required if you want to start roms with mame, or want to generate new rom database", ValidatedFSPathInputBox.SelectionMode.FILES_ONLY, true);
    fsOutput = new ValidatedFSPathInputBox("Output directory", "Directory where the list export function copy the available Mame resources (roms, chds, extras, multimedia, etc.)", false);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        cancelSettingsDialog();
      }
    });
  }

  public JPanel getSettingsPanel() {
    return pnlSettings;
  }

  public static void showSettingsPanel(JFrame frameIn) {
    frame = frameIn;
    JPanel settingsPanel = new SettingsPanel().getSettingsPanel();
    frame.setContentPane(settingsPanel);
    Dimension preferredSize = settingsPanel.getPreferredSize();
    frame.setLocation(MFMUI.screenCenterPoint.x - preferredSize.width / 2, MFMUI.screenCenterPoint.y - preferredSize.height / 2);
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    pnlSettings = new JPanel();
    pnlSettings.setLayout(new GridLayoutManager(14, 1, new Insets(0, 0, 0, 0), -1, -1));
    pnlSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null));
    final JLabel label1 = new JLabel();
    Font label1Font = this.$$$getFont$$$(null, Font.BOLD, -1, label1.getFont());
    if (label1Font != null) label1.setFont(label1Font);
    label1.setText("Mame resources");
    pnlSettings.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    pnlSettings.add(fsMameRom.$$$getRootComponent$$$(), new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    pnlSettings.add(fsMameChd.$$$getRootComponent$$$(), new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    pnlSettings.add(fsMameSRoms.$$$getRootComponent$$$(), new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    pnlSettings.add(fsMameSChd.$$$getRootComponent$$$(), new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    pnlSettings.add(fsMameExtra.$$$getRootComponent$$$(), new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    pnlSettings.add(fsMameMultimedia.$$$getRootComponent$$$(), new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    pnlSettings.add(fsMameExec.$$$getRootComponent$$$(), new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JLabel label2 = new JLabel();
    Font label2Font = this.$$$getFont$$$(null, Font.BOLD, -1, label2.getFont());
    if (label2Font != null) label2.setFont(label2Font);
    label2.setText("MFM settings");
    pnlSettings.add(label2, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    pnlSettings.add(fsOutput.$$$getRootComponent$$$(), new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    chkCheckUpdates = new JCheckBox();
    chkCheckUpdates.setEnabled(false);
    chkCheckUpdates.setText("Check updates on startup");
    pnlSettings.add(chkCheckUpdates, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer1 = new Spacer();
    pnlSettings.add(spacer1, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), null, null, 0, false));
    pnlSettings.add(cbMameRomType.$$$getRootComponent$$$(), new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
    pnlSettings.add(panel1, new GridConstraints(13, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    btnSave = new JButton();
    btnSave.setText("Save");
    panel1.add(btnSave, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    btnCancel = new JButton();
    btnCancel.setText("Cancel");
    panel1.add(btnCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
  }

  /**
   * @noinspection ALL
   */
  private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
    if (currentFont == null) return null;
    String resultName;
    if (fontName == null) {
      resultName = currentFont.getName();
    } else {
      Font testFont = new Font(fontName, Font.PLAIN, 10);
      if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
        resultName = fontName;
      } else {
        resultName = currentFont.getName();
      }
    }
    return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return pnlSettings;
  }

}
