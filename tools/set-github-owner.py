#!/usr/bin/env python3
from pathlib import Path
import sys

if len(sys.argv) != 2:
    raise SystemExit("Usage: python tools/set-github-owner.py <GitHub-owner-or-org>")
owner = sys.argv[1].strip()
if not owner or '/' in owner:
    raise SystemExit("Pass only the GitHub owner/org name, not a full URL")
root = Path(__file__).resolve().parents[1]
needle = "REPLACE-WITH-YOUR-GITHUB"
for path in root.rglob('*'):
    if path.is_file() and path.suffix.lower() in {'.md', '.json', '.gradle', '.txt'}:
        try:
            text = path.read_text(encoding='utf-8')
        except UnicodeDecodeError:
            continue
        if needle in text:
            path.write_text(text.replace(needle, owner), encoding='utf-8')
            print(path.relative_to(root))
