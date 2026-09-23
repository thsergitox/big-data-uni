#!/usr/bin/env bash
set -euo pipefail

ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
DECK="$ROOT/beamer/beamer.tex"

test -f "$DECK"

frame_count=$(grep -c '\\begin{frame}' "$DECK")
test "$frame_count" -eq 18 || {
  echo "Se esperaban 18 diapositivas y se encontraron $frame_count" >&2
  exit 1
}

grep -Fq 'Indicadores de Ocupabilidad' "$DECK"
grep -Fq '\tableofcontents' "$DECK"
grep -Fq 'Referencias' "$DECK"
grep -Fq 'thebibliography' "$DECK"
grep -Fq 'Sergio Sebastian Pezo Jimenez' "$DECK"
grep -Fq 'Arbués Enrique Pérez Villegas' "$DECK"

for query in $(seq -w 1 14); do
  grep -Fq "Q$query" "$DECK" || {
    echo "Falta Q$query en la presentación" >&2
    exit 1
  }
done

grep -Fq '\begin{tikzpicture' "$DECK" || {
  echo "Falta el diagrama editable del flujo MapReduce" >&2
  exit 1
}
grep -Fq 'Mapper' "$DECK"
grep -Fq 'Reducer' "$DECK"
grep -Fq 'Stealth' "$DECK"

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

for output in \
  'national=54897127' \
  'mean\_tnoh=41.5950' \
  'count=1950' \
  'f1=0.793103' \
  'rmse=3.879584'; do
  grep -Fq "$output" "$DECK" || {
    echo "Falta una salida real de Hadoop: $output" >&2
    exit 1
  }
done

test "$(grep -c '\\note{' "$DECK")" -eq 18
grep -Fq 'Arbués:' "$DECK"
grep -Fq 'Sergio:' "$DECK"

for mapping in \
  'Q01--Q05: ítem 1' \
  'Q06: ítem 2' \
  'Q07: ítem 3' \
  'Q08: ítem 4' \
  'Q09--Q10: ítem 5' \
  'Q11--Q12: ítem 6' \
  'Q13--Q14: ítem 7'; do
  grep -Fq "$mapping" "$DECK" || {
    echo "Falta relacionar consultas con la práctica: $mapping" >&2
    exit 1
  }
done

for query in $(seq -w 1 14); do
  section=$(sed -n "/\\\\begin{frame}{Q$query/,/\\\\end{frame}/p" "$DECK")
  grep -Fq '\queryflow{' <<<"$section" || {
    echo "Falta el flujo Mapper/Reducer/output en el frame Q$query" >&2
    exit 1
  }
done

test "$(grep -o '\\includegraphics' "$DECK" | wc -l)" -ge 2 || {
  echo "La presentación debe incluir al menos dos evidencias visuales" >&2
  exit 1
}

if grep -Eq 'Ownership|Distributed Futures|Questions\?' "$DECK"; then
  echo "La presentación conserva contenido de ejemplo" >&2
  exit 1
fi

echo "Presentación Beamer verificada"
