# Simulacro teórico: responder, explicar y dibujar

**Duración sugerida:** 45-60 minutos. Preguntas originales de práctica, inspiradas en el estilo de las planchas; no son un examen filtrado ni una predicción. Solo incluyen temas cubiertos en lessons. Las fuentes y páginas se encuentran en [Cobertura](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/COBERTURA.md) y en las guías enlazadas allí.

No abras las respuestas hasta terminar. Un nombre de componente sin explicación no obtiene el mismo mérito que describir su función.

## Preguntas

### A. Núcleo de la plancha del parcial

1. **Graficar y describir.** Dibuja HDFS con un cliente, NameNode, DataNodes y dos racks. Distingue flechas de metadatos, datos y replicación. Explica qué aporta distribuir réplicas entre hosts.
2. **Describir con ejemplo.** Procesa las entradas `sol luna sol` y `luna mar` mediante MapReduce. Muestra salida de Map, agrupación y salida de Reduce. Explica qué decide la clave.
3. **Describir mecanismo.** Un chunk tiene tres réplicas y dos clientes desean modificarlo. Explica cómo el lease permite aplicar un orden común y distingue responsabilidades del master y la primaria.
4. **Graficar y comparar.** Dibuja los cuatro niveles de la pirámide BI de clase. Señala los niveles transaccionales y analíticos, y da un ejemplo propio por nivel.
5. **Elegir y justificar.** Un DW está vacío; después debe refrescarse cada noche; finalmente una fuente no permite detectar cambios con fiabilidad. ¿Qué tipo de carga ETL usarías en cada situación y por qué?
6. **Enumerar y organizar.** Enumera los cuatro grupos de desafíos del data lake y asigna a cada uno los problemas de la diapositiva. Debes recordar diez problemas, no solamente cuatro títulos.

### B. Temas adicionales que sí aparecen en lessons

7. **Diferenciar.** Una compañía registra pedidos diarios, compara ventas trimestrales y descubre grupos de consumidores para planificar campañas. Relaciona cada actividad con plazo, uso y tecnología/técnica BI de la tabla de clase.
8. **Graficar con ejemplo.** Construye un cubo con ventas como medida y tiempo, producto y región como dimensiones. Define una celda y explica qué cambia al pasar de meses a años.
9. **Explicar una distinción.** ¿Puede una región GFS ser consistente y, aun así, no reflejar una única mutación completa? Explica con el vocabulario y las salvedades del material.
10. **Explicar componentes.** Sin inventar la diapositiva de reforzamiento faltante, describe una Lakehouse usando el artículo disponible: almacenamiento, archivos, metadatos y optimizaciones. ¿Por qué no basta con almacenar archivos abiertos?
11. **Comparar con ejemplos.** Diferencia clasificación, agrupamiento y asociación. Elige un ejemplo distinto para cada una e indica si parte de clases predefinidas.
12. **Explicar fallas.** Si falla un worker MapReduce, ¿por qué puede ser necesario repetir un map terminado, pero no un reduce terminado? Usa el lugar donde se guarda cada resultado.
13. **Justificar una configuración.** En un clúster Hadoop, ¿para qué sirven nombres de hosts, SSH, Java, los directorios de HDFS y los servicios YARN? Responde por función, no como lista de comandos.
14. **Relacionar calidad y análisis.** Una fuente guarda fechas distintas, códigos incompatibles y datos faltantes. Explica qué trabajo de integración/transformación necesitas antes de analizar y por qué omitirlo puede producir conclusiones incorrectas.

## Cómo autoevaluarte

Puntúa cada pregunta de 0 a 3, máximo 42 puntos. Es una rúbrica de práctica, **no la del profesor**:

- **0:** no responde, confunde el tema o inventa componentes.
- **1:** nombra conceptos correctos, pero omite funciones o el ejemplo pedido.
- **2:** explica el mecanismo principal y ofrece un ejemplo coherente; hay alguna omisión menor.
- **3:** responde todo el enunciado, justifica funciones/diferencias y realiza el gráfico si se solicita.

Repite las preguntas con 0-1 puntos; no releas todas las guías indiscriminadamente si sabes exactamente dónde está tu dificultad.

---

# Respuestas modelo: consultar después

## 1. HDFS

El cliente consulta al NameNode los metadatos/ubicaciones. Los DataNodes conservan y sirven bloques; los datos se leen/escriben con ellos. Las réplicas en distintos nodos/racks ayudan a mantener acceso si falla un host o una parte de la infraestructura. El dibujo debe separar la consulta de metadatos del acceso a bytes.

```text
Cliente -- consulta --> NameNode -- coordinación --> DataNodes
Cliente <-- datos --> DataNodes [rack 1] <-- réplica --> DataNodes [rack 2]
```

**Repasar:** guía 01 y parcial P1.

## 2. MapReduce

- Map primera entrada: `(sol,1)`, `(luna,1)`, `(sol,1)`.
- Map segunda: `(luna,1)`, `(mar,1)`.
- Agrupación: `(sol,[1,1])`, `(luna,[1,1])`, `(mar,[1])`.
- Reduce: `(sol,2)`, `(luna,2)`, `(mar,1)`.

La clave determina qué ocurrencias se combinan. El orden de presentación de las tres salidas no cambia el conteo. Un map aislado no conoce necesariamente todas las ocurrencias del conjunto completo.

**Repasar:** guías 02-03 y parcial P2.

## 3. Lease

El master concede un lease a una réplica, la primaria. El cliente consulta quién es la primaria y dónde están las réplicas; envía los datos y solicita a la primaria la mutación. La primaria fija el orden y las secundarias lo siguen. El lease es temporal, revocable y extensible. No es el cliente quien posee el lease del chunk ni el master quien transporta todos los datos.

**Repasar:** guía 10 y parcial P3.

## 4. Pirámide

Base a cima: **operacional/BD**, **administrativo/sistemas de información**, **conocimientos/Data Warehousing**, **estratégico/Data Mining**. Los dos primeros pertenecen al grupo transaccional/OLTP y los dos superiores al analítico/BI en p. 6. Ejemplos: registrar venta, gestionar pedidos repetitivos, comparar historia de ventas, identificar patrones para planificar expansión.

**Repasar:** guía 09 y parcial P4. No reemplazar la pirámide por corto-mediano-largo plazo.

## 5. ETL

DW vacío: **Initial Load**, primera gran carga. Refresco nocturno: **Incremental Load**, datos nuevos/modificados desde la última actualización. Sin control de cambios practicable: **Full Load**, recarga completa desde cero. Detectar cambios puede usar marcas de tiempo, triggers, identificación de instancias o comparación OLTP-DW según el material.

**Repasar:** guía 08 y parcial P5.

## 6. Desafíos lake

- Fiabilidad: dificultad de anexar, modificar, fallas a mitad, tiempo real y costo de versiones históricas.
- Rendimiento: grandes metadatos, demasiados archivos y bajo rendimiento.
- Gobernanza: mala seguridad.
- Calidad: calidad de datos.

**Repasar:** guía 06 y parcial P6. Distribución: 5+3+1+1.

## 7. BI por plazo

Registro diario: corto plazo, gestión/control, sistemas existentes y OLTP, datos operativos. Comparación trimestral: mediano, decisión táctica, DW/OLAP, información. Grupos de consumidores para campañas futuras: largo, estrategia/pronóstico, minería/agrupamiento, patrones y nuevos conocimientos. Los ejemplos son ilustrativos; la tabla no fija umbrales universales de duración.

**Repasar:** guía 09 y sustitutorio P2.

## 8. Cubo

La celda «enero, producto A, Lima» contiene una medida, por ejemplo ventas por 1000 unidades monetarias. Tiempo, producto y región son dimensiones. Pasar de meses a años resume a una granularidad más gruesa; buscar meses desde el año aumenta detalle. Se puede dibujar el cubo o mostrar una matriz región-producto para cada mes como secciones. No se limita a tres dimensiones.

**Repasar:** guía 08 y sustitutorio P6.

## 9. Consistencia GFS

Sí: la p. 23 señala que mutaciones concurrentes pueden producir los mismos datos en todas las réplicas, pero compuestos de fragmentos de distintas mutaciones. Consistente significa igualdad de lo observado entre réplicas; definido añade que refleja una mutación exitosa sin interferencia. La primaria coordina orden y las versiones ayudan a excluir réplicas obsoletas, pero la caché del cliente introduce una salvedad reconocida en el PDF.

**Repasar:** guía 10 y sustitutorio P5. No deducir que «no definido» siempre equivale a «diferente entre réplicas».

## 10. Lakehouse

Almacenamiento económico con archivos abiertos, una capa de metadatos transaccionales que identifica versiones de tablas y aporta gestión, y optimizaciones como caché, índices y estadísticas para consultas. Permite distintos consumidores sobre datos compartidos. Archivos abiertos solos no proporcionan toda esa gestión ni garantizan rendimiento analítico. Esta respuesta corresponde al artículo disponible; no afirma reproducir la presentación faltante.

**Repasar:** guías 06-07 y sustitutorio P3, de cobertura parcial.

## 11. Minería

**Clasificación:** asignar un correo a spam/no spam, clases definidas. **Agrupamiento:** descubrir conjuntos de compradores parecidos sin fijar de antemano esas clases. **Asociación:** identificar que dos productos aparecen juntos en compras. La asociación describe una relación, no demuestra por sí sola causalidad.

**Repasar:** guía 05. Los ejemplos son elaboración propia.

## 12. Fallas MapReduce

El resultado intermedio del map se guarda en el disco local del worker; si falla, puede quedar inaccesible y debe regenerarse. El resultado de un reduce terminado se almacena en el sistema de archivos global, por lo que no requiere repetirse por ese motivo. Las tareas en progreso se vuelven a asignar; los reducers son avisados si cambia el worker del que deben leer intermedios.

**Repasar:** guía 02, fuente MapReduce p. 12.

## 13. Configuración

Los nombres de hosts permiten identificar/resolver nodos; SSH permite la comunicación de administración/inicio remoto que usa la guía; Java permite ejecutar los componentes Hadoop; los directorios HDFS almacenan metadatos/bloques según el rol; YARN administra recursos y ejecución. Arrancar un servicio no basta para demostrar funcionamiento: hay que verificar procesos y operaciones. Esto explica el procedimiento, no ejecuta una instalación nueva.

**Repasar:** guía 04 y guía 01.

## 14. Calidad e integración

Extraer de las fuentes y transformar antes de cargar/analisar: unificar formatos de fecha, armonizar códigos, resolver o tratar valores faltantes según su significado y verificar coherencia. Si una misma categoría tiene códigos distintos, se pueden separar registros equivalentes; si se mezclan fechas, se comparan periodos equivocados. Analizar sin revisar calidad puede generar resultados aparentemente numéricos pero conceptualmente incorrectos.

**Repasar:** guías 05, 08 y 09. No inventar una regla universal para rellenar todos los datos faltantes.
