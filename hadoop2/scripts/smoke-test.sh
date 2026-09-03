#!/usr/bin/env bash
set -Eeuo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
readonly COMPOSE_FILE="$PROJECT_DIR/compose.yaml"
readonly HDFS_BASE="/labs/docker-smoke"
readonly HDFS_INPUT="$HDFS_BASE/input"
readonly HDFS_OUTPUT="$HDFS_BASE/output"
readonly LOCAL_INPUT="/tmp/hadoop-docker-smoke.txt"
readonly -a COMPOSE=(docker compose --project-directory "$PROJECT_DIR" -f "$COMPOSE_FILE")

wait_for_hdfs() {
  local attempt
  local hdfs_report
  for attempt in {1..60}; do
    hdfs_report="$("${COMPOSE[@]}" exec -T --user hadoop namenode \
      hdfs dfsadmin -report 2>/dev/null || true)"
    if grep -q 'Live datanodes (1)' <<<"$hdfs_report"; then
      return 0
    fi
    sleep 2
  done
  printf 'ERROR: HDFS no registró un DataNode a tiempo.\n' >&2
  return 1
}

wait_for_yarn() {
  local attempt
  local yarn_nodes
  for attempt in {1..60}; do
    yarn_nodes="$("${COMPOSE[@]}" exec -T resourcemanager \
      yarn node -list 2>/dev/null || true)"
    if grep -q 'RUNNING' <<<"$yarn_nodes"; then
      return 0
    fi
    sleep 2
  done
  printf 'ERROR: YARN no registró un NodeManager a tiempo.\n' >&2
  return 1
}

wait_for_hdfs
wait_for_yarn

"${COMPOSE[@]}" exec -T --user hadoop namenode hdfs dfs -mkdir -p \
  /tmp /user/hadoop /user/history/done /user/history/intermediate-done
"${COMPOSE[@]}" exec -T --user hadoop namenode hdfs dfs -chmod 1777 /tmp /user/history
"${COMPOSE[@]}" exec -T --user hadoop namenode hdfs dfs -rm -r -f "$HDFS_BASE"
"${COMPOSE[@]}" exec -T --user hadoop namenode bash -c \
  "printf 'hadoop big data\nhadoop docker\nbig data\n' > '$LOCAL_INPUT'"
"${COMPOSE[@]}" exec -T --user hadoop namenode hdfs dfs -mkdir -p "$HDFS_INPUT"
"${COMPOSE[@]}" exec -T --user hadoop namenode hdfs dfs -put "$LOCAL_INPUT" "$HDFS_INPUT/input.txt"

jar_path="$("${COMPOSE[@]}" exec -T --user hadoop namenode bash -c \
  "ls /opt/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-*.jar | head -n 1")"
jar_path="${jar_path//$'\r'/}"

"${COMPOSE[@]}" exec -T --user hadoop namenode \
  hadoop jar "$jar_path" wordcount "$HDFS_INPUT" "$HDFS_OUTPUT"

result="$("${COMPOSE[@]}" exec -T --user hadoop namenode \
  hdfs dfs -cat "$HDFS_OUTPUT/part-r-00000" | sort)"
expected=$'big\t2\ndata\t2\ndocker\t1\nhadoop\t2'

if [[ "$result" != "$expected" ]]; then
  printf 'ERROR: WordCount produjo un resultado inesperado:\n%s\n' "$result" >&2
  exit 1
fi

printf 'PASS: Hadoop 2 ejecutó HDFS + YARN + MapReduce correctamente.\n'
printf '%s\n' "$result"
