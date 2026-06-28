---
name: fill-template-plugin
description: Customize this PaperMC/Spigot plugin template for a real plugin. Use when asked to fill in, adapt, rename, fork, or customize the template project.
---

Read `docs/customization.md` before editing. Ask for any missing plugin details before making
changes.

# Checklist

1. Set `rootProject.name`; rename `ExamplePlugin.java` and all references to the example plugin.
2. Set the Gradle `group`; rename main and test package paths, declarations, and imports.
3. Replace or remove the example code.
4. Update `plugin.yml` metadata and permissions. Do not declare commands there.
5. Align the Paper API, `api-version`, Java toolchain, CI Java versions, dependencies, shading,
   and supported-version docs.
6. Update README content and links, project docs, `AGENTS.md`, and `.agents/skills/`.
7. Update GitHub ownership, funding, conduct contact, templates, policies, workflows, variables,
   and secrets as applicable.
8. Search for template leftovers, then run `./gradlew clean build`.
