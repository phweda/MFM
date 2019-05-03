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



}
