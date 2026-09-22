#!/usr/bin/env python3
"""Perfila el dataset 'Indicadores de Ocupabilidad' y reproduce los 3 hallazgos.

1. Cambio de esquema entre 2023 y 2024
2. Filas agregadas 'TODAS CONSOLIDADAS' (doble conteo)
3. Cardinalidad de las dimensiones

Uso:  python3 tools/perfilar_dataset.py     (requiere haber corrido descargar_dataset.py)
"""
import csv, io, os, collections

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CSV_DIR = os.path.join(BASE, "datos", "csv")


def load(year):
    fn = ("Indicadores_ocupabilidad_2025_1er_semestre.csv" if year == 2025
          else f"Indicadores_ocupabilidad_{year}.csv")
    path = os.path.join(CSV_DIR, fn)
    if not os.path.exists(path):
        raise FileNotFoundError(f"Falta {path}. Corre tools/descargar_dataset.py primero.")
    txt = open(path, "rb").read().decode("utf-8-sig", errors="replace")
    return [r for r in csv.DictReader(io.StringIO(txt), delimiter=";")
            if any((v or "").strip() for v in r.values())]


def header_of(year):
    fn = ("Indicadores_ocupabilidad_2025_1er_semestre.csv" if year == 2025
          else f"Indicadores_ocupabilidad_{year}.csv")
    txt = open(os.path.join(CSV_DIR, fn), "rb").read().decode("utf-8-sig", errors="replace")
    return next(csv.reader(io.StringIO(txt), delimiter=";"))


def main():
    print("=" * 72)
    print("HALLAZGO 1 - CAMBIO DE ESQUEMA")
    print("=" * 72)
    for y in (2015, 2019, 2021, 2022, 2023, 2024, 2025):
        h = header_of(y)
        print(f"  {y}: {len(h):>2} columnas | primeras: {h[:4]}")
    a, b = header_of(2023), header_of(2024)
    print(f"\n  Solo en 2023: {[c for c in a if c not in b]}")
    print(f"\n  Solo en 2024: {[c for c in b if c not in a]}")
    print(f"\n  Columnas con nombre idéntico entre ambos: {[c for c in a if c in b]}")

    print()
    print("=" * 72)
    print("HALLAZGO 2 - FILAS AGREGADAS (DOBLE CONTEO)")
    print("=" * 72)
    for y in (2022, 2023, 2024, 2025):
        rows = load(y)
        # el nombre de las columnas difiere segun el esquema
        def is_consolidated(r):
            return any(str(v).strip().upper() == "TODAS CONSOLIDADAS"
                       for k, v in r.items() if "CLASE" in k.upper() or "CATEGOR" in k.upper())
        n = sum(1 for r in rows if is_consolidated(r))
        print(f"  {y}: {n:>5,} de {len(rows):>5,} filas son agregados "
              f"({100 * n / len(rows):.1f} %)")
    print("\n  -> Filtrar antes de sumar, o sumar SOLO las filas consolidadas.")
    print("     NUNCA: df.groupby('DEPARTAMENTO').NUMERO_PLAZAS_CAMA.sum() sin filtrar")

    print()
    print("=" * 72)
    print("HALLAZGO 3 - CARDINALIDAD DE DIMENSIONES")
    print("=" * 72)
    rows = load(2024)
    dep = {r.get("DEPARTAMENTO", "").strip() for r in rows} - {""}
    cla = {r.get("CLASE", "").strip() for r in rows} - {""}
    cat = {r.get("CATEGORIA", "").strip() for r in rows} - {""}
    combos = {(r.get("DEPARTAMENTO", "").strip(), r.get("CLASE", "").strip(),
               r.get("CATEGORIA", "").strip()) for r in rows}
    meses = sorted({r.get("MES", "").strip() for r in rows} - {""})
    print(f"  2024 -> {len(rows):,} filas")
    print(f"    DEPARTAMENTO : {len(dep)}")
    print(f"    CLASE        : {len(cla)} -> {sorted(cla)}")
    print(f"    CATEGORIA    : {len(cat)} -> {sorted(cat)}")
    print(f"    Combinaciones dep x clase x categoria: {len(combos)}")
    print(f"    Meses presentes: {len(meses)} -> {meses}")

    print()
    print("=" * 72)
    print("TRAMPAS DE PARSEO")
    print("=" * 72)
    print("  separador      : ';'      (no coma)")
    print("  encoding       : UTF-8 con BOM  -> usar encoding='utf-8-sig'")
    print("  decimal        : COMA ('9,36')  -> decimal=',' o locale es_PE")
    print("  2025           : solo enero-junio (3 068 filas, no ~6 100)")
    print("  TOTAL_EMPLEO   : solo existe desde 2024 -> rompe un concat ingenuo")
    print("  descarga       : exige header User-Agent")

    print()
    print("=" * 72)
    print("VERIFICACION CRUZADA PENDIENTE")
    print("=" * 72)
    print("  Calcular el TNOC nacional 2024 desde los CSV y compararlo con el")
    print("  oficial de MINCETUR: 35,5 % (mincetur_informe_hospedaje2024).")
    print("  Si coincide, el filtro y el parseo son correctos.")


if __name__ == "__main__":
    main()
