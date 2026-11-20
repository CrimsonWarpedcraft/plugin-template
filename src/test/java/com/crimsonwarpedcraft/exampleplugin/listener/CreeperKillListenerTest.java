package com.crimsonwarpedcraft.exampleplugin.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.crimsonwarpedcraft.cwcommons.store.bukkit.PlayerDataManager;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerData;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.ArgumentCaptor;

@Timeout(5)
class CreeperKillListenerTest {

  @SuppressWarnings("unchecked")
  private final PlayerDataManager<PlayerData> manager = mock(PlayerDataManager.class);
  private final Logger logger = mock(Logger.class);
  private final Player killer = mock(Player.class);
  private final UUID playerId = UUID.randomUUID();
  private final LivingEntity mob = mock(LivingEntity.class);
  private final EntityDeathEvent event = mock(EntityDeathEvent.class);
  private final CreeperKillListener listener = new CreeperKillListener(manager, logger);

  @BeforeEach
  void setUp() {
    when(event.getEntity()).thenReturn(mob);
    when(mob.getType()).thenReturn(EntityType.CREEPER);
    when(mob.getKiller()).thenReturn(killer);
    when(killer.getUniqueId()).thenReturn(playerId);
  }

  @Test
  void submitsBothKillsBeforeEitherWriteIsAcknowledged() {
    when(manager.update(eq(killer), any())).thenReturn(new CompletableFuture<>())
        .thenReturn(new CompletableFuture<>());

    listener.onEntityDeath(event);
    listener.onEntityDeath(event);

    ArgumentCaptor<Function<Optional<PlayerData>, PlayerData>> credits = ArgumentCaptor.captor();
    verify(manager, times(2)).update(eq(killer), credits.capture());
    verifyNoMoreInteractions(manager);
    PlayerData first = credits.getAllValues().getFirst().apply(Optional.empty());
    assertEquals(1, first.getCreeperKills());
    PlayerData second = credits.getAllValues().getLast().apply(Optional.of(first));
    assertEquals(2, second.getCreeperKills());
  }

  @Test
  void logsDeferredWriteFailureWithoutTouchingPlayer() {
    var write = new CompletableFuture<PlayerData>();
    when(manager.update(eq(killer), any())).thenReturn(write);
    listener.onEntityDeath(event);
    verifyNoInteractions(logger);
    clearInvocations(killer);

    var failure = new IOException("disk unavailable");
    write.completeExceptionally(failure);

    verify(logger).log(Level.SEVERE,
        "Failed to update creeper kill count for " + playerId, failure);
    verifyNoInteractions(killer);
  }

  @Test
  void logsSynchronousUpdateRejection() {
    var failure = new RejectedExecutionException("closing");
    when(manager.update(eq(killer), any())).thenThrow(failure);
    listener.onEntityDeath(event);
    verify(logger).log(Level.SEVERE,
        "Failed to update creeper kill count for " + playerId, failure);
  }

  @Test
  void ignoresNonCreeperDeaths() {
    when(mob.getType()).thenReturn(EntityType.ZOMBIE);
    listener.onEntityDeath(event);

    verifyNoInteractions(manager);
  }

  @Test
  void ignoresCreeperDeathWithNoKiller() {
    when(mob.getKiller()).thenReturn(null);
    listener.onEntityDeath(event);

    verifyNoInteractions(manager);
  }
}
