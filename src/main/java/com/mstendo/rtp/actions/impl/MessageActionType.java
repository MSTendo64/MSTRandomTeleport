package com.mstendo.rtp.actions.impl;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import com.mstendo.rtp.MSTRandomTeleport;
import com.mstendo.rtp.actions.Action;
import com.mstendo.rtp.actions.ActionType;
import com.mstendo.rtp.utils.Utils;

public final class MessageActionType implements ActionType {
  private static final Key KEY = Key.key("mstrandomteleport:message");
  
  @NotNull
  public Action instance(@NotNull String context, @NotNull MSTRandomTeleport plugin) {
    String text = Utils.COLORIZER.colorize(context);
    return new MessageAction(text);
  }
  
  @NotNull
  public Key key() {
    return KEY;
  }
}
