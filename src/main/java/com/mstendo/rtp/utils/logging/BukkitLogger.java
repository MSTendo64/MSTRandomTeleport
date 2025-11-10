package com.mstendo.rtp.utils.logging;

import com.mstendo.rtp.MSTRandomTeleport;

public class BukkitLogger implements Logger {
  private final java.util.logging.Logger logger;
  
  public BukkitLogger(MSTRandomTeleport plugin) {
    this.logger = plugin.getLogger();
  }
  
  public void info(String msg) {
    this.logger.info(msg);
  }
  
  public void warn(String msg) {
    this.logger.warning(msg);
  }
}
