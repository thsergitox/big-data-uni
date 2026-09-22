# Bullets para la presentación

Frases listas para pegar en diapositivas. Cada una dice qué es la fuente, qué
aporta y por qué justifica el uso de este dataset. Entre corchetes va la clave
BibTeX de [`referencias.bib`](referencias.bib).

## 1. Sobre el dataset y su legitimidad

- **El dataset no es una elección arbitraria:** el Ministerio de Economía y
  Finanzas obliga a usar los "Indicadores mensuales de ocupabilidad de
  establecimientos de hospedaje" del MINCETUR como población de referencia para
  estimar demanda en proyectos de inversión pública turística. `[mef_ficha_tecnica_turismo2021]`

- **Fuente oficial y trazable:** MINCETUR publica capacidad y uso de la oferta de
  alojamiento a nivel nacional y regional bajo licencia ODC-BY, con 11 archivos CSV
  desde 2015 y actualización al 4 de agosto de 2026. `[mincetur_ocupabilidad]`

- **Representatividad acreditada:** la encuesta que alimenta el dataset es un censo
  de establecimientos de 5, 4 y 3 estrellas y albergues en Lima y Callao, una
  muestra aleatoria irrestricta en 1 y 2 estrellas, y censo total en el resto de
  regiones. `[inei_rj037_2024]`

- **Cifras oficiales para contrastar:** MINCETUR reporta 567 mil plazas-cama y una
  ocupabilidad de 35,5 % en 2024, y 585 mil plazas-cama con 37,7 % en 2025.
  `[mincetur_informe_hospedaje2024]` `[mincetur_informe_hospedaje2025]`

## 2. Sobre qué se ha hecho ya con estos datos

- **El dataset se usa, pero no se analiza:** decenas de tesis peruanas lo citan
  para dimensionar muestras o estimar demanda de un hotel, no como objeto de
  estudio. `[taipe_laura_desestacionalizacion2021]`

- **Único uso analítico localizado:** una tesis de la UNH construye su base de datos
  directamente con los indicadores de ocupabilidad de Huancavelica (2003–2019) y
  aplica regresión multivariada con dummies estacionales en EViews.
  `[taipe_laura_desestacionalizacion2021]`

- **Segundo uso analítico:** otra tesis de la UNH halla que la capacidad hotelera
  tiene un efecto **limitado** sobre la demanda y que la elasticidad ingreso es
  inelástica en Huancavelica (2010–2019). `[nahuincopa_rojas_demanda2022]`

- **La misma variable ya se estudió con panel:** 924 observaciones en 7 categorías
  de hospedaje revelan heterogeneidad inobservable entre categorías, lo que justifica
  no analizar solo el agregado nacional. `[ccora_ocupabilidad_panel2024]`

- **La ocupabilidad importa económicamente:** un panel de 216 observaciones
  encuentra efecto positivo y significativo de la tasa de ocupabilidad sobre la
  rentabilidad financiera hotelera. `[alvarado_ocupabilidad_rentabilidad2022]`

## 3. Sobre analítica con datos abiertos peruanos

- **Precedente más cercano:** un artículo en *Sustainability* descarga los CSV de la
  Plataforma Nacional de Datos Abiertos, los une con Google Trends y TripAdvisor
  (1 534 registros) y compara cuatro algoritmos de regresión.
  `[bravo_moche_ml2023]`

- **Los datos oficiales peruanos soportan inferencia causal:** un control sintético
  en *Empirical Economics* estima que las visitas a Kuélap se incrementaron ~100 %
  tras la inversión en infraestructura. `[lahura_sabrera_kuelap2023]`

- **Reportar error y explicación a la vez:** en un benchmark 2026 sobre flujos
  turísticos peruanos, el modelo estacional simple gana en MAPE (18,49 %) mientras
  LightGBM gana en R² (0,923). `[pinedo_flujos_peru2026]`

- **Protocolo para datos faltantes:** una tesis de la UNMSM imputa los seis meses
  vacíos de 2020 con filtro de Kalman en lugar de eliminar registros, y obtiene un
  MAPE de 10,89 % con SARIMA. `[graciano_demanda_sarima2024]`

- **Cómo tratar la pandemia:** un artículo de 2026 entrena perceptrones multicapa
  sobre dos décadas de arribos de MINCETUR evaluando dos escenarios, con y sin el año
  anómalo 2020. `[bravo_amazonia_mlp2026]`

## 4. Sobre arquitectura de macrodatos

- **Precedente nacional directo:** una tesis construye una solución con Apache
  Hadoop sobre Microsoft Azure para analizar datos abiertos de MINSA y CENARES desde
  la misma plataforma nacional. `[viteri_hadoop_minisa2023]`

- **Marco de evaluación reutilizable:** otra tesis propone una arquitectura bajo el
  proceso KDD y mide eficiencia de procesamiento, calidad de registros, precisión y
  agilidad de reportería. `[gomez_arquitectura_bigdata2023]`

- **Anclajes técnicos:** el modelo MapReduce `[dean_ghemawat_mapreduce2008]` y el
  motor de procesamiento en memoria Apache Spark `[zaharia_spark2016]`.

- **El contraste regional:** en destinos latinoamericanos la escasez de datos
  abiertos turísticos es el obstáculo principal; en el Perú el dataset existe y es
  abierto. `[mendoza_bigdata_manta2024]`

## 5. Sobre el aporte y el vacío

- **Vacío demostrado:** no se localizó ningún trabajo que aplique macrodatos,
  MapReduce, Hadoop, Spark o aprendizaje automático al conjunto "Indicadores de
  Ocupabilidad" de MINCETUR.

- **Evidencia negativa directa:** la búsqueda del texto exacto de la URL del dataset
  devuelve únicamente el propio portal y páginas de `gob.pe`; cero documentos
  académicos.

- **Dos comunidades que no se hablan:** la econometría peruana trabaja la
  ocupabilidad con paneles sobre una sola región; la informática peruana construye
  arquitecturas de macrodatos sobre la plataforma nacional pero en salud. Nadie ha
  cruzado ambas.

- **Aporte original:** ningún estudio ha explotado la desagregación por año, región,
  clase de establecimiento y categoría, ni ha documentado que el esquema del dataset
  **cambia entre 2023 y 2024** o que el **54 % de las filas son agregados** que
  inducen doble conteo.

- **Cifras del dataset:** 21 347 registros entre 2022 y 2025, 25 departamentos, 8
  clases de establecimiento, 9 categorías y 516 combinaciones posibles.

## 6. Diapositiva de advertencias (si el jurado pregunta)

- **2025 está incompleto:** el dataset solo publica el primer semestre de 2025
  (3 068 filas, frente a ~6 100 de un año completo).

- **No es big data por volumen:** 21 347 registros y ~750 KB por año. La
  justificación es variedad, heterogeneidad de esquema y extensibilidad del pipeline,
  no tamaño.

- **Riesgo de doble conteo:** el 54 % de las filas son agregados marcados
  `TODAS CONSOLIDADAS`; sumar sin filtrar duplica el total nacional.

- **Límite de cobertura declarado:** los portales regionales de turismo publican los
  indicadores en PDF, fuera de repositorios indexados; queda como cobertura
  pendiente.
