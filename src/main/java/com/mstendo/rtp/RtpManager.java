package com.mstendo.rtp;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntSortedMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import com.mstendo.rtp.actions.Action;
import com.mstendo.rtp.actions.ActionRegistry;
import com.mstendo.rtp.actions.ActionType;
import com.mstendo.rtp.actions.impl.ActionBarActionType;
import com.mstendo.rtp.actions.impl.ConsoleActionType;
import com.mstendo.rtp.actions.impl.EffectActionType;
import com.mstendo.rtp.actions.impl.MessageActionType;
import com.mstendo.rtp.actions.impl.SoundActionType;
import com.mstendo.rtp.actions.impl.TitleActionType;
import com.mstendo.rtp.channels.Channel;
import com.mstendo.rtp.channels.ChannelType;
import com.mstendo.rtp.channels.Settings;
import com.mstendo.rtp.channels.settings.Cooldown;
import com.mstendo.rtp.channels.settings.Costs;
import com.mstendo.rtp.channels.settings.LocationGenOptions;
import com.mstendo.rtp.channels.settings.Messages;
import com.mstendo.rtp.channels.settings.Particles;
import com.mstendo.rtp.configuration.Config;
import com.mstendo.rtp.utils.Utils;

public final class RtpManager {
  private final MSTRandomTeleport plugin;
  
  private final Config pluginConfig;
  
  private final ActionRegistry actionRegistry;
  
  private Channel defaultChannel;
  
  @Generated
  public ActionRegistry getActionRegistry() {
    return this.actionRegistry;
  }
  
  @Generated
  public Channel getDefaultChannel() {
    return this.defaultChannel;
  }
  
  private final Map<String, Channel> namedChannels = new HashMap<>();
  
  @Generated
  public Map<String, Channel> getNamedChannels() {
    return this.namedChannels;
  }
  
  private final Specifications specifications = new Specifications(new HashSet<String>(), new HashMap<String, List<World>>(), (Object2IntMap<String>)new Object2IntOpenHashMap<String>(), new HashMap<String, List<World>>());
  
  @Generated
  public Specifications getSpecifications() {
    return this.specifications;
  }
  
  private final Map<String, RtpTask> perPlayerActiveRtpTask = new ConcurrentHashMap<>();
  
  private final LocationGenerator locationGenerator;
  
  private Map<String, String> proxyCalls;
  
  private final Set<String> teleportingNow;
  
  private final String[] searchList;
  
  @Generated
  public Map<String, RtpTask> getPerPlayerActiveRtpTask() {
    return this.perPlayerActiveRtpTask;
  }
  
  @Generated
  public LocationGenerator getLocationGenerator() {
    return this.locationGenerator;
  }
  
  @Generated
  public Map<String, String> getProxyCalls() {
    return this.proxyCalls;
  }
  
  private void registerDefaultActions() {
    this.actionRegistry.register((ActionType)new ActionBarActionType());
    this.actionRegistry.register((ActionType)new ConsoleActionType());
    this.actionRegistry.register((ActionType)new EffectActionType());
    this.actionRegistry.register((ActionType)new MessageActionType());
    this.actionRegistry.register((ActionType)new SoundActionType());
    this.actionRegistry.register((ActionType)new TitleActionType());
  }
  
  public void initProxyCalls() {
    this.proxyCalls = new HashMap<>();
  }
  
  public void setupChannels(FileConfiguration config, PluginManager pluginManager) {
    long startTime = System.currentTimeMillis();
    ConfigurationSection channelsSection = config.getConfigurationSection("channels");
    if (channelsSection == null) {
      printDebug("No channels section found in config");
      return;
    }
    for (String channelId : channelsSection.getKeys(false)) {
      FileConfiguration fileConfiguration;
      printDebug("Id: " + channelId);
      ConfigurationSection channelSection = config.getConfigurationSection("channels." + channelId);
      if (channelSection != null && !channelSection.getString("file", "").isEmpty()) {
        fileConfiguration = this.pluginConfig.getChannelFile(this.plugin.getDataFolder().getAbsolutePath() + "/channels", channelSection.getString("file"));
        if (fileConfiguration == null) {
          printDebug("Unable to get channel settings. Skipping...");
          continue;
        } 
      } else {
        fileConfiguration = config;
      }
      String name = fileConfiguration.getString("name", "");
      ChannelType type = ChannelType.valueOf(fileConfiguration.getString("type", "DEFAULT").toUpperCase(Locale.ENGLISH));
      if (type == ChannelType.NEAR_REGION && !pluginManager.isPluginEnabled("WorldGuard")) {
        this.plugin.getPluginLogger().warn("§cКанал '" + channelId + "' использует тип NEAR_REGION, но WorldGuard не установлен! Канал будет пропущен.");
        printDebug("Channel '" + channelId + "' skipped: NEAR_REGION requires WorldGuard");
        continue;
      } 
      List<World> activeWorlds = Utils.getWorldList(fileConfiguration.getStringList("active_worlds"));
      boolean teleportToFirstAllowedWorld = fileConfiguration.getBoolean("teleport_to_first_world", false);
      String serverToMove = fileConfiguration.getString("server_to_move", "");
      int minPlayersToUse = fileConfiguration.getInt("min_players_to_use", -1);
      int invulnerableTicks = fileConfiguration.getInt("invulnerable_after_teleport", 12);
      Settings baseTemplate = (Settings)this.pluginConfig.getChannelTemplates().get(fileConfiguration.getString("template"));
      Settings channelSettings = Settings.create(this.plugin, (ConfigurationSection)fileConfiguration, this.pluginConfig, baseTemplate, true);
      LocationGenOptions locationGenOptions = channelSettings.locationGenOptions();
      if (locationGenOptions == null) {
        printDebug("Could not setup location generator options for channel '" + channelId + "'. Skipping...");
        continue;
      } 
      Messages messages = setupChannelMessages(fileConfiguration.getConfigurationSection("messages"));
      Channel newChannel = new Channel(channelId, name, type, activeWorlds, teleportToFirstAllowedWorld, serverToMove, minPlayersToUse, invulnerableTicks, channelSettings, messages);
      this.namedChannels.put(channelId, newChannel);
      assignChannelToSpecification(fileConfiguration.getConfigurationSection("specifications"), newChannel);
    } 
    this.defaultChannel = getChannelById(config.getString("main_settings.default_channel", ""));
    if (this.defaultChannel != null) {
      printDebug("Default channel is: " + this.defaultChannel.id());
    } else {
      printDebug("Default channel not specified.");
    } 
    long endTime = System.currentTimeMillis();
    printDebug("Channels setup done in " + (endTime - startTime) + " ms");
  }
  
  private void assignChannelToSpecification(ConfigurationSection specificationsSection, Channel newChannel) {
    this.specifications.assign(newChannel, specificationsSection);
  }
  
  private Messages setupChannelMessages(ConfigurationSection messages) {
    Messages defaultMessages = this.pluginConfig.getDefaultChannelMessages();
    if (this.pluginConfig.isNullSection(messages))
      return defaultMessages; 
    String prefix = this.pluginConfig.isConfigValueExist(messages, "prefix") ? messages.getString("prefix") : this.pluginConfig.getMessagesPrefix();
    String noPerms = getMessage(messages, "no_perms", defaultMessages.noPerms(), prefix);
    String invalidWorld = getMessage(messages, "invalid_world", defaultMessages.invalidWorld(), prefix);
    String notEnoughPlayers = getMessage(messages, "not_enough_players", defaultMessages.notEnoughPlayers(), prefix);
    String notEnoughMoney = getMessage(messages, "not_enough_money", defaultMessages.notEnoughMoney(), prefix);
    String notEnoughHunger = getMessage(messages, "not_enough_hunger", defaultMessages.notEnoughHunger(), prefix);
    String notEnoughExp = getMessage(messages, "not_enough_experience", defaultMessages.notEnoughExp(), prefix);
    String cooldown = getMessage(messages, "cooldown", defaultMessages.cooldown(), prefix);
    String movedOnTeleport = getMessage(messages, "moved_on_teleport", defaultMessages.movedOnTeleport(), prefix);
    String teleportedOnTeleport = getMessage(messages, "teleported_on_teleport", defaultMessages.teleportedOnTeleport(), prefix);
    String damagedOnTeleport = getMessage(messages, "damaged_on_teleport", defaultMessages.damagedOnTeleport(), prefix);
    String damagedOtherOnTeleport = getMessage(messages, "damaged_other_on_teleport", defaultMessages.damagedOtherOnTeleport(), prefix);
    String failToFindLocation = getMessage(messages, "fail_to_find_location", defaultMessages.failToFindLocation(), prefix);
    return new Messages(noPerms, invalidWorld, notEnoughPlayers, notEnoughMoney, notEnoughHunger, notEnoughExp, cooldown, movedOnTeleport, teleportedOnTeleport, damagedOnTeleport, damagedOtherOnTeleport, failToFindLocation);
  }
  
  private String getMessage(ConfigurationSection messages, String key, String global, String prefix) {
    return this.pluginConfig.isConfigValueExist(messages, key) ? this.pluginConfig.getPrefixed(messages.getString(key), prefix) : global;
  }
  
  public Channel getChannelById(String channelId) {
    if (channelId.isEmpty())
      return null; 
    return this.namedChannels.get(channelId);
  }
  
  public boolean hasActiveTasks(String playerName) {
    if (this.perPlayerActiveRtpTask.containsKey(playerName)) {
      return true;
    }
    if (this.teleportingNow.contains(playerName)) {
      printDebug("Player " + playerName + " is in teleportingNow but has no active task");
      return true;
    }
    return false;
  }
  
  public RtpManager(MSTRandomTeleport plugin) {
    this.teleportingNow = ConcurrentHashMap.newKeySet();
    this.searchList = new String[] { "%player%", "%name%", "%time%", "%x%", "%y%", "%z%" };
    this.plugin = plugin;
    this.pluginConfig = plugin.getPluginConfig();
    this.actionRegistry = new ActionRegistry(plugin);
    this.locationGenerator = new LocationGenerator(plugin, this);
    registerDefaultActions();
  }
  
  @Generated
  public Set<String> getTeleportingNow() {
    return this.teleportingNow;
  }
  
  public void cleanupPlayerState(String playerName) {
    this.teleportingNow.remove(playerName);
    this.perPlayerActiveRtpTask.remove(playerName);
  }
  
  private void handleLocationGenerationFailure(Player player, Channel channel, String playerName) {
    cleanupPlayerState(playerName);
    Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
      if (player.isOnline()) {
        Utils.sendMessage(channel.messages().failToFindLocation(), player);
      }
    });
    returnCost(player, channel);
  }
  
  public void preTeleport(Player player, Channel channel, World world, boolean force) {
    String playerName = player.getName();
    if (this.teleportingNow.contains(playerName))
      return; 
    if (this.proxyCalls != null && !channel.serverToMove().isEmpty()) {
      printDebug("Moving player '" + playerName + "' with channel '" + channel.id() + "' to server " + channel.serverToMove());
      this.plugin.getPluginMessage().sendCrossProxy(player, channel.serverToMove(), playerName + " " + playerName + ";" + channel.id());
      this.teleportingNow.remove(playerName);
      this.plugin.getPluginMessage().connectToServer(player, channel.serverToMove());
      return;
    } 
    Settings settings = channel.settings();
    int channelPreTeleportCooldown = getChannelPreTeleportCooldown(player, settings.cooldown());
    boolean finalForce = (force || channelPreTeleportCooldown <= 0);
    printDebug("Pre teleporting player '" + playerName + "' with channel '" + channel.id() + "' in world '" + world.getName() + "' (force: " + finalForce + ")");
    this.teleportingNow.add(playerName);
    Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
          try {
            this.locationGenerator.getIterationsPerPlayer().put(playerName, 1);
            Location loc = generateLocationForChannel(player, channel, settings, world, playerName);
            if (loc == null) {
              handleLocationGenerationFailure(player, channel, playerName);
              return;
            } 
            if (!finalForce) {
              executeActions(player, channel, settings.actions().preTeleportActions(), player.getLocation());
              printDebug("Generating task and starting pre teleport timer for player '" + playerName + "' with channel '" + channel.id() + "'");
              RtpTask rtpTask = new RtpTask(this.plugin, this, playerName, channelPreTeleportCooldown, channel);
              rtpTask.startPreTeleportTimer(player, channel, loc);
              return;
            } 
            teleportPlayer(player, channel, loc);
          } catch (Exception e) {
            printDebug("Error in preTeleport async task for player " + playerName + ": " + e.getMessage());
            e.printStackTrace();
            handleLocationGenerationFailure(player, channel, playerName);
          }
        });
  }
  
  private Location generateLocationForChannel(Player player, Channel channel, Settings settings, World world, String playerName) {
    switch (channel.type()) {
      case DEFAULT:
        return this.locationGenerator.generateRandomLocation(player, settings, world);
      case NEAR_PLAYER:
        return this.locationGenerator.generateRandomLocationNearPlayer(player, settings, world);
      case NEAR_REGION:
        return generateLocationNearRegion(player, settings, world, playerName);
      default:
        return this.locationGenerator.generateRandomLocation(player, settings, world);
    }
  }
  
  private Location generateLocationNearRegion(Player player, Settings settings, World world, String playerName) {
    if (this.locationGenerator.getWgLocationGenerator() == null) {
      printDebug("WorldGuard location generator is null for NEAR_REGION channel. This should not happen if channel was loaded correctly.");
      this.plugin.getPluginLogger().warn("§cПопытка использовать канал типа NEAR_REGION без WorldGuard для игрока " + playerName + ". Телепортация отменена.");
      return null;
    }
    
    try {
      return this.locationGenerator.getWgLocationGenerator().generateRandomLocationNearRandomRegion(player, settings, world);
    } catch (Exception e) {
      printDebug("Error generating location near region for player " + playerName + ": " + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }
  
  public boolean takeCost(Player player, Channel channel) {
    Costs costs = channel.settings().costs();
    return (costs.processMoneyCost(player, channel) && costs.processHungerCost(player, channel) && costs.processExpCost(player, channel));
  }
  
  public void returnCost(Player player, Channel channel) {
    Costs costs = channel.settings().costs();
    costs.processMoneyReturn(player);
    costs.processHungerReturn(player);
    costs.processExpReturn(player);
  }
  
  public void teleportPlayer(Player player, Channel channel, Location loc) {
    printDebug("Teleporting player '" + player.getName() + "' with channel '" + channel.id() + "' to location " + Utils.locationToString(loc));
    // Remove from active tasks if exists
    this.perPlayerActiveRtpTask.remove(player.getName());
    if (channel.invulnerableTicks() > 0) {
      player.setInvulnerable(true);
      Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
        if (player.isOnline()) {
          player.setInvulnerable(false);
        }
      }, channel.invulnerableTicks());
    }
    handlePlayerCooldown(player, channel.settings().cooldown());
    Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
          try {
            player.teleport(loc);
            this.teleportingNow.remove(player.getName());
            spawnParticleSphere(player, channel.settings().particles());
            executeActions(player, channel, channel.settings().actions().afterTeleportActions(), loc);
          } catch (Exception e) {
            printDebug("Error in teleportPlayer task for player " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
            // Ensure cleanup even on error
            this.teleportingNow.remove(player.getName());
            this.perPlayerActiveRtpTask.remove(player.getName());
          }
        });
  }
  
  public void spawnParticleSphere(Player player, Particles particles) {
    if (!particles.afterTeleportEnabled())
      return; 
    Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)this.plugin, () -> {
          Location loc = player.getLocation();
          loc.add(0.0D, 1.0D, 0.0D);
          World world = loc.getWorld();
          double goldenAngle = Math.PI * (3.0D - Math.sqrt(5.0D));
          List<Player> receivers = particles.afterTeleportSendOnlyToPlayer() ? java.util.Collections.<Player>singletonList(player) : null;
          for (int i = 0; i < particles.afterTeleportCount(); i++) {
            double yOffset = 1.0D - 2.0D * i / (particles.afterTeleportCount() - 1);
            double radiusAtHeight = Math.sqrt(1.0D - yOffset * yOffset);
            double theta = goldenAngle * i;
            double xOffset = particles.afterTeleportRadius() * radiusAtHeight * Math.cos(theta);
            double zOffset = particles.afterTeleportRadius() * radiusAtHeight * Math.sin(theta);
            Location particleLocation = loc.clone().add(xOffset, yOffset * particles.afterTeleportRadius(), zOffset);
            if (receivers != null && !receivers.isEmpty()) {
              for (Player receiver : receivers) {
                if (particles.afterTeleportParticle().dustOptions() != null) {
                  receiver.spawnParticle(particles.afterTeleportParticle().particle(), particleLocation, 1, 0.0D, 0.0D, 0.0D, particles.afterTeleportParticleSpeed(), particles.afterTeleportParticle().dustOptions());
                } else {
                  receiver.spawnParticle(particles.afterTeleportParticle().particle(), particleLocation, 1, 0.0D, 0.0D, 0.0D, particles.afterTeleportParticleSpeed());
                }
              }
            } else {
              if (particles.afterTeleportParticle().dustOptions() != null) {
                world.spawnParticle(particles.afterTeleportParticle().particle(), particleLocation, 1, 0.0D, 0.0D, 0.0D, particles.afterTeleportParticleSpeed(), particles.afterTeleportParticle().dustOptions());
              } else {
                world.spawnParticle(particles.afterTeleportParticle().particle(), particleLocation, 1, 0.0D, 0.0D, 0.0D, particles.afterTeleportParticleSpeed());
              }
            }
          } 
        }, 1L);
  }
  
  private void handlePlayerCooldown(Player player, Cooldown cooldown) {
    int cooldownTime = getChannelCooldown(player, cooldown);
    if (cooldownTime > 0 && !player.hasPermission("rtp.bypasscooldown")) {
      cooldown.setCooldown(player.getName(), cooldownTime);
    }
  }
  
  public int getChannelCooldown(Player player, Cooldown cooldown) {
    if (cooldown.defaultCooldown() < 0)
      return -1; 
    Object2IntSortedMap<String> groupCooldowns = cooldown.groupCooldowns();
    if (groupCooldowns.isEmpty())
      return cooldown.defaultCooldown(); 
    String playerGroup = this.plugin.getPerms().getPrimaryGroup(player);
    return groupCooldowns.getOrDefault(playerGroup, cooldown.defaultCooldown());
  }
  
  public int getChannelPreTeleportCooldown(Player player, Cooldown cooldown) {
    if (cooldown.defaultPreTeleportCooldown() < 0)
      return -1; 
    Object2IntSortedMap<String> preTeleportCooldowns = cooldown.preTeleportCooldowns();
    if (preTeleportCooldowns.isEmpty())
      return cooldown.defaultPreTeleportCooldown(); 
    String playerGroup = this.plugin.getPerms().getPrimaryGroup(player);
    return preTeleportCooldowns.getOrDefault(playerGroup, cooldown.defaultPreTeleportCooldown());
  }
  
  public void executeActions(Player player, Channel channel, List<Action> actionList, Location loc) {
    if (actionList.isEmpty())
      return; 
    String name = channel.name();
    String cd = Utils.getTime(getChannelPreTeleportCooldown(player, channel.settings().cooldown()));
    String x = Integer.toString(loc.getBlockX());
    String y = Integer.toString(loc.getBlockY());
    String z = Integer.toString(loc.getBlockZ());
    String[] replacementList = { player.getName(), name, cd, x, y, z };
    Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
          for (Action action : actionList)
            action.perform(channel, player, this.searchList, replacementList); 
        });
  }
  
  public void cancelAllTasks() {
    for (RtpTask task : this.perPlayerActiveRtpTask.values())
      task.cancel(); 
  }
  
  public void printDebug(String message) {
    if (Utils.DEBUG)
      this.plugin.getPluginLogger().info(message); 
  }
}