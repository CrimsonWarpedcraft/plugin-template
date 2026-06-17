package com.crimsonwarpedcraft.exampleplugin.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.crimsonwarpedcraft.cwcommons.config.ConfigManager;
import org.junit.jupiter.api.Test;

class PluginConfigTest {

  @Test
  void defaultsAreSet() {
    PluginConfig config = new PluginConfig();

    assertEquals("Pong!", config.getPongMessage());
    assertEquals("Hello, {player}!", config.getGreetMessage());
    assertEquals("You have killed {count} creeper(s)!", config.getCreepersKilledMessage());
  }

  @Test
  void acceptsValidConfig() {
    PluginConfig config = new PluginConfig("Custom!", "Hello, {player}!");
    assertDoesNotThrow(() -> new ConfigManager().validate(config));
  }

  @Test
  void rejectsBlankPongMessage() {
    PluginConfig config = new PluginConfig("", "Hello, {player}!");
    assertThrows(IllegalStateException.class,
        () -> new ConfigManager().validate(config));
  }

  @Test
  void rejectsBlankGreetMessage() {
    PluginConfig config = new PluginConfig("Pong!", "");
    assertThrows(IllegalStateException.class,
        () -> new ConfigManager().validate(config));
  }

  @Test
  void rejectsBlankCreepersKilledMessage() {
    PluginConfig config = new PluginConfig("Pong!", "Hello, {player}!", "");
    assertThrows(IllegalStateException.class,
        () -> new ConfigManager().validate(config));
  }
}
