package com.mstendo.rtp.configuration.data;

import java.util.Objects;

public final class PlaceholderMessages {
  private final String noCooldown;
  private final String noValue;
  
  public PlaceholderMessages(String noCooldown, String noValue) {
    this.noCooldown = noCooldown;
    this.noValue = noValue;
  }
  
  public String noCooldown() {
    return noCooldown;
  }
  
  public String noValue() {
    return noValue;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PlaceholderMessages that = (PlaceholderMessages) o;
    return Objects.equals(noCooldown, that.noCooldown) &&
           Objects.equals(noValue, that.noValue);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(noCooldown, noValue);
  }
  
  @Override
  public String toString() {
    return "PlaceholderMessages[noCooldown=" + noCooldown + ", noValue=" + noValue + "]";
  }
}
