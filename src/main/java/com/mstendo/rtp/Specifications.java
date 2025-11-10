package com.mstendo.rtp;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import com.mstendo.rtp.channels.Channel;
import com.mstendo.rtp.utils.Utils;
import com.mstendo.rtp.utils.VersionUtils;

public final class Specifications {
  private final Set<String> joinChannels;
  private final Map<String, List<World>> voidChannels;
  private final Object2IntMap<String> voidLevels;
  private final Map<String, List<World>> respawnChannels;
  
  public Specifications(Set<String> joinChannels, Map<String, List<World>> voidChannels, Object2IntMap<String> voidLevels, Map<String, List<World>> respawnChannels) {
    this.joinChannels = joinChannels;
    this.voidChannels = voidChannels;
    this.voidLevels = voidLevels;
    this.respawnChannels = respawnChannels;
  }
  
  public Set<String> joinChannels() {
    return joinChannels;
  }
  
  public Map<String, List<World>> voidChannels() {
    return voidChannels;
  }
  
  public Object2IntMap<String> voidLevels() {
    return voidLevels;
  }
  
  public Map<String, List<World>> respawnChannels() {
    return respawnChannels;
  }
  
  public void clearAll() {
    joinChannels.clear();
    voidChannels.clear();
    voidLevels.clear();
    respawnChannels.clear();
  }
  
  public void assign(Channel newChannel, ConfigurationSection section) {
    if (section == null)
      return; 
    if (section.getBoolean("teleport_on_first_join", false))
      joinChannels.add(newChannel.id()); 
    List<World> voidWorlds = Utils.getWorldList(section.getStringList("void_worlds"));
    if (!voidWorlds.isEmpty())
      voidChannels.put(newChannel.id(), voidWorlds); 
    int voidLevel = section.getInt("voidLevel");
    if (voidLevel != VersionUtils.VOID_LEVEL && voidChannels.containsKey(newChannel.id()))
      voidLevels.put(newChannel.id(), section.getInt("voidLevel")); 
    List<World> respawnWorlds = Utils.getWorldList(section.getStringList("respawn_worlds"));
    if (!respawnWorlds.isEmpty())
      respawnChannels.put(newChannel.id(), respawnWorlds); 
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Specifications that = (Specifications) o;
    return Objects.equals(joinChannels, that.joinChannels) &&
           Objects.equals(voidChannels, that.voidChannels) &&
           Objects.equals(voidLevels, that.voidLevels) &&
           Objects.equals(respawnChannels, that.respawnChannels);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(joinChannels, voidChannels, voidLevels, respawnChannels);
  }
  
  @Override
  public String toString() {
    return "Specifications[joinChannels=" + joinChannels + ", voidChannels=" + voidChannels + ", voidLevels=" + voidLevels + ", respawnChannels=" + respawnChannels + "]";
  }
}
