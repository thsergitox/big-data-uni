# Herramientas y operación del laboratorio

## Qué herramientas estamos usando

| Herramienta | Para qué sirve |
| --- | --- |
| Docker Engine | Ejecuta los contenedores. |
| Docker Compose | Declara y coordina los cinco servicios. |
| `hadoop` | Utilidades comunes y ejecución de archivos JAR. |
| `hdfs` | Operación y administración de HDFS. |
| `yarn` | Consulta y administración de aplicaciones y nodos. |
| `mapred` | Utilidades específicas de MapReduce y JobHistory. |
| Interfaces web | Observación del estado; no sustituyen la CLI ni las APIs. |

## Ciclo normal de una práctica

Desde la versión que quieras usar:

```bash
cd hadoop3              # o hadoop2
./scripts/start.sh
./scripts/smoke-test.sh
```

Trabaja con HDFS o ejecuta jobs. Al terminar:

```bash
./scripts/stop.sh
```

Los datos permanecen en volúmenes. Usa `reset.sh` únicamente si deseas eliminar
HDFS y empezar desde cero.

## Tres sistemas de archivos que no debes confundir

1. **Host Arch Linux:** tus archivos normales del proyecto.
2. **Filesystem del contenedor:** por ejemplo `/tmp/entrada.txt` dentro de
   `namenode`.
3. **HDFS:** por ejemplo `/user/hadoop/entrada.txt`, accesible mediante `hdfs dfs`.

Entrar al contenedor no coloca automáticamente un archivo en HDFS. Primero existe
localmente y luego se copia con `-put` o `-copyFromLocal`.

## Comandos HDFS esenciales

Ejecuta una shell en el NameNode:

```bash
docker compose exec --user hadoop namenode bash
```

### Navegación y directorios

```bash
hdfs dfs -ls /
hdfs dfs -mkdir -p /user/hadoop/datos
hdfs dfs -du -h /user/hadoop
```

### Copiar datos

```bash
hdfs dfs -put /tmp/entrada.txt /user/hadoop/datos/
hdfs dfs -get /user/hadoop/datos/entrada.txt /tmp/copia.txt
```

### Leer y eliminar

```bash
hdfs dfs -cat /user/hadoop/datos/entrada.txt
hdfs dfs -tail /user/hadoop/datos/entrada.txt
hdfs dfs -rm /user/hadoop/datos/entrada.txt
hdfs dfs -rm -r /user/hadoop/datos
```

### Diagnóstico

```bash
hdfs dfsadmin -report
hdfs dfsadmin -safemode get
hdfs fsck / -files -blocks -locations
```

`dfsadmin` es administrativo; `dfs` trabaja con archivos; `fsck` comprueba la
salud lógica de archivos y bloques.

## Comandos YARN esenciales

Desde el host:

```bash
docker compose exec resourcemanager yarn node -list
docker compose exec resourcemanager yarn application -list
docker compose exec resourcemanager yarn application -list -appStates ALL
```

Si tienes un identificador como `application_...`:

```bash
docker compose exec resourcemanager yarn application -status ID
docker compose exec resourcemanager yarn logs -applicationId ID
```

## Ejecutar un JAR MapReduce

Dentro del NameNode:

```bash
EXAMPLES_JAR="$(find /opt/hadoop/share/hadoop/mapreduce \
  -name 'hadoop-mapreduce-examples-*.jar' | head -n 1)"

hadoop jar "$EXAMPLES_JAR" wordcount \
  /laboratorio/entrada /laboratorio/salida
```

Hadoop exige que el directorio de salida no exista. Esto evita sobrescribir
resultados accidentalmente:

```bash
hdfs dfs -rm -r -f /laboratorio/salida
```

## Qué mirar en cada interfaz

### NameNode

- capacidad usada y disponible;
- número de DataNodes activos;
- bloques faltantes o subreplicados;
- exploración de archivos HDFS.

### ResourceManager

- aplicación en estado `ACCEPTED`, `RUNNING`, `FINISHED` o `FAILED`;
- memoria y vcores solicitados;
- nodo donde se ejecuta;
- enlaces a intentos y logs.

### NodeManager

- salud del nodo;
- contenedores YARN ejecutados localmente;
- uso de recursos y logs locales.

### JobHistory

- duración de maps y reduces;
- contadores de registros y bytes;
- intentos de tareas;
- diagnóstico de jobs terminados.

## Docker: estado y logs

```bash
docker compose ps
docker compose logs --tail=100 namenode
docker compose logs --tail=100 datanode
docker compose logs --tail=100 resourcemanager
docker compose logs --tail=100 nodemanager
docker compose logs --tail=100 historyserver
```

Para seguir los logs en tiempo real:

```bash
docker compose logs -f resourcemanager nodemanager
```

## Cómo diagnosticar sin adivinar

Sigue este orden:

1. `docker compose ps`: ¿el contenedor existe y está saludable?
2. logs del servicio que falla: ¿hay error de puerto, memoria o permisos?
3. NameNode: ¿existe un DataNode vivo?
4. ResourceManager: ¿existe un NodeManager en `RUNNING`?
5. UI de YARN/JobHistory: ¿qué tarea y qué intento fallaron?
6. `hdfs fsck`: ¿la entrada está disponible?

Reiniciar sin leer el error puede ocultar el síntoma, pero NO enseña la causa.

## Prueba automatizada incluida

`./scripts/smoke-test.sh` valida una cadena completa:

1. HDFS responde y tiene un DataNode vivo.
2. YARN registra un NodeManager.
3. Se carga un archivo en HDFS.
4. WordCount se ejecuta sobre YARN.
5. La salida coincide con los conteos esperados.

Esta prueba es más valiosa que verificar únicamente que los contenedores estén
encendidos: demuestra que almacenamiento, scheduling, ejecución y salida
colaboran correctamente.

## Fuentes oficiales

- [File System Shell](https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-common/FileSystemShell.html)
- [Comandos de HDFS](https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-hdfs/HDFSCommands.html)
- [Comandos de YARN](https://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/YarnCommands.html)

**Siguiente:** [Potencia, casos de uso y limitaciones](06-potencia-casos-de-uso-y-limitaciones.md).

