package com.crimsonwarpedcraft.exampleplugin.data;

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
