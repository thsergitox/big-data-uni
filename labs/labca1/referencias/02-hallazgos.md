# Hallazgos sobre el dataset

Lo que apareció al **descargar y parsear los CSV**, no al leer documentación. Nada
de esto está publicado: son hallazgos propios, reproducibles con
`herramientas/perfilar_dataset.py`.

> Reproducir: `python3 herramientas/descargar_dataset.py && python3 herramientas/perfilar_dataset.py`

## 1. Corrección importante: el dataset sí se usa

La suposición inicial era que nadie usaba este conjunto de datos. **Es falsa.** Sí
se usa, de tres maneras, y solo una es analítica.

| Forma | Quién | Qué hace con él |
| --- | --- | --- |
| **Normada** | El Ministerio de Economía y Finanzas | Obliga a usarlo como población de referencia para estimar demanda en proyectos de inversión pública turística |
| **Instrumental** | Tesis de UNH, UPN, USIL, UCV, PUCP; expedientes de inversión; planes de desarrollo turístico local | Dimensionar muestras, estimar demanda de un hotel |
| **Analítica** | Solo dos tesis de pregrado de la UNH (Huancavelica) | Regresión lineal multivariada y mínimos cuadrados ordinarios |

**Por qué esto fortalece el informe y no lo debilita:** el dataset tiene respaldo
normativo y usos previos, así que usarlo no requiere justificación. Lo que no
tiene es análisis de macrodatos. Ver
[el vacío de investigación](06-tabla-comparativa.md#lo-que-revela-la-tabla).

**Por qué se me escapó al principio:** buscaba "ocupabilidad hotelera". El nombre
real del producto dentro del Estado es "indicadores mensuales de ocupabilidad de
establecimientos de hospedaje colectivo". Lección registrada en la
[metodología](01-metodologia.md#familia-a--terminología-administrativa-la-que-funcionó).

## 2. El esquema cambia entre 2023 y 2024

Ninguna fuente lo documenta. Son **dos formatos distintos**, no una evolución
menor.

### 2015–2023 · 23 columnas · encabezados legibles, sin identificadores

```text
AÑO; MES; CLASE; DESCRIPCIÓN CLASE; CATEGORÍA; DESCRIPCIÓN CATEGORÍA;
DEPARTAMENTO; UBIGEO; NÚMERO DE ESTABLECIMIENTO; NÚMERO DE HABITACIONES;
NÚMERO DE PLAZAS-CAMA; TNOH EN EL MES(%); TNOC EN EL MES(%);
PROM PERMANENCIA(DÍAS); PROM PERMANENCIA - NAC (DÍAS); PROM PERMANENCIA - EXT (DÍAS);
TOTAL DE ARRIBOS EN EL MES; TOTAL DE ARRIBOS MES - NAC; TOTAL DE ARRIBOS MES - EXT;
TOTAL PERNOCT MES; TOTAL PERNOCT MES - NAC (DÍAS); TOTAL PERNOCT MES - EXT (DÍAS);
TOTAL EMPLEO EN EL MES
```

### 2024–2025 · 24 columnas · encabezados técnicos, con identificadores

```text
FECHA_CORTE; ANIO; MES; ID_CLASE; CLASE; ID_CATEGORIA; CATEGORIA;
DEPARTAMENTO; ID_UBIGEO; NUMERO_ESTABLECIMIENTOS; NUMERO_HABITACIONES;
NUMERO_PLAZAS_CAMA; PORCENTAJE_TNOH; PORCENTAJE_TNOC; PROMEDIO_PERMANENCIA;
PROMEDIO_PERMANENCIA_NAC; PROMEDIO_PERMANENCIA_EXT; TOTAL_ARRIBOS;
TOTAL_ARRIBOS_NAC; TOTAL_ARRIBOS_EXT; TOTAL_PERNOCT; TOTAL_PERNOCT_NAC;
TOTAL_PERNOCT_EXT; TOTAL_EMPLEO
```

**Solo tres columnas conservan el nombre idéntico:** `MES`, `CLASE` y
`DEPARTAMENTO`. El resto cambia de nombre, aparecen identificadores nuevos
(`ID_CLASE`, `ID_CATEGORIA`, `ID_UBIGEO`, `FECHA_CORTE`) y desaparecen las
columnas de descripción.

**Consecuencia:** un análisis de 2022–2025 cruza el corte y **exige una etapa
explícita de normalización de esquema**. Es exactamente el trabajo de ETL que un
informe de macrodatos debe documentar.

## 3. El 54 % de las filas son agregados (doble conteo)

Las columnas `CLASE` y `CATEGORIA` contienen el valor literal
`TODAS CONSOLIDADAS` en las filas de agregado, mezcladas con las desagregadas.

| Año | Filas agregadas | Total | % |
| --- | ---: | ---: | ---: |
| 2022 | 3 262 | 6 017 | 54,2 % |
| 2023 | 3 316 | 6 111 | 54,3 % |
| 2024 | 3 348 | 6 151 | 54,4 % |
| 2025 (S1) | 1 674 | 3 068 | 54,6 % |

Sumar una columna de totales sin filtrar **duplica el resultado nacional**.

Ejemplo verificado (Amazonas, enero 2024, fila agregada):

| Campo | Valor |
| --- | --- |
| CLASE | NO CLASIFICADO |
| CATEGORIA | TODAS CONSOLIDADAS |
| NUMERO_PLAZAS_CAMA | 4 530 |
| PORCENTAJE_TNOC | 9,36 |
| TOTAL_ARRIBOS | 10 143 |

```python
# Análisis desagregado (recomendado para explotar la variedad)
df = df[(df.CLASE != "TODAS CONSOLIDADAS") & (df.CATEGORIA != "TODAS CONSOLIDADAS")]

# NUNCA sin filtrar:
# df.groupby("DEPARTAMENTO").NUMERO_PLAZAS_CAMA.sum()
```

La decisión de qué filtro usar es del autor del informe y debe quedar documentada.

## 4. Trampas de parseo

| Detalle | Valor real | Qué rompe |
| --- | --- | --- |
| Separador de campos | `;` | `pd.read_csv` por defecto usa coma |
| Codificación | UTF-8 **con BOM** | Requiere `encoding="utf-8-sig"` |
| Decimales | **coma** (`9,36`) | Las columnas numéricas llegan como texto |
| Año 2025 | solo enero–junio | 3 068 filas en lugar de ~6 100 |
| `TOTAL_EMPLEO` | solo desde 2024 | Rompe una concatenación ingenua |
| Descarga | exige `User-Agent` | La petición falla en silencio |

## 5. Volumen y variedad reales

| Métrica | Valor |
| --- | --- |
| Registros 2022–2025 | **21 347** |
| Registros 2015–2025 completo | 61 890 |
| Departamentos | 25 |
| Clases de establecimiento | 8 |
| Categorías | 9 |
| Combinaciones departamento × clase × categoría (2024) | **516** |
| Peso por año | ~750 KB |

### Advertencia: esto no es big data por volumen

21 347 registros y 750 KB por año **no justifican MapReduce por tamaño**. La
justificación honesta es otra:

1. **Variedad**, no volumen: 516 combinaciones sobre 25 departamentos y 8 clases.
2. **Heterogeneidad de esquema**: el cambio 2023→2024 es un problema real de ETL.
3. **Suciedad estructural**: filas agregadas, separador no estándar, decimales con
   coma, año incompleto.
4. **Extensibilidad**: el pipeline debe funcionar igual sobre los 61 890 registros
   completos y sobre los datasets hermanos de MINCETUR (arribos, turismo receptivo
   y emisivo, movimiento de pasajeros).

Redacción sugerida para la metodología del informe:

> El conjunto analizado no constituye un caso de macrodatos por volumen —21 347
> registros en el período 2022-2025—, sino por variedad y complejidad de
> integración. La estructura del dato combina 25 departamentos, 8 clases de
> establecimiento y 9 categorías, con un esquema que se modifica entre 2023 y 2024
> y que mezcla filas desagregadas con filas de agregado, lo que exige una etapa
> explícita de normalización y validación antes de cualquier análisis. Se emplea el
> modelo MapReduce (Dean & Ghemawat, 2008) e infraestructura de procesamiento en
> memoria (Zaharia et al., 2016) porque el pipeline debe ser extensible a los
> conjuntos de datos relacionados de MINCETUR y al histórico completo 2015-2025
> (61 890 registros), no porque el volumen actual lo exija.

## 6. Verificación cruzada pendiente

La validación más barata de que el pipeline está bien construido:

1. Calcular el TNOC nacional de 2024 desde los CSV, filtrando correctamente.
2. Compararlo con la cifra oficial de MINCETUR: **35,5 %**.

Si coincide, el filtro y el parseo son correctos. Si no, hay doble conteo o un
problema con los decimales.

## 7. Evidencia negativa: nadie cita la URL del dataset

La búsqueda del texto exacto de la URL del conjunto de datos en el índice web
devuelve **únicamente** el propio portal y páginas de `gob.pe`. Cero documentos
académicos.

Complementario: la Datatón "Exprésate Perú con Datos 2025" (211 conjuntos de datos
en catálogo, 9 finalistas) **no tuvo ningún proyecto de turismo u ocupabilidad**.
Los ganadores fueron de educación, empleo y vivienda.
