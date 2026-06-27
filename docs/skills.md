# Agent instructions and skills

This repository keeps agent instructions in the AGENTS standard.
Contributors should only edit these standard files.

## Sources of truth

- `AGENTS.md` contains repository-wide context and constraints, including the
  plugin architecture, packaging, dependencies, command registration, versioning, and CI setup.
  Agents will read this file automatically at the beginning of a session.
- `.agents/skills/` contains reusable, task-specific instructions. Each
  skill lives in its own directory and is defined by a `SKILL.md` file.
  Agents get a list of available skills and can decide when to read their full contents.

#### Claude Code sync

`.claude/hooks/sync-agent-files.sh` copies the following:

| Canonical, tracked source | Generated, ignored Claude Code file |
| --- | --- |
| `AGENTS.md` | `CLAUDE.md` |
| `.agents/skills/<skill>/` | `.claude/skills/<skill>/` |

Hooks in `.claude/settings.json` run synchronization automatically:

- `SessionStart` on new, resumed, and cleared sessions.
- `PostToolUse` when Claude Code writes or edits AGENTS sources.

Do not edit or create `CLAUDE.md` or files under `.claude/skills/`.
They are automatically generated/replaced by scripts, and never tracked.

## Adding a skill

1. Create `.agents/skills/<skill-name>/SKILL.md`.
2. Write the following:

```
---
name: skill-name
description: Give it a short description.
---
```

`name` is the same as the directory.
