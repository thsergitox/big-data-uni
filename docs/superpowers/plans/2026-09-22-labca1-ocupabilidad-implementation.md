# LABCA1 Occupability Analysis Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Producir el CSV consolidado 2019-2025, catorce consultas Java MapReduce reproducibles, resultados/JAR, evidencias de Hadoop y un informe técnico completo.

**Architecture:** Los siete CSV oficiales se mantienen intactos y un consolidador genera un archivo canónico. Las consultas comparten un lector Java pequeño, pero cada una conserva su propio Driver, Mapper, Reducer, documentación y resultados. Hadoop 3 ejecuta los trabajos sobre YARN; los modelos de machine learning se entrenan y evalúan con MapReduce sin añadir Spark o Mahout.

**Tech Stack:** Python 3 estándar para consolidación, Java 21, Apache Hadoop 3.5.0, API Java MapReduce, Bash, Docker Compose, Playwright y LaTeX.

**Spec:** `docs/superpowers/specs/2026-09-22-labca1-ocupabilidad-design.md`

## Global Constraints

- Trabajar directamente sobre `main`.
- Aplicar TDD: prueba roja, cambio mínimo y prueba verde.
- No ejecutar `docker compose build`; se usa la imagen oficial ya configurada.
- Mantener intactos los siete CSV originales de `labs/labca1/data/`.
- Tratar 2025 como primer semestre, nunca como año completo.
- Filtrar `ID_CLASE=TT` e `ID_CATEGORIA=TT` en agregaciones generales.
- Escribir el informe en español sencillo, casual y en tercera persona: “se logró”, “se obtuvo”, “se observó”.
- No completar ni sobrescribir antecedentes, artículos relacionados o referencia bibliográfica formal; esas secciones pertenecen al compañero del usuario.
- No inventar resultados, métricas, tiempos ni capturas.
- Usar commits convencionales sin atribución de IA.

---

## File Map

| Ruta | Responsabilidad |
|---|---|
| `labs/labca1/dataset/prepare_dataset.py` | Normalizar y concatenar los siete CSV. |
| `labs/labca1/dataset/test_prepare_dataset.py` | Probar esquema, decimales, codificación y conteos. |
| `labs/labca1/data/Indicadores_ocupabilidad_2019_2025.csv` | Entrada canónica de todas las consultas. |
| `hadoop3/scripts/run-job.sh` | Compilar, ejecutar, exportar resultado y JAR al directorio invocante. |
| `tests/test-run-job.sh` | Probar el runner con Docker simulado. |
| `labs/labca1/queries/common/src/labca1/common/OccupabilityRecord.java` | Parsear una fila canónica y aplicar filtros de agregación. |
| `labs/labca1/queries/common/src/labca1/common/FeatureVector.java` | Crear características sin fuga para ML. |
| `labs/labca1/queries/common/src/labca1/common/ClassificationMetrics.java` | Accuracy, precision, recall, F1 y log loss. |
| `labs/labca1/queries/common/src/labca1/common/RegressionMetrics.java` | MAE, RMSE y R cuadrado. |
| `labs/labca1/queries/query01` a `query14` | Una consulta ejecutable y documentada por carpeta. |
| `labs/labca1/images/` | Capturas verificadas de Hadoop y recursos. |
| `labs/labca1/Plantilla201latex/Template.tex` | Informe final. |

---

### Task 1: Consolidar el dataset 2019-2025

**Files:**
- Create: `labs/labca1/dataset/prepare_dataset.py`
- Create: `labs/labca1/dataset/test_prepare_dataset.py`
- Create: `labs/labca1/data/Indicadores_ocupabilidad_2019_2025.csv`

**Interfaces:**
- Produces: `normalise_row(header: list[str], row: list[str]) -> list[str]`
- Produces: `consolidate(input_paths: list[Path], output_path: Path) -> int`
- Output: 24-column UTF-8 CSV separated by `;`, with decimal point.

- [ ] **Step 1: Write the failing unit tests**

```python
class PrepareDatasetTest(unittest.TestCase):
    def test_normalises_old_schema_and_decimal_comma(self):
        header = ["AÑO", "MES", "CLASE", "DESCRIPCIÓN CLASE", "CATEGORÍA",
                  "DESCRIPCIÓN CATEGORÍA", "DEPARTAMENTO", "UBIGEO"] + [f"C{i}" for i in range(15)]
        row = ["2019", "01", "TT", "TODAS CONSOLIDADAS", "TT",
               "TODAS CONSOLIDADAS", "LIMA", "150000"] + ["1"] * 3 + ["27,45"] + ["1"] * 11
        result = normalise_row(header, row)
        self.assertEqual(24, len(result))
        self.assertEqual("", result[0])
        self.assertEqual("2019", result[1])
        self.assertEqual("27.45", result[12])

    def test_rejects_unknown_column_count(self):
        with self.assertRaisesRegex(ValueError, "columnas"):
            normalise_row(["A", "B"], ["1", "2"])
```

- [ ] **Step 2: Run the focused tests and verify RED**

Run: `python3 -m unittest labs/labca1/dataset/test_prepare_dataset.py -v`

Expected: FAIL because `prepare_dataset` does not exist.

- [ ] **Step 3: Implement the minimal normaliser**

```python
CANONICAL_HEADER = (
    "FECHA_CORTE", "ANIO", "MES", "ID_CLASE", "CLASE", "ID_CATEGORIA",
    "CATEGORIA", "DEPARTAMENTO", "ID_UBIGEO", "NUMERO_ESTABLECIMIENTOS",
    "NUMERO_HABITACIONES", "NUMERO_PLAZAS_CAMA", "PORCENTAJE_TNOH",
    "PORCENTAJE_TNOC", "PROMEDIO_PERMANENCIA", "PROMEDIO_PERMANENCIA_NAC",
    "PROMEDIO_PERMANENCIA_EXT", "TOTAL_ARRIBOS", "TOTAL_ARRIBOS_NAC",
    "TOTAL_ARRIBOS_EXT", "TOTAL_PERNOCT", "TOTAL_PERNOCT_NAC",
    "TOTAL_PERNOCT_EXT", "TOTAL_EMPLEO",
)

DECIMAL_INDEXES = range(12, 17)

def normalise_row(header, row):
    if len(header) == 23 and len(row) == 23:
        result = [""] + row
    elif len(header) == 24 and len(row) == 24:
        result = list(row)
    else:
        raise ValueError(f"número de columnas inválido: {len(row)}")
    for index in DECIMAL_INDEXES:
        result[index] = result[index].replace(",", ".")
        float(result[index])
    if not 2019 <= int(result[1]) <= 2025:
        raise ValueError(f"año fuera de alcance: {result[1]}")
    return result
```

- [ ] **Step 4: Add integration assertions for all resources**

Assert exactly 38,730 data rows, years 2019-2025, months 01-12 for 2019-2024,
months 01-06 for 2025 and 25 departments. Read input using `encoding="latin-1"`
and write output using `encoding="utf-8"` and `newline=""`.

- [ ] **Step 5: Run tests and generate the canonical file**

Run:

```bash
python3 -m unittest labs/labca1/dataset/test_prepare_dataset.py -v
python3 labs/labca1/dataset/prepare_dataset.py \
  --input-dir labs/labca1/data \
  --output labs/labca1/data/Indicadores_ocupabilidad_2019_2025.csv
```

Expected: PASS and `Registros consolidados: 38730`.

- [ ] **Step 6: Commit**

```bash
git add labs/labca1/dataset labs/labca1/data
git commit -m "feat: consolidate occupancy datasets"
```

---

### Task 2: Adaptar el runner sin romper el flujo existente

**Files:**
- Modify: `tests/test-run-job.sh`
- Modify: `hadoop3/scripts/run-job.sh`

**Interfaces:**
- `--source <dir>` becomes repeatable.
- `--job-arg <value>` becomes repeatable and is appended after input/output.
- Default host destination: `$PWD/resultados/<output>/`.
- Produces: `resultado.txt` and `<output>.jar`.

- [ ] **Step 1: Write failing runner tests**

Add three tests:

```bash
test_defaults_results_to_invocation_directory() {
  # Execute from $temp_dir/query01 without HADOOP_RESULTS_DIR.
  # Assert $temp_dir/query01/resultados/examen/resultado.txt exists.
}

test_exports_job_jar() {
  # Fake docker cp writes the requested host file.
  # Assert results/examen/examen.jar exists.
}

test_accepts_shared_sources_and_job_arguments() {
  # Pass --source common --source query --job-arg HOTEL.
  # Assert both source copies and trailing HOTEL occur in docker-calls.
}
```

- [ ] **Step 2: Verify RED**

Run: `bash tests/test-run-job.sh`

Expected: FAIL on invocation-relative output and missing JAR.

- [ ] **Step 3: Implement array-based arguments**

```bash
readonly INVOCATION_DIR="$PWD"
readonly RESULTS_DIR="${HADOOP_RESULTS_DIR:-$INVOCATION_DIR/resultados}"
SOURCE_DIRS=()
JOB_ARGS=()

# parse_arguments branches
--source) SOURCE_DIRS+=("$2"); shift 2 ;;
--job-arg) JOB_ARGS+=("$2"); shift 2 ;;
```

Copy every source directory into a distinct remote subdirectory, compile every
`.java`, invoke:

```bash
hadoop jar "$REMOTE_ROOT/job.jar" "$MAIN_CLASS" \
  "$hdfs_input" "$hdfs_output" "${JOB_ARGS[@]}"
```

Export with:

```bash
"${COMPOSE[@]}" cp "namenode:$REMOTE_ROOT/job.jar" \
  "$local_output_dir/$OUTPUT_NAME.jar"
```

- [ ] **Step 4: Verify GREEN and shell syntax**

Run:

```bash
bash -n hadoop3/scripts/run-job.sh tests/test-run-job.sh
bash tests/test-run-job.sh
```

Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add hadoop3/scripts/run-job.sh tests/test-run-job.sh
git commit -m "feat: export query artifacts locally"
```

---

### Task 3: Crear el núcleo Java compartido

**Files:**
- Create: `labs/labca1/queries/common/src/labca1/common/OccupabilityRecord.java`
- Create: `labs/labca1/queries/common/src/labca1/common/FeatureVector.java`
- Create: `labs/labca1/queries/common/src/labca1/common/ClassificationMetrics.java`
- Create: `labs/labca1/queries/common/src/labca1/common/RegressionMetrics.java`
- Create: `labs/labca1/queries/common/test/labca1/common/CommonTest.java`
- Create: `labs/labca1/queries/test-common.sh`

**Interfaces:**

```java
public record OccupabilityRecord(
    String cutDate, int year, int month, String classId, String className,
    String categoryId, String categoryName, String department, String ubigeo,
    long establishments, long rooms, long beds, double tnoh, double tnoc,
    double averageStay, double nationalStay, double foreignStay,
    long arrivals, long nationalArrivals, long foreignArrivals,
    long overnightStays, long nationalOvernightStays,
    long foreignOvernightStays, long employment) {
  public static OccupabilityRecord parse(String line);
  public boolean isOverall();
  public boolean isClassTotal();
}
```

- [ ] **Step 1: Write the failing Java test harness**

The harness uses plain assertions and exits non-zero on failure:

```java
var row = OccupabilityRecord.parse(
    ";2019;01;TT;TODAS CONSOLIDADAS;TT;TODAS CONSOLIDADAS;LIMA;150000;" +
    "10;100;200;27.45;20.10;1.5;1.4;2.0;1000;900;100;1500;1300;200;50");
check(row.year() == 2019, "year");
check(row.isOverall(), "overall filter");
check(Math.abs(row.tnoh() - 27.45) < 1e-9, "decimal");
```

Also test malformed column counts, zero-denominator feature ratios, confusion
matrix values, log-loss clipping, MAE, RMSE and R squared.

- [ ] **Step 2: Verify RED**

Run: `bash labs/labca1/queries/test-common.sh`

Expected: compilation failure because the production classes do not exist.

- [ ] **Step 3: Implement parsing and pure metric classes**

Use `line.split(";", -1)` to preserve the empty `FECHA_CORTE`. Reject headers
and malformed records explicitly. Feature ratios return `0.0` when their
denominator is zero.

- [ ] **Step 4: Verify GREEN**

Run: `bash labs/labca1/queries/test-common.sh`

Expected: `PASS: common query classes`.

- [ ] **Step 5: Commit**

```bash
git add labs/labca1/queries/common labs/labca1/queries/test-common.sh
git commit -m "feat: add occupancy query domain model"
```

---

### Task 4: Implementar las cinco consultas base

**Files:**
- Create: `labs/labca1/queries/query01/` through `query05/`

**Interfaces:**

| Folder | Main class | Map output | Final output |
|---|---|---|---|
| query01 | `labca1.query01.ArrivalsByOriginDriver` | `year -> nac,ext` | year totals |
| query02 | `labca1.query02.OccupancyByDepartmentDriver` | `year;department -> tnoh,count` | mean TNOH |
| query03 | `labca1.query03.OccupancyByClassDriver` | `year;class -> tnoh,tnoc,count` | both means |
| query04 | `labca1.query04.EmploymentCapacityDriver` | `year;department -> employment,rooms` | jobs per 100 rooms |
| query05 | `labca1.query05.SeasonalityDriver` | `month -> arrivals,nights` | monthly totals |

- [ ] **Step 1: Create one controlled fixture and failing output checks**

Create `labs/labca1/queries/fixtures/overall-small.csv` with four TT/TT rows and
one detail row. Add `labs/labca1/queries/test-reference-results.py` that computes
the five expected outputs from that fixture. The detail row must not affect
queries 01, 02, 04 or 05.

- [ ] **Step 2: Verify RED for query01**

Run query01 against the fixture through `run-job.sh`. Expected: fail because the
main class does not exist.

- [ ] **Step 3: Implement query01 with Driver, Mapper and Reducer**

Mapper rule:

```java
if (record.isOverall()) {
  output.collect(new IntWritable(record.year()),
      new Text(record.nationalArrivals() + "," + record.foreignArrivals()));
}
```

Reducer sums both values and emits `year\tnational=<n>\tforeign=<n>`.

- [ ] **Step 4: Run query01 and compare with the reference**

Use the runner from inside `query01` with common and local sources. Assert the
text output matches the fixture reference exactly.

- [ ] **Step 5: Repeat the same RED-GREEN cycle for query02-query05**

For means, reducers carry `sum` and `count`. Query03 accepts only
`record.isClassTotal() && !record.isOverall()`. Query04 calculates the ratio
after summing employment and rooms, not as a mean of row-level ratios.

- [ ] **Step 6: Add one README per query**

Each README includes question, fields, filter, command and output format. Use
the exact main class from the interface table.

- [ ] **Step 7: Commit**

```bash
git add labs/labca1/queries/query0{1,2,3,4,5} labs/labca1/queries/fixtures labs/labca1/queries/test-reference-results.py
git commit -m "feat: add base occupancy mapreduce queries"
```

---

### Task 5: Implementar estadísticas, subtexto y extremos

**Files:**
- Create: `labs/labca1/queries/query06/`
- Create: `labs/labca1/queries/query07/`
- Create: `labs/labca1/queries/query08/`

**Interfaces:**
- query06 main: `labca1.query06.OccupancyStatisticsDriver`
- query07 main: `labca1.query07.SubstringSearchDriver`
- query07 third argument: required search text, case-insensitive and accent-preserving.
- query08 main: `labca1.query08.OccupancyExtremesDriver`

- [ ] **Step 1: Add failing reference cases**

Use values `10, 20, 30` for query06. Expected count 3, mean 20, median 20 and
population standard deviation `8.1649658093`. Query07 searches `hotel` across
class/category/department and must return the full original canonical line.
Query08 uses two departments and verifies both maximum and minimum per year.

- [ ] **Step 2: Verify RED for all three main classes**

Run each query on the controlled fixture and record the expected missing-class
failure.

- [ ] **Step 3: Implement query06**

The mapper emits TT/TT TNOH values under a constant key. The reducer stores the
values, sorts them for the median and uses Welford's algorithm for stable mean
and variance.

- [ ] **Step 4: Implement query07**

The Driver validates `args.length == 3` and stores the term in `JobConf`. The
Mapper uses `Locale.ROOT` for case folding. It emits the input offset as key and
the complete line as value when any of the three text fields contains the term.

- [ ] **Step 5: Implement query08**

First aggregate monthly TNOH by `year;department`, then select annual extrema.
If the selected API requires two jobs to avoid selecting extrema from partial
mapper data, keep both inside the same query Driver and document that query08
uses two stages even though only queries 09-10 require it.

- [ ] **Step 6: Verify outputs and commit**

```bash
git add labs/labca1/queries/query0{6,7,8} labs/labca1/queries/test-reference-results.py
git commit -m "feat: add statistical and search queries"
```

---

### Task 6: Implementar las consultas encadenadas

**Files:**
- Create: `labs/labca1/queries/query09/`
- Create: `labs/labca1/queries/query10/`

**Interfaces:**
- query09 main: `labca1.query09.OccupancyChangeDriver`
- query10 main: `labca1.query10.ForeignShareDriver`
- Both derive `<output>-intermediate` and delete it only after successful final output.

- [ ] **Step 1: Write failing two-stage fixture tests**

For query09, provide 2019 and 2024 values for two departments and assert the
percentage-point difference. For query10, include a zero-arrivals row and assert
it is excluded rather than divided by zero.

- [ ] **Step 2: Implement query09 stage 1**

Emit `year;department -> tnoh,count`, reduce to annual means.

- [ ] **Step 3: Implement query09 stage 2**

Re-key stage-1 rows by department, retain 2019 and 2024, and emit
`department\t2019=<x>\t2024=<y>\tchange_pp=<y-x>`.

- [ ] **Step 4: Implement query10 stage 1 and stage 2**

Stage 1 computes `100 * foreignArrivals / arrivals` for each valid TT/TT row.
Stage 2 averages by department and period. Period labels are `PRE_2019`,
`IMPACT_2020_2021`, `RECOVERY_2022_2024` and `CURRENT_2025_H1`.

- [ ] **Step 5: Assert both stages ran**

Use fake-Docker call logs or JobHistory to verify two `hadoop` jobs per query.
Compare the final fixture outputs with the reference script.

- [ ] **Step 6: Commit**

```bash
git add labs/labca1/queries/query{09,10} labs/labca1/queries/test-reference-results.py
git commit -m "feat: add chained decimal mapreduce queries"
```

---

### Task 7: Implementar clasificación comparable

**Files:**
- Create: `labs/labca1/queries/query11/`
- Create: `labs/labca1/queries/query12/`
- Extend: common feature and metric tests.

**Interfaces:**
- query11 main: `labca1.query11.NaiveBayesClassificationDriver`
- query12 main: `labca1.query12.LogisticClassificationDriver`
- Label: `HIGH` when TNOH is at least the training median, otherwise `LOW`.
- Train: 2019-2023; test: 2024; recent validation: 2025-I.

- [ ] **Step 1: Test feature extraction and temporal split**

Assert the feature vector contains month, establishments, rooms, beds,
arrivals/room, nights/bed and employment/establishment. Assert it contains
neither TNOH nor TNOC. Assert 2024 never enters training statistics.

- [ ] **Step 2: Test metrics on known predictions**

Use four labelled examples with one false positive and assert the exact
confusion matrix, accuracy, precision, recall, F1 and finite log loss.

- [ ] **Step 3: Implement query11 training and evaluation**

Training reducers aggregate class counts, sums and squared sums for Gaussian
Naive Bayes. Evaluation loads the small model through the job configuration or
distributed cache and aggregates metrics separately for `TEST_2024` and
`VALIDATION_2025_H1`.

- [ ] **Step 4: Implement query12 iterative gradient aggregation**

Standardise using 2019-2023 statistics. Each iteration maps training examples
to gradients and a reducer sums them. The Driver updates weights with a fixed,
documented learning rate and stops after the configured iteration count. Store
iteration count and learning rate in the output.

- [ ] **Step 5: Add numerical safety tests**

Assert zero variances receive a small epsilon, sigmoid avoids overflow and
probabilities are clipped before log loss.

- [ ] **Step 6: Run fixture evaluation and commit**

```bash
git add labs/labca1/queries/common labs/labca1/queries/query{11,12}
git commit -m "feat: add occupancy classification models"
```

---

### Task 8: Implementar regresión comparable

**Files:**
- Create: `labs/labca1/queries/query13/`
- Create: `labs/labca1/queries/query14/`
- Extend: `RegressionMetrics` tests.

**Interfaces:**
- query13 main: `labca1.query13.LinearRegressionDriver`
- query14 main: `labca1.query14.RidgeRegressionDriver`
- Same features and temporal split as classification.
- Outputs: coefficients, intercept, sample count, MAE, RMSE and R squared.

- [ ] **Step 1: Add failing regression tests**

Use `y = 2x + 1` to assert near-zero MAE/RMSE and R squared 1. Add a constant
target case and define R squared as 0 when total variance is zero.

- [ ] **Step 2: Implement shared sufficient-statistics aggregation**

Mappers emit partial `X'X`, `X'y`, counts and scaling statistics. Reducers add
them component-wise. Solve the small linear system in the Driver with partial
pivoting and reject singular matrices with a clear message.

- [ ] **Step 3: Implement query13**

Use lambda 0.0 and the training-only standardisation values.

- [ ] **Step 4: Implement query14**

Use a documented positive lambda and do not regularise the intercept. Keep all
other inputs identical so the metrics are comparable.

- [ ] **Step 5: Evaluate 2024 and 2025-I separately**

Run evaluation jobs using the trained coefficients. Assert all metrics are
finite and sample counts equal the expected TT/TT rows for each period.

- [ ] **Step 6: Commit**

```bash
git add labs/labca1/queries/common labs/labca1/queries/query{13,14}
git commit -m "feat: add occupancy regression models"
```

---

### Task 9: Ejecutar las catorce consultas y conservar artefactos

**Files:**
- Create: `labs/labca1/queries/run-all.sh`
- Create: each `queryXX/resultados/<name>/resultado.txt`
- Create: each `queryXX/resultados/<name>/<name>.jar`
- Create: `labs/labca1/queries/execution-summary.tsv`

**Interfaces:**
- `run-all.sh` stops on the first failed query.
- Summary columns: query, main class, status, HDFS path, local result, JAR.

- [ ] **Step 1: Write a dry-run test for command construction**

Add `--dry-run` to print fourteen runner commands without invoking Docker.
Assert fourteen unique outputs and main classes.

- [ ] **Step 2: Start and smoke-test Hadoop**

Run:

```bash
./hadoop3/scripts/start.sh
./hadoop3/scripts/smoke-test.sh
```

Expected: HDFS has one live DataNode and YARN has one running NodeManager.

- [ ] **Step 3: Execute query01-query10**

Run `labs/labca1/queries/run-all.sh --from 1 --to 10`. Inspect each output for
headers accidentally processed as data, `NaN`, `Infinity` or duplicate totals.

- [ ] **Step 4: Execute query11-query14**

Run `labs/labca1/queries/run-all.sh --from 11 --to 14`. Verify model and metric
sections exist for 2024 and 2025-I.

- [ ] **Step 5: Cross-check results independently**

Use a read-only Python verification script to calculate representative totals,
means and ML sample counts from the consolidated CSV. Compare with Hadoop
outputs. Do not rewrite the Hadoop results.

- [ ] **Step 6: Commit reproducible sources and required deliverables**

Before committing generated JAR/results, confirm repository policy and size.
If binaries are intentionally not versioned, keep them in the final ZIP and
document their generation command. Do not silently omit mandatory deliverables.

---

### Task 10: Capturar monitoreo y evidencia visual

**Files:**
- Create: `labs/labca1/images/namenode-overview.png`
- Create: `labs/labca1/images/resourcemanager-applications.png`
- Create: `labs/labca1/images/nodemanager-resources.png`
- Create: `labs/labca1/images/jobhistory-query.png`
- Create: `labs/labca1/images/docker-stats.png`
- Create: `labs/labca1/images/README.md`

- [ ] **Step 1: Load the browser-control skill before Playwright**

Read `/home/sergi/.codex/plugins/cache/openai-bundled/browser/26.810.50856/skills/control-in-app-browser/SKILL.md`.

- [ ] **Step 2: Capture the four Hadoop UIs**

Use ports 9870, 8088, 8042 and 19888. Set a consistent viewport. Capture only
after verifying the page shows the expected service and real job data.

- [ ] **Step 3: Capture live resource use**

Start query12 or another sufficiently long query, run `docker stats --no-stream`
during execution and save readable evidence. Record timestamp and query in
`images/README.md`.

- [ ] **Step 4: Verify every image**

Open every PNG and reject blank pages, loading spinners, clipped tables or
screens without identifying information.

- [ ] **Step 5: Commit**

```bash
git add labs/labca1/images
git commit -m "docs: add hadoop execution evidence"
```

---

### Task 11: Redactar el informe con resultados reales

**Files:**
- Modify: `labs/labca1/Plantilla201latex/Template.tex`
- Modify: `labs/labca1/Plantilla201latex/Bibliog.bib` only when integrating the teammate's references.

**Interfaces:**
- Every query subsection contains type, question, fields, solution, code,
  input/output, interpretation and limitation.
- Voice: third person, short sentences, casual but technically correct.

- [ ] **Step 1: Mark ownership boundaries**

Keep the three teammate-owned subsections intact. Add a short LaTeX comment
such as `% CONTENIDO A INTEGRAR POR EL COMPAÑERO`, not invented prose.

- [ ] **Step 2: Write dataset preparation**

Use this paragraph as the starting voice reference:

```latex
Los archivos no tenían exactamente la misma estructura. Los datos de 2019 a
2023 usaban 23 columnas, mientras que los archivos más recientes añadían la
fecha de corte. Por eso, se creó un archivo consolidado con 24 columnas. También
se cambió la coma decimal por un punto y se guardó el resultado en UTF-8. Al
final, se obtuvieron 38 730 registros entre enero de 2019 y junio de 2025.
```

- [ ] **Step 3: Write environment and methodology**

Explain the single-node Docker deployment, HDFS, YARN, runner, TT/TT filtering,
2025-I treatment and temporal ML split. Avoid textbook-length explanations.

- [ ] **Step 4: Write query01-query10 sections from outputs**

For each section, paste a short real output excerpt and interpret only what the
numbers support. Use “se obtuvo” for results and “se usó” for method.

- [ ] **Step 5: Write classification and regression sections**

Create one comparison table per model family. Classification columns include
accuracy, log loss, precision, recall and F1. Regression columns include MAE,
RMSE and R squared. Separate 2024 from 2025-I.

- [ ] **Step 6: Write monitoring and limitations**

Insert the verified images with captions. Explain that the small dataset and
single NodeManager prove the processing flow but do not demonstrate horizontal
scalability.

- [ ] **Step 7: Write results, discussion and conclusions**

Use measured findings. Do not claim causation from temporal association. Keep
conclusions brief and connect them to the questions.

- [ ] **Step 8: Run prose checks**

Search for first-person phrasing, inflated wording, unsupported “demuestra”,
unresolved placeholders and repeated explanations. Confirm frequent but natural
use of “se logró”, “se obtuvo” and “se observó”.

- [ ] **Step 9: Commit**

```bash
git add labs/labca1/Plantilla201latex
git commit -m "docs: write labca1 occupancy report"
```

---

### Task 12: Verificar la entrega y preparar el ZIP

**Files:**
- Create: final PDF from `Template.tex`
- Create: delivery ZIP outside tracked source if repository policy excludes it.

- [ ] **Step 1: Run all non-destructive tests**

```bash
python3 -m unittest labs/labca1/dataset/test_prepare_dataset.py -v
bash tests/test-run-job.sh
bash labs/labca1/queries/test-common.sh
python3 labs/labca1/queries/test-reference-results.py
```

- [ ] **Step 2: Verify artifacts**

Assert fourteen Java source folders, fourteen JARs, fourteen readable outputs,
the consolidated CSV, the report source, final PDF and monitoring images.

- [ ] **Step 3: Compile and inspect the final report**

Compile using the existing LaTeX workflow. Render every PDF page to PNG and
inspect for clipped code, broken tables, missing images, bad accents and blank
sections. Do not modify teammate-owned content.

- [ ] **Step 4: Audit the report against the PDF prompt**

Check all fourteen consultations, linked MapReduce jobs, ML metrics, inputs,
outputs, interpretations, resource monitoring, dataset inclusion and mandatory
JARs. Record any missing teammate-owned bibliography item separately.

- [ ] **Step 5: Inspect Git state**

Run `git status --short` and `git log --oneline -12`. Confirm no temporary files,
credentials or unrelated edits are included.

- [ ] **Step 6: Stop Hadoop without deleting volumes**

Run: `./hadoop3/scripts/stop.sh`

- [ ] **Step 7: Final commit if verification changed tracked files**

Use a conventional commit describing only the verified changes.

---

## Execution Order and Checkpoints

1. Tasks 1-3 establish trusted data and tooling.
2. Tasks 4-6 complete the ten non-ML consultations.
3. Tasks 7-8 complete and compare the four ML consultations.
4. Task 9 creates real outputs and mandatory JARs.
5. Task 10 captures evidence while Hadoop data still exists.
6. Tasks 11-12 write and verify the report.

Do not begin report result paragraphs before Task 9. Doing so would force the
writer to guess numbers, and guessed numbers are not acceptable evidence.
