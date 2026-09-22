#!/usr/bin/env bash
set -Eeuo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly REPOSITORY_DIR="$(cd "$SCRIPT_DIR/../../.." && pwd)"
readonly RUNNER="$REPOSITORY_DIR/hadoop3/scripts/run-job.sh"
readonly INPUT="$REPOSITORY_DIR/labs/labca1/data/Indicadores_ocupabilidad_2019_2025.csv"
readonly SUMMARY="$SCRIPT_DIR/execution-summary.tsv"

FROM=1
TO=14
DRY_RUN=false

declare -a MAIN_CLASSES=(
  ''
  labca1.query01.ArrivalsByOriginDriver
  labca1.query02.OccupancyByDepartmentDriver
  labca1.query03.OccupancyByClassDriver
  labca1.query04.EmploymentCapacityDriver
  labca1.query05.SeasonalityDriver
  labca1.query06.OccupancyStatisticsDriver
  labca1.query07.SubstringSearchDriver
  labca1.query08.OccupancyExtremesDriver
  labca1.query09.OccupancyChangeDriver
  labca1.query10.ForeignShareDriver
  labca1.query11.NaiveBayesClassificationDriver
  labca1.query12.LogisticClassificationDriver
  labca1.query13.LinearRegressionDriver
  labca1.query14.RidgeRegressionDriver
)

parse_arguments() {
  while (($#)); do
    case "$1" in
      --from) FROM="$2"; shift 2 ;;
      --to) TO="$2"; shift 2 ;;
      --dry-run) DRY_RUN=true; shift ;;
      *) printf 'ERROR: argumento desconocido: %s\n' "$1" >&2; exit 1 ;;
    esac
  done
  [[ "$FROM" =~ ^[0-9]+$ && "$TO" =~ ^[0-9]+$ ]] || { echo 'ERROR: rango inválido' >&2; exit 1; }
  ((FROM >= 1 && TO <= 14 && FROM <= TO)) || { echo 'ERROR: rango fuera de 1-14' >&2; exit 1; }
}

build_command() {
  local number="$1"
  local query="query$(printf '%02d' "$number")"
  local -n destination="$2"
  destination=(
    env "HADOOP_RESULTS_DIR=$SCRIPT_DIR/$query/resultados"
    "$RUNNER"
    --source "$SCRIPT_DIR/common/src"
    --source "$SCRIPT_DIR/$query/src"
    --main "${MAIN_CLASSES[$number]}"
    --input "$INPUT"
    --output "$query"
  )
  if ((number == 7)); then
    destination+=(--job-arg hotel)
  fi
}

print_command() {
  printf '%q ' "$@"
  printf '\n'
}

main() {
  parse_arguments "$@"
  if [[ "$DRY_RUN" == false ]]; then
    printf 'query\tmain_class\tstatus\thdfs_path\tlocal_result\tjar\n' >"$SUMMARY"
  fi

  local number query
  local -a command
  for ((number = FROM; number <= TO; number++)); do
    query="query$(printf '%02d' "$number")"
    build_command "$number" command
    if [[ "$DRY_RUN" == true ]]; then
      print_command "${command[@]}"
      continue
    fi
    printf '\n=== Ejecutando %s ===\n' "$query"
    "${command[@]}"
    printf '%s\t%s\tSUCCESS\t/labs/%s/output\t%s\t%s\n' \
      "$query" "${MAIN_CLASSES[$number]}" "$query" \
      "$SCRIPT_DIR/$query/resultados/$query/resultado.txt" \
      "$SCRIPT_DIR/$query/resultados/$query/$query.jar" >>"$SUMMARY"
  done
}

main "$@"
