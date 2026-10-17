# Plancha del sustitutorio: resolver solo lo respaldado

**Enunciado:** [PDF del sustitutorio](/home/sergi/Documents/semesters/nine/bigdata/labs/planchas/Examen%20susti%202026-1%20-p1-sol.pdf), p. 1.

**Regla:** que el examen sea sustitutorio no impide repasar un tema ya presente en `lessons`. Lo que no está desarrollado allí se deja SIN RESPUESTA. Las páginas de abajo son páginas físicas de los PDFs.

## 1. Escritura aleatoria de Spark en el caso Uber

**PENDIENTE. No se responde.** No está la presentación de Uber citada por la plancha. Las menciones a Spark en Hadoop o a Hudi en el artículo Lakehouse no desarrollan el flujo de escritura aleatoria del caso Uber y no permiten reconstruir sus componentes.

## 2. Usos, técnicas, tecnologías y conocimiento por plazo en BI

**COBERTURA COMPLETA.** Fuente: [BI](/home/sergi/Documents/semesters/nine/bigdata/lessons/05%20C_BI_v11.pdf), **p. 18**.

### Respuesta modelo

La clase distingue tres horizontes. Los datos operativos alimentan el análisis táctico y la identificación de patrones para decisiones estratégicas.

| Plazo | Uso | Técnica, según la tabla | Tecnología/procedimientos, según la tabla | Resultado/conocimiento |
|---|---|---|---|---|
| Corto | Gestión, obtención y control de datos. | Sistemas existentes o *Legacy Systems*. | OLTP: procesamiento de transacciones en línea. | Datos y conocimiento operativo. |
| Mediano | Decisiones tácticas. | Data Warehouse. | OLAP: procesamiento analítico en línea. | Información para tomar decisiones. |
| Largo | Estrategia y pronóstico. | Minería de datos. | Agrupamiento, clasificación, secuenciación y reglas de asociación. | Patrones y nuevos conocimientos. |

**Ejemplo propio que conecta los tres:** una tienda registra pedidos diariamente mediante OLTP (**corto plazo**); reúne el historial y analiza ventas por producto y región con DW/OLAP para ajustar inventarios (**mediano**); explora agrupamientos de clientes y reglas de asociación para orientar promociones y planificación futura (**largo**).

- **Agrupamiento:** encontrar grupos de clientes parecidos sin partir de categorías previamente asignadas.
- **Clasificación:** asignar registros a clases conocidas.
- **Secuenciación:** identificar patrones de eventos que se suceden en un orden.
- **Asociación:** identificar relaciones entre elementos, como productos que aparecen juntos en compras.

El apoyo adicional para agrupamiento, clasificación y asociación está en [Analítica y minería](/home/sergi/Documents/semesters/nine/bigdata/lessons/04%20DataAnalyticDataMining_v15_es.pdf), pp. 13-16. Secuenciación se menciona en la tabla BI; el ejemplo anterior es solo ilustrativo.

**No inventar:** p. 18 no fija un número universal de días, meses o años para cada plazo. Tampoco es necesario agregar marcas de herramientas no listadas. La segunda columna titulada «Tecnología» contiene imágenes ilustrativas, no una lista adicional de productos.

## 3. Graficar y describir la arquitectura Lakehouse

**COBERTURA PARCIAL DEL ENUNCIADO EXACTO.** El tema sí está cubierto; la presentación de reforzamiento con la diapositiva 18 que cita la plancha **no está identificada** entre los archivos. El documento de Andy Aguilar es un artículo de seis páginas, no esa presentación.

Se responde únicamente la arquitectura que explican [Warehouse, lake y lakehouse](/home/sergi/Documents/semesters/nine/bigdata/lessons/04%20LakeVsLakeHouse_v03ppt.pdf), pp. 10-11, y [Lakehouse, Andy Aguilar](/home/sergi/Documents/semesters/nine/bigdata/lessons/Lakehouse%20Andy%20Aguilar.pdf), sección 3, pp. 3-4. **No se garantiza que este esquema sea el dibujo exacto del reforzamiento faltante.**

### Respuesta basada en el material disponible

Una Lakehouse combina almacenamiento económico y formatos abiertos propios de un data lake con capacidades de gestión y optimización de un data warehouse. Busca que análisis, aplicaciones en tiempo real y ciencia de datos/aprendizaje automático puedan trabajar sobre una misma base de datos almacenados, evitando mantener copias separadas para cada uso.

```text
      Análisis / SQL       Aplicaciones         Ciencia de datos /
         y BI              en tiempo real      aprendizaje automático
           \                    |                     /
            \____ acceso a los mismos datos _________/

       Gestión mediante metadatos transaccionales
    versiones de tabla, transacciones, calidad y auditoría
       Optimizaciones: caché, índices, estadísticas
                              |
              Almacenamiento económico compartido
                   Archivos en formatos abiertos
                  Ejemplo del artículo: Parquet
                              ^
           Datos estructurados, semiestructurados
                    y no estructurados
```

**Esquema propio de estudio**, que sintetiza las dos fuentes; no copia una supuesta diapositiva 18.

Componentes y responsabilidades:

1. **Almacenamiento:** conserva la mayor parte de los datos en almacenamiento de objetos económico; el artículo también contempla almacenamiento local como HDFS.
2. **Archivos abiertos:** permiten acceso por distintos sistemas; el artículo da Apache Parquet como ejemplo de formato estándar.
3. **Metadatos transaccionales:** registran qué archivos corresponden a una versión de una tabla y permiten capacidades como transacciones ACID y versionado. ACID significa atomicidad, consistencia, aislamiento y durabilidad.
4. **Optimizaciones de consulta:** caché, índices, estadísticas y organización de datos para mejorar el rendimiento SQL sin depender de modificar el formato base.
5. **Cómputo y consumidores:** distintas cargas analíticas pueden usar los mismos datos; el artículo destaca separar almacenamiento y procesamiento y el uso de APIs DataFrame.

**Límite importante:** el diagrama de la presentación anuncia soporte para tiempo real, pero no explica Spark Streaming. Eso no habilita responder la pregunta 4.

**Pendiente para el sustitutorio:** obtener el reforzamiento citado y contrastar sus componentes/dibujo con esta explicación.

## 4. Conceptos clave del manejo de streams de Spark

**PENDIENTE. No se responde.** No aparece el PDF Spark Streaming citado. Mencionar «tiempo real» o Spark de manera general no cubre los conceptos específicos de su manejo de streams. No se agregan definiciones externas ni se adelanta ese temario.

## 5. Al menos cuatro características del modelo de consistencia de GFS

**COBERTURA COMPLETA DEL TEMA.** Fuente: [GFS](/home/sergi/Documents/semesters/nine/bigdata/lessons/07%20GFS01_v03.pdf), **pp. 22-25**. P. 22 presenta los estados; las explicaciones necesarias continúan en las páginas siguientes.

### Respuesta modelo

GFS usa un modelo de consistencia relajado, adaptado a sus cargas de trabajo. Las garantías dependen del éxito de las mutaciones y de si hay escritores concurrentes. Se pueden describir estas seis características:

1. **Región consistente (*consistent*):** los clientes observan los mismos datos al leer diferentes réplicas. Con escrituras concurrentes, esos datos pueden contener fragmentos de varias mutaciones; igualdad entre réplicas no implica que el resultado sea una única escritura intacta.
2. **Región definida (*defined*):** además de ser consistente, refleja lo escrito por una mutación exitosa sin interferencia de escritores concurrentes.
3. **Región indefinida, según la terminología de la diapositiva (*undefined*):** p. 23 la asocia a una mutación fallida y a la posibilidad de que clientes observen datos distintos. Aprende esa formulación para esta clase; no concluyas que todo resultado no definido necesariamente difiere entre réplicas, porque el propio punto 1 contempla resultados consistentes compuestos por varias mutaciones.
4. **Orden común de mutaciones:** la primaria que tiene el lease serializa las mutaciones, y las réplicas aplican el mismo orden.
5. **Versionado de chunks:** permite identificar réplicas obsoletas; el master no entrega sus ubicaciones como réplicas actuales y no les aplica nuevas mutaciones.
6. **Salvedad de caché y diseño de aplicaciones:** una ubicación obsoleta puede permanecer en la caché de un cliente. El material reconoce esa lectura posible y explica que las cargas dominadas por anexos, los checkpoints y los registros autovalidables ayudan a tolerar la consistencia relajada.

**Ejemplo propio:** si una mutación escribe correctamente `ABC` sin competencia, todas las réplicas muestran ese contenido: región definida y consistente. Si dos escrituras interfieren, todas podrían mostrar el mismo resultado mezclado: consistente, pero no equivalente a una sola mutación completa.

**Para responder «al menos cuatro»:** explica 1, 2, 4 y 5, y añade 3 y 6 si hay espacio. No conviertas consistencia relajada en una garantía de que «toda escritura siempre queda idéntica sin posibilidad de falla».

## 6. Definir y ejemplificar gráficamente cubos multidimensionales OLAP

**COBERTURA COMPLETA.** Fuente: [OLAP](/home/sergi/Documents/semesters/nine/bigdata/lessons/05%20B_OLAP.pdf), **pp. 26-30**.

### Respuesta modelo

Un cubo multidimensional representa datos planos organizados en filas y columnas como una matriz de N dimensiones. Las dimensiones son ejes de análisis; la intersección de sus valores determina el valor de un indicador o medida. No está limitado a tres dimensiones: «cubo» es una representación intuitiva, y con más ejes se habla también de hipercubo.

Ejemplo propio: analizar **ventas** por **tiempo**, **producto** y **región**.

```text
                        Región
                          ^
                         /|
                        / |
                  +----+--+
                 /    /   |
                +----+    |        Medida en cada celda:
                |    |    +        total de ventas
                |    |   /
                +----+--+ --------> Producto
               /
              v
            Tiempo
```

Una celda puede responder: «¿Cuánto se vendió del producto A en Lima durante enero?». Si fijamos enero, una sección del cubo se puede mostrar así:

| Enero | Producto A | Producto B |
|---|---:|---:|
| Lima | 1000 | 700 |
| Cusco | 600 | 400 |

Otra sección corresponde a febrero:

| Febrero | Producto A | Producto B |
|---|---:|---:|
| Lima | 1200 | 800 |
| Cusco | 650 | 450 |

Cada número es la medida; mes, producto y región son las coordenadas. Las jerarquías permiten distintos niveles de detalle, como año, trimestre y mes para tiempo. La figura del PDF utiliza región, año y tipo de producto, y muestra una celda como las ventas de teléfonos Standard en 1997 en Vaud.

**No confundir:** una dimensión responde «¿por qué criterio analizo?»; una medida responde «¿qué valor analizo?». El cubo no implica tres servidores ni tres tablas obligatorias.

## Parte 2: exposición

Se deja fuera: faltan los lineamientos específicos del trabajo y el pedido actual es estudiar teoría.
