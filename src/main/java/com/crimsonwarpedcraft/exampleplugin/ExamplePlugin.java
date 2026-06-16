package com.crimsonwarpedcraft.exampleplugin;

import com.crimsonwarpedcraft.cwcommons.config.ConfigManager;
import com.crimsonwarpedcraft.exampleplugin.command.ExampleCommand;
import com.crimsonwarpedcraft.exampleplugin.config.PluginConfig;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import io.papermc.lib.PaperLib;
import java.io.File;
import java.io.IOException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Levi Muniz on 7/29/20.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
public class ExamplePlugin extends JavaPlugin {

  @Override
  public void onLoad() {
    CommandAPI.onLoad(new CommandAPIPaperConfig(this));
  }

  @Override
  public void onEnable() {
    CommandAPI.onEnable();
    PaperLib.suggestPaper(this);
    saveDefaultConfig();
    PluginConfig config;

    try {
      config = new ConfigManager()
          .load(new File(getDataFolder(), "config.yml"), PluginConfig.class);
    } catch (IOException | IllegalStateException e) {
      getLogger().severe("Failed to load config: " + e.getMessage());
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    new ExampleCommand(config).register();
  }

  @Override
  public void onDisable() {
    CommandAPI.onDisable();
  }
}
