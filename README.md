# Laboratorios de Big Data con Hadoop en Docker

Este proyecto proporciona Hadoop real dentro de contenedores. No instala Hadoop,
Java ni `winutils` en Arch Linux.

Incluye dos entornos independientes:

- **Hadoop 2.10.2**: última imagen oficial disponible de la rama 2.
- **Hadoop 3.5.0**: imagen oficial actual de la rama 3.

Cada entorno ejecuta HDFS, YARN y MapReduce mediante NameNode, DataNode,
ResourceManager, NodeManager y JobHistory Server.

## Documentación conceptual

Antes de limitarte a copiar comandos, entiende qué hace cada pieza. La
[guía de estudio de Hadoop](docs/hadoop/README.md) cubre:

- fundamentos y arquitectura de Hadoop;
- HDFS, bloques, replicación, NameNode y DataNode;
- YARN, los servicios y todos los puertos del laboratorio;
- MapReduce, incluido el flujo Map → Shuffle/Sort → Reduce;
- herramientas, operación, casos de uso y limitaciones.

## Requisito

Solo necesitas Docker con el complemento Compose:

```bash
docker --version
docker compose version
```

El primer inicio descarga la imagen seleccionada y puede tardar varios minutos.
Los siguientes inicios reutilizan la imagen local.

> **Importante:** No ejecutes ambas versiones simultáneamente. Comparten los
> puertos `8088`, `8042` y `19888`.

## Hadoop 3: inicio rápido

```bash
cd hadoop3
./scripts/start.sh
./scripts/smoke-test.sh
```

La prueba carga datos en HDFS y ejecuta `wordcount` con MapReduce sobre YARN. Su
salida correcta termina así:

```text
big     2
data    2
docker  1
hadoop  2
```

Detén el entorno cuando termines:

```bash
./scripts/stop.sh
```

Los archivos de HDFS permanecen guardados en volúmenes de Docker.

## Hadoop 2: inicio rápido

Si Hadoop 3 está activo, deténlo primero. Después ejecuta:

```bash
cd hadoop2
./scripts/start.sh
./scripts/smoke-test.sh
```

Para detenerlo conservando HDFS:

```bash
./scripts/stop.sh
```

## Interfaces web

### Hadoop 3

| Servicio | URL |
| --- | --- |
| HDFS NameNode | <http://localhost:9870> |
| YARN ResourceManager | <http://localhost:8088> |
| YARN NodeManager | <http://localhost:8042> |
| MapReduce JobHistory | <http://localhost:19888> |

### Hadoop 2

| Servicio | URL |
| --- | --- |
| HDFS NameNode | <http://localhost:50070> |
| YARN ResourceManager | <http://localhost:8088> |
| YARN NodeManager | <http://localhost:8042> |
| MapReduce JobHistory | <http://localhost:19888> |

Son interfaces administrativas web, no aplicaciones gráficas de escritorio.

## Entrar al entorno Hadoop

Ejecuta estos comandos desde `hadoop2/` o `hadoop3/`, según la versión activa:

```bash
docker compose exec --user hadoop namenode bash
```

Dentro del contenedor puedes usar los comandos normales del curso:

```bash
hadoop version
hdfs dfs -ls /
hdfs dfs -mkdir -p /user/hadoop/datos
hdfs dfs -put /etc/hosts /user/hadoop/datos/hosts.txt
hdfs dfs -cat /user/hadoop/datos/hosts.txt
hdfs dfs -rm /user/hadoop/datos/hosts.txt
exit
```

También puedes ejecutar comandos sin abrir una shell:

```bash
docker compose exec --user hadoop namenode hadoop version
docker compose exec --user hadoop namenode hdfs dfs -ls /
docker compose exec resourcemanager yarn node -list
```

## Ejecutar un ejemplo MapReduce manualmente

Primero entra al contenedor NameNode:

```bash
docker compose exec --user hadoop namenode bash
```

Después ejecuta:

```bash
printf 'uno dos dos tres tres tres\n' > /tmp/numeros.txt
hdfs dfs -mkdir -p /laboratorio/entrada
hdfs dfs -put -f /tmp/numeros.txt /laboratorio/entrada/
hdfs dfs -rm -r -f /laboratorio/salida

EXAMPLES_JAR="$(find /opt/hadoop/share/hadoop/mapreduce \
  -name 'hadoop-mapreduce-examples-*.jar' | head -n 1)"

hadoop jar "$EXAMPLES_JAR" wordcount \
  /laboratorio/entrada /laboratorio/salida

hdfs dfs -cat /laboratorio/salida/part-r-00000
```

El trabajo aparecerá en la interfaz de YARN y posteriormente en JobHistory.

## Ejecutar ejercicios Java con un solo comando

El ejecutor de Hadoop 3 inicia el entorno cuando sea necesario, compila todos
los `.java` de una carpeta, genera el JAR, carga la entrada en HDFS, elimina la
salida anterior y muestra el resultado:

```bash
./hadoop3/scripts/run-job.sh
```

El modo interactivo solicita la carpeta con los archivos `.java`, la clase
principal, el archivo o carpeta de entrada y el nombre de salida. También puedes
proporcionar todos o algunos de esos valores como argumentos:

```bash
./hadoop3/scripts/run-job.sh \
  --source Ventas \
  --main SalesCountry.SalesCountryDriver \
  --input Ventas/SalesJan2009.csv \
  --output ventas
```

`--output ventas` reserva `/labs/ventas/input` y `/labs/ventas/output` en HDFS.
Usa otro nombre para conservar los resultados de otro ejercicio. La entrada
puede ser un archivo o una carpeta que contenga archivos directamente. Al
terminar, el ejecutor combina los archivos `part-*` y también guarda una copia
legible en el host:

```text
resultados/ventas/resultado.txt
```

Si un ejercicio necesita dos trabajos MapReduce, el `Driver` debe ejecutarlos
en orden y usar una ruta intermedia derivada de su argumento de salida. El
ejecutor sigue recibiendo una sola clase principal y un solo comando.

## Ver estado y logs

Desde el directorio de la versión activa:

```bash
docker compose ps
docker compose logs --tail=100 namenode
docker compose logs --tail=100 datanode
docker compose logs --tail=100 resourcemanager
docker compose logs --tail=100 nodemanager
docker compose logs --tail=100 historyserver
```

## Reiniciar desde cero

Este comando elimina contenedores **y todos los datos almacenados en HDFS**:

```bash
./scripts/reset.sh
```

El script exige escribir `BORRAR` para evitar una eliminación accidental. Luego
puedes ejecutar nuevamente `./scripts/start.sh`.

## Solución de problemas

### Un puerto ya está ocupado

Comprueba que la otra versión esté detenida:

```bash
cd ../hadoop2 && ./scripts/stop.sh
# o
cd ../hadoop3 && ./scripts/stop.sh
```

### Un servicio no queda saludable

Consulta el estado y los logs:

```bash
docker compose ps
docker compose logs --tail=200
```

Si los datos de una práctica ya no importan, ejecuta `./scripts/reset.sh` y vuelve
a iniciar el entorno.

### Docker no tiene suficiente memoria

Hadoop ejecuta varios procesos Java. Cierra otros contenedores y procura disponer
de al menos 4 GB de memoria libre para una experiencia estable.

## Estructura del repositorio

```text
.
├── docs/hadoop/       explicaciones conceptuales por tema
├── hadoop2/           entorno Docker de Hadoop 2.10.2
├── hadoop3/           entorno Docker de Hadoop 3.5.0
├── labs/              trabajos de laboratorio del curso
└── tests/             validaciones estáticas de ambos entornos
```

### Laboratorios

| Carpeta | Contenido |
| --- | --- |
| [`labs/labca1/`](labs/labca1/) | Laboratorio 1: informe en LaTeX y su plantilla |
| [`labs/labca1/referencias/`](labs/labca1/referencias/README.md) | Investigación bibliográfica sobre el dataset de MINCETUR: metodología, fuentes, BibTeX y bullets de presentación |
| [`labs/Ventas/`](labs/Ventas/) | Ejemplo de MapReduce en Java sobre un CSV de ventas |

Los manuales originales proporcionados por el docente no se publican aquí porque
no se cuenta con autorización para redistribuirlos. El entorno toma como punto de
partida los conceptos del curso, pero adapta rutas, red y operación a Docker sobre
Linux.

## Referencias

- [Apache Hadoop](https://hadoop.apache.org/)
- [Módulos de Hadoop](https://hadoop.apache.org/modules.html)
- [Documentación de Hadoop 2.10.2](https://hadoop.apache.org/docs/current2/)
- [Documentación de Hadoop 3.5.0](https://hadoop.apache.org/docs/current/)
