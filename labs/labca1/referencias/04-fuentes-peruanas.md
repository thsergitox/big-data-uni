# Investigaciones peruanas sobre ocupabilidad y turismo-alojamiento

Quién ha usado estos datos y para qué. La distinción entre uso **analítico**,
**instrumental** y **predictivo sobre otros datasets** es el núcleo de esta sección.

## 1. Uso analítico del dataset (solo dos, ambas de la UNH)

### `taipe_laura_desestacionalizacion2021` · Tier 1 · ventana extendida 2021

**Taipe Pari, Hugo & Laura Huamán, Juan Gustavo (2021).** Desestacionalización de la
serie de tiempo llegada mensual de turistas y la oferta de hospedaje en la región
Huancavelica, periodo 2003–2019. Tesis para optar el título profesional de
Economista, Universidad Nacional de Huancavelica.

- **Es la única investigación localizada que construye su base de datos
  directamente con los indicadores de ocupabilidad.** Cita textual: *"se procederá
  a la construcción de una base datos a partir de los indicadores mensuales de
  ocupabilidad de establecimientos de hospedaje colectivo en la región
  Huancavelica; para el periodo 2003 al 2019"*.
- **Método:** regresión lineal multivariada con variables dicotómicas estacionales,
  mínimos cuadrados ordinarios, en EViews 10. La oferta de hospedaje (número de
  habitaciones) es la variable explicativa de la llegada mensual de turistas.
- **Hallazgos:** patrón estacional en febrero, marzo, julio y octubre; efectos
  parciales de **−1 326, +1 399, +1 012 y +715** llegadas respectivamente; sin
  efecto autónomo significativo (la ecuación final no incluye constante).
- <https://repositorio.unh.edu.pe/bitstreams/40f626d7-24d7-4b3a-a55c-e29b7f3cf001/download>

### `nahuincopa_rojas_demanda2022` · Tier 1

**Ñahuincopa Huarancca, Misael & Rojas Romero, Wendy Milinda (2022).** La demanda
turística y la capacidad hotelera en la región Huancavelica, período 2010–2019.
Tesis de grado, Universidad Nacional de Huancavelica.

- **Método:** modelo lineal multivariado con capacidad hotelera, índice de precios
  al consumidor e ingreso per cápita.
- **Hallazgos:** existe un nivel de demanda autónomo o inercial no explicado por los
  regresores; la **capacidad hotelera tiene un efecto limitado** sobre la demanda;
  la elasticidad ingreso es **inelástica**; el nivel de precios tiene efecto
  significativo y directo (los servicios turísticos se comportan como bien
  inferior en la región).
- **Por qué importa:** el resultado "capacidad hotelera → efecto limitado" es
  exactamente la hipótesis que un análisis con el universo nacional puede poner a
  prueba frente a una sola región.
- <https://hdl.handle.net/20.500.14597/5197>

**Rasgos comunes que acotan su alcance:** tesis de pregrado, una sola región,
métodos econométricos clásicos sobre series cortas. Ninguna explota la
desagregación por categoría de hospedaje ni la cobertura nacional.

## 2. Estudios sobre la misma variable

### `ccora_ocupabilidad_panel2024` · Tier 1

**Ccora Huallpa, Katerynne Jenny & Nuñez Vargas, Teófilo (2024).** Determinantes de
la tasa de ocupabilidad hotelera en el Perú: un análisis de datos panel por
categoría de hospedaje para el periodo 2010–2020. Tesis de grado, UNH.

- **Método:** panel de **924 observaciones** = 7 categorías de hospedaje (1 a 5
  estrellas, albergue, no categorizados) × 132 meses. Modelo de efectos aleatorios.
- **Hallazgo clave:** **heterogeneidad inobservable entre categorías de
  hospedaje**. Las variables más significativas son el ingreso disponible y la tasa
  inflacionaria.
- **Uso en el informe:** justifica analizar la dimensión "categoría" que el CSV trae
  y que un análisis agregado a nivel nacional desperdiciaría.
- <https://hdl.handle.net/20.500.14597/9086>

### `alvarado_ocupabilidad_rentabilidad2022` · Tier 1

**Alvarado Anampa, Max Henrry (2022).** La tasa de ocupabilidad hotelera y su efecto
en la rentabilidad financiera en el Perú, 2016–2021. Tesis de maestría, Universidad
César Vallejo.

- **Método:** panel balanceado de **216 observaciones**; tres segmentos de
  alojamiento (Midscale and Economy, Upscale and Upper Midscale, Luxury and Upper
  Upscale).
- **Hallazgo:** efecto **positivo y significativo** de la tasa de ocupabilidad y de
  la tarifa promedio por habitación vendida sobre la rentabilidad financiera. El
  crecimiento del PBI y el IPC resultan no significativos.
- **Uso en el informe:** responde "¿para qué sirve medir esto?" — la ocupabilidad es
  predictor con significancia estadística de resultados económicos.
- <https://hdl.handle.net/20.500.12692/95715>

## 3. Analítica predictiva con datos abiertos peruanos (otro dataset, mismo portal)

Estos trabajos usan la Plataforma Nacional de Datos Abiertos, pero el conjunto de
**arribos a sitios turísticos**, no el de ocupabilidad.

### `bravo_moche_ml2023` · Tier 1

**Bravo, J., Alarcón, R., Valdivia, C. & Serquén, O. (2023).** Application of
Machine Learning Techniques to Predict Visitors to the Tourist Attractions of the
Moche Route in Peru. *Sustainability*, 15(11), 8967.

- **Datos:** CSV de la Plataforma Nacional de Datos Abiertos + Sistema de
  Inteligencia Turística de MINCETUR + Google Trends + TripAdvisor. **1 534
  registros**, enero 2011 – mayo 2022.
- **Método:** cuatro algoritmos — regresión lineal, KNN, árbol de decisión, bosque
  aleatorio.
- **Hallazgo:** la **regresión lineal** predijo mejor tanto turistas nacionales como
  extranjeros.
- **Tratamiento de faltantes:** normalizados con el promedio del mes respectivo,
  **sin eliminar registros**. Es el mismo problema que enfrenta un análisis de 2020
  y 2025-S1.
- **Es el precedente más cercano al caso de este informe.**

### `serquen_moche_nn2023` · Tier 1

**Serquén, O., Alarcón, R., Bravo, J., Valdivia, C. & Aquino, J. (2023).** Prediction
Model of National Visitors to the Moche Route in Peru based on Time Series and
Neural Networks. *CEUR Workshop Proceedings*, vol. 3858, pp. 28–36. Actas del
International Tourism, Hospitality & Gastronomy Congress (ITHGC 2023), Lima.

- **Método:** redes neuronales feedforward sobre datos abiertos del Estado peruano,
  enero 2011 – diciembre 2019.
- **Validación:** proceso predictivo **recursivo**, con entrenamiento de 5 años y
  validación de 4; las métricas MSE y MAE mejoran año a año sobre 2020–2023.
- <https://ceur-ws.org/Vol-3858/paper2.pdf>

### `bravo_amazonia_mlp2026` · Tier 1

**Bravo-Jaico, J., Serquén, O., Alarcón, R., Suarez-Rivadeneira, J. E.,
Ruiz-Camacho, W. & Manayay, F. A. (2026).** Artificial intelligence for biodiversity
and tourism governance: predictive insights from multilayer perceptron models in
Amazonia. *Frontiers in Artificial Intelligence*, 9, 1702544.

- **Datos:** dos décadas (2003–2023) de arribos a la ciudad de Bagua, **obtenidos de
  MINCETUR**, que los autores declaran de libre disponibilidad.
- **Método:** perceptrones multicapa. Evalúa **dos escenarios: con y sin el año
  anómalo 2020**.
- **Uso en el informe:** resuelve la decisión de diseño sobre cómo tratar la
  pandemia.

### `pinedo_flujos_peru2026` · Tier 1

**Pinedo, L., Solano-Lavado, M.-S., Scattolon-Huapaya, L., Fernández-Trujillo, M.-C.
& Umaña-Chacón, F. (2026).** Forecasting tourist flows in Peru's major destinations
to support sustainable management through data-driven approaches. *Frontiers in
Sustainable Tourism*, 5, 1876343.

- **Datos:** base mensual del Sistema de Información Estadística de Turismo de
  MINCETUR para los cinco destinos más visitados, más variables climáticas de
  SENAMHI, Google Trends y eventos.
- **Método:** seis modelos — Seasonal Naive, SARIMA, SARIMAX, Random Forest,
  XGBoost, LightGBM.
- **Hallazgos:** el **Seasonal Naive gana en MAPE** (18,49 % nacional, 12,06 %
  internacional) pero los modelos de aprendizaje automático ganan en **R²**
  (LightGBM 0,923; XGBoost 0,897; Random Forest 0,867; Naive 0,652).
- **Uso en el informe:** el error de pronóstico y el poder explicativo son criterios
  distintos; reportar ambos es más honesto que declarar un ganador único.

### `graciano_demanda_sarima2024` · Tier 1

**Graciano Alvarado, Domenico Giancarlo (2024).** Modelación y proyección de la
demanda de turismo en el Perú. Tesis de pregrado, Universidad Nacional Mayor de San
Marcos.

- **Datos:** datos abiertos de MINCETUR, enero 2002 – julio 2023 (**253
  observaciones**).
- **Método:** metodología Box-Jenkins; **imputación de los seis meses sin registro
  de 2020 con filtro de Kalman**; comparación de cinco modelos SARIMA en R.
- **Hallazgo:** SARIMA(2,1,2)(1,1,3)12 con **MAPE de 10,89 %**.
- **Uso en el informe:** protocolo de datos faltantes por pandemia. Imputar
  explícitamente, no borrar registros.
- <https://cybertesis.unmsm.edu.pe/item/8cc2f828-7b46-4dcb-866d-3262b0a0268d>

## 4. Estudios econométricos sobre el sector turístico peruano

### `lahura_sabrera_kuelap2023` · Tier 1

**Lahura, E. & Sabrera, R. (2023).** The effect of infrastructure investment on
tourism demand: a synthetic control approach for the case of Kuelap, Peru.
*Empirical Economics*, 65(1), 443–478.

- **Datos:** anuales y mensuales de visitantes, de MINCETUR e INEI, 2008–2018. Las
  unidades de control son sitios clasificados por MINCETUR como "sitios
  arqueológicos" y "edificaciones".
- **Método:** control sintético.
- **Hallazgo:** las visitas a Kuélap se incrementaron **aproximadamente 100 %** tras
  el paquete de inversión (primer teleférico del país y remodelación del aeropuerto
  de Jaén).
- **Uso en el informe:** demuestra que los datos oficiales peruanos soportan
  inferencia causal, no solo análisis descriptivos.

### `sanchez_davila_pobreza2026` · Tier 1

**Sánchez-Dávila, E. & García, L. Y. (2026).** Does the origin of tourism matter for
poverty reduction? Evidence from Peruvian's department, 2003–2022. *Journal of
Policy Research in Tourism, Leisure and Events*, 18(2), 329–346.

- **Método:** modelo de panel autorregresivo de rezagos distribuidos (PARDL) sobre
  **24 departamentos peruanos**, 2003–2022.
- **Hallazgo central:** el turismo **doméstico** contribuye más a la reducción de la
  pobreza que el receptivo, por su mayor volumen. El crecimiento económico es la
  variable más influyente; el gasto público, la de menor impacto.
- **Uso en el informe:** el mejor ejemplo reciente de econometría de panel
  subnacional peruano. Cita a Lahura & Sabrera.

## 5. Uso instrumental del dataset (no analítico)

Estos trabajos citan los indicadores de ocupabilidad para dimensionar muestras o
estimar demanda, no para analizarlos. Sirven como evidencia de que el dataset es
conocido pero no explotado.

| Trabajo | Institución | Uso que le da |
| --- | --- | --- |
| Expediente "Mejoramiento de los servicios turísticos públicos de la ruta del café", Jaén | Proyecto de inversión | Demanda actual a partir de los indicadores mensuales de ocupabilidad |
| Tesis sobre calidad de servicio en hoteles de Huanchaco | UPN | Anexo 7: indicadores de ocupabilidad de Huanchaco 2018; población de 2 113 individuos usando plazas-cama y TNOC |
| Tesis sobre pernoctaciones en establecimientos hoteleros | USIL | "Fuente: Indicadores mensuales de ocupabilidad de establecimientos de hospedaje colectivo, MINCETUR, 2019" |
| Proyecto arquitectónico Lamas | UPN | Indicadores de Lamas 2021 y 2023 desde el portal de consulta de MINCETUR |
| Tesis "Nivel de satisfacción del turista nacional… Piura 2017" | UCV | 10 675 turistas que pernoctaron, según los indicadores de 1 a 5 estrellas |
| Plan de Desarrollo Turístico Local, distrito de San Antonio (Moquegua) | Municipalidad | Tabla 23: indicadores de ocupabilidad de San Antonio 2023 |

**Patrón:** casi todos citan la versión regional del portal de consulta de MINCETUR,
no el CSV consolidado de la Plataforma Nacional de Datos Abiertos. Eso refuerza el
argumento de que el conjunto de datos está subexplotado.

## 6. Fuentes complementarias (Tier 2)

| Clave | Aporte |
| --- | --- |
| `zavaleta_kuelap2024` | *Land*: análisis estructural del impacto económico del turismo en Kuélap desde la perspectiva de los prestadores |
| `gutierrez_vuelos2025` | *Journal of Air Transport Management*: la oferta de asientos aéreos determina la llegada de turistas al Perú |
| `vasquez_chachapoyas2023` | 31 hospedajes; prueba exacta de Fisher y V de Cramer; asociación moderada pero **sin significancia estadística** — es el techo del análisis con muestra pequeña |
| `camero_ollantaytambo2024` | Cuantifica el desplome pospandemia a nivel de establecimiento: ingresos de S/ 6 125 a S/ 500 mensuales en hoteles; 4 despidos promedio en 2020 |
