# Query 10: participación de arribos extranjeros

Pregunta: ¿qué porcentaje de los arribos fue extranjero en cada departamento y periodo?

- Campos: `ANIO`, `MES`, `DEPARTAMENTO`, `TOTAL_ARRIBOS`, `TOTAL_ARRIBOS_EXT`.
- Filtro: solo filas TT/TT con arribos mayores que cero.
- Clase principal: `labca1.query10.ForeignShareDriver`.
- Etapa 1: calcula el porcentaje mensual de arribos extranjeros.
- Etapa 2: obtiene la media por departamento y periodo.
- Salida: departamento, periodo y participación extranjera media.

Los periodos son `PRE_2019`, `IMPACT_2020_2021`, `RECOVERY_2022_2024` y `CURRENT_2025_H1`.

```bash
../../../../hadoop3/scripts/run-job.sh --source ../common/src --source src --main labca1.query10.ForeignShareDriver --input ../../data/Indicadores_ocupabilidad_2019_2025.csv --output query10
```
