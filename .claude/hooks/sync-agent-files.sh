#!/bin/sh
# Generate the Claude-Code-facing agent files from their sources of truth.
#
# Two jobs, both regenerating gitignored output from tracked sources:
#   1. Mirror .agents/skills/ -> .claude/skills/. Claude Code only discovers
#      skills under .claude/skills/, but the canonical copies live in
#      .agents/skills/ so they are shared with other agents (e.g. Codex).
#   2. Copy AGENTS.md -> CLAUDE.md. AGENTS.md is the single source of truth for
#      project instructions; CLAUDE.md is the generated copy Claude Code reads.
#
# Usage:
#   sh .claude/hooks/sync-agent-files.sh            # generate only
#   sh .claude/hooks/sync-agent-files.sh --reload   # generate, then ask Claude
#                                                   # Code to re-scan skills
#                                                   # (SessionStart hook)
set -eu

# Resolve the repo root from this script's location
# (.claude/hooks/sync-agent-files.sh) so the script works regardless of the
# current working directory.
SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ROOT=$(CDPATH= cd -- "$SCRIPT_DIR/../.." && pwd)

SRC="$ROOT/.agents/skills"
DEST="$ROOT/.claude/skills"

# --- 1. Mirror .agents/skills/ -> .claude/skills/ ---------------------------
if [ ! -d "$SRC" ]; then
  echo "sync-agent-files: source directory not found: $SRC" >&2
  exit 1
fi

# Regenerate the whole mirror so stale or renamed skills do not linger.
rm -rf "$DEST"
mkdir -p "$DEST"
for dir in "$SRC"/*/; do
  [ -d "$dir" ] || continue
  name=$(basename "$dir")
  cp -R "$dir" "$DEST/$name"
done

# --- 2. Copy AGENTS.md -> CLAUDE.md -----------------------------------------
if [ -f "$ROOT/AGENTS.md" ]; then
  cp "$ROOT/AGENTS.md" "$ROOT/CLAUDE.md"
fi

# For the SessionStart hook: tell Claude Code to re-scan skills now so the
# mirror is usable in this same session (no restart needed).
if [ "${1:-}" = "--reload" ]; then
  printf '%s\n' '{"hookSpecificOutput": {"hookEventName": "SessionStart", "reloadSkills": true}}'
fi
