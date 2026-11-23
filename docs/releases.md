# Releases and versioning

## PaperMC version mapping

Choose the template source for your server's Paper version. An exact template version is the
last release that supports that baseline. A `+` includes that version and later releases until
the baseline changes. Java is the template's build target.

| Template version | Paper baseline | Java |
|------------------|---------------|------|
| 7.6.0+           | 26.3          | 25   |
| 7.5.2            | 26.1.2        | 25   |
| 4.0.18           | 1.21.11       | 21   |
| 3.11.0           | 1.20.6        | 21   |
| 3.2.1            | 1.19.4        | 17   |
| 3.0.2            | 1.18.2        | 17   |
| 2.2.0            | 1.17.1        | 17   |
| 2.1.2            | 1.16.5        | 11   |

Update the table only when `plugin.yml`'s declared `api-version` increases. Set the previous row
to the last template release that supports it, then add the new baseline with its first release
followed by `+`. The `minecraft` entry in `gradle/libs.versions.toml` supplies `api-version`.

When forking, document the Paper versions your plugin supports. See
[GitHub Releases](https://github.com/CrimsonWarpedcraft/plugin-template/releases) for template tags
and release notes.

## Versioning strategy

Tags in this repository version the **template**: the scaffold, build, and CI that a fork starts
with. Choose the bump for the change to that template: a dependency refresh can be a patch; an
added test suite is a minor feature. A fork chooses its own plugin versions and tags.

For a tagged build, CI passes the tag to Gradle as `-Pver`. For example, `v1.2.3` produces
`ExamplePlugin.jar` with `version: "1.2.3"` in `plugin.yml`. The separate `api-version` field
declares the Paper baseline.

| Event                       | Version in `plugin.yml` | CI action                 | Release type      |
|-----------------------------|-------------------------|---------------------------|-------------------|
| PR                          | `yyMMdd-HHmm-SNAPSHOT`  | Build and test            | None              |
| `main` release artifact     | `0.0.0-RC-1-SNAPSHOT`   | Build, upload, and notify | None              |
| Tag `vX.Y.Z-RC-N`           | `X.Y.Z-RC-N-SNAPSHOT`   | Build, draft, and notify  | Pre-release draft |
| Tag `vX.Y.Z`                | `X.Y.Z`                 | Build, draft, and notify  | Release draft     |

## What is verified

`./gradlew artifactTest` runs against the shaded JAR alongside JUnit and the Paper API. It loads
the packaged YAML, exercises validation, closes and reopens a real SQLite file, and checks
`plugin.yml`, relocation, service descriptors and native resources. This is separate from starting
a live Paper server and invoking the plugin's commands.

The PR and merge-queue checks run this suite on Ubuntu and Windows. For a tag, Ubuntu runs
`release` (including unit, integration and artifact tests) and uploads the exact JAR it checked.
Windows downloads that artifact by ID and tests it. Only then does the draft job download and
attach the same artifact; neither job rebuilds it. A failed verification blocks the draft.

To verify a prebuilt stable or RC JAR yourself, pass its original version and path, for example:

```sh
./gradlew artifactTest "-Pver=v1.2.3" "-PartifactUnderTest=path/to/ExamplePlugin.jar"
```

## Creating a release

1. Verify the intended version locally, e.g. `./gradlew clean release "-Pver=v1.2.3"`. This builds
   and tests the release JAR. The version comes from the command line; there is no version file to bump.
2. Once the checks on the intended `main` commit pass, tag that commit (`vX.Y.Z` for a stable
   release or `vX.Y.Z-RC-N` for a release candidate) and push the tag.
3. Review the generated GitHub draft, add the changes that matter to template users since the
   previous tag, and publish it.
