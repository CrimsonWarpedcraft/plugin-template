package com.crimsonwarpedcraft.exampleplugin.command;

import com.crimsonwarpedcraft.cwcommons.command.BaseCommand;
import com.crimsonwarpedcraft.exampleplugin.config.PluginConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;

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
   * </ul>
   *
   * @param config the plugin configuration
   */
  public ExampleCommand(PluginConfig config) {
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
    );
  }

  @Override
  public ExampleCommand register() {
    super.register();

    return this;
  }
}
