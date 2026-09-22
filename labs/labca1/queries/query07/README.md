# Query 07: búsqueda de subtexto

Pregunta: ¿qué registros contienen un texto en clase, categoría o departamento?

- Campos: `CLASE`, `CATEGORIA`, `DEPARTAMENTO`.
- Búsqueda: configurable, sin distinguir mayúsculas, conservando acentos.
- Clase principal: `labca1.query07.SubstringSearchDriver`.
- Salida: desplazamiento y registro canónico completo.

```bash
../../../../hadoop3/scripts/run-job.sh --source ../common/src --source src --main labca1.query07.SubstringSearchDriver --input ../../data/Indicadores_ocupabilidad_2019_2025.csv --output query07 --job-arg hotel
```
