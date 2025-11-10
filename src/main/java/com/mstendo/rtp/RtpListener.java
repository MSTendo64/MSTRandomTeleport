package com.mstendo.rtp;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import com.mstendo.rtp.Specifications;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import com.mstendo.rtp.channels.Channel;
import com.mstendo.rtp.channels.settings.Restrictions;
import com.mstendo.rtp.utils.Utils;
import com.mstendo.rtp.utils.VersionUtils;

public class RtpListener implements Listener {
  private final MSTRandomTeleport plugin;
  
  private final RtpManager rtpManager;
  
  public RtpListener(MSTRandomTeleport plugin) {
    this.plugin = plugin;
    this.rtpManager = plugin.getRtpManager();
  }
  
  @EventHandler(ignoreCancelled = true)
  public void onMove(PlayerMoveEvent e) {
    if (e.getFrom().getBlockX() == e.getTo().getBlockX() && 
        e.getFrom().getBlockY() == e.getTo().getBlockY() && 
        e.getFrom().getBlockZ() == e.getTo().getBlockZ())
      return; 
    Player player = e.getPlayer();
    Specifications specifications = this.rtpManager.getSpecifications();
    Map<String, List<World>> voidChannels = specifications.voidChannels();
    if (!voidChannels.isEmpty() && e.getFrom().getBlockY() > e.getTo().getBlockY())
      for (Map.Entry<String, List<World>> entry : voidChannels.entrySet()) {
        String channelId = entry.getKey();
        Object2IntMap<String> voidLevels = specifications.voidLevels();
        if (e.getTo().getBlockY() > (
          voidLevels.isEmpty() ? 
          VersionUtils.VOID_LEVEL : 
          voidLevels.getOrDefault(channelId, VersionUtils.VOID_LEVEL)))
          continue; 
        List<World> worlds = entry.getValue();
        if (!worlds.contains(player.getWorld()))
          continue; 
        if (!player.hasPermission("rtp.channel." + channelId))
          continue; 
        Channel channel = this.rtpManager.getChannelById(channelId);
        if (channel == null)
          continue;
        processTeleport(player, channel, true);
        return;
      }  
    String playerName = player.getName();
    if (this.rtpManager.hasActiveTasks(playerName)) {
      Channel activeChannel = getActiveChannel(playerName);
      if (activeChannel != null && activeChannel.settings().restrictions().restrictMove()) {
        Utils.sendMessage(activeChannel.messages().movedOnTeleport(), player);
        cancelTeleportation(playerName);
      } 
    } 
  }
  
  @EventHandler(ignoreCancelled = true)
  public void onTeleport(PlayerTeleportEvent e) {
    if (e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN)
      return; 
    Player player = e.getPlayer();
    String playerName = player.getName();
    if (this.rtpManager.hasActiveTasks(playerName)) {
      Channel activeChannel = getActiveChannel(playerName);
      if (activeChannel != null && activeChannel.settings().restrictions().restrictTeleport()) {
        Utils.sendMessage(activeChannel.messages().teleportedOnTeleport(), player);
        cancelTeleportation(playerName);
      } 
    } 
  }
  
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    Player player = e.getPlayer();
    if (this.rtpManager.getProxyCalls() != null && !this.rtpManager.getProxyCalls().isEmpty()) {
      String data = this.rtpManager.getProxyCalls().get(player.getName());
      if (data == null)
        return; 
      int separatorIndex = data.indexOf(';');
      if (separatorIndex == -1 || separatorIndex >= data.length() - 1)
        return;
      Channel channel = this.rtpManager.getChannelById(data.substring(0, separatorIndex));
      if (channel == null)
        return;
      World world = Bukkit.getWorld(data.substring(separatorIndex + 1));
      if (world == null)
        return;
      this.rtpManager.preTeleport(player, channel, world, false);
      this.rtpManager.getProxyCalls().remove(player.getName());
      return;
    } 
    if (player.hasPlayedBefore())
      return; 
    Set<String> joinChannels = this.rtpManager.getSpecifications().joinChannels();
    if (joinChannels.isEmpty())
      return; 
    for (String channelId : joinChannels) {
      if (!player.hasPermission("rtp.channel." + channelId))
        continue; 
      Channel channel = this.rtpManager.getChannelById(channelId);
      if (channel == null)
        continue;
      processTeleport(player, channel, false);
      return;
    } 
  }
  
  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    Map<String, List<World>> respawnChannels = this.rtpManager.getSpecifications().respawnChannels();
    if (respawnChannels.isEmpty())
      return; 
    Player player = e.getPlayer();
    for (Map.Entry<String, List<World>> entry : respawnChannels.entrySet()) {
      List<World> worlds = entry.getValue();
      if (!worlds.contains(player.getWorld()))
        continue; 
      String channelId = entry.getKey();
      if (!player.hasPermission("rtp.channel." + channelId))
        continue; 
      Channel channel = this.rtpManager.getChannelById(channelId);
      if (channel == null)
        continue;
      processTeleport(player, channel, false);
      return;
    } 
  }
  
  private void processTeleport(Player player, Channel channel, boolean force) {
    if (!channel.activeWorlds().contains(player.getWorld())) {
      if (channel.teleportToFirstAllowedWorld())
        this.rtpManager.preTeleport(player, channel, channel.activeWorlds().get(0), force); 
      return;
    } 
    this.rtpManager.preTeleport(player, channel, player.getWorld(), force);
  }
  
  @EventHandler(ignoreCancelled = true)
  public void onDamage(EntityDamageEvent e) {
    Player player;
    Entity entity = e.getEntity();
    if (entity instanceof Player) {
      player = (Player)entity;
    } else {
      return;
    } 
    String playerName = player.getName();
    if (this.rtpManager.hasActiveTasks(playerName)) {
      Channel activeChannel = getActiveChannel(playerName);
      if (activeChannel != null && activeChannel.settings().restrictions().restrictDamage() && !activeChannel.settings().restrictions().damageCheckOnlyPlayers()) {
        Utils.sendMessage(activeChannel.messages().damagedOnTeleport(), player);
        cancelTeleportation(playerName);
      } 
    } 
  }
  
  @EventHandler(ignoreCancelled = true)
  public void onDamage(EntityDamageByEntityEvent e) {
    Entity damagerEntity = e.getDamager();
    Entity damagedEntity = e.getEntity();
    if (damagerEntity instanceof Player) {
      Player damager = (Player)damagerEntity;
      handleDamagerPlayer(damager, damagedEntity);
    } 
    if (damagedEntity instanceof Player) {
      Player damaged = (Player)damagedEntity;
      handleDamagedPlayer(damagerEntity, damaged);
    } 
  }
  
  private void handleDamagerPlayer(Player damager, Entity damagedEntity) {
    String damagerName = damager.getName();
    if (this.rtpManager.hasActiveTasks(damagerName)) {
      Channel activeChannel = getActiveChannel(damagerName);
      if (activeChannel == null) return;
      Restrictions restrictions = activeChannel.settings().restrictions();
      if (restrictions.restrictDamageOthers()) {
        if (restrictions.damageCheckOnlyPlayers() && !(damagedEntity instanceof Player))
          return; 
        damager.sendMessage(activeChannel.messages().damagedOtherOnTeleport());
        cancelTeleportation(damagerName);
      } 
    } 
  }
  
  private void handleDamagedPlayer(Entity damagerEntity, Player damaged) {
    String damagedName = damaged.getName();
    if (this.rtpManager.hasActiveTasks(damagedName)) {
      Channel activeChannel = getActiveChannel(damagedName);
      if (activeChannel == null) return;
      Restrictions restrictions = activeChannel.settings().restrictions();
      if (restrictions.restrictDamage()) {
        Player damager = getDamager(damagerEntity);
        if (damager == null && restrictions.damageCheckOnlyPlayers())
          return; 
        damaged.sendMessage(activeChannel.messages().damagedOnTeleport());
        cancelTeleportation(damagedName);
      } 
    } 
  }
  
  private Channel getActiveChannel(String playerName) {
    RtpTask task = this.rtpManager.getPerPlayerActiveRtpTask().get(playerName);
    if (task == null) {
      this.rtpManager.printDebug("getActiveChannel: task is null for player " + playerName + " (probably in teleportingNow but task already removed)");
      return null;
    }
    return task.getActiveChannel();
  }
  
  private Player getDamager(Entity damagerEntity) {
    if (damagerEntity instanceof Player) {
      Player player = (Player)damagerEntity;
      return player;
    } 
    if (damagerEntity instanceof Projectile) {
      Projectile projectile = (Projectile)damagerEntity;
      ProjectileSource source = projectile.getShooter();
      if (source instanceof Player) {
        Player player = (Player)source;
        return player;
      } 
    } 
    if (damagerEntity instanceof AreaEffectCloud) {
      AreaEffectCloud areaEffectCloud = (AreaEffectCloud)damagerEntity;
      ProjectileSource source = areaEffectCloud.getSource();
      if (source instanceof Player) {
        Player player = (Player)source;
        return player;
      } 
    } 
    if (damagerEntity instanceof TNTPrimed) {
      TNTPrimed tntPrimed = (TNTPrimed)damagerEntity;
      Entity source = tntPrimed.getSource();
      if (source instanceof Player) {
        Player player = (Player)source;
        return player;
      } 
    } 
    return null;
  }
  
  @EventHandler(ignoreCancelled = true)
  public void onDeath(EntityDeathEvent e) {
    Player player;
    LivingEntity livingEntity = e.getEntity();
    if (livingEntity instanceof Player) {
      player = (Player)livingEntity;
    } else {
      return;
    } 
    handlePlayerLeave(player);
  }
  
  @EventHandler
  public void onLeave(PlayerQuitEvent e) {
    Player player = e.getPlayer();
    handlePlayerLeave(player);
  }
  
  @EventHandler(ignoreCancelled = true)
  public void onKick(PlayerKickEvent e) {
    Player player = e.getPlayer();
    handlePlayerLeave(player);
  }
  
  private void handlePlayerLeave(Player player) {
    Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
          String playerName = player.getName();
          if (this.rtpManager.hasActiveTasks(playerName))
            cancelTeleportation(playerName); 
        });
  }
  
  private void cancelTeleportation(String playerName) {
    this.rtpManager.printDebug("Teleportation for player " + playerName + " was cancelled because of restrictions");
    RtpTask task = this.rtpManager.getPerPlayerActiveRtpTask().get(playerName);
    if (task != null) {
      task.cancel();
    } else {
      this.rtpManager.printDebug("Task is null for player " + playerName + ", cleaning up state");
      this.rtpManager.cleanupPlayerState(playerName);
    }
    this.rtpManager.getLocationGenerator().getIterationsPerPlayer().removeInt(playerName);
  }
}
