package com.mstendo.rtp.channels.settings;

import java.util.Objects;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Biome;

public final class Avoidance {
  private final boolean avoidBlocksBlacklist;
  private final Set<Material> avoidBlocks;
  private final boolean avoidBiomesBlacklist;
  private final Set<Biome> avoidBiomes;
  private final boolean avoidRegions;
  private final boolean avoidTowns;
  
  public Avoidance(boolean avoidBlocksBlacklist, Set<Material> avoidBlocks, boolean avoidBiomesBlacklist, Set<Biome> avoidBiomes, boolean avoidRegions, boolean avoidTowns) {
    this.avoidBlocksBlacklist = avoidBlocksBlacklist;
    this.avoidBlocks = avoidBlocks;
    this.avoidBiomesBlacklist = avoidBiomesBlacklist;
    this.avoidBiomes = avoidBiomes;
    this.avoidRegions = avoidRegions;
    this.avoidTowns = avoidTowns;
  }
  
  public boolean avoidBlocksBlacklist() {
    return avoidBlocksBlacklist;
  }
  
  public Set<Material> avoidBlocks() {
    return avoidBlocks;
  }
  
  public boolean avoidBiomesBlacklist() {
    return avoidBiomesBlacklist;
  }
  
  public Set<Biome> avoidBiomes() {
    return avoidBiomes;
  }
  
  public boolean avoidRegions() {
    return avoidRegions;
  }
  
  public boolean avoidTowns() {
    return avoidTowns;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Avoidance avoidance = (Avoidance) o;
    return avoidBlocksBlacklist == avoidance.avoidBlocksBlacklist &&
           avoidBiomesBlacklist == avoidance.avoidBiomesBlacklist &&
           avoidRegions == avoidance.avoidRegions &&
           avoidTowns == avoidance.avoidTowns &&
           Objects.equals(avoidBlocks, avoidance.avoidBlocks) &&
           Objects.equals(avoidBiomes, avoidance.avoidBiomes);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(avoidBlocksBlacklist, avoidBlocks, avoidBiomesBlacklist, avoidBiomes, avoidRegions, avoidTowns);
  }
  
  @Override
  public String toString() {
    return "Avoidance[avoidBlocksBlacklist=" + avoidBlocksBlacklist + ", avoidBlocks=" + avoidBlocks + ", avoidBiomesBlacklist=" + avoidBiomesBlacklist + ", avoidBiomes=" + avoidBiomes + ", avoidRegions=" + avoidRegions + ", avoidTowns=" + avoidTowns + "]";
  }
}
