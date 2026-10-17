# 04. Hadoop 3.3.6 en clúster: comprender la instalación

**Fuente exclusiva:** [Instalacion_y_Configuracion_de_Hadoop_3_en_cluster_de_nodos.pdf](/home/sergi/Documents/semesters/nine/bigdata/lessons/Instalacion_y_Configuracion_de_Hadoop_3_en_cluster_de_nodos.pdf), **6 páginas reales**.

**Objetivo teórico:** explicar qué configura cada etapa y cómo se relacionan red, usuarios, SSH, almacenamiento HDFS, recursos YARN y ejecución MapReduce. Esta es una guía de estudio, **NO un procedimiento ejecutado o validado**. Se señalan inconsistencias internas del PDF para no memorizarlas como correctas.

## 1. Mapa de temas y dependencias

| Tema | Páginas PDF |
|---|---:|
| Roles del clúster, requisitos, red y usuarios | 1-2 |
| Instalación y variables de entorno | 2 |
| Archivos de configuración HDFS, MapReduce y YARN | 2-3 |
| Lista workers, directorios y permisos | 3-4 |
| SSH, formateo inicial y sincronización | 4 |
| Arranque, procesos e interfaces web | 5 |
| Prueba HDFS y job WordCount | 5-6 |
| Parada y criterios finales de funcionamiento | 6 |

```text
Red / nombres / usuarios / Java
              |
Instalación + entorno + configuración
              |
Directorios/permisos + SSH + workers + sincronización
              |
      Inicialización y arranque
              |
 Ver procesos -> consultar interfaces -> probar HDFS -> ejecutar WordCount
```

El esquema expresa **dependencias conceptuales**. Para reconocer el orden literal del documento: formateo figura como paso 11 y sincronización como paso 12; no es una invitación a ejecutar ni reorganizar el guion.

## 2. Estructura: separar dos familias de servicios (p. 1 y 5)

| Rol | Hostname / IP del ejemplo | Usuario | Servicios |
|---|---|---|---|
| Maestro | `master` / `192.168.18.101` | `master` | NameNode, SecondaryNameNode, ResourceManager |
| Trabajador 1 | `slave1` / `192.168.18.100` | `slave1` | DataNode, NodeManager |
| Trabajador 2 | `slave2` / `192.168.18.102` | `slave2` | DataNode, NodeManager |

```text
                   MASTER
          +-------------------------+
 HDFS     | NameNode                |
          | SecondaryNameNode       |
 YARN     | ResourceManager          |
          +-------------------------+
               /                \
     +----------------+     +----------------+
     | SLAVE1         |     | SLAVE2         |
HDFS | DataNode       |     | DataNode       |
YARN | NodeManager    |     | NodeManager    |
     +----------------+     +----------------+
```

El material distingue el almacenamiento distribuido **HDFS** de la gestión/ejecución de trabajos **YARN**. Los trabajadores ejecutan servicios de ambas familias. No todos los procesos del clúster son procesos MapReduce ni todos residen en el maestro.

**Nombre de host, IP y usuario son conceptos distintos:** el hostname identifica la máquina en configuración; la IP permite alcanzarla en la red; el usuario define la cuenta bajo la cual se accede y los permisos locales. El PDF elige nombres iguales para host/usuario por claridad, no porque sean una sola entidad.

El procedimiento admite uno o más trabajadores y dice repetir configuración para nodos nuevos. Las IP son las del ejemplo; no se presentan como direcciones universales para cualquier clúster.

## 3. Requisitos y red (p. 1)

El PDF instala **OpenJDK 8, SSH y rsync** en todos los nodos, y verifica Java. Su texto dice versión 1.8 o superior; esta es la indicación del material para Hadoop 3.3.6, no una matriz externa verificada de compatibilidad con cualquier Java.

Se edita `/etc/hosts` en cada máquina con las asociaciones de `master`, `slave1` y `slave2`. El propósito es que las referencias por nombre apunten a las IP definidas. Luego se prueban con `ping`.

**Diferencia de verificaciones:** que un nombre responda a ping verifica una parte de la conectividad; no demuestra que Hadoop esté iniciado, que SSH autentique o que un job funcione. Cada capa necesita su comprobación específica.

La creación de usuarios da una cuenta propia por nodo. Esto repercute en las rutas de instalación, propietario de directorios y destino de las conexiones SSH.

## 4. Instalación y entorno de cada nodo (p. 2)

Cada nodo descarga/descomprime Hadoop 3.3.6 y renombra su carpeta como `hadoop` dentro del home del usuario. La estructura prevista queda así:

```text
/home/master/hadoop
/home/slave1/hadoop
/home/slave2/hadoop
```

Las variables de `~/.bashrc` relacionan herramientas y componentes con esa instalación:

| Variable / modificación | Sentido en el material |
|---|---|
| `HADOOP_HOME` | Directorio base `/home/$(whoami)/hadoop` |
| `HADOOP_INSTALL` | Referencia a la instalación |
| `HADOOP_MAPRED_HOME` | Referencia del componente MapReduce |
| `HADOOP_COMMON_HOME` | Referencia de Common |
| `HADOOP_HDFS_HOME` | Referencia de HDFS |
| `YARN_HOME` | Referencia de YARN |
| `HADOOP_COMMON_LIB_NATIVE_DIR` | Ubicación de bibliotecas nativas en `lib/native` |
| `PATH` con `bin` y `sbin` | Permite localizar comandos/scripts de la instalación |

El PDF aplica el entorno mediante `source ~/.bashrc` y consulta `hadoop version`. **Esto verifica localización/versión**, no sustituye las pruebas de funcionamiento distribuido.

## 5. Archivos de configuración: qué responsabilidad tiene cada uno (p. 2-3)

Se editan inicialmente en `/home/master/hadoop/etc/hadoop/`.

| Archivo | Propiedad o dato del ejemplo | Interpretación para examen |
|---|---|---|
| `core-site.xml` | `fs.defaultFS = hdfs://master:9000` | Sistema de archivos HDFS predeterminado y endpoint del ejemplo |
| `hdfs-site.xml` | `dfs.replication = 2` | Factor de replicación configurado para el ejemplo |
| `hdfs-site.xml` | `dfs.namenode.name.dir = file:/hadoopdata/namenode` | Directorio local del NameNode |
| `hdfs-site.xml` | `dfs.datanode.data.dir = file:/hadoopdata/datanode` | Directorio local del DataNode |
| `mapred-site.xml` | `mapreduce.framework.name = yarn` | El trabajo MapReduce utiliza YARN |
| `mapred-site.xml` | `mapreduce.application.classpath` | Rutas de configuración y bibliotecas utilizadas por la aplicación |
| `yarn-site.xml` | `yarn.nodemanager.aux-services = mapreduce_shuffle` | Servicio auxiliar configurado para el shuffle de MapReduce |
| `yarn-site.xml` | `yarn.resourcemanager.hostname = master` | Nombre del host ResourceManager |
| `hadoop-env.sh` | `JAVA_HOME = /usr/lib/jvm/java-8-openjdk-amd64/jre` | Ruta Java elegida en el procedimiento |

**Endpoint y consola web son distintos:** `hdfs://master:9000` configura acceso HDFS; la interfaz NameNode de p. 5 usa HTTP y puerto 9870. No se sustituyen uno por otro porque ambos contengan el nombre del maestro.

**Rutas locales y rutas HDFS también son distintas:** `/hadoopdata/datanode` es el directorio local configurado para el servicio; `/user/master/input` es la ruta HDFS que usa el ejemplo de datos.

El factor 2 es una **decisión concreta del clúster del material**, no el número invariable de réplicas de todo Hadoop.

## 6. Workers, directorios y propietarios (p. 3-4)

El archivo `/home/master/hadoop/etc/hadoop/workers` lista `slave1` y `slave2`. Identifica los trabajadores que el maestro tiene configurados para el procedimiento. Añadir un nombre aquí no instala por sí solo Java, Hadoop, directorios o acceso SSH en esa máquina.

El PDF crea `/hadoopdata/namenode` y `/hadoopdata/datanode` en maestro y trabajadores, y asigna propiedad al usuario correspondiente. La relación conceptual es:

```text
Ruta declarada en configuración
            |
Directorio existente en la máquina
            |
Usuario de servicio con propiedad/permisos adecuados
```

No basta que el archivo XML nombre una ruta: esa ruta debe existir y resultar accesible a la cuenta usada. Que el guion cree un directorio `namenode` en un trabajador NO significa que espere allí un servicio NameNode: la lista de procesos lo reserva al maestro.

## 7. SSH sin contraseña y sincronización (p. 4)

### 7.1. SSH

El maestro configura `~/.ssh/config` para que el alias `slave1` use su IP y usuario `slave1`, y lo mismo para `slave2`. Crea/distribuye una clave y prueba conexiones que consultan `hostname` y `whoami`.

**Qué comprueba esa prueba:** que se entra a la máquina esperada y con la cuenta esperada. Es más informativa que verificar solo conectividad de red, especialmente porque los usuarios varían entre nodos.

El material también fija permisos 700 para `.ssh` y 600 para su archivo `config`, y utiliza `StrictHostKeyChecking accept-new`. Esta guía explica el contenido del PDF; no agrega una política externa de seguridad.

### 7.2. Formateo inicial

El paso 11 indica `hdfs namenode -format` en el maestro. Está presentado como **inicialización del NameNode dentro de la instalación**, no como verificación cotidiana ni como solución general a cualquier problema. El PDF no desarrolla recuperación ni mantenimiento de un clúster con datos existentes.

### 7.3. Copia de configuración

El paso 12 copia a cada trabajador cinco archivos: `core-site.xml`, `hdfs-site.xml`, `yarn-site.xml`, `mapred-site.xml` y `hadoop-env.sh`. Busca que los nodos compartan las referencias necesarias para el funcionamiento distribuido.

**Inconsistencia de rutas verificada:** el classpath de p. 3 usa rutas `/home/master/hadoop/...`, pero p. 2 instala trabajadores en `/home/slave1/hadoop` y `/home/slave2/hadoop`; p. 4 copia el archivo sin mostrar adaptación. Las rutas del classpath NO coinciden con los directorios de instalación documentados para esos nodos. Esto requiere revisión al implementar; no se afirma que el guion sea ejecutable sin cambios ni que se haya probado.

## 8. Inicio y comprobación de procesos (p. 5)

El maestro inicia `start-dfs.sh` y `start-yarn.sh`. Luego se usa `jps` localmente y por SSH en trabajadores.

- Maestro esperado: **NameNode, SecondaryNameNode, ResourceManager**.
- Trabajadores esperados: **DataNode, NodeManager**.

Estas listas permiten comprobar distribución de servicios. Que se vea un proceso no demuestra por sí solo que un job haya completado correctamente: la prueba HDFS y WordCount verifican capas posteriores.

**No deducir alta disponibilidad:** la guía lista un SecondaryNameNode, pero no explica elección de un standby ni conmutación automática. No afirma que esos términos sean equivalentes.

## 9. Interfaces web de monitoreo (p. 5)

| Servicio | Host y puerto del ejemplo | Qué indica la tabla |
|---|---|---|
| NameNode | `192.168.18.101:9870` | Estado general HDFS |
| ResourceManager | `192.168.18.101:8088` | Gestión de trabajos YARN |
| DataNode trabajador 1 | `192.168.18.100:9864` | Estado DataNode |
| DataNode trabajador 2 | `192.168.18.102:9864` | Estado DataNode |
| NodeManager | `192.168.18.100:8042` | Tareas YARN locales |

Son interfaces HTTP. La tabla muestra un NodeManager; el diseño del clúster espera NodeManager en ambos trabajadores. No confundas supervisión del clúster completo, estado de almacenamiento local y tareas locales.

## 10. Prueba HDFS y ejecución WordCount (p. 5-6)

### 10.1. Qué demuestra la prueba

Se crea `/user/master/input` en HDFS; se crean dos archivos de texto locales; se cargan con `hdfs dfs -put`; se listan en HDFS. Así se diferencia **crear contenido local** de **incorporarlo al sistema distribuido**.

Después se invoca el ejemplo WordCount del JAR `hadoop-mapreduce-examples-3.3.6.jar`, con entrada `/user/master/input` y salida `/user/master/output`. La lectura final usa `hdfs dfs -cat` sobre `part-r-00000`.

**Concepto de extremo a extremo:** no basta iniciar HDFS y YARN; el job debe leer datos, procesarlos y dejar un resultado consultable. La salida mostrada es un archivo de la ejecución, no un archivo local creado con `echo`.

### 10.2. Inconsistencia del resultado esperado: NO memorizar el conteo erróneo

Las entradas de p. 5 son:

```text
texto1: hadoop mapreduce yarn test
texto2: bigdata cluster system processing
```

Cada palabra aparece **una sola vez**. Por conteo directo de estas entradas, la salida coherente es:

```text
bigdata     1
cluster     1
hadoop      1
mapreduce   1
processing  1
system      1
test        1
yarn        1
```

Sin embargo, p. 6 muestra `cluster 2`, `hadoop 3`, `mapreduce 3`, `yarn 2` y agrega `distributed 1` y `hdfs 1`, palabras ausentes de las dos entradas. **La salida esperada del PDF no corresponde a los archivos que especifica.** La tabla anterior es una reconstrucción aritmética a partir de la fuente, no un resultado de ejecución real.

## 11. Parada y criterios de funcionamiento (p. 6)

El orden de parada mostrado es `stop-yarn.sh` y luego `stop-dfs.sh`. El documento concluye con comunicación por nombres, SSH sin contraseña, HDFS/YARN distribuidos, jobs MapReduce y monitoreo web; para escalar, propone repetir preparación en nuevos trabajadores.

**Conclusión rigurosa para estudiar:** el documento propone esas verificaciones, pero su conclusión no es evidencia de que nosotros hayamos ejecutado el clúster. Además, las inconsistencias de classpath y salida deben revisarse en una implementación real.

## 12. Confusiones frecuentes

1. **Ping exitoso = clúster funcional:** solo verifica parte de conectividad.
2. **Usuario = hostname = IP:** coinciden parcialmente por convención del ejemplo, no por naturaleza.
3. **Puerto 9000 = interfaz NameNode:** el PDF usa 9000 para endpoint HDFS y 9870 para consola HTTP.
4. **`/hadoopdata` = `/user/master/input`:** una es ruta local de servicios y otra ruta HDFS del ejemplo.
5. **Todos ejecutan NameNode:** el material espera NameNode solo en maestro.
6. **Listar workers configura completamente cada nodo:** todavía requiere instalación, entorno, directorios y SSH.
7. **Copiar XML garantiza rutas correctas:** el classpath copiado mantiene `/home/master` y debe revisarse.
8. **Formatear es una prueba periódica:** el PDF lo presenta en la inicialización.
9. **La salida de p. 6 es el conteo de p. 5:** no coincide; hay frecuencias y palabras ajenas.

## 13. Preguntas estilo examen

1. Graficar y describir la distribución de servicios del maestro y dos trabajadores en Hadoop 3.3.6.
2. Explicar la diferencia entre verificar red, verificar SSH, verificar procesos y ejecutar un job.
3. Describir qué configura cada archivo: `core-site.xml`, `hdfs-site.xml`, `mapred-site.xml`, `yarn-site.xml`, `hadoop-env.sh`.
4. Comparar endpoint HDFS, interfaz NameNode, rutas locales y rutas HDFS del ejemplo.
5. Explicar por qué workers, directorios y propietarios son necesarios para el clúster.
6. Describir propósito del SSH sin contraseña, formateo inicial y sincronización; señalar el problema de classpath.
7. Explicar las interfaces de ResourceManager, DataNode y NodeManager según su alcance.
8. Describir la prueba de datos/WordCount y reconstruir la salida correcta desde las entradas del documento.

---

## 14. Respuestas modelo

**1.** Maestro `master` aloja NameNode, SecondaryNameNode y ResourceManager. `slave1` y `slave2` alojan DataNode y NodeManager. El diagrama debe distinguir servicios HDFS de YARN y mostrar que cada trabajador participa en ambas familias. Los nombres/IP/usuarios son los concretos del ejemplo, no requisitos universales. (p. 1 y 5)

**2.** Ping comprueba conectividad/resolución; SSH con hostname/whoami comprueba acceso a nodo/cuenta; jps comprueba los procesos esperados; cargar/listar datos verifica operaciones HDFS; WordCount verifica lectura, procesamiento y salida de un trabajo. Las pruebas no son equivalentes ni una sustituye a todas. (p. 1, 4-5)

**3.** Core define sistema predeterminado `hdfs://master:9000`; HDFS define réplica 2 y rutas locales NameNode/DataNode; MapReduce selecciona YARN y classpath; YARN configura shuffle auxiliar y hostname ResourceManager; hadoop-env fija JAVA_HOME. Cada archivo atiende una responsabilidad diferente de la configuración. (p. 2-3)

**4.** El endpoint HDFS del ejemplo es `hdfs://master:9000`; la interfaz web NameNode está en HTTP/9870. `/hadoopdata/namenode` y `/hadoopdata/datanode` son rutas locales configuradas para servicios, mientras `/user/master/input` es el directorio HDFS del job. Usar nombres similares no vuelve equivalentes estos accesos. (p. 2-5)

**5.** Workers lista las máquinas del procedimiento. Los directorios deben existir y tener propietario correspondiente a la cuenta usada. Agregar un host a workers no instala ni concede permisos por sí solo. Crear carpeta namenode en trabajadores tampoco demuestra que ejecuten ese servicio: los procesos esperados siguen siendo DataNode/NodeManager. (p. 3-5)

**6.** SSH permite acceso del maestro a cuentas correctas sin introducir contraseña en la operación indicada; formateo figura como inicialización NameNode; sincronización distribuye cinco archivos. El classpath del maestro usa `/home/master/hadoop`, mientras la instalación de trabajadores usa otros homes; copiarlo tal cual no documenta adaptación de rutas. (p. 2-4)

**7.** ResourceManager muestra gestión de trabajos YARN; DataNode muestra estado del servicio de datos de un trabajador; NodeManager muestra tareas YARN locales. Sus puertos de ejemplo son 8088, 9864 y 8042. No son tres vistas intercambiables del mismo alcance. (p. 5)

**8.** Se crean textos locales, se cargan al input HDFS, se ejecuta el ejemplo WordCount y se consulta su archivo de salida. Las palabras `hadoop,mapreduce,yarn,test,bigdata,cluster,system,processing` aparecen una vez cada una. La salida p. 6 es inconsistente porque introduce palabras ausentes y frecuencias mayores. Esa reconstrucción es conteo manual de entradas, no prueba de ejecución. (p. 5-6)
