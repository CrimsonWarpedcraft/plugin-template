# PaperMC/Spigot Minecraft Server Plugin Template
A template for building PaperMC/Spigot Minecraft server plugins!

<!-- TODO: CHANGE ME -->
[![](https://github.com/CrimsonWarpedcraft/plugin-template/actions/workflows/main.yml/badge.svg)](https://github.com/CrimsonWarpedcraft/plugin-template/actions/workflows/main.yml)

<!-- TODO: CHANGE ME -->
[![](https://dcbadge.limes.pink/api/server/5XMmeV6EtJ)](https://discord.gg/5XMmeV6EtJ)

## Features
### Github Actions 🎬
* Automated builds, testing, and release drafting
* [Discord notifcations](https://github.com/marketplace/actions/discord-message-notify) for snapshots and releases

### Bots 🤖
* **Probot: Stale**
    * Mark issues stale after 30 days
* **Dependabot**
    * Update GitHub Actions workflows
    * Update Gradle dependencies

### Issue Templates 📋
* Bug report template
* Feature request template

### Gradle Builds 🏗
* Shadowed plugin dependencies
* [Checkstyle](https://checkstyle.org/) Google standard style check
* [SpotBugs](https://spotbugs.github.io/) code analysis

### Testing 🧪
* [JUnit 5](https://junit.org/) unit tests
* [Mockito](https://site.mockito.org/) for mocking dependencies in unit tests

### Example Plugin Code 🔌
* `/example` command via [CommandAPI](https://commandapi.jorel.dev) demonstrating subcommands, tab completion, and permissions
* Example config loading and validation via [cw-commons](https://github.com/CrimsonWarpedcraft/cw-commons)' `ConfigManager`, backed by [Jackson](https://github.com/fasterxml/jackson) and [Hibernate Validator](https://hibernate.org/validator/)
* Example persistent per-player data storage via cw-commons' `Repository`/`PlayerDataManager`, demonstrated by `/example creepersKilled`: `CreeperKillListener` writes to it on each creeper kill, `CreepersKilled` reads it back

### Config Files 📁
* Sample plugin.yml with autofill name, version, and main class.
* Example config.yml
* Gradle build config
* Simple .gitignore for common Gradle files

## Usage
In order to use this template for yourself, there are a few things that you will need to keep in mind.

- [Extending the example code](docs/usage.md) — add commands, subcommands, config fields, or persistent per-player data
- [Customizing this template](docs/customization.md) — the one-time checklist for forking this repo (placeholders, secrets, etc.)
- [Releases & versioning](docs/releases.md) — PaperMC compatibility, version format, and how to cut a release
- [Agent instructions & skills](docs/skills.md) — agent guidance, Claude Code support, and available skills

## Building locally
Thanks to [Gradle](https://gradle.org/), building locally is easy no matter what platform you're on. Simply run the following command:

```text
./gradlew build
```

This build step will also run all checks and tests, making sure your code is clean.

JARs can be found in `build/libs/`.

## Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md).
