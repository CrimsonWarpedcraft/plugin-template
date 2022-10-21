# PaperMC/Spigot Minecraft Server Plugin Template
A template for building PaperMC/Spigot Minecraft server plugins!

[![Build, Test, and Release](https://github.com/CrimsonWarpedcraft/plugin-template/actions/workflows/main.yml/badge.svg)](https://github.com/CrimsonWarpedcraft/plugin-template/actions/workflows/main.yml)

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
* Shadowed [PaperLib](https://github.com/PaperMC/PaperLib) build
* [Checkstyle](https://checkstyle.org/) Google standard style check
* [SpotBugs](https://spotbugs.github.io/) code analysis
* [JUnit](https://junit.org/) testing

### Release and Versioning Strategy
| Event             | Version Format       | CI Action                        | GitHub Release Draft? |
|-------------------|----------------------|----------------------------------|-----------------------|
| PR                | yyMMdd-HHmm-SNAPSHOT | Build and test                   | No                    |
| Schedule          | yyMMdd-HHmm-SNAPSHOT | Build, test, and notify          | No                    |
| Push on `main`    | 0.0.0-SNAPSHOT       | Build, test, release, and notify | No                    |
| Tag `vX.Y.Z-RC-N` | X.Y.Z-SNAPSHOT       | Build, test, release, and notify | Pre-release           |
| Tag `vX.Y.Z`      | X.Y.Z                | Build, test, release, and notify | Release               |

### Config Files 📁
* Sample plugin.yml with autofill name, version, and main class.
* Empty config.yml (just to make life \*that\* much easier)
* Gradle build config
* Simple .gitignore for common Gradle files

## Usage
In order to use this template for yourself, there are a few things that you will need to keep in mind.

### Release Info
Stable version of this repo are tagged `vX.Y.Z` and have an associated [release](https://github.com/CrimsonWarpedcraft/plugin-template/releases).

Testing versions of this repo are tagged `vX.Y.Z-RC-N` and have an associated [pre-release](https://github.com/CrimsonWarpedcraft/plugin-template/releases).

Development versions of this repo are pushed to the master branch and are **not** tagged.

### Discord Notifications
In order to use Discord notifications, you will need to create two GitHub secrets. `DISCORD_WEBHOOK_ID` 
should be set to the id of your Discord webhook. `DISCORD_WEBHOOK_TOKEN` will be the token for the webhook.

You can find these values by copying the Discord Webhook URL:  
`https://discord.com/api/webhooks/<DISCORD_WEBHOOK_ID>/<DISCORD_WEBHOOK_TOKEN>`

For more information, see [Discord Message Notify](https://github.com/marketplace/actions/discord-message-notify).

---

**I've broken the rest of the changes up by their files to make things a bit easier to find.**

---

### settings.gradle
Update the line below with the name of your plugin.

```groovy
rootProject.name = 'ExamplePlugin'
```

### build.gradle
Make sure to update the `group` to your package's name in the following section.

```groovy
group = "com.crimsonwarpedcraft.exampleplugin"
```

Add any required repositories for your dependencies in the following section.

```groovy
repositories {
    maven {
        name 'papermc'
        url 'https://papermc.io/repo/repository/maven-public/'
        content {
            includeModule("io.papermc.paper", "paper-api")
            includeModule("io.papermc", "paperlib")
            includeModule("net.md-5", "bungeecord-chat")
        }
    }

    mavenCentral()
}
```

Also, update your dependencies as needed (of course).

```groovy
dependencies {
    compileOnly 'io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT'
    compileOnly 'com.github.spotbugs:spotbugs-annotations:4.5.2'
    implementation 'io.papermc:paperlib:1.0.7'
    spotbugsPlugins 'com.h3xstream.findsecbugs:findsecbugs-plugin:1.11.0'
    testCompileOnly 'com.github.spotbugs:spotbugs-annotations:4.5.2'
    testImplementation 'io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT'
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.2'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.8.2'
}
```

### src/main/resources/plugin.yml
First, update the following with your information.

```yaml
author: AUTHOR
description: DESCRIPTION
```

Next, the `commands` and `permissions` sections below should be updated as needed.

```yaml
commands:
  ex:
    description: Base command for EXAMPLE
    usage: "For a list of commands, type /ex help"
    aliases: example
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

### .github/dependabot.yml
You will need to replace all instances of `leviem1`, such as the one below, with your GitHub
username.

```yaml
reviewers:
  - "leviem1"
```

### .github/CODEOWNERS
You will need to replace `leviem1`, with your GitHub username.

```text
*   @leviem1
```

### .github/FUNDING.yml
Update this file with whatever applies to you.

```yaml
github: leviem1
```

## Creating a Release
Below are the steps you should follow to create a release.

1. Create a tag on `main` using semantic versioning (e.g. v0.1.0)
2. Push the tag and get some coffee while the workflows run
3. Publish the release draft once it's been automatically created

## Contributing
### General workflow
0. (External contributors only) Create a fork of the repository
1. Pull any changes from `main` to make sure you're up-to-date
2. Create a branch from `main`
    * Give your branch a name that describes your change (e.g. add-scoreboard)
    * Focus on one change per branch
    * Keep your commits small (<300 LOC), and write descriptive commit messages
3. When you're ready, create a pull request to `main` with a descriptive title, and listing any changes made in its description
    * Link any issues that your pull request is related to as well

#### Example:
```text
Create scoreboard for total points

ADDED - Scoreboard displayed in-game at game end  
CHANGED - Updated `StorageManager` class to persist scoreboard data
```

After the pull request has been reviewed, approved, and passes all automated checks, it will be merged into main.

### Building locally
Thanks to [Gradle](https://gradle.org/), building locally is easy no matter what platform you're on. Simply run the following command:

```text
./gradlew build
```

This build step will also run all checks and tests, making sure your code is clean.

JARs can be found in `build/libs/`.

---

I think that's all... phew!
