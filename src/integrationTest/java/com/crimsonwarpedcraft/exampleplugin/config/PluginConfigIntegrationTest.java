package com.crimsonwarpedcraft.exampleplugin.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.crimsonwarpedcraft.cwcommons.config.ConfigManager;
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

    PluginConfig config = new ConfigManager().load(configFile, PluginConfig.class);

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
        () -> new ConfigManager().load(configFile.toFile(), PluginConfig.class));
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
}
