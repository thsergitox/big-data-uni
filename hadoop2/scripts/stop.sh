#!/usr/bin/env bash
set -Eeuo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
readonly COMPOSE_FILE="$PROJECT_DIR/compose.yaml"

docker compose --project-directory "$PROJECT_DIR" -f "$COMPOSE_FILE" \
  down --remove-orphans

printf 'Hadoop 2 se detuvo. Los datos de HDFS se conservaron.\n'

