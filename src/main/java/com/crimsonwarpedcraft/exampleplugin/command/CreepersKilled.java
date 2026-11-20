package com.crimsonwarpedcraft.exampleplugin.command;

import com.crimsonwarpedcraft.cwcommons.store.bukkit.PlayerDataManager;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerData;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Executor for the /example creepersKilled subcommand.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
public class CreepersKilled implements CommandExecutor {

  private final PlayerDataManager<PlayerData> manager;
  private final Plugin plugin;
  private final BukkitScheduler scheduler;
  private final Logger logger;
  private final String template;

  /**
   * Creates a CreepersKilled executor with the given data store and message template.
   *
   * @param manager stores each player's data
   * @param plugin the owning plugin, used to reschedule onto the main thread
   * @param template MiniMessage template; {count} is substituted with the sender's kill count
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
      justification = "Plugin is the owning JavaPlugin instance; caller owns it")
  public CreepersKilled(PlayerDataManager<PlayerData> manager, Plugin plugin, String template) {
    this.manager = manager;
    this.plugin = plugin;
    this.scheduler = plugin.getServer().getScheduler();
    this.logger = plugin.getLogger();
    this.template = template;
  }

  @Override
  public void run(CommandSender sender, CommandArguments args) {
    if (!(sender instanceof Player player)) {
      sender.sendRichMessage("<red>This command can only be used by players.</red>");
      return;
    }

    UUID playerId = player.getUniqueId();
    CompletableFuture<Optional<PlayerData>> read;
    try {
      read = manager.get(player);
    } catch (RuntimeException failure) {
      read = CompletableFuture.failedFuture(failure);
    }
    read.handle((data, failure) -> {
      if (failure != null) {
        logger.log(Level.SEVERE, "Failed to read creeper kill count for " + playerId, failure);
        reply(playerId, "<red>Could not load your creeper kill count.</red>");
      } else {
        int count = data.map(PlayerData::getCreeperKills).orElse(0);
        reply(playerId, template.replace("{count}", String.valueOf(count)));
      }
      return null;
    });
  }

  private void reply(UUID playerId, String message) {
    if (!plugin.isEnabled()) {
      return;
    }
    try {
      scheduler.runTask(plugin, () -> {
        if (plugin.isEnabled()) {
          Player online = plugin.getServer().getPlayer(playerId);
          if (online != null) {
            online.sendRichMessage(message);
          }
        }
      });
    } catch (RuntimeException failure) {
      // Disable can race with the pre-scheduling check.
      logger.log(Level.WARNING, "Failed to schedule creeper kill count reply for " + playerId,
          failure);
    }
  }
}
