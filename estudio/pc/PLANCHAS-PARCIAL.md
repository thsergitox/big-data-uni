# Plancha resuelta: Examen Parcial 2026-I

**Alcance:** Parte 1, teoría. Las seis preguntas tienen respaldo en `lessons`. Las respuestas están redactadas para explicarlas en un examen, no para memorizar diapositivas sin entenderlas. Los ejemplos numéricos son elaboración propia.

**Enunciado original:** [PDF del parcial](/home/sergi/Documents/semesters/nine/bigdata/labs/planchas/Examen%20Parcial%202026-1-p1%20-%20sol.pdf), p. 1.

Las páginas indicadas son las páginas físicas del PDF, contando desde 1, incluso cuando una página es solo una imagen.

## 1. Graficar y describir los componentes de la arquitectura HDFS

**Fuente:** [Hadoop](/home/sergi/Documents/semesters/nine/bigdata/lessons/01%20Hadoop%20A_v12.pdf), pp. 29-32; el dibujo pedido está en la **p. 30**.

### Respuesta modelo
 
HDFS, Hadoop Distributed File System, es el sistema de archivos distribuido de Hadoop. Divide los archivos en bloques y los almacena en distintos DataNodes. Para aumentar la tolerancia a fallos, mantiene réplicas de esos bloques en varios nodos y puede distribuirlas entre racks.

```text
                      NameNode
                 metadatos del archivo:
               nombre, bloques, réplicas
                  ^                |
                  | consulta       | coordinación de bloques
                  |                v
Cliente ------ lectura/escritura ------ DataNodes
                                         |         |
                                  Rack 1 |         | Rack 2
                                     [B1] <----> [B1]
                                     [B2] <----> [B2]
                                         replicación
```

- **Cliente:** solicita información de ubicación al NameNode y lee/escribe los datos en los DataNodes.
- **NameNode:** administra el índice del sistema de archivos y los metadatos. En el dibujo aparece el nombre del archivo y su número de réplicas; no se muestra como intermediario de los bytes que lee el cliente.
- **DataNodes:** guardan y sirven los bloques reales. Pueden comunicarse para mover y replicar bloques.
- **Bloques:** partes de los archivos; se distribuyen para poder almacenar archivos grandes.
- **Réplicas y racks:** las copias en distintos hosts y racks reducen el riesgo de perder acceso por una falla localizada.

Ejemplo: si un archivo tiene bloques B1 y B2 y un nodo con B1 deja de funcionar, el cliente puede obtener B1 de otro nodo que mantenga una réplica.

**Complemento de p. 29:** el NameNode secundario genera instantáneas de las estructuras del principal. No debe presentarse como si la diapositiva dijera que reemplaza automáticamente al principal; eso no está demostrado allí.

**No confundir:** HDFS almacena; MapReduce procesa; YARN gestiona recursos. JobTracker/TaskTracker pertenecen al esquema de procesamiento antiguo mostrado en otras páginas, no son los componentes que debes poner en este dibujo HDFS.

**Para recordar:** «El NameNode conoce dónde está; el DataNode guarda el bloque; el cliente accede al dato; las réplicas permiten resistir fallas».

## 2. Describir el modelo de programación Mapeduce

**Fuente:** [MapReduce](/home/sergi/Documents/semesters/nine/bigdata/lessons/02%20MapReduce_v22.pdf), pp. 2-5 y 10-11; [WordCount](/home/sergi/Documents/semesters/nine/bigdata/lessons/02%20BigDataWordCount.pdf), pp. 20 y 25.

### Respuesta modelo

MapReduce es un modelo de programación y una implementación para procesar grandes conjuntos de datos en paralelo. El programador define dos funciones: **Map**, que transforma cada registro de entrada en pares clave-valor intermedios, y **Reduce**, que combina los valores agrupados por una misma clave. La infraestructura distribuye el trabajo entre máquinas y reúne los pares que tienen la misma clave.

```text
Entrada -> división -> Map -> agrupación por clave -> Reduce -> resultado
```

Ejemplo propio: contar palabras en dos líneas, `hadoop datos` y `hadoop`.

| Etapa | Resultado |
|---|---|
| Map de línea 1 | `(hadoop, 1)`, `(datos, 1)` |
| Map de línea 2 | `(hadoop, 1)` |
| Agrupación | `(hadoop, [1,1])`, `(datos, [1])` |
| Reduce: sumar | `(hadoop, 2)`, `(datos, 1)` |

Los Map pueden trabajar simultáneamente sobre partes diferentes. La agrupación o *shuffle* hace que todos los valores de una clave lleguen al Reduce correspondiente. Así, el programador expresa la transformación y la agregación sin programar manualmente toda la coordinación del clúster.

**No confundir:** Map no obtiene por sí solo el total global. Un combiner puede sumar localmente para ahorrar tráfico, pero su resultado es intermedio; Reduce produce el resultado de su partición.

**Para recordar:** «Map emite, la infraestructura agrupa y Reduce combina».

## 3. Describir y ejemplificar el funcionamiento de chunk leases

**Fuente:** [GFS](/home/sergi/Documents/semesters/nine/bigdata/lessons/07%20GFS01_v03.pdf), pp. 19-21 y 28-29.

### Respuesta modelo

En GFS, un *chunk lease* es una autorización temporal que el master concede a **una réplica de un chunk**, convirtiéndola en la primaria. Esa primaria determina un orden serial para las mutaciones del chunk. Las demás réplicas aplican las mutaciones en el mismo orden. El objetivo es coordinar las escrituras sin hacer pasar todos los datos por el master.

```text
Cliente -- pregunta primaria y ubicaciones --> Master
Cliente <-- primaria P, secundarias S1 y S2 -- Master

Cliente -- datos --> P, S1, S2 (reciben y almacenan temporalmente)
Cliente -- solicitud de escritura --> P [posee el lease]
                                      |
                                      +-- establece orden --> S1 y S2
                                      <-- confirmaciones --- S1 y S2
Cliente <-- resultado ---------------- P
```

**Ejemplo propio:** el chunk C tiene réplicas P, S1 y S2. Dos clientes solicitan mutaciones A y B. El master indica que P es primaria. Tras recibir los datos, P decide el orden A y luego B, y comunica ese orden a S1 y S2. Las tres réplicas aplican A antes que B, aunque las solicitudes hayan llegado casi a la vez.

El lease puede expirar, ser revocado por el master o extenderse mediante comunicación del chunkserver con el master. El envío de datos y la decisión del orden están separados: los bytes pueden circular según la topología conveniente de la red, no necesariamente pasando primero por la primaria.

**Advertencia del material:** p. 28 simplifica el permiso como si se otorgara al cliente; pp. 19-20 explican expresamente que la réplica primaria es quien posee el lease. Para describir quién serializa las mutaciones, usa esta última formulación.

**No confundir:** el master selecciona la primaria; la primaria ordena las mutaciones. No es un bloqueo global de todos los archivos.

## 4. Graficar y describir los niveles de uso de datos en BI

**Fuente:** [Inteligencia de negocios](/home/sergi/Documents/semesters/nine/bigdata/lessons/05%20C_BI_v11.pdf), **p. 6**, diagrama inspeccionado visualmente.

### Respuesta modelo

La diapositiva presenta cuatro niveles. De la base a la cima se pasa de las operaciones elementales a la planificación estratégica: se reduce el volumen resumido y aumenta el valor para decidir.

```text
                  / ESTRATÉGICO \
                 /  Data Mining  \       ANALÍTICO / BI
                /-----------------\
               /  CONOCIMIENTOS    \
              /  Data Warehousing  \      ANALÍTICO / BI
             /-----------------------\
            /    ADMINISTRATIVO       \
           / Sistemas de Información   \  TRANSACCIONAL / OLTP
          /-----------------------------\
         /          OPERACIONAL          \
        /          Base de datos          \ TRANSACCIONAL / OLTP
       /___________________________________\
```

| Nivel de la diapositiva | Función | Ejemplo propio |
|---|---|---|
| Operacional | Actividades y transacciones elementales de base de datos. | Registrar una venta individual. |
| Administrativo | Operaciones repetitivas, captura de datos básicos y tareas predefinidas mediante sistemas de información. | Registrar y consultar pedidos mediante el sistema habitual. |
| Conocimientos | Análisis, seguimiento y control; decisiones a partir de datos históricos mediante Data Warehousing. | Comparar ventas por región durante los últimos años. |
| Estratégico | Planificación empresarial, administrativa y a largo plazo; orientación al futuro mediante Data Mining. | Identificar patrones de clientes para planificar una expansión. |

En **esta figura**, los dos niveles inferiores se agrupan como transaccionales/OLTP y los superiores como analíticos/Business Intelligence. OLTP significa procesamiento de transacciones en línea; BI significa inteligencia de negocios.

**No confundir:** la pregunta solicita la pirámide de cuatro niveles de p. 6. La tabla de corto, mediano y largo plazo está en p. 18 y corresponde a otra pregunta del sustitutorio.

## 5. Describir con ejemplos los tipos de tareas de ETL

**Fuente:** [OLAP](/home/sergi/Documents/semesters/nine/bigdata/lessons/05%20B_OLAP.pdf), **p. 14**; contexto de extracción, transformación y carga en pp. 7-13.

### Respuesta modelo

ETL significa **extracción, transformación y carga**: extraer datos de sus fuentes, adecuarlos e incorporarlos al almacén de datos. Pero la pregunta remite a p. 14, donde las **tareas** se clasifican por tipo de carga:

1. **Initial Load, carga inicial:** es la primera incorporación de datos. Mueve grandes cantidades de información y consume bastante tiempo. Ejemplo propio: al crear el almacén, cargar todo el historial de ventas de los últimos cinco años.
2. **Incremental Load, carga incremental o actualización:** refresca periódicamente el almacén con datos nuevos o modificados. Mueve menos datos, pero exige detectar qué cambió desde la actualización anterior. Ejemplo propio: cada noche incorporar las ventas del día y los pedidos corregidos. El material menciona identificar instancias OLTP, disparadores (*triggers*), marcas de tiempo y comparación entre OLTP y DW.
3. **Full Load, carga total:** vuelve a cargar desde cero cuando el control de cambios resulta complejo. Ejemplo propio: reconstruir completamente una tabla analítica a partir de la fuente cuando no se puede identificar de manera fiable lo modificado.

**Diferencia clave:** carga inicial es la primera carga; carga total es una reconstrucción completa, que puede ocurrir después. Incremental no significa únicamente insertar: también contempla datos modificados.

**Error que debes evitar:** responder solo «extraer, transformar y cargar». Eso define ETL, pero omite los tres tipos de tarea que pide la diapositiva indicada.

## 6. Mencionar los desafíos del data lake

**Fuente:** [Warehouse, lake y lakehouse](/home/sergi/Documents/semesters/nine/bigdata/lessons/04%20LakeVsLakeHouse_v03ppt.pdf), **p. 13**.

### Respuesta modelo

La diapositiva agrupa **diez problemas concretos en cuatro categorías**. Conviene mencionar ambos niveles de la lista:

| Categoría | Desafíos que muestra la diapositiva |
|---|---|
| **Fiabilidad** | 1. Dificultad para anexar nuevos datos. 2. Dificultad para modificar datos existentes. 3. Trabajos que fallan a mitad del proceso. 4. Dificultad para manejar datos en tiempo real. 5. Mantener versiones históricas resulta costoso. |
| **Rendimiento** | 6. Dificultad para manejar grandes metadatos. 7. Demasiados archivos. 8. Bajo rendimiento. |
| **Gobernanza** | 9. Mala seguridad. |
| **Calidad** | 10. Calidad de los datos. |

Ejemplos propios para explicarlos: una carga interrumpida afecta la fiabilidad; demasiados archivos y metadatos dificultan consultas rápidas; accesos sin control afectan la gobernanza; fechas incompatibles afectan la calidad.

**Verificación importante:** parte de la lista está dentro de una imagen. Leer solo el texto extraído del PDF deja visibles las cuatro categorías, pero pierde los diez problemas; aquí se incluyeron tras revisar visualmente la página.

**Para recordar:** «Fiabilidad 5, rendimiento 3, gobernanza 1, calidad 1».

## Parte 2: exposición

No se inventa una solución: el enunciado remite a los lineamientos de un trabajo y no los incluye. Además, esta guía se concentra en el examen teórico solicitado.
