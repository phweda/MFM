package com.github.phweda.mfm.ui.component.validation;

import javax.swing.*;
import java.awt.*;

public class ValidationBase {
  private JPanel pnlValidationMsgHolder;
  private JLabel lblValidationMsg;
  private UiMessageSeverity severity = null;
  private JPanel panelComponentHolder;

  public void setPanelComponentHolder(JPanel panelComponentHolder) {
    this.panelComponentHolder = panelComponentHolder;
  }

  public void clearMessage() {
    severity = null;
    lblValidationMsg.setText("");
    panelComponentHolder.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
  }

  public boolean setMessage(String text, UiMessageSeverity messageSeverity) {
    if (!UiMessageSeverity.higherSeverity(this.severity, messageSeverity)) {
      return false;
    }
    lblValidationMsg.setText(text);
    this.severity = messageSeverity;
    switch (messageSeverity) {
      case ERROR:
        lblValidationMsg.setForeground(Color.RED);
        panelComponentHolder.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        break;
      case WARNING:
        lblValidationMsg.setForeground(Color.ORANGE);
        panelComponentHolder.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        break;
      default:
        throw new RuntimeException("Unknown severity: " + messageSeverity);
    }
    return true;
  }

}
