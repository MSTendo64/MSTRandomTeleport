package com.mstendo.rtp.actions.impl;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.mstendo.rtp.actions.Action;
import com.mstendo.rtp.channels.Channel;
import com.mstendo.rtp.utils.Utils;

final class MessageAction implements Action {
  @NotNull
  private final String message;
  
  public static final String[] HOVER_MARKERS = new String[] { "hoverText={", "clickEvent={" };
  
  public MessageAction(@NotNull String message) {
    this.message = message;
  }
  
  @NotNull
  public String message() {
    return message;
  }
  
  public void perform(@NotNull Channel channel, @NotNull Player player, @NotNull String[] searchList, @NotNull String[] replacementList) {
    String messageToPlayer = Utils.replaceEach(this.message, searchList, replacementList);
    if (Utils.USE_PAPI)
      messageToPlayer = Utils.parsePlaceholders(messageToPlayer, player); 
    boolean hasAdvancedFormatting = (messageToPlayer.contains("button={") || messageToPlayer.contains("hoverText={") || messageToPlayer.contains("clickEvent={"));
    if (hasAdvancedFormatting) {
      Component component = parseMessage(messageToPlayer);
      try {
        // Try to use Adventure API directly
        player.getClass().getMethod("sendMessage", net.kyori.adventure.text.Component.class).invoke(player, component);
      } catch (Exception e) {
        // Fallback to string if Component not supported
        player.sendMessage(messageToPlayer);
      }
      return;
    } 
    player.sendMessage(messageToPlayer);
  }
  
  private Component parseMessage(String formattedMessage) {
    Component component;
    List<Component> components = new ArrayList<>();
    int currentIndex = 0;
    String globalHoverText = null;
    String globalClickEvent = null;
    while (currentIndex < formattedMessage.length()) {
      int buttonStart = formattedMessage.indexOf("button={", currentIndex);
      if (buttonStart == -1) {
        String remainingText = formattedMessage.substring(currentIndex);
        if (!remainingText.isEmpty()) {
          globalHoverText = extractValue(remainingText, "hoverText={");
          globalClickEvent = extractValue(remainingText, "clickEvent={");
          remainingText = extractMessage(remainingText);
          if (!remainingText.isEmpty())
            components.add(LegacyComponentSerializer.legacySection().deserialize(remainingText)); 
        } 
        break;
      } 
      if (buttonStart > currentIndex) {
        String beforeButton = formattedMessage.substring(currentIndex, buttonStart);
        globalHoverText = extractValue(beforeButton, "hoverText={");
        globalClickEvent = extractValue(beforeButton, "clickEvent={");
        beforeButton = extractMessage(beforeButton);
        if (!beforeButton.isEmpty())
          components.add(LegacyComponentSerializer.legacySection().deserialize(beforeButton)); 
      } 
      int buttonEnd = findClosingBracket(formattedMessage, buttonStart + "button={".length());
      if (buttonEnd == -1)
        throw new IllegalArgumentException("Некорректный формат кнопки: отсутствует закрывающая }"); 
      String buttonContent = formattedMessage.substring(buttonStart + "button={".length(), buttonEnd);
      Component buttonComponent = parseButtonContent(buttonContent);
      boolean hasLeadingSpace = (buttonStart > 0 && formattedMessage.charAt(buttonStart - 1) == ' ');
      boolean hasTrailingSpace = (buttonEnd + 1 < formattedMessage.length() && formattedMessage.charAt(buttonEnd + 1) == ' ');
      if (hasLeadingSpace)
        components.add(Component.text(" ")); 
      components.add(buttonComponent);
      if (hasTrailingSpace)
        components.add(Component.text(" ")); 
      currentIndex = buttonEnd + 1;
    } 
    BuildableComponent buildableComponent = ((TextComponent.Builder)Component.text().append(components)).build();
    component = (Component)buildableComponent;
    if (globalHoverText != null)
      component = createHoverEvent(component, globalHoverText); 
    if (globalClickEvent != null)
      component = createClickEvent(component, globalClickEvent); 
    return component;
  }
  
  private int findClosingBracket(String message, int startIndex) {
    int depth = 0;
    for (int i = startIndex; i < message.length(); i++) {
      char currentChar = message.charAt(i);
      if (currentChar == '{') {
        depth++;
      } else if (currentChar == '}') {
        if (depth == 0)
          return i; 
        depth--;
      } 
    } 
    return -1;
  }
  
  private Component parseButtonContent(String buttonContent) {
    Component component;
    String buttonText = null;
    String hoverText = null;
    String clickEvent = null;
    List<String> parts = getParts(buttonContent);
    for (int i = 0; i < parts.size(); i++) {
      String part = parts.get(i);
      if (part.startsWith("hoverText={")) {
        hoverText = extractValue(part, "hoverText={");
      } else if (part.startsWith("clickEvent={")) {
        clickEvent = extractValue(part, "clickEvent={");
      } else if (buttonText == null) {
        buttonText = part;
      } else {
        throw new IllegalArgumentException("Некорректный формат кнопки: несколько текстовых частей.");
      } 
    } 
    if (buttonText == null || buttonText.isEmpty())
      throw new IllegalArgumentException("Кнопка должна содержать текст."); 
    TextComponent textComponent = LegacyComponentSerializer.legacySection().deserialize(buttonText);
    component = (Component)textComponent;
    if (hoverText != null)
      component = createHoverEvent(component, hoverText); 
    if (clickEvent != null)
      component = createClickEvent(component, clickEvent); 
    return component;
  }
  
  private List<String> getParts(String buttonContent) {
    List<String> parts = new ArrayList<>();
    int start = 0;
    int depth = 0;
    for (int i = 0; i < buttonContent.length(); i++) {
      char c = buttonContent.charAt(i);
      if (c == '{') {
        depth++;
      } else if (c == '}') {
        depth--;
      } else if (c == ';' && depth == 0) {
        parts.add(buttonContent.substring(start, i).trim());
        start = i + 1;
      } 
    } 
    parts.add(buttonContent.substring(start).trim());
    return parts;
  }
  
  private String extractValue(String message, String prefix) {
    int startIndex = message.indexOf(prefix);
    if (startIndex != -1) {
      startIndex += prefix.length();
      int endIndex = findClosingBracket(message, startIndex);
      if (endIndex != -1)
        return message.substring(startIndex, endIndex); 
    } 
    return null;
  }
  
  private String extractMessage(String message) {
    String baseMessage = getBaseMessage(message);
    for (int i = 0; i < HOVER_MARKERS.length; i++) {
      String marker = HOVER_MARKERS[i];
      int startIndex = message.indexOf(marker);
      if (startIndex != -1) {
        int endIndexMarker = findClosingBracket(message, startIndex + marker.length() - 1);
        if (endIndexMarker != -1)
          message = message.substring(0, startIndex).trim() + " " + message.substring(0, startIndex).trim(); 
      } 
    } 
    return baseMessage.trim();
  }
  
  private String getBaseMessage(String message) {
    IntArrayList intArrayList = new IntArrayList();
    for (int i = 0; i < HOVER_MARKERS.length; i++) {
      String marker = HOVER_MARKERS[i];
      int index = message.indexOf(marker);
      if (index != -1)
        intArrayList.add(index); 
    } 
    int endIndex = intArrayList.isEmpty() ? message.length() : ((Integer)Collections.<Integer>min((Collection<? extends Integer>)intArrayList)).intValue();
    return message.substring(0, endIndex).trim();
  }
  
  private Component createHoverEvent(Component message, String hoverText) {
    HoverEvent<Component> hover = HoverEvent.showText((Component)LegacyComponentSerializer.legacySection().deserialize(hoverText));
    return message.hoverEvent((HoverEventSource)hover);
  }
  
  private Component createClickEvent(Component message, String clickEvent) {
    int separatorIndex = clickEvent.indexOf(';');
    if (separatorIndex == -1)
      throw new IllegalArgumentException("Некорректный формат clickEvent: отсутствует разделитель ';'"); 
    String actionStr = clickEvent.substring(0, separatorIndex).trim();
    String context = clickEvent.substring(separatorIndex + 1).trim();
    ClickEvent.Action action = ClickEvent.Action.valueOf(actionStr.toUpperCase(Locale.ENGLISH));
    ClickEvent click = ClickEvent.clickEvent(action, context);
    return message.clickEvent(click);
  }
}
