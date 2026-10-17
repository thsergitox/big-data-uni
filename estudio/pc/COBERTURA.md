# Qué se puede responder y qué queda pendiente

## Criterio

Solo se resuelven temas desarrollados en los diez PDFs de `lessons`. Una mención aislada no equivale a tener una clase completa del tema. Ejemplos propios se etiquetan; no se usa internet para completar lo faltante. Las páginas son físicas del PDF, contando desde 1 y sin eliminar páginas con imágenes.

## Cruce de las dos planchas

| Plancha | Pregunta | Estado | Respaldo disponible |
|---|---|---|---|
| Parcial | 1. Arquitectura HDFS | Completa | Hadoop p. 30, con explicación pp. 29-32. |
| Parcial | 2. Modelo MapReduce | Completa | MapReduce pp. 2-5; WordCount pp. 20, 25. |
| Parcial | 3. Chunk leases | Completa | GFS pp. 19-21, 28-29. |
| Parcial | 4. Niveles de uso de datos/BI | Completa | BI p. 6, pirámide de cuatro niveles. |
| Parcial | 5. Tipos de tareas ETL | Completa | OLAP p. 14: inicial, incremental y total. |
| Parcial | 6. Desafíos data lake | Completa | Warehouse/lake/lakehouse p. 13: diez problemas en cuatro categorías. |
| Sustitutorio | 1. Escritura aleatoria Spark/Uber | **Pendiente, sin respuesta** | Falta la presentación Uber. Spark/Hudi mencionados en otros documentos no desarrollan ese caso. |
| Sustitutorio | 2. BI por plazos | Completa | BI p. 18. |
| Sustitutorio | 3. Arquitectura Lakehouse | **Parcial respecto del enunciado exacto** | LakeVs pp. 10-11 y artículo Lakehouse pp. 3-4 desarrollan el tema. Falta la presentación de reforzamiento/diapositiva 18 citada. |
| Sustitutorio | 4. Conceptos Spark Streaming | **Pendiente, sin respuesta** | Falta la presentación Spark Streaming. |
| Sustitutorio | 5. Consistencia GFS | Completa | GFS pp. 22-25. |
| Sustitutorio | 6. Cubos OLAP | Completa | OLAP pp. 26-30. |

**Balance:** 9 preguntas con cobertura completa, 1 respuesta limitada al respaldo disponible y 2 preguntas sin responder. Son 12 preguntas de teoría; las partes de exposición no incluyen lineamientos suficientes y quedan fuera.

## Inventario: una guía por cada PDF de lessons

| PDF fuente | Páginas | Guía |
|---|---:|---|
| [Hadoop](/home/sergi/Documents/semesters/nine/bigdata/lessons/01%20Hadoop%20A_v12.pdf) | 36 | [01-hadoop](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/01-hadoop.md) |
| [MapReduce](/home/sergi/Documents/semesters/nine/bigdata/lessons/02%20MapReduce_v22.pdf) | 23 | [02-mapreduce](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/02-mapreduce.md) |
| [BigData/WordCount](/home/sergi/Documents/semesters/nine/bigdata/lessons/02%20BigDataWordCount.pdf) | 29 | [03-wordcount](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/03-wordcount.md) |
| [Instalación Hadoop](/home/sergi/Documents/semesters/nine/bigdata/lessons/Instalacion_y_Configuracion_de_Hadoop_3_en_cluster_de_nodos.pdf) | 6 | [04-instalacion-hadoop](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/04-instalacion-hadoop.md) |
| [Analítica/minería](/home/sergi/Documents/semesters/nine/bigdata/lessons/04%20DataAnalyticDataMining_v15_es.pdf) | 37 | [05-analitica-mineria](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/05-analitica-mineria.md) |
| [Warehouse/lake/lakehouse](/home/sergi/Documents/semesters/nine/bigdata/lessons/04%20LakeVsLakeHouse_v03ppt.pdf) | 17 | [06-warehouse-lake-lakehouse](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/06-warehouse-lake-lakehouse.md) |
| [Artículo Lakehouse](/home/sergi/Documents/semesters/nine/bigdata/lessons/Lakehouse%20Andy%20Aguilar.pdf) | 6 | [07-lakehouse-refuerzo](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/07-lakehouse-refuerzo.md) |
| [OLAP](/home/sergi/Documents/semesters/nine/bigdata/lessons/05%20B_OLAP.pdf) | 43 | [08-olap](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/08-olap.md) |
| [BI](/home/sergi/Documents/semesters/nine/bigdata/lessons/05%20C_BI_v11.pdf) | 41 | [09-inteligencia-negocios](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/09-inteligencia-negocios.md) |
| [GFS](/home/sergi/Documents/semesters/nine/bigdata/lessons/07%20GFS01_v03.pdf) | 40 | [10-gfs](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/10-gfs.md) |

## Precauciones para no estudiar una respuesta equivocada

- **ETL:** la pregunta del parcial pide tipos de carga de p. 14, no solo las tres etapas E-T-L.
- **BI:** la pirámide de p. 6 tiene cuatro niveles; la tabla de p. 18 tiene tres plazos. No son la misma clasificación.
- **Hadoop:** el PDF mezcla arquitectura antigua con Hadoop 3. Se distinguen los módulos de almacenamiento, procesamiento y gestión de recursos.
- **Lakehouse:** el artículo de seis páginas no es una presentación con dieciocho diapositivas.
- **Imágenes:** la figura HDFS y parte de la lista de desafíos del lake no quedan completas al extraer texto. Se revisaron visualmente.
- **GFS:** se conservan las expresiones de clase, pero se explican las simplificaciones e inconsistencias internas del material.
- **Instalación:** estudiar la función de cada paso no exige ejecutar comandos ni aceptar como correcta toda salida ilustrativa del PDF.
