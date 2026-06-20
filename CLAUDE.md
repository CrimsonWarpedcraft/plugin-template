# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

**OneDrive locking**: If the project resides in OneDrive, the build fails with `Unable to delete directory '...\build\test-results\test\binary'`, delete that directory manually before retrying — OneDrive holds a sync lock on it.

```bash
# Build (runs Checkstyle, SpotBugs, and tests)
./gradlew build

# Run tests only
./gradlew test

# Run a single test class
./gradlew test --tests "com.crimsonwarpedcraft.exampleplugin.command.PingTest"

# Run a single test method
./gradlew test --tests "com.crimsonwarpedcraft.exampleplugin.command.GreetTest.greetsTarget"

# Build a release JAR (strips version from filename for stable tags)
./gradlew -Pver="X.Y.Z" release

# Output is in build/libs/
```

Checkstyle enforces Google Java style with `maxWarnings = 0` — the build fails on any warning. SpotBugs runs FindSecBugs. Both run as part of `build`; fix all findings before committing.

## Architecture

This is a **PaperMC/Spigot Minecraft plugin template**. The intent is that users fork/copy it and replace the example scaffolding with their own plugin.

**Entry point**: `ExamplePlugin extends JavaPlugin` — Bukkit/Paper calls `onEnable()` and `onDisable()` on the plugin lifecycle. The main class is referenced in `plugin.yml` via the `${PACKAGE}.${NAME}` substitution, which is filled at build time by `processResources` from `group` (build.gradle.kts) and `rootProject.name` (settings.gradle.kts).

**JAR packaging**: The standard `jar` task is disabled. `shadowJar` is the sole output — it shades CommandAPI (relocated to `<group>.commandapi`). CommandAPI is excluded from `minimize()` because it loads classes via reflection. `assemble` depends on `shadowJar`.

**cw-commons dependency**: `Command`/`BaseCommand` (command registration) and `Config`/`ConfigManager` (YAML config loading + Jakarta validation) come from [cw-commons](https://github.com/CrimsonWarpedcraft/cw-commons), not local code — consumed via JitPack (`com.github.CrimsonWarpedcraft:cw-commons`). `build.gradle.kts` pins a tagged release (e.g. `v0.1.0`) rather than the unstable `main-SNAPSHOT`; bump that tag deliberately. Jackson and Hibernate Validator remain direct dependencies here because `PluginConfig` uses their annotations (`@JsonProperty`, `@NotBlank`) directly — cw-commons exposes them as `api` (transitive, unbundled) dependencies specifically so each consumer shades/relocates its own copy without classloader conflicts. Note that cw-commons' `api` deps already put Jackson/Hibernate Validator on this project's classpath transitively, so the explicit declarations here are redundant for resolution — kept anyway since `PluginConfig` references them directly and shouldn't rely on another project's transitive exposure choices.

**Commands are not declared in `plugin.yml`**: CommandAPI registers commands programmatically in `onEnable()` (see `ExampleCommand`/`BaseCommand`). Adding a matching entry under `commands:` in `plugin.yml` makes Bukkit register the same command a second time, which CommandAPI flags at startup with a "Plugin command ... is registered by Bukkit" warning. `permissions:` entries are unaffected and still required.

**Versioning logic** (in `build.gradle.kts`):
- No `-Pver` supplied → `yyMMdd-HHmm-SNAPSHOT`
- `-Pver=vX.Y.Z-RC-N` → `X.Y.Z-SNAPSHOT`
- `-Pver=vX.Y.Z` → `X.Y.Z` (stable release)

**CI workflows** (`.github/workflows/`):
- `pr.yml` — builds and tests on Ubuntu + Windows for PRs and merge queue
- `main.yml` — builds, tests, and cuts a snapshot release on push to `main`
- `tag.yml` / `release.yml` — handle tagged releases and Discord notifications

## Testing

Command executor unit tests (`Ping`, `Greet`, etc.) use Mockito directly — mock `CommandArguments` and `CommandSender`/`Player`, then call `run()`. No server or plugin lifecycle needed. Mockito must be declared explicitly as `testImplementation 'org.mockito:mockito-core:...'` — it is not provided transitively.

## Template Customization Checklist

When adapting this template for a real plugin, update:
1. `settings.gradle.kts` — `rootProject.name`
2. `build.gradle.kts` — `group` (Java package)
3. `src/main/resources/plugin.yml` — `author`, `description`, `commands`, `permissions`
4. Rename the Java package and source directory from `com.crimsonwarpedcraft.exampleplugin`
5. `.github/dependabot.yml`, `.github/CODEOWNERS`, `.github/FUNDING.yml` — replace `leviem1`
6. `CODE_OF_CONDUCT.md` line 63 — contact method
7. README badges and Discord invite link