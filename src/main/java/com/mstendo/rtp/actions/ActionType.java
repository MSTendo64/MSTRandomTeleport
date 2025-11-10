package com.mstendo.rtp.actions;

import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import com.mstendo.rtp.MSTRandomTeleport;

public interface ActionType extends Keyed {
  @NotNull
  Action instance(@NotNull String paramString, @NotNull MSTRandomTeleport plugin);
}
