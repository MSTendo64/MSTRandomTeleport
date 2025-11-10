package com.mstendo.rtp.utils;

import lombok.Generated;
import org.bukkit.Bukkit;

public final class VersionUtils {
  @Generated
  private VersionUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  public static final int SUB_VERSION = Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]);
  
  public static final int VOID_LEVEL = (SUB_VERSION >= 18) ? -64 : 0;
}
