package labca1.common;

public final class LogisticRegressionModel {
  private final double[] means;
  private final double[] deviations;
  private final double[] weights;
  private final int iterations;
  private final double learningRate;

  private LogisticRegressionModel(
      double[] means, double[] deviations, double[] weights, int iterations, double learningRate) {
    this.means = means;
    this.deviations = deviations;
    this.weights = weights;
    this.iterations = iterations;
    this.learningRate = learningRate;
  }

  public static LogisticRegressionModel fit(
      double[][] features, boolean[] labels, int iterations, double learningRate) {
    ModelData.validate(features, labels);
    double[] means = ModelData.means(features);
    double[] deviations = ModelData.deviations(features, means);
    double[][] scaled = ModelData.standardize(features, means, deviations);
    double[] weights = new double[features[0].length + 1];
    for (int iteration = 0; iteration < iterations; iteration++) {
      double[] gradients = new double[weights.length];
      for (int row = 0; row < scaled.length; row++) {
        double error = ModelMath.sigmoid(score(weights, scaled[row])) - (labels[row] ? 1.0 : 0.0);
        gradients[0] += error;
        for (int column = 0; column < scaled[row].length; column++) {
          gradients[column + 1] += error * scaled[row][column];
        }
      }
      for (int index = 0; index < weights.length; index++) {
        weights[index] -= learningRate * gradients[index] / scaled.length;
      }
    }
    return new LogisticRegressionModel(means, deviations, weights, iterations, learningRate);
  }

  public double probabilityHigh(double[] features) {
    return ModelMath.sigmoid(score(weights, ModelData.standardize(features, means, deviations)));
  }

  public int iterations() {
    return iterations;
  }

  public double learningRate() {
    return learningRate;
  }

  private static double score(double[] weights, double[] features) {
    double score = weights[0];
    for (int column = 0; column < features.length; column++) {
      score += weights[column + 1] * features[column];
    }
    return score;
  }
}
