# Query 05: estacionalidad

Pregunta: ¿qué meses concentran más arribos y pernoctaciones?

- Campos: `MES`, `TOTAL_ARRIBOS`, `TOTAL_PERNOCT`.
- Filtro: solo filas TT/TT.
- Clase principal: `labca1.query05.SeasonalityDriver`.
- Salida: mes, arribos y pernoctaciones acumuladas.

```bash
../../../../hadoop3/scripts/run-job.sh --source ../common/src --source src --main labca1.query05.SeasonalityDriver --input ../../data/Indicadores_ocupabilidad_2019_2025.csv --output query05
```
