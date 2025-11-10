package com.mstendo.rtp.channels.settings;

import java.util.Objects;

public final class Restrictions {
  private final boolean restrictMove;
  private final boolean restrictTeleport;
  private final boolean restrictDamage;
  private final boolean restrictDamageOthers;
  private final boolean damageCheckOnlyPlayers;
  
  public Restrictions(boolean restrictMove, boolean restrictTeleport, boolean restrictDamage, boolean restrictDamageOthers, boolean damageCheckOnlyPlayers) {
    this.restrictMove = restrictMove;
    this.restrictTeleport = restrictTeleport;
    this.restrictDamage = restrictDamage;
    this.restrictDamageOthers = restrictDamageOthers;
    this.damageCheckOnlyPlayers = damageCheckOnlyPlayers;
  }
  
  public boolean restrictMove() {
    return restrictMove;
  }
  
  public boolean restrictTeleport() {
    return restrictTeleport;
  }
  
  public boolean restrictDamage() {
    return restrictDamage;
  }
  
  public boolean restrictDamageOthers() {
    return restrictDamageOthers;
  }
  
  public boolean damageCheckOnlyPlayers() {
    return damageCheckOnlyPlayers;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Restrictions that = (Restrictions) o;
    return restrictMove == that.restrictMove &&
           restrictTeleport == that.restrictTeleport &&
           restrictDamage == that.restrictDamage &&
           restrictDamageOthers == that.restrictDamageOthers &&
           damageCheckOnlyPlayers == that.damageCheckOnlyPlayers;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(restrictMove, restrictTeleport, restrictDamage, restrictDamageOthers, damageCheckOnlyPlayers);
  }
  
  @Override
  public String toString() {
    return "Restrictions[restrictMove=" + restrictMove + ", restrictTeleport=" + restrictTeleport + ", restrictDamage=" + restrictDamage + ", restrictDamageOthers=" + restrictDamageOthers + ", damageCheckOnlyPlayers=" + damageCheckOnlyPlayers + "]";
  }
}
