#!/usr/bin/env bash
set -euo pipefail

ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
REPORT="$ROOT/Plantilla201latex/Template.tex"
RESEARCH="$ROOT/referencias"

test -f "$RESEARCH/referencias.bib"

if grep -q 'CONTENIDO A INTEGRAR POR EL COMPAÑERO' "$REPORT"; then
  echo "El informe todavía conserva marcadores de integración" >&2
  exit 1
fi

for key in \
  mincetur_ocupabilidad \
  mef_ficha_tecnica_turismo2021 \
  inei_rj037_2024 \
  taipe_laura_desestacionalizacion2021 \
  nahuincopa_rojas_demanda2022 \
  bravo_moche_ml2023 \
  viteri_hadoop_minisa2023; do
  grep -q "\\\\cite{[^}]*$key" "$REPORT" || {
    echo "Falta citar $key en el informe" >&2
    exit 1
  }
  test "$(grep -Eic "^@[A-Za-z]+\\{$key," "$RESEARCH/referencias.bib")" -eq 1
done

grep -q 'cambio de esquema' "$REPORT"
grep -q 'doble conteo' "$REPORT"
grep -q 'división temporal' "$REPORT"
grep -q 'Docker Compose' "$REPORT"
grep -Fq '../referencias/referencias' "$REPORT"

echo "Integración de investigación verificada"
