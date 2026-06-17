package com.crimsonwarpedcraft.exampleplugin.command;

import com.crimsonwarpedcraft.cwcommons.store.bukkit.PlayerDataManager;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerData;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Executor for the /example creepersKilled subcommand.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
public class CreepersKilled implements CommandExecutor {

  private final PlayerDataManager<PlayerData> manager;
  private final Plugin plugin;
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
    this.template = template;
  }

  @Override
  public void run(CommandSender sender, CommandArguments args) {
    if (!(sender instanceof Player player)) {
      sender.sendRichMessage("<red>This command can only be used by players.</red>");
      return;
    }

    manager.get(player).thenAccept(data -> {
      int count = data.map(PlayerData::getCreeperKills).orElse(0);
      plugin.getServer().getScheduler().runTask(plugin, () ->
          sender.sendRichMessage(template.replace("{count}", String.valueOf(count))));
    });
  }
}
