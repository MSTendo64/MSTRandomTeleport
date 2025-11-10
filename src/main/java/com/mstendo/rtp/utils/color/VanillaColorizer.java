package com.mstendo.rtp.utils.color;

import com.mstendo.rtp.utils.Utils;

public class VanillaColorizer implements Colorizer {
  public String colorize(String message) {
    if (message == null || message.isEmpty())
      return message; 
    return Utils.translateAlternateColorCodes('&', message);
  }
}

