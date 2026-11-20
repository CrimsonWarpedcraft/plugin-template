package com.crimsonwarpedcraft.exampleplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.crimsonwarpedcraft.cwcommons.config.bukkit.BukkitConfigManagerBuilder;
import com.crimsonwarpedcraft.cwcommons.store.DataStore;
import com.crimsonwarpedcraft.cwcommons.store.KeySerializers;
import com.crimsonwarpedcraft.cwcommons.store.Repository;
import com.crimsonwarpedcraft.cwcommons.store.bukkit.BukkitDataStoreBuilder;
import com.crimsonwarpedcraft.exampleplugin.config.PluginConfig;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerData;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

@Timeout(30)
class ShadedArtifactTest {

  private static final Path ARTIFACT = Path.of(System.getProperty("artifact.jar"));
  private static final String PACKAGE = System.getProperty("artifact.package");

  @Test
  void shipsExpandedMetadataAndRelocatedCommandApi() throws Exception {
    try (JarFile jar = new JarFile(ARTIFACT.toFile());
        InputStream yaml = openEntry(jar, "plugin.yml")) {
      var descriptor = new PluginDescriptionFile(yaml);
      assertEquals(System.getProperty("artifact.name"), descriptor.getName());
      assertEquals(System.getProperty("artifact.version"), descriptor.getVersion());
      assertEquals(System.getProperty("artifact.api"), descriptor.getAPIVersion());
      assertEquals(PACKAGE + "." + descriptor.getName(), descriptor.getMain());
      assertTrue(JavaPlugin.class.isAssignableFrom(classFromArtifact(descriptor.getMain())));
      classFromArtifact(PACKAGE + ".commandapi.CommandAPI");
      assertNull(jar.getEntry("dev/jorel/commandapi/CommandAPI.class"));
      assertNull(jar.getEntry("org/bukkit/plugin/java/JavaPlugin.class"));
    }
  }

  @Test
  void shipsServiceProvidersAndSqliteNatives() throws Exception {
    try (JarFile jar = new JarFile(ARTIFACT.toFile())) {
      assertProviders(jar, "java.sql.Driver", "org.sqlite.JDBC");
      assertProviders(jar, PACKAGE + ".jakartavalidation.spi.ValidationProvider",
          PACKAGE + ".hibernatevalidator.HibernateValidator");
      String jackson = PACKAGE + ".fasterxml.jackson.";
      assertProviders(jar, jackson + "core.JsonFactory", jackson + "core.JsonFactory",
          jackson + "dataformat.yaml.YAMLFactory");
      assertProviders(jar, jackson + "core.ObjectCodec", jackson + "databind.ObjectMapper",
          jackson + "dataformat.yaml.YAMLMapper");
      classFromArtifact(PACKAGE + ".snakeyaml.Yaml");
      var natives = List.of("Linux/x86_64/libsqlitejdbc.so", "Windows/x86_64/sqlitejdbc.dll");
      for (String path : natives) {
        JarEntry binary = jar.getJarEntry("org/sqlite/native/" + path);
        assertNotNull(binary, path);
        assertTrue(binary.getSize() > 0, path);
      }
    }
  }

  @Test
  void packagedConfigLoadsAndRejectsBlankValues(@TempDir Path dir) throws Exception {
    classFromArtifact(PluginConfig.class.getName());
    classFromArtifact(BukkitConfigManagerBuilder.class.getName());
    Path file = dir.resolve("config.yml");
    try (JarFile jar = new JarFile(ARTIFACT.toFile());
        InputStream yaml = openEntry(jar, "config.yml")) {
      Files.copy(yaml, file);
    }
    var manager = new BukkitConfigManagerBuilder().build();
    assertEquals("Pong!", manager.load(file.toFile(), PluginConfig.class).getPongMessage());
    Files.writeString(file, "pong-message: '   '\n");
    var failure = assertThrows(IllegalStateException.class,
        () -> manager.load(file.toFile(), PluginConfig.class));
    assertTrue(failure.getMessage().contains("pongMessage"), failure::getMessage);
  }

  @Test
  void sqlitePersistsPlayerDataAcrossReopen(@TempDir Path dir) throws Exception {
    classFromArtifact(BukkitDataStoreBuilder.class.getName());
    classFromArtifact(PlayerData.class.getName());
    UUID player = UUID.randomUUID();
    try (DataStore store = new BukkitDataStoreBuilder("artifact", dir.toFile()).build()) {
      var data = new PlayerData();
      data.setCreeperKills(17);
      players(store).put(player, data).get();
    }
    try (DataStore store = new BukkitDataStoreBuilder("artifact", dir.toFile()).build()) {
      assertEquals(17, players(store).get(player).get().orElseThrow().getCreeperKills());
    }
  }

  private static Repository<UUID, PlayerData> players(DataStore store) {
    return store.repository("players", PlayerData.class, KeySerializers.forUuid());
  }

  private static InputStream openEntry(JarFile jar, String path) throws IOException {
    JarEntry entry = jar.getJarEntry(path);
    assertNotNull(entry, path);
    return jar.getInputStream(entry);
  }

  private static void assertProviders(JarFile jar, String service, String... expected)
      throws Exception {
    try (InputStream input = openEntry(jar, "META-INF/services/" + service)) {
      List<String> lines = new String(input.readAllBytes(), StandardCharsets.UTF_8).lines()
          .map(String::strip).toList();
      for (String name : expected) {
        assertTrue(lines.contains(name), () -> "Missing provider " + name + " for " + service);
        classFromArtifact(name);
      }
    }
  }

  private static Class<?> classFromArtifact(String name) throws Exception {
    Class<?> type = Class.forName(name, false, ShadedArtifactTest.class.getClassLoader());
    Path origin = Path.of(type.getProtectionDomain().getCodeSource().getLocation().toURI());
    assertTrue(Files.isSameFile(ARTIFACT, origin), () -> name + " came from " + origin);
    return type;
  }
}
