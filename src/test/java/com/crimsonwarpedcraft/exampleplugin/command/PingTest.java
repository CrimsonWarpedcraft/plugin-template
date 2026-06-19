package com.crimsonwarpedcraft.exampleplugin.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

class PingTest {

  @Test
  void sendsReply() {
    CommandSender sender = mock(CommandSender.class);
    CommandArguments args = mock(CommandArguments.class);

    new Ping("Pong!").run(sender, args);

    verify(sender).sendRichMessage("Pong!");
  }
}
