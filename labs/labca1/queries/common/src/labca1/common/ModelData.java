package labca1.common;

final class ModelData {
  private ModelData() {}

  static void validate(double[][] features, boolean[] labels) {
    if (features.length == 0 || features.length != labels.length || features[0].length == 0) {
      throw new IllegalArgumentException("datos de entrenamiento inválidos");
    }
    int dimensions = features[0].length;
    for (double[] row : features) {
      if (row.length != dimensions) throw new IllegalArgumentException("dimensiones inconsistentes");
    }
  }

  static void add(double[] destination, double[] source) {
    for (int index = 0; index < destination.length; index++) destination[index] += source[index];
  }

  static double[] divide(double[] values, int denominator) {
    double[] result = values.clone();
    for (int index = 0; index < result.length; index++) result[index] /= denominator;
    return result;
  }

  static double[] means(double[][] features) {
    double[] means = new double[features[0].length];
    for (double[] row : features) add(means, row);
    return divide(means, features.length);
  }

  static double[] deviations(double[][] features, double[] means) {
    double[] variances = new double[means.length];
    for (double[] row : features) {
      for (int column = 0; column < means.length; column++) {
        double difference = row[column] - means[column];
        variances[column] += difference * difference;
      }
    }
    for (int column = 0; column < variances.length; column++) {
      variances[column] = Math.sqrt(ModelMath.safeVariance(variances[column] / features.length));
    }
    return variances;
  }

  static double[][] standardize(double[][] features, double[] means, double[] deviations) {
    double[][] result = new double[features.length][];
    for (int row = 0; row < features.length; row++) {
      result[row] = standardize(features[row], means, deviations);
    }
    return result;
  }

  static double[] standardize(double[] features, double[] means, double[] deviations) {
    double[] result = new double[features.length];
    for (int column = 0; column < features.length; column++) {
      result[column] = (features[column] - means[column]) / deviations[column];
    }
    return result;
  }
}
