# Releases and versioning

## PaperMC version mapping

| Template version | Dev server / `plugin.yml` `api-version` | Compile-time Paper API | Java |
|------------------|-----------------------------------------|------------------------|------|
| `v7.6.0`         | `26.3`                                  | `26.3.build.255-stable` | 25   |
| `v7.5.2`         | `26.1.2`                                | `26.2.build.119-stable` | 25   |

See [GitHub Releases](https://github.com/CrimsonWarpedcraft/plugin-template/releases) for published
tags and release notes. The table records each revision's build settings. Spigot and Folia
compatibility is not claimed.

When forking, replace this table with versions you have tested. Set `minecraft` in
`gradle/libs.versions.toml` for the dev server and declared minimum API, and keep the separate full
stable `paper` API pin aligned. Compilation and all JUnit 6 suites share that API dependency.

## Versioning strategy

Tags in this repository version the **template**: the scaffold, build, and CI that a fork starts
with. Choose the bump for the change to that template: a dependency refresh can be a patch; an
added test suite is a minor feature. A fork chooses its own plugin versions and tags.

For a tagged build, CI passes the tag to Gradle as `-Pver`. For example, `v7.6.0` produces
`ExamplePlugin.jar` with `version: "7.6.0"` in `plugin.yml`. The separate `api-version` field
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

1. Verify the intended version locally, e.g. `./gradlew clean release "-Pver=v7.6.0"`. This builds
   and tests the release JAR. The version comes from the command line; there is no version file to bump.
2. Once the checks on the intended `main` commit pass, tag that commit (`vX.Y.Z` for a stable
   release or `vX.Y.Z-RC-N` for a release candidate) and push the tag.
3. Review the generated GitHub draft, add the changes that matter to template users since the
   previous tag, and publish it.
