#!/usr/bin/env bash
set -euo pipefail

ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
DECK="$ROOT/beamer/beamer.tex"

test -f "$DECK"

frame_count=$(grep -c '\\begin{frame}' "$DECK")
test "$frame_count" -eq 10 || {
  echo "Se esperaban 10 diapositivas y se encontraron $frame_count" >&2
  exit 1
}

grep -Fq 'Indicadores de Ocupabilidad' "$DECK"
grep -Fq 'Sergio Sebastian Pezo Jimenez' "$DECK"
grep -Fq 'Arbués Enrique Pérez Villegas' "$DECK"

for query in $(seq -w 1 14); do
  grep -Fq "Q$query" "$DECK" || {
    echo "Falta Q$query en la presentación" >&2
    exit 1
  }
done

for required in \
  '38 730' \
  '24 columnas' \
  '2019--2025-I' \
  'Docker Compose' \
  'HDFS' \
  'YARN' \
  'cambio de esquema' \
  'doble conteo' \
  'división temporal'; do
  grep -Fq "$required" "$DECK" || {
    echo "Falta contenido obligatorio: $required" >&2
    exit 1
  }
done

test "$(grep -c '\\note{' "$DECK")" -eq 10
grep -Fq 'Arbués:' "$DECK"
grep -Fq 'Sergio:' "$DECK"

if grep -Eq 'Ownership|Distributed Futures|Questions\?' "$DECK"; then
  echo "La presentación conserva contenido de ejemplo" >&2
  exit 1
fi

echo "Presentación Beamer verificada"
