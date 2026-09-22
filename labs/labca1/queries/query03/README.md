# Query 03: ocupación por clase

Pregunta: ¿cómo cambian la TNOH y la TNOC según la clase de hospedaje?

- Campos: `ANIO`, `CLASE`, `PORCENTAJE_TNOH`, `PORCENTAJE_TNOC`.
- Filtro: categorías totales, sin la clase total TT.
- Clase principal: `labca1.query03.OccupancyByClassDriver`.
- Salida: año, clase y medias de TNOH y TNOC.

```bash
../../../../hadoop3/scripts/run-job.sh --source ../common/src --source src --main labca1.query03.OccupancyByClassDriver --input ../../data/Indicadores_ocupabilidad_2019_2025.csv --output query03
```
