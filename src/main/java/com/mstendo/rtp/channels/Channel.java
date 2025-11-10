package com.mstendo.rtp.channels;

import java.util.List;
import java.util.Objects;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import com.mstendo.rtp.channels.settings.Messages;

public final class Channel {
  private final String id;
  private final String name;
  private final ChannelType type;
  private final List<World> activeWorlds;
  private final boolean teleportToFirstAllowedWorld;
  private final String serverToMove;
  private final int minPlayersToUse;
  private final int invulnerableTicks;
  @NotNull
  private final Settings settings;
  @NotNull
  private final Messages messages;
  
  public Channel(String id, String name, ChannelType type, List<World> activeWorlds, boolean teleportToFirstAllowedWorld, String serverToMove, int minPlayersToUse, int invulnerableTicks, @NotNull Settings settings, @NotNull Messages messages) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.activeWorlds = activeWorlds;
    this.teleportToFirstAllowedWorld = teleportToFirstAllowedWorld;
    this.serverToMove = serverToMove;
    this.minPlayersToUse = minPlayersToUse;
    this.invulnerableTicks = invulnerableTicks;
    this.settings = settings;
    this.messages = messages;
  }
  
  public String id() {
    return id;
  }
  
  public String name() {
    return name;
  }
  
  public ChannelType type() {
    return type;
  }
  
  public List<World> activeWorlds() {
    return activeWorlds;
  }
  
  public boolean teleportToFirstAllowedWorld() {
    return teleportToFirstAllowedWorld;
  }
  
  public String serverToMove() {
    return serverToMove;
  }
  
  public int minPlayersToUse() {
    return minPlayersToUse;
  }
  
  public int invulnerableTicks() {
    return invulnerableTicks;
  }
  
  @NotNull
  public Settings settings() {
    return settings;
  }
  
  @NotNull
  public Messages messages() {
    return messages;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Channel channel = (Channel) o;
    return teleportToFirstAllowedWorld == channel.teleportToFirstAllowedWorld &&
           minPlayersToUse == channel.minPlayersToUse &&
           invulnerableTicks == channel.invulnerableTicks &&
           Objects.equals(id, channel.id) &&
           Objects.equals(name, channel.name) &&
           type == channel.type &&
           Objects.equals(activeWorlds, channel.activeWorlds) &&
           Objects.equals(serverToMove, channel.serverToMove) &&
           Objects.equals(settings, channel.settings) &&
           Objects.equals(messages, channel.messages);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(id, name, type, activeWorlds, teleportToFirstAllowedWorld, serverToMove, minPlayersToUse, invulnerableTicks, settings, messages);
  }
  
  @Override
  public String toString() {
    return "Channel[id=" + id + ", name=" + name + ", type=" + type + ", activeWorlds=" + activeWorlds + ", teleportToFirstAllowedWorld=" + teleportToFirstAllowedWorld + ", serverToMove=" + serverToMove + ", minPlayersToUse=" + minPlayersToUse + ", invulnerableTicks=" + invulnerableTicks + ", settings=" + settings + ", messages=" + messages + "]";
  }
}
