package com.mstendo.rtp.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.mstendo.rtp.MSTRandomTeleport;
import com.mstendo.rtp.RtpManager;
import com.mstendo.rtp.channels.Settings;
import com.mstendo.rtp.channels.settings.Messages;
import com.mstendo.rtp.configuration.data.CommandMessages;
import com.mstendo.rtp.configuration.data.PlaceholderMessages;
import com.mstendo.rtp.utils.Utils;

public class Config {
  private final MSTRandomTeleport plugin;
  
  private final RtpManager rtpManager;
  
  private String messagesPrefix;
  
  private Messages defaultChannelMessages;
  
  private CommandMessages commandMessages;
  
  private PlaceholderMessages placeholderMessages;
  
  public static String timeHours;
  
  public static String timeMinutes;
  
  public static String timeSeconds;
  
  private final Map<String, Settings> channelTemplates;
  
  public Config(MSTRandomTeleport plugin) {
    this.channelTemplates = new HashMap<>();
    this.plugin = plugin;
    this.rtpManager = plugin.getRtpManager();
  }
  
  @Generated
  public String getMessagesPrefix() {
    return this.messagesPrefix;
  }
  
  @Generated
  public Messages getDefaultChannelMessages() {
    return this.defaultChannelMessages;
  }
  
  @Generated
  public CommandMessages getCommandMessages() {
    return this.commandMessages;
  }
  
  @Generated
  public PlaceholderMessages getPlaceholderMessages() {
    return this.placeholderMessages;
  }
  
  @Generated
  public Map<String, Settings> getChannelTemplates() {
    return this.channelTemplates;
  }
  
  public void setupMessages(FileConfiguration config) {
    ConfigurationSection messages = config.getConfigurationSection("messages");
    this.messagesPrefix = Utils.COLORIZER.colorize(messages.getString("prefix", "messages.prefix"));
    this
      
      .defaultChannelMessages = new Messages(getPrefixed(messages.getString("no_perms", "messages.no_perms"), this.messagesPrefix), getPrefixed(messages.getString("invalid_world", "messages.invalid_world"), this.messagesPrefix), getPrefixed(messages.getString("not_enough_players", "messages.not_enough_players"), this.messagesPrefix), getPrefixed(messages.getString("not_enough_money", "messages.not_enough_money"), this.messagesPrefix), getPrefixed(messages.getString("not_enough_hunger", "messages.not_enough_hunger"), this.messagesPrefix), getPrefixed(messages.getString("not_enough_experience", "messages.not_enough_experience"), this.messagesPrefix), getPrefixed(messages.getString("cooldown", "messages.cooldown"), this.messagesPrefix), getPrefixed(messages.getString("moved_on_teleport", "messages.moved_on_teleport"), this.messagesPrefix), getPrefixed(messages.getString("teleported_on_teleport", "messages.teleported_on_teleport"), this.messagesPrefix), getPrefixed(messages.getString("damaged_on_teleport", "messages.damaged_on_teleport"), this.messagesPrefix), getPrefixed(messages.getString("damaged_other_on_teleport", "messages.damaged_other_on_teleport"), this.messagesPrefix), getPrefixed(messages.getString("fail_to_find_location", "messages.fail_to_find_location"), this.messagesPrefix));
    ConfigurationSection admin = messages.getConfigurationSection("admin");
    this
      
      .commandMessages = new CommandMessages(getPrefixed(messages.getString("incorrect_channel", "messages.incorrect_channel"), this.messagesPrefix), getPrefixed(messages.getString("channel_not_specified", "messages.channel_not_specified"), this.messagesPrefix), getPrefixed(messages.getString("canceled", "messages.canceled"), this.messagesPrefix), getPrefixed(admin.getString("reload"), this.messagesPrefix), getPrefixed(admin.getString("unknown_argument"), this.messagesPrefix), getPrefixed(admin.getString("player_not_found"), this.messagesPrefix), getPrefixed(admin.getString("admin_help"), this.messagesPrefix));
    ConfigurationSection placeholders = messages.getConfigurationSection("placeholders");
    this
      
      .placeholderMessages = new PlaceholderMessages(Utils.COLORIZER.colorize(placeholders.getString("no_cooldown", "&aКулдаун отсутствует!")), Utils.COLORIZER.colorize(placeholders.getString("no_value", "&cОтсутствует")));
    ConfigurationSection time = placeholders.getConfigurationSection("time");
    timeHours = Utils.COLORIZER.colorize(time.getString("hours", " ч."));
    timeMinutes = Utils.COLORIZER.colorize(time.getString("minutes", " мин."));
    timeSeconds = Utils.COLORIZER.colorize(time.getString("seconds", " сек."));
  }
  
  public String getPrefixed(String message, String prefix) {
    if (message == null || prefix == null)
      return message; 
    return Utils.COLORIZER.colorize(message.replace("%prefix%", prefix));
  }
  
  public void setupTemplates() {
    FileConfiguration templatesConfig = getFile(this.plugin.getDataFolder().getAbsolutePath(), "templates.yml");
    for (String templateID : templatesConfig.getKeys(false)) {
      ConfigurationSection templateSection = templatesConfig.getConfigurationSection(templateID);
      Settings newTemplate = Settings.create(this.plugin, templateSection, this, null, false);
      this.channelTemplates.put(templateID, newTemplate);
    } 
  }
  
  public boolean isConfigValueExist(ConfigurationSection section, String key) {
    return (section.getString(key) != null);
  }
  
  public boolean isNullSection(ConfigurationSection section) {
    return (section == null);
  }
  
  public List<String> getStringListInAnyCase(Object raw) {
    List<String> stringList = new ArrayList<>();
    if (raw instanceof String) {
      String singleId = (String)raw;
      stringList.add(singleId);
    } else if (raw instanceof List) {
      List<?> listIds = (List)raw;
      for (Object obj : listIds) {
        if (obj instanceof String) {
          String id = (String)obj;
          stringList.add(id);
        } 
      } 
    } 
    return stringList;
  }
  
  public FileConfiguration getChannelFile(String path, String fileName) {
    File file = new File(path, fileName);
    if (!file.exists()) {
      this.plugin.saveResource("channels/" + fileName, false);
      this.plugin.getPluginLogger().warn("Channel file with name " + fileName + " does not exist.");
    } 
    return (FileConfiguration)YamlConfiguration.loadConfiguration(file);
  }
  
  public FileConfiguration getFile(String path, String fileName) {
    File file = new File(path, fileName);
    if (!file.exists())
      this.plugin.saveResource(fileName, false); 
    return (FileConfiguration)YamlConfiguration.loadConfiguration(file);
  }
}
