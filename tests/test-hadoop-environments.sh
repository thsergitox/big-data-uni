#!/usr/bin/env bash
set -Eeuo pipefail

readonly ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

fail() {
  printf 'FAIL: %s\n' "$1" >&2
  exit 1
}

assert_file() {
  local path="$1"
  [[ -f "$path" ]] || fail "falta el archivo ${path#"$ROOT_DIR/"}"
}

assert_contains() {
  local path="$1"
  local pattern="$2"
  grep -Fq -- "$pattern" "$path" || fail "${path#"$ROOT_DIR/"} no contiene: $pattern"
}

assert_compose_contract() {
  local version_dir="$1"
  local image="$2"
  local namenode_port="$3"
  local compose="$ROOT_DIR/$version_dir/compose.yaml"

  assert_file "$compose"
  docker compose -f "$compose" config --quiet

  local services
  services="$(docker compose -f "$compose" config --services)"
  for service in namenode datanode resourcemanager nodemanager historyserver; do
    grep -qx "$service" <<<"$services" || fail "$version_dir no define el servicio $service"
  done

  local image_count
  image_count="$(grep -Fc "image: $image" "$compose")"
  [[ "$image_count" -eq 5 ]] || fail "$version_dir debe fijar $image en sus cinco servicios"

  assert_contains "$compose" "\"$namenode_port:$namenode_port\""
  assert_contains "$compose" '"8088:8088"'
  assert_contains "$compose" '"8042:8042"'
  assert_contains "$compose" '"19888:19888"'
  assert_contains "$compose" 'namenode-data:'
  assert_contains "$compose" 'datanode-data:'
  assert_contains "$compose" 'current/VERSION'
  assert_contains "$compose" 'hdfs namenode -format -force'
  assert_contains "$compose" 'sudo -E -u hadoop /opt/hadoop/bin/hdfs'
  assert_contains "$compose" 'sudo -E -u hadoop /opt/hadoop/bin/hdfs dfsadmin -report'
  assert_contains "$compose" 'chown -R hadoop:users /data/namenode'
  assert_contains "$compose" 'chown -R hadoop:users /data/datanode'
  if grep -Fq 'ENSURE_NAMENODE_DIR' "$compose"; then
    fail "$version_dir no debe detectar inicialización solo por existencia del directorio"
  fi
}

assert_hadoop_configuration() {
  local version_dir="$1"
  local config_dir="$ROOT_DIR/$version_dir/config"
  local file

  for file in core-site.xml hdfs-site.xml mapred-site.xml yarn-site.xml; do
    assert_file "$config_dir/$file"
  done

  python3 - "$config_dir" <<'PY'
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

config_dir = Path(sys.argv[1])
required = {
    "core-site.xml": {"fs.defaultFS": "hdfs://namenode:8020"},
    "hdfs-site.xml": {
        "dfs.replication": "1",
        "dfs.namenode.name.dir": "file:///data/namenode",
        "dfs.datanode.data.dir": "file:///data/datanode",
    },
    "mapred-site.xml": {
        "mapreduce.framework.name": "yarn",
        "mapreduce.jobhistory.address": "historyserver:10020",
        "mapreduce.jobhistory.webapp.address": "historyserver:19888",
    },
    "yarn-site.xml": {
        "yarn.resourcemanager.hostname": "resourcemanager",
        "yarn.nodemanager.aux-services": "mapreduce_shuffle",
    },
}

for filename, expected in required.items():
    root = ET.parse(config_dir / filename).getroot()
    properties = {
        node.findtext("name"): node.findtext("value")
        for node in root.findall("property")
    }
    for name, value in expected.items():
        actual = properties.get(name)
        if actual != value:
            raise SystemExit(
                f"FAIL: {config_dir.name}/{filename}: {name}={actual!r}; esperado {value!r}"
            )
PY
}

assert_operational_scripts() {
  local version_dir="$1"
  local scripts_dir="$ROOT_DIR/$version_dir/scripts"
  local script

  for script in start.sh stop.sh reset.sh smoke-test.sh; do
    assert_file "$scripts_dir/$script"
    [[ -x "$scripts_dir/$script" ]] || fail "$version_dir/scripts/$script no es ejecutable"
    assert_contains "$scripts_dir/$script" 'set -Eeuo pipefail'
    assert_contains "$scripts_dir/$script" 'BASH_SOURCE[0]'
  done

  assert_contains "$scripts_dir/start.sh" 'up -d --wait'
  assert_contains "$scripts_dir/stop.sh" 'down --remove-orphans'
  if grep -Eq 'down[^\n]*(-v|--volumes)' "$scripts_dir/stop.sh"; then
    fail "$version_dir/scripts/stop.sh no debe eliminar volúmenes"
  fi

  assert_contains "$scripts_dir/reset.sh" 'BORRAR'
  assert_contains "$scripts_dir/reset.sh" 'down --volumes --remove-orphans'
  assert_contains "$scripts_dir/smoke-test.sh" '/labs/docker-smoke'
  assert_contains "$scripts_dir/smoke-test.sh" 'hadoop-mapreduce-examples-'
  assert_contains "$scripts_dir/smoke-test.sh" 'wordcount'
  assert_contains "$scripts_dir/smoke-test.sh" 'exec -T --user hadoop namenode'
  assert_contains "$scripts_dir/smoke-test.sh" 'hdfs_report='
  assert_contains "$scripts_dir/smoke-test.sh" 'yarn_nodes='
  if [[ "$version_dir" == 'hadoop3' ]]; then
    assert_file "$scripts_dir/run-job.sh"
    [[ -x "$scripts_dir/run-job.sh" ]] || fail 'hadoop3/scripts/run-job.sh no es ejecutable'
    assert_contains "$scripts_dir/run-job.sh" '--source'
    assert_contains "$scripts_dir/run-job.sh" '--main'
    assert_contains "$scripts_dir/run-job.sh" '--input'
    assert_contains "$scripts_dir/run-job.sh" '--output'
    assert_contains "$scripts_dir/run-job.sh" 'hadoop classpath'
  fi
}

assert_documentation() {
  local readme="$ROOT_DIR/README.md"

  assert_file "$readme"
  assert_contains "$readme" 'Hadoop 2.10.2'
  assert_contains "$readme" 'Hadoop 3.5.0'
  assert_contains "$readme" './scripts/start.sh'
  assert_contains "$readme" './scripts/smoke-test.sh'
  assert_contains "$readme" 'docker compose exec --user hadoop namenode bash'
  assert_contains "$readme" 'hdfs dfs -ls /'
  assert_contains "$readme" 'http://localhost:50070'
  assert_contains "$readme" 'http://localhost:9870'
  assert_contains "$readme" 'http://localhost:8088'
  assert_contains "$readme" 'No ejecutes ambas versiones simultáneamente'
  assert_contains "$readme" './hadoop3/scripts/run-job.sh'
  assert_contains "$readme" '--main SalesCountry.SalesCountryDriver'
  assert_contains "$readme" 'modo interactivo solicita la carpeta'
}

assert_compose_contract hadoop2 apache/hadoop:2.10.2 50070
assert_hadoop_configuration hadoop2
assert_operational_scripts hadoop2
assert_compose_contract hadoop3 apache/hadoop:3.5.0 9870
assert_hadoop_configuration hadoop3
assert_operational_scripts hadoop3
assert_documentation

printf 'PASS: contratos estáticos de Hadoop 2 y Hadoop 3\n'
