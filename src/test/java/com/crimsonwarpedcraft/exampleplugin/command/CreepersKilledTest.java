package com.crimsonwarpedcraft.exampleplugin.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.crimsonwarpedcraft.cwcommons.store.bukkit.PlayerDataManager;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerData;
import dev.jorel.commandapi.executors.CommandArguments;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Test;

class CreepersKilledTest {

  @Test
  void reportsCreeperKillCount() {
    Plugin plugin = mock(Plugin.class);
    Server server = mock(Server.class);
    BukkitScheduler scheduler = mock(BukkitScheduler.class);
    when(plugin.getServer()).thenReturn(server);
    when(server.getScheduler()).thenReturn(scheduler);
    when(scheduler.runTask(eq(plugin), any(Runnable.class))).thenAnswer(invocation -> {
      Runnable task = invocation.getArgument(1);
      task.run();
      return null;
    });

    @SuppressWarnings("unchecked")
    PlayerDataManager<PlayerData> manager = mock(PlayerDataManager.class);
    PlayerData stored = new PlayerData();
    stored.setCreeperKills(5);
    Player player = mock(Player.class);
    when(manager.get(player)).thenReturn(CompletableFuture.completedFuture(Optional.of(stored)));

    CommandArguments args = mock(CommandArguments.class);

    new CreepersKilled(manager, plugin, "You have killed {count} creeper(s)!")
        .run(player, args);

    verify(player).sendRichMessage("You have killed 5 creeper(s)!");
  }

  @Test
  void rejectsNonPlayerSender() {
    @SuppressWarnings("unchecked")
    PlayerDataManager<PlayerData> manager = mock(PlayerDataManager.class);
    Plugin plugin = mock(Plugin.class);
    CommandSender sender = mock(CommandSender.class);
    CommandArguments args = mock(CommandArguments.class);

    new CreepersKilled(manager, plugin, "You have killed {count} creeper(s)!")
        .run(sender, args);

    verify(sender).sendRichMessage("<red>This command can only be used by players.</red>");
    verifyNoInteractions(manager);
  }
}
