# Extending the Example Code

### Adding new top-level commands
To add an entirely new command (e.g. `/fly`), follow the `ExampleCommand` pattern:

1. Create a class extending `BaseCommand` (from [cw-commons](https://github.com/CrimsonWarpedcraft/cw-commons)); build the full `CommandAPICommand` tree in the constructor and pass it to `super(...)`
2. Call `.register()` on an instance of it in `ExamplePlugin.onEnable()`, alongside the existing `new ExampleCommand(config, creeperKillsManager, this).register()`
3. Add its permissions to `plugin.yml`. Do not add the command itself. CommandAPI registers it programmatically
4. Write unit tests for each executor class following the `PingTest`/`GreetTest` pattern

### Adding subcommands
To add a subcommand to `/example`, follow the `Ping`/`Greet` pattern:

1. Create a class implementing CommandAPI's `CommandExecutor`; inject any config values via the constructor
2. Add a `.withSubcommand(...)` call in `ExampleCommand`'s constructor
3. Write a unit test following `PingTest` or `GreetTest` — mock `CommandSender` and `CommandArguments`, call `run()`, verify `sendRichMessage()`
4. If the subcommand needs a config value, add it to `PluginConfig` and `config.yml`

### Adding new config fields
This project provides an example of loading config files using Jackson with Hibernate Validator,
via cw-commons' `Config` interface and `ConfigManager` (`com.crimsonwarpedcraft.cwcommons.config`).

To define your own config, add fields annotated with a Bean Validation constraint and a `@JsonProperty` YAML key to `PluginConfig` (`config/PluginConfig.java`):

```java
@NotBlank
@JsonProperty("my-message")
private String myMessage = "default";
```

Then add a getter. The config is validated upfront on every startup. If any constraint fails, the plugin logs the offending fields and disables itself cleanly.

Add a matching entry to `src/main/resources/config.yml` for every field you add, with a comment if desired:

```yaml
# Description of what the setting controls.
# Supports MiniMessage formatting: https://docs.advntr.dev/minimessage/format.html
my-message: "default"
```

Comments are preserved because `saveDefaultConfig()` writes this file once on first startup and never overwrites it. Schema migrations rewrite the file, but those are rare, and require custom code to handle.

### Adding persistent per-player data
cw-commons' `Repository`/`PlayerDataManager` stores any JSON-shaped data per player, not just counters. To add a new field, follow the `PlayerData`/`CreeperKillListener` pattern:

1. Add a field with a getter/setter to `PlayerData` (`data/PlayerData.java`)
2. Reuse the existing `Repository<UUID, PlayerData>`/`PlayerDataManager<PlayerData>` pair built once in `ExamplePlugin.onEnable()` — one repository backs every field on `PlayerData`, so adding a field never requires a new repository
3. Read and write it with `PlayerDataManager#get`/`#save`, e.g. from a `Listener` like `CreeperKillListener` or a command executor like `CreepersKilled`
4. Write unit tests mocking `PlayerDataManager`, following `CreeperKillListenerTest`/`CreepersKilledTest`
5. (Optional) Periodically flush data stores using `AutoFlushTask` to prevent session data loss from an unexpected shutdown.

Note: `PlayerDataManager` is just a `Player`-keyed convenience wrapper. For data that isn't tied to a specific player, call `DataStore#repository` directly with a different `KeySerializer` (e.g. `KeySerializers.forString()`) to get a standalone `Repository` for that data.
