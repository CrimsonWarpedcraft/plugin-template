package com.crimsonwarpedcraft.exampleplugin;

import com.crimsonwarpedcraft.cwcommons.config.ConfigManager;
import com.crimsonwarpedcraft.cwcommons.store.DataStore;
import com.crimsonwarpedcraft.cwcommons.store.KeySerializers;
import com.crimsonwarpedcraft.cwcommons.store.Repository;
import com.crimsonwarpedcraft.cwcommons.store.bukkit.AutoFlushTask;
import com.crimsonwarpedcraft.cwcommons.store.bukkit.PlayerDataManager;
import com.crimsonwarpedcraft.exampleplugin.command.ExampleCommand;
import com.crimsonwarpedcraft.exampleplugin.config.PluginConfig;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerData;
import com.crimsonwarpedcraft.exampleplugin.listener.CreeperKillListener;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Created by Levi Muniz on 7/29/20.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
public class ExamplePlugin extends JavaPlugin {

  private DataStore store;
  private BukkitTask autoFlushTask;

  @Override
  public void onLoad() {
    CommandAPI.onLoad(new CommandAPIPaperConfig(this));
  }

  @Override
  public void onEnable() {
    CommandAPI.onEnable();
    suggestPaper();
    saveDefaultConfig();
    PluginConfig config;

    // Load the configuration settings
    try {
      config = new ConfigManager()
          .load(new File(getDataFolder(), "config.yml"), PluginConfig.class);
    } catch (IOException | IllegalStateException e) {
      getLogger().severe("Failed to load config: " + e.getMessage());
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    // Setup persistent storage
    try {
      store = DataStore.getLocalDataStore(getName(), getDataFolder());
    } catch (IOException e) {
      getLogger().severe("Failed to open data store: " + e.getMessage());
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    autoFlushTask = new AutoFlushTask(store, this).start();

    Repository<UUID, PlayerData> playerDataRepository =
        store.repository("player-data", PlayerData.class, KeySerializers.forUuid());
    PlayerDataManager<PlayerData> creeperKillsManager =
        new PlayerDataManager<>(playerDataRepository, this).registerEvents();

    // Register event listener for creeper kills
    getServer().getPluginManager()
        .registerEvents(new CreeperKillListener(creeperKillsManager), this);

    // Set up in-game /example command
    new ExampleCommand(config, creeperKillsManager, this).register();
  }

  @Override
  public void onDisable() {
    if (autoFlushTask != null) {
      autoFlushTask.cancel();
    }

    if (store != null) {
      try {
        store.close();
      } catch (Exception e) {
        getLogger().severe("Failed to close data store: " + e.getMessage());
      }
    }

    CommandAPI.onDisable();
  }

  private void suggestPaper() {
    if (isPaper()) {
      return;
    }

    getLogger().warning(getName() + " recommends using Paper.");
  }

  private boolean isPaper() {
    try {
      Class.forName("io.papermc.paper.ServerBuildInfo");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}
