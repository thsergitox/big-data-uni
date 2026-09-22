package labca1.common;

public record FeatureVector(
    double month,
    double establishments,
    double rooms,
    double beds,
    double arrivalsPerRoom,
    double overnightStaysPerBed,
    double employmentPerEstablishment) {

  public static FeatureVector from(OccupabilityRecord record) {
    return new FeatureVector(
        record.month(),
        record.establishments(),
        record.rooms(),
        record.beds(),
        safeRatio(record.arrivals(), record.rooms()),
        safeRatio(record.overnightStays(), record.beds()),
        safeRatio(record.employment(), record.establishments()));
  }

  public double[] values() {
    return new double[] {
      month,
      establishments,
      rooms,
      beds,
      arrivalsPerRoom,
      overnightStaysPerBed,
      employmentPerEstablishment
    };
  }

  private static double safeRatio(long numerator, long denominator) {
    return denominator == 0 ? 0.0 : (double) numerator / denominator;
  }
}
