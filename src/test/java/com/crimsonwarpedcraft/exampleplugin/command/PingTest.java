package com.crimsonwarpedcraft.exampleplugin.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

class PingTest {

  @Test
  void sendsReply() throws WrapperCommandSyntaxException {
    CommandSender sender = mock(CommandSender.class);
    CommandArguments args = mock(CommandArguments.class);

    new Ping().run(sender, args);

    verify(sender).sendMessage("Pong!");
  }
}