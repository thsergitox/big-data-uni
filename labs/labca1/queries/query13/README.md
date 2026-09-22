# Query 13: regresión lineal

Pregunta: ¿qué TNOH se estima para cada registro mensual departamental usando los demás indicadores?

- Entrenamiento: 2019-2023.
- Prueba: 2024.
- Validación reciente: primer semestre de 2025.
- Características: mes, establecimientos, habitaciones, camas, arribos por habitación, pernoctaciones por cama y empleo por establecimiento.
- Regularización: ninguna (`lambda=0`).
- Clase principal: `labca1.query13.LinearRegressionDriver`.

Las variables se estandarizan usando solo el entrenamiento. La salida incluye intercepto, coeficientes, muestras, MAE, RMSE y R cuadrado.

```bash
../../../../hadoop3/scripts/run-job.sh --source ../common/src --source src --main labca1.query13.LinearRegressionDriver --input ../../data/Indicadores_ocupabilidad_2019_2025.csv --output query13
```
