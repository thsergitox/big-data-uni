# 06. Data Warehouse, Data Lake y Lakehouse

## Fuente y alcance

Fuente: [SQL Analytics & Lake Architecture](/home/sergi/Documents/semesters/nine/bigdata/lessons/04%20LakeVsLakeHouse_v03ppt.pdf), **17 páginas reales del PDF**. En esta presentación se llama «diapositiva» a la página correspondiente; no hay una página 18. Se revisaron texto y figuras. Las afirmaciones sobre productos, precios o capacidades se presentan **como contenido de clase**, no como comprobaciones actuales del mercado.

**Objetivo:** explicar por qué se propone Lakehouse, cómo se organiza y qué problemas concretos pretende resolver. BI significa inteligencia de negocios; DS, ciencia de datos; ML, aprendizaje automático; ETL, extracción, transformación y carga.

## 1. Recorrido completo del material

| Páginas PDF | Contenido |
|---|---|
| 1 | Portada: análisis SQL y arquitectura del lago |
| 2 | Data Warehouse: propósito y limitaciones |
| 3 | Data Lake: amplitud de datos, dificultades de BI y fiabilidad |
| 4 | Coexistencia de Warehouse y Lake como estrategia problemática |
| 5-6 | Lakehouse: datos, capa transaccional y motor de consulta |
| 7 | Motivación: uso de datos e IA en empresas |
| 8-9 | Warehouse con exportación al Lake y sus costos |
| 10-11 | Arquitectura abierta y plataforma para múltiples usos |
| 12 | Transición a los problemas del Lake |
| 13 | Diez desafíos agrupados en cuatro categorías |
| 14 | Organización Bronze, Silver y Gold |
| 15 | Ejemplo de Lakehouse mediante Databricks |
| 16 | Gráfico de relación precio/rendimiento |
| 17 | SQL Analytics: SQL, conectores, gobernanza y rendimiento |

## 2. Data Warehouse: una solución orientada a BI e informes

**Referencia: p. 2.** El Warehouse se presenta como un almacén creado específicamente para análisis de negocios e informes. Su figura muestra datos externos y operacionales que atraviesan una transformación antes de alimentar el almacén; de este salen BI y reportes.

La lámina señala cuatro limitaciones: no admite video, audio ni texto; no da soporte a ciencia de datos y ML; tiene soporte limitado para streaming; y utiliza formatos cerrados y propietarios. Estas limitaciones explican por qué la presentación afirma que muchos datos terminan en Lakes y almacenamiento de objetos. **No se debe convertir esta caracterización de clase en una afirmación universal sobre todos los productos existentes.**

La idea de fondo no es que BI deje de importar, sino que una arquitectura pensada para informes no necesariamente cubre todas las formas de datos y procesamiento que necesita la organización.

**Ejemplo didáctico propio:** una empresa consulta ventas por mes para sus informes, pero también quiere conservar grabaciones de atención y analizarlas. Según la comparación del material, el Warehouse cubre el primer propósito, mientras que los archivos no estructurados motivan recurrir a un Lake.

## 3. Data Lake: más tipos de datos no significa más confiabilidad

**Referencia: p. 3.** El lago acepta datos estructurados, semiestructurados y no estructurados. El diagrama conecta ese almacenamiento con preparación y validación para DS/ML, y con ETL hacia un Warehouse para reportes y BI. También muestra aplicaciones en tiempo real.

La presentación marca cuatro dificultades: escaso soporte para BI, configuración compleja, problemas de rendimiento y «pantanos de datos» poco fiables. Un pantano expresa el riesgo de acumular datos sin que su disponibilidad se traduzca en datos utilizables y confiables.

**Relación causa-consecuencia:** ampliar la capacidad de guardar datos resuelve un problema de almacenamiento, pero no resuelve automáticamente cómo actualizarlos, organizar sus versiones, consultarlos con buen desempeño o asegurar su calidad.

**Ejemplo propio:** conservar muchos archivos de ventas no garantiza que todos estén completos ni que sea fácil obtener un informe. El volumen almacenado y la calidad analítica son cuestiones diferentes.

## 4. Por qué coexistir no basta

**Referencias: pp. 4, 8-9.** La página 4 superpone las dos arquitecturas y declara que su coexistencia no es una estrategia deseable. Las páginas 8-9 hacen concreto un caso: datos estructurados entran al Warehouse, BI los consulta y una exportación los lleva al Lake para DS/ML y aplicaciones en tiempo real. Los datos no estructurados todavía necesitan el Lake.

Problemas expresados en las láminas:

- El Warehouse sigue siendo un sistema cerrado y propietario.
- No apoya directamente la ciencia de datos en esta caracterización.
- La operación y el costo financiero resultan elevados.
- Exportar no elimina la necesidad de almacenar en el Lake los datos no estructurados.

La coexistencia puede ampliar usos, pero mantiene dos entornos y un flujo de exportación. **La propuesta de clase es simplificar esa separación**, no afirmar que ningún diseño con ambos sistemas pueda funcionar.

```text
Arquitectura separada del material
Datos estructurados -> Warehouse -> BI / informes
                           |
                        exportación
                           v
Datos no estructurados -> Lake -> DS/ML / tiempo real

Propuesta Lakehouse
Datos de distintos tipos -> Lake + capa transaccional + motor
                                                   |
                             BI / DS / ML / streaming
```

Diagrama didáctico elaborado a partir de pp. 5-6 y 8-10; no reproduce literalmente una figura.

## 5. Lakehouse: qué aporta cada capa

**Referencias: pp. 5-6, 10-11.** No es simplemente renombrar un Lake. El esquema de la página 5 agrega una **capa transaccional**, representada por Delta Lake, sobre el lago. La página 6 añade un **motor de consulta de alto rendimiento**, Delta Engine. Por encima se sitúan streaming analytics, BI, ciencia de datos y ML.

| Elemento | Función en la propuesta de la presentación |
|---|---|
| Data Lake | Mantener datos estructurados, semiestructurados y no estructurados |
| Capa transaccional | Añadir estructura transaccional sobre esos datos |
| Motor de consulta | Mejorar el desempeño de consulta |
| Consumidores analíticos | Usar una plataforma para BI, DS, ML y streaming |

Las páginas 10-11 resumen tres beneficios buscados: formato y arquitectura abiertos sobre el lago; soporte de primera clase para análisis, tiempo real y DS/ML; datos recientes con una única fuente de verdad. «Recencia» se refiere aquí a la actualidad de los datos. Una fuente común busca evitar que cada caso de uso dependa de una copia distinta.

En p. 11, el círculo del ecosistema incluye herramientas BI, OLTP, ETL, proveedores de seguridad, proveedores de datos y Warehouses. OLTP identifica herramientas de procesamiento transaccional. La figura representa integración con un ecosistema; no desarrolla el funcionamiento interno de cada herramienta.

**Ejemplo propio:** un reporte y un proceso de ciencia de datos trabajan sobre la misma base organizada, en lugar de esperar una exportación del Warehouse hacia otro almacén. El ejemplo ilustra el objetivo de fuente común; la lámina no promete que todo retraso desaparezca.

## 6. Motivación empresarial de datos e IA

**Referencia: p. 7.** El material usa tres empresas como ejemplos: Uber para predicción de precio, oferta y demanda y viajes compartidos; Airbnb para preferencias, recomendaciones de precio y valor de vida del cliente; Netflix para sugerencias de contenido, calidad de streaming y personalización de miniaturas.

Estos casos motivan por qué la arquitectura debe admitir usos más amplios que informes. Son ejemplos mostrados en la presentación, **no estudios de implementación de esas empresas ni pruebas de que utilicen las capas descritas después**. IA significa inteligencia artificial.

## 7. Diez desafíos del Data Lake: tema directo de plancha

**Referencia: p. 13, «Desafíos de data lake».** La tabla de problemas está dentro de una imagen; leer solo el texto extraído ocultaría casi todo el detalle. Esta es la agrupación visual de la lámina:

| Categoría | Problemas de la figura | Explicación para estudiar |
|---|---|---|
| Fiabilidad | 1. Difícil añadir datos | El proceso de incorporación debe preservar datos utilizables |
| Fiabilidad | 2. Difícil modificar datos existentes | Guardar archivos no basta para administrar actualizaciones |
| Fiabilidad | 3. Trabajos fallan a medio camino | Una ejecución incompleta compromete la confiabilidad del resultado |
| Fiabilidad | 4. Operaciones en tiempo real difíciles | Mantener operaciones oportunas es un desafío del lago |
| Fiabilidad | 5. Costoso conservar versiones históricas | La historia necesita administración y recursos |
| Rendimiento | 6. Difícil manejar grandes metadatos | La organización de lo almacenado también crece |
| Rendimiento | 7. Problema de demasiados archivos | El número de archivos afecta la capacidad de operar eficientemente |
| Rendimiento | 8. Bajo rendimiento | Tener los datos disponibles no garantiza consultas rápidas |
| Gobernanza | 9. Seguridad deficiente | El control de acceso y protección forma parte del problema |
| Calidad | 10. Problemas de calidad de datos | Los datos guardados pueden no estar listos para análisis confiable |

Las explicaciones de la última columna son desarrollo didáctico de los rótulos de clase, no mecanismos adicionales descritos por la lámina.

**Cómo responder una plancha:** nombrar las cuatro categorías y desarrollar problemas concretos de la figura. Evitar responder solo «son muchos datos»: esa frase no identifica ni fiabilidad, ni gobernanza, ni calidad.

## 8. Construir un lago limpio: Bronze, Silver y Gold

**Referencia: p. 14.** La figura muestra tres etapas conectadas:

```text
BRONZE                        SILVER                         GOLD
Ingesta cruda e historial -> Filtrado, limpio, ampliado -> Agregados de negocio
```

- **Bronze:** mantiene la ingesta cruda y la historia.
- **Silver:** representa datos filtrados, limpiados y enriquecidos/ampliados.
- **Gold:** representa agregados a nivel de negocio.

No son tres algoritmos. Son estados o niveles de organización del dato en la figura.

**Ejemplo propio:** registros de ventas recibidos tal como llegan corresponden a Bronze; registros limpiados y enriquecidos, a Silver; totales de ventas por tienda y mes, a Gold. La elección de limpiar o agregar así es ilustrativa, no un ejercicio publicado en el PDF.

## 9. Ejemplo de plataforma y análisis SQL

**Referencias: pp. 15, 17.** En la p. 15, Databricks representa el Lakehouse con Delta Lake sobre datos de distintos tipos. Encima aparecen SQL Analytics, aplicaciones de tiempo real y un entorno de ciencia de datos y flujos ML. El lado BI incluye herramientas como Tableau, Qlik, Power BI, Spotfire, Looker, ThoughtSpot, Mode y MicroStrategy; son rótulos de conectividad del gráfico, no temas desarrollados individualmente.

La p. 17 explica cómo se busca que BI e informes sean usos de primera clase del lago:

1. Interfaz nativa SQL para analistas, ilustrada con `SELECT`, `FROM`, `WHERE`.
2. Conectores incorporados para herramientas BI existentes.
3. Gobernanza de grano fino.
4. Consultas de alto rendimiento.

**Qué debe entenderse:** abrir el almacenamiento no basta para servir al analista; además hacen falta una interfaz familiar, integración con herramientas, control y desempeño.

## 10. Relación precio/rendimiento: leer sin exagerar

**Referencia: p. 16.** El gráfico de la presentación está rotulado «30TB TPC-DS Price/Performance» e indica que menor es mejor. Muestra tres Warehouses anónimos con $83, $273 y $549, y Databricks Lakehouse con $59. A la derecha figura SQL Compute de $0.15/DBU.

Conclusión admisible para el examen: **la lámina presenta un ejemplo favorable de relación precio/rendimiento para Lakehouse**. No permite deducir precios actuales, superioridad en todas las cargas ni condiciones del experimento que no estén expresadas en el material. DBU aparece como unidad de la tarifa, sin explicación adicional en esta presentación.

## 11. Confusiones que debes evitar

- **Lake = Lakehouse:** falta explicar la capa transaccional y el motor de consulta.
- **Guardar todos los datos = tener datos de calidad:** p. 13 distingue expresamente calidad y fiabilidad.
- **Fiabilidad = rendimiento:** un resultado puede ser confiable sin obtenerse rápidamente; son categorías distintas en la lámina.
- **Bronze = datos listos para informes:** el material reserva agregados de negocio para Gold.
- **Una fuente de verdad = solo existe una herramienta:** p. 11 muestra varias herramientas sobre la arquitectura común.
- **El gráfico comercial prueba una ley universal:** es un ejemplo del material, no una evaluación de todo el mercado.

## 12. Preguntas de práctica tipo examen

Responde primero sin leer la sección siguiente.

1. Explique por qué el Warehouse de la presentación resulta insuficiente para todos los casos de uso.
2. ¿Qué ventajas y problemas presenta el Data Lake frente al Warehouse?
3. ¿Por qué exportar del Warehouse al Lake no elimina la complejidad arquitectónica?
4. Dibuje una arquitectura Lakehouse y explique la función de sus capas.
5. **Tipo Parcial P6:** enumere los desafíos de un Data Lake y agrúpelos como en la diapositiva 13.
6. Explique Bronze, Silver y Gold con un ejemplo propio.
7. ¿Qué capacidades de SQL Analytics permiten llevar BI al Lake?
8. ¿Qué conclusión permite y qué conclusión no permite el gráfico precio/rendimiento de p. 16?

## 13. Respuestas modelo separadas

### 1. Limitaciones del Warehouse

Fue creado para BI e informes. El material indica que no admite video, audio ni texto, no soporta ciencia de datos/ML, tiene soporte limitado para streaming y usa formatos cerrados. Por eso, cuando se necesitan datos no estructurados o usos avanzados, aparecen Lakes y otros almacenamientos. **Fuente: p. 2.**

### 2. Lake frente al Warehouse

El Lake reúne datos de diferentes tipos y permite usos DS/ML. Sin embargo, su soporte BI es escaso, su configuración es compleja y presenta problemas de rendimiento y confiabilidad. Amplitud de almacenamiento no equivale a facilidad de análisis. **Fuente: p. 3.**

### 3. Exportación y complejidad

Se mantienen un Warehouse propietario y un Lake, más el proceso de exportación entre ellos. Los datos no estructurados siguen necesitando el Lake y se conservan costos operativos y financieros. La propuesta Lakehouse busca atender varios usos desde una base común. **Fuentes: pp. 4, 8-10.**

### 4. Capas de Lakehouse

En la base hay un Lake con datos estructurados, semiestructurados y no estructurados. Sobre él se coloca una capa transaccional, representada por Delta Lake, y un motor de alto rendimiento, representado por Delta Engine. Los consumidores incluyen BI, DS, ML y streaming. El objetivo es arquitectura abierta y datos recientes con una única fuente de verdad. **Fuentes: pp. 5-6, 10.**

### 5. Desafíos del Lake

Se agrupan en **fiabilidad, rendimiento, gobernanza y calidad**. Fiabilidad incluye dificultad para agregar y modificar datos, fallas de trabajos a medio camino, operaciones en tiempo real y costo de versiones históricas. Rendimiento incluye grandes metadatos, demasiados archivos y bajo desempeño. Gobernanza incluye seguridad deficiente; calidad, problemas de calidad de datos. **Fuente: p. 13.**

### 6. Bronze, Silver y Gold

Bronze conserva ingesta cruda e historia; Silver contiene datos filtrados, limpios y ampliados; Gold contiene agregados de negocio. Ejemplo propio: ventas originales, ventas depuradas y totales mensuales por tienda, respectivamente. **Fuente conceptual: p. 14.**

### 7. SQL Analytics

Ofrece interfaz SQL nativa, conectores a herramientas BI existentes, gobernanza fina y consultas rápidas. Estas capacidades atienden tanto la forma de trabajar del analista como la integración, el control y el desempeño. **Fuente: p. 17.**

### 8. Lectura del gráfico

La presentación muestra un caso 30TB TPC-DS favorable para Databricks Lakehouse frente a tres Warehouses anónimos. No demuestra que siempre sea más barato ni establece precios actuales. Debe interpretarse dentro de la comparación presentada. **Fuente: p. 16.**

## 14. Repaso final

Practica la cadena **problema -> capa propuesta -> beneficio buscado**. Para la plancha, memoriza las cuatro categorías de p. 13 y al menos un problema concreto de cada una. Para arquitectura, no olvides la capa transaccional: es la diferencia visible entre el Lake sin esa organización y el Lakehouse de las láminas.
