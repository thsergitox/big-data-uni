# Guion completo de exposición

Duración prevista: **9 min 35 s**. Los 25 segundos restantes sirven para abrir la
demostración solicitada por el docente o responder una pregunta breve.

## Diapositiva 1. Portada — 20 s

**Sergio:**

“Buenas tardes. Somos Sergio Pezo y Arbués Pérez. En este trabajo se analizaron
los Indicadores de Ocupabilidad publicados por MINCETUR. Se preparó un dataset
único, se ejecutaron catorce consultas con Hadoop MapReduce y se compararon
modelos de clasificación y regresión. La exposición se concentrará en las
decisiones del análisis y en los resultados que sí se ejecutaron.”

**Transición 1:** “Antes de entrar en los resultados, mostramos brevemente el
recorrido de la exposición.”

## Diapositiva 2. Contenido — 15 s

**Sergio:**

“Este es el recorrido de la exposición. Primero veremos la fuente y la estructura
del dataset. Después explicaremos las decisiones de limpieza y el uso práctico
de Hadoop. Luego agruparemos las consultas según los siete ítems de la práctica
y cerraremos con clasificación, regresión y límites.”

**Transición 2:** “Con el recorrido claro, Arbués explicará de dónde salen los datos
y por qué se eligieron.”

## Diapositiva 3. Fuente y motivo del análisis — 55 s

**Arbués:**

“El dataset proviene de la Plataforma Nacional de Datos Abiertos y lo publica
MINCETUR. Contiene información mensual sobre capacidad y uso de establecimientos
de hospedaje a nivel nacional y regional. No se eligió solo porque estuviera
disponible. El Ministerio de Economía y Finanzas usa estos indicadores como
referencia para estimar demanda en proyectos públicos de turismo.

Al revisar antecedentes se observó que el dataset sí había sido usado, pero
principalmente en tesis regionales y con métodos estadísticos clásicos. No se
encontró un trabajo que aplicara Hadoop o aprendizaje automático directamente a
este CSV. Esa diferencia ayudó a definir el enfoque del laboratorio.”

**Transición 3:** “Con la fuente identificada, el siguiente paso fue revisar qué
contenían realmente los archivos.”

## Diapositiva 4. Dataset consolidado — 55 s

**Arbués:**

“Se trabajó con siete CSV, desde enero de 2019 hasta junio de 2025. Después de la
consolidación se obtuvieron 38 mil 730 registros y 24 columnas para los 25
departamentos. Los campos describen establecimientos, habitaciones, plazas-cama,
ocupabilidad, permanencia, arribos, pernoctaciones y empleo.

Los archivos originales se conservaron intactos. Se produjo una entrada canónica
en UTF-8, con punto decimal y una cabecera común. También se trató 2025 como
primer semestre, porque comparar esos seis meses como si fueran un año completo
daría una conclusión incorrecta.”

**Transición 4:** “La revisión de la fuente no quedó separada del código. Cada
hallazgo produjo una decisión concreta del pipeline.”

## Diapositiva 5. Cómo la investigación guio las decisiones — 70 s

**Arbués:**

“Los antecedentes trabajaban principalmente una sola región. Por eso las
consultas descriptivas compararon los 25 departamentos y distintos periodos.

También se descubrió que el esquema cambia entre 2023 y 2024. Eso obligó a
normalizar nombres y cantidad de columnas antes de cargar el archivo a HDFS.
Otro problema fueron las filas consolidadas mezcladas con las desagregadas. Para
los totales nacionales se usó el filtro TT/TT y así se evitó el doble conteo.

Finalmente, como los trabajos anteriores usaban sobre todo regresiones clásicas,
se decidió comparar dos clasificadores y dos regresores. La evaluación se hizo
de forma temporal: se entrenó con 2019 a 2023 y se evaluó 2024 y 2025-I. Así no
se mezcló información futura dentro del entrenamiento.”

**Transición 5:** “Estas decisiones se ejecutaron sobre un entorno Hadoop que
Sergio explicará desde el punto de vista práctico.”

## Diapositiva 6. Arquitectura Hadoop usada — 55 s

**Sergio:**

“Para el laboratorio se usó Hadoop 3.5.0 en un solo nodo. El entorno se levantó
con Docker Compose y tenía NameNode, DataNode, ResourceManager, NodeManager y
HistoryServer.

HDFS guardó el dataset y las salidas. YARN ejecutó los JAR de MapReduce y
JobHistory conservó la información de los trabajos terminados. Las imágenes
muestran el NameNode con los archivos y el ResourceManager con las aplicaciones
finalizadas. Este entorno demuestra todo el flujo de ejecución, aunque no busca
probar escalabilidad horizontal porque todos los servicios compartieron una
máquina.”

**Transición 6:** “Con el entorno listo, las consultas se organizaron según los
siete ítems pedidos en la práctica.”

## Diapositiva 7. Consultas descriptivas Q01–Q05 — 65 s

**Sergio:**

“Q01 a Q05 corresponden al ítem 1: cinco consultas que usan dos o más campos.

Q01 suma arribos nacionales y extranjeros por año. Q02 calcula TNOH por
departamento y año. Q03 compara TNOH y TNOC según la clase de hospedaje. Q04
relaciona empleo con cada cien habitaciones. Q05 analiza arribos y
pernoctaciones por mes.

El resultado más claro aparece en Q01. Los arribos nacionales bajaron de 54,9
millones en 2019 a 34,6 millones en 2020. Luego se observó recuperación, pero
2025 no se comparó directamente porque solo contiene seis meses.”

**Transición 7:** “Los siguientes cuatro ítems cubren estadísticas, texto,
extremos y consultas MapReduce encadenadas.”

## Diapositiva 8. Estadística y consultas encadenadas Q06–Q10 — 75 s

**Sergio:**

“Q06 corresponde al ítem 2. Calculó la media, mediana y desviación estándar de
TNOH. Se obtuvo una media de 21,74 y una mediana de 21,34.

Q07 corresponde al ítem 3 y busca un subtexto en clase, categoría o
departamento, devolviendo los registros completos. Q08 cubre el ítem 4 y
encuentra los departamentos con mayor y menor TNOH por año.

Q09 y Q10 pertenecen al ítem 5. Cada una enlaza dos trabajos MapReduce. Q09
comparó la ocupabilidad de 2019 y 2024: Apurímac tuvo la mayor caída y Ucayali
la mayor subida. Q10 agrupó la participación extranjera en Cusco y mostró una
caída durante 2020 y 2021, seguida por recuperación.”

**Transición 8:** “Después de responder las preguntas descriptivas, se pasó a
los modelos de clasificación.”

## Diapositiva 9. Clasificación Q11–Q12 — 55 s

**Sergio:**

“Q11 y Q12 corresponden al ítem 6. La etiqueta separa ocupabilidad alta y baja
usando la mediana de TNOH calculada solo con el entrenamiento.

Q11 aplicó Gaussian Naive Bayes y Q12 regresión logística. Para compararlos se
usaron accuracy, precisión, recall, F1 y log loss. La diferencia más clara se
observó en F1. En 2024 la logística obtuvo 0,7931 frente a 0,4403 de Naive
Bayes. En 2025-I volvió a superar al otro modelo. Por eso se consideró el
clasificador más equilibrado en estos datos.”

**Transición 9:** “Arbués cerrará la comparación de modelos con las dos
consultas de regresión.”

## Diapositiva 10. Regresión Q13–Q14 — 55 s

**Arbués:**

“Q13 y Q14 corresponden al ítem 7. Ambas predicen la TNOH con las mismas
variables y la misma división temporal. Q13 usa regresión lineal y Q14 agrega
regularización ridge.

Ridge redujo el RMSE de 4,0920 a 4,0726 en 2024. Para 2025-I bajó de 3,9273 a
3,8796. También mejoró un poco el R cuadrado. La mejora existe, pero es pequeña,
por lo que no se presenta como una diferencia grande. La conclusión correcta es
que ridge tuvo una ligera ventaja manteniendo una evaluación comparable.”

**Transición 10:** “Para terminar, Sergio y yo resumiremos qué se logró y qué
límites deben mantenerse claros.”

## Diapositiva 11. Conclusiones y límites — 45 s

**Arbués:**

“Se logró unificar los siete archivos sin modificar los originales. La
investigación previa permitió detectar el cambio de esquema, el riesgo de doble
conteo y la necesidad de evaluar los modelos por tiempo.”

**Sergio:**

“Las catorce consultas dejaron una salida real y un JAR reproducible. La
regresión logística obtuvo el mejor F1 y ridge mejoró ligeramente el error de
regresión. Como límites, 2025 tiene solo seis meses y el clúster usa un nodo. El
trabajo demuestra el pipeline y las consultas, pero no escalabilidad
horizontal.”

**Transición 11:** “Antes de terminar, dejamos las fuentes principales que
respaldan el dataset, los antecedentes y la arquitectura utilizada.”

## Diapositiva 12. Referencias — 10 s

**Ambos:**

“Esta última diapositiva reúne las referencias principales en formato IEEE. No
las leeremos una por una, pero quedan visibles para que se pueda rastrear la
fuente oficial, los antecedentes regionales y los trabajos que orientaron la
parte de MapReduce y macrodatos.”

“Con esto termina la exposición. Ahora podemos ejecutar la consulta que indique
el docente.”
