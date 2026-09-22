#!/usr/bin/env python3
"""Descarga los 11 CSV del dataset 'Indicadores de Ocupabilidad' de MINCETUR.

REQUISITO: header User-Agent. Sin el, las peticiones fallan en silencio.

Uso:  python3 tools/descargar_dataset.py
Salida: biblio/csv/*.csv  +  biblio/dataset_stats.json
"""
import csv, io, json, os, sys, urllib.request

BASE = "https://datosabiertos.mincetur.gob.pe/DGIETA"
OUT_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "datos")
CSV_DIR = os.path.join(OUT_DIR, "csv")
UA = {"User-Agent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 Chrome/126 Safari/537.36"}

URLS = {y: f"{BASE}/Indicadores_ocupabilidad_{y}.csv" for y in range(2015, 2025)}
URLS[2025] = f"{BASE}/Indicadores_ocupabilidad_2025_1er_semestre.csv"


def fetch(url, dest):
    req = urllib.request.Request(url, headers=UA)
    with urllib.request.urlopen(req, timeout=180) as r, open(dest, "wb") as f:
        f.write(r.read())
    return os.path.getsize(dest)


def main():
    os.makedirs(CSV_DIR, exist_ok=True)
    stats = {}
    for year, url in sorted(URLS.items()):
        dest = os.path.join(CSV_DIR, os.path.basename(url))
        if not os.path.exists(dest) or os.path.getsize(dest) < 1000:
            try:
                fetch(url, dest)
            except Exception as e:
                print(f"ERROR {year}: {e}", file=sys.stderr)
                continue
        raw = open(dest, "rb").read()
        txt = raw.decode("utf-8-sig", errors="replace")
        rows = list(csv.reader(io.StringIO(txt), delimiter=";"))
        header, body = rows[0], [r for r in rows[1:] if any(c.strip() for c in r)]
        stats[year] = {"bytes": len(raw), "cols": len(header),
                       "rows": len(body), "header": header}
        print(f"{year}: {len(raw):>9,} bytes | {len(header)} columnas | {len(body):>6,} filas")

    total_2225 = sum(stats[y]["rows"] for y in (2022, 2023, 2024, 2025) if y in stats)
    print(f"\nTOTAL 2022-2025: {total_2225:,} filas")
    print(f"TOTAL 2015-2025: {sum(v['rows'] for v in stats.values()):,} filas")

    with open(os.path.join(OUT_DIR, "dataset_stats.json"), "w") as f:
        json.dump({str(k): v for k, v in stats.items()}, f, ensure_ascii=False, indent=1)
    print(f"\n-> {os.path.join(OUT_DIR, 'dataset_stats.json')}")


if __name__ == "__main__":
    main()
