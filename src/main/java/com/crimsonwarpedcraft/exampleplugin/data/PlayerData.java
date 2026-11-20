package com.crimsonwarpedcraft.exampleplugin.data;

import java.util.Optional;

/**
 * Holds per-player statistics tracked by the plugin.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
public class PlayerData {

  private int creeperKills;

  /** Creates a {@link PlayerData} with all stats defaulted to zero. */
  public PlayerData() {}

  /**
   * Credits one kill to the current stats, creating them on the player's first kill.
   *
   * @param current the value read by the store's atomic update
   * @return the stats to save
   */
  public static PlayerData creditCreeperKill(Optional<PlayerData> current) {
    PlayerData data = current.orElseGet(PlayerData::new);
    data.creeperKills++;
    return data;
  }

  /**
   * Returns the number of creepers this player has killed.
   *
   * @return the creeper-kill count
   */
  public int getCreeperKills() {
    return creeperKills;
  }

  /**
   * Sets the number of creepers this player has killed.
   *
   * @param creeperKills the new creeper-kill count
   */
  public void setCreeperKills(int creeperKills) {
    this.creeperKills = creeperKills;
  }
}
