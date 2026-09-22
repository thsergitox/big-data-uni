package labca1.common;

public final class RegressionMetrics {
  private long count;
  private double absoluteErrorSum;
  private double squaredErrorSum;
  private double actualSum;
  private double actualSquaredSum;

  public void add(double actual, double predicted) {
    double error = predicted - actual;
    absoluteErrorSum += Math.abs(error);
    squaredErrorSum += error * error;
    actualSum += actual;
    actualSquaredSum += actual * actual;
    count++;
  }

  public long count() {
    return count;
  }

  public double mae() {
    return count == 0 ? 0.0 : absoluteErrorSum / count;
  }

  public double rmse() {
    return count == 0 ? 0.0 : Math.sqrt(squaredErrorSum / count);
  }

  public double rSquared() {
    if (count == 0) {
      return 0.0;
    }

    double totalVariance = actualSquaredSum - actualSum * actualSum / count;
    return totalVariance <= 0.0 ? 0.0 : 1.0 - squaredErrorSum / totalVariance;
  }
}
