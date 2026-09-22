# Query 09: cambio de ocupación frente a 2019

Pregunta: ¿cuántos puntos porcentuales cambió la TNOH media de cada departamento entre 2019 y 2024?

- Campos: `ANIO`, `DEPARTAMENTO`, `PORCENTAJE_TNOH`.
- Filtro: solo filas TT/TT de 2019 y 2024.
- Clase principal: `labca1.query09.OccupancyChangeDriver`.
- Etapa 1: calcula la TNOH media por departamento y año.
- Etapa 2: enlaza 2019 con 2024 y calcula la diferencia.
- Salida: departamento, ambos promedios y cambio en puntos porcentuales.

```bash
../../../../hadoop3/scripts/run-job.sh --source ../common/src --source src --main labca1.query09.OccupancyChangeDriver --input ../../data/Indicadores_ocupabilidad_2019_2025.csv --output query09
```
