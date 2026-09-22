# Query 01: arribos por origen

Pregunta: ¿cómo evolucionaron los arribos nacionales y extranjeros por año?

- Campos: `ANIO`, `TOTAL_ARRIBOS_NAC`, `TOTAL_ARRIBOS_EXT`.
- Filtro: solo filas con `ID_CLASE=TT` e `ID_CATEGORIA=TT`.
- Clase principal: `labca1.query01.ArrivalsByOriginDriver`.
- Salida: año, total nacional y total extranjero.

```bash
../../../../hadoop3/scripts/run-job.sh --source ../common/src --source src --main labca1.query01.ArrivalsByOriginDriver --input ../../data/Indicadores_ocupabilidad_2019_2025.csv --output query01
```
