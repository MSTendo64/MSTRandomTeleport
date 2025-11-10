package com.mstendo.rtp;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.mstendo.rtp.channels.Channel;
import com.mstendo.rtp.channels.Settings;
import com.mstendo.rtp.channels.settings.Cooldown;
import com.mstendo.rtp.channels.settings.Costs;
import com.mstendo.rtp.configuration.Config;
import com.mstendo.rtp.utils.Utils;

public class RtpExpansion extends PlaceholderExpansion {
  private final RtpManager rtpManager;
  
  private final Config pluginConfig;
  
  public RtpExpansion(MSTRandomTeleport plugin) {
    this.rtpManager = plugin.getRtpManager();
    this.pluginConfig = plugin.getPluginConfig();
  }
  
  @NotNull
  public String getAuthor() {
    return "mstendo";
  }
  
  @NotNull
  public String getIdentifier() {
    return "msrtp";
  }
  
  @NotNull
  public String getVersion() {
    return "1.0.0";
  }
  
  public boolean persist() {
    return true;
  }
  
  public String onPlaceholderRequest(Player player, @NotNull String params) {
    String[] args = params.split("_");
    if (args.length < 2)
      return null; 
    String placeholderType = args[0].toLowerCase();
    Channel channel = this.rtpManager.getChannelById(args[1]);
    if (channel == null)
      return null; 
    Cooldown channelCooldown = channel.settings().cooldown();
    switch (placeholderType) {
      case "hascooldown":
      
      case "cooldown":
      
      case "settings":
      
    } 
    return 
      
      null;
  }
  
  private String getSettingValue(Player player, Channel channel, String[] args) {
    if (args.length < 3)
      return null; 
    String settingName = args[2].toLowerCase();
    switch (settingName) {
      case "name":
      
      case "type":
      
      case "playersrequired":
      
      case "cost":
      
      case "cooldown":
      
    } 
    return 
      
      null;
  }
  
  private boolean isPlayerValid(Player player) {
    return (player != null && player.isOnline());
  }
  
  private String getCooldownValue(Player player, String[] args, Cooldown channelCooldown) {
    if (!channelCooldown.hasCooldown(player))
      return this.pluginConfig.getPlaceholderMessages().noCooldown(); 
    int cooldown = calculateCooldown(player, channelCooldown);
    if (args.length < 3)
      return Utils.getTime(cooldown); 
    return getCooldownTimeComponent(args[2], cooldown);
  }
  
  private int calculateCooldown(Player player, Cooldown channelCooldown) {
    long playerCooldownStart = ((Long)channelCooldown.playerCooldowns().get(player.getName())).longValue();
    return (int)(this.rtpManager.getChannelCooldown(player, channelCooldown) - (System.currentTimeMillis() - playerCooldownStart) / 1000L);
  }
  
  private String getCooldownTimeComponent(String timeUnit, int cooldown) {
    switch (timeUnit) {
      case "hours":
      
      case "minutes":
      
      case "seconds":
      
    } 
    return 
      
      null;
  }
  
  private String getCostValue(Settings settings, String[] args) {
    if (args.length < 4)
      return null; 
    Costs costs = settings.costs();
    String costIdentifier = args[3];
    switch (costIdentifier) {
      case "money":
      
      case "hunger":
      
      case "exp":
      
    } 
    return 
      
      null;
  }
  
  private String getCooldownValue(Player player, Settings settings, String[] args) {
    if (args.length < 4)
      return null; 
    Cooldown cooldown = settings.cooldown();
    String cooldownIdentifier = args[3];
    switch (cooldownIdentifier) {
      case "default":
        return 
          (args.length == 5 && args[4].equalsIgnoreCase("formatted")) ? 
          Utils.getTime(cooldown.defaultCooldown()) : 
          getValueIfPositiveOrDefault(cooldown.defaultCooldown());
      case "byplayergroup":
        return isPlayerValid(player) ? (
          (args.length == 5 && args[4].equalsIgnoreCase("formatted")) ? 
          Utils.getTime(this.rtpManager.getChannelCooldown(player, cooldown)) : 
          getValueIfPositiveOrDefault(this.rtpManager.getChannelCooldown(player, cooldown))) : 
          null;
    } 
    return null;
  }
  
  private String getValueIfPositiveOrDefault(int value) {
    return (value > 0) ? Integer.toString(value) : this.pluginConfig.getPlaceholderMessages().noValue();
  }
  
  private String getValueIfPositiveOrDefault(double value) {
    return (value > 0.0D) ? Double.toString(value) : this.pluginConfig.getPlaceholderMessages().noValue();
  }
  
  public String getBooleanPlaceholder(boolean b) {
    return b ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
  }
}
