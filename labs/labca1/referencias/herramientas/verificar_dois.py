#!/usr/bin/env python3
"""Verifica los DOI del .bib contra Crossref, comparando el titulo devuelto.

NUNCA emitir un DOI de memoria: un DOI incorrecto parece verificado y es peor
que uno ausente. Este script compara el titulo que devuelve Crossref con el que
esta escrito en el .bib y reporta discrepancias.

Usa mailto= (Crossref es mas permisivo con un contacto) y pausas de ~1 s.
Un ThreadPoolExecutor agresivo produce HTTP 429.

Uso:  python3 tools/verificar_dois.py [ruta.bib]
"""
import json, os, re, sys, time, urllib.parse, urllib.request

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DEFAULT_BIB = os.path.join(BASE, "referencias.bib")
MAILTO = "arbues.perez.v@uni.pe"


def parse_bib(path):
    txt = open(path, encoding="utf-8").read()
    out = []
    for full, key in re.findall(r"(@\w+\{([^,]+),.*?\n\})", txt, flags=re.S):
        doi = re.search(r"\n\s*doi\s*=\s*\{(.*?)\}", full)
        title = re.search(r"\n\s*title\s*=\s*\{(.*?)\}\s*,?\n", full, flags=re.S)
        if doi:
            out.append({
                "key": key.strip(),
                "doi": doi.group(1).strip(),
                "title": re.sub(r"\s+", " ", title.group(1)).strip() if title else "",
            })
    return out


def norm(s):
    s = re.sub(r"\\['\"`^~=.]", "", s or "")
    return re.sub(r"[^a-z0-9 ]", " ", s.lower()).split()


def resolve(doi):
    url = f"https://api.crossref.org/works/{urllib.parse.quote(doi)}?mailto={MAILTO}"
    req = urllib.request.Request(url, headers={"User-Agent": f"ref-check/1.0 (mailto:{MAILTO})"})
    with urllib.request.urlopen(req, timeout=45) as f:
        return json.load(f)["message"]


def main():
    path = sys.argv[1] if len(sys.argv) > 1 else DEFAULT_BIB
    entries = parse_bib(path)
    print(f"{len(entries)} entradas con DOI en {os.path.basename(path)}\n")

    ok = mismatch = fail = 0
    for e in entries:
        try:
            m = resolve(e["doi"])
            got = (m.get("title") or [""])[0]
            want = norm(e["title"])
            have = norm(got)
            # comparar por solapamiento de tokens (Crossref a veces acorta titulos)
            overlap = len(set(want) & set(have)) / max(1, len(set(want)))
            if overlap >= 0.6:
                ok += 1
                print(f"  OK    {e['key']}")
                print(f"        bib  : {e['title'][:88]}")
                print(f"        cross: {got[:88]}")
            else:
                mismatch += 1
                print(f"  REVISAR {e['key']}  (solapamiento {overlap:.0%})")
                print(f"        bib  : {e['title'][:88]}")
                print(f"        cross: {got[:88]}")
        except Exception as ex:
            fail += 1
            print(f"  FALLO {e['key']}: {ex}")
        time.sleep(1.0)

    print(f"\nResumen: {ok} coinciden | {mismatch} a revisar | {fail} fallos")
    print("\nNota: Crossref a veces devuelve el titulo corto (ej. 'MapReduce' en lugar")
    print("de 'MapReduce: simplified data processing on large clusters'). En ese caso")
    print("el .bib conserva el titulo completo del articulo, y eso es correcto.")
    return 0 if (mismatch == 0 and fail == 0) else 1


if __name__ == "__main__":
    sys.exit(main())
