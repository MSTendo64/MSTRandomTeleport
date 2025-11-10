package com.mstendo.rtp.channels.settings;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import com.mstendo.rtp.channels.Channel;
import com.mstendo.rtp.utils.economy.PlayerPointsUtils;

public final class Costs {
  private final Economy economy;
  private final MoneyType moneyType;
  private final double moneyCost;
  private final int hungerCost;
  private final int expCost;

  public Costs(Economy economy, MoneyType moneyType, double moneyCost, int hungerCost, int expCost) {
    this.economy = economy;
    this.moneyType = moneyType;
    this.moneyCost = moneyCost;
    this.hungerCost = hungerCost;
    this.expCost = expCost;
  }

  @Override
  public final String toString() {
    return "Costs[economy=" + this.economy + 
           ", moneyType=" + this.moneyType + 
           ", moneyCost=" + this.moneyCost + 
           ", hungerCost=" + this.hungerCost + 
           ", expCost=" + this.expCost + "]";
  }

  @Override
  public final int hashCode() {
    return java.util.Objects.hash(this.economy, this.moneyType, this.moneyCost, this.hungerCost, this.expCost);
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Costs costs = (Costs) o;
    return Double.compare(costs.moneyCost, this.moneyCost) == 0 &&
           this.hungerCost == costs.hungerCost &&
           this.expCost == costs.expCost &&
           java.util.Objects.equals(this.economy, costs.economy) &&
           this.moneyType == costs.moneyType;
  }

  public Economy economy() {
    return this.economy;
  }

  public MoneyType moneyType() {
    return this.moneyType;
  }

  public double moneyCost() {
    return this.moneyCost;
  }

  public int hungerCost() {
    return this.hungerCost;
  }

  public int expCost() {
    return this.expCost;
  }

  public boolean processMoneyCost(Player player, Channel channel) {
    if (this.moneyCost < 0.0D)
      return true;
    if (this.economy == null)
      return true;
    if (this.moneyType == MoneyType.PLAYER_POINTS) {
      if (PlayerPointsUtils.hasEnough(player, (int)this.moneyCost)) {
        PlayerPointsUtils.takePoints(player, (int)this.moneyCost);
        return true;
      }
      return false;
    }
    if (this.economy.has(player, this.moneyCost)) {
      this.economy.withdrawPlayer(player, this.moneyCost);
      return true;
    }
    return false;
  }

  public boolean processHungerCost(Player player, Channel channel) {
    if (this.hungerCost < 0)
      return true;
    if (player.getFoodLevel() >= this.hungerCost) {
      player.setFoodLevel(player.getFoodLevel() - this.hungerCost);
      return true;
    }
    return false;
  }

  public boolean processExpCost(Player player, Channel channel) {
    if (this.expCost < 0)
      return true;
    if (player.getLevel() >= this.expCost) {
      player.setLevel(player.getLevel() - this.expCost);
      return true;
    }
    return false;
  }

  public void processMoneyReturn(Player player) {
    if (this.moneyCost < 0.0D || this.economy == null)
      return;
    if (this.moneyType == MoneyType.PLAYER_POINTS) {
      PlayerPointsUtils.givePoints(player, (int)this.moneyCost);
      return;
    }
    this.economy.depositPlayer(player, this.moneyCost);
  }

  public void processHungerReturn(Player player) {
    if (this.hungerCost < 0)
      return;
    player.setFoodLevel(player.getFoodLevel() + this.hungerCost);
  }

  public void processExpReturn(Player player) {
    if (this.expCost < 0)
      return;
    player.setLevel(player.getLevel() + this.expCost);
  }

  public enum MoneyType {
    VAULT,
    PLAYER_POINTS
  }
}
