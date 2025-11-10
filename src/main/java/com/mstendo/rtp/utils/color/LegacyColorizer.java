package com.mstendo.rtp.utils.color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.mstendo.rtp.utils.Utils;

public class LegacyColorizer implements Colorizer {
  private static final Pattern HEX_PATTERN = Pattern.compile("&#([a-fA-F\\d]{6})");
  
  public String colorize(String message) {
    if (message == null || message.isEmpty())
      return message; 
    Matcher matcher = HEX_PATTERN.matcher(message);
    StringBuffer builder = new StringBuffer(message.length() + 32);
    while (matcher.find()) {
      String group = matcher.group(1);
      matcher.appendReplacement(builder, "§x§" + group
          
          .charAt(0) + "§" + group
          .charAt(1) + "§" + group
          .charAt(2) + "§" + group
          .charAt(3) + "§" + group
          .charAt(4) + "§" + group
          .charAt(5));
    } 
    message = matcher.appendTail(builder).toString();
    return Utils.translateAlternateColorCodes('&', message);
  }
}
