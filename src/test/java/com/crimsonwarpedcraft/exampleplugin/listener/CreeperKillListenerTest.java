package com.crimsonwarpedcraft.exampleplugin.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.crimsonwarpedcraft.cwcommons.store.bukkit.PlayerDataManager;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerData;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CreeperKillListenerTest {

  @Test
  void incrementsCountOnCreeperKill() {
    Player killer = mock(Player.class);
    LivingEntity creeper = mock(LivingEntity.class);
    when(creeper.getType()).thenReturn(EntityType.CREEPER);
    when(creeper.getKiller()).thenReturn(killer);
    PlayerData existing = new PlayerData();
    existing.setCreeperKills(3);
    @SuppressWarnings("unchecked")
    PlayerDataManager<PlayerData> manager = mock(PlayerDataManager.class);
    when(manager.get(killer)).thenReturn(CompletableFuture.completedFuture(Optional.of(existing)));
    when(manager.save(eq(killer), any(PlayerData.class)))
        .thenReturn(CompletableFuture.completedFuture(null));
    EntityDeathEvent event = mock(EntityDeathEvent.class);
    when(event.getEntity()).thenReturn(creeper);

    new CreeperKillListener(manager).onEntityDeath(event);

    ArgumentCaptor<PlayerData> captor = ArgumentCaptor.forClass(PlayerData.class);
    verify(manager).save(eq(killer), captor.capture());
    assertEquals(4, captor.getValue().getCreeperKills());
  }

  @Test
  void ignoresNonCreeperDeaths() {
    @SuppressWarnings("unchecked")
    PlayerDataManager<PlayerData> manager = mock(PlayerDataManager.class);
    LivingEntity zombie = mock(LivingEntity.class);
    when(zombie.getType()).thenReturn(EntityType.ZOMBIE);
    EntityDeathEvent event = mock(EntityDeathEvent.class);
    when(event.getEntity()).thenReturn(zombie);

    new CreeperKillListener(manager).onEntityDeath(event);

    verifyNoInteractions(manager);
  }

  @Test
  void ignoresCreeperDeathWithNoKiller() {
    LivingEntity creeper = mock(LivingEntity.class);
    when(creeper.getType()).thenReturn(EntityType.CREEPER);
    when(creeper.getKiller()).thenReturn(null);
    EntityDeathEvent event = mock(EntityDeathEvent.class);
    when(event.getEntity()).thenReturn(creeper);

    @SuppressWarnings("unchecked")
    PlayerDataManager<PlayerData> manager = mock(PlayerDataManager.class);
    new CreeperKillListener(manager).onEntityDeath(event);

    verifyNoInteractions(manager);
  }
}
