package com.mstendo.rtp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import com.mstendo.rtp.channels.Channel;
import com.mstendo.rtp.channels.settings.Cooldown;
import com.mstendo.rtp.channels.settings.Costs;
import com.mstendo.rtp.configuration.Config;
import com.mstendo.rtp.configuration.data.CommandMessages;
import com.mstendo.rtp.utils.Utils;

public class RtpCommand implements TabExecutor {
  private final MSTRandomTeleport plugin;
  
  private final Config pluginConfig;
  
  private final RtpManager rtpManager;
  
  public RtpCommand(MSTRandomTeleport plugin) {
    this.plugin = plugin;
    this.rtpManager = plugin.getRtpManager();
    this.pluginConfig = plugin.getPluginConfig();
  }
  
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
    this.rtpManager.printDebug("Command executed: " + label + " by " + sender.getName() + " with args: " + java.util.Arrays.toString(args));
    if (!(sender instanceof Player) && (args.length == 0 || !args[0].equalsIgnoreCase("admin"))) {
      this.plugin.getPluginLogger().info("Вы должны быть игроком!");
      return true;
    } 
    if (args.length == 0) {
      Player player = (Player)sender;
      if (this.rtpManager.hasActiveTasks(player.getName())) {
        this.rtpManager.printDebug("Player " + player.getName() + " has active tasks, command ignored");
        return true;
      } 
      Channel channel = this.rtpManager.getDefaultChannel();
      if (channel == null) {
        Utils.sendMessage(this.pluginConfig.getCommandMessages().channelNotSpecified(), player);
        return true;
      } 
      processTeleport(player, channel);
      return true;
    } 
    if (args[0].equalsIgnoreCase("admin")) {
      processAdminCommand(sender, args);
      return true;
    } 
    if (args.length == 1) {
      Player player = (Player)sender;
      if (this.rtpManager.hasActiveTasks(player.getName())) {
        if (args[0].equalsIgnoreCase("cancel") && player.hasPermission("rtp.cancel")) {
          RtpTask task = this.rtpManager.getPerPlayerActiveRtpTask().get(player.getName());
          if (task != null) {
            task.cancel();
            Utils.sendMessage(this.pluginConfig.getCommandMessages().cancelled(), player);
          } else {
            this.rtpManager.printDebug("Player " + player.getName() + " has active tasks but task is null (probably in teleportingNow)");
          }
        } else {
          this.rtpManager.printDebug("Player " + player.getName() + " has active tasks, command ignored (not cancel)");
        }
        return true;
      } 
      Channel channel = this.rtpManager.getChannelById(args[0]);
      if (channel == null) {
        Utils.sendMessage(this.pluginConfig.getCommandMessages().incorrectChannel(), player);
        return true;
      } 
      processTeleport(player, channel);
      return true;
    } 
    sender.sendMessage(this.pluginConfig.getCommandMessages().incorrectChannel());
    return true;
  }
  
  private void processTeleport(Player player, Channel channel) {
    this.rtpManager.printDebug("processTeleport called for player " + player.getName() + " with channel " + channel.id());
    this.rtpManager.printDebug("Channel name: " + channel.name() + " Channel permission: rtp.channel." + channel.id());
    this.rtpManager.printDebug("Player permission status: " + player.hasPermission("rtp.channel." + channel.id()));
    if (!player.hasPermission("rtp.channel." + channel.id())) {
      this.rtpManager.printDebug("Player " + player.getName() + " doesn't have permission rtp.channel." + channel.id());
      Utils.sendMessage(channel.messages().noPerms(), player);
      return;
    } 
    Cooldown cooldown = channel.settings().cooldown();
    if (cooldown.hasCooldown(player)) {
      Utils.sendMessage(channel.messages().cooldown()
          .replace("%time%", 
            Utils.getTime((int)(this.rtpManager.getChannelCooldown(player, cooldown) - (System.currentTimeMillis() - ((Long)cooldown.playerCooldowns().get(player.getName())).longValue()) / 1000L))), player);
      return;
    } 
    if (channel.minPlayersToUse() > 0 && Bukkit.getOnlinePlayers().size() - 1 < channel.minPlayersToUse()) {
      Utils.sendMessage(channel.messages().notEnoughPlayers().replace("%required%", Integer.toString(channel.minPlayersToUse())), player);
      return;
    } 
    if (!this.rtpManager.takeCost(player, channel)) {
      this.rtpManager.printDebug("Take cost for channel " + channel.id() + " didn't pass for player " + player.getName());
      // Check which cost failed and send appropriate message
      Costs costs = channel.settings().costs();
      if (costs.moneyCost() >= 0 && !costs.processMoneyCost(player, channel)) {
        Utils.sendMessage(channel.messages().notEnoughMoney(), player);
      } else if (costs.hungerCost() >= 0 && !costs.processHungerCost(player, channel)) {
        Utils.sendMessage(channel.messages().notEnoughHunger(), player);
      } else if (costs.expCost() >= 0 && !costs.processExpCost(player, channel)) {
        Utils.sendMessage(channel.messages().notEnoughExp(), player);
      }
      return;
    }
    this.rtpManager.printDebug("Cost taken successfully, calling preTeleport for player " + player.getName()); 
    if (!channel.activeWorlds().contains(player.getWorld())) {
      this.rtpManager.printDebug("Active worlds for channel " + channel.id() + " does not includes player's world: " + player.getWorld().getName());
      if (channel.teleportToFirstAllowedWorld()) {
        this.rtpManager.printDebug("Teleporting to first allowed world: " + ((World)channel.activeWorlds().get(0)).getName());
        this.rtpManager.preTeleport(player, channel, channel.activeWorlds().get(0), false);
        return;
      } 
      Utils.sendMessage(channel.messages().invalidWorld(), player);
      return;
    } 
    this.rtpManager.preTeleport(player, channel, player.getWorld(), false);
  }
  
  private void processAdminCommand(CommandSender sender, String[] args) {
    FileConfiguration config;
    Player targetPlayer;
    String message;
    Channel channel;
    CommandMessages commandMessages = this.pluginConfig.getCommandMessages();
    if (!sender.hasPermission("rtp.admin")) {
      sender.sendMessage(commandMessages.incorrectChannel());
      return;
    } 
    if (args.length < 2) {
      sender.sendMessage(commandMessages.adminHelp());
      return;
    } 
    switch (args[1].toLowerCase()) {
      case "reload":
        this.rtpManager.cancelAllTasks();
        this.plugin.reloadConfig();
        config = this.plugin.getConfig();
        Utils.setupColorizer(config.getConfigurationSection("main_settings"));
        this.pluginConfig.setupMessages(config);
        this.pluginConfig.setupTemplates();
        this.rtpManager.getNamedChannels().clear();
        this.rtpManager.getSpecifications().clearAll();
        this.rtpManager.setupChannels(config, Bukkit.getPluginManager());
        sender.sendMessage(commandMessages.reload());
        return;
      case "teleport":
      case "forceteleport":
      case "forcertp":
        if (args.length < 4 || args.length > 5) {
          sender.sendMessage(commandMessages.unknownArgument());
          return;
        } 
        targetPlayer = Bukkit.getPlayerExact(args[2]);
        if (targetPlayer == null) {
          sender.sendMessage(commandMessages.playerNotFound());
          return;
        } 
        channel = this.rtpManager.getChannelById(args[3]);
        if (channel == null) {
          sender.sendMessage(commandMessages.incorrectChannel());
          return;
        } 
        if (!channel.activeWorlds().contains(targetPlayer.getWorld())) {
          if (channel.teleportToFirstAllowedWorld()) {
            processForceTeleport(args, targetPlayer, channel, channel.activeWorlds().get(0));
            return;
          } 
          sender.sendMessage(channel.messages().invalidWorld());
          return;
        } 
        processForceTeleport(args, targetPlayer, channel, targetPlayer.getWorld());
        return;
      case "help":
        sender.sendMessage(commandMessages.adminHelp());
        return;
      case "update":
        checkAndUpdatePlugin(sender, this.plugin);
        return;
      case "debug":
        Utils.DEBUG = !Utils.DEBUG;
        message = "§7Дебаг переключен в значение: " + (Utils.DEBUG ? "§a" : "§c") + Utils.DEBUG;
        sender.sendMessage(message);
        return;
    } 
    sender.sendMessage(commandMessages.unknownArgument());
  }
  
  public void checkAndUpdatePlugin(CommandSender sender, MSTRandomTeleport plugin) {
    Bukkit.getScheduler().runTaskAsynchronously((Plugin)plugin, () -> Utils.checkUpdates(plugin, version -> {
      sender.sendMessage("§aПроверка обновлений завершена. Версия: " + version);
    }));
  }
  
  public void downloadFile(String fileURL, File targetFile, CommandSender sender) throws IOException {
    URL url = new URL(fileURL);
    URLConnection connection = url.openConnection();
    int fileSize = connection.getContentLength();
    BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
    try {
      FileOutputStream out = new FileOutputStream(targetFile);
      try {
        byte[] data = new byte[1024];
        int totalBytesRead = 0;
        int lastPercentage = 0;
        int bytesRead;
        while ((bytesRead = in.read(data, 0, 1024)) != -1) {
          out.write(data, 0, bytesRead);
          totalBytesRead += bytesRead;
          int progressPercentage = (int)(totalBytesRead / fileSize * 100.0D);
          if (progressPercentage >= lastPercentage + 10) {
            lastPercentage = progressPercentage;
            int downloadedKB = totalBytesRead / 1024;
            int fullSizeKB = fileSize / 1024;
            sender.sendMessage("§aЗагрузка: " + downloadedKB + "/" + fullSizeKB + "KB (" + progressPercentage + "%)");
          } 
        } 
        out.close();
      } catch (Throwable throwable) {
        try {
          out.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
      in.close();
    } catch (Throwable throwable) {
      try {
        in.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  private void processForceTeleport(String[] args, Player targetPlayer, Channel channel, World world) {
    if (args.length == 5 && args[4].equalsIgnoreCase("force")) {
      this.rtpManager.preTeleport(targetPlayer, channel, world, true);
      return;
    } 
    this.rtpManager.preTeleport(targetPlayer, channel, world, false);
  }
  
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
    List<String> completions = new ArrayList<>();
    if (args.length == 1 && sender instanceof Player) {
      Player player = (Player)sender;
      if (this.rtpManager.hasActiveTasks(player.getName()) && player.hasPermission("rtp.cancel")) {
        completions.add("cancel");
        return getResult(args, completions);
      } 
      for (String channelName : this.rtpManager.getNamedChannels().keySet()) {
        if (player.hasPermission("rtp.channel." + channelName))
          completions.add(channelName); 
      } 
    } 
    if (sender.hasPermission("rtp.admin")) {
      if (args.length == 1)
        completions.add("admin"); 
      if (args[0].equalsIgnoreCase("admin")) {
        if (args.length == 2) {
          completions.add("help");
          completions.add("reload");
          completions.add("teleport");
          completions.add("forceteleport");
          completions.add("forcertp");
          completions.add("update");
          completions.add("debug");
        } 
        if (args.length > 2 && isForceRtp(args[1]))
          getForceRtpTabCompletion(args, completions); 
      } 
    } 
    return getResult(args, completions);
  }
  
  private boolean isForceRtp(String arg) {
    return (arg.equalsIgnoreCase("forceteleport") || arg
      .equalsIgnoreCase("forcertp") || arg
      .equalsIgnoreCase("teleport"));
  }
  
  private void getForceRtpTabCompletion(String[] args, List<String> completions) {
    if (args.length == 3)
      for (Player onlinePlayer : Bukkit.getOnlinePlayers())
        completions.add(onlinePlayer.getName());  
    if (args.length == 4)
      completions.addAll(this.rtpManager.getNamedChannels().keySet()); 
    if (args.length == 5)
      completions.add("force"); 
  }
  
  private List<String> getResult(String[] args, List<String> completions) {
    List<String> result = new ArrayList<>();
    for (int i = 0; i < completions.size(); i++) {
      String c = completions.get(i);
      if (StringUtil.startsWithIgnoreCase(c, args[args.length - 1]))
        result.add(c); 
    } 
    return result;
  }
}
