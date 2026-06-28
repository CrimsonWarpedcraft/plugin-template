# Customizing This Template

Use this checklist when turning the template into a plugin.

## Plugin identity

1. Set `rootProject.name` in `settings.gradle.kts` to the Java entry point class name.
2. Rename `ExamplePlugin.java`, the `ExamplePlugin` class, and all references to it. The current
   `plugin.yml` build substitution requires this name to match `rootProject.name`.
3. Set `group` in `build.gradle.kts` to the Java package.
4. Rename the main and test package directories, declarations, and imports from
   `com.crimsonwarpedcraft.exampleplugin`.

## Example code

Replace or remove the example command, permission, config, data store, listener, and tests. Keep
these parts in sync:

- Command names and permission checks in Java
- Permission declarations in `src/main/resources/plugin.yml`
- Fields in `PluginConfig` and `src/main/resources/config.yml`
- Main and test code

CommandAPI registers commands in Java. Do not add matching entries under `commands:` in
`plugin.yml`.

## Metadata and build

- Update `author`, `description`, `permissions`, and `api-version` in `plugin.yml`.
- Keep the Paper API version, Java toolchain, CI Java versions, and documented server support in
  sync.
- Review repositories, dependencies, Shadow relocations, and `minimize` exclusions. Remove
  example dependencies the plugin no longer uses.

## Project files

- Rewrite `README.md` for the plugin. Replace the build badge, Discord link, commands, features,
  and repository links.
- Update `docs/usage.md`, `docs/releases.md`, `AGENTS.md`, and canonical skills under
  `.agents/skills/` when their examples or architecture change.
- Do not edit `CLAUDE.md` or `.claude/skills/`. They are generated mirrors.
- Check source attribution and license terms before changing copyright notices.

## GitHub

- Update `.github/CODEOWNERS` and update or delete `.github/FUNDING.yml`.
- Replace the enforcement contact in `CODE_OF_CONDUCT.md`.
- Review issue templates, labels, the stale policy, Dependabot, branch protection, and workflows.
- Update each `main` reference if the repository uses a different default branch.

Discord notifications use:

- Repository variable `DISCORD_WEBHOOK_ID`
- Actions secret `DISCORD_WEBHOOK_TOKEN`
- Optional repository variable `DISCORD_RELEASE_WEBHOOK_ID`
- Optional Actions secret `DISCORD_RELEASE_WEBHOOK_TOKEN`

Remove the notification jobs if the plugin will not use Discord webhooks.

## Verify

1. Search for old names, packages, permissions, placeholders, attribution, and repository URLs.
2. Run `./gradlew clean build`.
3. Check the processed `plugin.yml` and shaded JAR for the correct main class.
4. Start the JAR on the oldest supported Paper version.
