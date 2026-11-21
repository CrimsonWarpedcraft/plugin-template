This repository is a **PaperMC Minecraft plugin template targeting Paper 26.3 and Java 25**. Users fork or copy it, then replace the example scaffolding with their own plugin. Spigot and Folia compatibility is not claimed.

## Architecture

**Entry point**: `ExamplePlugin extends JavaPlugin`. Bukkit/Paper calls `onEnable()` and `onDisable()` during the plugin lifecycle. In `plugin.yml`, `${PACKAGE}.${NAME}` identifies the main class. The `processResources` task fills these placeholders at build time from `group` in `build.gradle.kts` and `rootProject.name` in `settings.gradle.kts`.

**Support and dependency versions**: `gradle/libs.versions.toml` is the version source of truth. Its `minecraft` value supplies `supportedMinecraftVersion` in `build.gradle.kts`, driving `runServer` and the `${MINECRAFT_VERSION}` placeholder in `plugin.yml`. Keep the full stable `paper` API pin explicit and aligned with that baseline; compilation and all test suites use the same pin. All suites share the catalog's JUnit 6 version. `processResources` declares all expansion values as task inputs so changing `-Pver` refreshes metadata without a clean build.

**JAR packaging**: The standard `jar` task is disabled, and `shadowJar` is the sole output. It shades CommandAPI and relocates it to `<group>.commandapi`. CommandAPI is excluded from `minimize()` because it loads classes through reflection. `assemble` depends on `shadowJar`.

**cw-commons dependency**: [cw-commons](https://github.com/CrimsonWarpedcraft/cw-commons) provides `Command`/`BaseCommand` for command registration, `Config`/`BukkitConfigManagerBuilder` for YAML loading and Jakarta validation, and `BukkitDataStoreBuilder`/`Repository`/`PlayerDataManager` for persistent player data. These classes are not defined locally. `gradle/libs.versions.toml` pins the JitPack dependency `com.github.CrimsonWarpedcraft:cw-commons` to the tagged release `v0.4.0`. The template closes its one-shot config manager after startup loading and uses `PlayerDataManager.update` for atomic counter increments.

Jackson and Hibernate Validator remain direct dependencies because `PluginConfig` uses their `@JsonProperty` and `@NotBlank` annotations. cw-commons exposes these libraries as transitive, unbundled `api` dependencies so each consumer can shade and relocate its own copy without classloader conflicts. The direct declarations are redundant for dependency resolution, but they prevent this project from relying on another project's transitive exposure choices.

**Command declaration**: CommandAPI registers commands programmatically in `onEnable()` (see `ExampleCommand`/`BaseCommand`). Adding a matching entry under `commands:` in `plugin.yml` makes Bukkit register the same command a second time, which CommandAPI flags at startup with a "Plugin command ... is registered by Bukkit" warning. `permissions:` entries are unaffected and still required.

**Versioning logic** (in `build.gradle.kts`): tags version this template. The tag workflow passes
the tag as `-Pver` to set the example JAR's `plugin.yml` version. Forks choose their own tags.

- No `-Pver` supplied -> `yyMMdd-HHmm-SNAPSHOT`
- `-Pver=vX.Y.Z-RC-N` -> `X.Y.Z-RC-N-SNAPSHOT`
- `-Pver=vX.Y.Z` -> `X.Y.Z` (stable release)

**CI workflows** (`.github/workflows/`):

- `pr.yml`: builds and tests on Ubuntu + Windows for PRs and merge queue
- `main.yml`: builds, tests, and uploads a snapshot artifact on push to `main`
- `tag.yml` / `release.yml`: handle tagged releases and Discord notifications

The common setup action uses Java 25 and gives `setup-gradle@v6` sole ownership of Gradle caching
with `cache-provider: basic`. Do not also enable Gradle caching in `setup-java`.

**Test suites**: `test` contains isolated unit tests and may mock external boundaries.
`integrationTest` exercises the pinned cw-commons dependency directly, including configuration
loading and SQLite persistence. Integration tests use JUnit temporary directories for generated
files. Run this suite separately with `./gradlew integrationTest`.
`artifactTest` compiles and runs against the actual shaded JAR, JUnit and Paper's API dependencies.
Do not inherit implementation dependencies or add main output to it. Shadow's minimizer must use
only main classes as roots. Tag CI verifies the same release artifact on Ubuntu and Windows.

## Agent instructions

1. Canonical skills live in `.agents/skills/`. The `.claude/skills/` directory is a generated mirror.
2. `CLAUDE.md` is a generated copy of this `AGENTS.md`.
3. Do not edit or create `CLAUDE.md` or files under `.claude/skills/`. Claude hooks configured in `.claude/settings.json` synchronize these mirrors on `SessionStart` and `PostToolUse`.
4. On Windows, direct Gradle calls through the agent terminal have repeatedly hung, including after build completion. For **every Gradle invocation**, use the bounded, log-redirected procedure in `.agents/skills/run-plugin/SKILL.md`; do not run `gradlew.bat` directly in the foreground.
