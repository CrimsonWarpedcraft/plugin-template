---
name: run-example-plugin
description: Build, test, and release ExamplePlugin (this PaperMC/Spigot plugin template) from source. Use when asked to build the plugin, run its tests, run a single test class, or produce a release JAR.
---

`ExamplePlugin` is a PaperMC/Spigot plugin — "running" it means building from source,
executing the test suite, and optionally producing the shaded JAR that gets dropped into a
server's `plugins/` folder.

## Build

```bash
./gradlew build            # macOS / Linux / Git Bash
gradlew.bat build           # Windows cmd/PowerShell
```

Runs compilation, Checkstyle (Google Java Style, `maxWarnings = 0`), SpotBugs with FindSecBugs,
and all tests. Fix every warning — the build fails on the first one.

## Verify

```bash
./gradlew test
```

All tests pass. Subset by class:

```bash
./gradlew test --tests "com.crimsonwarpedcraft.exampleplugin.command.PingTest"
```

Subset by method:

```bash
./gradlew test --tests "com.crimsonwarpedcraft.exampleplugin.command.GreetTest.greetsTarget"
```

## Release JAR

```bash
./gradlew -Pver="v1.0.0" release
# → build/libs/ExamplePlugin.jar (version stripped from filename for stable tags)
```

Versioning logic (`build.gradle.kts`):
- No `-Pver` → `yyMMdd-HHmm-SNAPSHOT`
- `-Pver=vX.Y.Z-RC-N` → `X.Y.Z-SNAPSHOT`
- `-Pver=vX.Y.Z` → `X.Y.Z` (stable; the `release` task then renames the shadow jar to
  `${rootProject.name}.jar`)

Quote the `-Pver` value to stop the shell/PowerShell from mangling the `=`.

## Maintenance

After adding a dependency, check whether `shadowJar` in `build.gradle.kts` needs a new
`relocate(...)` entry (to avoid classloader clashes with other plugins on the same server) and
a `minimize { exclude(...) }` entry — see the first Gotcha below for why this matters.

Keep these in sync with the current state of the project:

- **`CLAUDE.md`** — architecture, versioning logic, customization checklist
- **`README.md`**, **`docs/usage.md`**, **`docs/customization.md`**, **`docs/releases.md`** —
  feature list, extension recipes, fork checklist, PaperMC version mapping
- **`.claude/skills/run-example-plugin/SKILL.md`** (this file) — build commands, release
  process, shaded package list

## Gotchas

- **`minimize()` silently strips reflection/SPI-only dependencies** — `shadowJar`'s `minimize()`
  only treats a dependency as "used" if *this project's own compiled classes* reference it
  directly. A library only referenced from inside an already-`exclude`d dependency (e.g.
  cw-commons calling into a JDBC/driver library) looks unused and gets stripped down to empty
  `package-info.class` stubs, causing `NoClassDefFoundError` at runtime even though the build
  succeeds. Add an `exclude(dependency("group:artifact:.*"))` entry under `minimize` for any
  such dependency, mirroring the existing `cw-commons` entry (which works around the same
  problem for its bundled SQLite driver).
- **OneDrive build lock** — if this repo lives under OneDrive, `./gradlew build` can fail with
  `Unable to delete directory '...\build\classes\java\main'` (or `...\test\binary`, or other
  `build/` subdirectories) because OneDrive holds a sync lock on it. Delete the offending
  directory manually (e.g. `rm -rf build/classes/java/main`) and retry.
- **Commands declared in both `plugin.yml` and `onEnable()`** — CommandAPI registers commands
  programmatically; don't add a matching `commands:` entry to `plugin.yml` or Bukkit
  double-registers it, which CommandAPI flags at startup with a warning.
- **Mockito javaagent** — `mockito-core` is wired into a dedicated `mockitoAgent` configuration
  and attached via `-javaagent` in `tasks.test` to avoid the inline-mock-maker self-attach
  warning. Don't drop this wiring when customizing the template, and remember Mockito must stay
  declared explicitly as `testImplementation` — it isn't provided transitively.
