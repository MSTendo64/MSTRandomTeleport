package com.mstendo.rtp.actions;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.mstendo.rtp.channels.Channel;

public interface Action {
  void perform(@NotNull Channel paramChannel, @NotNull Player paramPlayer, @NotNull String[] paramArrayOfString1, @NotNull String[] paramArrayOfString2);
}
