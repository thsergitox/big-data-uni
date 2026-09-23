# Guion de exposición listo para leer

Duración objetivo: **menos de 10 minutos**, con lectura continua y sin detenerse en todos los decimales.

## Distribución

- **Sergio:** diapositivas 1 a 10.
- **Arbués:** diapositivas 11 a 20.
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

## Diapositiva 4. Tipos y preguntas

“La práctica define siete grupos. Q01 a Q05 corresponden a 1a hasta 1e. Q06 responde el ítem 2; Q07, el 3; Q08, el 4; Q09 y Q10, 5a y 5b; Q11 y Q12, 6a y 6b; Q13 y Q14, 7a y 7b.”

“El Mapper lee una fila, la filtra y emite una clave y un valor. Hadoop junta todos los valores con la misma clave. El Reducer recibe ese grupo y realiza la operación final.”

## Diapositiva 5. Q01 / ítem 1a: arribos por origen

“La pregunta es cómo evolucionaron los arribos nacionales y extranjeros por año.”

“El Mapper conserva TT/TT y emite `año` como clave y `arribos nacionales, arribos extranjeros` como valor. Hadoop agrupa todos los meses y departamentos con el mismo año.”

“El Reducer recorre el grupo con dos acumuladores: uno suma nacionales y otro extranjeros. Así obtiene ambos totales anuales. La salida evidencia la caída de 2020 y la recuperación posterior. El año 2025 solo contiene seis meses.”

## Diapositiva 6. Q02 / ítem 1b: TNOH territorial

“La pregunta es cuál fue la TNOH media de cada departamento por año.”

“El Mapper conserva TT/TT y emite una clave como `2024;CALLAO`. El valor es `TNOH,1`; el uno sirve para contar observaciones. Hadoop agrupa los meses del mismo departamento y año.”

“El Reducer suma las tasas, suma los contadores y divide ambas cantidades. Callao obtuvo 41.5950 y Loreto 12.9917. Es una media mensual simple, no ponderada por habitaciones.”

## Diapositiva 7. Q03 / ítem 1c: ocupación por clase

“La pregunta es cómo cambian TNOH y TNOC según la clase de hospedaje.”

“El Mapper conserva las filas cuya categoría es TT, porque representan el total de categorías de cada clase, pero excluye la clase TT. Emite `año;clase` como clave y `TNOH,TNOC,1` como valor.”

“Hadoop agrupa la misma clase y año. El Reducer suma ambas tasas y divide entre el contador. En 2024, los resorts obtuvieron TNOH de 46.7742 y TNOC de 48.2812. Las tasas difieren porque una habitación puede alojar varias personas.”

## Diapositiva 8. Q04 / ítem 1d: empleo por capacidad

“La pregunta es cuántos empleos existen por cada cien habitaciones.”

“El Mapper conserva TT/TT y emite `año;departamento` como clave y `empleo,habitaciones` como valor. Hadoop agrupa todos los meses de ese territorio y año.”

“El Reducer suma primero todos los empleos y todas las habitaciones. Después calcula cien por empleo dividido entre habitaciones. No promedia razones mensuales. Lima obtuvo 54.3562 en 2024. Esta razón no mide productividad ni calidad laboral.”

## Diapositiva 9. Q05 / ítem 1e: estacionalidad

“La pregunta es qué meses concentran más arribos y pernoctaciones.”

“El Mapper conserva TT/TT, usa el mes como clave y emite arribos y pernoctaciones como valor. Como el año no forma parte de la clave, Hadoop reúne todos los eneros, febreros y demás meses.”

“El Reducer suma por separado personas llegadas y noches ocupadas. Enero presenta el mayor acumulado. Sin embargo, enero a junio incluyen también 2025, mientras julio a diciembre no.”

## Diapositiva 10. Q06 / ítem 2: medidas estadísticas

“La pregunta es cuál es la cantidad, media, mediana y desviación estándar poblacional de TNOH.”

“El Mapper conserva TT/TT y emite cada tasa bajo una única clave llamada `TNOH`. Por eso, las mil novecientas cincuenta tasas llegan al mismo Reducer.”

“El Reducer calcula media y varianza de forma incremental. También ordena los valores para encontrar la mediana y obtiene la desviación como raíz de la varianza poblacional. La media fue 21.7433 y la desviación 7.0679.”

“Con esto termino mi bloque. Arbués continuará desde Q07.”

---

# BLOQUE DE ARBUÉS

## Diapositiva 11. Q07 / ítem 3: búsqueda de subtexto

“La pregunta es qué registros contienen un texto en clase, categoría o departamento.”

“El Mapper recibe `hotel`, convierte el término y los tres campos a minúsculas y comprueba si alguno contiene el texto. Si coincide, emite la posición de la línea como clave y la fila completa como valor.”

“No hay Reducer porque cada coincidencia ya es el resultado. Se encontraron once mil quinientas veinticinco filas. La búsqueda es literal: no reconoce sinónimos ni elimina diferencias de acentuación.”

## Diapositiva 12. Q08 / ítem 4: máximo y mínimo

“La pregunta es qué departamentos tuvieron la menor y mayor TNOH media de cada año.”

“El Mapper conserva TT/TT y emite el año como clave y `departamento,TNOH` como valor. Hadoop agrupa todos los departamentos y meses del mismo año.”

“El Reducer acumula suma y cantidad por departamento, calcula cada media y después selecciona la menor y la mayor. En 2024 fueron Loreto y Callao. Son dos operaciones dentro de un solo Reducer, no dos jobs.”

## Diapositiva 13. Q09 / ítem 5a: dos MapReduce enlazados

“La pregunta es cuánto cambió la TNOH media entre 2019 y 2024.”

“En el primer job, el Mapper emite `año;departamento` y `TNOH,1`. El primer Reducer calcula cada media anual.”

“La salida intermedia entra al segundo Mapper. Este cambia la clave a departamento y emite `año,media`. Hadoop reúne así los dos años del mismo territorio. El segundo Reducer resta media 2024 menos media 2019. Apurímac tuvo la mayor caída y Ucayali el mayor aumento.”

## Diapositiva 14. Q10 / ítem 5b: dos MapReduce enlazados

“La pregunta es qué porcentaje de arribos fue extranjero en cada departamento y periodo.”

“El primer Mapper calcula cien por arribos extranjeros dividido entre arribos totales y emite departamento, periodo, año y mes. El primer Reducer deja una participación por mes.”

“El segundo Mapper reagrupa por departamento y periodo. Hadoop junta sus porcentajes mensuales y el segundo Reducer calcula la media. Cusco cayó durante 2020 y 2021 y luego se recuperó. Es una media mensual no ponderada.”

## Diapositiva 15. Q11 / ítem 6a: clasificación Naive Bayes

“La pregunta es si la TNOH mensual es alta o baja según otros indicadores.”

“El Mapper convierte cada fila TT/TT en un ejemplo con año, TNOH y siete características de capacidad y actividad. Emite todos los ejemplos bajo la clave `MODEL`.”

“El Reducer usa 2019 a 2023 para entrenamiento, calcula la mediana de TNOH y etiqueta cada caso como alto o bajo. Luego entrena Gaussian Naive Bayes y evalúa 2024 y 2025. Su recall bajo indica que dejó sin detectar muchos casos altos.”

## Diapositiva 16. Q12 / ítem 6b: clasificación logística

“La pregunta es la misma, pero usamos regresión logística para comparar modelos.”

“El Mapper emite exactamente los mismos ejemplos y la clave `MODEL`. El Reducer calcula la estandarización usando solo entrenamiento y ajusta el modelo durante doscientas iteraciones.”

“Después produce matriz de confusión, accuracy, precision, recall, F1 y log loss. En 2024, el F1 aumentó de 0.4403 con Naive Bayes a 0.7931 con regresión logística.”

## Diapositiva 17. Q13 / ítem 7a: regresión lineal

“La pregunta es qué valor continuo de TNOH puede estimarse usando los demás indicadores.”

“El Mapper emite año, TNOH objetivo y las siete características bajo la clave `MODEL`. El Reducer entrena con 2019 a 2023, estandariza las variables y ajusta una regresión sin penalización.”

“Después predice 2024 y 2025 y compara predicción contra valor real para calcular MAE, RMSE y R cuadrado. En 2024, el MAE fue 3.19 puntos y el R cuadrado 0.6435.”

## Diapositiva 18. Q14 / ítem 7b: regresión Ridge

“La pregunta es qué TNOH se estima al controlar los coeficientes con regularización L2.”

“El Mapper utiliza exactamente los mismos ejemplos que Q13. El Reducer también estandariza y ajusta una regresión, pero agrega una penalización con lambda igual a uno sobre los coeficientes; el intercepto no se penaliza.”

“Ridge mejoró ligeramente RMSE y R cuadrado en 2025. Como solo probamos un valor de lambda, no afirmamos que sea el óptimo.”

## Diapositiva 19. Conclusiones

“Las consultas agregaron datos por año, departamento, clase y mes. También describieron la distribución, buscaron registros, calcularon extremos y enlazaron dos MapReduce.”

“En clasificación, la regresión logística fue más equilibrada. En regresión, Ridge produjo una mejora pequeña. Los resultados muestran asociaciones, no causas. Además, 2025 tiene seis meses y el nodo único valida el pipeline, pero no demuestra escalabilidad.”

## Diapositiva 20. Referencias y cierre

“MINCETUR respalda las definiciones del dataset y las referencias metodológicas respaldan MapReduce. Con esto finalizamos y quedamos disponibles para ejecutar la consulta que seleccione el docente.”
