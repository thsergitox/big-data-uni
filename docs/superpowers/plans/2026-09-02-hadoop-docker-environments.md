# Hadoop Docker Environments Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Entregar entornos Hadoop 2 y Hadoop 3 que ejecuten HDFS, YARN y MapReduce mediante imágenes oficiales, sin instalar Hadoop ni Java en Arch Linux.

**Architecture:** Cada versión usa un proyecto Docker Compose independiente con cinco procesos Hadoop aislados. Los cuatro XML de configuración se montan como solo lectura y HDFS persiste en volúmenes nombrados; scripts pequeños encapsulan arranque, parada, reinicio destructivo y una prueba `wordcount`.

**Tech Stack:** Docker Compose 5, Bash, Apache Hadoop 2.10.2 y 3.5.0.

**Spec:** `docs/superpowers/specs/2026-09-02-hadoop-docker-environments-design.md`

## Global Constraints

- No instalar Hadoop ni Java en el anfitrión.
- No ejecutar `docker build` ni `docker compose build`.
- Fijar `apache/hadoop:2.10.2` y `apache/hadoop:3.5.0`; no usar etiquetas flotantes.
- Ejecutar una versión a la vez porque ambas publican `8088`, `8042` y `19888`.
- Conservar datos de HDFS al ejecutar `stop.sh`.

---

### Task 1: Contrato estático de los dos entornos

**Files:**
- Create: `tests/test-hadoop-environments.sh`
- Create: `hadoop2/compose.yaml`
- Create: `hadoop3/compose.yaml`
- Create: `hadoop2/config/core-site.xml`
- Create: `hadoop2/config/hdfs-site.xml`
- Create: `hadoop2/config/mapred-site.xml`
- Create: `hadoop2/config/yarn-site.xml`
- Create: `hadoop3/config/core-site.xml`
- Create: `hadoop3/config/hdfs-site.xml`
- Create: `hadoop3/config/mapred-site.xml`
- Create: `hadoop3/config/yarn-site.xml`

**Interfaces:**
- Consumes: Docker Compose y las imágenes oficiales ya publicadas.
- Produces: proyectos Compose con servicios `namenode`, `datanode`, `resourcemanager`, `nodemanager` y `historyserver`.

- [ ] **Step 1: Escribir la prueba de contrato**

La prueba debe comprobar archivos requeridos, versiones exactas, servicios, puertos,
volúmenes, XML válido, `fs.defaultFS`, replicación `1`, YARN y JobHistory.

- [ ] **Step 2: Ejecutar la prueba y observar el fallo esperado**

Run: `bash tests/test-hadoop-environments.sh`
Expected: FAIL porque todavía no existen `hadoop2/compose.yaml` y `hadoop3/compose.yaml`.

- [ ] **Step 3: Implementar la configuración mínima**

Definir procesos directos como `hdfs namenode`, `hdfs datanode`,
`yarn resourcemanager`, `yarn nodemanager` y `mapred historyserver`. Montar cada XML
en `/opt/hadoop/etc/hadoop/` y persistir `/tmp/hadoop-root/dfs/name` y
`/tmp/hadoop-root/dfs/data`.

- [ ] **Step 4: Validar contratos y modelos Compose**

Run: `bash tests/test-hadoop-environments.sh`
Expected: PASS.

Run: `docker compose -f hadoop2/compose.yaml config --quiet && docker compose -f hadoop3/compose.yaml config --quiet`
Expected: exit 0.

### Task 2: Scripts operativos seguros

**Files:**
- Modify: `tests/test-hadoop-environments.sh`
- Create: `hadoop2/scripts/start.sh`
- Create: `hadoop2/scripts/stop.sh`
- Create: `hadoop2/scripts/reset.sh`
- Create: `hadoop2/scripts/smoke-test.sh`
- Create: `hadoop3/scripts/start.sh`
- Create: `hadoop3/scripts/stop.sh`
- Create: `hadoop3/scripts/reset.sh`
- Create: `hadoop3/scripts/smoke-test.sh`

**Interfaces:**
- Consumes: el `compose.yaml` del directorio padre de cada script.
- Produces: comandos sin argumentos para operar y validar el clúster correspondiente.

- [ ] **Step 1: Ampliar la prueba para exigir scripts ejecutables y seguros**

Comprobar `set -Eeuo pipefail`, resolución de ruta independiente del directorio
actual, confirmación destructiva y uso de un directorio HDFS exclusivo para la prueba.

- [ ] **Step 2: Ejecutar la prueba y observar el fallo esperado**

Run: `bash tests/test-hadoop-environments.sh`
Expected: FAIL porque los scripts todavía no existen.

- [ ] **Step 3: Implementar scripts mínimos**

`start.sh` usa `docker compose up -d`; `stop.sh` usa `docker compose down` sin `-v`;
`reset.sh` exige escribir `BORRAR`; `smoke-test.sh` espera servicios, carga texto,
ejecuta el JAR de ejemplos detectado dentro de la imagen y valida conteos.

- [ ] **Step 4: Validar contrato y sintaxis**

Run: `bash tests/test-hadoop-environments.sh`
Expected: PASS.

Run: `find hadoop2/scripts hadoop3/scripts -type f -name '*.sh' -exec bash -n {} +`
Expected: exit 0.

### Task 3: Guía de laboratorio y verificación real

**Files:**
- Modify: `tests/test-hadoop-environments.sh`
- Create: `README.md`

**Interfaces:**
- Consumes: scripts de operación y URLs publicadas.
- Produces: una guía con inicio rápido, comandos HDFS/MapReduce, paneles web,
  solución de problemas y limpieza.

- [ ] **Step 1: Ampliar la prueba para exigir documentación esencial**

Comprobar que el README incluye las dos versiones, inicio, prueba, acceso shell,
URLs y advertencia de no ejecutar ambos entornos simultáneamente.

- [ ] **Step 2: Ejecutar la prueba y observar el fallo esperado**

Run: `bash tests/test-hadoop-environments.sh`
Expected: FAIL porque `README.md` todavía no existe.

- [ ] **Step 3: Escribir la guía mínima completa**

Documentar `./scripts/start.sh`, `./scripts/smoke-test.sh`, `docker compose exec`,
comandos `hdfs dfs`, paneles web y `./scripts/reset.sh`.

- [ ] **Step 4: Ejecutar verificaciones estáticas completas**

Run: `bash tests/test-hadoop-environments.sh`
Expected: PASS.

- [ ] **Step 5: Ejecutar cada entorno y su prueba de humo, uno por vez**

Run: `cd hadoop3 && ./scripts/start.sh && ./scripts/smoke-test.sh && ./scripts/stop.sh`
Expected: HDFS saludable, YARN con un NodeManager y conteos correctos.

Run: `cd hadoop2 && ./scripts/start.sh && ./scripts/smoke-test.sh && ./scripts/stop.sh`
Expected: HDFS saludable, YARN con un NodeManager y conteos correctos.

- [ ] **Step 6: Verificación final sin build**

Run: `bash tests/test-hadoop-environments.sh && find hadoop2/scripts hadoop3/scripts -type f -name '*.sh' -exec bash -n {} +`
Expected: todas las pruebas pasan sin ejecutar ningún build.

