# Query 11: clasificación con Gaussian Naive Bayes

Pregunta: ¿la TNOH mensual departamental es alta o baja según los demás indicadores?

- Entrenamiento: 2019-2023.
- Prueba: 2024.
- Validación reciente: primer semestre de 2025.
- Etiqueta alta: TNOH mayor o igual a la mediana del entrenamiento.
- Características: mes, establecimientos, habitaciones, camas, arribos por habitación, pernoctaciones por cama y empleo por establecimiento.
- Clase principal: `labca1.query11.NaiveBayesClassificationDriver`.

La TNOH y la TNOC no forman parte de las características. La salida incluye matriz de confusión, accuracy, precision, recall, F1 y log loss.

```bash
../../../../hadoop3/scripts/run-job.sh --source ../common/src --source src --main labca1.query11.NaiveBayesClassificationDriver --input ../../data/Indicadores_ocupabilidad_2019_2025.csv --output query11
```
