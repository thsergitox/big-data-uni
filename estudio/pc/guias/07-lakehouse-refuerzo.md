# 07. Lakehouse: arquitectura, gestión y analíticas avanzadas

## Fuente y advertencia de numeración

Fuente: [Lakehouse: Una nueva generación de plataformas abiertas que unifican el almacenamiento de datos y las analíticas avanzadas](/home/sergi/Documents/semesters/nine/bigdata/lessons/Lakehouse%20Andy%20Aguilar.pdf), de Aguilar Coronel Andy Franco. **Es un artículo de 6 páginas, no una presentación de 18 diapositivas.** El abstract indica que está basado en el artículo de CIDR 2021. Esta guía trabaja únicamente con el documento disponible en `lessons`; no incorpora el artículo externo.

**Plancha Sustitutorio P3:** la referencia a «diapositiva 18 de refuerzo» no coincide con este archivo. El tema arquitectura Lakehouse sí está respaldado por su sección 3, pp. 3-4. Por tanto, puede prepararse una respuesta **con cobertura parcial respecto de la referencia de la plancha**, pero no asegurar que reproduzca el dibujo exacto de aquella diapositiva no disponible.

Los datos históricos y las afirmaciones sobre productos pertenecen al documento; no son verificaciones de las versiones actuales. BI significa inteligencia de negocios; ML, aprendizaje automático; API, interfaz de programación; SQL, lenguaje de consulta; ETL, extracción, transformación y carga; ELT, extracción, carga y transformación.

## 1. Mapa completo de lectura

| Página PDF | Secciones y temas |
|---|---|
| 1 | Abstract e introducción: primera y segunda generación de plataformas |
| 2 | Arquitectura de dos niveles, cuatro problemas, soluciones Lakehouse y desafíos del Data Warehousing |
| 3 | Continuación de desafíos; definición de Lakehouse; implementación y separación de almacenamiento/cómputo |
| 4 | Capas de metadatos; gestión transaccional; rendimiento SQL y optimizaciones |
| 5 | Acceso para analítica avanzada; alternativas; preguntas de investigación e implicaciones |
| 6 | Conclusión y expectativa de simplificación arquitectónica |

## 2. Evolución: por qué apareció la arquitectura de dos niveles

**Referencia: pp. 1-2.** La primera generación centralizaba datos procedentes de bases operacionales para apoyar decisiones y BI. El esquema se aplicaba al escribir: se organizaba el dato antes de consumirlo analíticamente. El artículo identifica dos límites:

1. Cómputo y almacenamiento se combinaban en un equipo local. Se aprovisionaban y pagaban recursos para la carga máxima, lo que se encarecía al crecer los datos.
2. Crecían los conjuntos no estructurados, como video, audio y texto, que no encajaban en ese Warehouse.

La segunda generación volcó datos sin procesar al Lake. Se apoyó en almacenamiento económico con API de archivos y formatos generalmente abiertos, como Parquet y ORC; inicialmente, con Hadoop y HDFS, su sistema distribuido de archivos. El esquema se aplicaba al leer, ofreciendo flexibilidad de almacenamiento, pero **aplazando** la administración y calidad.

Solo un subconjunto del Lake se transformaba hacia el Warehouse para BI y soporte de decisiones. Otros motores podían acceder directamente a los archivos del Lake. El texto señala que desde 2015 los Lakes en la nube comenzaron a reemplazar HDFS en este escenario, destacando durabilidad, replicación geográfica y bajo costo. Eso describe la evolución expuesta en el documento, no la eliminación universal de HDFS.

```text
Primera generación
Datos operacionales -> ETL -> Warehouse -> BI

Segunda generación / dos niveles
Datos operacionales -> Lake -> ETL -> Warehouse -> BI
                         |
                         +-> otros motores, incluida analítica avanzada
```

Diagrama didáctico basado en la introducción; no es una figura original del artículo.

**Concepto clave:** incorporar el Lake amplía el almacenamiento y el acceso, pero una segunda transferencia hacia el Warehouse agrega demoras, complejidad y nuevos modos de fallo.

## 3. Cuatro problemas de la arquitectura actual del artículo

**Referencia: p. 2.** No confundir esta lista con las cuatro categorías de desafíos de la presentación LakeVsLakeHouse: se relacionan, pero **no tienen los mismos nombres ni el mismo agrupamiento**.

### 3.1 Confiabilidad

Mantener consistencia entre Lake y Warehouse es difícil y costoso. Se requiere ingeniería continua para ETL; cada paso puede fallar o introducir errores, incluso por diferencias sutiles entre motores. El problema no se limita a que un archivo exista: es que las distintas representaciones mantengan datos confiables.

### 3.2 Obsolescencia de datos

Los datos del Warehouse pueden ir por detrás de los del Lake. Los trabajos periódicos y las áreas de preparación demoran su disponibilidad. El artículo reproduce una encuesta: 86% de analistas usan datos desactualizados y 62% espera recursos de ingeniería varias veces al mes. **Son cifras citadas por el texto, no una encuesta realizada o actualizada para esta guía.**

### 3.3 Soporte limitado para análisis avanzados

Las cargas ML no se parecen a una consulta BI que extrae pocos datos. Necesitan leer conjuntos grandes con código no SQL. El artículo menciona TensorFlow, PyTorch y XGBoost y cuestiona el acceso mediante ODBC/JDBC y la falta de acceso directo a formatos internos propietarios. ODBC/JDBC aparecen como interfaces de acceso a bases de datos; no se desarrolla su implementación.

### 3.4 Costo total de propiedad

Además del ETL continuo, las copias generan almacenamiento duplicado. Los formatos propietarios aumentan costos de migración. No se trata solo del precio de una consulta, sino también de mantener el conjunto de sistemas y sus transferencias.

**Ejemplo propio:** si una actualización existe hoy en el Lake pero el Warehouse se actualiza después, un análisis y un informe pueden consultar versiones distintas. Aumentar servidores no elimina por sí solo esa separación temporal.

## 4. Desafíos de Data Warehousing y respuestas esperadas

**Referencias: pp. 2-3.** El artículo amplía cuatro necesidades: calidad/confiabilidad de canalizaciones, datos actualizados, manejo de datos no estructurados y soporte adecuado para ciencia de datos/ML. Sistemas de atención al cliente y recomendación son ejemplos de aplicaciones afectadas por datos obsoletos.

Frente a ello, la p. 2 presenta tres respuestas del Lakehouse:

- **Gestión confiable:** conservar datos brutos y admitir ETL/ELT que los curen para análisis.
- **Soporte ML y ciencia de datos:** aprovechar la lectura directa de formatos del Lake.
- **Rendimiento SQL:** lograr consultas competitivas sobre grandes conjuntos Parquet/ORC.

La arquitectura unificada **no elimina la preparación de los datos**. Cambia dónde se administran y usan; el artículo sigue considerando necesaria su curación.

## 5. Definición y arquitectura Lakehouse

**Referencia principal: p. 3, sección 3.** Lakehouse es un sistema de gestión sobre almacenamiento económico y de acceso directo que incorpora capacidades tradicionales de un sistema analítico de bases de datos:

- transacciones ACID;
- versionado;
- auditoría;
- indexación;
- caché;
- optimización de consultas.

ACID es la denominación de propiedades transaccionales; el artículo la usa como capacidad y no desarrolla sus propiedades una por una. Para responder con el alcance del material basta explicar que la capa transaccional añade garantías de gestión, no atribuir al documento una lección detallada de teoría de transacciones que no contiene.

La combinación buscada es **almacenamiento económico, abierto y accesible** del Lake, más **gestión y desempeño** del Warehouse. El artículo advierte que el acceso directo implica renunciar a ciertos aspectos de la independencia de datos: los consumidores conocen el formato que leen, por lo que no todo queda oculto tras una interfaz de consulta.

### 5.1 Separación de almacenamiento y cómputo

En nube, distintas aplicaciones pueden ejecutarse bajo demanda en nodos independientes y acceder a los mismos datos; el texto usa como ejemplo un clúster GPU para ML. También señala que un Lakehouse puede implementarse sobre almacenamiento local como HDFS. **Nube es un entorno adecuado, no una condición obligatoria de la definición.**

### 5.2 Implementación por capas

**Referencia: p. 3, sección 3.1.** La idea central es mantener datos en almacenamiento de objetos económico, por ejemplo S3, con formato estándar como Parquet, y añadir metadatos transaccionales que indiquen **qué objetos pertenecen a una versión de una tabla**.

```text
        BI / consultas SQL              ciencia de datos / ML
                |                                  |
      motor con optimizaciones          bibliotecas y API DataFrame
                |                                  |
          metadatos: tablas, versiones, gestión transaccional
                |                                  |
       almacenamiento económico con archivos en formato abierto
                      (por ejemplo, Parquet)
```

Diagrama conceptual elaborado a partir de pp. 3-5. No reproduce una «diapositiva 18» ni implica que todo lector deba pasar por un único motor: el artículo permite lectura directa de archivos después de identificar la versión pertinente.

**Ejemplo propio:** una tabla tiene una versión compuesta por los archivos A y B. Un proceso consulta los metadatos para identificar esos archivos y los entrega a una biblioteca ML. La lógica ejemplifica la propuesta del texto sin introducir protocolos adicionales.

## 6. Metadatos: no son solo un catálogo de nombres

**Referencia: p. 4, sección 3.2.** La capa eleva el nivel de abstracción desde archivos/objetos hacia tablas, versiones y capacidades transaccionales. El artículo contrasta almacenamiento básico S3/HDFS con capas como Hive ACID.

Ejemplos del documento:

- **Delta Lake:** mantiene en el propio Lake un registro transaccional de los objetos que integran una tabla. El texto atribuye a este enfoque escalabilidad a miles de millones de objetos por tabla.
- **Apache Hudi:** se presenta como un sistema iniciado en Uber, orientado a simplificar la ingesta de streaming. El artículo afirma que no admite escritura concurrente en el contexto que describe; **no extender esa afirmación a versiones actuales**.

El documento indica que estas capas pueden agregar transacciones, clonación sin copia y acceso a versiones previas, con rendimiento similar o mejor al Lake sin procesar. También son un lugar natural para controles de calidad.

**Distinción fundamental:** la capa de metadatos permite saber qué leer y administrar versiones, pero **por sí sola no garantiza buen rendimiento SQL**. El artículo trata el desempeño como otro problema que exige optimizaciones.

## 7. Rendimiento SQL sin cerrar los formatos

**Referencias: pp. 3-4, sección 3.3.** Un Warehouse puede combinar almacenamiento rápido, estadísticas, índices y optimizaciones del formato y motor. En Lakehouse, si se conserva un formato estándar, algunas decisiones están limitadas. El artículo propone otras optimizaciones que no requieren cambiar los archivos base:

| Técnica | Qué aporta según el texto |
|---|---|
| Caché | Archivos del almacenamiento de objetos se mantienen en SSD o RAM de nodos de cómputo; las transacciones determinan si siguen siendo válidos |
| Datos auxiliares | Índices, estadísticas u otros datos optimizan consultas sin impedir acceso directo al formato base |
| Distribución/disposición | El agrupamiento y orden de registros influye en qué datos se leen juntos y en el desempeño |

SSD es almacenamiento de estado sólido; RAM es memoria principal. El texto también propone como posibilidades futuras distintos órdenes de columnas y estrategias de compresión para grupos de registros; no los presenta como requisitos obligatorios.

La sección de resultados describe combinar las tres optimizaciones con Delta Engine, un motor C++ para Spark, y atribuye rendimiento comparable o mejor a menor costo. **Es una afirmación del artículo, no un benchmark reproducido en esta guía.** Como dirección futura propone nuevos formatos que faciliten distribución, índices o adaptación al hardware.

**Ejemplo propio:** acelerar acceso a archivos frecuentes con caché no obliga a reemplazar el formato abierto de los archivos originales. La función de aceleración y la de interoperabilidad pueden coexistir.

## 8. Analítica avanzada y API DataFrame

**Referencias: pp. 3 y 5, sección 3.4.** Muchas bibliotecas ML ya leen formatos de Lake. La integración más simple consiste en consultar metadatos para determinar archivos de una tabla y entregarlos a la biblioteca.

El reto es que gran parte de la analítica avanzada está escrita en código imperativo no ejecutable como SQL. El artículo propone versiones declarativas de API DataFrame: operaciones de preparación se traducen a planes Spark SQL y aprovechan optimizaciones de Delta Lake/Delta Engine.

El texto menciona caché, omisión de datos y disposición de datos en esa integración. También cita `tf.data` como otra interfaz de acceso que no intenta trasladar semántica de consulta al almacenamiento subyacente. **No todas las APIs consiguen o persiguen la misma integración.**

Como trabajo futuro aparecen ML factorizado, traslado de lógica ML hacia uniones SQL y otras optimizaciones, y necesidad de interfaces estándar para aprovechar la gestión de datos. Son direcciones de investigación, no características garantizadas de todo Lakehouse.

## 9. Preguntas de investigación e implicaciones

**Referencia: p. 5, sección 4.** El artículo no presenta Lakehouse como un problema totalmente cerrado. Formula alternativas y asuntos pendientes:

1. **¿Otro camino hacia los mismos objetivos?** Una capa de lectura masivamente paralela sobre un Warehouse podría servir analítica avanzada. Los autores consideran que sería más costosa, difícil de administrar y menos eficiente que acceder directamente al almacenamiento de objetos; indican no haber visto una implementación generalizada. Esa evaluación es del artículo.
2. **¿Qué formatos e interfaces convienen?** Se debe coordinar formato bruto, bibliotecas lectoras e interfaz SQL. La distribución de funcionalidades entre capas permanece abierta, incluyendo lectores con decodificación más sofisticada.
3. **¿Cómo afecta a otros trabajos de gestión de datos?** El acceso común puede cambiar el diseño de otras herramientas.

Implicaciones mencionadas:

- **Polystores:** consultan datos de motores distintos; algunas consultas podrían ejecutarse sobre objetos accesibles directamente, aunque los Lakes/Lakehouses sean lógicamente separados.
- **Integración y limpieza:** pueden operar sobre el Lakehouse con acceso paralelo amplio.
- **Gestión para ML:** versionado y almacenes de características podrían reutilizar capacidades de gestión de datos en vez de reimplementarlas.
- **DBMS nativos de nube y motores sin servidor:** deberían integrarse con metadatos más ricos, en lugar de limitarse a escanear archivos brutos. DBMS significa sistema gestor de bases de datos.

**Tradeoff desarrollado por el material:** formato abierto y acceso directo facilitan múltiples consumidores; un formato totalmente controlado por el motor permitiría otras optimizaciones. El artículo propone caché, auxiliares y disposición para conciliar apertura y desempeño, sin asegurar que toda tensión desaparezca.

## 10. Conclusión del artículo

**Referencia: p. 6.** La tesis final es que una plataforma unificada puede proporcionar funcionalidad de Warehouse sobre formatos abiertos del Lake, alcanzar desempeño competitivo y atender desafíos de usuarios. Aunque el formato estándar parece limitar optimizaciones, caché y disposición permiten compensar parte de esa limitación.

La convergencia futura de la industria hacia Lakehouse es **una expectativa defendida por los autores**, apoyada en los datos ya almacenados en Lakes y en la posibilidad de simplificar arquitecturas; no es un hecho demostrado por el documento.

## 11. Confusiones frecuentes

- **Lakehouse elimina ETL/ELT:** falso según p. 2; sigue admitiendo procesos que curan datos.
- **Metadatos transaccionales = rendimiento SQL resuelto:** p. 3 separa explícitamente los problemas.
- **Lakehouse exige nube:** p. 3 admite HDFS local.
- **Acceso directo = desconocer versiones:** la biblioteca debe recibir archivos que corresponden a la tabla/versión apropiada.
- **API DataFrame = cualquier código imperativo se optimiza automáticamente:** la propuesta traduce operaciones declarativas de preparación; no promete optimización universal.
- **Pregunta de investigación = capacidad ya implantada:** p. 5 distingue alternativas y futuras direcciones.
- **Refuerzo p. 18 disponible:** el archivo tiene seis páginas. No inventar una cita ni una figura.

## 12. Preguntas tipo examen

1. Compare la primera y segunda generación de plataformas analíticas.
2. Identifique los cuatro problemas de la arquitectura de dos niveles y explique sus causas.
3. **Tipo Sustitutorio P3, cobertura parcial:** describa y dibuje la arquitectura Lakehouse respaldada por el artículo.
4. ¿Cómo permite la capa transaccional conservar acceso directo a formatos abiertos?
5. Explique tres optimizaciones de rendimiento SQL sin reemplazar el formato base.
6. ¿Por qué leer datos para ML mediante un Warehouse no equivale a leer archivos directamente?
7. Explique la integración declarativa DataFrame y una limitación del planteamiento.
8. Presente una alternativa a Lakehouse y sus tradeoffs según el artículo.
9. Explique dos implicaciones para otras áreas de gestión de datos y distinga propuesta de hecho.

## 13. Respuestas modelo separadas

### 1. Generaciones

La primera transforma datos operacionales hacia un Warehouse centralizado con esquema al escribir; afronta costos de aprovisionamiento conjunto y limitaciones con datos no estructurados. La segunda deposita datos brutos en almacenamiento económico, generalmente abierto, aplica esquema al leer y transforma un subconjunto al Warehouse. Gana flexibilidad, pero añade una segunda etapa y aplaza calidad/administración. **Fuentes: pp. 1-2.**

### 2. Cuatro problemas

Confiabilidad: mantener Lake y Warehouse consistentes exige ETL continuo y arriesga errores. Obsolescencia: el Warehouse se actualiza después y los trabajos periódicos añaden espera. Análisis avanzado limitado: ML necesita muchos datos y acceso no SQL, mientras los formatos internos no son accesibles directamente. Costo total: ETL, copias y dependencia de formatos propietarios aumentan operación y migración. **Fuente: p. 2.**

### 3. Arquitectura Lakehouse

Mantiene archivos de formato abierto en almacenamiento económico y directo. Encima se sitúan metadatos transaccionales que identifican objetos de cada versión de tabla y aportan gestión como transacciones y versionado. SQL usa optimizaciones de caché, auxiliares y disposición; ML puede identificar archivos mediante metadatos y leerlos directamente o usar API DataFrame. Cómputo y almacenamiento pueden separarse. **Fuentes: pp. 3-5. Advertencia: el artículo no contiene la diapositiva 18 indicada por la plancha; no se afirma equivalencia con su dibujo exacto.**

### 4. Metadatos y apertura

La información de tabla/versión se administra en una capa sobre archivos estándar. Así se agregan garantías de gestión sin que la mayor parte de los datos deje de estar en archivos económicos y accesibles a otros sistemas. Delta Lake es el ejemplo del registro de objetos de tabla en el Lake. **Fuentes: pp. 3-4.**

### 5. Optimización SQL

Caché guarda datos frecuentes en SSD/RAM y usa la información transaccional para validar lecturas. Datos auxiliares como índices y estadísticas mejoran consultas sin cerrar el formato base. La disposición de registros agrupa lo que conviene leer conjuntamente. Son tres caminos de optimización compatibles con archivos abiertos. **Fuente: p. 4.**

### 6. Acceso para ML

Una consulta BI suele extraer pocos resultados, mientras ML procesa conjuntos grandes mediante código no SQL. El artículo considera ineficiente obtenerlos por ODBC/JDBC y destaca que los formatos internos del Warehouse no son accesibles directamente. Los formatos del Lake permiten que bibliotecas lectoras accedan a los archivos identificados por los metadatos. **Fuentes: pp. 2-3.**

### 7. DataFrame declarativo

Operaciones de preparación se convierten en planes Spark SQL y aprovechan optimizaciones. No se afirma que todo código imperativo pueda trasladarse a SQL ni que todas las APIs empujen su semántica al almacenamiento; `tf.data` es citado como enfoque diferente. **Fuente: p. 5.**

### 8. Alternativa

Una capa de lectura masivamente paralela sobre Warehouse podría atender ML. Según la valoración del artículo, implica infraestructura más costosa y compleja y potencialmente menos eficiente que lectura directa del almacenamiento de objetos. Es una alternativa de investigación, no un sistema generalizado probado en el texto. **Fuente: p. 5.**

### 9. Implicaciones

Las herramientas de integración/limpieza podrían aprovechar acceso paralelo al Lakehouse. La gestión de ML podría reutilizar versionado y funciones de bases de datos en vez de duplicarlas en almacenes específicos. El artículo las plantea como implicaciones y oportunidades, no como resultados garantizados de toda implementación. **Fuente: p. 5.**

## 14. Repaso final

Para arquitectura, sigue el orden **archivos abiertos -> metadatos de tabla/versiones -> gestión -> optimizaciones -> consumidores SQL/ML**. Para preguntas explicativas, conecta cada decisión con confiabilidad, actualidad, acceso avanzado o costo. Mantén separadas las capacidades descritas, las evaluaciones del artículo y sus preguntas abiertas.
