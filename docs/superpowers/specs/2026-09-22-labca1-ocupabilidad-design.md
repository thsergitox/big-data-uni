# Diseño del laboratorio calificado 1: indicadores de ocupabilidad

**Fecha:** 2026-09-22  
**Estado:** aprobado para planificación  
**Rama de trabajo:** `main`

## 1. Objetivo

Construir una entrega reproducible para el Laboratorio Calificado 1 usando el
dataset de Indicadores de Ocupabilidad de MINCETUR. La entrega incluye un CSV
consolidado de 2019 a junio de 2025, catorce consultas en Java MapReduce, los
resultados exportados al host, evidencias de Hadoop y el informe en LaTeX.

El trabajo no desarrollará las secciones de investigaciones anteriores,
referencia bibliográfica formal del dataset ni artículos relacionados. Otro
integrante del grupo prepara esas secciones. La plantilla conservará su espacio
para integrarlas después.

## 2. Alcance y restricciones

- Se trabaja directamente sobre `main`, por decisión del usuario.
- Se implementan catorce consultas. El PDF presenta siete grupos, pero algunos
  grupos exigen dos o cinco consultas independientes.
- Todas las consultas se escriben en Java y se ejecutan en Hadoop 3 mediante
  YARN y MapReduce.
- Los modelos de clasificación y regresión se implementan en Java. No se añade
  Spark, Mahout ni otro servicio al `compose.yaml`.
- Los CSV originales se conservan sin modificaciones.
- El informe usa redacción sencilla, directa y natural. Cada afirmación sobre
  resultados debe estar respaldada por una salida real.
- La presentación queda fuera de este cambio. Se preparará después del informe.

## 3. Preparación del dataset

### 3.1 Archivos de entrada

La carpeta `labs/labca1/data/` contiene siete recursos oficiales:

- `Indicadores_ocupabilidad_2019.csv`
- `Indicadores_ocupabilidad_2020.csv`
- `Indicadores_ocupabilidad_2021.csv`
- `Indicadores_ocupabilidad_2022.csv`
- `Indicadores_ocupabilidad_2023.csv`
- `Indicadores_ocupabilidad_2024.csv`
- `Indicadores_ocupabilidad_2025_1er_semestre.csv`

Los archivos suman 38 730 registros, sin contar las cabeceras. Los años 2019 a
2024 contienen doce meses. El recurso 2025 contiene enero a junio.

### 3.2 Diferencias que se deben normalizar

Los recursos no tienen una estructura uniforme. Los archivos hasta 2023 usan
23 columnas y nombres antiguos. Los archivos 2024 y 2025 agregan
`FECHA_CORTE` y emplean nombres normalizados. Los decimales usan coma en los
primeros recursos y punto en 2025. Los archivos originales usan codificación
ISO-8859-1.

El consolidador produce UTF-8, separador punto y coma y punto decimal. La
cabecera canónica será:

```text
FECHA_CORTE;ANIO;MES;ID_CLASE;CLASE;ID_CATEGORIA;CATEGORIA;DEPARTAMENTO;ID_UBIGEO;NUMERO_ESTABLECIMIENTOS;NUMERO_HABITACIONES;NUMERO_PLAZAS_CAMA;PORCENTAJE_TNOH;PORCENTAJE_TNOC;PROMEDIO_PERMANENCIA;PROMEDIO_PERMANENCIA_NAC;PROMEDIO_PERMANENCIA_EXT;TOTAL_ARRIBOS;TOTAL_ARRIBOS_NAC;TOTAL_ARRIBOS_EXT;TOTAL_PERNOCT;TOTAL_PERNOCT_NAC;TOTAL_PERNOCT_EXT;TOTAL_EMPLEO
```

`FECHA_CORTE` queda vacío en los recursos que no publican ese campo. No se
inventa una fecha. El archivo resultante se llamará
`Indicadores_ocupabilidad_2019_2025.csv`.

### 3.3 Consolidador reproducible

Un script con la biblioteca estándar de Python realizará una única tarea:
transformar los siete recursos al esquema canónico y concatenarlos. El script
fallará si encuentra una cabecera desconocida, un número de columnas inválido,
un año fuera de 2019-2025 o un decimal que no se pueda interpretar.

Las pruebas comprobarán:

- transformación de la cabecera antigua;
- conservación de la cabecera nueva;
- conversión de coma a punto decimal;
- salida UTF-8;
- 38 730 registros finales;
- doce meses para 2019-2024 y seis para 2025;
- 25 departamentos por periodo;
- lectura numérica de todos los campos cuantitativos.

El informe describirá este proceso y aclarará que la normalización cambia la
representación, no el significado de los datos.

## 4. Regla de agregación

El dataset mezcla filas de detalle con subtotales. Una suma directa de todas
las filas duplicaría información. Las consultas nacionales o departamentales
generales usarán `ID_CLASE=TT` e `ID_CATEGORIA=TT`. Las comparaciones por clase
usarán `ID_CATEGORIA=TT` y excluirán `ID_CLASE=TT`.

Los porcentajes publicados no se suman. Se calcula su media cuando la pregunta
requiere comparar varios meses. Los valores absolutos, como arribos y
pernoctaciones, sí se suman después de aplicar el filtro de consolidación.

El año 2025 nunca se comparará como año completo. Las comparaciones que lo
incluyan usarán enero-junio para todos los años o lo etiquetarán como primer
semestre.

## 5. Organización del código

```text
labs/labca1/
├── data/
│   ├── archivos originales
│   └── Indicadores_ocupabilidad_2019_2025.csv
├── dataset/
│   ├── prepare_dataset.py
│   └── test_prepare_dataset.py
├── queries/
│   ├── common/
│   │   └── src/labca1/common/
│   ├── query01/
│   │   ├── README.md
│   │   ├── src/labca1/query01/
│   │   └── resultados/
│   ├── query02/ hasta query13/
│   └── query14/
├── images/
└── Plantilla201latex/
```

`common` contendrá solamente responsabilidades compartidas reales, como el
lector de registros normalizados, validación numérica, partición temporal y
cálculo de métricas. Cada consulta tendrá su propio `Driver`, `Mapper` y
`Reducer`, siguiendo el estilo del ejemplo `labs/Ventas/`.

Cada `README.md` registrará la pregunta, campos usados, filtro de filas, comando
de ejecución y archivos esperados. La documentación no sustituye la explicación
del informe.

## 6. Consultas MapReduce

### 6.1 Consultas con dos o más campos

1. **Evolución de arribos por origen.** Suma arribos nacionales y extranjeros
   por año. Distingue 2019, 2020-2021, 2022-2024 y 2025-I.
2. **Ocupación por departamento y año.** Calcula la media mensual de TNOH para
   cada combinación de departamento y año.
3. **Ocupación por clase de hospedaje.** Compara TNOH y TNOC por año y clase,
   usando solo los totales de cada clase.
4. **Empleo respecto a la capacidad.** Calcula empleos por cada cien
   habitaciones para cada departamento y año.
5. **Estacionalidad.** Suma arribos y pernoctaciones por mes para identificar
   los meses con mayor actividad.

### 6.2 Estadísticas, texto y extremos

6. **Medidas de TNOH.** Obtiene cantidad, media aritmética, mediana y desviación
   estándar. Se incluye mediana porque el enunciado repite los términos
   “promedio” y “media”.
7. **Búsqueda de subtexto.** Busca un texto configurable en `CLASE`,
   `CATEGORIA` y `DEPARTAMENTO`. Devuelve los registros completos coincidentes.
8. **Máximo y mínimo agrupado.** Devuelve el departamento con mayor y menor
   TNOH media para cada año.

### 6.3 Consultas con dos MapReduce enlazados

9. **Cambio de ocupación respecto a 2019.** El primer trabajo calcula la TNOH
   media por departamento y año. El segundo obtiene para cada departamento la
   diferencia en puntos porcentuales entre 2019 y 2024.
10. **Participación de arribos extranjeros.** El primer trabajo calcula el
    porcentaje mensual de arribos extranjeros. El segundo obtiene la media por
    departamento y periodo.

Los `Driver` de estas consultas crearán una ruta HDFS intermedia derivada de la
ruta final. El segundo trabajo solo comenzará si el primero termina con éxito.

## 7. Clasificación

Las consultas 11 y 12 responden la misma pregunta para que sus métricas sean
comparables: clasificar si la TNOH mensual consolidada de un departamento es
alta o baja a partir de sus demás indicadores. El umbral será la mediana de
TNOH del conjunto de entrenamiento.

Se usarán las siguientes características, sin incluir TNOH ni TNOC:

- mes;
- número de establecimientos;
- número de habitaciones;
- número de plazas-cama;
- arribos por habitación;
- pernoctaciones por plaza-cama;
- empleos por establecimiento.

La consulta 11 implementará Gaussian Naive Bayes. La consulta 12 implementará
regresión logística binaria. El entrenamiento utilizará 2019-2023. La prueba
principal utilizará 2024. El primer semestre de 2025 se reportará por separado
como validación reciente.

Cada salida incluirá matriz de confusión, accuracy, precision, recall, F1 y log
loss. Las probabilidades se limitarán a un intervalo seguro antes de calcular
log loss para evitar `log(0)`.

## 8. Regresión

Las consultas 13 y 14 estimarán TNOH para conservar una base de comparación:

13. **Regresión lineal.** Modelo lineal sin regularización.
14. **Regresión ridge.** Modelo lineal con regularización L2.

Ambos modelos usarán la misma partición temporal y las mismas características
que la clasificación. Las variables se estandarizarán usando únicamente el
conjunto de entrenamiento. Las salidas incluirán MAE, RMSE y coeficiente de
determinación R cuadrado.

El informe presentará estos modelos como una demostración sobre agregados
regionales mensuales. No se afirmará que predicen la ocupación de hoteles
individuales ni que establecen relaciones causales.

## 9. Ejecutor de trabajos

`hadoop3/scripts/run-job.sh` mantendrá compatibilidad con el uso actual y
añadirá tres capacidades:

1. El directorio de resultados por defecto será `./resultados`, relativo al
   directorio desde el que se ejecuta el script.
2. El JAR generado se copiará junto a `resultado.txt` porque el enunciado exige
   entregar ambos.
3. El runner aceptará fuentes compartidas y argumentos adicionales del trabajo
   sin obligar a duplicar el lector del dataset.

`HADOOP_RESULTS_DIR` continuará permitiendo un destino explícito. Las pruebas
existentes usarán un Docker falso y se ampliarán antes de modificar el runner.

## 10. Evidencias y monitoreo

Las imágenes se guardarán en `labs/labca1/images/` con nombres descriptivos. Se
capturarán NameNode, ResourceManager, NodeManager y JobHistory. JobHistory será
la evidencia principal de duración, tareas Map, tareas Reduce y estado final.

Como los datos ocupan pocos megabytes, varios trabajos pueden terminar antes de
abrir la interfaz. Para observar consumo durante la ejecución se usará una
consulta encadenada o un entrenamiento iterativo. Se complementarán las
capturas con una medición puntual de `docker stats`. El informe aclarará que el
entorno tiene un solo DataNode y un solo NodeManager. Las mediciones demuestran
el funcionamiento del flujo, no la escalabilidad del clúster.

## 11. Integración con el informe

`Template.tex` conservará la estructura existente. Se completarán las secciones
de entorno, dataset, preprocesamiento, metodología, consultas, modelos,
monitoreo, resultados, discusión y conclusiones.

Cada consulta seguirá la misma secuencia:

1. tipo de consulta;
2. pregunta;
3. campos y filas utilizadas;
4. solución MapReduce;
5. fragmento relevante del código;
6. input y output real;
7. interpretación y limitaciones.

La sección de preparación del dataset documentará la codificación original, el
cambio de esquema, la normalización decimal, la cabecera canónica y las reglas
de validación. Las secciones asignadas al compañero permanecerán claramente
separadas para evitar conflictos al integrar contenido.

## 12. Estrategia de pruebas

Se aplicará desarrollo guiado por pruebas:

- pruebas unitarias del consolidador antes de escribir su implementación;
- pruebas del runner con Docker simulado antes de cambiar sus rutas y exportar
  el JAR;
- pruebas deterministas de parseo, estadística y métricas con conjuntos
  pequeños de datos;
- comparación de las salidas MapReduce con resultados de referencia calculados
  sobre muestras controladas;
- validación estructural del CSV consolidado;
- inspección de los outputs reales después de ejecutar Hadoop;
- compilación de LaTeX y revisión visual del PDF final cuando el contenido esté
  completo.

No se aceptará una consulta solo porque termine con `_SUCCESS`. Su salida debe
coincidir con una referencia conocida y respetar los filtros de agregación.

## 13. Riesgos y mitigaciones

- **Doble conteo:** se mitiga centralizando los filtros `TT/TT` y documentando
  cuándo se usan totales por clase.
- **Comparación injusta de 2025:** se mitiga separando el primer semestre o
  comparando enero-junio en todos los años.
- **Fuga de información en ML:** se excluyen TNOH y TNOC de las características
  y se ajusta la normalización solo con entrenamiento.
- **Métricas infladas por partición aleatoria:** se usa una prueba temporal en
  2024, no una mezcla aleatoria de meses de todos los años.
- **Jobs demasiado rápidos para monitoreo:** se captura JobHistory y se elige un
  trabajo de varias etapas para la evidencia en vivo.
- **Resultados no reproducibles:** cada consulta documenta un único comando y
  conserva su JAR, input identificado y output exportado.

## 14. Criterios de aceptación

El cambio estará listo cuando:

- el CSV consolidado tenga 38 730 registros válidos;
- existan catorce consultas separadas y documentadas;
- las consultas 9 y 10 ejecuten dos trabajos MapReduce enlazados;
- clasificación y regresión produzcan métricas sobre 2024 y 2025-I;
- cada consulta exporte `resultado.txt` y su JAR en su carpeta;
- las capturas muestren los cuatro servicios de Hadoop y al menos un trabajo;
- el informe contenga inputs, outputs, interpretación y limitaciones reales;
- las secciones asignadas al compañero no hayan sido inventadas ni sobrescritas.
