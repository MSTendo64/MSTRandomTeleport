package com.mstendo.rtp;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import com.mstendo.rtp.channels.Settings;
import com.mstendo.rtp.channels.settings.LocationGenOptions;
import com.mstendo.rtp.utils.regions.WGUtils;

public class WGLocationGenerator {
  private final RtpManager rtpManager;
  
  private final LocationGenerator locationGenerator;
  
  @Generated
  public WGLocationGenerator(RtpManager rtpManager, LocationGenerator locationGenerator) {
    this.rtpManager = rtpManager;
    this.locationGenerator = locationGenerator;
  }
  
  public Location generateRandomLocationNearRandomRegion(Player player, Settings settings, World world) {
    try {
      LocationGenOptions locationGenOptions = settings.locationGenOptions();
      if (this.locationGenerator.hasReachedMaxIterations(player.getName(), locationGenOptions))
        return null; 
      RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
      if (regionManager == null || regionManager.getRegions().isEmpty()) {
        this.rtpManager.printDebug("RegionManager is null or empty for world " + world.getName());
        return null; 
      }
      int minX = locationGenOptions.minX();
      int maxX = locationGenOptions.maxX();
      int minZ = locationGenOptions.minZ();
      int maxZ = locationGenOptions.maxZ();
      BukkitPlayer bukkitPlayer = new BukkitPlayer(WorldGuardPlugin.inst(), player);
      List<ProtectedRegion> regionsInRange = new ArrayList<>();
      for (ProtectedRegion region : regionManager.getRegions().values()) {
        try {
          if (region.getType() == RegionType.GLOBAL || region.isMember((LocalPlayer)bukkitPlayer))
            continue; 
          boolean flag = Boolean.TRUE.equals(region.getFlag((Flag)WGUtils.RTP_IGNORE_FLAG));
          if (flag)
            continue; 
          BlockVector3 minPoint = region.getMinimumPoint();
          BlockVector3 maxPoint = region.getMaximumPoint();
          if (minPoint.getX() >= minX && maxPoint.getX() <= maxX && minPoint.getZ() >= minZ && maxPoint.getZ() <= maxZ)
            regionsInRange.add(region);
        } catch (Exception e) {
          this.rtpManager.printDebug("Error processing region " + region.getId() + ": " + e.getMessage());
          // Continue with next region
        }
      } 
      if (regionsInRange.isEmpty()) {
        this.rtpManager.printDebug("No regions found to generate location near region for player " + player.getName());
        return null;
      } 
      ProtectedRegion randomRegion = regionsInRange.get(this.locationGenerator.getRandom().nextInt(regionsInRange.size()));
      int centerX = (randomRegion.getMinimumPoint().getX() + randomRegion.getMaximumPoint().getX()) / 2;
      int centerZ = (randomRegion.getMinimumPoint().getZ() + randomRegion.getMaximumPoint().getZ()) / 2;
      LocationGenOptions.Shape shape = locationGenOptions.shape();
      Location location = this.locationGenerator.generateRandomLocationNearPoint(shape, player, centerX, centerZ, settings, world);
      if (location == null) {
        this.locationGenerator.getIterationsPerPlayer().addTo(player.getName(), 1);
        return generateRandomLocationNearRandomRegion(player, settings, world);
      } 
      this.rtpManager.printDebug("Location for player '" + player.getName() + "' found in " + this.locationGenerator.getIterationsPerPlayer().getInt(player.getName()) + " iterations");
      this.locationGenerator.getIterationsPerPlayer().removeInt(player.getName());
      return location;
    } catch (Exception e) {
      this.rtpManager.printDebug("Error in generateRandomLocationNearRandomRegion for player " + player.getName() + ": " + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }
}
