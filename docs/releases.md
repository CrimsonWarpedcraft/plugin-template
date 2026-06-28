# Releases and versioning

## PaperMC version mapping

Replace this example with the Paper versions your plugin supports.

| PaperMC | Plugin version |
|---------|----------------|
| 26.1.2  | 5.0.0+        |
| 1.21.11 | 4.0.18        |
| 1.20.6  | 3.11.0        |
| 1.19.4  | 3.2.1         |
| 1.18.2  | 3.0.2         |
| 1.17.1  | 2.2.0         |
| 1.16.5  | 2.1.2         |

Start from the latest tagged template release, then set the Paper API and supported server versions
for your plugin.

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

## Creating a release

1. Create a semantic version tag on `main`, such as `v0.1.0` or `v0.1.0-RC-1`.
2. Push the tag and wait for the tag workflow to create a draft release.
3. Review and publish the generated draft.
