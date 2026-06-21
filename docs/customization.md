# Customizing This Template

When adapting this template for your own plugin, you'll need to update the following files.

### Discord Notifications
This repo allows automatically pushing releases to a Discord webhook.

To use this Action, you will need to set two GitHub Actions secrets.
- `DISCORD_WEBHOOK_ID`
- `DISCORD_WEBHOOK_TOKEN`

You can find these values by copying the Discord Webhook URL:
`https://discord.com/api/webhooks/<DISCORD_WEBHOOK_ID>/<DISCORD_WEBHOOK_TOKEN>`

Optionally, you can also configure `DISCORD_RELEASE_WEBHOOK_ID` and `DISCORD_RELEASE_WEBHOOK_TOKEN`
to send release announcements to a separate channel.

For more information, see [Discord Message Notify](https://github.com/marketplace/actions/discord-message-notify).

### `README.md`
Make this relevant to your project.

Be sure to replace the badges for build status and Discord.

### `settings.gradle.kts`
Replace `ExamplePlugin` with the name of your plugin.

```kotlin
rootProject.name = "ExamplePlugin"
```

### `build.gradle.kts`
Make sure to update `group` to your package's name in the following section.

```kotlin
group = "com.crimsonwarpedcraft.exampleplugin"
```

Add any required repositories for your dependencies:

```kotlin
repositories {
    // ...
}
```

Also, update your dependencies as needed (of course).

```kotlin
dependencies {
    // ...
}
```

### `src/main/resources/plugin.yml`
First, update the following with your information.

```yaml
author: AUTHOR
description: DESCRIPTION
```

Next, the `permissions` section below should be updated as needed.

```yaml
permissions:
  example.test:
    description: DESCRIPTION
    default: true
  example.*:
    description: Grants all other permissions
    default: false
    children:
      example.test: true
```

Do NOT create a `commands:` section — CommandAPI registers commands programmatically in
`onEnable()` (see `ExampleCommand`), not via `plugin.yml`.

Declaring a command in both places
causes Bukkit to register it a second time, which CommandAPI will warn about at startup.

### `.github/`
- `CODEOWNERS` -> Replace `leviem1` with your username.
- `FUNDING.yml` -> Update or delete this file, [whatever applies to you.](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/displaying-a-sponsor-button-in-your-repository)

### Code of Conduct
If you choose to adopt the Code of Conduct for your project,
please update line 63 of `CODE_OF_CONDUCT.md` with your preferred contact method.
