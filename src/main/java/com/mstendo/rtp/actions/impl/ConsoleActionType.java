package com.mstendo.rtp.actions.impl;

import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.mstendo.rtp.MSTRandomTeleport;
import com.mstendo.rtp.actions.Action;
import com.mstendo.rtp.actions.ActionType;
import com.mstendo.rtp.channels.Channel;
import com.mstendo.rtp.utils.Utils;

public final class ConsoleActionType implements ActionType {
  private static final Key KEY = Key.key("mstrandomteleport:console");
  
  @NotNull
  public Action instance(@NotNull String context, @NotNull MSTRandomTeleport plugin) {
    return new ConsoleAction(context);
  }
  
  @NotNull
  public Key key() {
    return KEY;
  }
  
  private static final class ConsoleAction implements Action {
    @NotNull
    private final String command;
    
    public ConsoleAction(@NotNull String command) {
      this.command = command;
    }
    
    @NotNull
    public String command() {
      return command;
    }
    
    public void perform(@NotNull Channel channel, @NotNull Player player, @NotNull String[] searchList, @NotNull String[] replacementList) {
      Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), Utils.replaceEach(this.command, searchList, replacementList));
    }
  }
}
