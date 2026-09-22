# Query 02: ocupación por departamento

Pregunta: ¿cuál fue la TNOH media de cada departamento por año?

- Campos: `ANIO`, `DEPARTAMENTO`, `PORCENTAJE_TNOH`.
- Filtro: solo filas TT/TT.
- Clase principal: `labca1.query02.OccupancyByDepartmentDriver`.
- Salida: año, departamento y TNOH media.

```bash
../../../../hadoop3/scripts/run-job.sh --source ../common/src --source src --main labca1.query02.OccupancyByDepartmentDriver --input ../../data/Indicadores_ocupabilidad_2019_2025.csv --output query02
```
