# 09. Información, conocimiento e inteligencia de negocios

## Fuente y prioridad de examen

Fuente exclusiva: [05 C_BI_v11.pdf](/home/sergi/Documents/semesters/nine/bigdata/lessons/05%20C_BI_v11.pdf), **41 páginas físicas**. «p.» significa página física contando la portada. Se verificaron visualmente la pirámide de p. 6, la comparación gráfica de p. 13 y la tabla de plazos de p. 18. La p. 41 es una lámina de cierre.

**Prioridad de planchas:** graficar los niveles de uso de datos, **p. 6** (Parcial P4); comparar usos, técnicas, tecnologías y conocimiento obtenido por plazo, **p. 18** (Sustitutorio P2). Aprender la correspondencia completa, no solamente OLTP/OLAP/minería.

## 1. Datos, información y conocimiento

Las figuras muestran una progresión: **datos → información → conocimiento → entendimiento → sabiduría**. Las pp. 2 y 4 presentan variantes: datos por percepción sensorial o captura de sensores; información al entender relaciones o relacionar datos; conocimiento al reconocer/buscar patrones; entendimiento al comprender principios; sabiduría como crítica del entendimiento. **Fuente: pp. 2 y 4.**

El conocimiento se caracteriza como aquello que permite tomar decisiones, responde «¿cómo?» y «¿cuándo tomar una decisión?», y representa información útil y experiencia adquirida. La lámina enfatiza su carácter **principalmente humano, basado en experiencia y personal**. Por ello, no debe confundirse un volumen grande de datos con conocimiento disponible para decidir. **Fuente: p. 3.**

**Diagrama de síntesis basado en pp. 2-4:**

```text
Datos -> relaciones -> Información -> patrones -> Conocimiento
                                                    |
                                             principios
                                                    v
                                             Entendimiento
                                                    |
                                        crítica del entendimiento
                                                    v
                                                Sabiduría
```

**Ejemplo, elaboración propia:** registros de ventas son datos; relacionarlos para comparar periodos produce información; descubrir patrones útiles para decidir se aproxima al conocimiento definido por el curso. El ejemplo ilustra el sentido del diagrama, sin afirmar que una agregación automática produzca por sí sola sabiduría.

## 2. Niveles de uso de los datos: pirámide que debe poder dibujarse

**Fuente principal: p. 6.** Se conserva la nomenclatura exacta de la figura, que no coincide simplemente con nombrar «operacional, táctico y estratégico».

```text
                      / Nivel Estratégico \
                     /     Data Mining     \
                    / planificación y futuro\
                   /-------------------------\
                  / Nivel de Conocimientos    \
                 /      Data Warehousing       \
                / análisis, control, históricos \
               /---------------------------------\
              /       Nivel Administrativo        \
             /      Sistemas de Información         \
            / repetición, captura, tareas predefinidas\
           /-------------------------------------------\
          /             Nivel Operacional               \
         /                     BD                        \
        /     actividades y transacciones elementales     \
       /___________________________________________________\

Dos niveles superiores: Analítico / Business Intelligence
Dos niveles inferiores: Transaccional / Sistemas OLTP
Hacia arriba: mayor valor; la base representa mayor volumen.
```

*Reconstrucción textual de la figura de p. 6, no una pirámide tomada de otra fuente.*

| Nivel exacto del gráfico | Soporte indicado | Uso indicado |
|---|---|---|
| Operacional | BD | Actividades y transacciones elementales de BD. |
| Administrativo | Sistemas de Información | Operaciones repetitivas, captura de datos básicos, tareas predefinidas. |
| De Conocimientos | Data Warehousing | Análisis, seguimiento y control; toma de decisiones; consulta de históricos. |
| Estratégico | Data Mining | Planificación empresarial, administrativa y a largo plazo; mira al futuro. |

La figura agrupa los dos inferiores como **transaccionales**, y los dos superiores como **analíticos**. Además, una pequeña pirámide representa Data, Information, Knowledge y Decision junto a la relación valor/volumen. Para el examen, es importante incluir ambos bloques y explicar qué cambia hacia arriba: de registrar transacciones a analizar y planificar. No reemplazar los nombres originales por una taxonomía distinta.

## 3. Sistemas operacionales y OLTP

Una BD operacional está orientada a la aplicación, usa estructuras normalizadas, contiene operaciones, almacena el máximo detalle, se actualiza en línea y cambia constantemente. El material vincula normalización con integridad, minimización de espacio y rendimiento. **Fuente: p. 7.**

Los sistemas operacionales están diseñados para alto rendimiento de funcionamiento y procesamiento, pero sus estructuras pueden ser complejas, la información puede estar dispersa y pueden no ajustarse a consultas complejas. OLTP significa **On-Line Transaction Processing**, procesamiento de transacciones en línea: administrar aplicaciones transaccionales para entrada, recuperación y procesamiento de datos. Ejemplos: banca electrónica, pedidos, comercio electrónico, supermercados e industria. **Fuente: p. 8.**

**No confundir «rápido» con «óptimo para todo»:** el PDF contrasta el rendimiento de operar transacciones con el de resolver consultas analíticas complejas. Es una diferencia de objetivo, no una afirmación de que OLTP sea inútil.

## 4. Sistemas analíticos y OLAP

OLAP significa **On-Line Analytical Processing**, procesamiento analítico en línea. Se presenta como rápido para consultas complejas y basado en estructuras multidimensionales, cubos, que contienen datos resumidos de sistemas OLTP. El PDF contrapone entidades en tablas relacionales normalizadas con datos dimensionales calculados y agregados para consulta. **Fuente: p. 10.**

Características: esquema optimizado para consultas rápidas, varios niveles de datos, uso interactivo, informes complejos, vista multidimensional y facilidad para cambiar filas, columnas y páginas. No se reduce a mostrar un reporte fijo: permite explorar diferentes vistas. **Fuente: p. 11.**

### Usos de OLAP

- **Sistemas ejecutivos:** indicadores, comportamiento normal y excepciones/variaciones.
- **Finanzas:** comunicar, planear, analizar escenarios de mercado y pronosticar.
- **Ventas y marketing:** facturación, producto, cliente y ventas por región.
- **Otros:** producción, servicio al cliente y evolución del costo de producto.

**Fuente: p. 12.**

### Comparación que debe poder explicarse

| Aspecto | OLTP / operacional | OLAP / analítico |
|---|---|---|
| Objetivo | Operaciones. | Información para decidir. |
| Orientación | Aplicación. | Sujeto. |
| Vigencia | Actual. | Actual e histórica. |
| Granularidad | Detallada. | Detallada y resumida. |
| Organización | Normalizada. | Estructurada según el análisis. |
| Cambios | Continuos. | Estable. |

**Fuente: p. 14.** La p. 13 agrega una comparación gráfica: OLTP apoya el servicio diario y almacena a nivel transaccional; el reporte analítico usa información histórica integrada y diseño desnormalizado/esquema estrella. La p. 14 escribe «OLPT» como encabezado: es una errata, mientras la definición de p. 8 dice OLTP.

**Ejemplo, elaboración propia:** registrar un pedido corresponde al objetivo operacional; comparar ventas actuales e históricas por producto/región corresponde al objetivo analítico. La comparación no dice que OLAP carezca de detalle: incluye explícitamente «detallada + resumida».

## 5. Qué es BI y para qué sirve

BI es transformar **datos en información** y, mediante descubrimiento, **información en conocimiento**. Es un conjunto de técnicas y herramientas que apoya decisiones, administrando y creando conocimiento a partir del análisis de datos existentes. **Fuente: p. 16.**

```text
Dato -------- transformar --------> Información
Información ---- descubrir -------> Conocimiento
                                      |
                                toma de decisiones
```

*Síntesis basada en pp. 16-17.*

Finalidades desarrolladas:

1. Convertir grandes volúmenes en valor de negocio mediante reportes analíticos.
2. Producir información para controlar procesos, independientemente de la fuente.
3. Apoyar decisiones.
4. Distinguir información útil para los usuarios finales.
5. Uniformizar términos de la institución, independientemente del origen y del proceso de extracción, transformación y agregación.

**Fuente: p. 17.**

## 6. BI por plazo: tabla clave del Sustitutorio

**Fuente: p. 18.** La lámina contiene dos columnas llamadas Tecnología. La primera tiene los nombres escritos; la segunda contiene imágenes (tabla, cubo y red), **no nombres adicionales de productos**. Esta reconstrucción no inventa tecnologías en esa columna.

| Plazo | Uso | Técnica escrita | Tecnología escrita | Resultado, columna «Conocimiento» |
|---|---|---|---|---|
| Corto | Gestión de datos; obtención y control. | Legacy Systems, escrito «Legacy Sistems». | OLTP: On-Line Transaction Processing. | Datos; Operativo. |
| Mediano | Decisiones tácticas. | Data Warehouse. | OLAP: On-Line Analytical Processing. | Información; Toma de Decisiones. |
| Largo | Estratégico; pronóstico. | Minería de Datos. | Agrupamiento, Clasificación, Secuenciación, Reglas de asociación. | Patrones; Nuevos Conocimientos. |

La lógica de la tabla es avanzar desde controlar operaciones, pasando por información integrada para decisiones tácticas, hasta patrones y pronóstico estratégico. **No todas las celdas de «Conocimiento» contienen conocimiento en el mismo sentido de p. 3:** la tabla diferencia literalmente datos, información y patrones/nuevos conocimientos.

**Ejemplos, elaboración propia:** corto plazo, registrar/controlar pedidos; mediano, comparar resultados integrados para una decisión táctica; largo, buscar patrones para el pronóstico. Estos ejemplos no añaden proveedores ni herramientas ajenas al cuadro. «Secuenciación» aparece en la tabla, pero no se define ni se desarrolla un algoritmo en este PDF.

## 7. Evolución histórica de la explotación de datos

La tabla de p. 19 vincula etapas, preguntas de negocio y tecnologías. Se estudia como la evolución presentada por el curso, no como cronología exhaustiva verificada externamente.

| Etapa y año del PDF | Pregunta ilustrada | Tecnología | Característica |
|---|---|---|---|
| Data Collection, 1960 | Total de ventas en Lima y Arequipa. | Computadoras, cintas y discos. | Retrospectivo, estático. |
| Data Access, 1980 | Ventas por sucursal en esas ciudades. | RDBMS SQL. | Retrospectivo, dinámico. |
| Data Navigation, 1990 | Total de Lima con Drill Down. | OLAP DW. | Retrospectivo, dinámico, varios niveles. |
| Data Mining, 2000 | Cómo evolucionarán ventas el próximo año. | Algoritmos avanzados y multiprocesadores. | Prospectivo, proactivo. |

Proveedores citados por etapa: IBM/NCR; Oracle/Informix/Sybase; Pilot/Discoverer/Arbor; Intelligent Miner (IBM)/SAS. Son **ejemplos históricos escritos en la lámina**, no recomendaciones actuales. **Fuente: p. 19.**

## 8. Disciplinas y capa semántica

- **Business Intelligence:** almacenamiento, metodologías, análisis y software para apoyar decisiones.
- **Data Warehousing:** cubos/datamarts y estructuras multidimensionales con información precalculada de combinaciones, según la descripción de la lámina.
- **Knowledge Discovery in Databases, KDD:** extracción no trivial de información implícita, desconocida y potencialmente útil.
- **Data Mining:** extracción de patrones/reglas para crear modelos que el analista no había percibido, pero existen en los datos.

**Fuente: p. 20.** El PDF no especifica una secuencia formal de fases KDD ni equipara automáticamente KDD con un algoritmo.

Rasgos comunes: información para controlar procesos, soporte de decisiones y **capa semántica**. La información debe servir al usuario en lenguaje de negocio comprensible, sin intérpretes, independientemente de fuentes y transformaciones. Uniformizar términos permite concentrarse en decidir con rapidez y seguridad. **Fuente: p. 21.**

**Ejemplo, elaboración propia:** si dos áreas usan «venta» con criterios distintos, un reporte integrado necesita términos comprensibles y uniformes; no basta juntar valores con el mismo encabezado. El ejemplo aplica el requisito semántico, no presenta un problema real documentado.

## 9. ETL e inconsistencias al pasar de OLTP a OLAP

El PDF indica que OLAP obtiene datos de OLTP y que el proceso no es trivial. Problemas potenciales: múltiples tecnologías, reportes obsoletos, ausencia de metadatos, algoritmos de cálculo distintos, extracción o detalle diferentes, nombres y significados diferentes, pérdida de información, falta de reglas de corrección y falta de capacidad de drill-down. **Fuente: pp. 22-23.**

Ejemplos concretos de transformación:

| Caso | Inconsistencia | Solución descrita |
|---|---|---|
| Codificación | M/F, 1/0, Masculino/Femenino. | Elegir una convención única y transformar. p. 24. |
| Unidades | Litros, cm³, hectolitros. | Elegir una unidad útil, como litros. p. 25. |
| Fechas | yyyy/mm/dd, mm/dd/yyyy, dd/mm/yyyy. | Adoptar un formato común, como dd/mm/yyyy. p. 26. |
| Varias columnas en una | Calle, número, piso, departamento; nombre/apellido. | Posible consolidación en una columna del DW. p. 27. |
| Una columna en varias | Tipo y número de documento juntos. | Posible separación en dos campos. p. 28. |

Las dos últimas son posibilidades de diseño, no reglas que obliguen siempre a unir o separar. La transformación debe responder a la estructura necesaria para el almacén.

## 10. Data Warehouse y datamarts

Un **Data Warehouse** es una colección de datos orientada a un ámbito —empresa, organización, área o tema—, **integrada, no volátil y variable en el tiempo**. Ayuda a decidir, va más allá de datos transaccionales/operacionales y favorece análisis/divulgación eficiente. El material presenta datamarts como unidades lógicas más pequeñas. **Fuente: pp. 30-31.**

Las características deben memorizarse con la definición. El PDF no desarrolla en una sección separada cómo se implementa cada una: «no volátil» no debe confundirse con afirmar que jamás hay procesos de carga o que no conserva tiempo.

Ventajas enumeradas: confiable y controlado; fuente única; evitar esfuerzos duplicados, conflictos de periodos y confusión de algoritmos; información de calidad; evitar disparidades de datos, significado y representación; no requerir herramientas para soportar múltiples tecnologías; ausencia de restricciones de drill-down según la lista de la lámina. **Fuente: p. 32.** Esta última es una ventaja enunciada, no garantía técnica de recuperar cualquier detalle inexistente.

### Datamart

Base departamental especializada en un área de negocio, estructurada para analizar con detalle desde perspectivas relevantes. Puede recibir datos de un DW **o integrar por sí misma varias fuentes**: no afirmar que todo datamart depende obligatoriamente de un DW. **Fuente: p. 33.**

| Tipo del material | Base y organización |
|---|---|
| Datamart OLAP | Cubos con dimensiones e indicadores; creación/explotación/mantenimiento dependen de la herramienta. |
| Datamart OLTP | Extracto de un DW con mejoras de rendimiento, agregaciones y filtrados; tablas report y vistas materializadas. |

**Fuente: p. 34.** Se mantienen los nombres de la lámina sin sustituirlos por categorías externas.

## 11. Tecnología y elementos que integran un DW

El material enumera hardware, sistema operativo, BD, herramientas de consulta y aplicaciones. Añade grandes BD, arquitectura de 64 bits, indización, sistemas abiertos, herramientas robustas de DW y herramientas sofisticadas de usuario final. No prescribe marcas ni especificaciones dimensionadas. **Fuente: p. 35.**

Elementos enumerados y explicados:

| Elemento | Función según el PDF |
|---|---|
| Metadata | Datos sobre los datos; describen estructura y relaciones. |
| API | Interfaz de programación; lenguaje/formato de mensajes para activar e interactuar con funciones de otro programa o equipo. |
| Middleware | Asegura conectividad de componentes; capa que permite trabajar en distintos ambientes sin ocuparse de protocolos de red/comunicación. |
| Mecanismos de extracción | Dinámica necesaria para consultas ante grandes volúmenes operacionales/transaccionales; la lámina no detalla una implementación. |
| Mecanismos de carga | Acumulación simple y rolling, definidos en p. 40. |

**Fuente: pp. 36-40.**

### Acumulación simple y rolling

**Acumulación simple:** resume todas las transacciones del periodo seleccionado y lleva el resultado al DW como una única transacción.

**Rolling:** mantiene varios niveles de granularidad, almacenando resúmenes a niveles correspondientes a distintas agrupaciones de la unidad temporal. **Fuente: p. 40.**

**Ejemplo, elaboración propia:** acumular todas las transacciones de un periodo en un resumen ilustra la primera; conservar resúmenes diarios y mensuales ilustra diferentes agrupaciones temporales de rolling. No confundir estos mecanismos con Initial/Incremental/Full Load: esos nombres pertenecen a la guía OLAP y responden a otra clasificación.

## 12. Confusiones clave

1. **BI no es una base de datos:** es técnicas/herramientas y un proceso orientado a decisiones.
2. **OLTP no equivale a OLAP:** difieren en objetivo, historia, estructura y cambio de datos.
3. **OLAP no almacena solo resúmenes:** la comparación incluye detalle y resumen.
4. **Datos no son automáticamente conocimiento:** el material exige información útil/patrones y destaca la decisión humana.
5. **La pirámide p. 6 tiene cuatro niveles:** no omitir Administrativo ni sustituir De Conocimientos.
6. **La tabla p. 18 distingue técnica de tecnología:** no intercambiar Data Warehouse y OLAP.
7. **Un datamart no necesariamente nace de un DW:** puede integrar fuentes directamente.
8. **Metadata no son los datos de negocio mismos:** describen estructura y relaciones.
9. **Una gráfica de red no da un nombre de tecnología:** la segunda columna Tecnología de p. 18 es ilustrativa.
10. **Fuente única y lenguaje común son objetivos relevantes:** integrar no es simplemente concatenar registros.

## 13. Autoevaluación tipo plancha

Las preguntas 1 y 2 corresponden a temas indicados de las planchas; las demás son **elaboración propia**. Responder antes de ver soluciones.

1. **Parcial P4:** grafique y explique los niveles del uso de datos, incluyendo bloques transaccional/analítico.
2. **Sustitutorio P2:** explique uso, técnica, tecnología y resultado de BI a corto, mediano y largo plazo.
3. Explique datos, información y conocimiento; indique por qué el conocimiento se presenta como principalmente humano.
4. Compare sistemas OLTP y OLAP en objetivo, orientación, vigencia, granularidad, organización y cambios.
5. Defina BI y explique su finalidad y la importancia de la capa semántica.
6. Explique las etapas de evolución de colección a minería según la tabla histórica.
7. Diferencie BI, Data Warehousing, KDD y Data Mining según las definiciones de la clase.
8. Explique problemas ETL y tres transformaciones concretas del material.
9. Defina DW y datamart; distinga los dos tipos de datamart presentados.
10. Explique metadata, API, middleware, acumulación simple y rolling.

---

## 14. Respuestas modelo

### 1. Niveles de datos

Dibujar cuatro niveles de base a cima: **Operacional → Administrativo → De Conocimientos → Estratégico**. Operacional usa BD para actividades/transacciones elementales; Administrativo usa sistemas de información para operaciones repetitivas/captura/tareas predefinidas; De Conocimientos usa Data Warehousing para análisis, control, decisiones e históricos; Estratégico usa Data Mining para planificación y futuro. Los dos inferiores son transaccionales/Sistemas OLTP; los superiores analíticos/BI. Hacia arriba se representa mayor valor frente al mayor volumen de la base. **Fuente: p. 6.**

### 2. Plazos

Corto: gestión, obtención/control; Legacy Systems; OLTP; datos operativos. Mediano: decisiones tácticas; Data Warehouse; OLAP; información para tomar decisiones. Largo: estrategia/pronóstico; minería; agrupamiento, clasificación, secuenciación y reglas de asociación; patrones/nuevos conocimientos. La segunda columna Tecnología contiene imágenes, no productos adicionales que deban inventarse. **Fuente: p. 18.**

### 3. De datos a conocimiento

Relacionar datos produce información; buscar o entender patrones se vincula con conocimiento. El PDF define conocimiento como información útil y experiencia que permite decidir cómo y cuándo actuar. Lo caracteriza como personal y principalmente humano. Entender principios conduce a entendimiento y su crítica se vincula con sabiduría. **Fuente: pp. 2-4.**

### 4. OLTP frente a OLAP

OLTP: objetivo operacional, orientación a aplicación, datos actuales/detallados, organización normalizada, cambio continuo. OLAP: decisiones, orientación al sujeto, actual e histórico, detalle y resumen, estructura según análisis y datos estables. OLTP optimiza funcionamiento transaccional; OLAP consultas complejas, informes e interacción multidimensional. **Fuente: pp. 7-14.**

### 5. BI y semántica

BI transforma datos en información y descubre conocimiento para decidir. Produce valor y control de procesos, distingue información útil y uniformiza términos. La capa semántica ofrece lenguaje de negocio comprensible para que usuarios decidan sin intérpretes, independientemente de fuente y transformación. **Fuente: pp. 16-17 y 21.**

### 6. Evolución

Data Collection (1960): computadoras/cintas/discos, retrospectivo estático. Data Access (1980): RDBMS SQL, retrospectivo dinámico por sucursal. Data Navigation (1990): OLAP DW y drill-down, retrospectivo dinámico multinivel. Data Mining (2000): algoritmos avanzados/multiprocesadores, pronóstico prospectivo/proactivo. Se reproduce la periodización del PDF. **Fuente: p. 19.**

### 7. Disciplinas

BI combina tecnologías/metodologías/software para decisiones. Data Warehousing ofrece estructuras multidimensionales precalculadas. KDD extrae información implícita desconocida, no trivial y potencialmente útil. Minería extrae patrones y reglas para modelos antes no percibidos. Comparten control de procesos, decisiones y lenguaje de negocio. **Fuente: pp. 20-21.**

### 8. ETL

Puede haber tecnologías, algoritmos, granularidades, nombres/significados distintos y ausencia de metadatos o reglas de corrección. Transformaciones concretas: elegir códigos comunes M/F; convertir litros/cm³/hectolitros a litros; normalizar formatos de fecha. También se permite unir varias columnas o separar un campo según necesidad del DW. **Fuente: pp. 22-28.**

### 9. Almacenes

DW: colección orientada a un ámbito, integrada, no volátil y variable en el tiempo, destinada al análisis/decisiones. Datamart: base especializada en un departamento, alimentada por DW o por integración propia. OLAP se basa en cubos con dimensiones e indicadores; OLTP, según la denominación del PDF, en extractos optimizados con tablas report/vistas materializadas. **Fuente: pp. 30-34.**

### 10. Elementos

Metadata describe datos y relaciones; API define lenguaje/formato para interactuar con funciones; middleware asegura conectividad entre componentes y ambientes. Acumulación simple resume un periodo y lo transporta como una transacción al DW; rolling conserva resúmenes de varias agrupaciones temporales/granularidades. **Fuente: pp. 36-40.**
