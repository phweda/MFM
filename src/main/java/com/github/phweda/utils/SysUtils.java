package com.github.phweda.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SysUtils {
  private static String computerName = null;

  /**
   * return computer name (calculated or forced)
   *
   * @return computer name
   */
  public static String getComputerName() {
    if (computerName == null) {
      try {
        String origComputerName = InetAddress.getLocalHost().getHostName();
        computerName = FileUtils.sanitizeFileName(origComputerName).toLowerCase();
      } catch (UnknownHostException e1) {
      }
      if (computerName==null) {
        computerName = "ufo";
      }
    }
    return computerName;
  }


}
