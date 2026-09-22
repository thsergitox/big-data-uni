#!/usr/bin/env bash
set -Eeuo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly SOURCE_DIR="$SCRIPT_DIR/common/src"
readonly TEST_DIR="$SCRIPT_DIR/common/test"

build_dir="$(mktemp -d)"
trap 'rm -rf "$build_dir"' EXIT

mapfile -d '' sources < <(find "$SOURCE_DIR" "$TEST_DIR" -type f -name '*.java' -print0)
javac -d "$build_dir" "${sources[@]}"
java -ea -cp "$build_dir" labca1.common.CommonTest
