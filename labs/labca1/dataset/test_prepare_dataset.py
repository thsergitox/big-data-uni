import csv
import tempfile
import unittest
from collections import defaultdict
from pathlib import Path

from labs.labca1.dataset.prepare_dataset import (
    CANONICAL_HEADER,
    OLD_HEADER,
    consolidate,
    normalise_row,
)


DATA_DIR = Path(__file__).resolve().parents[1] / "data"
INPUT_NAMES = (
    "Indicadores_ocupabilidad_2019.csv",
    "Indicadores_ocupabilidad_2020.csv",
    "Indicadores_ocupabilidad_2021.csv",
    "Indicadores_ocupabilidad_2022.csv",
    "Indicadores_ocupabilidad_2023.csv",
    "Indicadores_ocupabilidad_2024.csv",
    "Indicadores_ocupabilidad_2025_1er_semestre.csv",
)


class PrepareDatasetTest(unittest.TestCase):
    def test_normalises_old_schema_and_decimal_comma(self):
        header = list(OLD_HEADER)
        row = [
            "2019",
            "01",
            "TT",
            "TODAS CONSOLIDADAS",
            "TT",
            "TODAS CONSOLIDADAS",
            "LIMA",
            "150000",
        ] + ["1"] * 3 + ["27,45"] + ["1"] * 11

        result = normalise_row(header, row)

        self.assertEqual(24, len(result))
        self.assertEqual("", result[0])
        self.assertEqual("2019", result[1])
        self.assertEqual("27.45", result[12])

    def test_rejects_unknown_column_count(self):
        with self.assertRaisesRegex(ValueError, "columnas"):
            normalise_row(["A", "B"], ["1", "2"])

    def test_rejects_unknown_header_with_valid_column_count(self):
        header = [f"UNKNOWN_{index}" for index in range(23)]
        row = ["1"] * 23

        with self.assertRaisesRegex(ValueError, "cabecera"):
            normalise_row(header, row)

    def test_preserves_new_schema_and_decimal_point(self):
        row = [
            "20260512",
            "2025",
            "01",
            "TT",
            "TODAS CONSOLIDADAS",
            "TT",
            "TODAS CONSOLIDADAS",
            "LIMA",
            "150000",
            "10",
            "100",
            "200",
            "27.45",
            "20.10",
            "1.5",
            "1.4",
            "2.0",
            "1000",
            "900",
            "100",
            "1500",
            "1300",
            "200",
            "50",
        ]

        result = normalise_row(list(CANONICAL_HEADER), row)

        self.assertEqual(row, result)

    def test_consolidates_all_resources(self):
        input_paths = [DATA_DIR / name for name in INPUT_NAMES]

        with tempfile.TemporaryDirectory() as temporary_directory:
            output_path = Path(temporary_directory) / "consolidated.csv"
            count = consolidate(input_paths, output_path)
            raw_output = output_path.read_bytes()
            decoded_output = raw_output.decode("utf-8")
            rows = list(csv.reader(decoded_output.splitlines(), delimiter=";"))

        self.assertEqual(38_730, count)
        self.assertEqual(list(CANONICAL_HEADER), rows[0])
        self.assertEqual(38_731, len(rows))

        expected_months = {
            year: {f"{month:02d}" for month in range(1, 13)}
            for year in range(2019, 2025)
        }
        expected_months[2025] = {f"{month:02d}" for month in range(1, 7)}

        months_by_year = defaultdict(set)
        departments_by_period = defaultdict(set)
        for row in rows[1:]:
            self.assertEqual(24, len(row))
            year = int(row[1])
            month = row[2]
            months_by_year[year].add(month)
            departments_by_period[(year, month)].add(row[7])
            for value in row[9:24]:
                float(value)

        self.assertEqual(expected_months, dict(months_by_year))
        self.assertTrue(departments_by_period)
        self.assertTrue(
            all(len(departments) == 25 for departments in departments_by_period.values())
        )


if __name__ == "__main__":
    unittest.main()
