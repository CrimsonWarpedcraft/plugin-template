package com.crimsonwarpedcraft.exampleplugin.listener;

import com.crimsonwarpedcraft.cwcommons.store.bukkit.PlayerDataManager;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerData;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Increments a player's stored creeper-kill count whenever they kill a creeper.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
public class CreeperKillListener implements Listener {

  private final PlayerDataManager<PlayerData> manager;
  private final Logger logger;

  /**
   * Creates a CreeperKillListener backed by the given per-player data store.
   *
   * @param manager stores each player's data
   * @param logger reports failed counter updates
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
      justification = "Use the plugin's shared logger so failures follow its logging settings")
  public CreeperKillListener(PlayerDataManager<PlayerData> manager, Logger logger) {
    this.manager = manager;
    this.logger = logger;
  }

  /**
   * Credits the killer with one more creeper kill, if a player gets credit for the kill.
   *
   * @param event the death event
   */
  @EventHandler
  public void onEntityDeath(EntityDeathEvent event) {
    if (event.getEntity().getType() != EntityType.CREEPER) {
      return;
    }

    Player killer = event.getEntity().getKiller();
    if (killer == null) {
      return;
    }

    UUID playerId = killer.getUniqueId();
    CompletableFuture<PlayerData> update;
    try {
      update = manager.update(killer, PlayerData::creditCreeperKill);
    } catch (RuntimeException failure) {
      update = CompletableFuture.failedFuture(failure);
    }
    update.exceptionally(failure -> {
      logger.log(Level.SEVERE, "Failed to update creeper kill count for " + playerId, failure);
      return null;
    });
  }
}
