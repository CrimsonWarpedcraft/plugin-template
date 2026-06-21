# Releases & Versioning

#### PaperMC Version Recommendation Mapping
Here's an example of what a version map for your plugin may look like.

| PaperMC | ExamplePlugin |
|---------|---------------|
| 26.1.2  | 5.0.0+        |
| 1.21.11 | 4.0.18        |
| 1.20.6  | 3.11.0        |
| 1.19.4  | 3.2.1         |
| 1.18.2  | 3.0.2         |
| 1.17.1  | 2.2.0         |
| 1.16.5  | 2.1.2         |

To use this template to make your own plugin, just use the latest tagged version of this project and update the PaperMC
version as needed. See more info on release stability below.

#### Release and Versioning Strategy
Stable versions of this repo are tagged `vX.Y.Z` and have an associated [release](https://github.com/CrimsonWarpedcraft/plugin-template/releases).

Testing versions of this repo are tagged `vX.Y.Z-RC-N` and have an associated [pre-release](https://github.com/CrimsonWarpedcraft/plugin-template/releases).

Development versions of this repo are pushed to the master branch and are **not** tagged.

| Event             | Plugin Version Format | CI Action                        | GitHub Release Draft? |
|-------------------|-----------------------|----------------------------------|-----------------------|
| PR                | yyMMdd-HHmm-SNAPSHOT  | Build and test                   | No                    |
| Push to `main`    | 0.0.0-SNAPSHOT        | Build, test, release, and notify | No                    |
| Tag `vX.Y.Z-RC-N` | X.Y.Z-SNAPSHOT        | Build, test, release, and notify | Pre-release           |
| Tag `vX.Y.Z`      | X.Y.Z                 | Build, test, release, and notify | Release               |

## Creating a Release
Below are the steps you should follow to create a release.

1. Create a tag on `main` using semantic versioning (e.g. v0.1.0)
2. Push the tag and get some coffee while the workflows run
3. Publish the release draft once it's been automatically created
