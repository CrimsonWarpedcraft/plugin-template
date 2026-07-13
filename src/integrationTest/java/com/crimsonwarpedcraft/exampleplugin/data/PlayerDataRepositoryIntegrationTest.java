package com.crimsonwarpedcraft.exampleplugin.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.crimsonwarpedcraft.cwcommons.store.DataStore;
import com.crimsonwarpedcraft.cwcommons.store.KeySerializers;
import com.crimsonwarpedcraft.cwcommons.store.Repository;
import com.crimsonwarpedcraft.cwcommons.store.bukkit.BukkitDataStoreBuilder;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PlayerDataRepositoryIntegrationTest {

  private static final String STORE_NAME = "integration-test";
  private static final String NAMESPACE = "player-data";

  @TempDir
  private Path tempDirectory;

  @Test
  void persistsPlayerDataAcrossStoreInstances() throws Exception {
    UUID playerId = UUID.randomUUID();
    PlayerData playerData = new PlayerData();
    playerData.setCreeperKills(7);

    try (DataStore store = openStore()) {
      repository(store).put(playerId, playerData).join();
    }

    try (DataStore store = openStore()) {
      Optional<PlayerData> restored = repository(store).get(playerId).join();

      assertTrue(restored.isPresent());
      assertEquals(7, restored.orElseThrow().getCreeperKills());
    }
  }

  private DataStore openStore() throws Exception {
    return new BukkitDataStoreBuilder(STORE_NAME, tempDirectory.toFile()).build();
  }

  private Repository<UUID, PlayerData> repository(DataStore store) {
    return store.repository(NAMESPACE, PlayerData.class, KeySerializers.forUuid());
  }
}
