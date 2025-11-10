package com.mstendo.rtp.channels.settings;

import java.util.Objects;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public final class Bossbar {
  private final boolean bossbarEnabled;
  private final String bossbarTitle;
  private final BarColor bossbarColor;
  private final BarStyle bossbarType;
  
  public Bossbar(boolean bossbarEnabled, String bossbarTitle, BarColor bossbarColor, BarStyle bossbarType) {
    this.bossbarEnabled = bossbarEnabled;
    this.bossbarTitle = bossbarTitle;
    this.bossbarColor = bossbarColor;
    this.bossbarType = bossbarType;
  }
  
  public boolean bossbarEnabled() {
    return bossbarEnabled;
  }
  
  public String bossbarTitle() {
    return bossbarTitle;
  }
  
  public BarColor bossbarColor() {
    return bossbarColor;
  }
  
  public BarStyle bossbarType() {
    return bossbarType;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Bossbar bossbar = (Bossbar) o;
    return bossbarEnabled == bossbar.bossbarEnabled &&
           Objects.equals(bossbarTitle, bossbar.bossbarTitle) &&
           bossbarColor == bossbar.bossbarColor &&
           bossbarType == bossbar.bossbarType;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(bossbarEnabled, bossbarTitle, bossbarColor, bossbarType);
  }
  
  @Override
  public String toString() {
    return "Bossbar[bossbarEnabled=" + bossbarEnabled + ", bossbarTitle=" + bossbarTitle + ", bossbarColor=" + bossbarColor + ", bossbarType=" + bossbarType + "]";
  }
}
