# Query 06: estadísticas de TNOH

Pregunta: ¿cuál es la cantidad, media, mediana y desviación estándar poblacional de la TNOH?

- Campo: `PORCENTAJE_TNOH`.
- Filtro: solo filas TT/TT.
- Clase principal: `labca1.query06.OccupancyStatisticsDriver`.
- Salida: cantidad, media, mediana y desviación estándar.

```bash
../../../../hadoop3/scripts/run-job.sh --source ../common/src --source src --main labca1.query06.OccupancyStatisticsDriver --input ../../data/Indicadores_ocupabilidad_2019_2025.csv --output query06
```
