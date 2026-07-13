package com.crimsonwarpedcraft.exampleplugin.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.crimsonwarpedcraft.cwcommons.config.bukkit.BukkitConfigManagerBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PluginConfigIntegrationTest {

  @TempDir
  private Path tempDirectory;

  @Test
  void loadsDefaultConfigWithCwCommons() throws IOException {
    File configFile = copyDefaultConfig();

    PluginConfig config = manager().load(configFile, PluginConfig.class);

    assertEquals("Pong!", config.getPongMessage());
    assertEquals("Hello, {player}!", config.getGreetMessage());
    assertEquals("You have killed {count} creeper(s)!", config.getCreepersKilledMessage());
  }

  @Test
  void rejectsInvalidConfigWithCwCommons() throws IOException {
    Path configFile = tempDirectory.resolve("config.yml");
    Files.writeString(configFile, "pong-message: ''\n"
        + "greet-message: ''\n"
        + "creepers-killed-message: ''\n");

    assertThrows(IllegalStateException.class,
        () -> manager().load(configFile.toFile(), PluginConfig.class));
  }

  @Test
  void acceptsValidConfigWithCwCommons() {
    PluginConfig config = new PluginConfig("Custom!", "Hello, {player}!");

    assertDoesNotThrow(() -> manager().validate(config));
  }

  @Test
  void rejectsBlankPongMessageWithCwCommons() {
    PluginConfig config = new PluginConfig("", "Hello, {player}!");

    assertThrows(IllegalStateException.class, () -> manager().validate(config));
  }

  @Test
  void rejectsBlankGreetMessageWithCwCommons() {
    PluginConfig config = new PluginConfig("Pong!", "");

    assertThrows(IllegalStateException.class, () -> manager().validate(config));
  }

  @Test
  void rejectsBlankCreepersKilledMessageWithCwCommons() {
    PluginConfig config = new PluginConfig("Pong!", "Hello, {player}!", "");

    assertThrows(IllegalStateException.class, () -> manager().validate(config));
  }

  private File copyDefaultConfig() throws IOException {
    Path configFile = tempDirectory.resolve("config.yml");
    try (var config = getClass().getResourceAsStream("/config.yml")) {
      if (config == null) {
        throw new IOException("Default config.yml was not found");
      }
      Files.copy(config, configFile);
    }
    return configFile.toFile();
  }

  private static com.crimsonwarpedcraft.cwcommons.config.ConfigManager manager() {
    return new BukkitConfigManagerBuilder().build();
  }
}
