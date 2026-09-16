#!/usr/bin/env bash
set -Eeuo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
readonly COMPOSE_FILE="$PROJECT_DIR/compose.yaml"
readonly -a COMPOSE=(docker compose --project-directory "$PROJECT_DIR" -f "$COMPOSE_FILE")

SOURCE_DIR=''
MAIN_CLASS=''
INPUT_PATH=''
OUTPUT_NAME=''
REMOTE_ROOT=''

usage() {
  cat <<'EOF'
Uso:
  run-job.sh --source <carpeta-java> --main <clase-principal> \
    --input <archivo-o-carpeta> --output <nombre>

Ejemplo:
  ./hadoop3/scripts/run-job.sh \
    --source Ventas \
    --main SalesCountry.SalesCountryDriver \
    --input Ventas/SalesJan2009.csv \
    --output ventas

El nombre de salida identifica las rutas HDFS:
  /labs/<nombre>/input
  /labs/<nombre>/output
EOF
}

die() {
  printf 'ERROR: %s\n' "$1" >&2
  exit 1
}

parse_arguments() {
  while (($#)); do
    case "$1" in
      --source)
        (($# >= 2)) || die 'falta el valor de --source'
        SOURCE_DIR="$2"
        shift 2
        ;;
      --main)
        (($# >= 2)) || die 'falta el valor de --main'
        MAIN_CLASS="$2"
        shift 2
        ;;
      --input)
        (($# >= 2)) || die 'falta el valor de --input'
        INPUT_PATH="$2"
        shift 2
        ;;
      --output)
        (($# >= 2)) || die 'falta el valor de --output'
        OUTPUT_NAME="$2"
        shift 2
        ;;
      --help|-h)
        usage
        exit 0
        ;;
      *)
        die "argumento desconocido: $1"
        ;;
    esac
  done
}

prompt_for_missing_arguments() {
  if [[ -z "$SOURCE_DIR" ]]; then
    printf 'Carpeta con los archivos .java: '
    IFS= read -r SOURCE_DIR || die 'no se pudo leer la carpeta de fuentes'
  fi
  if [[ -z "$MAIN_CLASS" ]]; then
    printf 'Clase principal: '
    IFS= read -r MAIN_CLASS || die 'no se pudo leer la clase principal'
  fi
  if [[ -z "$INPUT_PATH" ]]; then
    printf 'Archivo o carpeta de entrada: '
    IFS= read -r INPUT_PATH || die 'no se pudo leer la entrada'
  fi
  if [[ -z "$OUTPUT_NAME" ]]; then
    printf 'Nombre de salida: '
    IFS= read -r OUTPUT_NAME || die 'no se pudo leer el nombre de salida'
  fi
}

validate_arguments() {
  [[ -n "$SOURCE_DIR" ]] || die 'debes indicar --source'
  [[ -n "$MAIN_CLASS" ]] || die 'debes indicar --main'
  [[ -n "$INPUT_PATH" ]] || die 'debes indicar --input'
  [[ -n "$OUTPUT_NAME" ]] || die 'debes indicar --output'
  [[ -d "$SOURCE_DIR" ]] || die "no existe la carpeta de fuentes: $SOURCE_DIR"
  [[ -e "$INPUT_PATH" ]] || die "no existe la entrada: $INPUT_PATH"
  [[ "$OUTPUT_NAME" =~ ^[A-Za-z0-9._-]+$ ]] || \
    die '--output solo admite letras, números, punto, guion y guion bajo'
  find "$SOURCE_DIR" -type f -name '*.java' -print -quit | grep -q . || \
    die "no hay archivos .java en: $SOURCE_DIR"

  if [[ -d "$INPUT_PATH" ]]; then
    find "$INPUT_PATH" -maxdepth 1 -type f -print -quit | grep -q . || \
      die 'la carpeta de entrada no contiene archivos'
  fi

  command -v docker >/dev/null || die 'Docker no está instalado o no está en PATH'
}

ensure_cluster_is_running() {
  local running_services
  running_services="$("${COMPOSE[@]}" ps --status running --services 2>/dev/null || true)"

  for service in namenode datanode resourcemanager nodemanager historyserver; do
    if ! grep -qx "$service" <<<"$running_services"; then
      printf 'Iniciando Hadoop 3...\n'
      "$SCRIPT_DIR/start.sh"
      return
    fi
  done
}

prepare_remote_workspace() {
  REMOTE_ROOT="/tmp/hadoop-runner-$OUTPUT_NAME"

  "${COMPOSE[@]}" exec -T namenode \
    rm -rf "$REMOTE_ROOT"
  "${COMPOSE[@]}" exec -T namenode \
    mkdir -p "$REMOTE_ROOT/source" "$REMOTE_ROOT/input" "$REMOTE_ROOT/classes"
  "${COMPOSE[@]}" exec -T namenode \
    chown -R hadoop:users "$REMOTE_ROOT"

  "${COMPOSE[@]}" cp "$SOURCE_DIR/." "namenode:$REMOTE_ROOT/source/"
  if [[ -d "$INPUT_PATH" ]]; then
    "${COMPOSE[@]}" cp "$INPUT_PATH/." "namenode:$REMOTE_ROOT/input/"
  else
    "${COMPOSE[@]}" cp "$INPUT_PATH" "namenode:$REMOTE_ROOT/input/data"
  fi
  "${COMPOSE[@]}" exec -T namenode \
    chown -R hadoop:users "$REMOTE_ROOT"
}

compile_job() {
  "${COMPOSE[@]}" exec -T --user hadoop namenode bash -c '
    set -Eeuo pipefail
    remote_root="$1"
    mapfile -d "" sources < <(find "$remote_root/source" -type f -name "*.java" -print0)
    ((${#sources[@]} > 0))
    javac -cp "$(hadoop classpath)" -d "$remote_root/classes" "${sources[@]}"
    jar -cf "$remote_root/job.jar" -C "$remote_root/classes" .
  ' bash "$REMOTE_ROOT"
}

upload_input() {
  local hdfs_base="/labs/$OUTPUT_NAME"
  local hdfs_input="$hdfs_base/input"

  "${COMPOSE[@]}" exec -T --user hadoop namenode \
    hdfs dfs -rm -r -f "$hdfs_base"
  "${COMPOSE[@]}" exec -T --user hadoop namenode \
    hdfs dfs -mkdir -p "$hdfs_input"
  "${COMPOSE[@]}" exec -T --user hadoop namenode bash -c '
    set -Eeuo pipefail
    shopt -s nullglob
    files=("$1"/*)
    ((${#files[@]} > 0))
    hdfs dfs -put -f "${files[@]}" "$2"
  ' bash "$REMOTE_ROOT/input" "$hdfs_input"
}

run_job() {
  local hdfs_input="/labs/$OUTPUT_NAME/input"
  local hdfs_output="/labs/$OUTPUT_NAME/output"

  "${COMPOSE[@]}" exec -T --user hadoop namenode \
    hadoop jar "$REMOTE_ROOT/job.jar" "$MAIN_CLASS" "$hdfs_input" "$hdfs_output"

  "${COMPOSE[@]}" exec -T --user hadoop namenode \
    hdfs dfs -test -e "$hdfs_output/_SUCCESS" || \
    die "el trabajo no generó $hdfs_output/_SUCCESS"

  printf '\nResultado de %s:\n' "$hdfs_output"
  "${COMPOSE[@]}" exec -T --user hadoop namenode \
    hdfs dfs -cat "$hdfs_output/part-*"
}

cleanup_remote_workspace() {
  [[ -n "$REMOTE_ROOT" ]] || return 0
  "${COMPOSE[@]}" exec -T namenode rm -rf "$REMOTE_ROOT" >/dev/null 2>&1 || true
}

main() {
  parse_arguments "$@"
  prompt_for_missing_arguments
  validate_arguments
  ensure_cluster_is_running
  prepare_remote_workspace
  trap cleanup_remote_workspace EXIT

  printf 'Compilando %s...\n' "$SOURCE_DIR"
  compile_job
  upload_input
  run_job
}

main "$@"
