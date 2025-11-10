package com.mstendo.rtp.utils.logging;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.mstendo.rtp.MSTRandomTeleport;
import org.bukkit.Bukkit;

public class PaperLogger implements Logger {
  private final LegacyComponentSerializer legacySection;
  
  public PaperLogger(MSTRandomTeleport plugin) {
    this.legacySection = LegacyComponentSerializer.legacySection();
  }
  
  public void info(String msg) {
    Component component = this.legacySection.deserialize(msg);
    try {
      Bukkit.getServer().getConsoleSender().getClass().getMethod("sendMessage", net.kyori.adventure.text.Component.class).invoke(Bukkit.getServer().getConsoleSender(), component);
    } catch (Exception e) {
      Bukkit.getServer().getConsoleSender().sendMessage(msg);
    }
  }
  
  public void warn(String msg) {
    Component component = this.legacySection.deserialize(msg);
    try {
      Bukkit.getServer().getConsoleSender().getClass().getMethod("sendMessage", net.kyori.adventure.text.Component.class).invoke(Bukkit.getServer().getConsoleSender(), component);
    } catch (Exception e) {
      Bukkit.getServer().getConsoleSender().sendMessage(msg);
    }
  }
}
