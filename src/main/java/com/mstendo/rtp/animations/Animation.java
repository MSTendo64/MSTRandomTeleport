package com.mstendo.rtp.animations;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.mstendo.rtp.channels.settings.Particles;

public abstract class Animation extends BukkitRunnable {
  protected Player player;
  
  protected int duration;
  
  protected Particles particles;
  
  public Animation(Player player, int duration, Particles particles) {
    this.player = player;
    this.duration = duration;
    this.particles = particles;
  }
}
