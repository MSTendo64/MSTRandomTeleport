package com.mstendo.rtp;

import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import com.mstendo.rtp.channels.Channel;
import com.mstendo.rtp.utils.Utils;

public class RtpTask {
  private final MSTRandomTeleport plugin;
  private final RtpManager rtpManager;
  private final String playerName;
  private final Channel activeChannel;
  private BukkitTask task;
  private Location targetLocation;
  private boolean cancelled = false;

  public RtpTask(MSTRandomTeleport plugin, RtpManager rtpManager, String playerName, int cooldown, Channel channel) {
    this.plugin = plugin;
    this.rtpManager = rtpManager;
    this.playerName = playerName;
    this.activeChannel = channel;
    this.rtpManager.getPerPlayerActiveRtpTask().put(playerName, this);
  }

  @Generated
  public Channel getActiveChannel() {
    return this.activeChannel;
  }

  public void startPreTeleportTimer(Player player, Channel channel, Location loc) {
    if (this.cancelled) {
      return;
    }
    this.targetLocation = loc;
    int cooldown = this.rtpManager.getChannelPreTeleportCooldown(player, channel.settings().cooldown());
    if (cooldown <= 0) {
      this.rtpManager.teleportPlayer(player, channel, loc);
      // teleportPlayer already removes from perPlayerActiveRtpTask
      return;
    }
    this.rtpManager.printDebug("Starting pre-teleport timer for player " + this.playerName + " with cooldown " + cooldown + " seconds, location: " + (loc != null ? Utils.locationToString(loc) : "null"));
    this.task = Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
      try {
        this.rtpManager.printDebug("Pre-teleport timer expired for player " + this.playerName + ", cancelled: " + this.cancelled);
        if (!this.cancelled) {
          Player targetPlayer = Bukkit.getPlayer(this.playerName);
          if (targetPlayer != null && targetPlayer.isOnline()) {
            if (this.targetLocation != null && this.targetLocation.getWorld() != null) {
              this.rtpManager.printDebug("Teleporting player " + this.playerName + " to location " + Utils.locationToString(this.targetLocation));
              this.rtpManager.teleportPlayer(targetPlayer, channel, this.targetLocation);
            } else {
              this.rtpManager.printDebug("Target location is null or world is null for player " + this.playerName + ", location: " + (this.targetLocation != null ? this.targetLocation.toString() : "null"));
              this.rtpManager.returnCost(targetPlayer, channel);
              Utils.sendMessage(channel.messages().failToFindLocation(), targetPlayer);
            }
          } else {
            this.rtpManager.printDebug("Player " + this.playerName + " not found or offline when teleport timer expired");
          }
          this.rtpManager.getPerPlayerActiveRtpTask().remove(this.playerName);
        } else {
          this.rtpManager.printDebug("Task was cancelled for player " + this.playerName);
        }
      } catch (Exception e) {
        this.rtpManager.printDebug("Error in pre-teleport timer task for player " + this.playerName + ": " + e.getMessage());
        e.printStackTrace();
        // Ensure cleanup even on error
        this.rtpManager.cleanupPlayerState(this.playerName);
        Player targetPlayer = Bukkit.getPlayer(this.playerName);
        if (targetPlayer != null && targetPlayer.isOnline()) {
          this.rtpManager.returnCost(targetPlayer, channel);
        }
      }
    }, cooldown * 20L);
  }

  public void cancel() {
    this.cancelled = true;
    if (this.task != null && !this.task.isCancelled()) {
      this.task.cancel();
    }
    this.rtpManager.getPerPlayerActiveRtpTask().remove(this.playerName);
    Player player = Bukkit.getPlayer(this.playerName);
    if (player != null && player.isOnline()) {
      this.rtpManager.returnCost(player, this.activeChannel);
    }
  }
}
