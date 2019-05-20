package com.github.phweda.mfm.ui.component.validation;

public class ValidationResult {

  UiMessageSeverity mesasgeSeverity = null;

  public UiMessageSeverity getMesasgeSeverity() {
    return mesasgeSeverity;
  }

  public void updateMessageSeverity(UiMessageSeverity nMesasgeSeverity) {
    mesasgeSeverity = UiMessageSeverity.maxSeverity(mesasgeSeverity, nMesasgeSeverity);
  }
}
