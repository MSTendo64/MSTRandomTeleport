package com.mstendo.rtp.utils.regions;

import lombok.Generated;
import org.bukkit.Location;

public final class TownyUtils {
  @Generated
  private TownyUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  private static Object api;
  
  static {
    try {
      Class<?> townyAPIClass = Class.forName("com.palmergames.bukkit.towny.TownyAPI");
      api = townyAPIClass.getMethod("getInstance").invoke(null);
    } catch (Exception e) {
      api = null;
    }
  }
  
  public static boolean isAvailable() {
    return api != null;
  }
  
  public static Object getTownByLocation(Location loc) {
    if (!isAvailable()) return null;
    try {
      return api.getClass().getMethod("getTown", Location.class).invoke(api, loc);
    } catch (Exception e) {
      return null;
    }
  }
}
