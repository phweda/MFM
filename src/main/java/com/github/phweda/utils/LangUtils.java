package com.github.phweda.utils;

/**
 * java language utils
 *
 * @author voji
 *
 */
public class LangUtils {

  /**
   * determinate given number is in range
   * @param rangeStart
   *        range start
   *
   * @param rangeEnd
   *        range end
   *
   * @param value
   *        given value to check
   *
   * @return
   *         true - given value is between given range
   *         false - given vaule isnt in given range
   */
  public static boolean isBetween(int rangeStart, int rangeEnd, int value) {
    return rangeEnd > rangeStart ? value > rangeStart && value < rangeEnd : value > rangeEnd
        && value < rangeStart;
  }

  /**
   * return given array element at given index. if array is null you outindex the array
   * return given default value instead of excepion
   *
   * @param array
   *        given array
   *
   * @param index
   *        given index
   *
   * @param defaultValue
   *        default value if fails
   *
   * @return
   *         array item from given index or default value on error
   */
  public static <T extends Object> T safeGetArray(T[] array, int index, T defaultValue) {
    if (array != null) {
      // check indexes
      int arrayLength = array.length;
      if (isBetween(-1, arrayLength, index)) {
        return array[index];
      }
    }
    return defaultValue;
  }
}
