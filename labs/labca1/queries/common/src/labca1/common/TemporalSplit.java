package labca1.common;

public final class TemporalSplit {
  private TemporalSplit() {}

  public static boolean isTraining(int year) {
    return year >= 2019 && year <= 2023;
  }

  public static String evaluationPeriod(int year) {
    if (year == 2024) {
      return "TEST_2024";
    }
    if (year == 2025) {
      return "VALIDATION_2025_H1";
    }
    return null;
  }
}
