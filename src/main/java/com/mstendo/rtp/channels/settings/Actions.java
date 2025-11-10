package com.mstendo.rtp.channels.settings;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Objects;
import com.mstendo.rtp.actions.Action;

public final class Actions {
  private final List<Action> preTeleportActions;
  private final Int2ObjectMap<List<Action>> onCooldownActions;
  private final List<Action> afterTeleportActions;
  
  public Actions(List<Action> preTeleportActions, Int2ObjectMap<List<Action>> onCooldownActions, List<Action> afterTeleportActions) {
    this.preTeleportActions = preTeleportActions;
    this.onCooldownActions = onCooldownActions;
    this.afterTeleportActions = afterTeleportActions;
  }
  
  public List<Action> preTeleportActions() {
    return preTeleportActions;
  }
  
  public Int2ObjectMap<List<Action>> onCooldownActions() {
    return onCooldownActions;
  }
  
  public List<Action> afterTeleportActions() {
    return afterTeleportActions;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Actions actions = (Actions) o;
    return Objects.equals(preTeleportActions, actions.preTeleportActions) &&
           Objects.equals(onCooldownActions, actions.onCooldownActions) &&
           Objects.equals(afterTeleportActions, actions.afterTeleportActions);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(preTeleportActions, onCooldownActions, afterTeleportActions);
  }
  
  @Override
  public String toString() {
    return "Actions[preTeleportActions=" + preTeleportActions + ", onCooldownActions=" + onCooldownActions + ", afterTeleportActions=" + afterTeleportActions + "]";
  }
}
