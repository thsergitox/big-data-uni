# Marco de big data, machine learning y arquitectura

Definiciones de referencia, taxonomía de fuentes de datos y anclajes técnicos.
Incluye las referencias fundacionales, que **no son antecedentes históricos** sino
la base canónica del área.

## 1. Definición institucional de turismo big data

### `adb_unwto_bigdata2021` · Tier 1 · ventana extendida 2021

**Asian Development Bank & World Tourism Organization (2021).** *Big Data for
Better Tourism Policy, Management, and Sustainable Recovery from COVID-19*. ADB,
Manila.

- **Qué aporta:** definición institucional de "turismo big data"; cómo lo están
  usando los gobiernos; desafíos (privacidad, brechas de habilidades, fiabilidad del
  dato, gobernanza, infraestructura, brecha digital); y un **apéndice específico de
  competencias** requeridas para la recolección y el análisis.
- **Conclusión central:** los datos masivos complementan, sin reemplazar, las
  estadísticas oficiales.
- **Uso en el informe:** la definición de referencia y el aval de nivel OMT.

### `fao_unwto_mountain2023` · Tier 2

**FAO & UNWTO (2023).** *Understanding and Quantifying Mountain Tourism*.
FAO/UNWTO, Roma/Madrid.

Recomienda explícitamente mejorar las estadísticas oficiales de turismo mediante
big data y nuevas tecnologías. Respaldo institucional internacional a la dirección
del informe.

### `unwto_bigdata_cultural2021` · Tier 2 · ventana extendida 2021

**UNWTO (2021).** *Big Data in Cultural Tourism – Building Sustainability and
Enhancing Competitiveness*. UNWTO, Madrid.

Línea base de la OMT sobre casos de uso de big data en destinos culturales y las
implicancias éticas para los responsables de política. Útil si el informe incluye
una sección de limitaciones o gobernanza de datos.

## 2. Taxonomía de fuentes de datos

### `mariani_baggio_bigdata2022` · Tier 1

**Mariani, M. & Baggio, R. (2022).** Big data and analytics in hospitality and
tourism: a systematic literature review. *International Journal of Contemporary
Hospitality Management*, 34(1), 231–278.

- **Aporte taxonómico:** distingue datos **generados por usuarios** (reseñas, redes
  sociales, búsquedas) de datos **administrados** (producidos por el Estado o por
  operadores).
- **Afirmación clave:** la literatura se ha concentrado en los primeros y ha
  desatendido los segundos, pese a su mayor fiabilidad y cobertura.
- **Uso en el informe:** ubica el dataset de ocupabilidad en la categoría que la
  propia literatura reporta como la menos explotada.

### `sancho_ml_turismo2024` · Tier 2

**Sancho Núñez, J. C., Gómez-Pulido, J. A. & Robina Ramírez, R. (2024).** Machine
learning applied to tourism: a systematic review. *WIREs Data Mining and Knowledge
Discovery*, 14(5), e1549.

Señala la **escasez de estudios basados en datos oficiales de países en
desarrollo**. Es la cita de vacío de investigación para el marco teórico.

### `nugraha_ogd_smarttourism2026` · Tier 2

**Nugraha, U., Murnawan & Darajat, Z. (2026).** Open Government Data-Based Smart
Tourism Analytics: A Conceptual Governance Framework. *Journal of Information
Systems and Informatics*, 8(4), 4731–4757.

Argumenta que los portales de datos gubernamentales son **infraestructura
analítica**, no solo repositorios. Útil para la sección "por qué usar un dataset de
la Plataforma Nacional de Datos Abiertos en lugar de extracción web".

### `ramirez_flores_bibliometria2025` · Tier 2

**Ramirez-Flores, L. A. (2025).** Emerging technologies in the Peruvian tourism
sector: a bibliometric analysis and perspectives on digital innovation. *Revista
Científica de Sistemas e Informática*, 5(2), e929.

Análisis bibliométrico de tecnologías emergentes en el turismo peruano. Sirve para
mapear qué se ha hecho en el país en tecnología turística.

## 3. Anclajes técnicos canónicos

Estas dos **no son antecedentes históricos**. Se citan en Metodología o Marco
teórico, sin marca temporal, porque son las referencias canónicas del área.

### `dean_ghemawat_mapreduce2008`

**Dean, J. & Ghemawat, S. (2008).** MapReduce: simplified data processing on large
clusters. *Communications of the ACM*, 51(1), 107–113.

Modelo de programación canónico de MapReduce. Cita obligatoria si el informe
menciona MapReduce.

### `zaharia_spark2016`

**Zaharia, M., Xin, R. S., Wendell, P., Das, T., Armbrust, M., Dave, A., Meng, X.,
Rosen, J., Venkataraman, S., Franklin, M. J., Ghodsi, A., Gonzalez, J., Shenker, S.
& Stoica, I. (2016).** Apache Spark: a unified engine for big data processing.
*Communications of the ACM*, 59(11), 56–65.

Motor de procesamiento en memoria. Relevante porque para un dataset de ~21 000
registros **Spark es más apropiado que Hadoop MapReduce puro** — argumento que
conviene desarrollar en la metodología, dado que este repositorio usa Hadoop.

### `dean_ghemawat_bigtable2008` · opcional

Chang, F., Dean, J., Ghemawat, S. et al. (2008). Bigtable: a distributed storage
system for structured data. *ACM Transactions on Computer Systems*, 26(2), 1–26.

Solo si el informe describe una capa de almacenamiento distribuido.

## 4. Arquitecturas de big data sobre datos abiertos peruanos

Los **precedentes nacionales directos**: misma plataforma, misma clase de problema,
distinto dominio.

### `viteri_hadoop_minisa2023` · Tier 1

**Viteri Gonzales, A. P. & Beltrán García, J. A. (2023).** Solución de big data para
el análisis de los datos abiertos de MINSA y CENARES para el monitoreo y control de
la emergencia sanitaria covid-19 bajo el ecosistema de Apache Hadoop y Microsoft
Azure. Tesis de grado, Universidad Privada Antenor Orrego.

- **Qué construyen:** solución basada en **Apache Hadoop** desplegada sobre
  **Microsoft Azure** para analizar datos abiertos de MINSA y CENARES y optimizar la
  compra y distribución de vacunas e implementos por región, provincia y distrito.
- **Por qué importa:** mismo portal, misma clase de problema (datos oficiales
  heterogéneos que requieren integración y limpieza), tecnología directamente
  aplicable. Cierra el argumento de que MapReduce sobre la plataforma nacional ya se
  hizo en Perú; lo que falta es hacerlo sobre turismo.
- <https://hdl.handle.net/20.500.12759/10283>

### `gomez_arquitectura_bigdata2023` · Tier 2

**Gomez Figueroa, I. F. & Urcia Farroñay, L. A. (2023).** Modelo de una arquitectura
big data para mejorar el análisis de datos abiertos del MINSA de casos covid-19 en
el Perú. Tesis de grado, Universidad Tecnológica del Perú.

- **Método:** arquitectura fundamentada en el proceso **KDD** (Knowledge Discovery
  in Databases).
- **Métricas que evalúa:** eficiencia en el procesamiento, calidad de los registros,
  precisión de los análisis y agilidad en la generación de informes.
- **Uso en el informe:** el marco de evaluación reutilizable para las métricas del
  pipeline propio.
- <https://hdl.handle.net/20.500.12867/8171>

## 5. Contraste regional

### `mendoza_bigdata_manta2024` · Tier 2

**Mendoza Mendoza, B. E., Zambrano Alcívar, J. L., Jama Cornejo, F. V. & Bravo
Terán, N. O. (2024).** Modelo de gestión Big data turístico, caso Manta, Ecuador.
*Revista Internacional de Gestión, Innovación y Sostenibilidad Turística*, 4(1),
31–43.

- **Diagnóstico:** escasez de datos abiertos turísticos en destinos
  latinoamericanos.
- **Propuesta:** modelo de gestión en tres fases — recolección, integración mediante
  ETL, mejora continua — con indicadores de Destino Turístico Inteligente.
- **Uso en el informe:** el contraste regional perfecto. En el Perú el dataset de
  ocupabilidad **sí existe y es abierto**, lo que convierte el análisis en un caso de
  aprovechamiento de un recurso que en la región normalmente falta.

## 6. Comparación de modelos de pronóstico

### `yanez_series_ml2026` · Tier 2

**Yáñez, M., Inca, C., Coronel, F. & Mena, A. (2026).** Time Series Models and
Machine Learning for Predicting Tourism Demand in Emerging Destinations. *WSEAS
Transactions on Computer Research*, 14, 471.

Confirma que la elección del método depende de la estructura de la serie, no de la
sofisticación del algoritmo.

## 7. Fuentes descartadas

Se descartaron por no aportar al caso peruano ni al argumento del informe. Siguen en
`referencias.bib` si se necesita el ancla metodológica de ML aplicado a ocupación
hotelera:

| Fuente | Motivo |
| --- | --- |
| Contessi et al. (2024), *IJHM* | Pronóstico hotelero internacional genérico |
| Anshori et al. (2026), *JRPM* | Ídem |
| Dowlut & Gobin-Rahimbux (2023), *Heliyon* | Ídem |
| Ampountolas & Legg (2021), *IJCHM* | Ídem |
| Stylos et al. (2021), *IJCHM* | Periférico (agilidad analítica) |
| Bermúdez-Tapia (2025), *Chornancap* | Periférico (gobernanza y conflictos) |
| Schrader et al. (2025), *Frontiers in AI* | Encuestas propias, no el dataset |
| Brañes et al. (2025), *Sustainability* | Solo cita una cifra de MINCETUR para justificar su corte geográfico |
