package com.crimsonwarpedcraft.exampleplugin.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;
import org.bukkit.entity.Player;

/**
 * Registers and handles the /example command.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
public class ExampleCommand {

  private CommandAPICommand command;

  /**
   * Creates an ExampleCommand backed by the given pre-built command tree.
   *
   * @param command the CommandAPICommand to use for registration
   */
  private ExampleCommand(CommandAPICommand command) {
    this.command = Objects.requireNonNull(command);
  }

  /**
   * Creates an ExampleCommand with the default /example command tree.
   *
   * <p>Subcommands:
   *
   * <ul>
   *   <li>/example ping — replies with "Pong!"
   *   <li>/example greet &lt;player&gt; — greets the named player
   * </ul>
   */
  public ExampleCommand() {
    this(
        new CommandAPICommand("example")
            .withPermission("example.use")
            .withSubcommand(
                new CommandAPICommand("ping")
                    .executes(new Ping())
            )
            .withSubcommand(
                new CommandAPICommand("greet")
                    .withArguments(new EntitySelectorArgument.OnePlayer("target"))
                    .executes(new Greet())
            )
    );
  }

  /**
   * Registers the /example command with the server.
   *
   * @return this instance for chaining
   */
  public ExampleCommand register() {
    command.register();

    return this;
  }
}