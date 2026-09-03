#!/usr/bin/env bash
set -Eeuo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
readonly COMPOSE_FILE="$PROJECT_DIR/compose.yaml"

printf 'Esto eliminará TODOS los datos persistentes de Hadoop 2.\n'
read -r -p 'Escribe BORRAR para continuar: ' confirmation

if [[ "$confirmation" != "BORRAR" ]]; then
  printf 'Operación cancelada.\n'
  exit 0
fi

docker compose --project-directory "$PROJECT_DIR" -f "$COMPOSE_FILE" \
  down --volumes --remove-orphans

printf 'Entorno Hadoop 2 reiniciado; los datos persistentes fueron eliminados.\n'

