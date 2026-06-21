---
name: fill-template-plugin
description: Customize this PaperMC/Spigot plugin template for a real plugin. Use when asked to fill in, adapt, rename, fork, or customize the template project.
---

If information required in the checklist below is missing, end your turn and ask for it before editing.

# Template Customization Checklist

When adapting this template for a real plugin, update:
1. `settings.gradle.kts` — `rootProject.name`
2. `build.gradle.kts` — `group` (Java package)
3. `src/main/resources/plugin.yml` — `author`, `description`, `commands`, `permissions`
4. Rename the Java package and source directory from `com.crimsonwarpedcraft.exampleplugin`
5. `.github/dependabot.yml`, `.github/CODEOWNERS`, `.github/FUNDING.yml` — replace `leviem1`
6. `CODE_OF_CONDUCT.md` line 63 — contact method
7. README badges and Discord invite link
