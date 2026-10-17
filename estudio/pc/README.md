# Estudio para la PC teórica de Análisis en Macrodatos

Material preparado **solo con las dos planchas y los diez PDFs disponibles en lessons**. Incluye explicaciones, diagramas, ejemplos propios, preguntas para practicar y respuestas modelo. No predice lo que el profesor preguntará: prioriza los temas que efectivamente aparecen en las planchas.

## Empieza aquí

1. [Plancha del parcial resuelta](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/PLANCHAS-PARCIAL.md): las seis preguntas, con respuestas explicadas y fuentes/páginas.
2. [Repaso rápido](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/REPASO-RAPIDO.md): relaciones entre conceptos, diferencias que suelen confundirse y lista de comprobación.
3. [Simulacro teórico](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/SIMULACRO.md): responde sin mirar y después revisa las respuestas.
4. [Plancha del sustitutorio, filtrada](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/PLANCHAS-SUSTITUTORIO.md): solo lo respaldado. Uber y Spark Streaming quedan sin responder; Lakehouse tiene advertencia de referencia faltante.
5. [Cobertura y fuentes](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/COBERTURA.md): matriz pregunta-tema-PDF-página e inventario completo.

## Guías por PDF

| Orden | Guía | Qué debes poder explicar |
|---|---|---|
| 1 | [Hadoop](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/01-hadoop.md) | Módulos, HDFS, bloques, réplicas, localidad y ecosistema. |
| 2 | [MapReduce](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/02-mapreduce.md) | Map, agrupación, Reduce, ejecución, fallas y optimizaciones. |
| 3 | [Big Data y WordCount](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/03-wordcount.md) | Concepto Big Data y recorrido concreto de pares clave-valor. |
| 4 | [Instalación Hadoop](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/04-instalacion-hadoop.md) | Por qué se configuran nombres, SSH, Java, HDFS y YARN; verificación conceptual. |
| 5 | [Analítica y minería](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/05-analitica-mineria.md) | Minería, ML, calidad, clasificación, agrupamiento, asociación y visualización. |
| 6 | [Warehouse, lake y lakehouse](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/06-warehouse-lake-lakehouse.md) | Arquitecturas, desafíos del lake y capas de preparación de datos. |
| 7 | [Artículo Lakehouse](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/07-lakehouse-refuerzo.md) | Almacenamiento abierto, metadatos transaccionales, SQL y analítica avanzada. |
| 8 | [OLAP](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/08-olap.md) | ETL, hechos, dimensiones, granularidad, cubos y consultas multidimensionales. |
| 9 | [Inteligencia de negocios](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/09-inteligencia-negocios.md) | Datos-información-conocimiento, niveles, plazos, DW, datamarts e integración. |
| 10 | [GFS](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/guias/10-gfs.md) | Master, chunks, flujo de datos/control, leases, consistencia y recuperación. |

Cada guía tiene preguntas y respuestas modelo para autoevaluarte. Puedes leer en otro orden según tus dificultades.

## Si tienes poco tiempo: ruta de 90 minutos

- **25 min:** responde las seis preguntas del parcial y contrasta con el solucionario.
- **20 min:** redibuja de memoria HDFS, chunk lease, pirámide BI y cubo OLAP. Explica cada flecha: quién consulta, quién guarda, quién decide y quién recibe.
- **15 min:** compara carga inicial/incremental/total, OLTP/OLAP y warehouse/lake/lakehouse.
- **20 min:** realiza el simulacro sin consultar; revisa después solo tus errores.
- **10 min:** repite en voz alta las respuestas que no pudiste justificar.

**Tradeoff:** esta ruta prioriza los temas repetidos en planchas, pero no reemplaza las diez guías. Si tienes más tiempo, recorre todas y responde sus preguntas.

## Cómo construir una respuesta teórica sólida

Usa esta estructura, adaptándola al verbo del enunciado:

1. **Definir:** qué es el concepto, en una o dos frases.
2. **Describir:** componentes o etapas y función de cada uno.
3. **Explicar el porqué:** qué problema resuelve o qué ventaja aporta.
4. **Ejemplificar:** un caso pequeño, concreto y coherente con la explicación.
5. **Graficar si lo piden:** flechas con significado y elementos rotulados.

No basta con nombrar herramientas. Si escribes «NameNode», debes poder explicar qué administra y por qué el cliente no le pide los bytes del archivo. Si escribes «Reduce», debes mostrar qué clave y qué valores recibe.

## Límites

- No se resuelve el caso Uber ni Spark Streaming.
- La pregunta Lakehouse del sustitutorio se responde solo con la arquitectura del material disponible, no como copia del reforzamiento faltante.
- No se modificaron los PDFs ni se ejecutó/compiló el proyecto.
- Las estadísticas antiguas y comparativas comerciales se tratan como contenido histórico de clase, no como datos actuales verificados.
