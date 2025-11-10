package com.mstendo.rtp.channels.settings;

import java.util.Objects;
import org.bukkit.Particle;

public final class ParticleData {
  private final Particle particle;
  private final Particle.DustOptions dustOptions;
  
  public ParticleData(Particle particle, Particle.DustOptions dustOptions) {
    this.particle = particle;
    this.dustOptions = dustOptions;
  }
  
  public Particle particle() {
    return particle;
  }
  
  public Particle.DustOptions dustOptions() {
    return dustOptions;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ParticleData that = (ParticleData) o;
    return particle == that.particle &&
           Objects.equals(dustOptions, that.dustOptions);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(particle, dustOptions);
  }
  
  @Override
  public String toString() {
    return "ParticleData[particle=" + particle + ", dustOptions=" + dustOptions + "]";
  }
}
