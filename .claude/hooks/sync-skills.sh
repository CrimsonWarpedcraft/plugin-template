#!/bin/sh
# Mirror .agents/skills/ (source of truth) into .claude/skills/ (generated).
#
# Claude Code only discovers skills under .claude/skills/, but this repo keeps
# the canonical copies in .agents/skills/ so they are shared with other agents.
# This script regenerates the .claude/skills/ mirror, preserving the one tracked
# Claude-only skill, sync-skills/, which is the manual lever for this sync.
#
# Usage:
#   sh .claude/hooks/sync-skills.sh            # mirror only
#   sh .claude/hooks/sync-skills.sh --reload   # mirror, then ask Claude Code to
#                                              # re-scan skills (SessionStart hook)
set -eu

# The skill that lives only in .claude/skills/ and must never be removed.
KEEP="sync-skills"

# Resolve the repo root from this script's location (.claude/hooks/sync-skills.sh)
# so the script works regardless of the current working directory.
SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ROOT=$(CDPATH= cd -- "$SCRIPT_DIR/../.." && pwd)

SRC="$ROOT/.agents/skills"
DEST="$ROOT/.claude/skills"

if [ ! -d "$SRC" ]; then
  echo "sync-skills: source directory not found: $SRC" >&2
  exit 1
fi

mkdir -p "$DEST"

# Drop every mirrored skill except the tracked sync-skills/ skill, so stale or
# renamed skills do not linger in the mirror.
for dir in "$DEST"/*/; do
  [ -d "$dir" ] || continue
  name=$(basename "$dir")
  [ "$name" = "$KEEP" ] && continue
  rm -rf "$dir"
done

# Copy each source skill into the mirror.
for dir in "$SRC"/*/; do
  [ -d "$dir" ] || continue
  name=$(basename "$dir")
  [ "$name" = "$KEEP" ] && continue
  rm -rf "$DEST/$name"
  cp -R "$dir" "$DEST/$name"
done

# For the SessionStart hook: tell Claude Code to re-scan skills now so the
# mirror is usable in this same session (no restart needed).
if [ "${1:-}" = "--reload" ]; then
  printf '%s\n' '{"hookSpecificOutput": {"hookEventName": "SessionStart", "reloadSkills": true}}'
fi
