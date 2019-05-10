package com.github.phweda.utils;

public class StringUtils {

  /**
   * remove given char consecutive repetitions on given string
   * @param source
   * @return
   */
  public static String removeDuplicates(String source, char dupcar) {
    StringBuilder sb = new StringBuilder(source.length());
    // init last char
    boolean lastCharIsDupChar = false;
    for (int i = 0; i < source.length(); i++) {
      char ch = source.charAt(i);
      if (ch != dupcar) {
        lastCharIsDupChar = false;
        sb.append(ch);
      } else {
        // append only if last char is not dup char
        if (!lastCharIsDupChar) {
          sb.append(ch);
        }
        lastCharIsDupChar = true;
      }
    }

    return sb.toString();
  }

  /**
   * check given string is empty (null string tolerant, and ignore whitespaces)
   *
   * @param param
   *        input string to check (null or str)
   *
   * @return
   *         when string is null return false, else return string isEmpty
   *         function result
   */
  public static boolean safeIsEmpty(String param) {
    if (param == null) {
      return true;
    } else {
      return param.trim().isEmpty();
    }
  }

  /**
   * fill text from start to target length with given fillChar
   *
   * @param text
   *        original text (example: apple)
   *
   * @param fillChar
   *        fill character (example: z)
   *
   * @param targetLength
   *        result string target length (example: 10)
   *
   * @return
   *         filled string (example: zzzzzapple)
   */
  public static String fillLeft(String text, char fillChar, int targetLength) {
    String worktext = text;
    if (worktext == null) {
      worktext = "";
    }
    while (worktext.length() < targetLength) {
      worktext = fillChar + worktext;
    }
    return worktext;
  }

  /**
   * fill text to given length with given fillChar
   *
   * @param text
   *        original text (example: apple)
   *
   * @param fillChar
   *        fill character (example: z)
   *
   * @param targetLength
   *        result string target length (example: 10)
   *
   * @return
   *         filled string (example: applezzzzz)
   */
  public static String fillRight(String text, char fillChar, int targetLength) {
    String worktext = text;
    if (worktext == null) {
      worktext = "";
    }
    while (worktext.length() < targetLength) {
      worktext += fillChar;
    }
    return worktext;
  }



  /**
   * compare version number to another version number
   *
   * @param version1
   *        version number 1
   *
   * @param version2
   *        version number 2
   *
   * @return
   *        0 if the two given version is equal
   *        <0 if version is less than the second argument;
   *        >0 if version is greater than the second argument.
   */
  public static int compareVersions(String version1, String version2) {
    String[] split1 = version1.toLowerCase().split("\\.");
    String[] split2 = version2.toLowerCase().split("\\.");

    StringBuilder sb1 = new StringBuilder();
    StringBuilder sb2 = new StringBuilder();
    int partnum = Math.max(split1.length, split2.length);
    for (int i = 0; i < partnum; i++) {
      // determinate max length
      String version1Part = LangUtils.<String>safeGetArray(split1, i, "0");
      String version2Part = LangUtils.<String>safeGetArray(split2, i, "0");
      int partlen = Math.max(version1Part.length(), version2Part.length());
      if (i == 0) {
        version1Part = StringUtils.fillLeft(version1Part, '0', partlen);
        version2Part = StringUtils.fillLeft(version2Part, '0', partlen);
      } else {
        version1Part = StringUtils.fillRight(version1Part, '0', partlen);
        version2Part = StringUtils.fillRight(version2Part, '0', partlen);
      }
      sb1.append(version1Part);
      sb2.append(version2Part);
    }

    String fVstr1 = sb1.toString();
    String fVstr2 = sb2.toString();
    int result = fVstr1.compareTo(fVstr2);
    return result;
  }


}
