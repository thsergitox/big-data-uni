import csv
import unittest
from collections import defaultdict
from pathlib import Path


FIXTURE = Path(__file__).parent / "fixtures" / "overall-small.csv"


def read_rows():
    with FIXTURE.open(encoding="utf-8", newline="") as source:
        return list(csv.DictReader(source, delimiter=";"))


def overall_rows():
    return [
        row
        for row in read_rows()
        if row["ID_CLASE"] == "TT" and row["ID_CATEGORIA"] == "TT"
    ]


def query01_reference():
    totals = defaultdict(lambda: [0, 0])
    for row in overall_rows():
        totals[row["ANIO"]][0] += int(row["TOTAL_ARRIBOS_NAC"])
        totals[row["ANIO"]][1] += int(row["TOTAL_ARRIBOS_EXT"])
    return [
        f"{year}\tnational={national}\tforeign={foreign}"
        for year, (national, foreign) in sorted(totals.items())
    ]


def query02_reference():
    values = defaultdict(list)
    for row in overall_rows():
        values[(row["ANIO"], row["DEPARTAMENTO"])].append(
            float(row["PORCENTAJE_TNOH"])
        )
    return [
        f"{year};{department}\tmean_tnoh={sum(items) / len(items):.4f}"
        for (year, department), items in sorted(values.items())
    ]


def query03_reference():
    values = defaultdict(lambda: [0.0, 0.0, 0])
    for row in read_rows():
        if row["ID_CATEGORIA"] == "TT" and row["ID_CLASE"] != "TT":
            key = (row["ANIO"], row["CLASE"])
            values[key][0] += float(row["PORCENTAJE_TNOH"])
            values[key][1] += float(row["PORCENTAJE_TNOC"])
            values[key][2] += 1
    return [
        f"{year};{class_name}\tmean_tnoh={tnoh / count:.4f}\tmean_tnoc={tnoc / count:.4f}"
        for (year, class_name), (tnoh, tnoc, count) in sorted(values.items())
    ]


def query04_reference():
    totals = defaultdict(lambda: [0, 0])
    for row in overall_rows():
        key = (row["ANIO"], row["DEPARTAMENTO"])
        totals[key][0] += int(row["TOTAL_EMPLEO"])
        totals[key][1] += int(row["NUMERO_HABITACIONES"])
    return [
        f"{year};{department}\temployment_per_100_rooms={100 * employment / rooms:.4f}"
        for (year, department), (employment, rooms) in sorted(totals.items())
    ]


def query05_reference():
    totals = defaultdict(lambda: [0, 0])
    for row in overall_rows():
        totals[row["MES"]][0] += int(row["TOTAL_ARRIBOS"])
        totals[row["MES"]][1] += int(row["TOTAL_PERNOCT"])
    return [
        f"{month}\tarrivals={arrivals}\tovernight_stays={nights}"
        for month, (arrivals, nights) in sorted(totals.items())
    ]


class ReferenceResultsTest(unittest.TestCase):
    def test_query01(self):
        self.assertEqual(
            [
                "2019\tnational=230\tforeign=70",
                "2024\tnational=120\tforeign=40",
                "2025\tnational=120\tforeign=60",
            ],
            query01_reference(),
        )

    def test_query02(self):
        self.assertEqual(
            [
                "2019;LIMA\tmean_tnoh=25.0000",
                "2024;CUSCO\tmean_tnoh=40.0000",
                "2025;CUSCO\tmean_tnoh=50.0000",
            ],
            query02_reference(),
        )

    def test_query03(self):
        self.assertEqual(
            ["2019;3 ESTRELLAS\tmean_tnoh=60.0000\tmean_tnoc=55.0000"],
            query03_reference(),
        )

    def test_query04(self):
        self.assertEqual(
            [
                "2019;LIMA\temployment_per_100_rooms=55.0000",
                "2024;CUSCO\temployment_per_100_rooms=50.0000",
                "2025;CUSCO\temployment_per_100_rooms=50.0000",
            ],
            query04_reference(),
        )

    def test_query05(self):
        self.assertEqual(
            [
                "01\tarrivals=440\tovernight_stays=660",
                "02\tarrivals=200\tovernight_stays=300",
            ],
            query05_reference(),
        )


if __name__ == "__main__":
    unittest.main()
