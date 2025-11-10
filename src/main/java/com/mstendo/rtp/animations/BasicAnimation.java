package com.mstendo.rtp.animations;

import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import com.mstendo.rtp.channels.settings.Particles;
import com.mstendo.rtp.channels.settings.ParticleData;

public class BasicAnimation extends Animation {
  private double angle;
  
  private double yOffset;
  
  private int tickCounter;
  
  private final double initialRadius;
  
  private final double radiusStep;
  
  private final double rotationSpeed;
  
  private final double yStep;
  
  private final double verticalRotationSpeed;
  
  private final List<Player> receivers;
  
  private Iterator<ParticleData> preTeleportParticle;
  
  public BasicAnimation(Player player, int duration, Particles particles) {
    super(player, duration, particles);
    this.yOffset = this.particles.preTeleportInvert() ? 0.0D : 2.0D;
    this.initialRadius = this.particles.preTeleportRadius();
    this.radiusStep = this.particles.preTeleportMoveNear() ? (this.initialRadius / this.duration) : 0.0D;
    this.rotationSpeed = 6.283185307179586D * this.particles.preTeleportSpeed() / this.duration * ((this.particles.preTeleportInvert() && this.particles.preTeleportJumping()) ? 2.0 : 1.0);
    this.yStep = this.particles.preTeleportInvert() ? (2.0D / this.duration) : (-2.0D / this.duration);
    this.verticalRotationSpeed = 12.566370614359172D / this.duration;
    this.receivers = this.particles.preTeleportSendOnlyToPlayer() ? java.util.Collections.<Player>singletonList(this.player) : null;
    this.preTeleportParticle = this.particles.preTeleportParticles().iterator();
  }
  
  public void run() {
    this.tickCounter++;
    if (this.tickCounter >= this.duration) {
      cancel();
      return;
    } 
    if (!this.preTeleportParticle.hasNext())
      this.preTeleportParticle = this.particles.preTeleportParticles().iterator(); 
    ParticleData preTeleportParticleData = this.preTeleportParticle.next();
    Location location = this.player.getLocation();
    World world = location.getWorld();
    double yRingOffset = Math.sin(Math.PI * this.tickCounter / this.duration) * 2.0D;
    double currentRadius = this.particles.preTeleportMoveNear() ? (this.initialRadius - this.radiusStep * this.tickCounter) : this.initialRadius;
    for (int i = 0; i < this.particles.preTeleportDots(); i++) {
      double x, y, z, phaseOffset = i * 6.283185307179586D / this.particles.preTeleportDots();
      if (this.particles.preTeleportJumping()) {
        y = yRingOffset;
        x = Math.cos(this.angle + phaseOffset) * currentRadius;
        z = Math.sin(this.angle + phaseOffset) * currentRadius;
        double cosRotation = Math.cos(this.verticalRotationSpeed * this.tickCounter);
        double sinRotation = Math.sin(this.verticalRotationSpeed * this.tickCounter);
        double rotatedX = x * cosRotation - z * sinRotation;
        double rotatedZ = x * sinRotation + z * cosRotation;
        x = rotatedX;
        z = rotatedZ;
      } else {
        x = Math.cos(this.angle + phaseOffset) * currentRadius;
        y = this.yOffset;
        z = Math.sin(this.angle + phaseOffset) * currentRadius;
      } 
      location.add(x, y, z);
      if (this.receivers != null && !this.receivers.isEmpty()) {
        for (Player receiver : this.receivers) {
          if (preTeleportParticleData.dustOptions() != null) {
            receiver.spawnParticle(preTeleportParticleData.particle(), location, 1, 0.0D, 0.0D, 0.0D, this.particles.preTeleportParticleSpeed(), preTeleportParticleData.dustOptions());
          } else {
            receiver.spawnParticle(preTeleportParticleData.particle(), location, 1, 0.0D, 0.0D, 0.0D, this.particles.preTeleportParticleSpeed());
          }
        }
      } else {
        if (preTeleportParticleData.dustOptions() != null) {
          world.spawnParticle(preTeleportParticleData.particle(), location, 1, 0.0D, 0.0D, 0.0D, this.particles.preTeleportParticleSpeed(), preTeleportParticleData.dustOptions());
        } else {
          world.spawnParticle(preTeleportParticleData.particle(), location, 1, 0.0D, 0.0D, 0.0D, this.particles.preTeleportParticleSpeed());
        }
      }
      location.subtract(x, y, z);
    } 
    this.angle += this.particles.preTeleportInvert() ? -this.rotationSpeed : this.rotationSpeed;
    if (!this.particles.preTeleportJumping())
      this.yOffset += this.yStep; 
  }
}
