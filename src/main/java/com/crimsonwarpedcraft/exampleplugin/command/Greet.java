package com.crimsonwarpedcraft.exampleplugin.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executor for the /example greet subcommand.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
public class Greet implements CommandExecutor {

  @Override
  public void run(CommandSender sender, CommandArguments args)
      throws WrapperCommandSyntaxException {
    Player target = (Player) args.get("target");

    if (target == null) {
      throw CommandAPI.failWithString("No target specified!");
    }

    sender.sendMessage("Hello, " + target.getName() + "!");
  }
}
