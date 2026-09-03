#!/usr/bin/env bash
set -Eeuo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
readonly COMPOSE_FILE="$PROJECT_DIR/compose.yaml"
readonly OTHER_PROJECT="hadoop3-lab"

if [[ -n "$(docker ps --quiet --filter "label=com.docker.compose.project=$OTHER_PROJECT")" ]]; then
  printf 'ERROR: Hadoop 3 está activo y usa los mismos puertos. Deténlo primero.\n' >&2
  exit 1
fi

docker compose --project-directory "$PROJECT_DIR" -f "$COMPOSE_FILE" \
  up -d --wait --wait-timeout 240

printf '\nHadoop 2 está activo:\n'
printf '  HDFS NameNode:  http://localhost:50070\n'
printf '  YARN:           http://localhost:8088\n'
printf '  NodeManager:    http://localhost:8042\n'
printf '  JobHistory:     http://localhost:19888\n'
printf '\nValida el laboratorio con: %s/smoke-test.sh\n' "$SCRIPT_DIR"

