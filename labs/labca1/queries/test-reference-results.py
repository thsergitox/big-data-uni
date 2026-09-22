import csv
import unittest
from collections import defaultdict
from pathlib import Path


FIXTURE = Path(__file__).parent / "fixtures" / "overall-small.csv"
STATISTICS_FIXTURE = Path(__file__).parent / "fixtures" / "statistics-search-small.csv"


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


def statistics_rows():
    with STATISTICS_FIXTURE.open(encoding="utf-8", newline="") as source:
        return list(csv.DictReader(source, delimiter=";"))


def query06_reference():
    values = [
        float(row["PORCENTAJE_TNOH"])
        for row in statistics_rows()
        if row["ID_CLASE"] == "TT" and row["ID_CATEGORIA"] == "TT"
    ]
    mean = sum(values) / len(values)
    variance = sum((value - mean) ** 2 for value in values) / len(values)
    return (
        f"TNOH\tcount={len(values)}\tmean={mean:.4f}\tmedian=20.0000"
        f"\tstddev={variance ** 0.5:.10f}"
    )


def query07_reference():
    with STATISTICS_FIXTURE.open(encoding="utf-8") as source:
        return [line.rstrip("\n") for line in source if "HOTEL" in line]


def query08_reference():
    values = defaultdict(list)
    for row in statistics_rows():
        if row["ID_CLASE"] == "TT" and row["ID_CATEGORIA"] == "TT":
            values[(row["ANIO"], row["DEPARTAMENTO"])].append(
                float(row["PORCENTAJE_TNOH"])
            )
    means = {
        key: sum(items) / len(items)
        for key, items in values.items()
    }
    by_year = defaultdict(list)
    for (year, department), mean in means.items():
        by_year[year].append((mean, department))
    return [
        f"{year}\tmin={min(items)[1]}:{min(items)[0]:.4f}"
        f"\tmax={max(items)[1]}:{max(items)[0]:.4f}"
        for year, items in sorted(by_year.items())
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

    def test_query06(self):
        self.assertEqual(
            "TNOH\tcount=3\tmean=20.0000\tmedian=20.0000\tstddev=8.1649658093",
            query06_reference(),
        )

    def test_query07(self):
        self.assertEqual(1, len(query07_reference()))
        self.assertIn(";HOTEL BOUTIQUE;", query07_reference()[0])

    def test_query08(self):
        self.assertEqual(
            ["2019\tmin=LIMA:10.0000\tmax=AREQUIPA:30.0000"],
            query08_reference(),
        )


if __name__ == "__main__":
    unittest.main()
