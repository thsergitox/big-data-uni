# 03. Big Data y WordCount: comprender todo el flujo

**Fuente exclusiva:** [02 BigDataWordCount.pdf](/home/sergi/Documents/semesters/nine/bigdata/lessons/02%20BigDataWordCount.pdf), **29 páginas reales**. Las referencias indican posición real del PDF: el código Java está en **p. 27**, aunque el pie muestra 28. Se revisaron las láminas gráficas p. 10, 15-17, 19, 21 y 24-27.

**Prioridad de examen:** explicar mediante pares clave-valor cómo pasan las entradas por Map, Shuffle y Reduce; distinguir modelo conceptual, ejecución paralela y configuración del job.

## 1. Mapa de temas

| Tema | Páginas PDF |
|---|---:|
| Hechos/cifras de motivación | 1-6 |
| Contexto económico, telefonía y plataformas sociales | 7-10 |
| Historia y definición orientada a valor | 11-14 |
| Mapas del ecosistema 2016, 2017 y 2018 | 15-17 |
| WordCount, analogía y paralelismo | 18-21 |
| MapReduce, Lisp y modelo gráfico | 22-24 |
| Flujo de palabras y arquitectura distribuida | 25-26 |
| Mapper, Reducer y job Java | 27 |
| Referencias no desarrolladas | 28-29 |

## 2. Motivación de Big Data: cifras históricas, no estadísticas actuales

El material muestra crecimiento de cantidad, diversidad de fuentes y generación continua de datos. Las cifras sirven para contextualizar estas ideas.

| Página | Cifras/ejemplos presentados | Concepto |
|---:|---|---|
| 2 | Información de dos días comparable a la acumulada hasta 2003; más del 90% creado en dos años; proyección de 3,2 a 40 zettabytes hacia 2020; duplicación industrial cada 1,2 años | Crecimiento y escala |
| 3 | 204 millones de correos/minuto, 1,8 millones de me gusta, 278 mil tweets y 200 mil fotos/minuto; más de 40 mil búsquedas Google/segundo y 3,5 mil millones/día | Actividad continua de usuarios/servicios |
| 4 | 100 horas de video/minuto; comparación de DVDs apilados hasta la Luna; registros telefónicos con 312 TB y casi 2 billones de filas | Multimedia y registros masivos |
| 5 | 570 sitios nuevos/minuto; proyección laboral hacia 2015; centros de datos equivalentes a casi 6 000 campos de fútbol | Infraestructura y empleo |
| 6 | 12 TB diarios de tweets para sentimiento; tráfico móvil de 1,5 EB/mes con aumento de 81% entre 2012-2014, 53% video; referencia a análisis de tráfico global por NSA | Comportamiento, movilidad y análisis |

**Advertencia:** son afirmaciones/proyecciones de las diapositivas, varias expresamente históricas. No se verifican como cifras actuales y no se presentan como datos de 2026.

Las p. 7-9 relacionan Ciencia de Datos con oportunidades económicas, y teléfonos/plataformas sociales con generación diaria de contenido. La p. 8 menciona 5 000 millones de líneas/usuarios y 7 800 millones de SIM en 2017, junto con una proyección a 2025 de 5 900 millones de suscriptores y 71% de población. **Personas y SIM no son equivalentes:** la propia lámina muestra cantidades diferentes.

La p. 9 contiene la expresión `2167 usuario` sin unidad clara; no completamos inventando millones. La p. 10 muestra usuarios activos de plataformas globales en **enero de 2018**, expresados en millones: un panorama de redes/mensajería encabezado por Facebook y YouTube, no un ranking vigente.

## 3. Historia que presenta la clase (p. 11-13)

| Año | Hito indicado por el documento |
|---:|---|
| 1965 | Proyección de un centro de datos en Estados Unidos |
| 1989 | Erik Larson habla de Big Data con el sentido indicado |
| 1991 | Nacimiento de internet en esta cronología |
| 1996 | Almacenamiento con costos más accesibles |
| 1997 | Lanzamiento del sistema de búsqueda de Google |
| 1999 | Estudio académico del término Big Data |
| 2001 | Doug Laney define las tres V |
| 2005 | Web 2.0, contenido de usuarios y ubicación de Hadoop en la cronología |
| 2007 | Difusión del concepto en Wired |
| 2016 | Interés generalizado, expertos, aprendizaje automático e Internet de las Cosas |

El cierre proyecta más datos y necesidad de recolectarlos, adaptarlos, almacenarlos y analizarlos; menciona computación cuántica como expectativa. **Idea de examen:** relacionar expansión de fuentes y capacidad de almacenamiento, no convertir estas láminas en prueba histórica externa o de predicciones cumplidas.

La p. 12 menciona tres V sin desarrollar sus definiciones en este PDF. No se añade aquí una lección externa sobre ellas.

## 4. Definición: volumen no equivale a valor (p. 14)

Big Data comprende grandes volúmenes de datos **estructurados y no estructurados**, pero la definición destaca qué hace la organización con ellos: analizarlos para obtener comprensión que apoye mejores decisiones y acciones estratégicas.

**Ejemplo propio:** almacenar textos no da automáticamente una conclusión. WordCount los transforma en frecuencias por palabra, una medida interpretable. La utilidad nace del procesamiento y uso de resultados, no solo de acumular bytes.

## 5. Ecosistema 2016-2018 (p. 15-17)

Los mapas agrupan herramientas/empresas por responsabilidad:

- **Infraestructura:** Hadoop local/en nube, servicios, almacenamiento, bases NoSQL, integración/transformación, monitoreo y seguridad.
- **Analítica:** plataformas de ciencia de datos/análisis, BI, visualización, estadística, aprendizaje automático y tiempo real.
- **Aplicaciones:** usos empresariales y sectores; ventas/marketing, atención al cliente y recursos humanos, entre otros.
- **Herramientas transversales y código abierto:** frameworks, consultas/flujos, acceso a datos, coordinación y procesamiento de streams.
- **Fuentes y API:** salud, finanzas, geografía, IoT y otras áreas; también aparecen formación/incubación.

**Qué aprender:** el ecosistema conecta fuentes, infraestructura, procesamiento, análisis y aplicación. Las imágenes son panoramas históricos; no explican algoritmos de cada logotipo. No corresponde inventar características de cada empresa ni añadir los documentos de los enlaces finales.

## 6. WordCount: problema, clave y valor (p. 18-20)

**Problema:** contar cuántas veces aparece **cada palabra** en el conjunto de textos. No es únicamente contar el total de palabras ni el número de documentos.

| Etapa | Clave | Valor |
|---|---|---|
| Entrada conceptual del pseudocódigo | Nombre del documento | Contenido del documento |
| Salida de Map | Palabra | Un conteo 1 por aparición |
| Entrada de Reduce tras agrupar | Palabra | Lista de conteos de esa palabra |
| Resultado | Palabra asociada al total | Suma de los conteos |

El pseudocódigo representa los conteos como **cadenas**; Reduce usa `ParseInt` antes de sumar y `AsString` al emitir. No concatena los unos: realiza suma numérica.

### 6.1. Ejemplo exacto de p. 20

```text
Map:
(Sam,1), (Apple,1), (Sam,1), (Mom,1), (Sam,1), (Mom,1)

Agrupación:
(Sam,[1,1,1])   (Apple,[1])   (Mom,[1,1])

Reduce:
(Sam,3)        (Apple,1)     (Mom,2)
```

La palabra se vuelve clave porque **todas sus apariciones deben reunirse**. El nombre del documento era una clave de entrada, pero conservarlo como clave intermedia resolvería otro agrupamiento.

### 6.2. Analogía de frutas (p. 19 y 21)

Cortar una fruta representa transformar; licuar representa combinar. La ilustración paralela muestra varias entradas transformadas y piezas reunidas por tipo de fruta antes de combinarse.

**Límite:** Map no significa simplemente cortar archivos. El contrato formal es transformar registros en pares; Shuffle reúne por clave y Reduce combina. La analogía ayuda a visualizar, pero NO sustituye ese contrato.

## 7. Modelo funcional y responsabilidad del entorno (p. 22-24)

La interfaz se expresa como **Map + Reduce**, mientras la implementación distribuida oculta paralelización, planificación, entrada/salida y tolerancia a fallos.

La p. 23 presenta la inspiración en Lisp: Map de longitud sobre listas produce **0,1,2,3**; Reduce con suma sobre esos valores produce **6**. Aplicar una transformación y combinar resultados son responsabilidades distintas.

El modelo de p. 24 añade **Shuffle (merge sort by key)**:

```text
(input_key, input_value)
          |
       MAP: transformar
          |
(intermediate_key, intermediate_value)
          |
 SHUFFLE: reunir/ordenar por clave
          |
(key, lista_de_valores)
          |
      REDUCE: combinar
          |
 resultado asociado a la clave
```

Map no reúne por sí mismo valores de todos los workers. Reduce no busca manualmente en todos los documentos: recibe valores ya agrupados.

## 8. Flujo completo de los tres textos de p. 25

### 8.1. Input y Split

```text
S1: the small brown fox
S2: a fox speaks to another fox
S3: brown cow cross the road
```

Split divide el trabajo de entrada en porciones; cada Map procesa su porción.

### 8.2. Reconstrucción desde las entradas

```text
Map S1 -> (the,1),(small,1),(brown,1),(fox,1)
Map S2 -> (a,1),(fox,1),(speaks,1),(to,1),(another,1),(fox,1)
Map S3 -> (brown,1),(cow,1),(cross,1),(the,1),(road,1)

Shuffle:
 brown -> [1,1]        fox -> [1,1,1]       the -> [1,1]
 a,another,cow,cross,road,small,speaks,to -> [1] cada una

Reduce:
 brown -> 2           fox -> 3             the -> 2
 las otras ocho palabras -> 1 cada una
```

La salida se divide entre **dos Reduce**: un grupo con `a,another,brown,cross,cow,fox`; otro con `road,small,speaks,the,to`. Tener dos reducers no cambia que todos los valores de cada palabra deben reunirse en el que le corresponde.

**Inconsistencia gráfica verificada:** la columna Map del primer split repite `(brown,1)`, aunque su entrada solo tiene un `brown`. El resultado final `brown,2` sí corresponde a las dos apariciones totales de las entradas S1/S3. La reconstrucción anterior sigue esas entradas y su resultado coherente, no copia esa repetición del dibujo.

## 9. Arquitectura distribuida (p. 26)

Componentes visibles: programa del usuario, scheduler, master, workers, GFS de entrada y GFS de salida.

1. Programa/scheduler participan en solicitud y asignación de recursos.
2. Master asigna tareas map y reduce a workers.
3. Maps leen splits de la entrada en GFS.
4. Intermedios se escriben **localmente**; se notifican ubicaciones al master.
5. Reducers leen **remotamente**, ordenan y combinan.
6. Los resultados finales se escriben en GFS; el dibujo muestra dos archivos.

```text
                Programa -> Scheduler
                      |
                    Master
              tareas / ubicaciones
                  /             \
GFS input -> workers MAP -> discos locales -> workers REDUCE -> GFS output
                                                  ordenar       archivo 0
                                                  reducir       archivo 1
```

**No confundir:** intermedios locales no equivalen a GFS de entrada/salida. El gráfico es el modelo mostrado, no una descripción exhaustiva de todos los servicios Hadoop 3.

## 10. Interpretación del código Java (p. 27 REAL)

### 10.1. Mapper

Extiende `Mapper<LongWritable, Text, Text, IntWritable>`. Convierte el valor de entrada a cadena, recorre tokens con `StringTokenizer` y emite con `context.write(word, one)`.

- Entrada del método: clave `LongWritable`, valor `Text`.
- Salida: palabra `Text`, conteo `IntWritable` con valor 1.
- El cuerpo mostrado recibe la clave de entrada pero no la usa para construir las palabras.

**Diferencia con el pseudocódigo:** p. 20 usa nombre/contenido; Java usa otros tipos de entrada. No atribuimos un significado exacto a la clave numérica por suposición: la imagen no lo explica aquí. Representaciones diferentes pueden conservar el problema de contar palabras.

### 10.2. Reducer

Extiende `Reducer<Text, IntWritable, Text, IntWritable>`. Recibe palabra e iterable de conteos, inicia `sum=0`, suma cada `val.get()` y escribe palabra/nuevo total.

No cuenta cuántas claves hay: **suma valores asociados a una clave**.

### 10.3. Configuración del job

El `main` crea `Configuration` y un `Job` llamado `wordcount`; establece tipos de salida, Mapper/Reducer, `TextInputFormat` y `TextOutputFormat`; añade rutas a partir de argumentos y espera con `waitForCompletion(true)`.

Un **job** es el trabajo completo configurado, no una sola palabra, tarea o máquina. La imagen no incluye configuración de combiner, por lo que este documento no añade ese mecanismo al ejemplo.

Las p. 28-29 listan referencias de MapReduce y tecnologías como Spark, Tez, DataFlow, TensorFlow y Beam; no desarrollan sus algoritmos. No se consultaron esos enlaces.

## 11. Confusiones frecuentes

1. **Clave de entrada = clave intermedia:** nombre de documento y palabra cumplen papeles distintos.
2. **Map emite una sola vez por palabra distinta:** emite por aparición; de ahí los repetidos.
3. **Shuffle suma:** agrupa/ordena; Reduce suma.
4. **Reduce cuenta documentos:** en este problema cuenta ocurrencias por palabra.
5. **El 1 cadena se concatena:** el pseudocódigo lo convierte a entero y suma.
6. **Maps escriben el resultado final:** el gráfico separa intermedios locales y archivos de reducers.
7. **Job = tarea:** job reúne la ejecución completa.
8. **Cifras 2018 = cifras actuales:** son contexto histórico.
9. **Cada emisión dibujada en p. 25 es correcta:** existe la repetición de `brown` advertida.

## 12. Preguntas estilo examen: responde antes de consultar

1. Definir Big Data según el material y explicar por qué volumen no garantiza valor.
2. Describir tres fuentes que motivan Big Data y explicar el contexto temporal de sus cifras.
3. Explicar las familias de los mapas de ecosistema: infraestructura, analítica y aplicación.
4. **Graficar y describir WordCount** para `Sam Apple Sam Mom Sam Mom`, mostrando pares de cada fase.
5. Explicar por qué la palabra es la clave intermedia y qué representa cada valor.
6. Reconstruir salida de los tres textos de p. 25 e identificar su inconsistencia gráfica.
7. Comparar analogía de frutas con el contrato clave-valor; explicar Shuffle.
8. Describir el diagrama distribuido y separar intermedios locales de salida GFS.
9. Explicar Mapper, Reducer y job Java; comparar tipos con pseudocódigo.
10. Explicar los ejemplos Lisp de longitud/suma y qué añade el agrupamiento por clave al modelo distribuido.

---

## 13. Respuestas modelo

**1.** Big Data abarca grandes volúmenes estructurados y no estructurados. Importa su análisis para obtener comprensión y apoyar decisiones y acciones estratégicas. Acumular datos sin usarlos no es producir valor. (p. 14)

**2.** Correos/redes aportan actividad de usuarios, videos aportan multimedia y telefonía aporta registros/tráfico móvil. El documento usa cantidades por segundo/minuto y volúmenes almacenados para mostrar escala y generación continua. Muchas son proyecciones o estadísticas históricas, no mediciones actuales. (p. 2-10)

**3.** Infraestructura sostiene almacenamiento, integración y operación; analítica transforma/explora datos; aplicaciones muestran usos de negocio y sectores. También hay herramientas abiertas, fuentes/API y formación. Los mapas ilustran especialización y amplitud, no capacidades detalladas de cada producto. (p. 15-17)

**4.** Map emite `(Sam,1),(Apple,1),(Sam,1),(Mom,1),(Sam,1),(Mom,1)`. Shuffle reúne `(Sam,[1,1,1]),(Apple,[1]),(Mom,[1,1])`. Reduce da `(Sam,3),(Apple,1),(Mom,2)`. El dibujo debe separar emisión, agrupación y suma. (p. 20 y 24)

**5.** Se quiere un total por palabra, por eso las apariciones de esa palabra comparten clave. El valor map registra una aparición; la lista agrupada reúne sus conteos; Reduce suma la lista. El nombre del documento pertenece a la entrada conceptual, no al agrupamiento global por palabra. (p. 20)

**6.** `brown=2, fox=3, the=2`; `a,another,cow,cross,road,small,speaks,to=1`. El primer split contiene `brown` una vez, pero la columna Map lo repite. La salida final coincide con las entradas originales y no con esa repetición gráfica. (p. 25)

**7.** Cortar transforma y licuar combina, pero el modelo formal exige pares clave-valor y reunión por clave. Map emite, Shuffle agrupa/ordena y Reduce combina. La implementación paralela de frutas ayuda a visualizar el reparto y reagrupamiento, no reemplaza el contrato. (p. 19, 21 y 24)

**8.** Programa/scheduler y master coordinan workers. Maps leen splits de GFS y producen intermedios locales; notifican ubicaciones. Reducers leen esos archivos remotamente, ordenan/reducen y escriben archivos finales en GFS. Intermedios y resultados finales tienen ubicación y función distintas. (p. 26)

**9.** Mapper recorre tokens y emite palabra/1; Reducer suma los valores de cada palabra; `main` configura clases, tipos, formatos, rutas y espera el job. Pseudocódigo usa nombre/contenido y conteos cadena; Java usa entrada `LongWritable/Text` y conteos `IntWritable`. Los tipos cambian, la idea del conteo se conserva. (p. 20 y 27)

**10.** Map de longitud transforma listas en 0,1,2,3; Reduce con suma da 6. El modelo distribuido añade claves y Shuffle para entregar a cada Reduce los valores que corresponden a su clave, en lugar de una lista indiferenciada de todos los resultados. (p. 23-24)
