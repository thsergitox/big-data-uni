# 08. OLAP, ETL y modelos multidimensionales

## Fuente y prioridad de examen

Fuente exclusiva: [05 B_OLAP.pdf](/home/sergi/Documents/semesters/nine/bigdata/lessons/05%20B_OLAP.pdf), **43 páginas físicas**. «p.» cuenta desde la portada; no se confunde con la numeración impresa. Se inspeccionaron visualmente los esquemas y tablas no recuperados como texto.

**Prioridad de planchas:** tareas ETL en **p. 14** (Parcial, pregunta 5) y cubos multidimensionales en **p. 26**, ampliados en pp. 27-30 (Sustitutorio, pregunta 6). No basta memorizar tres nombres de carga: hay que distinguir primera carga, refresco y reconstrucción.

## 1. Arquitectura: del dato operacional al análisis

El PDF enumera OLTP, Load Manager, DW Manager y Query Manager como componentes. El esquema muestra fuentes internas/externas, adquisición e integración, Data Warehouse, servidor OLAP, extracción y consultas/análisis, con salidas OLAP, reportes y minería. Los metadatos y la supervisión/administración acompañan la construcción y mantenimiento. **Fuente: pp. 2-5.**

**Reconstrucción esquemática basada en pp. 4-5:**

```text
Fuentes internas y externas
  aplicaciones / BD / archivos
               |
      adquisición + integración
               |
       Data Warehouse (DW)
               |
      cubos multidimensionales
               |
consulta / exploración / análisis / reportes / minería

Metadata y administración apoyan el conjunto.
```

El flujo explica por qué no se consulta necesariamente cada aplicación por separado: se extraen datos, se integran, transforman y limpian para cargarlos en el DW; se estructuran en cubos destinados a consultas dinámicas de buen rendimiento. El material no detalla contratos técnicos independientes para cada *manager*, así que el nombre no justifica inventar módulos o APIs. **Fuente: p. 5.**

**OLTP**, procesamiento transaccional en línea, genera información durante la operación empresarial. Sus fuentes pueden diferir en procedencia, función, formato y configuración: texto, hipertexto, hojas de cálculo, informes y bases transaccionales. **Fuente: p. 6.**

## 2. ETL: tres fases, no tres tipos de carga

**ETL = Extracción, Transformación y Carga.** Las fases indican qué se hace al dato; los tipos de carga de la p. 14 indican cuándo y con qué alcance se alimenta el almacén. **Fuente: pp. 7-14.**

### 2.1 Extracción

Obtiene datos relevantes de distintas fuentes OLTP, como CRM, ERP, TXT, aplicaciones a medida y otras. El PDF pide procesar **sin paralizar el OLTP ni el DWH**, gestionar metadatos, integrar fuentes internas/externas y utilizar tablas auxiliares o temporales para cálculos intermedios. Desde esas tablas se puebla el almacén. **Fuente: p. 8.**

**Ejemplo, elaboración propia:** reunir datos de pedidos de una aplicación y clientes de otra en tablas temporales. Extraer no significa que ya sean compatibles: esa compatibilidad corresponde a transformación.

### 2.2 Transformación e integración

Convierte datos inconsistentes en compatibles y congruentes para el DW. El material distingue codificación, medida de atributos, convenciones de nombramiento, integración de fuentes múltiples y limpieza. **Fuente: pp. 9-11.**

Los ejemplos de la figura de p. 10 son:

| Problema | Entradas visibles en el PDF | Salida uniforme |
|---|---|---|
| Codificación | off/on; 0/1; Apagado/Encendido | 0/1 |
| Medida de atributos | centímetros, metros, pulgadas | metros |
| Nombramiento | nombre, razón_social, proveedor | proveedor |
| Fuentes múltiples | varias fuentes con descripción | descripción integrada |

La figura de p. 9 incorpora una base temporal, evaluación de calidad, registros de excepción y corrección por el usuario. No es solo «copiar columnas»: se necesita compatibilizar significado y representación.

**Datos faltantes:** pueden no haberse registrado o una BD integrada puede carecer de la columna. **Datos extremos:** pueden ser un caso excepcional o un error de digitación. Esta distinción importa: un extremo NO ES necesariamente un error. El PDF no determina una técnica universal de imputación o eliminación. **Fuente: p. 11.**

### 2.3 Carga

Carga el almacén con datos transformados desde tablas temporales. También contempla datos OLTP que tengan correspondencia directa con el DWH. La representación general es extraer hacia temporales, transformar allí para evitar inconsistencias y cargar al almacén. **Fuente: pp. 12-13.**

```text
OLTP -> extracción -> tablas temporales -> transformación -> carga -> DWH
  \---------------- correspondencia directa ----------------------->/
```

*Reconstrucción didáctica de p. 13; la vía directa solo representa el caso explícitamente previsto por el material.*

## 3. Tareas de ETL: carga inicial, incremental y total

**Tema directo de plancha. Fuente: p. 14.**

| Tarea | Cuándo y qué mueve | Consecuencia o dificultad |
|---|---|---|
| Initial Load: carga inicial | Primera carga; gran cantidad de datos. | Fuerte consumo de tiempo. |
| Incremental Load: carga incremental o actualización | Refresco periódico; pocos datos nuevos o modificados. | Detectar cambios desde la fecha anterior. |
| Full Load: carga total | Se carga desde cero cuando el control de cambios es complejo. | No depende de seleccionar solo los cambios; rehace la carga. |

La frecuencia de actualización se establece para el mantenimiento incremental. Para controlar cambios, la lámina enumera: identificar las instancias OLTP involucradas, utilizar disparadores (*triggers*), recurrir a marcas de tiempo (*timestamps*) y comparar los datos existentes en OLTP y DW. Son alternativas mencionadas, no pasos obligatorios que siempre se ejecutan todos juntos.

**Ejemplos de elaboración propia, basados en p. 14:**

- **Inicial:** al inaugurar el DW de ventas se trasladan los registros disponibles por primera vez.
- **Incremental:** cada periodo se cargan ventas nuevas o modificadas desde la actualización anterior, identificadas con marcas de tiempo.
- **Total:** si distinguir cambios resulta demasiado complejo, se vuelve a poblar desde cero.

**Tradeoff que sí permite explicar la lámina:** inicial y total trabajan con una carga amplia, mientras incremental mueve pocos datos pero exige control de cambios. La lámina no cuantifica duración ni prescribe una frecuencia específica.

## 4. Administración del DWH

Sus responsabilidades incluyen transformar fuentes a un modelo dimensional, gestionar tablas de hechos/dimensiones, crear cubos, permitir sentencias **MDX (Multidimensional Expressions)**, definir políticas de particionamiento de la tabla de hechos para mejorar consultas y ejecutar respaldos. MDX se menciona, pero no se enseña su sintaxis. **Fuente: p. 15.**

## 5. Bases multidimensionales: dimensiones y medidas

Una base multidimensional se utiliza para crear aplicaciones OLAP. Cada tabla almacena registros con estructura **D1, D2, D3, …, M1, M2, M3, …**, y se relaciona con un hipercubo. **Fuente: pp. 16-17.**

- **Di, dimensión:** describe un aspecto del negocio; organiza lógicamente los datos; ofrece un medio para analizarlos; permite filtrar y manipular lo almacenado.
- **Mi, medida o hecho:** valor numérico que cruza dimensiones y niveles y permite sumarizaciones: sumas, promedios, mínimos, máximos, totales o porcentajes.

**Ejemplo, elaboración propia:** año, producto y región son criterios de análisis; cantidad vendida es un valor a analizar. Un número no siempre cumple la misma función: un identificador de producto puede estar escrito con cifras, pero en este ejemplo identifica una dimensión, no representa cantidad vendida.

### Tablas de dimensiones

Definen la organización lógica y contienen PK única, posibles claves foráneas, referencias primarias que identifican la dimensión y referencias secundarias que la describen. La figura muestra geografía (país, provincia, ciudad, barrio), productos (rubro, tipo, nombre), clientes y fechas (año, trimestre, mes, día). **Fuente: p. 22.**

La lámina advierte que la PK del OLTP no siempre corresponde a la PK de la dimensión, pero deja «¿por qué?» como pregunta. **No proporciona una explicación explícita ni enseña claves sustitutas:** esta guía no añade esa teoría como si estuviera desarrollada en el PDF.

### Tablas de hechos

Contienen medidas numéricas utilizadas para crear indicadores con sumarizaciones preestablecidas al construir el cubo. La lámina describe una PK compuesta por las PK de las dimensiones relacionadas; se reproduce el modelo del curso, no se afirma que toda implementación posible deba tener esa misma clave. **Fuente: p. 23.**

### Medidas naturales y calculadas

Ejemplos del material: pacientes admitidos, llamadas, importe total = precio × cantidad, rentabilidad = utilidad / PN, cantidad vendida y promedio de notas finales. No se expande PN porque la lámina no define la abreviatura. El PDF exige valores numéricos para cálculos y menciona codificar y posteriormente decodificar un valor no numérico para mostrarlo. **Fuente: p. 24.**

| Naturales: agregaciones enumeradas | Calculadas: formas enumeradas |
|---|---|
| Suma, cuenta, mínima, máxima, cuenta de distintos | Cálculos matemáticos, expresiones condicionales, alertas |

**Fuente: p. 25.** No confundir cuenta total con cuenta de distintos: el texto las enumera como operaciones diferentes.

## 6. Esquemas multidimensionales y advertencia de las figuras

El PDF enumera **estrella (Star Scheme), copo de nieve (Snowflake Scheme) y constelación (Starflake Scheme)**. **Fuente: p. 18.**

- **Estrella, p. 19:** F_SALES aparece como tabla central de hechos y se conecta a D_TIME, D_PRODUCT, D_KAM y D_CUSTOMER. Se distinguen claves de dimensiones y campos de medidas.
- **Lámina titulada Copo de Nieve, p. 20:** la figura muestra F_SALES y F_FORECAST, compartiendo conexiones con dimensiones de tiempo, producto y cliente.
- **Lámina titulada Constelación, p. 21:** muestra F_SALES y dimensiones ramificadas: D_PRODUCT se vincula a D_BRAND y D_CUSTOMER a D_CUSTOMER_GROUP y D_COUNTRY.

**Advertencia:** el documento no define en prosa las diferencias y los títulos de las dos últimas figuras pueden resultar confusos. Para mantener respaldo exclusivo, aquí se informa exactamente qué aparece bajo cada título; no se reemplazan con definiciones de manual externo ni se asegura una clasificación corregida. Si se pide dibujar, se puede reproducir el esquema y explicar conexiones visibles, explicitando esta limitación.

**Esquema estrella de elaboración propia, inspirado en p. 19:**

```text
               TIEMPO
                  |
PRODUCTO ----- HECHOS VENTAS ----- CLIENTE
                  |
                REGIÓN

Hechos: claves dimensionales + medidas de ventas
Dimensiones: identificadores + atributos descriptivos
```

## 7. Cubos multidimensionales o hipercubos

**Tema directo de plancha. Fuente: pp. 26-28.** Un cubo transforma o representa datos planos en filas y columnas como una **matriz de N dimensiones**. Los atributos se distribuyen por ejes y su intersección representa el valor del indicador. «Cubo» no restringe la estructura a tres dimensiones: la definición expresa N.

La figura de p. 26 identifica una celda con las coordenadas **Atributo 1, valor 5; Atributo 2, valor 4; Atributo 3, valor 3**. Esa combinación ubica el indicador, no es el valor numérico del indicador en sí.

La p. 27 relaciona ventas con región, tiempo (año/trimestre) y producto (categoría/tipo/producto). La p. 28 ejemplifica tres dimensiones: **región, año y tipo de producto**. La celda resaltada corresponde a **ventas de teléfonos Standard en 1997 en Vaud**; no se proporciona una cifra de ventas para esa celda.

**Diagrama de elaboración propia basado en p. 28:**

```text
             Región
                ^
                |
                +----------> Tipo de producto
               /
              /
             v Año

Celda: (Vaud, 1997, Standard) -> indicador de ventas
```

### Indicadores, atributos y jerarquías

- **Indicador:** sumarización (suma, conteo, promedio, etc.) sobre un hecho; depende de los atributos y jerarquías usados en el análisis.
- **Atributo:** criterio de análisis basado en referencias de las dimensiones; actúa como eje del cubo.
- **Jerarquía:** relación lógica padre-hijo entre al menos dos atributos, con relaciones 1-n entre niveles consecutivos. Puede haber varias en un mismo cubo.

**Fuente: pp. 29-30.**

**Ejemplo, elaboración propia:** medida = importe vendido; indicador = suma de importes; atributo = mes; jerarquía = año → trimestre → mes → día. Se basa en los atributos de fechas de p. 22 y la definición de p. 30.

### Granularidad

Es el nivel de detalle con que se almacena información. Ventas o compras pueden registrarse por día, mientras sueldos o cuotas por mes. Más detalle ofrece más posibilidades analíticas porque puede resumirse. La información fina puede convertirse en media o gruesa, **pero no se recupera el detalle original a partir del resumen**. **Fuente: p. 31.**

**Ejemplo, elaboración propia:** guardar ventas diarias permite sumar por mes; conocer solo el total mensual no permite determinar cada venta diaria. No confundir cambiar la vista del cubo con inventar detalle que nunca se almacenó.

## 8. Consultas y navegación OLAP

Una consulta obtiene indicadores de hechos restringidos por propiedades o condiciones de atributos. El material incluye *joins*, agregaciones y operaciones de análisis. El esquema de p. 33 usa VENTAS con dimensiones MARCAS, PRODUCTOS, CLIENTES y TIEMPO, indicador SUM(VENTAS.Venta) y jerarquía Producto → Clase. **Fuente: pp. 32-33.**

| Operación, según este PDF | Qué cambia | Ejemplo visible |
|---|---|---|
| Drill-down | Desciende hacia mayor detalle de una jerarquía. | Producto se descompone en clases A1/B1; ventas 40 se separan en 22 y 18. p. 34. |
| Drill-up | Sube hacia resumen de esa jerarquía. | Clases A1/B1 se resumen en Producto1; 22 + 18 = 40. p. 35. |
| Drill-across | Agrega un criterio más para analizar con mayor detalle. | Año/producto incorpora marca: 117 pasa a 40, 52 y 25. p. 36. |
| Roll-across | Retira un criterio y resume respecto de él. | Se elimina marca: 40 + 52 + 25 = 117. p. 37. |
| Pivot | Reordena visualización de atributos/indicadores y ejes. | Cambia el orden marca/año/producto a año/producto/marca. p. 38. |
| Page | Divide el cubo en secciones por valores de un atributo. | Página Producto1 frente a página Producto2. pp. 39, 41-42. |

**No importar otra definición de drill-across:** la tabla explica expresamente el uso del curso, «agregar un criterio». La p. 40 se titula Page, pero su texto dice **Pivot** y enumera mover atributos/indicadores de filas a columnas y viceversa, además de cambiar el orden de encabezados. Es una inconsistencia editorial: esas acciones corresponden al texto explícito de Pivot, mientras Page se define claramente en p. 39.

### Ejemplo integrado de las láminas

Datos resumidos para 2007:

| Producto | Marca M1 | Marca M2 | Marca M3 | Total |
|---|---:|---:|---:|---:|
| Producto1 | 40 | 52 | 25 | 117 |
| Producto2 | 39 | 65 | 48 | 152 |

**Fuente: pp. 34-42.** La suma por marcas muestra *roll-across*; volver a incluir marcas muestra *drill-across*. Cambiar filas y columnas sin cambiar ventas ilustra *pivot*. Ver solamente Producto1 como una sección ilustra *page*. Desglosar Producto1 en Clase A1/B1 es *drill-down*, no simplemente reordenar la tabla.

## 9. Confusiones de examen

1. **Fases ETL ≠ tareas de carga:** extracción/transformación/carga frente a inicial/incremental/total.
2. **Inicial ≠ total:** ambas pueden mover mucho, pero inicial es primera carga y total una recarga desde cero por complejidad del control.
3. **Dimensión ≠ medida:** criterio para analizar frente a valor numérico analizado.
4. **Medida ≠ indicador:** dato numérico frente a sumarización utilizada en un contexto de atributos.
5. **Granularidad ≠ cantidad de dimensiones:** nivel de detalle frente a ejes de análisis.
6. **Pivot ≠ drill-down:** cambia orientación/orden, no baja necesariamente a otro nivel de detalle.
7. **Atípico ≠ error confirmado:** puede ser excepcional o digitado incorrectamente.
8. **Un cubo no necesita exactamente tres ejes:** la definición es N-dimensional.

## 10. Autoevaluación tipo plancha

Las preguntas 2 y 6 se alinean con temas de las planchas indicadas; las demás son **elaboración propia**. Oculta las respuestas al practicar.

1. Grafique el recorrido desde OLTP hasta el usuario analítico y explique extracción, transformación y carga.
2. **Parcial P5:** explique los tipos de tareas ETL y dé un ejemplo de cada uno. ¿Qué mecanismos se mencionan para controlar cambios?
3. Explique cuatro inconsistencias que resuelve la transformación y diferencie datos faltantes de extremos.
4. Distinga dimensiones, hechos e indicadores. Explique cómo se organizan sus tablas y qué hace el administrador del DW.
5. Enumere los modelos multidimensionales del PDF y describa el esquema estrella. Señale la limitación de las otras figuras.
6. **Sustitutorio P6:** explique y grafique los cubos multidimensionales OLAP, indicando dimensiones, atributos, intersección e indicador.
7. Defina jerarquía y granularidad. Explique por qué no puede reconstruirse detalle a partir de datos resumidos.
8. Compare medidas naturales y calculadas e indique ejemplos del curso.
9. Distinga drill-down, drill-up, drill-across y roll-across con los datos del ejemplo.
10. Diferencie Pivot y Page y explique la inconsistencia editorial de p. 40.

---

## 11. Respuestas modelo

### 1. Arquitectura y ETL

OLTP produce datos operacionales; extracción obtiene los relevantes hacia tablas temporales sin paralizar OLTP/DWH; transformación integra y compatibiliza datos; carga los lleva al DWH, con una vía directa para datos de correspondencia inmediata. Desde hechos/dimensiones se construyen cubos que consultan los usuarios. Metadatos y administración apoyan la arquitectura. **Fuente: pp. 4-13.**

### 2. Tareas ETL

Inicial: primera carga, mucha información y alto consumo de tiempo. Incremental: actualización periódica con datos nuevos/modificados y control de cambios. Total: cargar desde cero cuando ese control es complejo. **Ejemplos de elaboración propia:** primera carga de ventas, incorporación periódica de ventas cambiadas y recarga completa. Para cambios, el curso cita identificación de instancias OLTP, triggers, timestamps y comparación OLTP/DW. **Fuente: p. 14.**

### 3. Inconsistencias

La transformación uniformiza códigos, unidades, nombres y datos de múltiples fuentes. Por ejemplo, off/on y Apagado/Encendido se llevan a 0/1; centímetros/metros/pulgadas a metros. Un faltante puede no haberse capturado o provenir de una BD sin la columna; un extremo puede ser excepcional o error de digitación. **Fuente: pp. 9-11.**

### 4. Modelo dimensional

Dimensiones organizan criterios; hechos son medidas numéricas; indicadores son sumarizaciones de hechos dependientes de atributos/jerarquías. Dimensiones contienen PK y referencias; hechos relacionan las dimensiones y sus medidas. El administrador gestiona esas tablas, cubos, MDX, particionamiento y respaldo. **Fuente: pp. 15-17 y 22-30.**

### 5. Esquemas

Se enumeran estrella, copo de nieve y constelación. Estrella muestra F_SALES conectada directamente con dimensiones de tiempo, producto, responsable comercial y cliente. Las pp. 20-21 no definen en prosa las diferencias: «Copo de Nieve» muestra dos hechos compartiendo dimensiones y «Constelación» dimensiones ramificadas. Hay que distinguir descripción verificable de una definición externa no desarrollada. **Fuente: pp. 18-21.**

### 6. Cubo

Representa filas y columnas en una matriz N-dimensional. Los atributos ocupan ejes; cada intersección de valores identifica el indicador correspondiente. En el ejemplo: año, región y tipo de producto; la celda (1997, Vaud, Standard) corresponde a sus ventas. Se debe dibujar los tres ejes y una celda, sin inventar una cifra para ella. **Fuente: pp. 26-28.**

### 7. Jerarquía y granularidad

Jerarquía relaciona dos o más atributos como padre-hijo, con 1-n entre niveles. Granularidad es detalle almacenado. La información diaria puede resumirse por mes, pero un total mensual no contiene la distribución diaria para recuperarla. El último ejemplo es elaboración propia basada en la regla explícita del PDF. **Fuente: pp. 30-31.**

### 8. Medidas

Naturales: suma, cuenta, mínima, máxima y cuenta de distintos. Calculadas: cálculos matemáticos, condiciones y alertas. Ejemplos: importe total = precio × cantidad, promedio de notas y cantidad de llamadas. **Fuente: pp. 24-25.**

### 9. Navegación

Drill-down desglosa en clases: 40 → 22/18; drill-up vuelve a resumir 22 + 18 = 40. En este curso, drill-across añade marca y distribuye 117 entre 40/52/25; roll-across retira marca y suma esas cifras. No son cambios de presentación solamente: cambia el detalle o el criterio de análisis. **Fuente: pp. 34-37.**

### 10. Presentación

Pivot reordena atributos/indicadores o cambia su posición entre filas y columnas; Page divide por valores de un atributo, como una página por producto. La p. 40 tiene encabezado Page pero el texto describe Pivot; Page queda definido en p. 39 e ilustrado en pp. 41-42. **Fuente: pp. 38-42.**
