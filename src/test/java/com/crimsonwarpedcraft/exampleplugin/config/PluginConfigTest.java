package com.crimsonwarpedcraft.exampleplugin.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PluginConfigTest {

  @Test
  void defaultsAreSet() {
    PluginConfig config = new PluginConfig();

    assertEquals("Pong!", config.getPongMessage());
    assertEquals("Hello, {player}!", config.getGreetMessage());
    assertEquals("You have killed {count} creeper(s)!", config.getCreepersKilledMessage());
  }
}
