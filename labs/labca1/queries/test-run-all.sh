#!/usr/bin/env bash
set -Eeuo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

output="$($SCRIPT_DIR/run-all.sh --dry-run)"
command_count="$(grep -c 'run-job.sh' <<<"$output")"
[[ "$command_count" -eq 14 ]] || {
  printf 'FAIL: se esperaban 14 comandos y se obtuvieron %s\n' "$command_count" >&2
  exit 1
}

unique_mains="$(grep -o -- '--main [^ ]*' <<<"$output" | sort -u | wc -l)"
[[ "$unique_mains" -eq 14 ]] || {
  printf 'FAIL: las clases principales no son únicas\n' >&2
  exit 1
}

unique_outputs="$(grep -o -- '--output [^ ]*' <<<"$output" | sort -u | wc -l)"
[[ "$unique_outputs" -eq 14 ]] || {
  printf 'FAIL: las salidas no son únicas\n' >&2
  exit 1
}

printf 'PASS: run-all construye catorce comandos únicos\n'
