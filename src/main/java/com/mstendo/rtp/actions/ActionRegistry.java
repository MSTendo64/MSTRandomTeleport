package com.mstendo.rtp.actions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.mstendo.rtp.MSTRandomTeleport;

public final class ActionRegistry {
  private static final Pattern ACTION_PATTERN = Pattern.compile("\\[(\\S+)] ?(.*)");
  
  private final MSTRandomTeleport plugin;
  
  private final Map<String, ActionType> types;
  
  public ActionRegistry(MSTRandomTeleport plugin) {
    this.plugin = plugin;
    this.types = new HashMap<>();
  }
  
  public void register(@NotNull ActionType type) {
    if (this.types.put(type.key().toString(), type) != null)
      this.plugin.getPluginLogger().warn("Type '" + type.key() + "' was overridden with '" + type.getClass().getName() + "'"); 
    this.types.putIfAbsent(type.key().value(), type);
  }
  
  @Nullable
  public ActionType getType(@NotNull String typeStr) {
    return this.types.get(typeStr.toLowerCase(Locale.ENGLISH));
  }
  
  @Nullable
  public Action resolveAction(@NotNull String actionStr) {
    Matcher matcher = ACTION_PATTERN.matcher(actionStr);
    if (!matcher.matches())
      return null; 
    ActionType type = getType(matcher.group(1));
    if (type == null)
      return null; 
    return type.instance(matcher.group(2).trim(), this.plugin);
  }
}
