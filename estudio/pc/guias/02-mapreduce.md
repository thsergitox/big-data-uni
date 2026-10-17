# 02. MapReduce: modelo, ejecución y tolerancia a fallos

**Fuente exclusiva:** [02 MapReduce_v22.pdf](/home/sergi/Documents/semesters/nine/bigdata/lessons/02%20MapReduce_v22.pdf), 23 páginas. Las páginas indicadas son las posiciones reales del PDF. Se inspeccionaron los gráficos/pseudocódigos de p. 4-5, 9 y 20.

**Prioridad de examen:** describir el modelo con tus palabras, mostrar pares clave-valor en un ejemplo y justificar por qué el sistema puede distribuir trabajo y recuperarse de fallos.

## 1. Mapa de temas

| Tema | Páginas PDF |
|---|---:|
| Definición, funciones y dominios clave-valor | 1-5 |
| Problemas que se pueden expresar | 6-7 |
| Entorno de implementación y secuencia de ejecución | 8-11 |
| Fallos de worker y master | 12-13 |
| Localidad, granularidad y tareas de respaldo | 14-16 |
| Particionamiento y combiner | 17-18 |
| Registros problemáticos y contadores | 19-21 |
| Conclusiones y principios de diseño | 22-23 |

## 2. Qué es MapReduce y cuál es su contrato (p. 2-5)

MapReduce es **un modelo de programación y una implementación asociada** para procesar y generar grandes conjuntos de datos. El programa expresa dos operaciones; el sistema se ocupa de paralelizarlas y ejecutarlas en un clúster.

- **Map:** recibe un registro lógico como par clave-valor y genera pares intermedios.
- **Agrupación del sistema:** reúne todos los valores que comparten la misma clave intermedia.
- **Reduce:** recibe una clave intermedia y su conjunto/lista de valores; los combina en una salida, por lo general menor. El texto dice que normalmente produce un valor o ninguno por invocación.

```text
Entrada                  Map                 Agrupación              Reduce
(k1, v1) ----------> [(k2, v2), ...] ----> (k2, [v2, v2, ...]) ----> salida
```

**Lo decisivo es la clave:** define qué valores se reúnen para una misma invocación de Reduce. Elegir mal la clave cambia el problema que se está resolviendo, aunque el programa ejecute sin errores.

### 2.1. Tipos y dominios

La p. 5 representa:

```text
map    (k1, v1)        -> list(k2, v2)
reduce (k2, list(v2))  -> list(v2)
```

El documento describe entrada y salida del trabajo como conjuntos de pares clave-valor (p. 3); en la firma conceptual de Reduce la clave aparece como argumento y la salida se representa como lista de valores. **No tomes la notación abreviada como pérdida de la asociación con la clave.** El dominio de entrada puede ser distinto del intermedio; la presentación relaciona el dominio intermedio con el final. Debes explicar esos dominios concretamente en el ejemplo, no inventar una restricción universal sobre implementaciones no desarrolladas aquí.

### 2.2. WordCount del PDF (p. 4)

- Entrada conceptual: `(nombre_del_documento, contenido_del_documento)`.
- Map recorre palabras y emite `(palabra, "1")` por aparición.
- El sistema agrupa todos los `"1"` de una palabra.
- Reduce convierte los valores a enteros, los suma y emite el resultado como cadena.

**Ejemplo propio:** documentos `D1: sol sol` y `D2: sol luna`.

```text
Map(D1) -> (sol,1), (sol,1)
Map(D2) -> (sol,1), (luna,1)
Agrupar -> (sol,[1,1,1]), (luna,[1])
Reduce  -> (sol,3), (luna,1)
```

Map NO sabe todavía el total global de `sol`: diferentes documentos pueden haberse procesado en distintos workers.

## 3. Otros problemas del modelo (p. 6-7)

| Problema | Qué hace Map | Qué hace Reduce / qué salida se busca |
|---|---|---|
| **Grep distribuido** | Emite una línea si cumple un patrón | Copia las líneas intermedias a la salida |
| **Frecuencia de accesos a URL** | El PDF enuncia el problema | Obtener frecuencias por URL; no desarrolla pseudocódigo |
| **Reverse Web-Link Graph** | Por enlace desde source hacia target, emite `(target, source)` | Reúne sources y genera `(target, lista_de_sources)` |
| **Term-Vector por Host** | Emite `(hostname, vector_de_términos)` por documento | Une vectores y descarta términos infrecuentes |
| **Índice invertido** | Emite `(palabra, ID_documento)` | Ordena/reúne IDs en `(palabra, lista_de_IDs)`; se pueden rastrear posiciones |
| **Ordenamiento distribuido** | Mencionado como aplicación | No se desarrolla el algoritmo en este PDF |

Un **vector de términos** resume palabras importantes mediante pares `(palabra, frecuencia)`. Un **índice invertido** cambia la dirección de búsqueda: permite ir de una palabra a los documentos que la contienen. Un **grafo web inverso** permite ir de una página destino a las páginas que la enlazan.

**Ejemplo propio de grafo inverso:** A enlaza C y B enlaza C; Map emite `(C,A)` y `(C,B)`; Reduce produce `(C,[A,B])`. No se cuenta necesariamente: Reduce también puede construir listas.

## 4. Ejecución distribuida: seguir los datos (p. 8-11)

**Alcance:** la p. 8 advierte que esta implementación se dirige al entorno usado en Google. Por eso aquí aparecen master/workers y después GFS. No conviertas sus tamaños o roles en requisitos obligatorios de cualquier Hadoop.

### 4.1. Secuencia completa

1. **Dividir entrada:** el input se divide en **M** piezas, generalmente de 16 o 64 MB en el ejemplo de implementación.
2. **Crear tareas y asignarlas:** una copia del programa es master; las demás son workers. Hay M tareas map y **R** tareas reduce. El master asigna tareas a workers ociosos.
3. **Aplicar Map:** cada worker map lee su pieza, interpreta registros clave-valor y llama a Map. Los pares intermedios se guardan inicialmente en un buffer de memoria.
4. **Escribir intermedios:** periódicamente el buffer se escribe en disco local, dividido en R regiones según una función de particionamiento. Se informa al master de las ubicaciones.
5. **Transferir y agrupar:** el master comunica esas ubicaciones a los workers reduce. Estos leen remotamente los intermedios desde discos locales de workers map y ordenan por clave para reunir valores iguales.
6. **Aplicar Reduce:** por cada clave única se entrega su lista de valores a Reduce. La salida se añade al archivo final de esa partición reduce.
7. **Finalizar:** cuando todas las tareas terminaron, el master devuelve el control/resultado al programa principal.

```text
                       MASTER
             asigna tareas / comunica ubicaciones
                  /                         \
Entrada -> worker MAP -> disco LOCAL -> worker REDUCE -> archivo final
           Map(k,v)       intermedios      lectura remota
                          R particiones     ordenar + agrupar
```

### 4.2. Dónde se guarda cada cosa

| Información | Lugar descrito | Consecuencia |
|---|---|---|
| Pares intermedios recién emitidos | Buffer de memoria de worker map | Luego se escriben periódicamente |
| Salida intermedia de Map | Disco local del worker map | Puede quedar inaccesible si el worker falla |
| Ubicaciones de intermedios | Comunicadas al master | El master orienta la lectura de Reduce |
| Salida final de Reduce | Sistema de archivos global | Una tarea reduce ya finalizada no debe repetirse por perder ese worker |

**Un worker no equivale a una tarea:** puede ejecutar diferentes tareas; M y R no son necesariamente cantidades de máquinas.

## 5. Tolerancia a fallos: razonar por persistencia (p. 12-13)

### 5.1. Si falla un worker

El master envía un ping periódico. Si el worker no responde dentro de cierto tiempo, lo marca como fallido.

- Las tareas **en progreso** vuelven al estado de espera y se asignan a otro worker.
- Las tareas **map ya completadas** en ese worker se reejecutan: sus archivos intermedios estaban en su disco local, ahora inaccesible.
- Las tareas **reduce ya completadas** NO necesitan reejecutarse: la salida ya está en el sistema de archivos global.
- Los workers reduce reciben aviso de las reejecuciones map para cambiar el worker del que leen los intermedios.

**Respuesta causal:** no se decide repetir por el nombre «map» o «reduce», sino por si el producto útil quedó accesible después del fallo.

### 5.2. Si falla el master

La p. 13 menciona puntos de control de las estructuras necesarias para dirigir workers y la posibilidad de cargar una nueva copia desde ellos. Luego especifica que, en el comportamiento descrito, se cancela el proceso MapReduce ante un fallo del master y el cliente puede reintentar.

**Cuidado:** que el material mencione checkpoints NO demuestra que ese proceso continúe automáticamente. Distingue la posibilidad explicada del comportamiento concreto que indica la diapositiva.

## 6. Localidad y granularidad (p. 14-15)

### 6.1. Localidad

La entrada reside en discos de máquinas del clúster. La diapositiva indica que **GFS** divide archivos en bloques de 64 MB y normalmente conserva tres copias en distintas máquinas. El master usa esa ubicación para asignar Map a una máquina que contenga una réplica de la entrada. Leer localmente ahorra ancho de banda.

La ventaja no elimina toda comunicación: la secuencia del documento también incluye lectura remota de intermedios en Reduce. **Localidad de entrada no significa que la red jamás se use.**

### 6.2. Granularidad y balanceo

Conviene disponer de más tareas que workers para distribuir dinámicamente la carga y recuperar tareas de un worker fallido en diferentes máquinas. El documento propone M asociado a entradas de 16-64 MB por tarea y R como un múltiplo pequeño de workers.

Ejemplo del PDF: **M = 200 000**, **R = 5 000**, **2 000 workers**. Esto demuestra que M/R y workers no son lo mismo. Una partición pequeña facilita redistribuir trabajo; no hace falta esperar a que una única máquina termine una porción enorme e indivisible.

## 7. Tareas de respaldo frente a reejecución por fallo (p. 16)

Un worker **rezagado** puede estar vivo pero terminar muy lentamente, por ejemplo por problemas de rendimiento del disco. Cerca del final, el master lanza ejecuciones de respaldo de tareas todavía en progreso. La tarea se marca completa cuando termina la original o la copia.

- **Reejecución por fallo:** recupera trabajo cuya ejecución/producto se perdió.
- **Respaldo de una tarea lenta:** reduce la espera causada por las últimas tareas rezagadas.

No toda ejecución redundante significa que una máquina ya murió. La redundancia es una herramienta tanto de recuperación como de reducción de impacto de lentitud (p. 23).

## 8. Particionamiento: decidir a qué Reduce va una clave (p. 17)

Se reparten los intermedios entre R tareas reduce usando una función sobre la clave. El ejemplo por defecto es:

```text
partición = hash(clave) mod R
```

**Idea:** las ocurrencias con la misma clave deben terminar en la misma partición para poder reunirse. No es lo mismo dividir inicialmente el input en M piezas que particionar la salida de Map para R tareas reduce.

Se puede cambiar la función según la organización deseada. El PDF propone `hash(Hostname(urlkey)) mod R` cuando se quiere que las entradas de un mismo host terminen en el mismo archivo de salida. Esa función decide destino; no calcula el agregado de Reduce.

## 9. Combiner: agregar antes de enviar (p. 18)

Cuando Map genera muchas claves repetidas, un **combiner** combina parcialmente los datos en la máquina map antes de transferirlos. Para WordCount, varios `(sol,1)` del mismo worker pueden pasar a `(sol,3)`.

```text
Sin combiner: Map -> (sol,1),(sol,1),(sol,1) -> red -> Reduce global
Con combiner: Map -> (sol,1),(sol,1),(sol,1) -> (sol,3) -> red -> Reduce global
```

La presentación dice que frecuentemente comparte código con Reduce, pero **su salida sigue siendo intermedia**, en un archivo que se enviará a una tarea reduce. No sustituye la agregación global: otro worker puede tener más ocurrencias de `sol`.

**Límite:** el PDF explica la suma de WordCount, pero no establece reglas de validez de un combiner para toda operación posible. No concluyas que cualquier Reduce puede reutilizarse sin analizar el problema.

## 10. Registros problemáticos y contadores (p. 19-21)

### 10.1. Omitir registros malos

Algunos registros desencadenan fallos en Map/Reduce que no pueden corregirse en ese contexto. Si la aplicación acepta ignorar unos pocos registros, un manejador detecta violaciones de segmento/errores de bus y notifica al master. Cuando varias señales se asocian a un registro particular, se indica omitirlo.

**Tradeoff del material:** continuar el cálculo a costa de excluir datos. No es una justificación para omitir silenciosamente cualquier error; el texto condiciona la técnica a que esa pérdida sea aceptable.

### 10.2. Contadores

La librería permite crear un contador e incrementarlo en Map o Reduce para contar eventos. El ejemplo de p. 20 crea `uppercase` e incrementa por palabras con mayúscula inicial. No es el mismo resultado que el conteo por palabra: es una medida del evento que se decidió observar.

Los valores de cada worker se comunican al master en las respuestas al ping de control. El master suma contadores de operaciones map/reduce **exitosas** y los entrega al final. La librería también mantiene contadores automáticos, como pares de entrada procesados y pares de salida generados.

## 11. Principios que sintetizan todo el tema (p. 22-23)

1. Restringir el programa a Map/Reduce facilita paralelizar y tolerar fallos.
2. El usuario expresa transformaciones; el sistema oculta coordinación, localidad y balanceo.
3. El ancho de banda es limitado: localidad y combiner reducen movimiento de datos por vías distintas.
4. La ejecución redundante ayuda ante máquinas lentas y pérdida de trabajo.
5. El modelo permite muchos problemas y puede escalar a clústeres grandes; no promete que cualquier problema tenga una expresión conveniente.

## 12. Confusiones frecuentes

- **Map calcula el total global:** emite información parcial; Reduce reúne por clave.
- **Reduce recibe toda la entrada original:** recibe la clave y valores intermedios agrupados.
- **Una tarea = una máquina:** varios trabajos/tareas pueden pasar por un worker.
- **Los intermedios de Map están en GFS:** la ejecución descrita los almacena en disco local.
- **Todo trabajo completado es intocable ante fallo:** Map completado puede necesitar repetirse si desaparece su disco.
- **Localidad elimina shuffle/red:** evita mover parte de la entrada; Reduce todavía lee intermedios remotos.
- **Combiner = Reduce final:** el primero produce parciales; el segundo produce salida final.
- **Respaldo = detección de muerte:** una tarea lenta también motiva respaldo.
- **Checkpoints = continuidad automática del master:** la diapositiva describe cancelación y reintento en el caso presentado.

## 13. Preguntas estilo examen: primero resuelve

1. **Describir con tus palabras el modelo de programación MapReduce visto en clase.** Incluir entrada, intermedios, agrupación y salida con un ejemplo gráfico.
2. Explicar Reverse Web-Link Graph e índice invertido: ¿qué cambia en la clave y el valor?
3. Graficar y describir la secuencia distribuida de ejecución, desde la división del input hasta los archivos finales.
4. Si falla un worker que había terminado dos maps y un reduce, ¿qué debe repetirse y por qué? ¿Qué sucede con sus tareas en progreso?
5. Explicar la falla del master según el material, distinguiendo checkpoints y comportamiento del caso descrito.
6. Describir localidad y granularidad y explicar por qué M y R pueden superar el número de workers.
7. Comparar particionador y combiner con un ejemplo WordCount. ¿Cuál decide el destino y cuál reduce información enviada?
8. Describir un worker rezagado y el mecanismo de tareas de respaldo; compararlo con recuperación por fallo.
9. Explicar cuándo se omiten registros malos y qué se sacrifica.
10. Describir los contadores del ejemplo de mayúsculas y la manera en que el master obtiene el resultado global.

---

## 14. Respuestas modelo

**1.** MapReduce procesa grandes conjuntos mediante dos funciones definidas por el usuario. Map transforma cada registro clave-valor en pares intermedios; el sistema agrupa los valores de la misma clave; Reduce combina esa lista y genera resultados. Ejemplo: `sol sol luna` produce `(sol,1),(sol,1),(luna,1)`; se agrupa en `(sol,[1,1])` y `(luna,[1])`; resulta `(sol,2),(luna,1)`. El entorno paraleliza y coordina. (p. 2-5)

**2.** En el grafo inverso Map emite `(destino,origen)` y Reduce reúne orígenes por destino: C recibe enlaces de A/B y produce `(C,[A,B])`. En el índice invertido Map emite `(palabra,ID_documento)` y Reduce reúne/ordena documentos por palabra. Ambos construyen listas, pero la clave representa entidades diferentes. (p. 6-7)

**3.** Se divide input en M piezas; master asigna M mapas y R reducciones; Map lee registros y emite a un buffer; los intermedios se escriben localmente en R regiones; master comunica ubicaciones; Reduce lee remotamente, ordena/agrupa y llama a la función por clave; escribe su archivo final. El master termina cuando todas las tareas completan. (p. 9-11)

**4.** Los maps completados deben reejecutarse porque sus intermedios estaban en el disco local ahora inaccesible. El reduce completado no, pues su salida está en el sistema de archivos global. Las tareas en progreso vuelven a espera y se reasignan; Reduce recibe aviso del nuevo origen de intermedios. (p. 12)

**5.** Se explican checkpoints que podrían permitir cargar estructuras de dirección en otra copia. Sin embargo, el comportamiento concreto descrito es cancelar el proceso si falla el master; el cliente puede detectar esa condición y reintentar. No corresponde afirmar recuperación automática solo porque se mencionen puntos de control. (p. 13)

**6.** La localidad programa Map donde existe una réplica de entrada y ahorra red. Dividir el trabajo en muchas tareas permite repartirlas dinámicamente y recuperar en varias máquinas las tareas de una máquina fallida. M es tareas map, R tareas reduce, no número de workers; el ejemplo usa 200 000, 5 000 y 2 000 respectivamente. (p. 14-15)

**7.** El particionador usa la clave, por ejemplo `hash(clave) mod R`, para decidir qué Reduce recibe un intermedio. El combiner combina repeticiones localmente: tres `(sol,1)` pueden pasar a `(sol,3)` antes de la red. Reduce todavía debe reunir parciales de diferentes maps. Destinar y agregar son operaciones distintas. (p. 17-18)

**8.** Un rezagado sigue trabajando pero demora demasiado. Cerca del final se ejecuta una copia de respaldo de la tarea restante y se acepta la primera que termine. Recuperar un worker fallido responde a trabajo perdido; respaldar un rezagado responde a demora, sin exigir que la máquina haya muerto. (p. 12 y 16)

**9.** Si un registro causa fallos repetidos y es aceptable perder unas pocas observaciones, señales de errores se notifican al master y este puede indicar omitir el registro. Se sacrifica inclusión completa de datos para poder finalizar. Es una decisión condicionada por la aplicación, no una corrección general de bugs. (p. 19)

**10.** Un contador `uppercase` se incrementa cuando se encuentra una palabra con mayúscula inicial. Los workers comunican valores al responder pings; master suma los valores de operaciones exitosas y entrega el total. Es distinto de contar la frecuencia de cada palabra; también hay contadores automáticos de pares procesados/generados. (p. 20-21)
