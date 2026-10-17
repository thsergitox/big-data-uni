# 10. Google File System (GFS): arquitectura y decisiones de diseño

## Fuente y alcance

Fuente: [GFS](/home/sergi/Documents/semesters/nine/bigdata/lessons/07%20GFS01_v03.pdf), **40 páginas reales del PDF**. Cada página corresponde a una diapositiva de esta presentación; el formfeed vacío al final del texto extraído no constituye una página 41.

La guía desarrolla todos los bloques de las láminas, incluidos los diagramas. Conserva el vocabulario de consistencia usado en clase y señala una contradicción interna en la descripción del lease de p. 28. No incorpora protocolos ni teoría externa al temario.

**Preguntas directas de plancha:** Parcial P3, chunk leases, pp. 19-21; Sustitutorio P5, al menos cuatro características del modelo de consistencia, pp. 22-25.

## 1. Mapa completo de páginas

| Páginas PDF | Contenido |
|---|---|
| 1 | Portada |
| 2-3 | Propósito de GFS y relación entre diseño, clientes y fallas |
| 4-5 | Supuestos de lectura, escritura, concurrencia y ancho de banda |
| 6-7 | Clúster y estructura de archivos/chunks/bloques |
| 8-14 | Arquitectura, responsabilidades y metadatos; diagramas |
| 15-16 | Separación de flujo de datos y control |
| 17-18 | Operation log y checkpoints |
| 19-21 | Chunk leases y separación entre transmisión y orden de mutación |
| 22-25 | Modelo de consistencia y adaptación al workload |
| 26-29 | Tipos de mutación y secuencia de escritura; diagrama p. 27 |
| 30 | Atomic record append |
| 31 | Snapshots |
| 32-34 | Colocación, re-replicación y balanceo |
| 35-38 | Garbage collection diferida |
| 39-40 | Síntesis de decisiones de diseño |

## 2. El problema al que responde GFS

**Referencias: pp. 2-3.** GFS se presenta como un sistema distribuido propietario de Google, diseñado para aplicaciones de datos con alta concurrencia. Sus objetivos son almacenamiento confiable, escalable y de alta disponibilidad. Emplea primitivas como replicación, registro de operaciones, instantáneas y checkpoints.

El punto central es que su diseño está acoplado a las **cargas de trabajo de sus clientes**. El sistema reúne mucho hardware básico y asume un diseño tolerante a fallas. No basta describirlo como «archivos en varios computadores»: las decisiones posteriores responden a cómo leen, escriben y procesan datos esas aplicaciones.

**Workload** significa carga de trabajo: el conjunto de operaciones, tamaños y patrones de acceso que debe atender el sistema.

## 3. Supuestos: ancho de banda antes que latencia individual

**Referencias: pp. 4-5.** Las lecturas son principalmente grandes flujos de cientos de KB a varios MB, acompañadas de algunas lecturas aleatorias pequeñas. Las escrituras son principalmente adiciones secuenciales grandes; existen pocas escrituras pequeñas aleatorias y los archivos tienden a no modificarse después de escritos.

También se esperan muchas adiciones concurrentes. Los clientes participan en grandes trabajos de procesamiento que necesitan **ancho de banda alto y sostenido**, más que minimizar la demora de una operación aislada.

- **Ancho de banda:** cuánto dato se mueve de forma sostenida.
- **Latencia individual:** cuánto tarda una operación concreta.

Esta distinción explica por qué el sistema acepta ciertas decisiones de consistencia y organización. Optimizar una solicitud diminuta no es el mismo problema que mantener un gran procesamiento continuo.

**Ejemplo propio:** un trabajo que recorre muchos archivos grandes depende del flujo sostenido de datos. Su necesidad no es idéntica a la de una aplicación que exige respuesta mínima para cada lectura pequeña. El ejemplo ilustra la prioridad del material, no amplía sus workloads.

## 4. Componentes y estructura de almacenamiento

**Referencias: pp. 6-7, 13.** Un clúster contiene un **master**, múltiples **chunkservers** y clientes concurrentes.

| Concepto | Definición/tamaño en las láminas |
|---|---|
| Archivo | Se divide en chunks |
| Chunk | Fragmento de 64 MB |
| Chunk handle | Identificador de 64 bits que identifica un chunk |
| Réplica | Copia del chunk; 3 réplicas por defecto |
| Bloque dentro del chunk | 64 KB |
| Checksum | Suma de verificación de 32 bits por bloque |

Los chunks se guardan como archivos Linux en los chunkservers. Para acceder al contenido se utiliza el handle junto con un desplazamiento de bytes (**byte offset**).

**No mezclar niveles:** 64 MB es el tamaño del chunk; 64 KB es el del bloque; 64 bits es el identificador; 32 bits es el checksum. Un identificador no es un tamaño de almacenamiento. La suma de verificación atiende la integridad del bloque; no reemplaza el orden de mutaciones ni la replicación.

**Ejemplo didáctico propio:** dos chunks de un archivo y tres réplicas por chunk implican seis copias de chunks distribuidas. No significan seis archivos lógicos distintos para el usuario.

## 5. Master, chunkservers y metadatos

**Referencias: pp. 8-14.** Los diagramas muestran dos tipos de mensajes: control/metadatos y datos. El cliente consulta al master, pero transmite o recibe el contenido directamente de chunkservers.

### 5.1 Responsabilidades del master

Según pp. 9 y 11, administra:

- espacio de nombres de archivos y chunks;
- relación archivo -> chunks;
- ubicaciones de réplicas;
- creación, asignación y replicación de chunks;
- instantáneas;
- puntos de control y registro de cambios de metadatos.

El **namespace** es la organización lógica de nombres del sistema. La relación file-to-chunk permite asociar el archivo visible al usuario con sus fragmentos almacenados.

### 5.2 Responsabilidades de chunkservers

Guardan chunks, mantienen sumas de verificación por bloque y comunican estado al master, incluidos arranque y recuperación de fallos. La comunicación periódica también interviene en la identificación de chunks que ya no son necesarios, explicada en pp. 35-38.

### 5.3 Por qué los metadatos caben en memoria

La p. 12 combina archivos grandes con poca información por chunk, aproximadamente **64 bytes de metadatos por chunk**. Según la exposición, el número de archivos es relativamente bajo y se puede mantener la información de metadatos en memoria para acelerar estas operaciones.

**En memoria no significa sin recuperación persistente.** Las pp. 17-18 explican operation log y checkpoints. La excepción de p. 13 son las **ubicaciones de réplicas**, que no se persisten por el master: se reconstruyen consultando chunkservers al arrancar.

La p. 14 muestra además un «Shadow Master» en el esquema. La presentación no desarrolla su protocolo; basta reconocerlo como rótulo del diagrama, sin inventar elecciones, tiempos de conmutación ni garantías que no estén descritas.

## 6. Separar datos y control evita cargar el master con todo el contenido

**Referencias: pp. 8, 10, 15-16.** El cliente traduce nombre del archivo y offset en un índice de chunk. Envía al master nombre e índice; este responde con handle y ubicaciones de las réplicas. El cliente conserva esa información en caché y realiza operaciones de datos directamente con los chunkservers.

```text
                         CONTROL / METADATOS
Cliente -> Master: nombre de archivo + índice de chunk
Cliente <- Master: handle + ubicaciones de réplicas
   |
   | caché de esa información
   v
Cliente <===============================> Chunkserver
                  DATOS: handle + rango de bytes
```

Diagrama didáctico basado en pp. 8, 10 y 15-16.

**Razón arquitectónica:** un solo master simplifica namespace, colocación y replicación porque hay una única fuente de verdad de los metadatos. Separar el contenido evita que todos los bytes circulen por él. El material plantea esta separación como mecanismo para evitar que el master sea un cuello de botella; no demuestra que un master carezca de todo límite posible.

## 7. Operation log y checkpoints: recuperar el estado del master

**Referencias: pp. 17-18.** El operation log establece una secuencia lógica coherente de **mutaciones de metadatos**. El master registra las operaciones en orden serial y replica el registro **antes** de confirmar la mutación y responder al cliente.

```text
Mutación de metadatos -> registro ordenado -> replicación del log
                      -> confirmación -> respuesta al cliente

Recuperación -> cargar checkpoint -> reproducir log posterior
```

Reproducir la secuencia permite restaurar el estado del master. Los checkpoints capturan el resultado de una serie de operaciones; así no es necesario reproducir siempre todo el historial. Son persistentes en disco y se conservan algunos logs y checkpoints anteriores para recuperación ante desastres.

**No confundir:** el operation log descrito aquí ordena cambios de metadatos del master. El orden de mutaciones de datos en réplicas se delega mediante leases, como explica el bloque siguiente.

**Ejemplo propio:** si un checkpoint ya contiene el efecto de operaciones anteriores, la recuperación aplica ese estado y luego las operaciones posteriores del registro, en vez de repetir todo desde el comienzo.

## 8. Chunk leases: delegar el orden de mutaciones

**Referencias: pp. 19-21. Tema directo de Parcial P3.** Como el master no participa en el flujo de datos, delega en una réplica la autoridad para ordenar mutaciones de un chunk. El **lease** es esa asignación temporal. La réplica que lo posee se denomina **primaria**.

### 8.1 Ciclo del lease

- El master otorga el lease a una de las réplicas del chunk.
- El lease vence después de un timeout breve.
- El master puede revocarlo manualmente si necesita proteger el chunk de mutaciones.
- El chunkserver puede solicitar una extensión comunicándose con el master.
- Para chunks que se modifican constantemente, las extensiones evitan revocaciones continuas, según la explicación del material.

### 8.2 Qué consulta y hace el cliente

Inicialmente pregunta al master cuál réplica tiene el lease y dónde están las demás. Si no hay lease asignado, el master elige una réplica. El cliente almacena esa información en caché, distribuye los datos y, tras la recepción confirmada por todas las réplicas, solicita la escritura a la primaria.

La primaria fija un orden serial de las mutaciones. Las demás réplicas aplican ese mismo orden. **El master decide quién coordina; la primaria coordina el orden de los cambios de datos.**

### 8.3 Por qué también mejora el flujo de red

La transmisión del contenido se desacopla de la determinación del orden y de la confirmación de la mutación. Así el flujo de bytes puede seguir la topología de red de las réplicas: no tiene que pasar primero por la primaria. La p. 27 muestra precisamente un flujo que pasa primero por una secundaria.

**Ejemplo propio:** llegan dos mutaciones A y B para un chunk. La primaria determina A antes de B y ese orden se usa en las réplicas. No es cada réplica la que decide independientemente cuál ejecutar primero.

### 8.4 Advertencia de redacción interna

La p. 28 dice que el master da «permiso de actualización a un cliente» y menciona 60 segundos. Esto entra en tensión con pp. 19-20, donde **el lease pertenece a una réplica**, la primaria. Para una explicación conceptual coherente con las láminas centrales, responde con **lease otorgado a la réplica primaria y timeout/extensión**, no con «el cliente posee el lease». El dato de 60 segundos es un detalle de esa p. 28, no necesario para desarrollar la pregunta de p. 19.

## 9. Modelo de consistencia: misma réplica visible no equivale siempre a resultado definido

**Referencias: pp. 22-25. Tema directo de Sustitutorio P5.** GFS adapta su consistencia a mutaciones que pueden ser exitosas, fallidas o concurrentes. Las láminas describen **regiones del archivo**, no una etiqueta invariable para todo el sistema.

| Término de las láminas | Descripción para responder con el material |
|---|---|
| `consistent` | Los clientes ven los mismos datos, independientemente de la réplica que leen; la concurrencia puede dejar una región formada por fragmentos de varias mutaciones |
| `defined` | La región es consistente y una mutación exitosa sin interferencia de escritores concurrentes deja visible lo que escribió |
| `undefined` | La p. 23 lo presenta como resultado de mutación fallida donde clientes pueden ver datos diferentes |

**Matiz importante:** la p. 23 usa `undefined` para diferencias observadas tras una falla y también permite que una región consistente contenga fragmentos de múltiples mutaciones. En esta guía se conserva esa redacción de clase; no se añaden estados ni tablas de una fuente externa. Tampoco se afirma que ver los mismos datos garantice ver una única mutación completa.

### 9.1 Orden común y versiones

La primaria con lease establece una secuencia y las réplicas aplican las mutaciones del mismo modo. GFS además usa números de versión de chunks: no aplica mutaciones a chunks obsoletos y el master no ofrece sus ubicaciones al cliente.

**La caché introduce una salvedad:** un cliente puede conservar una ubicación antigua y leer una réplica obsoleta. La p. 25 lo reconoce. No se debe decir «nunca puede leerse un chunk obsoleto» solo porque el master filtra sus respuestas.

### 9.2 Por qué puede relajarse el modelo

El material explica que las aplicaciones confían principalmente en adiciones, checkpoints y registros autovalidables para evitar efectos negativos. La carga intensa de append tiende a reducir el impacto de ciertas lecturas obsoletas. La posibilidad de relajar garantías surge del conocimiento del workload, **no de que la consistencia sea irrelevante**.

La p. 24 afirma que los clientes necesitan distinguir regiones definidas e indefinidas. Relaciona esa necesidad con la secuencia común de mutaciones y el control de versiones.

### 9.3 Ocho características listas para desarrollar en el examen

Si se piden «al menos cuatro», elige cuatro o más y explícalas, no solo enuméres términos:

1. Está adaptado a la carga de trabajo típica de clientes.
2. Distingue regiones consistentes, definidas e indefinidas según las láminas.
3. Consistente significa misma lectura entre réplicas, no necesariamente una única mutación sin mezcla.
4. Una región definida es consistente y refleja la mutación exitosa sin interferencia concurrente.
5. Una falla puede producir la situación indefinida descrita en p. 23.
6. Una primaria con lease fija un orden aplicado igual por todas las réplicas.
7. Las versiones excluyen chunks obsoletos de nuevas ubicaciones dadas por el master, aunque la caché del cliente puede conservar referencias antiguas.
8. El modelo relajado se acompaña de append, checkpoints y registros autovalidables en las aplicaciones.

## 10. Mutaciones: write frente a record append

**Referencias: pp. 26, 30.** Una **write** escribe en un offset específico. Una **record append** agrega datos al menos una vez en un offset elegido por GFS. La diferencia no es que una tenga réplicas y la otra no, sino quién determina la posición y qué garantía de incorporación se indica.

| Write | Atomic record append |
|---|---|
| Escribe en un desplazamiento específico | GFS elige el desplazamiento |
| Modifica la región indicada | Agrega el registro de forma atómica |
| No es la operación descrita como «al menos una vez» en la tabla de clase | Garantiza adición **al menos una vez** |
| Posición especificada para la escritura | Devuelve al cliente el offset elegido |

La p. 30 señala que record append se usa ampliamente para acceso concurrente. **Al menos una vez no significa exactamente una vez:** la garantía expresada no autoriza a prometer ausencia de duplicados. Atomicidad de append tampoco equivale a que todos los tipos de mutación del sistema tengan una garantía idéntica.

## 11. Secuencia completa de escritura y confirmación

**Referencias: pp. 20, 27-29.** La figura distingue flechas de datos y de control. El flujo que debe poder explicarse es:

1. El cliente consulta al master sobre el chunk y el lease.
2. El master informa la primaria y las réplicas; asigna una primaria si hace falta.
3. El cliente hace llegar los datos a todas las réplicas; la p. 28 indica que quedan en buffers LRU de los chunkservers. LRU es la política de caché mencionada por la lámina, sin mayor desarrollo allí.
4. Tras las confirmaciones de recepción, el cliente solicita la escritura a la primaria.
5. La primaria asigna orden/número serial, aplica localmente y envía la solicitud a las secundarias.
6. Las secundarias aplican en el mismo orden y responden a la primaria.
7. La primaria notifica al cliente que la mutación se completó.

```text
CONTROL INICIAL: Cliente <-> Master
DATOS:          Cliente -> réplicas (ruta según topología)
CONTROL WRITE:  Cliente -> Primaria -> Secundarias
CONFIRMACIÓN:    Secundarias -> Primaria -> Cliente
```

**Dos confirmaciones distintas:** recibir los bytes en un buffer no es lo mismo que haber aplicado la mutación. Esa separación permite distribuir datos antes de decidir y ejecutar el orden de escritura.

## 12. Snapshots: copia en escritura

**Referencia: p. 31.** Las instantáneas copian archivos y directorios en paralelo con operaciones habituales. El material indica que usan **copy-on-write**, vuelven temporalmente de solo lectura los datos copiados y permiten detectar cambios mientras se toma la instantánea.

No confundir snapshot con checkpoint:

- La instantánea corresponde a archivos y directorios.
- El checkpoint de pp. 17-18 ayuda a restaurar el estado del master junto con el log.

Las láminas no detallan el algoritmo interno de copy-on-write; no hace falta inventar pasos de referencia, bloqueo o copia física para responder esta sección.

## 13. Colocación de réplicas, re-replicación y balanceo

**Referencias: pp. 32-34.** Se colocan réplicas al crear chunks para integridad, se crean nuevas cuando las existentes quedan obsoletas o sus servidores no responden, y se reorganizan para equilibrar utilización de discos.

Tres criterios de colocación:

1. **Baja utilización de disco:** favorece reparto uniforme.
2. **Evitar muchas creaciones simultáneas en un servidor:** nuevos chunks suelen recibir grandes escrituras; concentrarlos provocaría puntos calientes.
3. **Distribución entre racks/bastidores:** agrega tolerancia a fallas por la colocación misma.

Se re-replica cuando el número de réplicas sanas cae por debajo del umbral, **3 por defecto y configurable dinámicamente**. El master balancea en segundo plano siguiendo criterios similares y busca retirar réplicas de servidores con uso de disco superior al promedio en comparación con servidores menos utilizados.

**Ejemplo propio:** si un chunk pierde una de sus tres réplicas sanas, el sistema debe restablecer el nivel configurado. Elegir cualquier servidor disponible sin atender utilización ni distribución ignoraría dos objetivos explícitos de la colocación.

## 14. Garbage collection: primero metadatos, después recuperación física

**Referencias: pp. 35-38.** La recolección de chunks no utilizados es diferida (**lazy**) y en segundo plano. Combina cambios de namespace/metadatos y mensajes periódicos master-chunkservers.

```text
Solicitud de borrado
   -> archivo oculto por nombre; chunks aún presentes
   -> después: eliminar del namespace y retirar relación archivo-chunks
   -> identificar chunks huérfanos y purgar sus metadatos
   -> intercambio periódico: informar qué chunks ya no figuran
   -> chunkservers recuperan espacio
```

Durante una ventana de tiempo, el archivo oculto puede seguir leyéndose por ese nombre y el borrado no se ha completado físicamente. Más tarde, el master elimina su información, identifica chunks huérfanos y comunica cuáles ya no son necesarios.

En el intercambio, los chunkservers informan qué réplicas mantienen; el master responde qué chunks ya no figuran en metadatos. Los servidores pueden entonces recuperar ese almacenamiento.

**Ventajas según las láminas:** el master posee una relación clara archivo-chunk, por lo que detectar huérfanos es sencillo; el proceso también recupera chunks inutilizables dejados por creaciones fallidas; y agrupa recuperaciones por chunkserver en lotes, amortizando el trabajo en vez de ejecutar cada liberación inmediatamente por separado.

No decir «borrar un archivo libera todo el espacio al instante»: el comportamiento diferido es precisamente parte del diseño descrito.

## 15. Síntesis de decisiones y sus razones

**Referencias: pp. 39-40.**

| Decisión | Razón expresada por el material |
|---|---|
| Un master para metadatos | Simplifica namespace, colocación y administración de réplicas |
| Datos fuera del master | Evita involucrarlo en transmisión de contenido |
| Chunk lease y primaria | Delegan serialización de mutaciones concurrentes |
| Operation log + checkpoint | Coherencia lógica y recuperación rápida del master |
| Detección/remediación de fallas | Responde a hardware y componentes que pueden fallar |
| Asignación y recolección lazy | Simplifica recuperación de espacio mediante mensajes y purga |
| Separar datos de confirmación | Permite transmitir por topología y ordenar después |
| Ajustar el sistema al workload | Justifica prioridades de ancho de banda, append y consistencia |

**Tradeoffs del propio material:** un master simplifica control y metadatos, y se descarga el flujo de datos para reducir su presión; la consistencia relajada se adapta a append, pero exige que las aplicaciones trabajen con las distinciones y mecanismos mencionados; la recolección diferida simplifica y agrupa trabajo, pero el espacio no se recupera inmediatamente.

## 16. Confusiones que cuestan puntos

- **El master almacena todos los datos:** almacena metadatos; los chunks están en chunkservers.
- **El lease es del cliente:** las pp. 19-20 lo asignan a una réplica; advertencia de p. 28 ya señalada.
- **La primaria debe recibir primero cada byte:** flujo de datos y orden están desacoplados.
- **Todas las réplicas ordenan por separado:** usan la secuencia de la primaria.
- **Consistente siempre significa definido:** las láminas admiten mezcla de mutaciones concurrentes en una región consistente.
- **Las versiones impiden cualquier lectura obsoleta:** la p. 25 reconoce ubicaciones cacheadas.
- **Append atómico = exactamente una vez:** el material dice al menos una vez.
- **Snapshot = checkpoint:** responden a objetos y objetivos diferentes.
- **Borrar nombre = recuperar espacio inmediatamente:** garbage collection es diferida.

## 17. Preguntas tipo examen

Intenta responder sin mirar el bloque siguiente.

1. ¿Qué workload presupone GFS y cómo influye en sus decisiones de diseño?
2. Describa master, chunkservers, clientes y estructura de archivos con los tamaños del material.
3. Explique cómo una lectura utiliza metadatos sin hacer pasar todo el contenido por el master.
4. ¿Cómo permiten operation log y checkpoints restaurar el master?
5. **Tipo Parcial P3:** explique chunk leases, quién los posee y cómo ordenan mutaciones.
6. Describa la secuencia de mutación y diferencie recepción de bytes de aplicación de escritura.
7. **Tipo Sustitutorio P5:** explique al menos cuatro características del modelo de consistencia.
8. Compare write y atomic record append, e indique qué significa «al menos una vez».
9. Explique colocación de réplicas, re-replicación y balanceo con sus criterios.
10. Describa snapshots y garbage collection, y distinga snapshot de checkpoint.

## 18. Respuestas modelo separadas

### 1. Workload

GFS presupone lecturas grandes de streaming, algunas aleatorias pequeñas, escrituras principalmente como adiciones secuenciales y pocas modificaciones aleatorias pequeñas. Espera append concurrente y grandes trabajos con necesidad de ancho de banda sostenido. Por ello diseña sus mecanismos para ese patrón, no principalmente para latencia de cada operación individual. **Fuentes: pp. 4-5.**

### 2. Arquitectura y tamaños

Hay un master de metadatos, varios chunkservers y clientes concurrentes. Un archivo se divide en chunks de 64 MB identificados por handles de 64 bits, con tres réplicas por defecto. Cada chunk tiene bloques de 64 KB con checksums de 32 bits. El master administra nombres, correspondencias archivo-chunk y colocación; los chunkservers guardan archivos Linux de chunks y comunican su estado. **Fuentes: pp. 6-13.**

### 3. Lectura

El cliente convierte nombre y offset en índice de chunk y consulta al master. Recibe handle y ubicaciones de réplicas, los cachea y accede directamente al chunkserver con handle y rango de bytes. El master atiende metadatos, no transmite todo el archivo. **Fuentes: pp. 8, 10, 15-16.**

### 4. Recuperación

Las mutaciones de metadatos se registran serialmente y el log se replica antes de confirmar. Reproducirlo reconstruye el estado. Un checkpoint persistente captura operaciones anteriores; al recuperarse se carga y luego se reproducen operaciones posteriores del log. Se retienen algunos logs y checkpoints previos para desastres. **Fuentes: pp. 17-18.**

### 5. Leases

El master otorga temporalmente a una réplica la autoridad para serializar mutaciones de un chunk; esa réplica es la primaria. El cliente consulta primaria y ubicaciones, distribuye datos y solicita escritura a la primaria tras confirmar recepción. Todas las réplicas aplican el orden fijado. El lease puede vencer, ser revocado o extenderse por solicitud del chunkserver. Así no se involucra al master en cada ordenamiento de datos y el flujo de bytes puede seguir la topología. **Fuentes: pp. 19-21.**

### 6. Secuencia de mutación

Consulta al master, información de primaria/réplicas, distribución y buffers de datos, confirmación de recepción, solicitud a primaria, asignación de orden y aplicación, reenvío a secundarias, aplicación en igual orden, confirmaciones a primaria y respuesta al cliente. La recepción en buffer es previa y distinta de la ejecución de la escritura. **Fuentes: pp. 20, 27-29.**

### 7. Cuatro o más características de consistencia

El modelo se adapta al workload. Una región consistente ofrece la misma lectura desde distintas réplicas, aunque puede mezclar mutaciones concurrentes. Una definida es consistente y refleja una mutación exitosa sin interferencia concurrente; una indefinida se describe asociada a falla y posibles lecturas distintas. La primaria fija un orden común. Las versiones excluyen chunks obsoletos de respuestas nuevas del master, pero ubicaciones cacheadas pueden permitir leerlos. Las aplicaciones recurren a append, checkpoints y registros autovalidables. **Fuentes: pp. 22-25.**

### 8. Write y append

Write escribe en un offset específico. Record append incorpora atómicamente el registro al menos una vez en el offset elegido por GFS y devuelve esa posición. El material lo relaciona con acceso concurrente. «Al menos una vez» no garantiza unicidad: no se debe sustituir por «exactamente una vez». **Fuentes: pp. 26, 30.**

### 9. Réplicas

Se crean réplicas al crear chunks, cuando las sanas quedan por debajo del umbral o para balancear uso. Se eligen discos menos utilizados, se evitan muchas creaciones juntas en un servidor y se distribuye entre racks para tolerancia a fallas. El umbral es tres por defecto y ajustable. El balanceo se ejecuta en segundo plano. **Fuentes: pp. 32-34.**

### 10. Snapshots y recolección

Snapshot copia archivos/directorios mediante copy-on-write en paralelo con operaciones normales, con datos temporalmente de solo lectura para detectar cambios. Checkpoint conserva estado de metadatos para recuperación del master. Garbage collection primero oculta el archivo, después purga namespace y relaciones, identifica chunks huérfanos y mediante mensajes periódicos permite recuperar espacio en lotes. **Fuentes: pp. 18, 31, 35-38.**

## 19. Repaso final

Para leases, explica **master asigna -> primaria ordena -> réplicas aplican**. Para consistencia, empieza por región, distingue misma lectura de resultado definido y menciona la salvedad de caché. Para arquitectura, separa namespace/metadatos de bytes. La idea integradora de toda la presentación es diseñar cada mecanismo para el workload real del cliente.
