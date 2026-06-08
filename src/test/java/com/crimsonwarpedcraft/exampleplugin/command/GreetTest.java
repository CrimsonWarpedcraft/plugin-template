package com.crimsonwarpedcraft.exampleplugin.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class GreetTest {

  @Test
  void greetsTarget() throws WrapperCommandSyntaxException {
    Player target = mock(Player.class);
    when(target.getName()).thenReturn("Alex");
    CommandArguments args = mock(CommandArguments.class);
    when(args.get("target")).thenReturn(target);
    CommandSender sender = mock(CommandSender.class);

    new Greet("Hello, {player}!").run(sender, args);

    verify(sender).sendRichMessage("Hello, Alex!");
  }

  @Test
  void throwsWhenTargetIsNull() {
    CommandSender sender = mock(CommandSender.class);
    CommandArguments args = mock(CommandArguments.class);
    when(args.get("target")).thenReturn(null);

    assertThrows(WrapperCommandSyntaxException.class,
        () -> new Greet("Hello, {player}!").run(sender, args));

    verify(sender, never()).sendRichMessage(anyString());
  }
}
