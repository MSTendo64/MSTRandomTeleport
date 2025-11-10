package com.mstendo.rtp.channels.settings;

import java.util.Objects;

public final class Messages {
  private final String noPerms;
  private final String invalidWorld;
  private final String notEnoughPlayers;
  private final String notEnoughMoney;
  private final String notEnoughHunger;
  private final String notEnoughExp;
  private final String cooldown;
  private final String movedOnTeleport;
  private final String teleportedOnTeleport;
  private final String damagedOnTeleport;
  private final String damagedOtherOnTeleport;
  private final String failToFindLocation;
  
  public Messages(String noPerms, String invalidWorld, String notEnoughPlayers, String notEnoughMoney, String notEnoughHunger, String notEnoughExp, String cooldown, String movedOnTeleport, String teleportedOnTeleport, String damagedOnTeleport, String damagedOtherOnTeleport, String failToFindLocation) {
    this.noPerms = noPerms;
    this.invalidWorld = invalidWorld;
    this.notEnoughPlayers = notEnoughPlayers;
    this.notEnoughMoney = notEnoughMoney;
    this.notEnoughHunger = notEnoughHunger;
    this.notEnoughExp = notEnoughExp;
    this.cooldown = cooldown;
    this.movedOnTeleport = movedOnTeleport;
    this.teleportedOnTeleport = teleportedOnTeleport;
    this.damagedOnTeleport = damagedOnTeleport;
    this.damagedOtherOnTeleport = damagedOtherOnTeleport;
    this.failToFindLocation = failToFindLocation;
  }
  
  public String noPerms() {
    return noPerms;
  }
  
  public String invalidWorld() {
    return invalidWorld;
  }
  
  public String notEnoughPlayers() {
    return notEnoughPlayers;
  }
  
  public String notEnoughMoney() {
    return notEnoughMoney;
  }
  
  public String notEnoughHunger() {
    return notEnoughHunger;
  }
  
  public String notEnoughExp() {
    return notEnoughExp;
  }
  
  public String cooldown() {
    return cooldown;
  }
  
  public String movedOnTeleport() {
    return movedOnTeleport;
  }
  
  public String teleportedOnTeleport() {
    return teleportedOnTeleport;
  }
  
  public String damagedOnTeleport() {
    return damagedOnTeleport;
  }
  
  public String damagedOtherOnTeleport() {
    return damagedOtherOnTeleport;
  }
  
  public String failToFindLocation() {
    return failToFindLocation;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Messages messages = (Messages) o;
    return Objects.equals(noPerms, messages.noPerms) &&
           Objects.equals(invalidWorld, messages.invalidWorld) &&
           Objects.equals(notEnoughPlayers, messages.notEnoughPlayers) &&
           Objects.equals(notEnoughMoney, messages.notEnoughMoney) &&
           Objects.equals(notEnoughHunger, messages.notEnoughHunger) &&
           Objects.equals(notEnoughExp, messages.notEnoughExp) &&
           Objects.equals(cooldown, messages.cooldown) &&
           Objects.equals(movedOnTeleport, messages.movedOnTeleport) &&
           Objects.equals(teleportedOnTeleport, messages.teleportedOnTeleport) &&
           Objects.equals(damagedOnTeleport, messages.damagedOnTeleport) &&
           Objects.equals(damagedOtherOnTeleport, messages.damagedOtherOnTeleport) &&
           Objects.equals(failToFindLocation, messages.failToFindLocation);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(noPerms, invalidWorld, notEnoughPlayers, notEnoughMoney, notEnoughHunger, notEnoughExp, cooldown, movedOnTeleport, teleportedOnTeleport, damagedOnTeleport, damagedOtherOnTeleport, failToFindLocation);
  }
  
  @Override
  public String toString() {
    return "Messages[noPerms=" + noPerms + ", invalidWorld=" + invalidWorld + ", notEnoughPlayers=" + notEnoughPlayers + ", notEnoughMoney=" + notEnoughMoney + ", notEnoughHunger=" + notEnoughHunger + ", notEnoughExp=" + notEnoughExp + ", cooldown=" + cooldown + ", movedOnTeleport=" + movedOnTeleport + ", teleportedOnTeleport=" + teleportedOnTeleport + ", damagedOnTeleport=" + damagedOnTeleport + ", damagedOtherOnTeleport=" + damagedOtherOnTeleport + ", failToFindLocation=" + failToFindLocation + "]";
  }
}
