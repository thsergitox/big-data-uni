package labca1.common;

public final class ModelMath {
  public static final double EPSILON = 1e-9;
  private static final double MIN_PROBABILITY = 1e-15;

  private ModelMath() {}

  public static double safeVariance(double variance) {
    return Math.max(EPSILON, variance);
  }

  public static double sigmoid(double value) {
    if (value >= 0.0) {
      double exponential = Math.exp(-value);
      return 1.0 / (1.0 + exponential);
    }
    double exponential = Math.exp(value);
    return exponential / (1.0 + exponential);
  }

  public static double clipProbability(double probability) {
    if (!Double.isFinite(probability)) {
      throw new IllegalArgumentException("la probabilidad debe ser finita");
    }
    return Math.max(MIN_PROBABILITY, Math.min(1.0 - MIN_PROBABILITY, probability));
  }
}
