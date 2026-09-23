# Guion completo de exposición

Duración prevista: **9 min 35 s**. Cada query se explica con la misma secuencia: Mapper, Reducer y output.

## Diapositiva 1. Portada — 15 s

**Sergio:** “Presentaremos catorce queries Hadoop. En cada una veremos qué transforma el Mapper, qué calcula el Reducer y cuál fue el output real.”

**Transición 1:** “Primero ubicamos los datos y el entorno.”

## Diapositiva 2. Datos y entorno — 35 s

**Arbués:** “Consolidamos 38 mil 730 registros de 2019 a 2025-I. **¿Por qué se tomó esta decisión?** Normalizamos el cambio de esquema, filtramos TT/TT para evitar doble conteo y usamos división temporal para evitar información futura.”

**Sergio:** “HDFS almacena entrada y salidas; YARN ejecuta los jobs.”

**Transición 2:** “Comenzamos con Q01.”

## Diapositiva 3. Q01: Arribos por origen — 30 s

**Sergio:** “El Mapper emite año como clave y arribos nacionales/extranjeros como valor; filtra TT/TT. El Reducer suma ambos orígenes para todas las filas del mismo año. El output mostrado es `2019 national=54897127 foreign=8264767`. **¿Por qué se tomó esta decisión?** Comparar por año revela la caída de 2020 sin mezclar el semestre 2025.”

**Transición 3:** “Continuamos con la siguiente query.”

## Diapositiva 4. Q02: TNOH por departamento — 30 s

**Sergio:** “El Mapper emite año-departamento y el par TNOH,1 para cada fila TT/TT. El Reducer acumula suma y conteo; calcula la TNOH media por territorio. El output mostrado es `2024;CALLAO mean\_tnoh=41.5950 | LORETO=12.9917`. **¿Por qué se tomó esta decisión?** La clave territorial permite comparar departamentos con la misma definición.”

**Transición 4:** “Continuamos con la siguiente query.”

## Diapositiva 5. Q03: TNOH y TNOC por clase — 30 s

**Sergio:** “El Mapper emite año-clase con TNOH, TNOC y contador; usa categoría total y excluye clase TT. El Reducer promedia ambos indicadores para cada clase de hospedaje. El output mostrado es `2024;RESORT mean\_tnoh=46.7742 mean\_tnoc=48.2812`. **¿Por qué se tomó esta decisión?** Separar clases muestra patrones que un total nacional ocultaría.”

**Transición 5:** “Continuamos con la siguiente query.”

## Diapositiva 6. Q04: Empleo por capacidad — 30 s

**Sergio:** “El Mapper emite año-departamento con empleo y habitaciones en filas TT/TT. El Reducer suma ambos campos y calcula 100 × empleo / habitaciones. El output mostrado es `2024;LIMA employment\_per\_100\_rooms=54.3562`. **¿Por qué se tomó esta decisión?** La razón normaliza capacidades distintas y hace comparable cada territorio.”

**Transición 6:** “Continuamos con la siguiente query.”

## Diapositiva 7. Q05: Estacionalidad mensual — 30 s

**Sergio:** “El Mapper emite mes como clave y arribos/pernoctaciones como valores. El Reducer suma ambos indicadores para todas las observaciones del mismo mes. El output mostrado es `01 arrivals=33585654 overnight\_stays=43787718`. **¿Por qué se tomó esta decisión?** Agrupar por mes muestra estacionalidad; enero-junio tiene un año adicional.”

**Transición 7:** “Continuamos con la siguiente query.”

## Diapositiva 8. Q06: Estadística de TNOH — 30 s

**Sergio:** “El Mapper emite todos los valores TNOH TT/TT bajo la clave común TNOH. El Reducer ordena 1 950 valores y calcula media, mediana y desviación poblacional. El output mostrado es `count=1950 mean=21.7433 median=21.3400 stddev=7.0679`. **¿Por qué se tomó esta decisión?** Una clave global sirve aquí porque el volumen es pequeño; no escala a cuantiles masivos.”

**Transición 8:** “Continuamos con la siguiente query.”

## Diapositiva 9. Q07: Búsqueda de subtexto — 30 s

**Sergio:** “El Mapper compara “hotel” con clase, categoría y departamento; emite offset y fila coincidente. El Reducer sin Reducer: cada coincidencia ya constituye el resultado final. El output mostrado es `matches=11525 | 2022;06;HOTEL;1 ESTRELLA;AMAZONAS;...`. **¿Por qué se tomó esta decisión?** Un job map-only evita una etapa de agregación que no aporta valor.”

**Transición 9:** “Continuamos con la siguiente query.”

## Diapositiva 10. Q08: Extremos de TNOH — 30 s

**Sergio:** “El Mapper emite año y el par departamento,TNOH para filas TT/TT. El Reducer calcula medias departamentales dentro del año y selecciona mínimo y máximo. El output mostrado es `2024 min=LORETO:12.9917 max=CALLAO:41.5950`. **¿Por qué se tomó esta decisión?** Resolver medias y extremos juntos evita un segundo job para este dataset.”

**Transición 10:** “Continuamos con la siguiente query.”

## Diapositiva 11. Q09: Cambio 2019–2024 — 30 s

**Sergio:** “El Mapper job 1 emite año-departamento,TNOH; Job 2 reagrupa promedios por departamento. El Reducer job 1 calcula medias; Job 2 une 2019/2024 y resta puntos porcentuales. El output mostrado es `APURIMAC change\_pp=-18.7325 | UCAYALI=5.0442`. **¿Por qué se tomó esta decisión?** Dos jobs separan agregación y comparación temporal, haciendo verificable cada etapa.”

**Transición 11:** “Continuamos con la siguiente query.”

## Diapositiva 12. Q10: Participación extranjera — 30 s

**Arbués:** “El Mapper job 1 emite departamento-mes con arribos; Job 2 cambia la clave a departamento-periodo. El Reducer calcula porcentaje mensual y después su media por periodo analítico. El output mostrado es `CUSCO PRE=63.3747 IMPACT=20.5234 RECOVERY=58.3507`. **¿Por qué se tomó esta decisión?** Los periodos distinguen choque y recuperación; la media no pondera por arribos.”

**Transición 12:** “Continuamos con la siguiente query.”

## Diapositiva 13. Q11: Gaussian Naive Bayes — 30 s

**Sergio:** “El Mapper emite cada ejemplo TT/TT codificado bajo la clave común MODEL. El Reducer separa años, fija la mediana, entrena Gaussian NB y calcula métricas. El output mostrado es `TEST\_2024 accuracy=0.703333 recall=0.286885 f1=0.440252`. **¿Por qué se tomó esta decisión?** Se eligió como línea base probabilística; su bajo recall revela la limitación de independencia. **¿Por qué Gaussian Naive Bayes?** Porque ofrece una línea base probabilística simple y expone el costo de asumir independencia.”

**Transición 13:** “Continuamos con la siguiente query.”

## Diapositiva 14. Q12: Regresión logística — 30 s

**Sergio:** “El Mapper emite las mismas variables y partición temporal bajo la clave MODEL. El Reducer estandariza con entrenamiento, ajusta 200 iteraciones y evalúa probabilidades. El output mostrado es `TEST\_2024 accuracy=0.840000 recall=0.754098 f1=0.793103`. **¿Por qué se tomó esta decisión?** Se eligió por interpretabilidad y mejor equilibrio entre precisión y recall. **¿Por qué regresión logística?** Porque estima la probabilidad de una clase con pesos interpretables y equilibró mejor precisión y recall.”

**Transición 14:** “Continuamos con la siguiente query.”

## Diapositiva 15. Q13: Regresión lineal — 30 s

**Arbués:** “El Mapper emite año, TNOH objetivo y vector de características bajo MODEL. El Reducer entrena con 2019–2023 y calcula MAE, RMSE y R² en años posteriores. El output mostrado es `TEST\_2024 mae=3.192221 rmse=4.092002 r\_squared=0.643537`. **¿Por qué se tomó esta decisión?** Da una referencia interpretable para predecir el valor continuo de TNOH. **¿Por qué regresión lineal?** Porque establece una referencia interpretable para predecir TNOH continuo.”

**Transición 15:** “Continuamos con la siguiente query.”

## Diapositiva 16. Q14: Regresión ridge — 30 s

**Arbués:** “El Mapper emite exactamente los mismos ejemplos que Q13 para una comparación justa. El Reducer ajusta regresión con penalización L2, lambda=1, y calcula las mismas métricas. El output mostrado es `VALIDATION\_2025\_H1 mae=3.013257 rmse=3.879584 r\_squared=0.670320`. **¿Por qué se tomó esta decisión?** Ridge controla coeficientes correlacionados; la mejora observada es pequeña. **¿Por qué ridge?** Porque penaliza coeficientes correlacionados y permite comprobar si mejora la generalización.”

**Transición 16:** “Continuamos con la siguiente query.”

## Diapositiva 17. Conclusiones y límites — 35 s

**Arbués:** “Las queries descriptivas muestran caída, recuperación, estacionalidad y diferencias territoriales.”

**Sergio:** “La logística obtuvo el mejor F1 y ridge una mejora pequeña. El clúster de un nodo valida el pipeline, no escalabilidad horizontal.”

**Transición 17:** “Cerramos con las referencias.”

## Diapositiva 18. Referencias — 10 s

**Ambos:** “Estas fuentes respaldan los datos y MapReduce. Podemos ejecutar la query que indique el docente.”
