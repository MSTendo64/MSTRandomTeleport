package com.mstendo.rtp.configuration.data;

import java.util.Objects;

public final class CommandMessages {
  private final String incorrectChannel;
  private final String channelNotSpecified;
  private final String cancelled;
  private final String reload;
  private final String unknownArgument;
  private final String playerNotFound;
  private final String adminHelp;
  
  public CommandMessages(String incorrectChannel, String channelNotSpecified, String cancelled, String reload, String unknownArgument, String playerNotFound, String adminHelp) {
    this.incorrectChannel = incorrectChannel;
    this.channelNotSpecified = channelNotSpecified;
    this.cancelled = cancelled;
    this.reload = reload;
    this.unknownArgument = unknownArgument;
    this.playerNotFound = playerNotFound;
    this.adminHelp = adminHelp;
  }
  
  public String incorrectChannel() {
    return incorrectChannel;
  }
  
  public String channelNotSpecified() {
    return channelNotSpecified;
  }
  
  public String cancelled() {
    return cancelled;
  }
  
  public String reload() {
    return reload;
  }
  
  public String unknownArgument() {
    return unknownArgument;
  }
  
  public String playerNotFound() {
    return playerNotFound;
  }
  
  public String adminHelp() {
    return adminHelp;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CommandMessages that = (CommandMessages) o;
    return Objects.equals(incorrectChannel, that.incorrectChannel) &&
           Objects.equals(channelNotSpecified, that.channelNotSpecified) &&
           Objects.equals(cancelled, that.cancelled) &&
           Objects.equals(reload, that.reload) &&
           Objects.equals(unknownArgument, that.unknownArgument) &&
           Objects.equals(playerNotFound, that.playerNotFound) &&
           Objects.equals(adminHelp, that.adminHelp);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(incorrectChannel, channelNotSpecified, cancelled, reload, unknownArgument, playerNotFound, adminHelp);
  }
  
  @Override
  public String toString() {
    return "CommandMessages[incorrectChannel=" + incorrectChannel + ", channelNotSpecified=" + channelNotSpecified + ", cancelled=" + cancelled + ", reload=" + reload + ", unknownArgument=" + unknownArgument + ", playerNotFound=" + playerNotFound + ", adminHelp=" + adminHelp + "]";
  }
}
