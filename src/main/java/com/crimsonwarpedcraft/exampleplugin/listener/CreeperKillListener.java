package com.crimsonwarpedcraft.exampleplugin.listener;

import com.crimsonwarpedcraft.cwcommons.store.bukkit.PlayerDataManager;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerData;
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

  /**
   * Creates a CreeperKillListener backed by the given per-player data store.
   *
   * @param manager stores each player's data
   */
  public CreeperKillListener(PlayerDataManager<PlayerData> manager) {
    this.manager = manager;
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

    manager.get(killer).thenCompose(data -> {
      PlayerData updated = data.orElseGet(PlayerData::new);
      updated.setCreeperKills(updated.getCreeperKills() + 1);
      return manager.save(killer, updated);
    });
  }
}
