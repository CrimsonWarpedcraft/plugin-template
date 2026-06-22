---
name: sync-skills
description: Mirror this repo's shared skills into Claude's skill directory. Use when asked to sync, mirror, refresh, or re-generate local skills, or after editing a skill under .agents/skills/.
---

# Source of truth: `.agents/skills/`

This repo keeps the canonical skills in **`.agents/skills/`** so they are shared
with other agents (e.g. Codex). Claude Code only discovers skills under
`.claude/skills/`, so that tree is a **generated mirror** of `.agents/skills/`.

Rules:

- **Edit skills only in `.agents/skills/<skill-name>/SKILL.md`.** Never edit files
  under `.claude/skills/` — they are overwritten on every sync.
- `.claude/skills/` is gitignored. The only tracked exception is this
  `sync-skills/` skill, which bootstraps and documents the sync.
- To add a skill, create `.agents/skills/<new-skill>/SKILL.md`, then sync.

# How the mirror stays in sync (automatic)

`.claude/settings.json` ships two hooks, both running
`.claude/hooks/sync-skills.sh`:

- **`SessionStart`** (`startup`, `resume`, `clear`) regenerates the mirror and
  returns `reloadSkills: true`, so a freshly cloned or pulled skill is usable in
  that same session. Claude Code asks to trust project hooks on first run —
  approve it once.
- **`PostToolUse`** re-mirrors immediately when Claude edits a file under
  `.agents/skills/**`.

A `git pull` made *while a session is already running* is not caught
automatically — run `/sync-skills`, `/clear`, or restart to pick it up.

# Manual sync (this skill)

Run the same script the hooks use, from the repo root:

```sh
sh .claude/hooks/sync-skills.sh
```

This regenerates every skill folder under `.claude/skills/` from
`.agents/skills/`, removing any stale or renamed mirror folders while preserving
this `sync-skills/` skill. Use it to bootstrap the mirror when hooks are
disabled, or to force an immediate re-sync.
