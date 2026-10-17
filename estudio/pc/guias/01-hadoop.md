# 01. Hadoop: arquitectura, evolución y ecosistema

**Fuente exclusiva:** [01 Hadoop A_v12.pdf](/home/sergi/Documents/semesters/nine/bigdata/lessons/01%20Hadoop%20A_v12.pdf), 36 páginas. Las referencias «p.» corresponden a la posición real en el PDF. Se revisaron también los diagramas sin texto extraíble. Las explicaciones y ejemplos propios desarrollan el material, no agregan un temario externo.

**Prioridad de examen:** poder GRAFICAR y DESCRIBIR HDFS, diferenciando metadatos, bloques, lectura/escritura y replicación; explicar qué cambia entre la arquitectura clásica y la basada en YARN.

## 1. Mapa completo del documento

| Tema | Páginas PDF | Qué debes poder explicar |
|---|---:|---|
| Presentación, Doug Cutting e historia | 1-3 | Antecedentes Nutch, GFS, MapReduce y evolución de proyectos |
| Data Warehouse frente a Big Data | 4-6 | Fuentes, volumen, tiempo de respuesta, costo y capacidades analíticas |
| Evolución Hadoop 1, 2 y comparación con 3/Spark 2 | 7-12 | Separación de recursos/procesamiento y comparación histórica |
| Ecosistema Apache | 13-20 | Elegir el componente según el problema, no solo enumerar nombres |
| Cuatro módulos de Hadoop | 21-23 | Common, HDFS, YARN y MapReduce |
| Infraestructura y localidad | 24-27 | Java, SSH, TCP/IP, racks, switches y proximidad de datos |
| Roles clásicos, HDFS y motor MapReduce | 28-34 | NameNode/DataNode, secundario, JobTracker/TaskTracker |
| Procesos y referencias | 35-36 | Distinguir servicios HDFS de YARN; enlaces finales no desarrollados aquí |

## 2. Origen y sentido de Hadoop

### 2.1. La evolución mostrada en clase (p. 2-3)

Doug Cutting aparece asociado al origen de Hadoop. La línea de tiempo de la p. 3 presenta esta secuencia: trabajo en Nutch en 2002; publicación de GFS en 2003 y MapReduce en 2004; incorporación de su soporte a Nutch en 2005; creación del subproyecto Hadoop desde Nutch en 2006; Hadoop como proyecto principal de Apache en 2008. También sitúa la fundación de Cloudera en 2008, la incorporación de Cutting y una distribución comercial en 2009, la graduación de Hive/Pig/HBase en 2010, Hadoop 2.2 en 2012 y herramientas posteriores de procesamiento como MillWheel, Impala, Spark Streaming, Samza y Storm.

**Idea que sí importa:** Hadoop no surge como una base de datos aislada, sino como un entorno distribuido acompañado de herramientas que amplían sus posibilidades. La línea de tiempo es la del material; no se usa para afirmar fechas externas más precisas.

### 2.2. Data Warehouse y Big Data (p. 4-6)

La p. 4 representa aplicaciones/bases de datos, un proceso ETL, un Data Warehouse y salidas de Business Intelligence y analítica; al costado muestra Hadoop como **HDFS + MapReduce**, alimentado por otras fuentes. Es una comparación de entornos de trabajo, no la prueba de que uno elimine al otro.

| Data Warehouse en la comparación de clase | Big Data en la comparación de clase |
|---|---|
| Fuentes actuales estructuradas | Nuevas fuentes estructuradas y no estructuradas |
| La capacidad analítica está condicionada por los datos de origen | Nuevas capacidades analíticas por nuevas fuentes |
| Velocidad y volumen con costos altos | Nuevas opciones de volumen/velocidad con costos reducidos |

La p. 5 relaciona **volumen** con **tiempo de respuesta**: Hadoop aparece en la zona de volúmenes muy grandes y respuestas más largas; Data Warehouses ocupan otra zona; bases en memoria y procesamiento de eventos aparecen hacia respuestas de segundos o fracciones de segundo. La interpretación es que no basta preguntar «¿qué herramienta es más rápida?»: también importa cuánto dato procesa y qué respuesta necesita la aplicación.

**Ejemplo propio:** analizar un gran archivo histórico y reaccionar a un evento inmediato son necesidades distintas. El diagrama invita a elegir por volumen y latencia, no por popularidad del nombre.

## 3. Evolución: NO mezclar arquitecturas de diferentes versiones

### 3.1. Hadoop 1 y Hadoop 2 (p. 8)

En Hadoop 1 el diagrama coloca aplicaciones como Hive/Pig sobre **MapReduce**, y este sobre **HDFS**. En Hadoop 2 introduce una capa **YARN** entre HDFS y los motores/aplicaciones, y muestra no solo MapReduce, sino también otros motores y servicios.

```text
Hadoop 1                       Hadoop 2 (esquema del PDF)
Hive / Pig / otros             MapReduce / otros motores y servicios
        |                                      |
    MapReduce                                 YARN
        |                                      |
       HDFS                                   HDFS
```

La separación conceptual es importante: **guardar datos**, **administrar recursos** y **procesar datos** son responsabilidades diferentes. YARN permite que la gestión de recursos no quede presentada como una responsabilidad inseparable del motor MapReduce. El PDF no desarrolla internamente todos los motores del dibujo.

### 3.2. Tabla histórica Hadoop 3, Hadoop 2 y Spark 2 (p. 9-12)

La tabla de clase presenta lo siguiente; debes distinguir su contenido de una recomendación actual o un resultado universal:

| Aspecto | Hadoop 3 | Hadoop 2 | Spark 2 |
|---|---|---|---|
| Año inicial indicado | 2017 | 2013 | 2016 |
| Categoría | Motor de procesamiento | Motor de procesamiento | Procesamiento y analítica |
| Analítica en tiempo real/interactividad | No marcadas | No marcadas | Marcadas |
| Lenguaje de desarrollo indicado | Java ≥ 8 | Java ≥ 7 | Scala |
| Scala, Java, Python y R | Marcados | Marcados | Marcados |
| Windows, Linux y macOS | Marcados | Marcados | Marcados |
| Recurso relacionado con velocidad | Disco | Disco | Memoria |
| Tolerancia a fallos indicada | Erasure coding | Replicación | Directed Acyclic Graph |
| YARN en la tabla | Versión 2 | Versión 1 | «All» |
| Nodos de coordinación descritos | Múltiples NameNodes en espera | Un NameNode activo y uno en espera | Driver en el maestro que gestiona SparkContext |
| Seguridad indicada | Kerberos y ACL | Kerberos y ACL | Autenticación con contraseña secreta |

**Erasure coding** aparece como técnica de tolerancia a fallos; estas páginas NO desarrollan su algoritmo. No inventes cómo calcula fragmentos o cuántos necesita. Tampoco deduzcas de la tabla que Hadoop 3 «ya no replica»: la explicación HDFS del mismo documento sí trabaja con replicación.

La tabla representa más nivel de abstracción y facilidad de uso para Spark, mayor costo relativo de disco para Hadoop y mayor costo relativo de memoria para Spark. Incluye multiplicadores de velocidad **1,3x / 1x / 10x** y una nota que dice que las estimaciones dependen del caso y cantidad de datos (p. 10 y 12). Son **referencias del cuadro**, NO garantías para cualquier programa.

En sistemas de archivos (p. 11-12), los tres tienen marcados sistema local, HDFS y Amazon S3. Hadoop 2 y 3 tienen además Azure Storage y «distributed file systems»; Hadoop 3 aparece también con Microsoft Azure Data Lake y Aliyun Object Storage. La tabla señala licencia abierta Apache 2. No agrega instrucciones para integrar esos servicios.

## 4. Los cuatro módulos: separar responsabilidades (p. 21-23)

| Módulo | Responsabilidad | Qué NO debes confundir |
|---|---|---|
| **Hadoop Common** | Utilidades comunes, abstracciones del sistema de archivos/SO, archivos `.jar` y scripts de arranque | No es el depósito de los bloques |
| **HDFS** | Sistema de archivos distribuido con acceso de alto rendimiento | No es la función que calcula resultados analíticos |
| **YARN** | Planificación de tareas y gestión de recursos del clúster | No equivale al sistema de archivos |
| **MapReduce** | Procesamiento paralelo de grandes conjuntos de datos, basado en YARN en esta descripción | No es sinónimo de todo Hadoop |

**Frase para responder:** «Hadoop reúne utilidades comunes, almacenamiento distribuido HDFS, administración de recursos YARN y procesamiento paralelo MapReduce; cada módulo resuelve una parte del problema».

## 5. Infraestructura, racks y localidad (p. 24-27)

El material describe ejecución sobre la máquina virtual de Java; scripts de inicio/parada mediante **SSH**; comunicación entre nodos por **TCP/IP**. La referencia a JRE 1.6 o superior pertenece a esa diapositiva histórica: no la uses como requisito universal para toda versión.

Un **rack** agrupa máquinas conectadas por infraestructura de red. Conocer el rack y el switch donde están los datos permite asignar procesamiento cerca de ellos. En lugar de transportar grandes volúmenes a una única máquina, se busca ejecutar trabajo en el nodo que ya contiene el dato o en uno próximo.

La p. 27 dibuja varios racks y switches. En ellos aparecen servicios NameNode, JobTracker, Secondary NameNode, cliente y múltiples pares **DN + TT** (DataNode + TaskTracker). No concluyas que todos los servicios maestros siempre viven en una sola máquina: el propio dibujo los distribuye entre racks.

**Localidad y replicación no son lo mismo:** localidad ahorra tráfico al acercar tareas y datos; replicación mantiene copias para soportar fallos. Colocar copias en racks distintos ayuda a no depender solo de una agrupación física (p. 26).

## 6. HDFS: el dibujo que debes poder reproducir (p. 29-31 y 34)

### 6.1. Componentes y responsabilidades

- **NameNode:** aloja el índice del sistema de archivos. En el dibujo administra **metadatos**: nombres, información de réplicas y relación con los bloques. Coordina operaciones de bloques; su importancia explica que se mencionen opciones de redundancia (p. 29-31).
- **DataNodes:** sirven los **bloques de datos**. Existen múltiples nodos y copias de los bloques en hosts diferentes (p. 31).
- **Cliente:** realiza consultas de metadatos al NameNode y operaciones de lectura/escritura con DataNodes; el dibujo separa esas conexiones (p. 30 y 34).
- **Racks:** agrupan DataNodes. La replicación representada cruza racks.
- **NameNode secundario:** el texto le asigna generación de instantáneas de estructuras en memoria del NameNode principal (p. 29). NO afirma que sea el mismo papel que un NameNode «standby» de la tabla de Hadoop 3.

### 6.2. Esquema reconstruido del gráfico de clase

```text
                    consultas de metadatos
          Cliente ----------------------------> NameNode
             |                            [nombre, réplicas, ...]
             |                                      |
             | lectura / escritura                  | operaciones
             v                                      | sobre bloques
     +---------------- Rack 1 --------+      +------ Rack 2 ---------+
     | DataNode A      DataNode B     |      | DataNode C  DataNode D|
     | [bloques]        [bloques] ----+------+> [copias]    [bloques] |
     +-------------------------------+ réplica +-------------------+
```

**Lectura:** el cliente necesita información sobre el archivo y los bloques; la vía de metadatos va al NameNode, mientras que el contenido se lee desde DataNodes. **Escritura:** el cliente escribe datos hacia DataNodes, y se mantienen copias de bloques. El dibujo NO muestra al NameNode como intermediario de todos los bytes del archivo.

**Ejemplo propio:** un archivo tiene bloques A y B. El NameNode conoce su organización y las ubicaciones relevantes; un DataNode sirve A y otro sirve B. Mantener otras copias permite no depender de una sola máquina. No agregamos un tamaño de bloque ni un factor obligatorio: estas páginas no los fijan como regla.

### 6.3. Tolerancia a fallos y balanceo (p. 26, 31-32)

La fiabilidad se obtiene replicando datos en múltiples hosts. Los DataNodes pueden comunicarse para balancear datos, mover copias y mantener replicación. Una copia adicional aporta disponibilidad del dato; no realiza por sí misma el cálculo MapReduce ni sustituye la gestión de metadatos.

## 7. Motor MapReduce clásico: JobTracker y TaskTracker (p. 28 y 32-33)

En la descripción clásica:

- **JobTracker:** asigna tareas map/reduce a TaskTrackers usando conocimiento de ubicación de datos. Busca procesamiento cercano a los datos.
- **TaskTracker:** ejecuta tareas en una JVM e informa periódicamente su estado al JobTracker.
- **Slots o ranuras:** espacios disponibles del TaskTracker que pueden ser ocupados por tareas map o reduce.
- Ante fallo de un TaskTracker, el texto señala reasignación de la tarea. No explica un algoritmo de recuperación instrucción por instrucción; no lo inventes.

La p. 28 presenta un maestro con JobTracker, TaskTracker, NameNode y DataNode, y trabajadores que normalmente combinan DataNode/TaskTracker. Esto es la **descripción de arquitectura clásica de esa sección**, no la lista de servicios Hadoop 3.

La p. 35 enumera procesos de **NameNode, DataNode, ResourceManager y NodeManager**, vinculados a HDFS y YARN. No combines JobTracker y ResourceManager como si fueran nombres intercambiables dentro de un único esquema invariable.

## 8. Ecosistema: elegir por la necesidad (p. 13-20)

| Herramienta | Función según el PDF | Ejemplo propio de necesidad |
|---|---|---|
| **Pig** | Ambiente de trabajos MapReduce con lenguaje de alto nivel Pig Latin; compila en uno o más jobs; trabaja con distintos tipos de datos | Expresar transformaciones sin escribir directamente cada tarea MapReduce |
| **Hive** | Consultar/administrar datos en sistemas distribuidos como HDFS mediante HiveQL, similar a SQL | Consultas sobre grandes conjuntos con una sintaxis cercana a SQL |
| **Mahout** | Biblioteca escalable de aprendizaje automático y minería; filtrado colaborativo, clasificación, agrupación y reducción dimensional; modos local/distribuido | Agrupar observaciones o clasificar registros |
| **HBase** | Base distribuida basada en BigTable; acceso aleatorio y lectura/escritura en tiempo real; filas/columnas a gran escala; APIs Java | Acceder a registros concretos sin plantear todo como un trabajo por lotes |
| **Sqoop** | Transferencia entre Hadoop y bases relacionales mediante paralelismo/tolerancia a fallos MapReduce | Importar datos de una base relacional a Hadoop |
| **Oozie** | Programar y coordinar workflows con trabajos MapReduce y no MapReduce | Ordenar trabajos dependientes dentro de un flujo |
| **ZooKeeper** | Coordinación distribuida, configuración y apoyo a proyectos como HBase para disponibilidad/coordinación | Mantener información compartida para coordinar servicios |
| **Ambari** | Aprovisionamiento, instalación, gestión, configuración, monitoreo, alertas/correo y API REST de clústeres | Instalar servicios y vigilar la salud del clúster |
| **Spark** | Sistema de procesamiento presentado como más veloz que MapReduce, con procesamiento en tiempo real y ejecución independiente | Procesamiento que no se limita a la ejecución MapReduce clásica |
| **Avro** | Serialización para procesar/almacenar datos y leerlos desde distintos lenguajes | Representar datos intercambiables entre programas |
| **Cassandra** | Base distribuida NoSQL para Big Data | Persistencia distribuida NoSQL |

**Límite de la fuente:** las capacidades y vínculos de estas herramientas se estudian tal como los presenta la clase. No se infiere de ellos el estado de mantenimiento ni la arquitectura actual de cada proyecto. La p. 36 solo lista referencias; no se consultaron esos enlaces.

## 9. Confusiones frecuentes

1. **«Hadoop es una base de datos».** Es un entorno con módulos y ecosistema; HDFS es un sistema de archivos y HBase/Cassandra son herramientas de base de datos diferenciadas.
2. **«NameNode guarda los bloques».** El gráfico separa metadatos del NameNode y bloques servidos por DataNodes.
3. **«Todos los bytes pasan por NameNode».** El dibujo conecta lectura/escritura de contenido con DataNodes.
4. **«SecondaryNameNode = standby de Hadoop 3».** El documento les atribuye descripciones distintas; no ofrece esa equivalencia.
5. **«Replicar significa procesar».** Réplica conserva copias; MapReduce procesa.
6. **«Hadoop 1, 2 y 3 tienen exactamente los mismos roles».** La evolución incorpora YARN y compara otros esquemas de nodos.
7. **«Spark es siempre diez veces más rápido».** El propio cuadro limita las estimaciones al caso y volumen.
8. **«SSH transporta todo el contenido de HDFS».** SSH se menciona para scripts de administración; el documento menciona TCP/IP para comunicación.

## 10. Preguntas estilo examen: responde sin mirar

1. **Graficar y describir los componentes de la arquitectura HDFS vistos en clase.** Incluye clientes, NameNode, DataNodes, bloques, racks y réplicas.
2. Describir con un ejemplo la diferencia entre operaciones de metadatos y operaciones de datos en HDFS.
3. Explicar por qué localidad y replicación mejoran aspectos diferentes del clúster.
4. Comparar Hadoop 1 y Hadoop 2 e identificar la responsabilidad de YARN.
5. Describir JobTracker, TaskTracker y slots. Explicar por qué no deben confundirse con la lista de servicios basada en YARN.
6. Explicar los cuatro módulos de Hadoop y asignar a cada uno un problema concreto.
7. Elegir y justificar herramientas del ecosistema para: consulta tipo SQL, importar desde una base relacional, coordinar trabajos y supervisar el clúster.
8. Comparar Hadoop 3, Hadoop 2 y Spark 2 según el cuadro de clase, indicando al menos una limitación de interpretar sus cifras literalmente.
9. Comparar Data Warehouse y Big Data según fuentes, capacidades analíticas y relación volumen/tiempo de respuesta.
10. Explicar el papel del NameNode secundario descrito en clase y por qué no basta para afirmar que es equivalente a un standby.

---

## 11. Respuestas modelo

**1.** El cliente consulta metadatos al NameNode, que mantiene el índice del sistema de archivos, nombres e información de réplicas. Los DataNodes contienen/sirven bloques; el cliente lee y escribe el contenido con ellos. Los DataNodes se distribuyen en racks, y las copias pueden colocarse en racks diferentes para tolerar fallos. El gráfico debe separar las flechas de metadatos, lectura/escritura y replicación. Reproduce el esquema de la sección 6. (p. 29-31 y 34)

**2.** Preguntar por el nombre y organización de un archivo es una operación de metadatos coordinada con NameNode; obtener los bytes de un bloque es una lectura con DataNode. Ejemplo propio: localizar el bloque A y luego leer A no son la misma operación. (p. 30)

**3.** La localidad intenta ejecutar procesamiento cerca de donde ya están los datos, ahorrando ancho de banda. La replicación conserva copias en distintos nodos/racks para no depender de una única máquina o agrupación física. Un cálculo puede aprovechar una réplica cercana, pero ambas técnicas cumplen funciones distintas. (p. 25-26 y 31-32)

**4.** El esquema Hadoop 1 muestra aplicaciones sobre MapReduce y HDFS. Hadoop 2 intercala YARN como capa de administración de recursos que sostiene distintos motores y servicios. YARN planifica tareas y gestiona los recursos, mientras HDFS almacena y MapReduce procesa. (p. 8 y 23)

**5.** JobTracker asigna tareas considerando localidad; TaskTracker las ejecuta en JVM y reporta estado; sus slots alojan tareas map/reduce. Esa explicación es la arquitectura clásica, mientras la lista de p. 35 nombra ResourceManager y NodeManager de YARN. Deben explicarse como esquemas distintos del documento. (p. 28, 32-35)

**6.** Common aporta utilidades/scripts; HDFS da almacenamiento distribuido; YARN gestiona recursos y tareas; MapReduce calcula en paralelo. Para un gran archivo, Common apoya el funcionamiento, HDFS conserva el contenido, YARN administra dónde ejecutar y MapReduce realiza la transformación requerida. (p. 21-23)

**7.** Hive para consultas HiveQL similares a SQL; Sqoop para transferir entre Hadoop y bases relacionales; Oozie para coordinar un workflow; Ambari para aprovisionamiento, configuración, supervisión y alertas. Se eligen por su responsabilidad, no porque todos sean parte del ecosistema. (p. 14, 17 y 19)

**8.** La tabla asigna Hadoop 3 a Java ≥ 8 y erasure coding, Hadoop 2 a Java ≥ 7 y replicación; Spark 2 a desarrollo Scala, memoria e interactividad/analítica en tiempo real. La tabla también compara sistemas de archivos, seguridad y nodos. Las velocidades 1,3x/1x/10x son estimaciones condicionadas por caso y cantidad de datos, no una ley universal. (p. 9-12)

**9.** La comparación sitúa al Data Warehouse en fuentes estructuradas y al Big Data en nuevas fuentes estructuradas/no estructuradas. Nuevas fuentes amplían capacidades analíticas; volumen y velocidad tienen distintas opciones de costo. El gráfico pone Hadoop en una zona de alto volumen y mayores tiempos de respuesta, y herramientas de eventos hacia menor latencia. No concluye que una herramienta elimine a todas las demás. (p. 4-6)

**10.** La p. 29 describe un secundario que genera instantáneas de estructuras de memoria del NameNode principal. La tabla Hadoop 3 habla de NameNodes en espera. Como la fuente no los identifica como equivalentes ni desarrolla el cambio automático de roles, no corresponde afirmar esa equivalencia. (p. 11 y 29)
