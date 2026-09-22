package labca1.common;

public final class GaussianNaiveBayesModel {
  private final double[] lowMeans;
  private final double[] lowVariances;
  private final double[] highMeans;
  private final double[] highVariances;
  private final double lowPrior;
  private final double highPrior;

  private GaussianNaiveBayesModel(
      double[] lowMeans,
      double[] lowVariances,
      double[] highMeans,
      double[] highVariances,
      double lowPrior,
      double highPrior) {
    this.lowMeans = lowMeans;
    this.lowVariances = lowVariances;
    this.highMeans = highMeans;
    this.highVariances = highVariances;
    this.lowPrior = lowPrior;
    this.highPrior = highPrior;
  }

  public static GaussianNaiveBayesModel fit(double[][] features, boolean[] labels) {
    ModelData.validate(features, labels);
    int dimensions = features[0].length;
    double[] lowSums = new double[dimensions];
    double[] highSums = new double[dimensions];
    int lowCount = 0;
    int highCount = 0;
    for (int row = 0; row < features.length; row++) {
      if (labels[row]) {
        highCount++;
        ModelData.add(highSums, features[row]);
      } else {
        lowCount++;
        ModelData.add(lowSums, features[row]);
      }
    }
    if (lowCount == 0 || highCount == 0) {
      throw new IllegalArgumentException("se necesitan ejemplos de ambas clases");
    }
    double[] lowMeans = ModelData.divide(lowSums, lowCount);
    double[] highMeans = ModelData.divide(highSums, highCount);
    double[] lowVariances = variances(features, labels, false, lowMeans, lowCount);
    double[] highVariances = variances(features, labels, true, highMeans, highCount);
    return new GaussianNaiveBayesModel(
        lowMeans, lowVariances, highMeans, highVariances,
        (double) lowCount / features.length, (double) highCount / features.length);
  }

  public double probabilityHigh(double[] features) {
    double lowScore = Math.log(lowPrior) + logLikelihood(features, lowMeans, lowVariances);
    double highScore = Math.log(highPrior) + logLikelihood(features, highMeans, highVariances);
    return ModelMath.sigmoid(highScore - lowScore);
  }

  private static double[] variances(
      double[][] features, boolean[] labels, boolean label, double[] means, int count) {
    double[] variances = new double[means.length];
    for (int row = 0; row < features.length; row++) {
      if (labels[row] != label) continue;
      for (int column = 0; column < means.length; column++) {
        double difference = features[row][column] - means[column];
        variances[column] += difference * difference;
      }
    }
    for (int column = 0; column < variances.length; column++) {
      variances[column] = ModelMath.safeVariance(variances[column] / count);
    }
    return variances;
  }

  private static double logLikelihood(double[] features, double[] means, double[] variances) {
    double result = 0.0;
    for (int column = 0; column < means.length; column++) {
      double difference = features[column] - means[column];
      result -= 0.5 * (Math.log(2.0 * Math.PI * variances[column])
          + difference * difference / variances[column]);
    }
    return result;
  }
}
