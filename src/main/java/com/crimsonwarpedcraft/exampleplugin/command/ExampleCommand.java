package com.crimsonwarpedcraft.exampleplugin.command;

import com.crimsonwarpedcraft.cwcommons.command.BaseCommand;
import com.crimsonwarpedcraft.cwcommons.store.bukkit.PlayerDataManager;
import com.crimsonwarpedcraft.exampleplugin.config.PluginConfig;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerData;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import org.bukkit.plugin.Plugin;

/**
 * Registers and handles the /example command.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
public class ExampleCommand extends BaseCommand {
  /**
   * Creates an ExampleCommand configured by the given plugin configuration.
   *
   * <p>Subcommands:
   *
   * <ul>
   *   <li>/example ping — replies with the configured pong message
   *   <li>/example greet &lt;player&gt; — greets the named player
   *   <li>/example creepersKilled — reports the sender's creeper-kill count
   * </ul>
   *
   * @param config the plugin configuration
   * @param creeperKillsManager stores each player's data
   * @param plugin the owning plugin, used to reschedule onto the main thread
   */
  public ExampleCommand(
      PluginConfig config, PlayerDataManager<PlayerData> creeperKillsManager, Plugin plugin) {
    super(
        new CommandAPICommand("example")
            .withPermission("example.use")
            .withSubcommand(
                new CommandAPICommand("ping")
                    .executes(new Ping(config.getPongMessage()))
            )
            .withSubcommand(
                new CommandAPICommand("greet")
                    .withArguments(new EntitySelectorArgument.OnePlayer("target"))
                    .executes(new Greet(config.getGreetMessage()))
            )
            .withSubcommand(
                new CommandAPICommand("creepersKilled")
                    .executes(new CreepersKilled(
                        creeperKillsManager, plugin, config.getCreepersKilledMessage()))
            )
    );
  }

  @Override
  public ExampleCommand register() {
    super.register();

    return this;
  }
}
