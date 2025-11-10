package com.mstendo.rtp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import lombok.Generated;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import com.mstendo.rtp.MSTRandomTeleport;
import com.mstendo.rtp.channels.settings.ParticleData;
import com.mstendo.rtp.configuration.Config;
import com.mstendo.rtp.utils.color.Colorizer;
import com.mstendo.rtp.utils.color.VanillaColorizer;

public final class Utils {
  public static boolean DEBUG;
  
  public static Colorizer COLORIZER;
  
  public static final char COLOR_CHAR = '§';
  
  public static boolean USE_PAPI;
  
  @Generated
  private Utils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  public static void setupColorizer(ConfigurationSection mainSettings) {
    switch (mainSettings.getString("serializer", "LEGACY").toUpperCase(Locale.ENGLISH)) {
      case "MINIMESSAGE":
      
      case "LEGACY":
      
      case "LEGACY_ADVANCED":
      
      default:
        break;
    } 
    COLORIZER = (Colorizer)new VanillaColorizer();
  }
  
  public static List<World> getWorldList(List<String> worldNames) {
    List<World> worldList = new ArrayList<>(worldNames.size());
    if (!worldNames.isEmpty() && ((String)worldNames.get(0)).equals("*")) {
      worldList.addAll(Bukkit.getWorlds());
      return worldList;
    } 
    for (String w : worldNames)
      worldList.add(Bukkit.getWorld(w)); 
    return worldList;
  }
  
  public static ParticleData createParticleData(String id) {
    int separatorIndex = id.indexOf(';');
    Particle particle = (separatorIndex == -1) ? Particle.valueOf(id.toUpperCase(Locale.ENGLISH)) : Particle.valueOf(id.substring(0, separatorIndex).toUpperCase(Locale.ENGLISH));
    Particle.DustOptions data = (separatorIndex != -1) ? parseParticleData(particle, id.substring(separatorIndex + 1)) : null;
    return new ParticleData(particle, data);
  }
  
  private static Particle.DustOptions parseParticleData(Particle particle, String value) {
    if (!particle.getDataType().isAssignableFrom(Particle.DustOptions.class))
      return null; 
    String[] parts = value.split(",");
    if (parts.length < 3)
      return null; 
    int red = Integer.parseInt(parts[0].trim());
    int green = Integer.parseInt(parts[1].trim());
    int blue = Integer.parseInt(parts[2].trim());
    float size = (parts.length > 3) ? Float.parseFloat(parts[3].trim()) : 1.0F;
    return new Particle.DustOptions(Color.fromRGB(red, green, blue), size);
  }
  
  public static void checkUpdates(MSTRandomTeleport plugin, Consumer<String> consumer) {
    Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)plugin, () -> {
          try {
            BufferedReader reader = new BufferedReader(new InputStreamReader((new URL("https://raw.githubusercontent.com/mstendo/MSTRandomTeleport/master/VERSION")).openStream()));
            try {
              consumer.accept(reader.readLine().trim());
              reader.close();
            } catch (Throwable throwable) {
              try {
                reader.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              } 
              throw throwable;
            } 
          } catch (IOException ex) {
            plugin.getLogger().warning("Unable to check for updates: " + ex.getMessage());
          } 
        }, 30L);
  }
  
  public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
    char[] b = textToTranslate.toCharArray();
    for (int i = 0, length = b.length - 1; i < length; i++) {
      if (b[i] == altColorChar && isValidColorCharacter(b[i + 1])) {
        b[i++] = '§';
        b[i] = (char)(b[i] | 0x20);
      } 
    } 
    return new String(b);
  }
  
  private static boolean isValidColorCharacter(char c) {
    return ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || c == 'r' || (c >= 'k' && c <= 'o') || c == 'x' || (c >= 'A' && c <= 'F') || c == 'R' || (c >= 'K' && c <= 'O') || c == 'X');
  }
  
  public static void sendMessage(String message, Player player) {
    if (message == null || message.trim().isEmpty())
      return; 
    if (USE_PAPI) {
      player.sendMessage(parsePlaceholders(message, player));
      return;
    } 
    player.sendMessage(message);
  }
  
  public static String parsePlaceholders(String message, Player player) {
    if (PlaceholderAPI.containsPlaceholders(message))
      message = PlaceholderAPI.setPlaceholders(player, message); 
    return message;
  }
  
  public static String locationToString(Location location) {
    return "(" + location.getWorld().getName() + "/" + location.getBlockX() + "/" + location.getBlockY() + "/" + location.getBlockZ() + ")";
  }
  
  public static String getTime(int time) {
    int hours = getHours(time);
    int minutes = getMinutes(time);
    int seconds = getSeconds(time);
    StringBuilder result = new StringBuilder();
    if (hours > 0)
      result.append(hours).append(Config.timeHours); 
    if (minutes > 0 || hours > 0)
      result.append(minutes).append(Config.timeMinutes); 
    if (seconds > 0 || (hours == 0 && minutes == 0))
      result.append(seconds).append(Config.timeSeconds); 
    return result.toString();
  }
  
  public static int getHours(int time) {
    return time / 3600;
  }
  
  public static int getMinutes(int time) {
    return time % 3600 / 60;
  }
  
  public static int getSeconds(int time) {
    return time % 60;
  }
  
  public static boolean isNumeric(CharSequence cs) {
    if (cs == null || cs.length() == 0)
      return false; 
    for (int i = 0, length = cs.length(); i < length; i++) {
      if (!Character.isDigit(cs.charAt(i)))
        return false; 
    } 
    return true;
  }
  
  public static String replaceEach(@NotNull String text, @NotNull String[] searchList, @NotNull String[] replacementList) {
    if (text.isEmpty() || searchList.length == 0 || replacementList.length == 0)
      return text; 
    if (searchList.length != replacementList.length)
      throw new IllegalArgumentException("Search and replacement arrays must have the same length."); 
    StringBuilder result = new StringBuilder(text);
    for (int i = 0; i < searchList.length; i++) {
      String search = searchList[i];
      String replacement = replacementList[i];
      int start = 0;
      while ((start = result.indexOf(search, start)) != -1) {
        result.replace(start, start + search.length(), replacement);
        start += replacement.length();
      } 
    } 
    return result.toString();
  }
}
