package com.mstendo.rtp.utils.color;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MiniMessageColorizer implements Colorizer {
  private static Object miniMessage;
  
  static {
    try {
      Class<?> miniMessageClass = Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
      miniMessage = miniMessageClass.getMethod("miniMessage").invoke(null);
    } catch (Exception e) {
      miniMessage = null;
    }
  }
  
  public String colorize(String message) {
    if (message == null || message.isEmpty())
      return message;
    if (miniMessage == null)
      return message;
    try {
      Component component = (Component) miniMessage.getClass().getMethod("deserialize", String.class).invoke(miniMessage, message);
      return LegacyComponentSerializer.legacySection().serialize(component);
    } catch (Exception e) {
      return message;
    }
  }
}
