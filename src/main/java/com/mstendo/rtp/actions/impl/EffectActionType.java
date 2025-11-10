package com.mstendo.rtp.actions.impl;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import com.mstendo.rtp.MSTRandomTeleport;
import com.mstendo.rtp.actions.Action;
import com.mstendo.rtp.actions.ActionType;
import com.mstendo.rtp.channels.Channel;

public final class EffectActionType implements ActionType {
  private static final Key KEY = Key.key("mstrandomteleport:effect");
  
  @NotNull
  public Action instance(@NotNull String context, @NotNull MSTRandomTeleport plugin) {
    String[] effectArgs = context.split(";");
    int length = effectArgs.length;
    return new EffectAction(new PotionEffect(
          PotionEffectType.getByName(effectArgs[0]), 
          (length > 1) ? Integer.parseInt(effectArgs[1]) : 1, 
          (length > 2) ? Integer.parseInt(effectArgs[2]) : 1));
  }
  
  @NotNull
  public Key key() {
    return KEY;
  }
  
  private static final class EffectAction implements Action {
    @NotNull
    private final PotionEffect effect;
    
    public EffectAction(@NotNull PotionEffect effect) {
      this.effect = effect;
    }
    
    @NotNull
    public PotionEffect effect() {
      return effect;
    }
    
    public void perform(@NotNull Channel channel, @NotNull Player player, @NotNull String[] searchList, @NotNull String[] replacementList) {
      player.addPotionEffect(this.effect);
    }
  }
}
