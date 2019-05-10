package com.github.phweda.mfm.ui.component;

import com.github.phweda.mfm.ui.component.validation.UiMessageSeverity;
import com.github.phweda.mfm.ui.component.validation.ValidationBase;

import javax.swing.*;

public class ValidatedComboBox {
  private JPanel panelComponentHolder;
  private ValidationBase validationBase;
  private JPanel panelSelectorControls;
  private JLabel lblFieldName;
  private JComboBox cmbSelection;
  private JLabel lblInfo;



  public ValidatedComboBox(String name, String info, String...values) {
    lblFieldName.setText(name);
    lblInfo.setToolTipText(info);
    this.validationBase.setPanelComponentHolder(panelComponentHolder);
    for (String value : values) {
      cmbSelection.addItem(value);
    }
  }


  public String getSelection() {
    return (String)cmbSelection.getSelectedItem();
  }

  public void setSelectedIndex(int index) {
    cmbSelection.setSelectedIndex(index);
  }

  public int getSelectedIndex() {
    return cmbSelection.getSelectedIndex();
  }

  public boolean setMessage(String text, UiMessageSeverity messageSeverity) {
    return validationBase.setMessage(text, messageSeverity);
  }

  public void clearMessage() {
    validationBase.clearMessage();
  }


}
