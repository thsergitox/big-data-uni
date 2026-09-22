# Referencias bibliográficas: dataset MINCETUR de Indicadores de Ocupabilidad

Esta carpeta reúne la investigación bibliográfica que sustenta el uso del dataset
[Indicadores de Ocupabilidad](https://www.datosabiertos.gob.pe/dataset/indicadores-de-ocupabilidad-ministerio-de-comercio-exterior-y-turismo-mincetur)
de MINCETUR en el informe del laboratorio. Cubre los años **2022–2025** del
conjunto de datos.

El objetivo no es acumular citas: es **demostrar con evidencia qué se ha hecho ya
con estos datos y qué no**, para poder justificar el aporte del informe sin
inventar un vacío que no existe.

## Ruta recomendada

1. [Metodología de la búsqueda](01-metodologia.md) — bases, consultas, decisiones y límites
2. [Hallazgos sobre el dataset](02-hallazgos.md) — lo que no está publicado en ninguna fuente
3. [Fuentes oficiales](03-fuentes-oficiales.md) — dataset, normativa, estadística del Estado
4. [Investigaciones peruanas](04-fuentes-peruanas.md) — quién ha usado estos datos
5. [Marco de big data y ML](05-fuentes-marco.md) — definiciones y anclajes técnicos
6. [Tabla comparativa](06-tabla-comparativa.md) — métodos y hallazgos lado a lado
7. [Bullets para la presentación](07-bullets-presentacion.md) — listos para diapositivas
8. [Verificación y trampas](08-verificacion.md) — cómo se validó cada fuente

## Resumen ejecutivo

**El dataset sí se usa en Perú, pero no como objeto de análisis de macrodatos.**

| Forma de uso | Evidencia | Consecuencia |
| --- | --- | --- |
| **Normado por el Estado** | El MEF obliga a usar los indicadores para estimar demanda en proyectos de inversión pública turística | Justifica el dataset sin discusión |
| **Instrumental** | Decenas de tesis peruanas lo citan para dimensionar muestras o estimar demanda de un hotel | El dato es conocido, pero no analizado |
| **Analítico** | Solo 2 tesis de pregrado regionales (UNH, Huancavelica) con regresión lineal multivariada y MCO | Antecedente real, alcance muy limitado |
| **Macrodatos / ML / MapReduce** | **Ninguno** | El aporte del informe |

Además, al descargar y parsear los CSV aparecieron **tres problemas de ingeniería
de datos que ninguna fuente documenta**: el esquema cambia entre 2023 y 2024, el
54 % de las filas son agregados que inducen doble conteo, y hay detalles de parseo
que rompen una lectura ingenua. Ver [hallazgos](02-hallazgos.md).

## Contenido de la carpeta

```text
referencias/
├── README.md                    este índice
├── 01-metodologia.md            cómo se buscó y qué se decidió
├── 02-hallazgos.md              hallazgos técnicos sobre el dataset
├── 03-fuentes-oficiales.md      dataset, MEF, INEI, informes MINCETUR
├── 04-fuentes-peruanas.md       tesis y artículos peruanos
├── 05-fuentes-marco.md          marco de big data, ML y arquitectura
├── 06-tabla-comparativa.md      comparación metodológica
├── 07-bullets-presentacion.md   bullets para diapositivas
├── 08-verificacion.md           verificación de fuentes y trampas
├── referencias.bib              BibTeX final, 36 entradas con tiering
├── datos/                       metadatos crudos y estadísticas del dataset
└── herramientas/                scripts reproducibles
```

## Cómo usar el BibTeX

`referencias.bib` tiene **36 entradas** divididas en dos niveles:

- **Tier 1 (15)** — leer y analizar. Cada una lleva el análisis completo en un
  comentario `%` encima de la entrada: sección sugerida, cómo se verificó, 2–4
  frases de relación con este informe, y una oración de borrador en inglés.
- **Tier 2 (21)** — cita de contexto. Basta con leer el resumen.

Para incorporarlas al informe:

```bash
# Ver qué claves existen antes de pegar
grep -E '^@' referencias.bib
```

> **Atención — colisión de claves.** `labs/labca1/Plantilla201latex/Bibliog.bib`
> ya contiene una entrada `mincetur_ocupabilidad`. `referencias.bib` define la
> misma clave con metadatos equivalentes. **No pegar ambas**: al fusionar, hay que
> conservar una sola. La versión de `referencias.bib` añade licencia, identificador
> CKAN y la fuente primaria; ver la comparación en
> [fuentes oficiales](03-fuentes-oficiales.md#entrada-para-el-bib-del-informe).

## Cómo reproducir

Los scripts de `herramientas/` no necesitan claves de API:

```bash
cd labs/labca1/referencias

# 1. Descarga los 11 CSV a datos/csv/ (~7 MB, exige User-Agent)
python3 herramientas/descargar_dataset.py

# 2. Reproduce los tres hallazgos del informe
python3 herramientas/perfilar_dataset.py

# 3. Verifica los DOI contra Crossref comparando el título
python3 herramientas/verificar_dois.py
```

Los CSV descargados **no se versionan** (son ~7 MB y MINCETUR los actualiza);
`datos/csv/` está ignorado por git y se regenera con el primer script.

## Fuentes principales

- [Plataforma Nacional de Datos Abiertos del Perú](https://www.datosabiertos.gob.pe/)
- [Observatorio de Datos de MINCETUR](https://observatorio.mincetur.gob.pe/dashobservatorio/)
- [OpenAlex](https://openalex.org/) · [Crossref](https://www.crossref.org/) ·
  [Semantic Scholar](https://www.semanticscholar.org/)
- [ALICIA / CONCYTEC](https://alicia.concytec.gob.pe/) — repositorio nacional de tesis
