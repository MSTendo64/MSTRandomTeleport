package com.mstendo.rtp.channels.settings;

import it.unimi.dsi.fastutil.objects.Object2IntSortedMap;
import java.util.Objects;
import org.bukkit.entity.Player;
import com.mstendo.rtp.utils.TimedExpiringMap;

public final class Cooldown {
  private final int defaultCooldown;
  private final TimedExpiringMap<String, Long> playerCooldowns;
  private final Object2IntSortedMap<String> groupCooldowns;
  private final int defaultPreTeleportCooldown;
  private final Object2IntSortedMap<String> preTeleportCooldowns;
  
  public Cooldown(int defaultCooldown, TimedExpiringMap<String, Long> playerCooldowns, Object2IntSortedMap<String> groupCooldowns, int defaultPreTeleportCooldown, Object2IntSortedMap<String> preTeleportCooldowns) {
    this.defaultCooldown = defaultCooldown;
    this.playerCooldowns = playerCooldowns;
    this.groupCooldowns = groupCooldowns;
    this.defaultPreTeleportCooldown = defaultPreTeleportCooldown;
    this.preTeleportCooldowns = preTeleportCooldowns;
  }
  
  public int defaultCooldown() {
    return defaultCooldown;
  }
  
  public TimedExpiringMap<String, Long> playerCooldowns() {
    return playerCooldowns;
  }
  
  public Object2IntSortedMap<String> groupCooldowns() {
    return groupCooldowns;
  }
  
  public int defaultPreTeleportCooldown() {
    return defaultPreTeleportCooldown;
  }
  
  public Object2IntSortedMap<String> preTeleportCooldowns() {
    return preTeleportCooldowns;
  }
  
  public boolean hasCooldown(Player player) {
    return (playerCooldowns != null && !playerCooldowns.isEmpty() && playerCooldowns.containsKey(player.getName()));
  }
  
  public void setCooldown(String name, long cooldownTime) {
    playerCooldowns.put(name, Long.valueOf(System.currentTimeMillis()), cooldownTime);
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Cooldown cooldown = (Cooldown) o;
    return defaultCooldown == cooldown.defaultCooldown &&
           defaultPreTeleportCooldown == cooldown.defaultPreTeleportCooldown &&
           Objects.equals(playerCooldowns, cooldown.playerCooldowns) &&
           Objects.equals(groupCooldowns, cooldown.groupCooldowns) &&
           Objects.equals(preTeleportCooldowns, cooldown.preTeleportCooldowns);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(defaultCooldown, playerCooldowns, groupCooldowns, defaultPreTeleportCooldown, preTeleportCooldowns);
  }
  
  @Override
  public String toString() {
    return "Cooldown[defaultCooldown=" + defaultCooldown + ", playerCooldowns=" + playerCooldowns + ", groupCooldowns=" + groupCooldowns + ", defaultPreTeleportCooldown=" + defaultPreTeleportCooldown + ", preTeleportCooldowns=" + preTeleportCooldowns + "]";
  }
}
