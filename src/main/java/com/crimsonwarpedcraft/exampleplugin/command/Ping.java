package com.crimsonwarpedcraft.exampleplugin.command;

import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Executor for the /example ping subcommand.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
public class Ping implements CommandExecutor {

  private final String message;

  /**
   * Creates a Ping executor with the given response message.
   *
   * @param message the MiniMessage-formatted reply sent to the command sender
   */
  public Ping(String message) {
    this.message = message;
  }

  @Override
  public void run(CommandSender sender, CommandArguments args) {
    sender.sendRichMessage(message);
  }
}
