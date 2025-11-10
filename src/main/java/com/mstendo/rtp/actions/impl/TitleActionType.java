package com.mstendo.rtp.actions.impl;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.mstendo.rtp.MSTRandomTeleport;
import com.mstendo.rtp.actions.Action;
import com.mstendo.rtp.actions.ActionType;
import com.mstendo.rtp.channels.Channel;
import com.mstendo.rtp.utils.Utils;

public final class TitleActionType implements ActionType {
  private static final Key KEY = Key.key("mstrandomteleport:title");
  
  @NotNull
  public Action instance(@NotNull String context, @NotNull MSTRandomTeleport plugin) {
    String[] titleMessages = context.split(";");
    int length = titleMessages.length;
    return new TitleAction(Utils.COLORIZER
        .colorize(titleMessages[0]), 
        (length > 1) ? Utils.COLORIZER.colorize(titleMessages[1]) : "", 
        (length > 2) ? Integer.parseInt(titleMessages[2]) : 10, 
        (length > 3) ? Integer.parseInt(titleMessages[3]) : 70, 
        (length > 4) ? Integer.parseInt(titleMessages[4]) : 20);
  }
  
  @NotNull
  public Key key() {
    return KEY;
  }
  
  private static final class TitleAction implements Action {
    @NotNull
    private final String title;
    @NotNull
    private final String subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;
    
    public TitleAction(@NotNull String title, @NotNull String subtitle, int fadeIn, int stay, int fadeOut) {
      this.title = title;
      this.subtitle = subtitle;
      this.fadeIn = fadeIn;
      this.stay = stay;
      this.fadeOut = fadeOut;
    }
    
    @NotNull
    public String title() {
      return title;
    }
    
    @NotNull
    public String subtitle() {
      return subtitle;
    }
    
    public int fadeIn() {
      return fadeIn;
    }
    
    public int stay() {
      return stay;
    }
    
    public int fadeOut() {
      return fadeOut;
    }
    
    public void perform(@NotNull Channel channel, @NotNull Player player, @NotNull String[] searchList, @NotNull String[] replacementList) {
      String titleText = Utils.replaceEach(this.title, searchList, replacementList);
      String subtitleText = Utils.replaceEach(this.subtitle, searchList, replacementList);
      if (Utils.USE_PAPI) {
        player.sendTitle(
            Utils.parsePlaceholders(titleText, player), 
            Utils.parsePlaceholders(subtitleText, player), fadeIn, stay, fadeOut);
        return;
      } 
      player.sendTitle(titleText, subtitleText, fadeIn, stay, fadeOut);
    }
  }
}
