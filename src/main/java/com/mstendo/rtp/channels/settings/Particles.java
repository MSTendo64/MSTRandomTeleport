package com.mstendo.rtp.channels.settings;

import java.util.List;
import java.util.Objects;

public final class Particles {
  private final boolean preTeleportEnabled;
  private final boolean preTeleportSendOnlyToPlayer;
  private final List<ParticleData> preTeleportParticles;
  private final int preTeleportDots;
  private final double preTeleportRadius;
  private final double preTeleportParticleSpeed;
  private final double preTeleportSpeed;
  private final boolean preTeleportInvert;
  private final boolean preTeleportJumping;
  private final boolean preTeleportMoveNear;
  private final boolean afterTeleportEnabled;
  private final boolean afterTeleportSendOnlyToPlayer;
  private final ParticleData afterTeleportParticle;
  private final int afterTeleportCount;
  private final double afterTeleportRadius;
  private final double afterTeleportParticleSpeed;
  
  public Particles(boolean preTeleportEnabled, boolean preTeleportSendOnlyToPlayer, List<ParticleData> preTeleportParticles, int preTeleportDots, double preTeleportRadius, double preTeleportParticleSpeed, double preTeleportSpeed, boolean preTeleportInvert, boolean preTeleportJumping, boolean preTeleportMoveNear, boolean afterTeleportEnabled, boolean afterTeleportSendOnlyToPlayer, ParticleData afterTeleportParticle, int afterTeleportCount, double afterTeleportRadius, double afterTeleportParticleSpeed) {
    this.preTeleportEnabled = preTeleportEnabled;
    this.preTeleportSendOnlyToPlayer = preTeleportSendOnlyToPlayer;
    this.preTeleportParticles = preTeleportParticles;
    this.preTeleportDots = preTeleportDots;
    this.preTeleportRadius = preTeleportRadius;
    this.preTeleportParticleSpeed = preTeleportParticleSpeed;
    this.preTeleportSpeed = preTeleportSpeed;
    this.preTeleportInvert = preTeleportInvert;
    this.preTeleportJumping = preTeleportJumping;
    this.preTeleportMoveNear = preTeleportMoveNear;
    this.afterTeleportEnabled = afterTeleportEnabled;
    this.afterTeleportSendOnlyToPlayer = afterTeleportSendOnlyToPlayer;
    this.afterTeleportParticle = afterTeleportParticle;
    this.afterTeleportCount = afterTeleportCount;
    this.afterTeleportRadius = afterTeleportRadius;
    this.afterTeleportParticleSpeed = afterTeleportParticleSpeed;
  }
  
  public boolean preTeleportEnabled() {
    return preTeleportEnabled;
  }
  
  public boolean preTeleportSendOnlyToPlayer() {
    return preTeleportSendOnlyToPlayer;
  }
  
  public List<ParticleData> preTeleportParticles() {
    return preTeleportParticles;
  }
  
  public int preTeleportDots() {
    return preTeleportDots;
  }
  
  public double preTeleportRadius() {
    return preTeleportRadius;
  }
  
  public double preTeleportParticleSpeed() {
    return preTeleportParticleSpeed;
  }
  
  public double preTeleportSpeed() {
    return preTeleportSpeed;
  }
  
  public boolean preTeleportInvert() {
    return preTeleportInvert;
  }
  
  public boolean preTeleportJumping() {
    return preTeleportJumping;
  }
  
  public boolean preTeleportMoveNear() {
    return preTeleportMoveNear;
  }
  
  public boolean afterTeleportEnabled() {
    return afterTeleportEnabled;
  }
  
  public boolean afterTeleportSendOnlyToPlayer() {
    return afterTeleportSendOnlyToPlayer;
  }
  
  public ParticleData afterTeleportParticle() {
    return afterTeleportParticle;
  }
  
  public int afterTeleportCount() {
    return afterTeleportCount;
  }
  
  public double afterTeleportRadius() {
    return afterTeleportRadius;
  }
  
  public double afterTeleportParticleSpeed() {
    return afterTeleportParticleSpeed;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Particles particles = (Particles) o;
    return preTeleportEnabled == particles.preTeleportEnabled &&
           preTeleportSendOnlyToPlayer == particles.preTeleportSendOnlyToPlayer &&
           preTeleportDots == particles.preTeleportDots &&
           Double.compare(particles.preTeleportRadius, preTeleportRadius) == 0 &&
           Double.compare(particles.preTeleportParticleSpeed, preTeleportParticleSpeed) == 0 &&
           Double.compare(particles.preTeleportSpeed, preTeleportSpeed) == 0 &&
           preTeleportInvert == particles.preTeleportInvert &&
           preTeleportJumping == particles.preTeleportJumping &&
           preTeleportMoveNear == particles.preTeleportMoveNear &&
           afterTeleportEnabled == particles.afterTeleportEnabled &&
           afterTeleportSendOnlyToPlayer == particles.afterTeleportSendOnlyToPlayer &&
           afterTeleportCount == particles.afterTeleportCount &&
           Double.compare(particles.afterTeleportRadius, afterTeleportRadius) == 0 &&
           Double.compare(particles.afterTeleportParticleSpeed, afterTeleportParticleSpeed) == 0 &&
           Objects.equals(preTeleportParticles, particles.preTeleportParticles) &&
           Objects.equals(afterTeleportParticle, particles.afterTeleportParticle);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(preTeleportEnabled, preTeleportSendOnlyToPlayer, preTeleportParticles, preTeleportDots, preTeleportRadius, preTeleportParticleSpeed, preTeleportSpeed, preTeleportInvert, preTeleportJumping, preTeleportMoveNear, afterTeleportEnabled, afterTeleportSendOnlyToPlayer, afterTeleportParticle, afterTeleportCount, afterTeleportRadius, afterTeleportParticleSpeed);
  }
  
  @Override
  public String toString() {
    return "Particles[preTeleportEnabled=" + preTeleportEnabled + ", preTeleportSendOnlyToPlayer=" + preTeleportSendOnlyToPlayer + ", preTeleportParticles=" + preTeleportParticles + ", preTeleportDots=" + preTeleportDots + ", preTeleportRadius=" + preTeleportRadius + ", preTeleportParticleSpeed=" + preTeleportParticleSpeed + ", preTeleportSpeed=" + preTeleportSpeed + ", preTeleportInvert=" + preTeleportInvert + ", preTeleportJumping=" + preTeleportJumping + ", preTeleportMoveNear=" + preTeleportMoveNear + ", afterTeleportEnabled=" + afterTeleportEnabled + ", afterTeleportSendOnlyToPlayer=" + afterTeleportSendOnlyToPlayer + ", afterTeleportParticle=" + afterTeleportParticle + ", afterTeleportCount=" + afterTeleportCount + ", afterTeleportRadius=" + afterTeleportRadius + ", afterTeleportParticleSpeed=" + afterTeleportParticleSpeed + "]";
  }
}
