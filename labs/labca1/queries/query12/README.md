# Query 12: clasificación con regresión logística

Pregunta: ¿la TNOH mensual departamental es alta o baja según los demás indicadores?

- Entrenamiento: 2019-2023.
- Prueba: 2024.
- Validación reciente: primer semestre de 2025.
- Etiqueta alta: TNOH mayor o igual a la mediana del entrenamiento.
- Características: las mismas siete variables de query11, estandarizadas solo con entrenamiento.
- Configuración: 200 iteraciones y tasa de aprendizaje 0.1.
- Clase principal: `labca1.query12.LogisticClassificationDriver`.

La salida incluye matriz de confusión, accuracy, precision, recall, F1 y log loss.

```bash
../../../../hadoop3/scripts/run-job.sh --source ../common/src --source src --main labca1.query12.LogisticClassificationDriver --input ../../data/Indicadores_ocupabilidad_2019_2025.csv --output query12
```
