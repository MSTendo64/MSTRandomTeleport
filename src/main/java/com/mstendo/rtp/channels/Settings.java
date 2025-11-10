package com.mstendo.rtp.channels;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntSortedMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import com.mstendo.rtp.MSTRandomTeleport;
import com.mstendo.rtp.actions.Action;
import com.mstendo.rtp.actions.ActionRegistry;
import com.mstendo.rtp.channels.settings.Actions;
import com.mstendo.rtp.channels.settings.Avoidance;
import com.mstendo.rtp.channels.settings.Bossbar;
import com.mstendo.rtp.channels.settings.Cooldown;
import com.mstendo.rtp.channels.settings.Costs;
import com.mstendo.rtp.channels.settings.LocationGenOptions;
import com.mstendo.rtp.channels.settings.Particles;
import com.mstendo.rtp.channels.settings.ParticleData;
import com.mstendo.rtp.channels.settings.Restrictions;
import com.mstendo.rtp.configuration.Config;
import com.mstendo.rtp.utils.TimedExpiringMap;
import com.mstendo.rtp.utils.Utils;
import com.mstendo.rtp.utils.VersionUtils;

public final class Settings {
  private final Costs costs;
  private final LocationGenOptions locationGenOptions;
  private final Cooldown cooldown;
  private final Bossbar bossbar;
  private final Particles particles;
  private final Restrictions restrictions;
  private final Avoidance avoidance;
  private final Actions actions;
  
  public Settings(Costs costs, LocationGenOptions locationGenOptions, Cooldown cooldown, Bossbar bossbar, Particles particles, Restrictions restrictions, Avoidance avoidance, Actions actions) {
    this.costs = costs;
    this.locationGenOptions = locationGenOptions;
    this.cooldown = cooldown;
    this.bossbar = bossbar;
    this.particles = particles;
    this.restrictions = restrictions;
    this.avoidance = avoidance;
    this.actions = actions;
  }
  
  public Costs costs() {
    return costs;
  }
  
  public LocationGenOptions locationGenOptions() {
    return locationGenOptions;
  }
  
  public Cooldown cooldown() {
    return cooldown;
  }
  
  public Bossbar bossbar() {
    return bossbar;
  }
  
  public Particles particles() {
    return particles;
  }
  
  public Restrictions restrictions() {
    return restrictions;
  }
  
  public Avoidance avoidance() {
    return avoidance;
  }
  
  public Actions actions() {
    return actions;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Settings settings = (Settings) o;
    return Objects.equals(costs, settings.costs) &&
           Objects.equals(locationGenOptions, settings.locationGenOptions) &&
           Objects.equals(cooldown, settings.cooldown) &&
           Objects.equals(bossbar, settings.bossbar) &&
           Objects.equals(particles, settings.particles) &&
           Objects.equals(restrictions, settings.restrictions) &&
           Objects.equals(avoidance, settings.avoidance) &&
           Objects.equals(actions, settings.actions);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(costs, locationGenOptions, cooldown, bossbar, particles, restrictions, avoidance, actions);
  }
  
  @Override
  public String toString() {
    return "Settings[costs=" + costs + ", locationGenOptions=" + locationGenOptions + ", cooldown=" + cooldown + ", bossbar=" + bossbar + ", particles=" + particles + ", restrictions=" + restrictions + ", avoidance=" + avoidance + ", actions=" + actions + "]";
  }
  
  public static Settings create(MSTRandomTeleport plugin, ConfigurationSection config, Config pluginConfig, Settings template, boolean applyTemplate) {
    return new Settings(
        setupCosts(plugin, config.getConfigurationSection("costs"), template, pluginConfig, applyTemplate), 
        setupLocationGenOptions(config.getConfigurationSection("location_generation_options"), template, pluginConfig, applyTemplate), 
        setupCooldown(plugin, config.getConfigurationSection("cooldown"), template, pluginConfig, applyTemplate), 
        setupBossBar(config.getConfigurationSection("bossbar"), template, pluginConfig, applyTemplate), 
        setupParticles(config.getConfigurationSection("particles"), template, pluginConfig, applyTemplate), 
        setupRestrictions(config.getConfigurationSection("restrictions"), template, pluginConfig, applyTemplate), 
        setupAvoidance(config.getConfigurationSection("avoid"), Bukkit.getPluginManager(), template, pluginConfig, applyTemplate), 
        setupActions(plugin, config.getConfigurationSection("actions"), template, pluginConfig, applyTemplate));
  }
  
  public static Costs setupCosts(MSTRandomTeleport plugin, ConfigurationSection channelCosts, Settings template, Config pluginConfig, boolean applyTemplate) {
    if (pluginConfig.isNullSection(channelCosts)) {
      if (!applyTemplate)
        return null; 
      return (template != null && template.costs() != null) ? 
        template.costs() : 
        new Costs(null, null, -1.0D, -1, -1);
    } 
    String moneyTypeStr = channelCosts.getString("money_type", "VAULT").toUpperCase(Locale.ENGLISH);
    // Normalize PLAYERPOINTS to PLAYER_POINTS for compatibility
    if ("PLAYERPOINTS".equals(moneyTypeStr)) {
      moneyTypeStr = "PLAYER_POINTS";
    }
    Costs.MoneyType moneyType = Costs.MoneyType.valueOf(moneyTypeStr);
    double moneyCost = channelCosts.getDouble("money_cost", -1.0D);
    int hungerCost = channelCosts.getInt("hunger_cost", -1);
    int expCost = channelCosts.getInt("experience_cost", -1);
    return new Costs(plugin.getEconomy(), moneyType, moneyCost, hungerCost, expCost);
  }
  
  public static LocationGenOptions setupLocationGenOptions(ConfigurationSection locationGenOptions, Settings template, Config pluginConfig, boolean applyTemplate) {
    if (pluginConfig.isNullSection(locationGenOptions)) {
      if (!applyTemplate || template == null || template.locationGenOptions() == null)
        return null; 
      return template.locationGenOptions();
    } 
    LocationGenOptions.Shape shape = LocationGenOptions.Shape.valueOf(locationGenOptions.getString("shape", "SQUARE").toUpperCase(Locale.ENGLISH));
    LocationGenOptions.GenFormat genFormat = LocationGenOptions.GenFormat.valueOf(locationGenOptions.getString("gen_format", "RECTANGULAR").toUpperCase(Locale.ENGLISH));
    int minX = locationGenOptions.getInt("min_x");
    int maxX = locationGenOptions.getInt("max_x");
    int minZ = locationGenOptions.getInt("min_z");
    int maxZ = locationGenOptions.getInt("max_z");
    int nearRadiusMin = locationGenOptions.getInt("min_near_point_distance", 30);
    int nearRadiusMax = locationGenOptions.getInt("max_near_point_distance", 60);
    int centerX = locationGenOptions.getInt("center_x", 0);
    int centerZ = locationGenOptions.getInt("center_z", 0);
    int maxLocationAttempts = locationGenOptions.getInt("max_location_attempts", 50);
    return new LocationGenOptions(shape, genFormat, minX, maxX, minZ, maxZ, nearRadiusMin, nearRadiusMax, centerX, centerZ, maxLocationAttempts);
  }
  
  public static Cooldown setupCooldown(MSTRandomTeleport plugin, ConfigurationSection cooldown, Settings template, Config pluginConfig, boolean applyTemplate) {
    Object2IntLinkedOpenHashMap object2IntLinkedOpenHashMap1 = new Object2IntLinkedOpenHashMap();
    Object2IntLinkedOpenHashMap object2IntLinkedOpenHashMap2 = new Object2IntLinkedOpenHashMap();
    if (pluginConfig.isNullSection(cooldown)) {
      if (!applyTemplate)
        return null; 
      return (template != null && template.cooldown() != null) ? 
        template.cooldown() : 
        new Cooldown(-1, null, (Object2IntSortedMap)object2IntLinkedOpenHashMap1, -1, (Object2IntSortedMap)object2IntLinkedOpenHashMap2);
    } 
    int defaultCooldown = cooldown.getInt("default_cooldown", -1);
    TimedExpiringMap<String, Long> playerCooldowns = (defaultCooldown > 0) ? new TimedExpiringMap(TimeUnit.SECONDS) : null;
    boolean useLastGroupCooldown = cooldown.getBoolean("use_last_group_cooldown", false);
    defaultCooldown = processCooldownSection(plugin, cooldown.getConfigurationSection("group_cooldowns"), (Object2IntSortedMap<String>)object2IntLinkedOpenHashMap1, useLastGroupCooldown, defaultCooldown, pluginConfig);
    int defaultPreTeleportCooldown = cooldown.getInt("default_pre_teleport_cooldown", -1);
    defaultPreTeleportCooldown = processCooldownSection(plugin, cooldown.getConfigurationSection("pre_teleport_group_cooldowns"), (Object2IntSortedMap<String>)object2IntLinkedOpenHashMap2, useLastGroupCooldown, defaultPreTeleportCooldown, pluginConfig);
    return new Cooldown(defaultCooldown, playerCooldowns, (Object2IntSortedMap)object2IntLinkedOpenHashMap1, defaultPreTeleportCooldown, (Object2IntSortedMap)object2IntLinkedOpenHashMap2);
  }
  
  private static int processCooldownSection(MSTRandomTeleport plugin, ConfigurationSection section, Object2IntSortedMap<String> map, boolean useLastGroup, int currentDefault, Config pluginConfig) {
    if (!pluginConfig.isNullSection(section) && plugin.getPerms() != null) {
      for (String groupName : section.getKeys(false))
        map.put(groupName, section.getInt(groupName)); 
      if (!map.isEmpty() && useLastGroup) {
        List<String> keys = new ArrayList<>((Collection<? extends String>)map.keySet());
        currentDefault = section.getInt(keys.get(keys.size() - 1));
      } 
    } 
    return currentDefault;
  }
  
  public static Bossbar setupBossBar(ConfigurationSection bossbar, Settings template, Config pluginConfig, boolean applyTemplate) {
    if (pluginConfig.isNullSection(bossbar)) {
      if (!applyTemplate)
        return null; 
      return (template != null && template.bossbar() != null) ? 
        template.bossbar() : 
        new Bossbar(false, null, null, null);
    } 
    boolean enabled = bossbar.getBoolean("enabled");
    String title = Utils.COLORIZER.colorize(bossbar.getString("title"));
    BarColor color = BarColor.valueOf(bossbar.getString("color").toUpperCase(Locale.ENGLISH));
    BarStyle style = BarStyle.valueOf(bossbar.getString("style").toUpperCase(Locale.ENGLISH));
    return new Bossbar(enabled, title, color, style);
  }
  
  public static Particles setupParticles(ConfigurationSection particles, Settings template, Config pluginConfig, boolean applyTemplate) {
    if (pluginConfig.isNullSection(particles)) {
      if (!applyTemplate)
        return null; 
      return (template != null && template.particles() != null) ? 
        template.particles() : 
        new Particles(false, false, null, -1, -1.0D, -1.0D, -1.0D, false, false, false, false, false, null, -1, -1.0D, -1.0D);
    } 
    boolean preTeleportEnabled = false;
    boolean preTeleportSendOnlyToPlayer = false;
    List<ParticleData> preTeleportParticles = null;
    ImmutableList<ParticleData> immutableList = ImmutableList.of();
    int preTeleportDots = 0;
    double preTeleportRadius = 0.0D;
    double preTeleportParticleSpeed = 0.0D;
    double preTeleportSpeed = 0.0D;
    boolean preTeleportInvert = false;
    boolean preTeleportJumping = false;
    boolean preTeleportMoveNear = false;
    boolean afterTeleportParticleEnabled = false;
    boolean afterTeleportSendOnlyToPlayer = false;
    ParticleData afterTeleportParticle = null;
    int afterTeleportCount = 0;
    double afterTeleportRadius = 0.0D;
    double afterTeleportParticleSpeed = 0.0D;
    ConfigurationSection preTeleport = particles.getConfigurationSection("pre_teleport");
    if (!pluginConfig.isNullSection(preTeleport)) {
      preTeleportEnabled = preTeleport.getBoolean("enabled", false);
      preTeleportSendOnlyToPlayer = preTeleport.getBoolean("send_only_to_player", false);
      preTeleportParticles = pluginConfig.getStringListInAnyCase(preTeleport.get("id")).stream().map(Utils::createParticleData).collect(Collectors.toList());
      immutableList = ImmutableList.copyOf(preTeleportParticles);
      preTeleportDots = preTeleport.getInt("dots");
      preTeleportRadius = preTeleport.getDouble("radius");
      preTeleportParticleSpeed = preTeleport.getDouble("particle_speed");
      preTeleportSpeed = preTeleport.getDouble("speed");
      preTeleportInvert = preTeleport.getBoolean("invert");
      preTeleportJumping = preTeleport.getBoolean("jumping");
      preTeleportMoveNear = preTeleport.getBoolean("move_near");
    } 
    ConfigurationSection afterTeleport = particles.getConfigurationSection("after_teleport");
    if (!pluginConfig.isNullSection(afterTeleport)) {
      afterTeleportParticleEnabled = afterTeleport.getBoolean("enabled", false);
      afterTeleportSendOnlyToPlayer = afterTeleport.getBoolean("send_only_to_player", false);
      afterTeleportParticle = Utils.createParticleData(afterTeleport.getString("id"));
      afterTeleportCount = afterTeleport.getInt("count");
      afterTeleportRadius = afterTeleport.getDouble("radius");
      afterTeleportParticleSpeed = afterTeleport.getDouble("particle_speed");
    } 
    return new Particles(preTeleportEnabled, preTeleportSendOnlyToPlayer, immutableList, preTeleportDots, preTeleportRadius, preTeleportParticleSpeed, preTeleportSpeed, preTeleportInvert, preTeleportJumping, preTeleportMoveNear, afterTeleportParticleEnabled, afterTeleportSendOnlyToPlayer, afterTeleportParticle, afterTeleportCount, afterTeleportRadius, afterTeleportParticleSpeed);
  }
  
  public static Restrictions setupRestrictions(ConfigurationSection restrictions, Settings template, Config pluginConfig, boolean applyTemplate) {
    if (pluginConfig.isNullSection(restrictions)) {
      if (!applyTemplate)
        return null; 
      return (template != null && template.restrictions() != null) ? 
        template.restrictions() : 
        new Restrictions(false, false, false, false, false);
    } 
    return new Restrictions(restrictions
        .getBoolean("move", false), restrictions
        .getBoolean("teleport", false), restrictions
        .getBoolean("damage", false), restrictions
        .getBoolean("damage_others", false), restrictions
        .getBoolean("damage_check_only_players", false));
  }
  
  public static Avoidance setupAvoidance(ConfigurationSection avoid, PluginManager pluginManager, Settings template, Config pluginConfig, boolean applyTemplate) {
    if (pluginConfig.isNullSection(avoid)) {
      if (!applyTemplate)
        return null; 
      return (template != null && template.avoidance() != null) ? 
        template.avoidance() : 
        new Avoidance(true, Collections.<Material>emptySet(), true, Collections.<Biome>emptySet(), false, false);
    } 
    boolean avoidBlocksBlacklist = avoid.getBoolean("blocks.blacklist", true);
    Set<Material> avoidBlocks = EnumSet.noneOf(Material.class);
    for (String material : avoid.getStringList("blocks.list"))
      avoidBlocks.add(Material.valueOf(material.toUpperCase(Locale.ENGLISH))); 
    boolean avoidBiomesBlacklist = avoid.getBoolean("biomes.blacklist", true);
    Set<Biome> avoidBiomes = (VersionUtils.SUB_VERSION > 20) ? new HashSet<>() : EnumSet.<Biome>noneOf(Biome.class);
    for (String biome : avoid.getStringList("biomes.list"))
      avoidBiomes.add(Biome.valueOf(biome.toUpperCase(Locale.ENGLISH))); 
    boolean avoidRegions = (avoid.getBoolean("regions", false) && pluginManager.isPluginEnabled("WorldGuard"));
    boolean avoidTowns = (avoid.getBoolean("towns", false) && pluginManager.isPluginEnabled("Towny"));
    return new Avoidance(avoidBlocksBlacklist, avoidBlocks, avoidBiomesBlacklist, avoidBiomes, avoidRegions, avoidTowns);
  }
  
  public static Actions setupActions(MSTRandomTeleport plugin, ConfigurationSection actions, Settings template, Config pluginConfig, boolean applyTemplate) {
    if (pluginConfig.isNullSection(actions)) {
      if (!applyTemplate)
        return null; 
      return (template != null && template.actions() != null) ? 
        template.actions() : 
        new Actions(Collections.<Action>emptyList(), (Int2ObjectMap)new Int2ObjectOpenHashMap(), Collections.<Action>emptyList());
    } 
    ActionRegistry actionRegistry = plugin.getRtpManager().getActionRegistry();
    ImmutableList<Action> immutableList1 = getActionList(plugin, actionRegistry, actions.getStringList("pre_teleport"));
    Int2ObjectOpenHashMap int2ObjectOpenHashMap = new Int2ObjectOpenHashMap();
    ConfigurationSection cooldownActions = actions.getConfigurationSection("on_cooldown");
    if (!pluginConfig.isNullSection(cooldownActions))
      for (String actionId : cooldownActions.getKeys(false)) {
        if (!Utils.isNumeric(actionId))
          continue; 
        int time = Integer.parseInt(actionId);
        ImmutableList<Action> immutableList = getActionList(plugin, actionRegistry, cooldownActions.getStringList(actionId));
        int2ObjectOpenHashMap.put(time, immutableList);
      }  
    ImmutableList<Action> immutableList2 = getActionList(plugin, actionRegistry, actions.getStringList("after_teleport"));
    return new Actions((List)immutableList1, (Int2ObjectMap)int2ObjectOpenHashMap, (List)immutableList2);
  }
  
  private static ImmutableList<Action> getActionList(MSTRandomTeleport plugin, ActionRegistry actionRegistry, List<String> actionStrings) {
    List<Action> actions = new ArrayList<>(actionStrings.size());
    for (String actionStr : actionStrings) {
      try {
        actions.add(Objects.<Action>requireNonNull(actionRegistry.resolveAction(actionStr), "Type doesn't exist"));
      } catch (Exception ex) {
        plugin.getPluginLogger().warn("Couldn't create action for string '" + actionStr + "'");
      } 
    } 
    return ImmutableList.copyOf(actions);
  }
}
