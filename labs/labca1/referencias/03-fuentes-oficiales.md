# Fuentes oficiales

El dataset, la normativa que obliga a usarlo y la estadística derivada del propio
Estado peruano. Todas son de Tier 1 o documentación de soporte.

## 1. El dataset

### `mincetur_ocupabilidad`

| Campo | Valor verificado |
| --- | --- |
| Título | Indicadores de Ocupabilidad [Ministerio de Comercio Exterior y Turismo - MINCETUR] |
| Produce los datos | MINCETUR — Dirección General de Investigación y Estudios sobre Turismo y Artesanía (DGIETA) |
| Administra el portal | Plataforma Nacional de Datos Abiertos (PNDA), Presidencia del Consejo de Ministros |
| Identificador CKAN | `dbf6bc9c-cabe-47f7-b052-d11cfdd4d9b0` |
| Publicado | **31 de diciembre de 2021** |
| Última actualización | **4 de agosto de 2026** |
| Licencia | Open Data Commons Attribution (ODC-BY) |
| Etiquetas | Turismo · hospedajes · MINCETUR |

**Descripción oficial:** base de datos con las principales estadísticas e
indicadores de la capacidad y uso de la oferta de alojamiento a nivel nacional y
regional, recolectada mediante la "Estadística Mensual de Turismo para
Establecimientos de Hospedaje", operación estadística dirigida a los
establecimientos de alojamiento temporal.

**Recursos publicados:** 11 CSV (2015 a 2025 Semestre I) más diccionario de datos,
metadatos y ficha técnica.

**Nota de reproducibilidad:** los CSV no se publicaron de golpe. Los de 2015–2019
son de diciembre de 2021; los de 2020–2023 se subieron el 30 de noviembre de 2025;
el de 2024 el 31 de marzo de 2026; y el de 2025-S1 el 23 de abril de 2026. Quien
descargó el dataset antes de noviembre de 2025 **no tenía 2020–2023**.

### Entrada para el `.bib` del informe

`labs/labca1/Plantilla201latex/Bibliog.bib` ya contiene una entrada con la clave
`mincetur_ocupabilidad`. La versión de [`referencias.bib`](referencias.bib) añade
licencia, identificador CKAN y fuente primaria. **Conservar una sola al fusionar.**

```bibtex
@Misc{mincetur_ocupabilidad,
  author       = {{Ministerio de Comercio Exterior y Turismo}},
  title        = {Indicadores de Ocupabilidad [Conjunto de datos]},
  howpublished = {Plataforma Nacional de Datos Abiertos (PNDA), Presidencia del Consejo de Ministros, Per\'u},
  year         = {2021},
  note         = {Publicado el 31 de diciembre de 2021; \'ultima actualizaci\'on del metadato: 4 de agosto de 2026. 11 recursos CSV (2015--2025 S1), diccionario de datos, metadatos y ficha t\'ecnica. Licencia Open Data Commons Attribution (ODC-BY). Identificador CKAN: dbf6bc9c-cabe-47f7-b052-d11cfdd4d9b0. Fuente primaria: Encuesta Mensual de Turismo para Establecimientos de Hospedaje (DGIETA-MINCETUR)},
  url          = {https://www.datosabiertos.gob.pe/dataset/indicadores-de-ocupabilidad-ministerio-de-comercio-exterior-y-turismo-mincetur},
  urldate      = {2026-09-22}
}
```

## 2. La norma que obliga a usar el dataset

### `mef_ficha_tecnica_turismo2021` · Tier 1 · ventana extendida 2021

**Es la fuente más fuerte del informe para justificar el dataset.** El Ministerio
de Economía y Finanzas, en su capacitación oficial sobre la Ficha Técnica
Simplificada para proyectos de inversión pública en servicios turísticos,
**instruye** usar "los Indicadores mensuales de ocupabilidad de establecimientos de
hospedaje de la página web del MINCETUR" como población de referencia para estimar
la demanda efectiva.

Cita textual del documento:

> "En los casos que no se cuente con data histórica de los visitantes al recurso
> turístico, la estimación de la demanda efectiva se realizará a partir de la
> población de referencia (considerando los Indicadores mensuales de ocupabilidad
> de establecimientos de hospedaje de la página web del MINCETUR, para el caso de
> los turistas y de los datos de la población del distrito y para el caso de los
> excursionistas)."

**Por qué importa:** convierte el uso de este dataset en una decisión **normada**,
no arbitraria. Responde de antemano a la pregunta "¿por qué estos datos y no otros?".

<https://www.mef.gob.pe/contenidos/inv_publica/docs/capacitaciones/Capacitacion_2021_11_15.pdf>

## 3. Resoluciones que autorizan la operación estadística

La fuente primaria del dataset es la "Estadística Mensual de Turismo para
Establecimientos de Hospedaje", que MINCETUR ejecuta con autorización anual del
INEI. Esto acredita representatividad y obligatoriedad estadística.

### `inei_rj037_2024` · Tier 2

Define el **marco muestral**, dato clave para la validez del informe:

- Lima Metropolitana, Región Lima y Callao: **censo** de establecimientos de 5, 4 y
  3 estrellas y albergues; **muestra aleatoria irrestricta** para 2 y 1 estrellas y
  no clasificados.
- Resto de regiones: **censo de la totalidad** de establecimientos.
- Plazo de respuesta: 8 días hábiles tras cada mes.

<https://busquedas.elperuano.pe/dispositivo/NL/2263688-1>

### `inei_rj058_2023` · Tier 2

Autoriza la operación para 2023.
<https://www.gob.pe/institucion/inei/normas-legales/3926086-058-2023-inei>

### `inei_rj028_2025` · Tier 2

Autoriza la operación para 2025 — el año que el dataset solo publica como Semestre
I. Útil para argumentar que los datos existen aunque no estén publicados completos.
<https://www.gob.pe/institucion/inei/normas-legales/6441949-028-2025-inei>

## 4. Informes oficiales con las cifras derivadas

Mismo universo, misma encuesta, cifras ya agregadas y validadas por MINCETUR.
Sirven para **contrastar** los resultados propios del análisis.

| Clave | Año de los datos | Cifras clave |
| --- | --- | --- |
| `mincetur_informe_hospedaje2025` | 2025 | 29 mil establecimientos (+2,4 % vs 2024, +20,1 % vs 2019), 585 mil plazas-cama, **TNOC 37,7 %**, 63,1 millones de arribos |
| `mincetur_informe_hospedaje2024` | 2024 | 28 mil establecimientos (+7,1 % vs 2023, +17,3 % vs prepandemia), 567 mil plazas-cama, **TNOC 35,5 %** |
| `mincetur_informe_hospedaje2023` | 2023 | 26 mil establecimientos (+5,6 % vs 2022, +9,6 % vs 2019), 53,0 millones de arribos (83,9 % del prepandemia) |
| `mincetur_compendio2024` | hasta ene-2024 | Arribos, pernoctaciones y capacidad ofertada en establecimientos de hospedaje |
| `mincetur_observatorio_ocupabilidad` | actualizado | Tablero interactivo del mismo conjunto, desagregable por región, categoría y período |

- Informe 2025: <https://www.gob.pe/institucion/mincetur/informes-publicaciones/8133152>
- Informe 2024: <https://www.gob.pe/institucion/mincetur/informes-publicaciones/6844713>
- Informe 2023: <https://www.gob.pe/institucion/mincetur/informes-publicaciones/5671698>
- Observatorio: <https://observatorio.mincetur.gob.pe/dashobservatorio/>

## 5. Orden de citación recomendado en la sección "Datos"

1. `mincetur_ocupabilidad` — qué es, quién lo publica, licencia, fechas.
2. `inei_rj037_2024` — de dónde salen los datos y con qué marco muestral.
3. `mef_ficha_tecnica_turismo2021` — por qué es legítimo y normado usarlos.
4. `mincetur_informe_hospedaje2024` — para contrastar los resultados propios.
5. `mincetur_observatorio_ocupabilidad` — si se menciona visualización interactiva.
