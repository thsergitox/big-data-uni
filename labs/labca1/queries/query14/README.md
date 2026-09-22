# Query 14: regresión ridge

Pregunta: ¿qué TNOH se estima al controlar el tamaño de los coeficientes con regularización L2?

- Entrenamiento: 2019-2023.
- Prueba: 2024.
- Validación reciente: primer semestre de 2025.
- Características: las mismas siete variables de query13.
- Regularización: `lambda=1`, sin regularizar el intercepto.
- Clase principal: `labca1.query14.RidgeRegressionDriver`.

La salida incluye intercepto, coeficientes, muestras, MAE, RMSE y R cuadrado para compararla directamente con query13.

```bash
../../../../hadoop3/scripts/run-job.sh --source ../common/src --source src --main labca1.query14.RidgeRegressionDriver --input ../../data/Indicadores_ocupabilidad_2019_2025.csv --output query14
```
