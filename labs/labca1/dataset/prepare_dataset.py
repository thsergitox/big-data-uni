import argparse
import csv
from collections.abc import Sequence
from pathlib import Path


OLD_HEADER = (
    "AÑO",
    "MES",
    "CLASE",
    "DESCRIPCIÓN CLASE",
    "CATEGORÍA",
    "DESCRIPCIÓN CATEGORÍA",
    "DEPARTAMENTO",
    "UBIGEO",
    "NÚMERO DE ESTABLECIMIENTO",
    "NÚMERO DE HABITACIONES",
    "NÚMERO DE PLAZAS-CAMA",
    "TNOH EN EL MES(%)",
    "TNOC EN EL MES(%)",
    "PROM PERMANENCIA(DÍAS)",
    "PROM PERMANENCIA - NAC (DÍAS)",
    "PROM PERMANENCIA - EXT (DÍAS)",
    "TOTAL DE ARRIBOS EN EL MES",
    "TOTAL DE ARRIBOS MES - NAC",
    "TOTAL DE ARRIBOS MES - EXT",
    "TOTAL PERNOCT MES",
    "TOTAL PERNOCT MES - NAC (DÍAS)",
    "TOTAL PERNOCT MES - EXT (DÍAS)",
    "TOTAL EMPLEO EN EL MES",
)

CANONICAL_HEADER = (
    "FECHA_CORTE",
    "ANIO",
    "MES",
    "ID_CLASE",
    "CLASE",
    "ID_CATEGORIA",
    "CATEGORIA",
    "DEPARTAMENTO",
    "ID_UBIGEO",
    "NUMERO_ESTABLECIMIENTOS",
    "NUMERO_HABITACIONES",
    "NUMERO_PLAZAS_CAMA",
    "PORCENTAJE_TNOH",
    "PORCENTAJE_TNOC",
    "PROMEDIO_PERMANENCIA",
    "PROMEDIO_PERMANENCIA_NAC",
    "PROMEDIO_PERMANENCIA_EXT",
    "TOTAL_ARRIBOS",
    "TOTAL_ARRIBOS_NAC",
    "TOTAL_ARRIBOS_EXT",
    "TOTAL_PERNOCT",
    "TOTAL_PERNOCT_NAC",
    "TOTAL_PERNOCT_EXT",
    "TOTAL_EMPLEO",
)

DECIMAL_INDEXES = range(12, 17)
SOURCE_FILENAMES = (
    "Indicadores_ocupabilidad_2019.csv",
    "Indicadores_ocupabilidad_2020.csv",
    "Indicadores_ocupabilidad_2021.csv",
    "Indicadores_ocupabilidad_2022.csv",
    "Indicadores_ocupabilidad_2023.csv",
    "Indicadores_ocupabilidad_2024.csv",
    "Indicadores_ocupabilidad_2025_1er_semestre.csv",
)


def normalise_row(header: list[str], row: list[str]) -> list[str]:
    if tuple(header) == OLD_HEADER and len(row) == len(OLD_HEADER):
        result = [""] + row
    elif tuple(header) == CANONICAL_HEADER and len(row) == len(CANONICAL_HEADER):
        result = list(row)
    elif len(header) not in (len(OLD_HEADER), len(CANONICAL_HEADER)) or len(
        row
    ) not in (len(OLD_HEADER), len(CANONICAL_HEADER)):
        raise ValueError(f"número de columnas inválido: {len(row)}")
    else:
        raise ValueError("cabecera desconocida")

    for index in DECIMAL_INDEXES:
        result[index] = result[index].replace(",", ".")
        float(result[index])

    if not 2019 <= int(result[1]) <= 2025:
        raise ValueError(f"año fuera de alcance: {result[1]}")

    return result


def consolidate(input_paths: list[Path], output_path: Path) -> int:
    output_path.parent.mkdir(parents=True, exist_ok=True)
    record_count = 0

    with output_path.open("w", encoding="utf-8", newline="") as output_file:
        writer = csv.writer(output_file, delimiter=";", lineterminator="\n")
        writer.writerow(CANONICAL_HEADER)

        for input_path in input_paths:
            with input_path.open("r", encoding="latin-1", newline="") as input_file:
                reader = csv.reader(input_file, delimiter=";")
                header = next(reader)
                for row_number, row in enumerate(reader, start=2):
                    try:
                        writer.writerow(normalise_row(header, row))
                    except ValueError as error:
                        raise ValueError(
                            f"{input_path.name}, fila {row_number}: {error}"
                        ) from error
                    record_count += 1

    return record_count


def parse_arguments(arguments: Sequence[str] | None = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Consolida los indicadores de ocupabilidad de 2019 a 2025."
    )
    parser.add_argument("--input-dir", required=True, type=Path)
    parser.add_argument("--output", required=True, type=Path)
    return parser.parse_args(arguments)


def main(arguments: Sequence[str] | None = None) -> int:
    options = parse_arguments(arguments)
    input_paths = [options.input_dir / name for name in SOURCE_FILENAMES]
    record_count = consolidate(input_paths, options.output)
    print(f"Registros consolidados: {record_count}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
