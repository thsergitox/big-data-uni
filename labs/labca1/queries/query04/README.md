# Query 04: empleo respecto a capacidad

Pregunta: ¿cuántos empleos existen por cada cien habitaciones en cada departamento y año?

- Campos: `ANIO`, `DEPARTAMENTO`, `TOTAL_EMPLEO`, `NUMERO_HABITACIONES`.
- Filtro: solo filas TT/TT.
- Clase principal: `labca1.query04.EmploymentCapacityDriver`.
- Salida: año, departamento y empleos por cien habitaciones.

```bash
../../../../hadoop3/scripts/run-job.sh --source ../common/src --source src --main labca1.query04.EmploymentCapacityDriver --input ../../data/Indicadores_ocupabilidad_2019_2025.csv --output query04
```
