# Releases and versioning

## PaperMC version mapping

| Template revision | PaperMC baseline | Java |
|-------------------|------------------|------|
| Current source    | 26.3             | 25   |

This is the current source's support baseline, not a claim that a particular template version has
been released with it. For a tagged template release, consult the build and support documentation
at that tag. Spigot and Folia compatibility is not claimed.

When forking, replace this table with versions you have tested. Set `minecraft` in
`gradle/libs.versions.toml` for the dev server and declared minimum API, and keep the separate full
stable `paper` API pin aligned. Compilation and all JUnit 6 suites share that API dependency.

## Versioning strategy

Stable plugin versions are tagged `vX.Y.Z` and have an associated GitHub release.

Testing plugin versions are tagged `vX.Y.Z-RC-N` and have an associated GitHub pre-release.

Development plugin versions are pushed to the `main` branch and are **not** tagged.

| Event             | Plugin version        | CI action                      | Release type      |
|-------------------|-----------------------|--------------------------------|-------------------|
| PR                | yyMMdd-HHmm-SNAPSHOT  | Build and test                 | None              |
| Push to `main`    | 0.0.0-RC-1-SNAPSHOT   | Build, upload, and notify      | None              |
| Tag `vX.Y.Z-RC-N` | X.Y.Z-RC-N-SNAPSHOT   | Build, draft, and notify       | Pre-release draft |
| Tag `vX.Y.Z`      | X.Y.Z                 | Build, draft, and notify       | Release draft     |

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

1. Create a semantic version tag on `main`, such as `v0.1.0` or `v0.1.0-RC-1`.
2. Push the tag and wait for the tag workflow to create a draft release.
3. Review and publish the generated draft.
