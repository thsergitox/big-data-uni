# Tabla comparativa y vacío de investigación

Comparación metodológica de las fuentes y delimitación del aporte del informe.

## 1. Tabla principal

| Fuente (año) | Datos usados | Método | Hallazgo clave |
| --- | --- | --- | --- |
| Taipe & Laura (2021) | Indicadores de ocupabilidad, Huancavelica 2003–2019 | Regresión multivariada con dummies estacionales, MCO, EViews 10 | Estacionalidad en feb/mar/jul/oct; efectos −1 326 / +1 399 / +1 012 / +715 llegadas; sin constante |
| Ñahuincopa & Rojas (2022) | Capacidad hotelera + IPC + PBI per cápita, Huancavelica 2010–2019 | Modelo lineal multivariado | Capacidad hotelera → efecto **limitado**; elasticidad ingreso inelástica; precio con efecto directo |
| Lahura & Sabrera (2023) | Visitas de MINCETUR e INEI, 2008–2018 | Control sintético | Visitas a Kuélap **+100 %** tras el paquete de inversión |
| Bravo et al. (2023) | CSV de la PNDA + Google Trends + TripAdvisor, 1 534 registros, 2011–2022 | Regresión lineal, KNN, árbol de decisión, bosque aleatorio | Gana **regresión lineal** (nacional y extranjero) |
| Serquén et al. (2023) | Datos abiertos del Estado peruano, 2011–2019 | Redes neuronales feedforward + series de tiempo | MSE y MAE mejoran año a año en validación recursiva |
| Bravo-Jaico et al. (2026) | Arribos de MINCETUR, 2003–2023 (Bagua) | Perceptrón multicapa | Dos escenarios: con y sin el año anómalo 2020 |
| Pinedo et al. (2026) | Sistema de Información Estadística de Turismo, 5 destinos + SENAMHI + Google Trends | Seis modelos: Seasonal Naive, SARIMA, SARIMAX, RF, XGBoost, LightGBM | Naive gana en **MAPE** (18,49 % nacional); LightGBM gana en **R²** (0,923) |
| Sánchez-Dávila & García (2026) | 24 departamentos peruanos, 2003–2022 | PARDL (panel autorregresivo de rezagos distribuidos) | El turismo **doméstico** reduce más la pobreza que el receptivo |
| Ccora & Nuñez (2024) | Panel de 924 obs., 7 categorías, 2010–2020 | Panel de datos, efectos aleatorios | **Heterogeneidad inobservable entre categorías**; dominan PBI e IPC |
| Alvarado (2022) | Panel balanceado de 216 obs., 3 segmentos, 2016–2021 | Panel, especificación agrupada | Ocupabilidad y tarifa → rentabilidad, **positivo y significativo** |
| Graciano (2024) | 253 meses de datos abiertos de MINCETUR, 2002–2023 | SARIMA + **filtro de Kalman** para 2020 | SARIMA(2,1,2)(1,1,3)12 con MAPE 10,89 % |
| Viteri & Beltrán (2023) | Datos abiertos de la PNDA (MINSA/CENARES) | **Apache Hadoop** + Microsoft Azure | Solución big data funcional sobre la plataforma peruana |
| Gomez & Urcia (2023) | Datos abiertos de la PNDA (MINSA) | Arquitectura bajo proceso **KDD** | Mejora eficiencia, calidad de registros y agilidad |
| Mariani & Baggio (2022) | — | Revisión sistemática | Datos **administrados** = categoría menos explotada |
| ADB & UNWTO (2021) | — | Informe institucional | Definición de "turismo big data"; apéndice de habilidades |
| FAO & UNWTO (2023) | — | Informe institucional | Recomienda mejorar estadísticas oficiales **con** big data |
| Mendoza et al. (2024) | Manta, Ecuador | Modelo en 3 fases (recolección, ETL, mejora continua) | Escasez de datos abiertos turísticos en Latinoamérica |
| Vásquez (2023) | 31 hospedajes, Chachapoyas | Fisher exacta + V de Cramer | Asociación moderada **sin significancia** |

## 2. Lo que revela la tabla

Al ordenar las fuentes por método aparece un patrón claro: **hay dos comunidades
que no se comunican entre sí.**

### Comunidad A — Econometría peruana

Trabaja la ocupabilidad y el turismo con **paneles y control sintético**, pero:

- sobre series construidas manualmente, no sobre el CSV de la Plataforma Nacional
  de Datos Abiertos;
- en **una sola región** (Huancavelica dos veces, Kuélap, Chachapoyas,
  Ollantaytambo);
- con métodos clásicos (MCO, efectos aleatorios, panel).

### Comunidad B — Informática peruana

Construye **arquitecturas de macrodatos sobre la plataforma nacional**, pero:

- en los dominios de **salud** (MINSA/CENARES) y educación;
- sin tocar datos turísticos.

### El hueco

La analítica predictiva sobre turismo peruano **sí existe** (Bravo et al., 2023;
Serquén et al., 2023; Bravo-Jaico et al., 2026; Pinedo et al., 2026), pero usa el
conjunto de **arribos a sitios turísticos**, no el de **ocupabilidad hotelera**.

Nadie ha cruzado las dos comunidades: nadie aplicó una arquitectura de macrodatos
al dataset de ocupabilidad, ni explotó su desagregación
año × región × clase × categoría.

## 3. El vacío, en tres afirmaciones separables

No se declara un "no encontré nada" en bloque. Se descompone en tres afirmaciones
independientes, cada una con su evidencia.

| # | Afirmación | Veredicto | Evidencia |
| --- | --- | --- | --- |
| 1 | ¿Existe el patrón general (macrodatos y ML sobre turismo peruano)? | **Sí** | Bravo et al. (2023), Serquén et al. (2023), Bravo-Jaico et al. (2026), Pinedo et al. (2026), Graciano (2024) |
| 2 | ¿Existe el método específico aplicado a **este** dataset? | **No** | Todas las anteriores usan el conjunto de arribos a sitios turísticos. La URL del dataset no aparece citada en ningún documento académico |
| 3 | ¿Alguien ha medido lo que este informe mide? | **No** | Las dos únicas investigaciones analíticas usan MCO regional sobre Huancavelica |

## 4. Matriz de evidencia del vacío

| Consulta | Ruta | Resultado |
| --- | --- | --- |
| `"indicadores de ocupabilidad"` | ALICIA / CONCYTEC | 3 resultados, ninguno relevante |
| `"ocupabilidad hotelera"` | ALICIA / CONCYTEC | 2 tesis, ninguna usa el CSV de la PNDA |
| `"indicadores mensuales de ocupabilidad de establecimientos de hospedaje"` | Web abierta | Usos reales, **todos instrumentales** salvo dos |
| URL exacta del dataset | Web abierta | Solo el portal y páginas de `gob.pe` |
| `tasa de ocupabilidad hotelera Peru` | OpenAlex | 2 resultados, ambos pre-2022 |
| `MapReduce` / `Hadoop` + turismo + Perú | Web, OpenAlex | 0 resultados |
| Cualquier consulta | Google Scholar | 0 — **bloqueo del backend, no ausencia** |
| Datatón "Exprésate Perú con Datos 2025" | PCM | 211 conjuntos, 9 finalistas, **0 de turismo** |

## 5. Por qué el vacío es creíble

1. **Se buscó la terminología administrativa, no solo la académica.** Con el nombre
   real del producto aparecieron los usos existentes, lo que confirma que la
   búsqueda funciona y que el vacío es de *tipo de análisis*, no de *existencia del
   dato*.
2. **Se recorrió la cadena de citas**, no solo búsqueda por palabras clave. Se
   recuperaron cuatro artículos peruanos de 2024–2026 no indexados bajo las
   consultas temáticas. Ninguno usa el dataset de ocupabilidad.
3. **Hay evidencia negativa directa.** El texto de la URL del dataset no aparece
   citado en ningún documento académico indexado.

## 6. Cómo formular el aporte

> Este trabajo aplica [técnica de macrodatos] al conjunto "Indicadores de
> Ocupabilidad" de MINCETUR, que la metodología nacional de inversión pública (MEF,
> 2021) reconoce como fuente de referencia pero que la investigación académica ha
> utilizado únicamente como insumo instrumental para dimensionar muestras. Las dos
> investigaciones que lo emplean analíticamente lo hacen sobre una sola región y con
> mínimos cuadrados ordinarios. Ninguna explota su desagregación por año, región,
> clase de establecimiento y categoría, ni documenta las particularidades de su
> esquema —que se modifica entre 2023 y 2024— o la presencia de filas de agregado
> que inducen doble conteo.

## 7. Amenazas al argumento y cómo responderlas

| Amenaza | Respuesta |
| --- | --- |
| "Alguien pudo haberlo hecho en un repositorio no indexado" | Cierto. Riesgo residual declarado: los portales regionales publican en PDF. Ver [límites](01-metodologia.md#6-límites-declarados) |
| "El dataset es muy pequeño para ser big data" | El argumento es variedad, ETL y extensibilidad, no volumen. Ver [hallazgos](02-hallazgos.md#advertencia-esto-no-es-big-data-por-volumen) |
| "Ya existen tesis que usan estos indicadores" | Cierto y documentado. Pero son MCO regional sobre una sola región. La diferencia es el método y el alcance |
| "MINCETUR ya publica estos indicadores agregados" | Cierto. El valor está en el dato desagregado y en el pipeline reproducible. Además, cruzar el resultado propio con la cifra oficial es la validación más barata del pipeline |

## 8. Nivel de evidencia

| Nivel | Fuentes |
| --- | --- |
| **Texto completo leído** | Taipe & Laura (2021), Lahura & Sabrera (2023), Bravo et al. (2023), MEF (2021), Pinedo et al. (2026), Bravo-Jaico et al. (2026), Graciano (2024), Zavaleta et al. (2024), Sánchez-Dávila & García (2026) |
| **Resumen leído** | Ñahuincopa & Rojas (2022), Ccora & Nuñez (2024), Alvarado (2022), Vásquez (2023), Camero (2024), Gutiérrez & Defilippi (2025), Mendoza et al. (2024) |
| **Solo metadatos verificados** | Serquén et al. (2023), Ramirez-Flores (2025), Nugraha et al. (2026), Yáñez et al. (2026), Viteri & Beltrán (2023), Gomez & Urcia (2023) |

No citar como "leído" lo que solo se verificó por metadatos. Si el informe cita una
cifra concreta de una fuente de la tercera fila, hay que leer el texto completo
primero.
