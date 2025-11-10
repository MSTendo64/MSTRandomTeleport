package com.mstendo.rtp;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.util.XoRoShiRo128PlusRandom;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import com.mstendo.rtp.channels.Settings;
import com.mstendo.rtp.channels.settings.Avoidance;
import com.mstendo.rtp.channels.settings.LocationGenOptions;
import com.mstendo.rtp.utils.Utils;
import com.mstendo.rtp.utils.regions.TownyUtils;
import com.mstendo.rtp.utils.regions.WGUtils;

public class LocationGenerator {
  private final RtpManager rtpManager;
  
  private final XoRoShiRo128PlusRandom random = new XoRoShiRo128PlusRandom();
  
  @Generated
  public XoRoShiRo128PlusRandom getRandom() {
    return this.random;
  }
  
  private final Object2IntOpenHashMap<String> iterationsPerPlayer = new Object2IntOpenHashMap<String>();
  
  private final WGLocationGenerator wgLocationGenerator;
  
  @Generated
  public Object2IntOpenHashMap<String> getIterationsPerPlayer() {
    return this.iterationsPerPlayer;
  }
  
  @Generated
  public WGLocationGenerator getWgLocationGenerator() {
    return this.wgLocationGenerator;
  }
  
  public LocationGenerator(MSTRandomTeleport plugin, RtpManager rtpManager) {
    this.rtpManager = rtpManager;
    this.wgLocationGenerator = plugin.hasWorldGuard() ? new WGLocationGenerator(rtpManager, this) : null;
  }
  
  public Location generateRandomLocation(Player player, Settings settings, World world) {
    LocationGenOptions locationGenOptions = settings.locationGenOptions();
    if (hasReachedMaxIterations(player.getName(), locationGenOptions))
      return null; 
    LocationGenOptions.Shape shape = locationGenOptions.shape();
    Location location;
    switch (shape) {
      case SQUARE:
        location = generateRandomSquareLocation(player, settings, world);
        break;
      case RECTANGULAR:
      case RADIAL:
        location = generateRandomRoundLocation(player, settings, world);
        break;
      default:
        location = generateRandomRoundLocation(player, settings, world);
        break;
    }
    if (location == null) {
      this.iterationsPerPlayer.addTo(player.getName(), 1);
      return generateRandomLocation(player, settings, world);
    } 
    this.rtpManager.printDebug("Location for player '" + player.getName() + "' found in " + this.iterationsPerPlayer.getInt(player.getName()) + " iterations");
    this.iterationsPerPlayer.removeInt(player.getName());
    return location;
  }
  
  public Location generateRandomLocationNearPlayer(Player player, Settings settings, World world) {
    LocationGenOptions locationGenOptions = settings.locationGenOptions();
    if (hasReachedMaxIterations(player.getName(), locationGenOptions))
      return null; 
    List<Player> nearbyPlayers = getNearbyPlayers(player, locationGenOptions, world);
    if (nearbyPlayers.isEmpty()) {
      this.rtpManager.printDebug("No players found to generate location near player");
      return null;
    } 
    Player targetPlayer = nearbyPlayers.get(this.random.nextInt(nearbyPlayers.size()));
    Location loc = targetPlayer.getLocation();
    int centerX = loc.getBlockX();
    int centerZ = loc.getBlockZ();
    LocationGenOptions.Shape shape = locationGenOptions.shape();
    Location location = generateRandomLocationNearPoint(shape, player, centerX, centerZ, settings, world);
    if (location == null) {
      this.iterationsPerPlayer.addTo(player.getName(), 1);
      return generateRandomLocationNearPlayer(player, settings, world);
    } 
    this.rtpManager.printDebug("Location for player '" + player.getName() + "' found in " + this.iterationsPerPlayer.getInt(player.getName()) + " iterations");
    this.iterationsPerPlayer.removeInt(player.getName());
    return location;
  }
  
  private List<Player> getNearbyPlayers(Player player, LocationGenOptions locationGenOptions, World world) {
    int minX = locationGenOptions.minX();
    int maxX = locationGenOptions.maxX();
    int minZ = locationGenOptions.minZ();
    int maxZ = locationGenOptions.maxZ();
    List<Player> nearbyPlayers = new ArrayList<>();
    List<Player> worldPlayers = world.getPlayers();
    this.rtpManager.printDebug("Players in world " + world.getName() + ": " + String.valueOf(worldPlayers.stream().map(Player::getName).collect(java.util.stream.Collectors.toList())));
    worldPlayers.remove(player);
    for (Player worldPlayer : worldPlayers) {
      if (worldPlayer.hasPermission("rtp.near.bypass") || isVanished(worldPlayer))
        continue; 
      Location loc = worldPlayer.getLocation();
      int px = loc.getBlockX();
      int pz = loc.getBlockZ();
      if (px >= minX && px <= maxX && pz >= minZ && pz <= maxZ)
        nearbyPlayers.add(worldPlayer); 
    } 
    return nearbyPlayers;
  }
  
  private boolean isVanished(Player player) {
    return (player.hasMetadata("vanished") && ((MetadataValue)player.getMetadata("vanished").get(0)).asBoolean());
  }
  
  public boolean hasReachedMaxIterations(String playerName, LocationGenOptions locationGenOptions) {
    int iterations = this.iterationsPerPlayer.getInt(playerName);
    this.rtpManager.printDebug("Iterations for player '" + playerName + "': " + iterations);
    if (iterations >= locationGenOptions.maxLocationAttempts()) {
      this.iterationsPerPlayer.removeInt(playerName);
      this.rtpManager.printDebug("Max iterations reached for player " + playerName);
      return true;
    } 
    return false;
  }
  
  public Location generateRandomSquareLocation(Player player, Settings settings, World world) {
    int centerX, centerZ;
    LocationGenOptions locationGenOptions = settings.locationGenOptions();
    int minX = locationGenOptions.minX();
    int maxX = locationGenOptions.maxX();
    int minZ = locationGenOptions.minZ();
    int maxZ = locationGenOptions.maxZ();
    int x = 0, z = 0;
    switch (locationGenOptions.genFormat()) {
      case RECTANGULAR:
        x = this.random.nextInt(maxX - minX + 1) + minX;
        z = this.random.nextInt(maxZ - minZ + 1) + minZ;
        break;
      case RADIAL:
        centerX = locationGenOptions.centerX();
        centerZ = locationGenOptions.centerZ();
        do {
          x = this.random.nextInt(maxX - minX + 1) + minX;
          z = this.random.nextInt(maxZ - minZ + 1) + minZ;
          x = this.random.nextBoolean() ? (centerX + x) : (centerX - x);
          z = this.random.nextBoolean() ? (centerZ + z) : (centerZ - z);
        } while (isInsideRadiusSquare(x, z, minX, minZ, maxX, maxZ, centerX, centerZ));
        break;
    } 
    int y = findSafeYPoint(world, x, z);
    if (y < 0)
      return null; 
    Location playerLocation = player.getLocation();
    Location location = new Location(world, x + 0.5D, y, z + 0.5D, playerLocation.getYaw(), playerLocation.getPitch());
    if (isLocationRestricted(location, settings.avoidance()))
      return null; 
    location.setY(y + 1.0D);
    return location;
  }
  
  public Location generateRandomRoundLocation(Player player, Settings settings, World world) {
    int centerX, centerZ, radiusX, radiusZ;
    double theta, r;
    LocationGenOptions locationGenOptions = settings.locationGenOptions();
    int minX = locationGenOptions.minX();
    int maxX = locationGenOptions.maxX();
    int minZ = locationGenOptions.minZ();
    int maxZ = locationGenOptions.maxZ();
    int x = 0, z = 0;
    switch (locationGenOptions.genFormat()) {
      case RECTANGULAR:
        centerX = (minX + maxX) / 2;
        centerZ = (minZ + maxZ) / 2;
        radiusX = (maxX - minX) / 2;
        radiusZ = (maxZ - minZ) / 2;
        theta = this.random.nextDouble() * 2.0D * Math.PI;
        r = Math.sqrt(this.random.nextDouble());
        x = (int)(centerX + r * radiusX * Math.cos(theta));
        z = (int)(centerZ + r * radiusZ * Math.sin(theta));
        break;
      case RADIAL:
        centerX = locationGenOptions.centerX();
        centerZ = locationGenOptions.centerZ();
        do {
          double d1 = this.random.nextDouble() * 2.0D * Math.PI;
          double rX = minX + (maxX - minX) * Math.sqrt(this.random.nextDouble());
          double rZ = minZ + (maxZ - minZ) * Math.sqrt(this.random.nextDouble());
          x = (int)(centerX + rX * Math.cos(d1));
          z = (int)(centerZ + rZ * Math.sin(d1));
        } while (isInsideRadiusCircle(x, z, minX, minZ, maxX, maxZ, centerX, centerZ));
        break;
    } 
    int y = findSafeYPoint(world, x, z);
    if (y < 0)
      return null; 
    Location playerLocation = player.getLocation();
    Location location = new Location(world, x + 0.5D, y, z + 0.5D, playerLocation.getYaw(), playerLocation.getPitch());
    if (isLocationRestricted(location, settings.avoidance()))
      return null; 
    location.setY(y + 1.0D);
    return location;
  }
  
  public Location generateRandomLocationNearPoint(LocationGenOptions.Shape shape, Player player, int centerX, int centerZ, Settings settings, World world) {
    switch (shape) {
      case SQUARE:
        return generateRandomSquareLocationNearPoint(player, centerX, centerZ, settings, world);
      case RECTANGULAR:
      case RADIAL:
        return generateRandomRoundLocationNearPoint(player, centerX, centerZ, settings, world);
      default:
        return generateRandomRoundLocationNearPoint(player, centerX, centerZ, settings, world);
    }
  }
  
  private Location generateRandomSquareLocationNearPoint(Player player, int centerX, int centerZ, Settings settings, World world) {
    int genCenterX, genCenterZ;
    LocationGenOptions locationGenOptions = settings.locationGenOptions();
    int minX = locationGenOptions.minX();
    int maxX = locationGenOptions.maxX();
    int minZ = locationGenOptions.minZ();
    int maxZ = locationGenOptions.maxZ();
    int radiusMin = locationGenOptions.nearRadiusMin();
    int radiusMax = locationGenOptions.nearRadiusMax();
    int x = 0, z = 0;
    switch (locationGenOptions.genFormat()) {
      case RECTANGULAR:
        do {
          x = centerX + this.random.nextInt(radiusMax * 2 + 1) - radiusMax;
          z = centerZ + this.random.nextInt(radiusMax * 2 + 1) - radiusMax;
        } while ((x < minX || x > maxX) && (z < minZ || z > maxZ));
        break;
      case RADIAL:
        genCenterX = locationGenOptions.centerX();
        genCenterZ = locationGenOptions.centerZ();
        do {
          double theta = this.random.nextDouble() * 2.0D * Math.PI;
          double r = radiusMin + (radiusMax - radiusMin) * Math.sqrt(this.random.nextDouble());
          x = (int)(centerX + r * Math.cos(theta));
          z = (int)(centerZ + r * Math.sin(theta));
        } while (isInsideRadiusSquare(x, z, minX, minZ, maxX, maxZ, genCenterX, genCenterZ));
        break;
    } 
    int y = findSafeYPoint(world, x, z);
    if (y < 0)
      return null; 
    Location playerLocation = player.getLocation();
    Location location = new Location(world, x + 0.5D, y, z + 0.5D, playerLocation.getYaw(), playerLocation.getPitch());
    if (isLocationRestricted(location, settings.avoidance()))
      return null; 
    location.setY(y + 1.0D);
    return location;
  }
  
  private Location generateRandomRoundLocationNearPoint(Player player, int centerX, int centerZ, Settings settings, World world) {
    int genCenterX, genCenterZ;
    LocationGenOptions locationGenOptions = settings.locationGenOptions();
    int minX = locationGenOptions.minX();
    int maxX = locationGenOptions.maxX();
    int minZ = locationGenOptions.minZ();
    int maxZ = locationGenOptions.maxZ();
    int radiusMin = locationGenOptions.nearRadiusMin();
    int radiusMax = locationGenOptions.nearRadiusMax();
    int x = 0, z = 0;
    switch (locationGenOptions.genFormat()) {
      case RECTANGULAR:
        do {
          x = centerX + this.random.nextInt(radiusMax * 2 + 1) - radiusMax;
          z = centerZ + this.random.nextInt(radiusMax * 2 + 1) - radiusMax;
        } while ((x < minX || x > maxX) && (z < minZ || z > maxZ));
        break;
      case RADIAL:
        genCenterX = locationGenOptions.centerX();
        genCenterZ = locationGenOptions.centerZ();
        do {
          double theta = this.random.nextDouble() * 2.0D * Math.PI;
          double r = radiusMin + (radiusMax - radiusMin) * Math.sqrt(this.random.nextDouble());
          x = (int)(centerX + r * Math.cos(theta));
          z = (int)(centerZ + r * Math.sin(theta));
        } while (isInsideRadiusCircle(x, z, minX, minZ, maxX, maxZ, genCenterX, genCenterZ));
        break;
    } 
    int y = findSafeYPoint(world, x, z);
    if (y < 0)
      return null; 
    Location playerLocation = player.getLocation();
    Location location = new Location(world, x + 0.5D, y, z + 0.5D, playerLocation.getYaw(), playerLocation.getPitch());
    if (isLocationRestricted(location, settings.avoidance()))
      return null; 
    location.setY(y + 1.0D);
    return location;
  }
  
  private int findSafeYPoint(World world, int x, int z) {
    return (world.getEnvironment() != World.Environment.NETHER) ? world.getHighestBlockYAt(x, z) : findSafeNetherYPoint(world, x, z);
  }
  
  private int findSafeNetherYPoint(World world, int x, int z) {
    for (int y = 32; y < 90; y++) {
      Location location = new Location(world, x, y, z);
      if (location.getBlock().getType().isSolid() && !isInsideBlocks(location, false))
        return location.getBlockY(); 
    } 
    return -1;
  }
  
  public boolean isInsideRadiusSquare(int x, int z, int minX, int minZ, int maxX, int maxZ, int centerX, int centerZ) {
    int realMinX = centerX + minX;
    int realMinZ = centerZ + minZ;
    int realMaxX = centerX + maxX;
    int realMaxZ = centerZ + maxZ;
    return (x >= realMinX && x <= realMaxX && z >= realMinZ && z <= realMaxZ);
  }
  
  public boolean isInsideRadiusCircle(int x, int z, int minX, int minZ, int maxX, int maxZ, int centerX, int centerZ) {
    int deltaX = x - centerX;
    int deltaZ = z - centerZ;
    double maxDistanceRatioX = deltaX / maxX;
    double maxDistanceRatioZ = deltaZ / maxZ;
    double maxDistance = maxDistanceRatioX * maxDistanceRatioX + maxDistanceRatioZ * maxDistanceRatioZ;
    double minDistanceRatioX = deltaX / minX;
    double minDistanceRatioZ = deltaZ / minZ;
    double minDistance = minDistanceRatioX * minDistanceRatioX + minDistanceRatioZ * minDistanceRatioZ;
    return (maxDistance <= 1.0D && minDistance >= 2.0D);
  }
  
  private boolean isLocationRestricted(Location location, Avoidance avoidance) {
    if (isOutsideWorldBorder(location)) {
      this.rtpManager.printDebug("Location " + Utils.locationToString(location) + " is outside the world border.");
      return true;
    } 
    if (location.getWorld().getEnvironment() != World.Environment.NETHER && isInsideBlocks(location, true)) {
      this.rtpManager.printDebug("Location " + Utils.locationToString(location) + " is inside blocks.");
      return true;
    } 
    if (isDisallowedBlock(location, avoidance)) {
      this.rtpManager.printDebug("Location " + Utils.locationToString(location) + " contains a disallowed block.");
      return true;
    } 
    if (isDisallowedBiome(location, avoidance)) {
      this.rtpManager.printDebug("Location " + Utils.locationToString(location) + " is in a disallowed biome.");
      return true;
    } 
    if (isInsideRegion(location, avoidance)) {
      this.rtpManager.printDebug("Location " + Utils.locationToString(location) + " is inside a disallowed region.");
      return true;
    } 
    if (isInsideTown(location, avoidance)) {
      this.rtpManager.printDebug("Location " + Utils.locationToString(location) + " is inside a disallowed town.");
      return true;
    } 
    return false;
  }
  
  private boolean isOutsideWorldBorder(Location loc) {
    return !loc.getWorld().getWorldBorder().isInside(loc);
  }
  
  private boolean isInsideBlocks(Location location, boolean onlyCheckOneBlockUp) {
    Location aboveLocation = location.clone().add(0.0D, 2.0D, 0.0D);
    if (!aboveLocation.getBlock().getType().isAir())
      return true; 
    return (!onlyCheckOneBlockUp && !aboveLocation.subtract(0.0D, 1.0D, 0.0D).getBlock().getType().isAir());
  }
  
  private boolean isDisallowedBlock(Location loc, Avoidance avoidance) {
    if (avoidance.avoidBlocks().isEmpty())
      return false; 
    boolean contains = avoidance.avoidBlocks().contains(loc.getBlock().getType());
    return (avoidance.avoidBlocksBlacklist() == contains);
  }
  
  private boolean isDisallowedBiome(Location loc, Avoidance avoidance) {
    if (avoidance.avoidBiomes().isEmpty())
      return false; 
    boolean contains = avoidance.avoidBiomes().contains(loc.getBlock().getBiome());
    return (avoidance.avoidBiomesBlacklist() == contains);
  }
  
  private boolean isInsideRegion(Location loc, Avoidance avoidance) {
    return (avoidance.avoidRegions() && WGUtils.getApplicableRegions(loc) != null && !WGUtils.getApplicableRegions(loc).getRegions().isEmpty());
  }
  
  private boolean isInsideTown(Location loc, Avoidance avoidance) {
    return (avoidance.avoidTowns() && TownyUtils.getTownByLocation(loc) != null);
  }
}
