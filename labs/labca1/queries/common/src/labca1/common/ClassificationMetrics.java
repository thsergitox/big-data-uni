package labca1.common;

public final class ClassificationMetrics {
  private static final double MIN_PROBABILITY = 1e-15;

  private long truePositives;
  private long trueNegatives;
  private long falsePositives;
  private long falseNegatives;
  private long count;
  private double logLossSum;

  public void add(boolean actualHigh, double highProbability) {
    double probability = clip(highProbability);
    boolean predictedHigh = probability >= 0.5;

    if (actualHigh && predictedHigh) {
      truePositives++;
    } else if (actualHigh) {
      falseNegatives++;
    } else if (predictedHigh) {
      falsePositives++;
    } else {
      trueNegatives++;
    }

    logLossSum -= actualHigh ? Math.log(probability) : Math.log(1.0 - probability);
    count++;
  }

  public long truePositives() {
    return truePositives;
  }

  public long trueNegatives() {
    return trueNegatives;
  }

  public long falsePositives() {
    return falsePositives;
  }

  public long falseNegatives() {
    return falseNegatives;
  }

  public double accuracy() {
    return divide(truePositives + trueNegatives, count);
  }

  public double precision() {
    return divide(truePositives, truePositives + falsePositives);
  }

  public double recall() {
    return divide(truePositives, truePositives + falseNegatives);
  }

  public double f1() {
    double precision = precision();
    double recall = recall();
    return precision + recall == 0.0 ? 0.0 : 2.0 * precision * recall / (precision + recall);
  }

  public double logLoss() {
    return count == 0 ? 0.0 : logLossSum / count;
  }

  private static double clip(double probability) {
    if (!Double.isFinite(probability)) {
      throw new IllegalArgumentException("la probabilidad debe ser finita");
    }
    return Math.max(MIN_PROBABILITY, Math.min(1.0 - MIN_PROBABILITY, probability));
  }

  private static double divide(long numerator, long denominator) {
    return denominator == 0 ? 0.0 : (double) numerator / denominator;
  }
}
