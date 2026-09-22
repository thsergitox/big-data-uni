# Verificación de fuentes

Ninguna cita de esta carpeta se emitió desde memoria. Todo DOI se resolvió en vivo
contra Crossref **comparando el título devuelto con el esperado**, y toda URL sin DOI
se comprobó con una petición HTTP real.

## 1. Resultado global

| Métrica | Valor |
| --- | --- |
| Fuentes totales | 36 |
| Entradas con DOI en `referencias.bib` | **16** (todas verificadas, 0 discrepancias) |
| DOI resueltos durante toda la investigación | 24 (incluye 8 candidatos luego descartados) |
| URLs verificadas por petición HTTP | 22 |
| Entradas que parsean con `bibtexparser` | 36 / 36 |
| Duplicadas · sin título | 0 · 0 |
| Con texto completo leído | 9 |
| Con resumen leído | 7 |
| Solo metadatos verificados | 20 |

Reproducible con:

```bash
python3 herramientas/verificar_dois.py     # -> 16 coinciden | 0 a revisar | 0 fallos
```

## 2. Verificaciones que fallaron y cómo se resolvieron

| Intento | Fallo | Sustituto |
| --- | --- | --- |
| Crossref en lote de 20 DOI | HTTP 429 (límite de tasa) | Reintento secuencial con pausas de ~1 s |
| `curl` a MDPI | HTTP 403 (protección perimetral) | Extracción renderizada — la página sí es accesible para una persona |
| `curl` al Banco Asiático de Desarrollo | HTTP 403 | Extracción renderizada + DOI resuelto en Crossref |
| Semantic Scholar en ráfaga | HTTP 429 | Pausas + cobertura equivalente vía OpenAlex |
| Google Scholar | 0 resultados (bloqueo del backend) | Se declara como brecha de cobertura. **No significa ausencia de literatura** |
| Navegador automatizado | El proveedor no expone punto de control | `curl` + extracción renderizada |
| Descarga del dataset sin cabecera | Fallo silencioso | Enviar `User-Agent` |
| `bibtexparser` en versión 2 | La API clásica cambió | Fijar la versión con `<2` |

## 3. Trampas de verificación

1. **Nunca emitir un DOI de memoria.** Un DOI recordado resolvió a un anuncio de
   empleo de la revista *Computer* en lugar del artículo esperado. Esa referencia se
   descartó y **no está en `referencias.bib`**.

2. **Crossref devuelve a veces títulos abreviados.** Por ejemplo, devuelve
   "MapReduce" para `10.1145/1327452.1327492`, cuyo título completo es "MapReduce:
   simplified data processing on large clusters". El `.bib` conserva el título
   completo del artículo, y eso es correcto.

3. **Crossref ocasionalmente intercambia nombre y apellido** en ordenamientos no
   occidentales. Se verificó el primer autor de cada entrada.

4. **Un 403 no significa inaccesible.** MDPI y el Banco Asiático de Desarrollo
   bloquean peticiones automatizadas pero sirven la página a un navegador real.

5. **Los DOI de arXiv (`10.48550/arXiv.*`) no resuelven en Crossref** porque están
   registrados en DataCite. Ninguna fuente de este conjunto es arXiv; si se añade
   alguna, verificarla por la página `abs`.

## 4. Validación del BibTeX

```bash
uv run --with "bibtexparser<2" python3 - <<'EOF'
import bibtexparser, re
from bibtexparser.bparser import BibTexParser
p = BibTexParser(common_strings=True); p.ignore_nonstandard_types=False
db = bibtexparser.load(open("referencias.bib"), parser=p)
raw = open("referencias.bib").read()
dec = re.findall(r'^@\w+\{([^,]+),', raw, flags=re.M)
print("declaradas:", len(dec), "| parseadas:", len(db.entries),
      "| faltantes:", set(dec) - {e['ID'] for e in db.entries},
      "| sin título:", [e['ID'] for e in db.entries if not e.get('title')])
EOF
```

Esperado: `declaradas: 36 | parseadas: 36 | faltantes: set() | sin título: []`

## 5. Validación del dataset

| Comprobación | Resultado |
| --- | --- |
| 11 CSV descargados y parseados | OK |
| Registros 2022–2025 | 21 347 |
| Registros 2015–2025 | 61 890 |
| Cambio de esquema 2023→2024 detectado | 23 → 24 columnas; solo 3 nombres en común |
| Filas agregadas identificadas | ~54 % en los cuatro años |
| Cardinalidad de dimensiones | 25 departamentos, 8 clases, 9 categorías, 516 combinaciones |

Pendiente: cruzar el TNOC nacional de 2024 calculado desde los CSV con el oficial
de MINCETUR (**35,5 %**). Si coincide, el filtro y el parseo son correctos.

## 6. Metadatos crudos de respaldo

En `datos/` quedan los archivos que sustentan las afirmaciones anteriores:

| Archivo | Contenido |
| --- | --- |
| `ckan_package.json` | Respuesta completa de la API CKAN del portal: fechas, licencia, recursos |
| `crossref_verified.json` | Metadatos devueltos por Crossref para cada DOI resuelto |
| `dataset_stats.json` | Bytes, columnas, filas y encabezado de cada uno de los 11 CSV |
| `csv_urls.txt` | Las 11 URLs de descarga directa |
