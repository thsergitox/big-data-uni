# Guion de exposición listo para leer

Esta es la **versión ampliada de estudio y exposición**. Su lectura íntegra supera
los 10 minutos de la práctica; por eso debe ensayarse y recortarse según el
tiempo disponible. Las explicaciones más importantes para conservar son Q09,
Q10 y las comparaciones de clasificación y regresión.

## Distribución

- **Sergio:** diapositivas 1 a 11.
- **Arbués:** diapositivas 12 a 25.
- Sergio termina completamente su bloque y luego entrega la exposición a Arbués. No se intercalan.

---

# BLOQUE DE SERGIO

## Diapositiva 1. Portada

“Buenos días. Analizamos los indicadores mensuales de ocupabilidad de MINCETUR mediante catorce consultas Hadoop. Explicaremos la pregunta, lo que emite el Mapper, cómo se agrupan los datos y la operación del Reducer.”

## Diapositiva 2. Contenido

“Primero explicaré el dataset y las consultas hasta Q06. Después, Arbués continuará desde Q07 hasta los modelos y conclusiones.”

## Diapositiva 3. Cómo leer el dataset y qué significa TT/TT

“Una fila no representa un hotel. Resume un mes, un departamento, una clase y una categoría.”

“`ID_CLASE` e `ID_CATEGORIA` contienen códigos. Por ejemplo, clase `01` significa HOTEL y categoría `01` significa una estrella.”

“El código `TT` sí viene en el dataset y significa TODAS CONSOLIDADAS. Cuando decimos `TT/TT`, se lee ‘te te, barra, te te’. No es una columna nueva. Es nuestra abreviatura para dos condiciones: `ID_CLASE` igual a `TT` e `ID_CATEGORIA` igual a `TT`.”

“HOTEL con una estrella contiene solo hoteles de una estrella. HOTEL con categoría TT reúne todas las categorías de hotel. Finalmente, TT con TT reúne todas las clases y categorías del departamento en ese mes.”

“Usamos TT/TT para obtener un único total departamental. Si además sumáramos hoteles, hostales y sus categorías, contaríamos varias veces la misma actividad.”

“TNOH es ocupación de habitaciones y TNOC es ocupación de plazas-cama. Un arribo cuenta al huésped cuando llega; una pernoctación cuenta cada noche.”

## Diapositiva 4. Diccionario de las 24 columnas

“Esta tabla organiza las veinticuatro columnas en dos bloques. Primero aparecen los campos de identificación y capacidad: fecha de corte, año, mes, clase, categoría, departamento, UBIGEO, establecimientos, habitaciones y plazas-cama.”

“Después aparecen los indicadores. TNOH es el porcentaje de habitaciones ocupadas y TNOC es el porcentaje de plazas-cama ocupadas. Las permanencias indican cuántos días permanece en promedio cada huésped.”

“Los arribos cuentan personas cuando ingresan al hospedaje. Las pernoctaciones cuentan noches; por eso una persona puede generar un arribo y varias pernoctaciones. Los campos terminados en NAC corresponden a nacionales y los terminados en EXT a extranjeros. Finalmente, TOTAL_EMPLEO contiene las personas empleadas durante el mes.”

“Esta distinción es importante: TNOH y TNOC son porcentajes; establecimientos, habitaciones, arribos, pernoctaciones y empleo son cantidades.”

## Diapositiva 5. Tipos y preguntas

“La práctica define siete grupos. Q01 a Q05 corresponden a 1a hasta 1e. Q06 responde el ítem 2; Q07, el 3; Q08, el 4; Q09 y Q10, 5a y 5b; Q11 y Q12, 6a y 6b; Q13 y Q14, 7a y 7b.”

“El Mapper lee una fila, la filtra y emite una clave y un valor. Hadoop junta todos los valores con la misma clave. El Reducer recibe ese grupo y realiza la operación final.”

## Diapositiva 6. Q01 / ítem 1a: arribos por origen

“La pregunta es cómo evolucionaron los arribos nacionales y extranjeros por año.”

“El Mapper conserva TT/TT y emite `año` como clave y `arribos nacionales, arribos extranjeros` como valor. Hadoop agrupa todos los meses y departamentos con el mismo año.”

“El Reducer recorre el grupo con dos acumuladores: uno suma nacionales y otro extranjeros. Así obtiene ambos totales anuales. La salida evidencia la caída de 2020 y la recuperación posterior. El año 2025 solo contiene seis meses.”

## Diapositiva 7. Q02 / ítem 1b: TNOH territorial

“La pregunta es cuál fue la TNOH media de cada departamento por año.”

“El Mapper conserva TT/TT y emite una clave como `2024;CALLAO`. El valor es `TNOH,1`; el uno sirve para contar observaciones. Hadoop agrupa los meses del mismo departamento y año.”

“El Reducer suma las tasas, suma los contadores y divide ambas cantidades. Callao obtuvo 41.5950 y Loreto 12.9917. Es una media mensual simple, no ponderada por habitaciones.”

## Diapositiva 8. Q03 / ítem 1c: ocupación por clase

“La pregunta es cómo cambian TNOH y TNOC según la clase de hospedaje.”

“El Mapper conserva las filas cuya categoría es TT, porque representan el total de categorías de cada clase, pero excluye la clase TT. Emite `año;clase` como clave y `TNOH,TNOC,1` como valor.”

“Hadoop agrupa la misma clase y año. El Reducer suma ambas tasas y divide entre el contador. En 2024, los resorts obtuvieron TNOH de 46.7742 y TNOC de 48.2812. Las tasas difieren porque una habitación puede alojar varias personas.”

## Diapositiva 9. Q04 / ítem 1d: empleo por capacidad

“La pregunta es cuántos empleos existen por cada cien habitaciones.”

“El Mapper conserva TT/TT y emite `año;departamento` como clave y `empleo,habitaciones` como valor. Hadoop agrupa todos los meses de ese territorio y año.”

“El Reducer suma primero todos los empleos y todas las habitaciones. Después calcula cien por empleo dividido entre habitaciones. No promedia razones mensuales. Lima obtuvo 54.3562 en 2024. Esta razón no mide productividad ni calidad laboral.”

## Diapositiva 10. Q05 / ítem 1e: estacionalidad

“La pregunta es qué meses concentran más arribos y pernoctaciones.”

“El Mapper conserva TT/TT, usa el mes como clave y emite arribos y pernoctaciones como valor. Como el año no forma parte de la clave, Hadoop reúne todos los eneros, febreros y demás meses.”

“El Reducer suma por separado personas llegadas y noches ocupadas. Enero presenta el mayor acumulado. Sin embargo, enero a junio incluyen también 2025, mientras julio a diciembre no.”

## Diapositiva 11. Q06 / ítem 2: medidas estadísticas

“La pregunta es cuál es la cantidad, media, mediana y desviación estándar poblacional de TNOH.”

“El Mapper conserva TT/TT y emite cada tasa bajo una única clave llamada `TNOH`. Por eso, las mil novecientas cincuenta tasas llegan al mismo Reducer.”

“El Reducer calcula media y varianza de forma incremental. También ordena los valores para encontrar la mediana y obtiene la desviación como raíz de la varianza poblacional. La media fue 21.7433 y la desviación 7.0679.”

“Con esto termino mi bloque. Arbués continuará desde Q07.”

---

# BLOQUE DE ARBUÉS

## Diapositiva 12. Q07 / ítem 3: búsqueda de subtexto

“La pregunta es qué registros contienen un texto en clase, categoría o departamento.”

“El Mapper recibe `hotel`, convierte el término y los tres campos a minúsculas y comprueba si alguno contiene el texto. Si coincide, emite la posición de la línea como clave y la fila completa como valor.”

“No hay Reducer porque cada coincidencia ya es el resultado. Se encontraron once mil quinientas veinticinco filas. La búsqueda es literal: no reconoce sinónimos ni elimina diferencias de acentuación.”

## Diapositiva 13. Q08 / ítem 4: máximo y mínimo

“La pregunta es qué departamentos tuvieron la menor y mayor TNOH media de cada año.”

“El Mapper conserva TT/TT y emite el año como clave y `departamento,TNOH` como valor. Hadoop agrupa todos los departamentos y meses del mismo año.”

“El Reducer acumula suma y cantidad por departamento, calcula cada media y después selecciona la menor y la mayor. En 2024 fueron Loreto y Callao. Son dos operaciones dentro de un solo Reducer, no dos jobs.”

## Diapositiva 14. Q09 / ítem 5a: dos MapReduce enlazados

“La pregunta es cuánto cambió la TNOH media entre 2019 y 2024.”

“En el primer job usamos la notación clave hacia valor. El Mapper emite `(año;departamento) hacia (TNOH, 1)`. Hadoop agrupa los meses con la misma clave. El primer Reducer suma las tasas, suma los contadores y produce `(año;departamento) hacia (media de TNOH)`. Esta es la salida completa del primer MapReduce.”

“El segundo Mapper lee exactamente esa salida. Separa el año del departamento y transforma `(año;departamento) hacia (media)` en `(departamento) hacia (año, media)`. Hadoop puede reunir así 2019 y 2024 bajo el mismo departamento. El segundo Reducer resta media 2024 menos media 2019 y emite `(departamento) hacia (valor 2019, valor 2024, cambio en puntos)`. Apurímac tuvo la mayor caída y Ucayali el mayor aumento.”

## Diapositiva 15. Q10 / ítem 5b: dos MapReduce enlazados

“La pregunta es qué porcentaje de arribos fue extranjero en cada departamento y periodo.”

“El primer Mapper calcula cien por arribos extranjeros dividido entre arribos totales. Emite `(departamento;periodo;año;mes) hacia (cuota extranjera, 1)`. El primer Reducer consolida el mes y deja como salida `(departamento;periodo) hacia (cuota mensual)`. Ese porcentaje mensual es el output del primer MapReduce.”

“El segundo Mapper lee esa salida y emite `(departamento;periodo) hacia (cuota mensual, 1)`. Hadoop junta todos los meses del periodo. El segundo Reducer suma las cuotas, cuenta los meses y emite `(departamento;periodo) hacia (participación extranjera media)`. Cusco cayó durante 2020 y 2021 y luego se recuperó. Es una media mensual no ponderada.”

## Diapositiva 16. Feature Vector / ítem 6: clasificación

“Antes de entrenar los clasificadores, este es el vector de características que representa cada fila TT/TT. Contiene exactamente siete entradas: mes, número de establecimientos, número de habitaciones, número de plazas-cama, arribos por habitación, pernoctaciones por cama y empleo por establecimiento.”

“Las tres últimas no llegan directamente como columnas nuevas. Arribos por habitación se obtiene dividiendo `TOTAL_ARRIBOS` entre `NUMERO_HABITACIONES`; pernoctaciones por cama divide `TOTAL_PERNOCT` entre `NUMERO_PLAZAS_CAMA`; y empleo por establecimiento divide `TOTAL_EMPLEO` entre `NUMERO_ESTABLECIMIENTOS`.”

“El CSV no contiene una categoría alta o baja. Contiene la TNOH numérica. Nosotros construimos la etiqueta comparándola con la mediana del entrenamiento: desde 21.71 por ciento se considera alta y por debajo se considera baja. Esta definición es relativa a la muestra de entrenamiento, no un límite oficial del sector.”

“El año únicamente separa entrenamiento, prueba y validación. Departamento, clase y categoría no entran al modelo; clase y categoría solo permiten seleccionar las filas consolidadas TT/TT. Naive Bayes utiliza el vector numérico y la regresión logística estandariza sus siete valores usando únicamente las estadísticas del entrenamiento.”

## Diapositiva 17. Q11 / ítem 6a: clasificación Naive Bayes

“La pregunta es si la TNOH mensual departamental es alta o baja según los demás indicadores.”

“Primero convertimos un valor continuo en dos clases. Con las observaciones de entrenamiento, ordenamos la TNOH y calculamos su mediana, que fue 21.71. Una fila con TNOH igual o superior se etiqueta como alta; una fila por debajo se etiqueta como baja. TNOH y TNOC no se usan como características, porque revelarían directamente la respuesta.”

“El Mapper conserva filas TT/TT y construye cada ejemplo con el año, la TNOH objetivo y siete características: mes, establecimientos, habitaciones, camas, arribos por habitación, pernoctaciones por cama y empleo por establecimiento. Todos los ejemplos se emiten bajo la clave `MODEL`; por eso llegan al mismo Reducer.”

“El Reducer separa 2019 a 2023 para entrenamiento, 2024 para prueba y 2025-I para validación reciente. Gaussian Naive Bayes calcula, para cada clase, su probabilidad previa y la media y varianza de cada característica. Luego combina esas probabilidades para estimar si un nuevo registro pertenece a ocupación alta o baja.”

“El accuracy de 2024 fue 0.7033, pero el recall fue 0.2869. Esto significa que el porcentaje total de aciertos parece aceptable, pero el modelo solo detectó aproximadamente 29 de cada 100 casos realmente altos. Su precision fue 0.9459: cuando afirmó que la ocupación era alta, casi siempre acertó. Por tanto, es un modelo conservador: produce pocas alertas altas y deja escapar muchas.”

## Diapositiva 18. Q12 / ítem 6b: clasificación logística

“La pregunta es exactamente la misma. Mantuvimos los mismos ejemplos, características, etiqueta y periodos para que la comparación dependa del modelo y no de un cambio en los datos.”

“El Mapper realiza la misma codificación que Q11 y vuelve a emitir todo bajo la clave `MODEL`.”

“El Reducer calcula la media y desviación de cada característica usando únicamente 2019 a 2023. Después estandariza los datos: resta la media y divide entre la desviación. Los valores de 2024 y 2025 se transforman usando esos mismos parámetros, evitando utilizar información futura.”

“La regresión logística forma una combinación ponderada de las siete características y la convierte en una probabilidad mediante la función sigmoide. Los pesos se ajustan durante doscientas iteraciones con tasa de aprendizaje 0.1. Finalmente calculamos matriz de confusión, accuracy, precision, recall, F1 y log loss.”

“En 2024 el accuracy subió a 0.8400, el recall a 0.7541 y el F1 a 0.7931. Ahora se detectan aproximadamente 75 de cada 100 casos altos. La precision baja ligeramente frente a Naive Bayes, pero el equilibrio general mejora de forma clara.”

## Diapositiva 19. Comparación de clasificación

“Esta tabla permite interpretar conjuntamente los modelos. Accuracy es la proporción total de predicciones correctas. Precision indica, entre los casos predichos como altos, cuántos eran realmente altos. Recall indica, entre todos los casos realmente altos, cuántos fueron detectados. F1 combina precision y recall. Log loss evalúa la calidad de las probabilidades; cuanto menor, mejor.”

“Naive Bayes tiene precision de 0.9459 en 2024 y 1.0000 en 2025-I, pero recall de solamente 0.2869 y 0.3051. Esto confirma que casi nunca se equivoca cuando anuncia un caso alto, pero anuncia muy pocos.”

“La logística obtiene F1 de 0.7931 en 2024 frente a 0.4403 de Naive Bayes. En 2025-I obtiene 0.6939 frente a 0.4675. También reduce el log loss de 0.9470 a 0.4988 en 2024 y de 0.8545 a 0.4748 en 2025-I.”

“Por eso seleccionamos la regresión logística como mejor clasificador general. Su recall disminuye en 2025-I, así que el rendimiento reciente es más débil que en 2024; la tabla muestra ese cambio, pero no demuestra su causa.”

## Diapositiva 20. Feature Vector / ítem 7: regresión

“Las consultas trece y catorce utilizan exactamente las mismas siete entradas que los clasificadores: mes, establecimientos, habitaciones, plazas-cama, arribos por habitación, pernoctaciones por cama y empleo por establecimiento. La diferencia es que ahora la salida esperada no es alta o baja; es directamente el porcentaje numérico de TNOH.”

“Antes de entrenar, cada característica se estandariza restando su media y dividiéndola entre su desviación estándar. Tanto la media como la desviación se calculan exclusivamente con 2019 a 2023. Los registros de 2024 y 2025 se transforman posteriormente con esos mismos valores para no utilizar información futura.”

“No convertimos departamento, clase ni categoría mediante one-hot encoding porque no forman parte del vector. Clase y categoría solo se usan para filtrar TT/TT. El mes sí entra como número de uno a doce. No se aplicó codificación one-hot ni cíclica al mes, por lo que el modelo interpreta ese valor de forma numérica; esta es una limitación que debemos reconocer.”

“Los dos regresores reciben el mismo vector y la misma TNOH objetivo. Q13 utiliza regresión lineal con lambda igual a cero. Q14 utiliza Ridge con lambda igual a uno para limitar coeficientes demasiado grandes. Así, cualquier diferencia en los resultados se debe a la regularización y no a un cambio de variables.”

## Diapositiva 21. Q13 / ítem 7a: regresión lineal

“La clasificación anterior responde alta o baja, pero pierde la magnitud exacta. Ahora preguntamos qué valor continuo de TNOH puede estimarse usando los demás indicadores.”

“El Mapper conserva filas TT/TT y emite año, TNOH objetivo y las mismas siete características bajo la clave `MODEL`. Como todos los ejemplos comparten esa clave, el Reducer recibe el conjunto completo.”

“El Reducer usa 2019 a 2023 para entrenamiento y calcula la estandarización solo con esos años. Después ajusta una regresión lineal sin penalización, equivalente a lambda igual a cero. El modelo aprende un intercepto y un coeficiente para cada característica.”

“Para evaluar, genera una TNOH estimada para cada fila de 2024 y 2025-I y la compara con la real. MAE es el error absoluto promedio. RMSE aumenta el peso de errores grandes. R cuadrado indica qué proporción de la variación logra explicar el modelo.”

“En 2024, MAE fue 3.1922 puntos, RMSE 4.0920 y R cuadrado 0.6435. En términos prácticos, la predicción se apartó alrededor de tres puntos porcentuales en promedio y explicó aproximadamente 64 por ciento de la variación observada.”

## Diapositiva 22. Q14 / ítem 7b: regresión Ridge

“La pregunta es qué TNOH se estima al controlar el tamaño de los coeficientes mediante regularización L2.”

“El Mapper es deliberadamente idéntico al de Q13: mismos registros, objetivo, características y periodos. Así aislamos el efecto de la regularización.”

“El Reducer vuelve a estandarizar y resolver la regresión, pero ahora utiliza lambda igual a uno. Ridge añade una penalización cuadrática sobre los coeficientes. En la implementación, lambda se agrega a la diagonal correspondiente a las características, pero no al intercepto.”

“Esta penalización evita que una variable obtenga un coeficiente excesivamente grande cuando características como establecimientos, habitaciones y camas contienen información relacionada. No elimina variables; reduce la magnitud de sus coeficientes.”

“En 2025-I, Ridge obtuvo MAE de 3.0133, RMSE de 3.8796 y R cuadrado de 0.6703. La mejora existe, pero es pequeña y solamente se probó lambda igual a uno.”

## Diapositiva 23. Comparación de regresión

“En esta tabla, un MAE o RMSE menor es mejor, mientras que un R cuadrado mayor es mejor.”

“En 2024, el modelo lineal tiene un MAE apenas menor: 3.1922 frente a 3.1943. Sin embargo, Ridge reduce RMSE de 4.0920 a 4.0726 y eleva R cuadrado de 0.6435 a 0.6469. Esto indica una reducción pequeña de los errores grandes.”

“En 2025-I, Ridge mejora las tres métricas: MAE baja de 3.0208 a 3.0133, RMSE baja de 3.9273 a 3.8796 y R cuadrado sube de 0.6622 a 0.6703.”

“La conclusión correcta no es que Ridge sea muy superior. La diferencia es modesta. Podemos afirmar que la regularización produjo una mejora ligera y consistente en RMSE y R cuadrado, especialmente en la validación reciente. Para elegir un lambda óptimo tendríamos que comparar varios valores usando únicamente datos de entrenamiento.”

## Diapositiva 24. Conclusiones

“Las consultas agregaron datos por año, departamento, clase y mes. También describieron la distribución, buscaron registros, calcularon extremos y enlazaron dos MapReduce.”

“En clasificación, la regresión logística detectó muchos más casos altos y obtuvo mejor F1 y menor log loss que Naive Bayes. En regresión, Ridge redujo ligeramente los errores grandes y elevó R cuadrado frente al modelo lineal.”

“Los resultados muestran asociaciones, no causas. Además, 2025 contiene seis meses, algunos promedios no están ponderados y los modelos se entrenan en un Reducer porque el conjunto TT/TT es pequeño.”

## Diapositiva 25. Referencias y cierre

“MINCETUR respalda las definiciones del dataset y las referencias metodológicas respaldan MapReduce. Con esto finalizamos y quedamos disponibles para ejecutar la consulta que seleccione el docente.”
