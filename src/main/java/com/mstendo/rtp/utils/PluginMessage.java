package com.mstendo.rtp.utils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Generated;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import com.mstendo.rtp.MSTRandomTeleport;
import com.mstendo.rtp.RtpManager;

public final class PluginMessage implements PluginMessageListener {
  private final MSTRandomTeleport plugin;
  
  private final RtpManager rtpManager;
  
  private final String serverId;
  
  @Generated
  public String getServerId() {
    return this.serverId;
  }
  
  public PluginMessage(MSTRandomTeleport plugin, String serverId) {
    this.plugin = plugin;
    this.rtpManager = plugin.getRtpManager();
    this.serverId = serverId;
  }
  
  public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {
    if (!channel.equals("BungeeCord"))
      return; 
    ByteArrayDataInput input = ByteStreams.newDataInput(message);
    String subchannel = input.readUTF();
    if (subchannel.equalsIgnoreCase("msrtp")) {
      String serverId = input.readUTF();
      this.rtpManager.printDebug("Received plugin message from another server.");
      this.rtpManager.printDebug("ServerID specified: " + this.serverId);
      this.rtpManager.printDebug("ServerID received: " + serverId);
      if (!this.serverId.equals(serverId))
        return; 
      String teleportData = input.readUTF();
      this.rtpManager.printDebug("Teleport data: " + teleportData);
      int separatorIndex = teleportData.indexOf(' ');
      String playerName = teleportData.substring(0, separatorIndex);
      String teleportInfo = teleportData.substring(separatorIndex + 1);
      this.rtpManager.getProxyCalls().put(playerName, teleportInfo);
    } 
  }
  
  public void sendCrossProxy(Player player, String serverId, String data) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("Forward");
    out.writeUTF("ALL");
    out.writeUTF("ovrtp");
    out.writeUTF(serverId);
    out.writeUTF(data);
    player.sendPluginMessage((Plugin)this.plugin, "BungeeCord", out.toByteArray());
  }
  
  public void connectToServer(Player player, String server) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("Connect");
    out.writeUTF(server);
    player.sendPluginMessage((Plugin)this.plugin, "BungeeCord", out.toByteArray());
  }
}
