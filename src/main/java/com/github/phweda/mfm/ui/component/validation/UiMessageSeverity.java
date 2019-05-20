package com.github.phweda.mfm.ui.component.validation;

public enum UiMessageSeverity {
  ERROR,
  WARNING;

  public static UiMessageSeverity maxSeverity(UiMessageSeverity ums1, UiMessageSeverity ums2) {
    if (ERROR == ums1 || ERROR == ums2) {
      return ERROR;
    } else if (WARNING == ums1 || WARNING == ums2){
      return WARNING;
    }
    return null;
  }

  public static boolean higherSeverity(UiMessageSeverity origS, UiMessageSeverity newS) {
    return origS==null || (WARNING == origS && newS ==ERROR);
  }
}
