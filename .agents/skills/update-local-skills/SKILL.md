---
name: update-local-skills
description: Update this repo's agent skills. Use when asked to sync, mirror, refresh, or add skills for this project.
---

This repo keeps local skills in two agent-specific locations:

- `.agents/skills/` is the source of truth for shared skills.
- `.claude/skills/` mirrors the same skill folders for Claude-compatible local use.

## Workflow

1. Inspect both trees before changing files.
2. Make the requested skill change in `.agents/skills/<skill-name>/SKILL.md` first.
3. `cp SKILL.md` to `.claude/skills/<skill-name>/SKILL.md`.
