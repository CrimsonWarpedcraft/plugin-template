package com.crimsonwarpedcraft.exampleplugin.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.crimsonwarpedcraft.cwcommons.store.bukkit.PlayerDataManager;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerData;
import dev.jorel.commandapi.executors.CommandArguments;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.ArgumentCaptor;

@Timeout(5)
class CreepersKilledTest {

  @SuppressWarnings("unchecked")
  private final PlayerDataManager<PlayerData> manager = mock(PlayerDataManager.class);
  private final Plugin plugin = mock(Plugin.class);
  private final Server server = mock(Server.class);
  private final BukkitScheduler scheduler = mock(BukkitScheduler.class);
  private final Logger logger = mock(Logger.class);
  private final Player player = mock(Player.class);
  private final UUID playerId = UUID.randomUUID();
  private final CommandArguments args = mock(CommandArguments.class);
  private final CompletableFuture<Optional<PlayerData>> read = new CompletableFuture<>();
  private CreepersKilled command;

  @BeforeEach
  void setUp() {
    when(plugin.getServer()).thenReturn(server);
    when(plugin.getLogger()).thenReturn(logger);
    when(plugin.isEnabled()).thenReturn(true);
    when(server.getScheduler()).thenReturn(scheduler);
    when(player.getUniqueId()).thenReturn(playerId);
    when(server.getPlayer(playerId)).thenReturn(player);
    when(manager.get(player)).thenReturn(read);
    command = new CreepersKilled(manager, plugin, "You have killed {count} creeper(s)!");
  }

  private Runnable scheduledReply() {
    var task = ArgumentCaptor.forClass(Runnable.class);
    verify(scheduler).runTask(eq(plugin), task.capture());
    return task.getValue();
  }

  @Test
  void reportsCreeperKillCount() {
    command.run(player, args);
    verifyNoInteractions(scheduler);
    PlayerData stored = new PlayerData();
    stored.setCreeperKills(5);
    read.complete(Optional.of(stored));
    verify(server, never()).getPlayer(playerId);
    verify(player, never()).sendRichMessage(anyString());

    scheduledReply().run();

    verify(player).sendRichMessage("You have killed 5 creeper(s)!");
  }

  @Test
  void newPlayerGetsZeroKills() {
    command.run(player, args);
    read.complete(Optional.empty());
    scheduledReply().run();
    verify(player).sendRichMessage("You have killed 0 creeper(s)!");
  }

  @Test
  void logsReadFailureAndSchedulesErrorReply() {
    command.run(player, args);
    var failure = new IOException("database unavailable");
    read.completeExceptionally(failure);
    verify(logger).log(Level.SEVERE, "Failed to read creeper kill count for " + playerId, failure);
    verify(player, never()).sendRichMessage(anyString());
    scheduledReply().run();
    verify(player).sendRichMessage("<red>Could not load your creeper kill count.</red>");
  }

  @Test
  void handlesSynchronousReadRejection() {
    var failure = new RejectedExecutionException("closing");
    when(manager.get(player)).thenThrow(failure);
    command.run(player, args);
    verify(logger).log(Level.SEVERE, "Failed to read creeper kill count for " + playerId, failure);
    scheduledReply().run();
    verify(player).sendRichMessage("<red>Could not load your creeper kill count.</red>");
  }

  @Test
  void readFailureAfterDisableStillLogsButDoesNotSchedule() {
    command.run(player, args);
    when(plugin.isEnabled()).thenReturn(false);
    var failure = new IOException("read failed");
    read.completeExceptionally(failure);
    verify(logger).log(Level.SEVERE, "Failed to read creeper kill count for " + playerId, failure);
    verifyNoInteractions(scheduler);
    verify(player, never()).sendRichMessage(anyString());
  }

  @Test
  void queuedReplySkipsPlayerLookupAfterDisable() {
    command.run(player, args);
    read.complete(Optional.empty());
    Runnable task = scheduledReply();
    when(plugin.isEnabled()).thenReturn(false);
    task.run();
    verify(server, never()).getPlayer(playerId);
    verify(player, never()).sendRichMessage(anyString());
  }

  @Test
  void skipsReplyWhenPlayerHasLeft() {
    command.run(player, args);
    read.complete(Optional.empty());
    when(server.getPlayer(playerId)).thenReturn(null);
    scheduledReply().run();
    verify(player, never()).sendRichMessage(anyString());
  }

  @Test
  void schedulingRejectionIsLoggedSeparatelyFromReadFailure() {
    var failure = new IllegalPluginAccessException("plugin disabled");
    when(scheduler.runTask(eq(plugin), any(Runnable.class))).thenThrow(failure);
    command.run(player, args);
    read.complete(Optional.empty());
    verify(logger).log(Level.WARNING, "Failed to schedule creeper kill count reply for " + playerId,
        failure);
    verify(player, never()).sendRichMessage(anyString());
  }

  @Test
  void rejectsNonPlayerSender() {
    CommandSender sender = mock(CommandSender.class);
    command.run(sender, args);

    verify(sender).sendRichMessage("<red>This command can only be used by players.</red>");
    verifyNoInteractions(manager);
  }
}
