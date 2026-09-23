# Guion completo de exposición

Duración prevista: **9 min 40 s**. El límite indicado por la práctica es 10 minutos.

En las diapositivas aparece la **pregunta exacta**. Este guion añade lo que debemos decir oralmente: **de dónde nace la pregunta**, cómo la resolvimos y cómo interpretar el resultado.

## Diapositiva 1. Portada — 10 s

**Sergio:** “Presentaremos catorce preguntas sobre los indicadores de ocupabilidad de MINCETUR. Para cada una mostraremos cómo la estructura del dataset origina la pregunta y cómo Mapper y Reducer construyen la respuesta.”

## Diapositiva 2. Contenido — 10 s

**Arbués:** “Las consultas no son todas iguales: tenemos agregaciones, estadística descriptiva, búsqueda, comparación encadenada, clasificación y regresión.”

## Diapositiva 3. Cómo leer el dataset — 40 s

**Arbués:** “Una fila representa un mes, un departamento y un nivel de clase y categoría; no representa un hotel individual. El archivo consolidado contiene 38 mil 730 filas desde 2019 hasta junio de 2025.”

**Sergio:** “El valor `TT` sí viene en el dataset: aparece tanto en `ID_CLASE` como en `ID_CATEGORIA`, acompañado por ‘TODAS CONSOLIDADAS’. Nosotros escribimos `TT/TT` como abreviatura para decir que ambos códigos son TT; no es una columna nueva ni un valor inventado. Filtramos ese par para no sumar el total junto con sus desagregaciones.”

**Arbués:** “TNOH mide ocupación de habitaciones; TNOC, ocupación de plazas-cama. Un arribo cuenta al huésped al llegar y una pernoctación cuenta cada noche. Esas diferencias generan varias preguntas.”

**Sergio:** “HDFS almacena el CSV y YARN ejecuta los JAR en Hadoop 3.5.0. Un nodo valida el flujo, pero no demuestra escalamiento horizontal.”

## Diapositiva 4. Tipos y preguntas — 25 s

**Sergio:** “Este cuadro organiza las catorce preguntas por finalidad. Q01 a Q05 responden los subítems 1a a 1e, que exigen usar dos o más columnas. Q06 responde el ítem 2; Q07, el 3; Q08, el 4; Q09 y Q10, los subítems 5a y 5b; Q11 y Q12, 6a y 6b; y Q13 y Q14, 7a y 7b.”

**Arbués:** “Esta clasificación explica por qué no todas las consultas necesitan el mismo diseño MapReduce.”

## Diapositiva 5. Q01 / ítem 1a: Arribos por origen — 25 s

**De dónde sale:** el dataset separa `TOTAL_ARRIBOS_NAC` y `TOTAL_ARRIBOS_EXT`, por lo que podemos comparar cómo evolucionaron ambos mercados.

**Sergio:** “La pregunta es cómo evolucionaron los arribos nacionales y extranjeros por año. El Mapper filtra TT/TT y emite año como clave con ambos valores. El Reducer recibe todos los meses y departamentos del año y realiza dos sumas independientes.”

**Sergio:** “La salida evidencia la caída de 2020 y la recuperación posterior. No comparamos directamente 2025 porque contiene seis meses.”

## Diapositiva 6. Q02 / ítem 1b: TNOH territorial — 25 s

**De dónde sale:** un valor nacional oculta diferencias entre departamentos y el CSV contiene TNOH mensual para cada territorio.

**Arbués:** “Preguntamos cuál fue la TNOH media de cada departamento por año. El Mapper emite año-departamento y el par `TNOH,1`. El Reducer suma tasas y contadores y calcula la media mensual.”

**Arbués:** “En 2024 Callao obtuvo 41.60 y Loreto 12.99. Es una media simple y no está ponderada por habitaciones.”

## Diapositiva 7. Q03 / ítem 1c: Ocupación por clase — 30 s

**De dónde sale:** el dataset incluye clase de hospedaje y dos tasas distintas de ocupación: habitaciones y plazas-cama.

**Sergio:** “Preguntamos cómo cambian TNOH y TNOC según la clase. El Mapper conserva el total de categoría de cada clase, excluye el total global y emite año-clase con ambas tasas y un contador. El Reducer calcula las dos medias.”

**Sergio:** “En 2024 los resorts presentaron valores altos. TNOH y TNOC pueden diferir porque una habitación ocupada puede contener más de una plaza-cama ocupada.”

## Diapositiva 8. Q04 / ítem 1d: Empleo por capacidad — 25 s

**De dónde sale:** el empleo bruto favorece a los departamentos con mayor capacidad; necesitamos una unidad común para compararlos.

**Arbués:** “Preguntamos cuántos empleos existen por cada cien habitaciones. El Mapper emite empleo y habitaciones por año-departamento. El Reducer suma ambos campos y después calcula cien por empleo dividido entre habitaciones.”

**Arbués:** “Lima obtuvo 54.36 en 2024. Esta razón no mide productividad ni calidad del empleo.”

## Diapositiva 9. Q05 / ítem 1e: Estacionalidad — 25 s

**De dónde sale:** la columna mes permite investigar si la demanda se concentra en determinadas épocas.

**Sergio:** “Preguntamos qué meses concentran más arribos y pernoctaciones. El Mapper usa el mes como clave y envía ambos indicadores. El Reducer suma el mismo mes de todos los departamentos y años.”

**Sergio:** “Enero tiene el mayor acumulado, pero enero a junio incluyen el semestre adicional de 2025.”

## Diapositiva 10. Q06 / ítem 2: Medidas estadísticas — 25 s

**De dónde sale:** además de comparar grupos, necesitamos conocer el valor típico y la dispersión de la TNOH departamental.

**Arbués:** “Preguntamos por cantidad, media, mediana y desviación poblacional. El Mapper envía las 1 950 tasas TT/TT bajo una clave común. El Reducer calcula media y varianza incremental y ordena los valores para hallar la mediana.”

**Arbués:** “La media fue 21.74 y la desviación 7.07. Una clave global funciona por el volumen pequeño.”

## Diapositiva 11. Q07 / ítem 3: Búsqueda de subtexto — 25 s

**De dónde sale:** no todas las consultas son numéricas; también necesitamos localizar registros por clase, categoría o departamento.

**Sergio:** “Preguntamos qué registros contienen un texto. El Mapper compara el término con tres columnas y emite el offset y la fila coincidente.”

**Sergio:** “No existe Reducer porque cada coincidencia ya es un resultado final. Se encontraron 11 mil 525 filas para ‘hotel’; es una búsqueda literal.”

## Diapositiva 12. Q08 / ítem 4: Máximo y mínimo — 25 s

**De dónde sale:** Q02 produce todos los promedios, pero no responde directamente quién ocupa los extremos de cada año.

**Arbués:** “Preguntamos qué departamentos tuvieron la menor y mayor TNOH media. El Mapper agrupa por año y envía departamento con TNOH. El Reducer calcula primero cada media territorial y después selecciona mínimo y máximo.”

**Arbués:** “En 2024 Loreto fue el mínimo y Callao el máximo. Son dos operaciones dentro de un solo Reducer, no dos jobs.”

## Diapositiva 13. Q09 / ítem 5a: Dos MapReduce enlazados — 35 s

**De dónde sale:** para hablar de recuperación necesitamos comparar un año previo a la pandemia con un año completo reciente.

**Sergio:** “Preguntamos cuánto cambió la TNOH entre 2019 y 2024. A la izquierda está el primer job: Mapper agrupa año-departamento y Reducer calcula cada media. A la derecha, el segundo Mapper reagrupa por departamento y el segundo Reducer resta 2024 menos 2019.”

**Sergio:** “Apurímac tuvo la mayor caída y Ucayali el mayor aumento. La diferencia cuantifica cambio, pero no demuestra causalidad.”

## Diapositiva 14. Q10 / ítem 5b: Dos MapReduce enlazados — 35 s

**De dónde sale:** los arribos totales no muestran cuánto depende cada territorio del visitante extranjero ni cómo cambió esa dependencia por periodo.

**Arbués:** “Preguntamos qué porcentaje de arribos fue extranjero. En el primer job, Mapper calcula la cuota y Reducer deja un valor por mes. En el segundo, Mapper reagrupa por departamento-periodo y Reducer promedia las cuotas mensuales.”

**Arbués:** “Cusco cayó fuertemente en 2020–2021 y luego se recuperó. Es una media mensual no ponderada por arribos.”

## Diapositiva 15. Q11 / ítem 6a: Clasificación Naive Bayes — 30 s

**De dónde sale:** después del análisis descriptivo queremos saber si la capacidad y la actividad permiten anticipar una ocupación alta o baja.

**Sergio:** “La pregunta es si la TNOH mensual es alta o baja según otros indicadores. El Mapper codifica cada ejemplo bajo `MODEL`. El Reducer usa 2019–2023 para calcular la mediana, formar las etiquetas y entrenar Gaussian Naive Bayes; después evalúa 2024 y 2025-I.”

**Sergio:** “El recall de 0.287 indica que dejó sin detectar muchos casos altos. Lo usamos como línea base probabilística.”

## Diapositiva 16. Q12 / ítem 6b: Clasificación logística — 30 s

**De dónde sale:** Naive Bayes obtuvo precisión alta, pero un recall bajo; necesitamos comprobar si otro clasificador logra un mejor equilibrio.

**Arbués:** “Repetimos la pregunta de si la TNOH mensual es alta o baja, ahora usando regresión logística. El Mapper entrega exactamente los mismos ejemplos. El Reducer estandariza usando solo entrenamiento, ajusta doscientas iteraciones y calcula las métricas.”

**Arbués:** “El F1 aumentó de 0.440 a 0.793 en 2024. Como usamos los mismos datos, la comparación es directa.”

## Diapositiva 17. Q13 / ítem 7a: Regresión lineal — 30 s

**De dónde sale:** clasificar TNOH como alta o baja pierde información sobre su magnitud exacta.

**Sergio:** “Preguntamos qué valor continuo de TNOH puede estimarse. El Mapper emite año, TNOH objetivo y siete características. El Reducer estandariza con 2019–2023, ajusta la regresión y evalúa 2024 y 2025-I.”

**Sergio:** “En 2024 el error absoluto medio fue 3.19 puntos y el modelo explicó 64.35 por ciento de la variación.”

## Diapositiva 18. Q14 / ítem 7b: Regresión Ridge — 30 s

**De dónde sale:** establecimientos, habitaciones y camas están relacionados; esa correlación puede volver inestables los coeficientes lineales.

**Arbués:** “Preguntamos qué TNOH se estima al controlar los coeficientes con regularización L2. El Mapper usa los mismos ejemplos que Q13. El Reducer aplica Ridge con lambda igual a uno y no penaliza el intercepto.”

**Arbués:** “Ridge mejoró ligeramente RMSE y R cuadrado en 2025-I. Solo evaluamos un lambda, por lo que no afirmamos que sea óptimo.”

## Diapositiva 19. Conclusiones — 35 s

**Sergio:** “Las consultas descriptivas respondieron evolución, territorio, clase, capacidad y estacionalidad. Q09 y Q10 mostraron cómo una salida intermedia alimenta un segundo job.”

**Arbués:** “En clasificación, la logística fue más equilibrada. En regresión, Ridge mejoró ligeramente. Los resultados describen asociaciones, no causas.”

**Sergio:** “El semestre incompleto, los promedios no ponderados y el nodo único limitan el alcance de las conclusiones.”

## Diapositiva 20. Referencias y cierre — 10 s

**Ambos:** “MINCETUR respalda las definiciones del dataset y la literatura respalda MapReduce. Podemos ampliar la consulta que indique el docente.”
