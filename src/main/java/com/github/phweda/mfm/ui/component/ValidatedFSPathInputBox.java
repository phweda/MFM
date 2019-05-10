package com.github.phweda.mfm.ui.component;

import com.github.phweda.mfm.ui.component.validation.UiMessageSeverity;
import com.github.phweda.mfm.ui.component.validation.ValidationBase;
import com.github.phweda.mfm.ui.component.validation.ValidationResult;
import com.github.phweda.utils.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Validated File System Path selector input box
 */
public class ValidatedFSPathInputBox {
  private JTextField txtPath;
  private JButton btnBrowse;
  private JLabel lblInfo;
  private JLabel lblFieldName;
  private JPanel panelComponentHolder;
  private JPanel panelSelectorControls;
  private ValidationBase validationBase;
  private SelectionMode selectionMode = SelectionMode.DIRECTORIES_ONLY;
  private JFileChooser fileChooser = null;
  private boolean optional = true;


  public enum SelectionMode {
    FILES_ONLY,
    DIRECTORIES_ONLY
  }

  public ValidatedFSPathInputBox(String name, String info, boolean optional) {
    initControl(name, info, SelectionMode.DIRECTORIES_ONLY, optional);

  }

  public ValidatedFSPathInputBox(String name, String info, SelectionMode selectionMode, boolean optional) {
    initControl(name, info, selectionMode, optional);
  }

  private void initControl(String name, String info, SelectionMode selectionMode, boolean optional) {
    lblFieldName.setText(name);
    lblInfo.setToolTipText(info);
    this.selectionMode = selectionMode;
    this.optional = optional;
    this.validationBase.setPanelComponentHolder(panelComponentHolder);

    Font f = lblFieldName.getFont();
    if (!optional) {
      lblFieldName.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
    } else {
      lblFieldName.setFont(f.deriveFont(f.getStyle() &~ Font.BOLD));
    }

    fileChooser = new JFileChooser();
    fileChooser.setPreferredSize(new Dimension(640, 480));
    btnBrowse.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        switch (selectionMode) {
          case FILES_ONLY:
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Select File");
            break;
          case DIRECTORIES_ONLY:
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle("Select Folder");
            break;
          default:
            throw new RuntimeException("Unknown mode: " + selectionMode);
        }
        int returnValue = fileChooser.showDialog(ValidatedFSPathInputBox.this.panelComponentHolder, "Select");

        if (returnValue == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();
          txtPath.setText(file.getAbsolutePath());
        }
      }
    });
  }


  /**
   * validate control
   *
   * @return true if control is valid (no error messages)
   */
  public boolean validate(ValidationResult validationResult) {
    this.clearMessage();
    String textFieldValue = getPath();
    //empty field
    if (StringUtils.safeIsEmpty(textFieldValue)) {
      if (!optional) {
        this.setMessage("This field is required", UiMessageSeverity.ERROR);
        return false;
      }
      return true;
    }

    File destFile = new File(textFieldValue);
    switch (selectionMode) {
      case FILES_ONLY:
        if (!destFile.isFile()) {
          this.setMessage("File isn't exists", UiMessageSeverity.ERROR);
          return false;
        }
        break;
      case DIRECTORIES_ONLY:
        if (!destFile.isDirectory()){
          this.setMessage("Directory is not exists", UiMessageSeverity.ERROR);
          return false;
        }
        break;
        default:
          throw new RuntimeException("Unknown mode: " + selectionMode);
    }
    return true;
  }

  public boolean setMessage(String text, UiMessageSeverity messageSeverity) {
    return validationBase.setMessage(text, messageSeverity);
  }

  public void clearMessage() {
    validationBase.clearMessage();
  }

  public String getPath() {
    return txtPath.getText().trim();
  }

  public void setPath(String path) {
    txtPath.setText(path);
  }


}
