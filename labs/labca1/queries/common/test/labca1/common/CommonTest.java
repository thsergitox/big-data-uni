package labca1.common;

public final class CommonTest {
  private static final double TOLERANCE = 1e-9;

  private CommonTest() {}

  public static void main(String[] args) {
    parsesCanonicalRows();
    rejectsHeadersAndMalformedRows();
    createsLeakFreeFeaturesWithSafeRatios();
    calculatesClassificationMetrics();
    clipsProbabilitiesForLogLoss();
    calculatesRegressionMetrics();
    handlesConstantRegressionTargets();
    System.out.println("PASS: common query classes");
  }

  private static void parsesCanonicalRows() {
    OccupabilityRecord row = OccupabilityRecord.parse(overallRow());

    check(row.year() == 2019, "year");
    check(row.month() == 1, "month");
    check(row.isOverall(), "overall filter");
    check(row.isClassTotal(), "class total filter");
    checkClose(27.45, row.tnoh(), "decimal");
  }

  private static void rejectsHeadersAndMalformedRows() {
    expectFailure(
        () -> OccupabilityRecord.parse("FECHA_CORTE;ANIO;MES"),
        "cabecera",
        "header rejection");
    expectFailure(
        () -> OccupabilityRecord.parse(";2019;01"),
        "24 columnas",
        "column count rejection");
  }

  private static void createsLeakFreeFeaturesWithSafeRatios() {
    OccupabilityRecord row = OccupabilityRecord.parse(
        ";2024;06;TT;TODAS CONSOLIDADAS;TT;TODAS CONSOLIDADAS;LIMA;150000;"
            + "0;0;0;50.0;40.0;1.5;1.4;2.0;1000;900;100;1500;1300;200;0");

    FeatureVector features = FeatureVector.from(row);

    checkClose(6.0, features.month(), "feature month");
    checkClose(0.0, features.arrivalsPerRoom(), "zero rooms");
    checkClose(0.0, features.overnightStaysPerBed(), "zero beds");
    checkClose(0.0, features.employmentPerEstablishment(), "zero establishments");
    check(features.values().length == 7, "feature count");
  }

  private static void calculatesClassificationMetrics() {
    ClassificationMetrics metrics = new ClassificationMetrics();
    metrics.add(true, 0.9);
    metrics.add(false, 0.8);
    metrics.add(true, 0.7);
    metrics.add(false, 0.1);

    check(metrics.truePositives() == 2, "true positives");
    check(metrics.falsePositives() == 1, "false positives");
    check(metrics.trueNegatives() == 1, "true negatives");
    check(metrics.falseNegatives() == 0, "false negatives");
    checkClose(0.75, metrics.accuracy(), "accuracy");
    checkClose(2.0 / 3.0, metrics.precision(), "precision");
    checkClose(1.0, metrics.recall(), "recall");
    checkClose(0.8, metrics.f1(), "f1");
    check(Double.isFinite(metrics.logLoss()), "finite log loss");
  }

  private static void clipsProbabilitiesForLogLoss() {
    ClassificationMetrics metrics = new ClassificationMetrics();
    metrics.add(true, 0.0);
    metrics.add(false, 1.0);

    check(Double.isFinite(metrics.logLoss()), "clipped log loss");
  }

  private static void calculatesRegressionMetrics() {
    RegressionMetrics metrics = new RegressionMetrics();
    metrics.add(3.0, 2.0);
    metrics.add(5.0, 5.0);
    metrics.add(7.0, 8.0);

    check(metrics.count() == 3, "regression count");
    checkClose(2.0 / 3.0, metrics.mae(), "mae");
    checkClose(Math.sqrt(2.0 / 3.0), metrics.rmse(), "rmse");
    checkClose(0.75, metrics.rSquared(), "r squared");
  }

  private static void handlesConstantRegressionTargets() {
    RegressionMetrics metrics = new RegressionMetrics();
    metrics.add(2.0, 2.0);
    metrics.add(2.0, 2.0);

    checkClose(0.0, metrics.rSquared(), "constant target r squared");
  }

  private static String overallRow() {
    return ";2019;01;TT;TODAS CONSOLIDADAS;TT;TODAS CONSOLIDADAS;LIMA;150000;"
        + "10;100;200;27.45;20.10;1.5;1.4;2.0;1000;900;100;1500;1300;200;50";
  }

  private static void check(boolean condition, String label) {
    if (!condition) {
      throw new AssertionError(label);
    }
  }

  private static void checkClose(double expected, double actual, String label) {
    if (Math.abs(expected - actual) > TOLERANCE) {
      throw new AssertionError(label + ": expected=" + expected + ", actual=" + actual);
    }
  }

  private static void expectFailure(Runnable operation, String message, String label) {
    try {
      operation.run();
      throw new AssertionError(label + ": no exception");
    } catch (IllegalArgumentException exception) {
      check(exception.getMessage().contains(message), label + ": wrong message");
    }
  }
}
