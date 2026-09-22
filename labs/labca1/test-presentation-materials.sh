#!/usr/bin/env bash
set -euo pipefail

ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
TUTORIAL="$ROOT/beamer/TUTORIAL-EJECUCION.md"
SCRIPT="$ROOT/beamer/GUION-PRESENTACION.md"

test -f "$TUTORIAL"
test -f "$SCRIPT"

for command in \
  './hadoop3/scripts/start.sh' \
  './hadoop3/scripts/smoke-test.sh' \
  './labs/labca1/queries/run-all.sh --from 1 --to 1' \
  './labs/labca1/queries/run-all.sh --from 1 --to 14' \
  './hadoop3/scripts/stop.sh'; do
  grep -Fq "$command" "$TUTORIAL" || {
    echo "Falta documentar el comando: $command" >&2
    exit 1
  }
done

for slide in $(seq 1 10); do
  grep -Fq "Diapositiva $slide" "$SCRIPT" || {
    echo "Falta el guion de la diapositiva $slide" >&2
    exit 1
  }
done

test "$(grep -c '^\*\*Transición' "$SCRIPT")" -eq 9
grep -Fq '**Arbués:**' "$SCRIPT"
grep -Fq '**Sergio:**' "$SCRIPT"
grep -Fq '9 min 10 s' "$SCRIPT"

echo "Tutorial y guion de exposición verificados"
