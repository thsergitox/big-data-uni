# Tutorial para ejecutar las consultas durante la exposición

Este procedimiento se ejecuta desde la raíz del repositorio:

```bash
cd /home/sergi/Documents/semesters/nine/bigdata
```

## 1. Revisión previa a la clase

Docker debe estar iniciado. Se puede comprobar sin modificar datos:

```bash
docker version
docker compose version
docker info
```

También se debe confirmar que están el dataset consolidado y los fuentes Java:

```bash
test -f labs/labca1/data/Indicadores_ocupabilidad_2019_2025.csv
find labs/labca1/queries -path '*/src/*.java' | head
```

No se deben editar ni reemplazar los siete CSV originales de `labs/labca1/data/`.

## 2. Levantar Hadoop 3

```bash
./hadoop3/scripts/start.sh
```

El script usa Docker Compose y espera a que estén disponibles NameNode, DataNode,
ResourceManager, NodeManager y HistoryServer.

Interfaces útiles:

- NameNode: <http://localhost:9870>
- ResourceManager: <http://localhost:8088>
- NodeManager: <http://localhost:8042>
- JobHistory: <http://localhost:19888>

## 3. Validar el clúster antes de exponer

```bash
./hadoop3/scripts/smoke-test.sh
```

Si esta prueba falla, no conviene comenzar la demostración. Primero se debe revisar:

```bash
docker compose -f hadoop3/compose.yaml ps
docker compose -f hadoop3/compose.yaml logs --tail=80 namenode
docker compose -f hadoop3/compose.yaml logs --tail=80 resourcemanager
```

## 4. Revisar los comandos sin ejecutar jobs

```bash
./labs/labca1/queries/run-all.sh --from 1 --to 14 --dry-run
```

Esto imprime los comandos de las catorce consultas. No compila, no sube datos y
no crea salidas.

## 5. Ejecutar una consulta elegida por el docente

La forma general es:

```bash
./labs/labca1/queries/run-all.sh --from N --to N
```

Se reemplaza `N` por un número de 1 a 14. Por ejemplo, para mostrar Q01:

```bash
./labs/labca1/queries/run-all.sh --from 1 --to 1
```

Q01 es una demostración corta y fácil de explicar: compila los fuentes comunes y
la consulta, arma el JAR, sube el CSV a HDFS, ejecuta MapReduce y muestra arribos
nacionales y extranjeros por año.

Otras opciones útiles:

```bash
# Búsqueda de subtexto; el orquestador busca "hotel"
./labs/labca1/queries/run-all.sh --from 7 --to 7

# Dos trabajos MapReduce enlazados
./labs/labca1/queries/run-all.sh --from 9 --to 9

# Comparación con regresión logística
./labs/labca1/queries/run-all.sh --from 12 --to 12
```

El runner elimina únicamente la ruta HDFS de la consulta que va a repetir. No
borra las salidas de las demás consultas.

## 6. Explicar lo que ocurre mientras corre

El flujo real es el siguiente:

1. `run-all.sh` elige la clase principal de la consulta.
2. `run-job.sh` copia los fuentes Java al contenedor NameNode.
3. Dentro del contenedor se ejecutan `javac` y `jar`.
4. El dataset se carga en `/labs/queryXX/input` de HDFS.
5. YARN ejecuta el JAR y escribe `/labs/queryXX/output`.
6. Se comprueba el archivo `_SUCCESS`.
7. El resultado y el JAR regresan al host.

Mientras el job corre se puede abrir ResourceManager y entrar a la aplicación
más reciente. Al terminar, se puede enseñar JobHistory.

## 7. Mostrar la salida

El comando ya imprime la salida en la terminal. También queda guardada en:

```text
labs/labca1/queries/queryXX/resultados/queryXX/resultado.txt
labs/labca1/queries/queryXX/resultados/queryXX/queryXX.jar
```

Para Q01:

```bash
cat labs/labca1/queries/query01/resultados/query01/resultado.txt
ls -lh labs/labca1/queries/query01/resultados/query01/query01.jar
```

Para comprobar HDFS:

```bash
docker compose -f hadoop3/compose.yaml exec -T --user hadoop namenode \
  hdfs dfs -ls /labs/query01/output

docker compose -f hadoop3/compose.yaml exec -T --user hadoop namenode \
  hdfs dfs -cat '/labs/query01/output/part-*'
```

## 8. Ejecutar todas las consultas

Esto se recomienda para una verificación previa, no como demostración completa
durante los diez minutos:

```bash
./labs/labca1/queries/run-all.sh --from 1 --to 14
```

El resumen queda en:

```text
labs/labca1/queries/execution-summary.tsv
```

## 9. Detener Hadoop sin borrar HDFS

```bash
./hadoop3/scripts/stop.sh
```

El script baja los contenedores y conserva los volúmenes. En el siguiente inicio
las rutas HDFS seguirán disponibles.

## Plan recomendado para la clase

1. Antes de entrar, levantar Hadoop y ejecutar `smoke-test.sh`.
2. Dejar abiertas las pestañas de NameNode, ResourceManager y JobHistory.
3. Tener una terminal ubicada en la raíz del repositorio.
4. Si el docente deja elegir, ejecutar Q01.
5. Si pide una consulta encadenada, ejecutar Q09.
6. Mostrar primero la terminal, luego ResourceManager y finalmente el
   `resultado.txt`.
7. No ejecutar las catorce consultas durante la exposición.
