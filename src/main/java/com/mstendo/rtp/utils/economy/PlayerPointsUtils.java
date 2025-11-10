package com.mstendo.rtp.utils.economy;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlayerPointsUtils {
  private static Object api;
  
  static {
    try {
      Class<?> playerPointsClass = Class.forName("org.black_ixx.playerpoints.PlayerPoints");
      Object playerPointsInstance = playerPointsClass.getMethod("getInstance").invoke(null);
      api = playerPointsClass.getMethod("getAPI").invoke(playerPointsInstance);
    } catch (Exception e) {
      api = null;
    }
  }
  
  public static boolean isAvailable() {
    return api != null;
  }
  
  public static void withdraw(Player player, int amount) {
    if (!isAvailable()) return;
    try {
      UUID uuid = player.getUniqueId();
      api.getClass().getMethod("take", UUID.class, int.class).invoke(api, uuid, amount);
    } catch (Exception e) {
      // PlayerPoints not available
    }
  }
  
  public static void deposit(Player player, int amount) {
    if (!isAvailable()) return;
    try {
      UUID uuid = player.getUniqueId();
      api.getClass().getMethod("give", UUID.class, int.class).invoke(api, uuid, amount);
    } catch (Exception e) {
      // PlayerPoints not available
    }
  }
  
  public static int getBalance(Player player) {
    if (!isAvailable()) return 0;
    try {
      UUID uuid = player.getUniqueId();
      Object result = api.getClass().getMethod("look", UUID.class).invoke(api, uuid);
      return result != null ? (Integer) result : 0;
    } catch (Exception e) {
      return 0;
    }
  }
  
  public static boolean hasEnough(Player player, int amount) {
    return getBalance(player) >= amount;
  }
  
  public static void takePoints(Player player, int amount) {
    withdraw(player, amount);
  }
  
  public static void givePoints(Player player, int amount) {
    deposit(player, amount);
  }
}
