# Potencia, casos de uso y limitaciones

## De dónde proviene la potencia de Hadoop

Hadoop no es potente porque un proceso individual sea mágico. Lo es porque
coordina muchas unidades de trabajo y almacenamiento bajo un modelo común.

### Escalamiento horizontal

Un conjunto puede crecer agregando nodos con almacenamiento y capacidad de
cómputo. Esto permite superar los límites de una sola máquina.

### Paralelismo

Si un problema se divide en partes independientes, varios mappers pueden trabajar
al mismo tiempo. El tiempo no se reduce siempre de manera lineal: coordinación,
red, discos y datos desbalanceados añaden costes.

### Localidad de datos

El scheduler intenta colocar el cálculo donde ya está el bloque necesario. Para
grandes volúmenes suele ser más barato mover una tarea pequeña que mover todos
los datos.

### Tolerancia a fallos

En un clúster real:

- HDFS replica bloques;
- los DataNodes reportan su estado;
- YARN puede volver a ejecutar tareas fallidas;
- el trabajo se divide en intentos observables.

La tolerancia no significa que nada falle; significa que el sistema **espera el
fallo y dispone de mecanismos de recuperación**.

### Procesamiento cerca del almacenamiento

HDFS y los motores de cómputo comparten información sobre la ubicación de los
datos. Esa integración hizo a Hadoop especialmente efectivo para procesamiento
batch de gran escala.

## Casos de uso apropiados

### ETL masivo

Extraer datos de múltiples fuentes, limpiarlos, agruparlos y producir conjuntos
preparados para análisis.

### Agregación de logs

Procesar grandes cantidades de registros de servidores para contar eventos,
detectar patrones o generar métricas históricas.

### Construcción de índices

Analizar documentos en paralelo y producir estructuras que después utilizará un
motor de búsqueda.

### Procesamiento histórico

Recalcular meses o años de transacciones, telemetría o actividad cuando no se
requiere una respuesta inmediata.

### Preparación de datos para machine learning

Normalizar, filtrar y generar características antes de entrenar modelos. Hadoop
puede formar parte del pipeline, aunque el entrenamiento use otro motor.

### Archivo activo o data lake tradicional

Conservar archivos grandes de diversos formatos para procesamiento posterior.
La gobernanza, catálogo, seguridad y formatos de tabla requieren herramientas
adicionales; HDFS por sí solo no resuelve todo un data lake.

## Herramientas del ecosistema

Hadoop central se limita a Common, HDFS, YARN y MapReduce. Alrededor de ellos
existen herramientas con responsabilidades diferentes:

| Herramienta | Papel conceptual | Relación posible con Hadoop |
| --- | --- | --- |
| Hive | SQL analítico sobre grandes datos | Puede leer HDFS y ejecutar mediante distintos motores. |
| Spark | Procesamiento general, iterativo y en memoria | Puede leer HDFS y usar YARN. |
| HBase | Base de datos distribuida orientada a columnas | Suele almacenar sobre HDFS. |
| Kafka | Transporte y retención de eventos | Puede alimentar pipelines que terminan en HDFS. |
| Trino | Motor SQL distribuido interactivo | Puede consultar datos del ecosistema mediante conectores. |

**Ninguna de estas herramientas está instalada en este repositorio.** Nombrarlas
sirve para entender el ecosistema, no para prometer funcionalidades inexistentes.

## Cuándo NO usar Hadoop/MapReduce

### Datos pequeños

Si los datos caben cómodamente en memoria o disco de una computadora, Python,
DuckDB, PostgreSQL o herramientas Unix pueden ser más simples y rápidas.

### OLTP

Crear pedidos, actualizar saldos o responder consultas individuales requiere baja
latencia y transacciones. HDFS y MapReduce no reemplazan una base de datos
transaccional.

### Tiempo real estricto

MapReduce es batch y tiene coste de arranque y materialización. Para eventos
continuos suelen considerarse motores de streaming.

### Consultas interactivas

Esperar que se inicie un job para cada exploración produce una mala experiencia.
Motores SQL distribuidos o formatos optimizados suelen ser mejores.

### Muchos archivos pequeños

Cada archivo y bloque añade metadatos al NameNode. Millones de archivos diminutos
pueden agotar memoria y causar sobrecarga aun si el total de bytes no es enorme.

### Algoritmos iterativos

Si cada iteración relee y reescribe datos, el uso intensivo de disco de MapReduce
puede ser costoso. Spark y otros motores reutilizan datos en memoria cuando
corresponde.

## Hadoop frente a alternativas modernas

No existe una herramienta “ganadora” para todos los casos:

| Necesidad | Alternativa frecuente | Trade-off |
| --- | --- | --- |
| SQL local sobre archivos | DuckDB | Muy simple, pero no reemplaza un clúster enorme. |
| Almacenamiento cloud | S3/GCS/Azure Blob | Separa almacenamiento y cómputo; depende del proveedor y la red. |
| Batch/iterativo distribuido | Spark | Más flexible y rápido en ciertos flujos; añade complejidad y memoria. |
| SQL distribuido interactivo | Trino | Excelente para consulta; no es un sistema de almacenamiento. |
| Streaming | Flink/Kafka Streams | Baja latencia continua; modelo operacional diferente. |

Aprender Hadoop sigue siendo valioso porque enseña fundamentos que reaparecen en
sistemas modernos: particionamiento, replicación, scheduling, localidad,
shuffle, tolerancia a fallos y separación entre almacenamiento y cómputo.

## Límites concretos de nuestro entorno

| Característica | Laboratorio | Producción típica |
| --- | --- | --- |
| Máquinas físicas | 1 | Varias |
| DataNodes | 1 | Varios |
| Replicación | 1 | Frecuentemente 3, según requisitos |
| NameNode HA | No | Recomendable |
| Seguridad | Mínima | Autenticación, autorización, cifrado y auditoría |
| Objetivo | Aprendizaje funcional | Disponibilidad, capacidad y rendimiento |

Docker nos da reproducibilidad y aislamiento. NO nos da por sí solo distribución
física, alta disponibilidad ni rendimiento de clúster.

## Preguntas para comprobar que entendiste

1. ¿Por qué un NameNode no debería transportar todos los bytes de cada lectura?
2. ¿Qué se pierde con replicación `1`?
3. ¿Qué diferencia existe entre un contenedor YARN y uno Docker?
4. ¿Por qué el shuffle puede dominar el coste de un job?
5. ¿Por qué “los contenedores están activos” no prueba que MapReduce funcione?
6. ¿Qué herramienta elegirías para una consulta local sobre 2 GB y por qué?

Si puedes responderlas razonando, no memorizando, ya tienes una base mucho más
sólida para los laboratorios.

## Fuentes oficiales

- [Arquitectura de HDFS](https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-hdfs/HdfsDesign.html)
- [Arquitectura de YARN](https://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/YARN.html)
- [Tutorial de MapReduce](https://hadoop.apache.org/docs/current/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html)

[Volver al índice](README.md).

