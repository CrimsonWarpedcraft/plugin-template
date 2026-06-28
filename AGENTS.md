This repository is a **PaperMC/Spigot Minecraft plugin template**. Users fork or copy it, then replace the example scaffolding with their own plugin.

## Architecture

**Entry point**: `ExamplePlugin extends JavaPlugin`. Bukkit/Paper calls `onEnable()` and `onDisable()` during the plugin lifecycle. In `plugin.yml`, `${PACKAGE}.${NAME}` identifies the main class. The `processResources` task fills these placeholders at build time from `group` in `build.gradle.kts` and `rootProject.name` in `settings.gradle.kts`.

**JAR packaging**: The standard `jar` task is disabled, and `shadowJar` is the sole output. It shades CommandAPI and relocates it to `<group>.commandapi`. CommandAPI is excluded from `minimize()` because it loads classes through reflection. `assemble` depends on `shadowJar`.

**cw-commons dependency**: [cw-commons](https://github.com/CrimsonWarpedcraft/cw-commons) provides `Command`/`BaseCommand` for command registration and `Config`/`ConfigManager` for YAML loading and Jakarta validation. These classes are not defined locally. The project consumes cw-commons from JitPack as `com.github.CrimsonWarpedcraft:cw-commons`. `build.gradle.kts` pins a tagged release, such as `v0.1.0`, instead of the unstable `main-SNAPSHOT`. Update that tag deliberately.

Jackson and Hibernate Validator remain direct dependencies because `PluginConfig` uses their `@JsonProperty` and `@NotBlank` annotations. cw-commons exposes these libraries as transitive, unbundled `api` dependencies so each consumer can shade and relocate its own copy without classloader conflicts. The direct declarations are redundant for dependency resolution, but they prevent this project from relying on another project's transitive exposure choices.

**Command declaration**: CommandAPI registers commands programmatically in `onEnable()` (see `ExampleCommand`/`BaseCommand`). Adding a matching entry under `commands:` in `plugin.yml` makes Bukkit register the same command a second time, which CommandAPI flags at startup with a "Plugin command ... is registered by Bukkit" warning. `permissions:` entries are unaffected and still required.

**Versioning logic** (in `build.gradle.kts`):

- No `-Pver` supplied -> `yyMMdd-HHmm-SNAPSHOT`
- `-Pver=vX.Y.Z-RC-N` -> `X.Y.Z-RC-N-SNAPSHOT`
- `-Pver=vX.Y.Z` -> `X.Y.Z` (stable release)

**CI workflows** (`.github/workflows/`):

- `pr.yml`: builds and tests on Ubuntu + Windows for PRs and merge queue
- `main.yml`: builds, tests, and uploads a snapshot artifact on push to `main`
- `tag.yml` / `release.yml`: handle tagged releases and Discord notifications

## Agent instructions

1. Canonical skills live in `.agents/skills/`. The `.claude/skills/` directory is a generated mirror.
2. `CLAUDE.md` is a generated copy of this `AGENTS.md`.
3. Do not edit or create `CLAUDE.md` or files under `.claude/skills/`. Claude hooks configured in `.claude/settings.json` synchronize these mirrors on `SessionStart` and `PostToolUse`.
