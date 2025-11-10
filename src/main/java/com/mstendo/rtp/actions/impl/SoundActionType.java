package com.mstendo.rtp.actions.impl;

import java.util.Locale;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.mstendo.rtp.MSTRandomTeleport;
import com.mstendo.rtp.actions.Action;
import com.mstendo.rtp.actions.ActionType;
import com.mstendo.rtp.channels.Channel;

public final class SoundActionType implements ActionType {
  private static final Key KEY = Key.key("mstrandomteleport:sound");
  
  @NotNull
  public Action instance(@NotNull String context, @NotNull MSTRandomTeleport plugin) {
    String[] soundArgs = context.split(";");
    int length = soundArgs.length;
    String soundName = soundArgs[0].toUpperCase(Locale.ENGLISH);
    return new SoundAction(
        soundName, 
        (length > 1) ? Float.parseFloat(soundArgs[1]) : 1.0F, 
        (length > 2) ? Float.parseFloat(soundArgs[2]) : 1.0F);
  }
  
  @NotNull
  public Key key() {
    return KEY;
  }
  
  private static final class SoundAction implements Action {
    @NotNull
    private final String sound;
    private final float volume;
    private final float pitch;
    
    public SoundAction(@NotNull String sound, float volume, float pitch) {
      this.sound = sound;
      this.volume = volume;
      this.pitch = pitch;
    }
    
    @NotNull
    public String sound() {
      return sound;
    }
    
    public float volume() {
      return volume;
    }
    
    public float pitch() {
      return pitch;
    }
    
    public void perform(@NotNull Channel channel, @NotNull Player player, @NotNull String[] searchList, @NotNull String[] replacementList) {
      player.playSound(player.getLocation(), org.bukkit.Sound.valueOf(sound.toUpperCase(Locale.ENGLISH)), volume, pitch);
    }
  }
}
