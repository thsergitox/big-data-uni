# 05. Analítica, minería de datos y aprendizaje automático

## Fuente y criterio de lectura

Fuente exclusiva: [04 DataAnalyticDataMining_v15_es.pdf](/home/sergi/Documents/semesters/nine/bigdata/lessons/04%20DataAnalyticDataMining_v15_es.pdf), **37 páginas físicas**. Las referencias «p.» indican la página del PDF, contando la portada como 1, no un número de diapositiva inferido. Se revisaron también las láminas gráficas. Los ejemplos marcados **elaboración propia** son aplicaciones didácticas de las definiciones, no datos reales ni contenido adicional del curso.

**Idea central:** identificar patrones no es lo mismo que limpiar datos, representar resultados o tomar una decisión. Estas actividades se complementan, pero responden a preguntas distintas.

## 1. Antecedente: procesar datos para resolver un problema

Las máquinas tabuladoras de tarjetas perforadas de Herman Hollerith introducen el tema: permitieron recoger y contar datos con más rapidez y precisión que los métodos manuales y sentaron bases de IBM. El PDF contrasta el censo estadounidense de 1880, cuyo procesamiento demandaba ocho años, con la transformación iniciada en el censo de 1890. No presenta una demostración técnica de la máquina ni algoritmos actuales: su función en la clase es mostrar el valor del procesamiento de datos frente a un problema de volumen, tiempo y costo. **Fuente: pp. 2-4.**

## 2. Mapa de conceptos: qué hace cada actividad

| Concepto | Definición del material | Qué debe distinguirse |
|---|---|---|
| Minería de datos, *data mining* | Identificar patrones existentes en los datos. | La «minería» es la búsqueda; aplicar después el patrón es otra actividad. |
| Aprendizaje automático, *machine learning* | Usar algoritmos de reconocimiento de patrones para resolver problemas. | Es una herramienta posible de la minería, no su único recurso. |
| Ciencia de datos | Usar estadística, programación, métodos científicos y aprendizaje automático para extraer conocimientos. | El PDF reconoce cercanía e incluso uso indistinto con minería. |
| Estadística | Recopilar, analizar, modelizar y presentar datos. | Es un componente de la minería; entenderla ayuda a comprender los algoritmos. |
| Visualización | Representar datos mediante gráficos y diagramas. | La elección del gráfico depende de lo que se quiere representar. |
| Programación visual | Especificar procesamiento arrastrando y soltando objetos en un espacio de trabajo. | Cambia la forma de expresar el proceso, frente a instrucciones textuales. |
| Inteligencia empresarial, BI | Convertir datos en información, ideas y recomendaciones prácticas mediante minería, aprendizaje y visualización. | Su finalidad es empresarial y orientada a la acción. |

**Fuentes: pp. 5-9, 17-19 y 25.**

La minería puede utilizar **estadísticas, consultas a bases de datos, visualización, programación tradicional y aprendizaje automático**. Por ello, una respuesta que afirme «minería = machine learning» omite parte de la definición. El PDF presenta al analista y al científico de datos como perfiles de funciones cercanas: ambos analizan e interpretan datos y usan herramientas de minería; no establece una división profesional rígida. **Fuente: pp. 17-19.**

**Diagrama de síntesis, elaboración propia basada en pp. 5-10, 17-19 y 25-26:**

```text
Datos nuevos -> evaluar calidad y limpiar -> buscar patrones
                                           | estadísticas
                                           | consultas
                                           | programación
                                           | machine learning
                                           v
                                 representar e interpretar
                                           v
                         información y recomendaciones de BI
```

No se pretende fijar una metodología formal ausente del PDF; el dibujo relaciona las actividades que sí presenta.

## 3. Limpieza y calidad: preparar datos aptos para el uso

La limpieza es el primer paso señalado para trabajar con datos nuevos. Incluye eliminar duplicados, resolver valores nulos o no numéricos (*NaN*) e identificar valores atípicos. Puede hacerse con Excel, programación o herramientas de terceros. Antes de iniciar la minería, el analista debe evaluar la calidad y reconocer anomalías pendientes de resolver. El título «Data Cleansing and Wrangling» no viene acompañado de una definición independiente de *wrangling*, así que no se debe inventar una lista de etapas para ese término. **Fuente: pp. 10 y 26.**

La **calidad** mide la idoneidad de los datos para el uso. El PDF desarrolla cuatro factores:

| Factor | Pregunta que ayuda a reconocerlo | Ejemplo del PDF |
|---|---|---|
| Precisión | ¿El dato representa correctamente el valor del mundo real? | Temperaturas del sensor dentro del rango correcto. |
| Exhaustividad | ¿Están todos los valores requeridos? | Una hora de datos de un sensor que informa cada segundo, con el 100% de los valores. |
| Coherencia | ¿Se alinean los valores relacionados en todo el conjunto? | Cada aparición de una dirección tiene el mismo código postal. |
| Conformidad | ¿Los valores cumplen normas empresariales? | Medir y almacenar sensores a intervalos de un segundo. |

**Fuente: pp. 11-12.**

**Ejemplo, elaboración propia:** si falta una parte de los registros esperados del sensor, el problema es de exhaustividad; si todos están, pero una lectura representa incorrectamente la temperatura, es de precisión. Si una dirección aparece con códigos postales diferentes, es de coherencia. Si el muestreo se realiza con otro intervalo que el establecido por la empresa, afecta la conformidad. No basta decir «los datos están completos» para afirmar que tienen calidad en todos los factores.

**Confusión importante:** identificar un atípico no equivale automáticamente a demostrar que es un error ni a justificar borrarlo. El PDF enumera su identificación y resolución, pero no prescribe un método universal de tratamiento. Tampoco proporciona umbrales ni fórmulas de puntuación de calidad.

## 4. Cuatro operaciones sobre los datos

### Agrupación o clustering

Agrupa elementos relacionados en uno o más clústeres. El curso lo asocia al aprendizaje no supervisado: descubrir patrones sin etiquetas ni conjunto de entrenamiento según la formulación de las láminas. La p. 27 ofrece una imagen de distribución geográfica de casos; no explica un algoritmo de clustering, parámetros ni cálculo de distancias. **Fuente: pp. 13, 27 y 36.**

### Clasificación

Asigna un dato a una categoría: tumor benigno/maligno, correo válido/spam o transacción legítima/fraudulenta. En las láminas posteriores, los algoritmos de clasificación se presentan como ejemplos de aprendizaje supervisado. **Fuente: pp. 14 y 36.**

### Análisis predictivo

Utiliza datos para predecir qué ocurrirá en el futuro. La p. 28 ilustra puntos y curvas de ajuste; no desarrolla ecuaciones ni métodos de entrenamiento. La respuesta teórica debe explicar la finalidad predictiva, sin atribuir al PDF un algoritmo detallado de regresión. **Fuente: pp. 15 y 28.**

### Asociación

Identifica relaciones clave entre variables. El ejemplo es la **cesta de compra**: se estudia si un artículo presente, el **antecedente**, influye en la adición de otro, el **consecuente**. La p. 29 muestra filas con combinaciones de artículos. No presenta fórmulas de soporte, confianza o *lift*: no son parte de esta guía. **Fuente: pp. 16 y 29.**

**Comparación, elaboración propia basada en estas páginas:** para clientes de una tienda, formar grupos de compradores relacionados es agrupación; asignar una compra a legítima/fraudulenta es clasificación; estimar ventas futuras es predicción; estudiar artículos que aparecen juntos es asociación. Se distingue la pregunta, no solo el nombre de la herramienta.

## 5. Formas de aprendizaje automático

El PDF empieza por dos formas principales (p. 6) y después enumera **supervisado, no supervisado, reforzado y profundo** (pp. 35-37). Para responder según el temario completo, deben estudiarse las cuatro denominaciones.

| Forma | Explicación del PDF | Clave para recordar |
|---|---|---|
| Supervisado | Usa datos etiquetados para formar un conjunto de entrenamiento del que aprende el algoritmo. | Se dispone de resultados correctos. |
| No supervisado | Identifica patrones por descubrimiento, sin etiquetas ni conjunto de entrenamiento, según el texto de las láminas. | Agrupación y asociación son los ejemplos mencionados. |
| Reforzado | Usa bucles de retroalimentación para recompensar predicciones correctas y castigar errores. | La retroalimentación distingue esta explicación. |
| Profundo | Proceso jerárquico por capas: la salida de una capa se convierte en entrada de la siguiente. | La organización por capas es lo enfatizado. |

**Fuente: pp. 6 y 33-37.**

En el aprendizaje supervisado, el material señala la separación del conjunto de origen en **entrenamiento, normalmente 70%-80%, y prueba, el resto**. El conjunto de entrenamiento contiene variables independientes y resultados correctos de la dependiente; el algoritmo aprende examinando qué valores de las primeras conducen a los resultados. **Fuente: p. 37.**

**Ejemplo, elaboración propia:** en correos etiquetados válido/spam, los atributos observados corresponden a entradas y la categoría correcta a la salida que se busca aprender. La división entrenamiento/prueba separa los componentes que describe el PDF; el porcentaje indicado es «normalmente», no una obligación universal.

**Precaución de fidelidad:** la frase «no supervisado no utiliza un conjunto de entrenamiento» reproduce la simplificación literal del curso. El PDF no desarrolla los matices técnicos de esa terminología ni explica relaciones formales entre aprendizaje profundo y los demás enfoques. No memorizar clasificaciones adicionales como si fueran contenido de estas diapositivas.

## 6. Visualización: elegir según la pregunta

La clave es saber qué gráfico usar en cada momento. El PDF distingue siete finalidades, no una única visualización válida para todo:

| Familia | Qué representa | Pregunta ilustrativa, elaboración propia |
|---|---|---|
| Comparación temporal | Evolución de uno o varios conjuntos de valores. | ¿Cómo cambiaron las ventas durante los meses? |
| Comparación por categorías | Comparación de dos o más categorías. | ¿Qué vendedor tiene mayores ventas? |
| Composición | Relación de valores con un todo mayor. | ¿Qué parte del total aporta cada región? |
| Correlación | Relación entre dos o más variables. | ¿Cómo se relacionan dos variables medidas? |
| Cuadros de mando | Indicadores clave de rendimiento para seguir iniciativas. | ¿Cómo van los indicadores frente al objetivo? |
| Distribución | Frecuencia de valores en un conjunto. | ¿Con qué frecuencia aparecen ciertos valores? |
| Geocharts | Comparación de valores de distintas ubicaciones. | ¿Cómo se comparan las ventas entre países? |

**Fuente: pp. 7 y 20-23.**

Las imágenes muestran un cuadro con ventas por tiempo, objetivos, región y vendedor (p. 20), gráficos circulares, barras, puntos y áreas (p. 21), y mapa, indicadores tipo medidor y otras representaciones (p. 23). Sirven para reconocer diversidad de formas, no para deducir reglas estadísticas no explicadas. La BI puede usar cuadros de mando interactivos cuyo detalle aumenta al pulsar elementos. **Fuente: p. 25.**

## 7. Herramientas y ejemplos gráficos del curso

- **RapidMiner y Orange:** entornos de programación visual mencionados; la p. 24 muestra RapidMiner con operadores conectados en un flujo. El PDF no enseña instalación ni una práctica completa. **Fuente: pp. 8 y 24.**
- **Python:** lenguaje utilizado extensamente en el texto presentado por su popularidad y bibliotecas para minería y aprendizaje. No hay código desarrollado en estas láminas. **Fuente: p. 9.**
- **UCI y Kaggle:** repositorios citados para obtener conjuntos descargables. Su mención no implica que se hayan consultado fuentes externas para esta guía. **Fuente: p. 7.**
- **Big Data Mining and Analytics:** la p. 30 muestra un *Shard Router* conectado a cuatro fragmentos con datos West, East, North y South. Se puede reconocer una división de datos por región, pero la lámina no especifica un protocolo ni un algoritmo distribuido de minería.
- **Weka:** la p. 31 muestra el selector gráfico con Explorer, Experimenter, KnowledgeFlow, Workbench y Simple CLI. La p. 32 enumera limpieza, visualización, identificación de variables predictoras, agrupación, clasificación y asociación. El título «Future» no presenta una predicción temporal desarrollada. **Fuente: pp. 30-32.**

## 8. Confusiones que pueden costar puntos

1. **Minería no es solo aprendizaje automático:** también admite estadísticas, consultas, visualización y programación tradicional.
2. **Clustering no es clasificación:** agrupar relacionados no es asignarlos a categorías conocidas.
3. **Calidad no es solo limpieza:** calidad es idoneidad; limpieza actúa sobre problemas concretos.
4. **Exhaustividad no es precisión:** tener todas las lecturas no demuestra que sean correctas.
5. **Programación visual no es visualización:** una especifica procesamiento; la otra representa datos.
6. **Antecedente no es consecuente:** el primero está presente en la cesta y se estudia su relación con el segundo.
7. **Predicción no es descripción del pasado:** su finalidad explícita es anticipar el futuro.
8. **No añadir algoritmos al temario:** las láminas no desarrollan k-means, árboles, fórmulas de asociación ni redes por capas concretas.

## 9. Autoevaluación: preguntas tipo plancha

Responde sin consultar las soluciones. Son **preguntas de elaboración propia**, basadas en el estilo teórico de una plancha; no se afirma que hayan aparecido en un examen.

1. Defina minería de datos y aprendizaje automático. Explique su relación e indique cuatro recursos que puede utilizar la minería.
2. Compare minería de datos, ciencia de datos y estadística según el PDF.
3. Explique precisión, exhaustividad, coherencia y conformidad. Dé un ejemplo para cada factor.
4. Diferencie agrupación y clasificación, indicando ejemplos y la forma de aprendizaje asociada por el curso.
5. Explique asociación y análisis predictivo. Identifique antecedente y consecuente en un ejemplo de cesta.
6. Describa las cuatro formas de aprendizaje de las láminas finales.
7. Explique qué contiene el conjunto de entrenamiento supervisado y cómo se separa el conjunto de origen.
8. Enumere las siete familias de visualización y señale qué pregunta responde cada una.
9. Diferencie programación visual y visualización. Cite las herramientas y repositorios mencionados en el PDF.
10. Explique la finalidad de BI y por qué evaluar la calidad precede al proceso de minería.

---

## 10. Respuestas modelo

### 1. Minería y aprendizaje

La minería identifica patrones existentes en los datos. El aprendizaje automático usa algoritmos de reconocimiento de patrones para resolver problemas sin instrucciones explícitas paso a paso para cada tarea. Puede ser una herramienta de minería, pero la minería también utiliza estadística, consultas a bases de datos, visualización y programación tradicional. **Fuente: pp. 5-6 y 17.**

### 2. Disciplinas

Minería busca patrones; ciencia de datos extrae conocimientos con estadística, programación, métodos científicos y aprendizaje; estadística recopila, analiza, modeliza y presenta datos. El PDF reconoce que minería y ciencia de datos suelen usarse indistintamente, mientras sitúa la estadística como uno de los componentes de la minería. **Fuente: pp. 18-19.**

### 3. Calidad

Precisión es representar correctamente valores reales; exhaustividad, incluir todos los requeridos; coherencia, alinear valores relacionados; conformidad, cumplir normas empresariales. Los ejemplos del curso son temperaturas en rango correcto, todas las lecturas por segundo de una hora, código postal uniforme por dirección y frecuencia de un segundo conforme a la regla empresarial. **Fuente: pp. 11-12.**

### 4. Agrupación y clasificación

Agrupación reúne elementos relacionados en clústeres mediante aprendizaje no supervisado. Clasificación asigna categorías como válido/spam o legítimo/fraudulento; el material la presenta entre los algoritmos supervisados que aprenden de etiquetas. La diferencia es descubrir grupos frente a asignar categorías. **Fuente: pp. 13-14 y 35-36.**

### 5. Asociación y predicción

Asociación identifica relaciones entre variables; predicción usa datos para anticipar qué ocurrirá. **Ejemplo, elaboración propia:** estudiar si la presencia de pan se relaciona con añadir mantequilla tiene pan como antecedente y mantequilla como consecuente. No se ha demostrado esa relación con datos reales. **Fuente: pp. 15-16.**

### 6. Formas de aprendizaje

Supervisado aprende de datos etiquetados; no supervisado descubre patrones sin etiquetas, con agrupación y asociación como ejemplos; reforzado usa recompensas y castigos en bucles de retroalimentación; profundo organiza capas en las que una salida alimenta la siguiente. **Fuente: pp. 35-37.**

### 7. Entrenamiento y prueba

El conjunto supervisado contiene variables independientes y resultados correctos de la variable dependiente. El algoritmo aprende relaciones entre entradas y salidas. Normalmente se asigna 70%-80% al entrenamiento y el resto a prueba, según el PDF. **Fuente: p. 37.**

### 8. Familias gráficas

Temporal: evolución; categorías: comparación de categorías; composición: partes de un todo; correlación: relación de variables; cuadros de mando: indicadores de iniciativas; distribución: frecuencia; geocharts: comparación entre ubicaciones. **Fuente: p. 22.**

### 9. Herramientas

Programación visual expresa un procesamiento arrastrando y conectando objetos; visualización representa datos con gráficos. Los entornos citados son RapidMiner y Orange; el lenguaje destacado es Python; los repositorios son UCI y Kaggle. Weka aparece con tareas de limpieza, visualización, identificación de variables, agrupación, clasificación y asociación. **Fuente: pp. 7-9, 24 y 31-32.**

### 10. BI y preparación

BI convierte datos en información y recomendaciones empresariales prácticas mediante minería, aprendizaje y visualización. Antes de minar, se debe determinar si los datos son idóneos y resolver anomalías como ausencias, duplicados o atípicos; de lo contrario, se estaría trabajando con problemas no resueltos del conjunto. **Fuente: pp. 9-12 y 25-26.**
