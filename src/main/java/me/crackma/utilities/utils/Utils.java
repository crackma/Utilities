package me.crackma.utilities.utils;

public class Utils {
  public String convertArray(String[] stringArray, String delimiter, int exclude) {
    StringBuilder stringBuilder = new StringBuilder();
    int current = 0;
    for (String string : stringArray) {
      current++;
      if (current <= exclude) continue;
      if (current == stringArray.length) {
        stringBuilder.append(string);
        return stringBuilder.toString();
      }
      stringBuilder.append(string).append(delimiter);
    }
    return stringBuilder.toString();
  }
}
