#!/usr/bin/env bash
set -Eeuo pipefail

readonly ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
readonly RUNNER="$ROOT_DIR/hadoop3/scripts/run-job.sh"

fail() {
  printf 'FAIL: %s\n' "$1" >&2
  exit 1
}

assert_contains() {
  local path="$1"
  local expected="$2"
  grep -Fq -- "$expected" "$path" || fail "no se encontró '$expected' en $path"
}

create_fake_docker() {
  local bin_dir="$1"

  cat >"$bin_dir/docker" <<'EOF'
#!/usr/bin/env bash
set -Eeuo pipefail

printf '%s ' "$@" >>"$DOCKER_CALLS"
printf '\n' >>"$DOCKER_CALLS"

if [[ "$*" == *"ps --status running --services"* ]]; then
  printf '%s\n' namenode datanode resourcemanager nodemanager historyserver
fi

if [[ "$*" == *"hdfs dfs -cat"* ]]; then
  printf 'Lima\tVisa\t2\n'
fi

if [[ "$*" == *"cp namenode:"* ]]; then
  destination="${@: -1}"
  mkdir -p "$(dirname "$destination")"
  printf 'Lima\tVisa\t2\n' >"$destination"
fi
EOF
  chmod +x "$bin_dir/docker"
}

test_runs_a_java_job_with_one_command() {
  local temp_dir
  temp_dir="$(mktemp -d)"
  trap 'rm -rf "$temp_dir"' RETURN

  mkdir -p "$temp_dir/bin" "$temp_dir/source"
  printf 'class Example {}\n' >"$temp_dir/source/Example.java"
  printf 'row\n' >"$temp_dir/input.csv"
  : >"$temp_dir/docker-calls"
  create_fake_docker "$temp_dir/bin"

  PATH="$temp_dir/bin:$PATH" DOCKER_CALLS="$temp_dir/docker-calls" \
    HADOOP_RESULTS_DIR="$temp_dir/results" \
    "$RUNNER" \
      --source "$temp_dir/source" \
      --main example.ExampleDriver \
      --input "$temp_dir/input.csv" \
      --output examen

  assert_contains "$temp_dir/docker-calls" 'cp'
  assert_contains "$temp_dir/docker-calls" 'javac'
  assert_contains "$temp_dir/docker-calls" 'jar -cf'
  assert_contains "$temp_dir/docker-calls" 'hdfs dfs -put'
  assert_contains "$temp_dir/docker-calls" 'hadoop jar'
  assert_contains "$temp_dir/docker-calls" 'example.ExampleDriver'
  assert_contains "$temp_dir/docker-calls" '/labs/examen/input'
  assert_contains "$temp_dir/docker-calls" '/labs/examen/output'
  assert_contains "$temp_dir/docker-calls" 'hdfs dfs -cat'
  assert_contains "$temp_dir/docker-calls" 'hdfs dfs -getmerge'
  [[ -f "$temp_dir/results/examen/resultado.txt" ]] || \
    fail 'no se exportó resultado.txt al host'
}

test_prompts_for_missing_values() {
  local temp_dir
  local output
  temp_dir="$(mktemp -d)"
  trap 'rm -rf "$temp_dir"' RETURN

  mkdir -p "$temp_dir/bin" "$temp_dir/source"
  printf 'class Example {}\n' >"$temp_dir/source/Example.java"
  printf 'row\n' >"$temp_dir/input.csv"
  : >"$temp_dir/docker-calls"
  create_fake_docker "$temp_dir/bin"

  output="$(
    printf '%s\n' \
      "$temp_dir/source" \
      'example.ExampleDriver' \
      "$temp_dir/input.csv" \
      'interactivo' |
      env PATH="$temp_dir/bin:$PATH" DOCKER_CALLS="$temp_dir/docker-calls" \
        HADOOP_RESULTS_DIR="$temp_dir/results" \
        "$RUNNER"
  )"

  grep -Fq 'Carpeta con los archivos .java:' <<<"$output" || \
    fail 'no se solicitó la carpeta de fuentes'
  grep -Fq 'Clase principal:' <<<"$output" || \
    fail 'no se solicitó la clase principal'
  grep -Fq 'Archivo o carpeta de entrada:' <<<"$output" || \
    fail 'no se solicitó la entrada'
  grep -Fq 'Nombre de salida:' <<<"$output" || \
    fail 'no se solicitó el nombre de salida'
  assert_contains "$temp_dir/docker-calls" 'example.ExampleDriver'
  assert_contains "$temp_dir/docker-calls" '/labs/interactivo/output'
  [[ -f "$temp_dir/results/interactivo/resultado.txt" ]] || \
    fail 'el modo interactivo no exportó resultado.txt al host'
}

test_runs_a_java_job_with_one_command
test_prompts_for_missing_values

printf 'PASS: ejecutor de trabajos Hadoop\n'
