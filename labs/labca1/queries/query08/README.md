# Query 08: extremos de ocupación

Pregunta: ¿qué departamentos tuvieron la menor y mayor TNOH media de cada año?

- Campos: `ANIO`, `DEPARTAMENTO`, `PORCENTAJE_TNOH`.
- Filtro: solo filas TT/TT.
- Clase principal: `labca1.query08.OccupancyExtremesDriver`.
- Salida: año, departamento mínimo y departamento máximo.

El reducer recibe todas las filas de cada año, calcula primero la media de cada departamento y luego selecciona ambos extremos. Por eso no necesita una segunda etapa.

```bash
../../../../hadoop3/scripts/run-job.sh --source ../common/src --source src --main labca1.query08.OccupancyExtremesDriver --input ../../data/Indicadores_ocupabilidad_2019_2025.csv --output query08
```
