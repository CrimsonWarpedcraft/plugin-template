# plugin-template
Template for building PaperMC plugins

## Setup

In order to use this template for yourself, there are a few things that you will need to change.

I've broken the changes up by their files to make things a bit easier to find.

### settings.gradle

Update the line below with the name of your plugin.

```groovy
rootProject.name = 'plugin-template'
```

### build.gradle

You may want to set a new version number, but this will probably be more important when [Creating a Release](#creating-a-release).

```groovy
version '0.1.0-SNAPSHOT'
```

Add any required repositories for your dependencies in the following section.

```groovy
repositories {
    mavenCentral()
    maven {
        name 'papermc'
        url 'https://papermc.io/repo/repository/maven-public/'
    }
}
```

Also, update your dependencies as needed (of course).

```groovy
dependencies {
    implementation "io.papermc:paperlib:1.0.5"
    implementation group: 'com.destroystokyo.paper', name: 'paper-api', version: '1.16.1-R0.1-SNAPSHOT'
}
```

Lastly, you probably want to update your package names on the `relocate` line below.

```groovy
shadowJar {
    relocate 'io.papermc.lib', 'com.snowypeaksystems.' + rootProject.name + '.paperlib'
    minimize()
}
```

### .github/tag.yml

In the following section, you will need to replace "plugin-template.jar" in `asset_path` and `asset_name` with the name of your plugin (see [settings.gradle](#settingsgradle)).

```yml
- name: Upload Release Asset
  uses: actions/upload-release-asset@v1
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
  with:
    upload_url: ${{ steps.create_release.outputs.upload_url }}
    asset_path: ${{ github.workspace }}/build/libs/plugin-template.jar
    asset_name: plugin-template.jar
    asset_content_type: application/java-archive
```

### src/main/resources/plugin.yml

First, you'll need to replace `com.snowypeaksystems` on the line below with your package's name.

```yml
main: com.snowypeaksystems.${NAME}.${NAME}
```

Next, update the following with your information.

```
author: AUTHOR
description: DESCRIPTION
```

The `commands` and `permissions` sections below are provided as examples and should be updated according to your needs.

```yml
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

## Creating a Release

Below are the steps you should follow to create a release.

1. Make sure to update the `version` in [build.gradle](#buildgradle)
2. Create a tag at the desired commit on `master` and push it to `origin`
3. Add a description to the release draft in the releases page

## Contributing

### General workflow

1. First, pull any changes from `master` to make sure you're up-to-date
2. Create a branch from `master`
    * Give your branch a name that describes your change (e.g. add-scoreboard)
    * Focus on one change per branch
    * Keep your commits small, and write descriptive commit messages
3. When you're ready, create a pull request to `master` with a descriptive title, and listing any changes made its description
    * Link any issues that your pull request is related to as well

#### Example:
```
Create scoreboard for total points

ADDED - Scoreboard displayed at in-game at game end  
CHANGED - Updated `StorageManager` class to persist scoreboard data
```

After the pull request is reviewed, approved, and passes all automated checks, it will be merged into master.

### Building locally

Thanks to [Gradle](https://gradle.org/), building locally is easy no matter what platform you're on. Simply run the following command:

#### macOS/Linux/Unix/
`./gradlew build`

#### Windows
`gradlew.bat build`

This build step will also run all checks, making sure your code is clean.

---

I think that's all... phew!
