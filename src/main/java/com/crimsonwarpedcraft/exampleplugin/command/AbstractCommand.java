package com.crimsonwarpedcraft.exampleplugin.command;

import dev.jorel.commandapi.CommandAPICommand;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;

/**
 * Base class for plugin commands backed by a {@link CommandAPICommand}.
 *
 * <p>Extend this class and pass a fully-configured {@link CommandAPICommand} to the constructor.
 * Call {@link #register()} in {@code onEnable()} to register the command with the server.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
@SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW",
        justification = "Internal class; Checkstyle NoFinalizer blocks the final-finalize fix.")
public class AbstractCommand implements Command {

  private final CommandAPICommand command;

  /**
   * Creates an AbstractCommand backed by the given pre-built command tree.
   *
   * @param command the CommandAPICommand to register
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
      justification = "CommandAPICommand cannot be defensively copied; caller owns it")
  public AbstractCommand(CommandAPICommand command) {
    this.command = Objects.requireNonNull(command);
  }

  @Override
  public AbstractCommand register() {
    command.register();
    return this;
  }
}
