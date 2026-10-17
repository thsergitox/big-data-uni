# Repaso rápido: entender conexiones, no memorizar nombres

**Guía de navegación:** [fuentes y páginas](/home/sergi/Documents/semesters/nine/bigdata/estudio/pc/COBERTURA.md). Este repaso sintetiza las guías; las justificaciones completas están en ellas y en las dos planchas resueltas.

## 1. Dos problemas diferentes: guardar y procesar

| Pregunta | Concepto | Idea esencial |
|---|---|---|
| ¿Dónde guardar archivos grandes y tolerar fallas? | HDFS / GFS | Dividir datos y conservar réplicas en varias máquinas. |
| ¿Cómo transformar muchos registros en paralelo? | MapReduce | Emitir pares, agrupar por clave y reducir los valores. |
| ¿Quién gestiona recursos de Hadoop? | YARN | Administrar recursos y planificar ejecución. |
| ¿Cómo convertir datos en decisiones? | BI / OLAP / minería | Integrar, analizar y extraer información o patrones. |

**Ejemplo propio:** guardar ventas en HDFS no calcula los totales. Un trabajo MapReduce puede calcular ventas por región. Un análisis OLAP puede explorar esos totales por tiempo y producto para tomar decisiones. Las tecnologías no son sinónimos.

## 2. Diagramas que debes dibujar sin mirar

### HDFS

```text
Cliente -- metadatos --> NameNode
Cliente -- datos <--> DataNodes -- réplica --> otros DataNodes
```

Explica: el NameNode administra ubicaciones/metadatos; los DataNodes guardan bloques; replicar en distintos nodos/racks ayuda frente a fallas. El secundario descrito en clase hace instantáneas; no lo conviertas sin evidencia en reemplazo automático.

### MapReduce

```text
"a b a" -> Map -> (a,1),(b,1),(a,1)
         -> agrupar -> (a,[1,1]),(b,[1])
         -> Reduce -> (a,2),(b,1)
```

La clave decide qué valores se reúnen. Particionar reparte claves entre reducers; agrupar reúne valores de una clave. Combiner hace agregación local intermedia para reducir tráfico.

### Chunk lease de GFS

```text
Master -- lease --> primaria
Cliente -- datos --> réplicas
Cliente -- escritura --> primaria -- orden común --> secundarias
```

El master concede el lease a una réplica; la primaria ordena mutaciones. Separar bytes y control evita hacer del master el intermediario de todo el tráfico.

### Pirámide BI de clase

```text
Estratégico       -> Data Mining          -> futuro       [BI]
Conocimientos     -> Data Warehousing     -> histórico    [BI]
Administrativo    -> Sistemas Información -> repetición   [OLTP]
Operacional       -> BD                   -> transacción  [OLTP]
```

No mezcles esta pirámide con la tabla de tres plazos: corto/operativo, mediano/táctico y largo/estratégico.

## 3. Diferencias que debes justificar

| Conceptos | Diferencia decisiva |
|---|---|
| Dato / información / conocimiento | Registro disponible / dato interpretado en contexto / comprensión que permite decidir. |
| OLTP / OLAP | Operaciones transaccionales habituales / consultas analíticas e históricas. |
| Extracción / transformación / carga | Obtener de fuentes / adecuar-integrar / incorporar al destino. |
| Initial / incremental / full load | Primera carga / nuevos y modificados / reconstrucción completa. |
| Hecho-medida / dimensión | Valor que analizo / criterio por el que lo analizo. |
| Granularidad fina / gruesa | Mayor detalle / mayor agregación. No puedes deducir detalles individuales de una suma que no los conserva. |
| Clasificación / agrupamiento | Clases predefinidas / descubrir grupos por semejanza. |
| Asociación / predicción | Relación entre elementos / estimación de lo que puede suceder. |
| Consistente / definido en GFS | Réplicas muestran lo mismo / además refleja una mutación exitosa sin interferencia. |
| Data warehouse / lake / lakehouse | Analítica organizada / repositorio amplio de datos / base abierta con gestión y optimizaciones analíticas. |

Las fuentes desarrollan matices; esta tabla no afirma que una tecnología sea incapaz de cualquier tarea distinta a su uso principal.

## 4. ETL: la trampa de la pregunta del parcial

Si piden **tipos de tareas ETL según p. 14**, escribe:

- **Inicial:** incorporar todo el historial por primera vez.
- **Incremental:** detectar y mover nuevos/modificados desde la carga anterior.
- **Total:** volver a cargar desde cero si detectar cambios es complejo.

Después puedes contextualizar con extracción-transformación-carga, pero NO reemplaces la respuesta por esas tres etapas.

## 5. Los diez desafíos del data lake

- **Fiabilidad:** anexar, modificar, fallas a mitad, tiempo real, versiones históricas costosas.
- **Rendimiento:** metadatos grandes, demasiados archivos, bajo rendimiento.
- **Gobernanza:** mala seguridad.
- **Calidad:** calidad de datos.

Fórmula de memoria: **5 + 3 + 1 + 1**. Las cuatro categorías vienen de la p. 13; el detalle está en una imagen de esa página.

## 6. Lakehouse en una explicación de 30 segundos

«Una Lakehouse busca usar el almacenamiento económico y los formatos abiertos de un data lake con capacidades de administración y rendimiento de un warehouse. Una capa de metadatos identifica versiones de tablas y permite gestión transaccional; optimizaciones como caché, índices y estadísticas ayudan a SQL. Distintos consumidores pueden acceder a una base compartida de datos».

No es suficiente decir «warehouse + lake» sin explicar **QUÉ se combina y PARA QUÉ**. No afirmar que existe una diapositiva 18 dentro del artículo de seis páginas.

## 7. Cubo OLAP

Ejemplo propio: medida **total vendido** y dimensiones **mes, producto, región**.

- «Enero, producto A, Lima» identifica una celda.
- Agrupar de meses a años cambia el nivel de detalle.
- Explorar de año a trimestre/mes aumenta el detalle.
- Cambiar orientación de ejes no cambia los valores base.
- Un cubo puede tener más de tres dimensiones.

## 8. Checklist de comprensión

Sin consultar, comprueba si puedes:

- [ ] Dibujar HDFS y distinguir metadatos de bloques.
- [ ] Recorrer MapReduce con pares intermedios y agrupación real.
- [ ] Explicar por qué se reejecuta un map fallido y qué papel juega el disco local.
- [ ] Diferenciar función de master y primaria en GFS.
- [ ] Explicar consistente vs definido con un ejemplo.
- [ ] Dibujar los cuatro niveles BI y mencionar los tres plazos sin confundirlos.
- [ ] Explicar los tres tipos de carga ETL.
- [ ] Enumerar diez desafíos del lake en cuatro categorías.
- [ ] Explicar medida, dimensiones y granularidad.
- [ ] Contrastar clasificación, agrupamiento y asociación.
- [ ] Explicar cómo la capa de metadatos aporta gestión a Lakehouse.

Si no puedes justificar una casilla, vuelve a su guía. Saber recitar la palabra no equivale a entender el mecanismo.
