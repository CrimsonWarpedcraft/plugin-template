This is a **PaperMC/Spigot Minecraft plugin template**. The intent is that users fork/copy it and replace the example scaffolding with their own plugin.

## Architecture

**Entry point**: `ExamplePlugin extends JavaPlugin` — Bukkit/Paper calls `onEnable()` and `onDisable()` on the plugin lifecycle. The main class is referenced in `plugin.yml` via the `${PACKAGE}.${NAME}` substitution, which is filled at build time by `processResources` from `group` (build.gradle.kts) and `rootProject.name` (settings.gradle.kts).

**JAR packaging**: The standard `jar` task is disabled. `shadowJar` is the sole output — it shades CommandAPI (relocated to `<group>.commandapi`). CommandAPI is excluded from `minimize()` because it loads classes via reflection. `assemble` depends on `shadowJar`.

**cw-commons dependency**: `Command`/`BaseCommand` (command registration) and `Config`/`ConfigManager` (YAML config loading + Jakarta validation) come from [cw-commons](https://github.com/CrimsonWarpedcraft/cw-commons), not local code — consumed via JitPack (`com.github.CrimsonWarpedcraft:cw-commons`). `build.gradle.kts` pins a tagged release (e.g. `v0.1.0`) rather than the unstable `main-SNAPSHOT`; bump that tag deliberately. Jackson and Hibernate Validator remain direct dependencies here because `PluginConfig` uses their annotations (`@JsonProperty`, `@NotBlank`) directly — cw-commons exposes them as `api` (transitive, unbundled) dependencies specifically so each consumer shades/relocates its own copy without classloader conflicts. Note that cw-commons' `api` deps already put Jackson/Hibernate Validator on this project's classpath transitively, so the explicit declarations here are redundant for resolution — kept anyway since `PluginConfig` references them directly and shouldn't rely on another project's transitive exposure choices.

**Command declaration**: CommandAPI registers commands programmatically in `onEnable()` (see `ExampleCommand`/`BaseCommand`). Adding a matching entry under `commands:` in `plugin.yml` makes Bukkit register the same command a second time, which CommandAPI flags at startup with a "Plugin command ... is registered by Bukkit" warning. `permissions:` entries are unaffected and still required.

**Versioning logic** (in `build.gradle.kts`):
- No `-Pver` supplied → `yyMMdd-HHmm-SNAPSHOT`
- `-Pver=vX.Y.Z-RC-N` → `X.Y.Z-SNAPSHOT`
- `-Pver=vX.Y.Z` → `X.Y.Z` (stable release)

**CI workflows** (`.github/workflows/`):
- `pr.yml` — builds and tests on Ubuntu + Windows for PRs and merge queue
- `main.yml` — builds, tests, and cuts a snapshot release on push to `main`
- `tag.yml` / `release.yml` — handle tagged releases and Discord notifications

**Agent files (skills + `CLAUDE.md`)**: Two Claude-Code-facing outputs are generated from tracked sources and gitignored. (1) Canonical skills live in `.agents/skills/` (shared with other agents); `.claude/skills/` is a **generated mirror** — Claude Code only discovers skills there. **Edit skills only in `.agents/skills/`; never edit the mirror.** (2) `CLAUDE.md` is a **generated copy of this `AGENTS.md`** — `AGENTS.md` is the single source of truth for project instructions; **edit `AGENTS.md`, never `CLAUDE.md`.** `.claude/hooks/sync-agent-files.sh` regenerates both; `.claude/settings.json` runs it on `SessionStart` (with `reloadSkills` so new skills load in-session) and on `PostToolUse` after edits under `.agents/skills/**` or to `AGENTS.md`. Run `sh .claude/hooks/sync-agent-files.sh` to regenerate manually.
