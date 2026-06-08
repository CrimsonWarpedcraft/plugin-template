package com.crimsonwarpedcraft.exampleplugin.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Executor for the /example ping subcommand.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
public class Ping implements CommandExecutor {

  @Override
  public void run(CommandSender sender, CommandArguments args)
      throws WrapperCommandSyntaxException {
    sender.sendMessage("Pong!");
  }
}