package com.mstendo.rtp.actions.impl;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.mstendo.rtp.MSTRandomTeleport;
import com.mstendo.rtp.actions.Action;
import com.mstendo.rtp.actions.ActionType;
import com.mstendo.rtp.channels.Channel;
import com.mstendo.rtp.utils.Utils;

public final class ActionBarActionType implements ActionType {
  private static final Key KEY = Key.key("mstrandomteleport:actionbar");
  
  @NotNull
  public Action instance(@NotNull String context, @NotNull MSTRandomTeleport plugin) {
    return new ActionBarAction(context);
  }
  
  @NotNull
  public Key key() {
    return KEY;
  }
  
  private static final class ActionBarAction implements Action {
    @NotNull
    private final String message;
    
    public ActionBarAction(@NotNull String message) {
      this.message = message;
    }
    
    @NotNull
    public String message() {
      return message;
    }
    
    public void perform(@NotNull Channel channel, @NotNull Player player, @NotNull String[] searchList, @NotNull String[] replacementList) {
      String messageText = Utils.replaceEach(this.message, searchList, replacementList);
      try {
        // Try to use Adventure API
        player.getClass().getMethod("sendActionBar", net.kyori.adventure.text.Component.class).invoke(player, 
          net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().deserialize(messageText));
      } catch (Exception e) {
        // Fallback - send as regular message if ActionBar not supported
        player.sendMessage(messageText);
      }
    }
  }
}
