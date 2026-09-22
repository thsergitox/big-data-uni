# Metodología de la búsqueda

Cómo se buscó, qué se decidió y qué límites tiene el resultado. Esta sección es
la que permite auditar las conclusiones de las demás.

## 1. Bases consultadas

| Base | Vía | Estado |
| --- | --- | --- |
| OpenAlex | API REST | Sin límite práctico de tasa; base del barrido amplio |
| Crossref | API REST | Fuente autoritativa para verificar DOI |
| Semantic Scholar | API y herramientas de citas | Recuperó el corpus que ninguna consulta temática encontró |
| CORE | Buscador unificado | El único conector adicional que aportó resultados |
| ALICIA / CONCYTEC (RENATI) | Portal web | Registro de tesis peruanas |
| Plataforma Nacional de Datos Abiertos | API CKAN | Metadatos autoritativos del dataset |
| Repositorios institucionales | HTTP directo | UNH, UNMSM, UCV, UPAO, UTP, UNSAAC, UNTRM, PUCP, UPN, USIL, UP, ULima |
| Google Scholar | — | **Bloqueado**: 0 resultados. No significa ausencia de literatura |
| BASE, DOAJ, Zenodo, OpenAIRE, HAL, SSRN, dblp | Buscador unificado | 0 resultados para este tema |

## 2. Las cuatro familias de consultas

El hallazgo metodológico más importante: **el mismo objeto está indexado con
vocabularios distintos según la comunidad**, y buscar solo el término académico
produce una conclusión falsa.

### Familia A — terminología administrativa (la que funcionó)

El nombre real del producto dentro del Estado peruano es **"indicadores mensuales
de ocupabilidad de establecimientos de hospedaje colectivo"**, no "ocupabilidad
hotelera". Con esta familia aparecieron los usos reales del dataset.

```text
"indicadores mensuales de ocupabilidad de establecimientos de hospedaje colectivo"
"indicadores de ocupabilidad" MINCETUR
"plazas-cama" OR "tasa neta de ocupación" ocupabilidad hotelera Perú
"Encuesta Mensual de Turismo" Perú investigación académica
```

**Resultado:** tesis de UNH, UPN, USIL, UCV, PUCP; expedientes de inversión
pública; planes de desarrollo turístico local.

### Familia B — terminología académica

```text
ocupabilidad hotelera Peru
tasa de ocupabilidad hotelera Peru
hotel occupancy rate Peru tourism
MINCETUR Peru tourism statistics
```

**Resultado:** dos tesis peruanas y artículos internacionales de hotelería.
Ninguna usa el CSV de la Plataforma Nacional de Datos Abiertos.

### Familia C — big data, ML y arquitectura

```text
tourism demand forecasting Peru machine learning
big data tourism Peru open data
MapReduce Hadoop tourism analytics
machine learning hotel occupancy forecasting
open government data reuse tourism
big data tourism destination management
```

**Resultado:** el marco internacional y precedentes peruanos de Hadoop sobre la
plataforma nacional, pero **en salud**, no en turismo.

### Familia D — la URL exacta del dataset

```text
"datosabiertos.gob.pe/dataset/indicadores-de-ocupabilidad"
```

**Resultado:** solo el propio portal y páginas de `gob.pe`. **Cero documentos
académicos.** Es la evidencia negativa más limpia del informe.

## 3. Cadena de citas

Se recorrieron las citas y referencias de las fuentes semilla. Esta ruta recuperó
cuatro artículos peruanos de 2024–2026 que **ninguna consulta por palabras clave
encontró**:

| Semilla | Dirección | Hallazgo útil |
| --- | --- | --- |
| Bravo et al. (2023) | citaciones | Bravo-Jaico et al. (2026), Pinedo et al. (2026) |
| Lahura & Sabrera (2023) | citaciones | Sánchez-Dávila & García (2026) |
| Pinedo et al. (2026) | referencias | Zavaleta et al. (2024), Gutiérrez & Defilippi (2025) |
| Bravo (2023) + Lahura (2023) | recomendaciones | Yáñez et al. (2026) |

## 4. Criterios aplicados

| Decisión | Criterio |
| --- | --- |
| Ventana temporal | Prioridad 2022–2026. Ampliación a 2021 solo sin equivalente posterior, marcada explícitamente |
| Pre-2021 | Solo como anclaje técnico canónico (MapReduce, Spark) o antecedente histórico, nunca como fuente de antecedentes |
| Calidad | Journals indexados, actas arbitradas, tesis peruanas y documentos oficiales por encima de blogs y notas de prensa |
| Verificación | Ningún DOI desde memoria: se resuelve en vivo contra Crossref y se compara el título |
| Descarte | Fuentes cuyo DOI o URL no se pudo verificar quedan fuera |

## 5. Resultado del barrido

| Ventana | Fuentes |
| --- | --- |
| 2022–2026 (prioritaria) | 31 |
| 2021 (ventana extendida, marcadas) | 4 |
| Pre-2021 (2 fundacionales + 1 antecedente) | 3 |
| **Total** | **36** |

De las 36: 9 con texto completo leído, 7 con resumen leído y 20 con metadatos
verificados. El nivel de evidencia de cada una está declarado en la
[tabla comparativa](06-tabla-comparativa.md#nivel-de-evidencia) para no citar
como leído lo que solo se verificó por metadatos.

## 6. Límites declarados

| Límite | Qué haría falta para cerrarlo |
| --- | --- |
| Los portales DIRCETUR/GERCETUR de los 25 gobiernos regionales publican los indicadores en PDF, no en repositorios indexados | Consultar los 25 portales uno por uno |
| La búsqueda de texto completo en PDFs de RENATI no es accesible programáticamente | Descarga masiva y búsqueda local |
| Google Scholar bloquea consultas automatizadas | Búsqueda manual desde el navegador |
| Literatura gris peruana (consultorías, informes no publicados) no tiene repositorio central | Contacto institucional |

**Cómo declararlo en el informe:** como limitación de cobertura, nunca como
ausencia demostrada. "Se consultaron las vías indexadas; los portales regionales
que publican en PDF quedan como cobertura pendiente."

## 7. Herramientas que no hay que reintentar

| Herramienta | Problema |
| --- | --- |
| Google Scholar vía API | Devuelve 0 resultados en cualquier consulta — es bloqueo, no ausencia |
| Navegador automatizado | El proveedor disponible no expone punto de control; usar `curl` y extracción renderizada |
| `curl` a MDPI y al Banco Asiático de Desarrollo | Devuelven 403 por protección perimetral, pero **la página sí es accesible** para una persona: verificar con extracción renderizada antes de descartar una fuente |

## 8. Trampas del entorno

| Trampa | Solución |
| --- | --- |
| Las URLs de descarga del dataset fallan en silencio sin cabecera | Enviar siempre `User-Agent` |
| Crossref limita en ráfaga (HTTP 429) | Añadir `mailto=` en la URL y pausas de ~1 s |
| Un DOI recordado resolvió a un artículo sin relación | **Nunca emitir un DOI de memoria** |
| `bibtexparser` v2 rompe la API clásica | Fijar la versión con `<2` |
