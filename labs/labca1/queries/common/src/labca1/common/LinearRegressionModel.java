package labca1.common;

public final class LinearRegressionModel {
  private final double[] means;
  private final double[] deviations;
  private final double intercept;
  private final double[] coefficients;
  private final double lambda;

  private LinearRegressionModel(
      double[] means, double[] deviations, double intercept, double[] coefficients, double lambda) {
    this.means = means;
    this.deviations = deviations;
    this.intercept = intercept;
    this.coefficients = coefficients;
    this.lambda = lambda;
  }

  public static LinearRegressionModel fit(double[][] features, double[] targets, double lambda) {
    if (features.length == 0 || features.length != targets.length || lambda < 0.0) {
      throw new IllegalArgumentException("datos de regresión inválidos");
    }
    boolean[] placeholder = new boolean[features.length];
    ModelData.validate(features, placeholder);
    double[] means = ModelData.means(features);
    double[] deviations = ModelData.deviations(features, means);
    double[][] scaled = ModelData.standardize(features, means, deviations);
    int size = features[0].length + 1;
    double[][] matrix = new double[size][size];
    double[] vector = new double[size];
    for (int row = 0; row < scaled.length; row++) {
      double[] augmented = new double[size];
      augmented[0] = 1.0;
      System.arraycopy(scaled[row], 0, augmented, 1, scaled[row].length);
      for (int left = 0; left < size; left++) {
        vector[left] += augmented[left] * targets[row];
        for (int right = 0; right < size; right++) {
          matrix[left][right] += augmented[left] * augmented[right];
        }
      }
    }
    for (int index = 1; index < size; index++) matrix[index][index] += lambda;
    double[] solution = solve(matrix, vector);
    double[] coefficients = new double[size - 1];
    System.arraycopy(solution, 1, coefficients, 0, coefficients.length);
    return new LinearRegressionModel(means, deviations, solution[0], coefficients, lambda);
  }

  public double predict(double[] features) {
    double[] scaled = ModelData.standardize(features, means, deviations);
    double result = intercept;
    for (int index = 0; index < coefficients.length; index++) result += coefficients[index] * scaled[index];
    return result;
  }

  public double intercept() {
    return intercept;
  }

  public double[] coefficients() {
    return coefficients.clone();
  }

  public double lambda() {
    return lambda;
  }

  private static double[] solve(double[][] matrix, double[] vector) {
    int size = vector.length;
    for (int pivot = 0; pivot < size; pivot++) {
      int best = pivot;
      for (int row = pivot + 1; row < size; row++) {
        if (Math.abs(matrix[row][pivot]) > Math.abs(matrix[best][pivot])) best = row;
      }
      if (Math.abs(matrix[best][pivot]) < 1e-12) {
        throw new IllegalArgumentException("matriz singular; no se puede ajustar el modelo");
      }
      double[] matrixRow = matrix[pivot];
      matrix[pivot] = matrix[best];
      matrix[best] = matrixRow;
      double vectorValue = vector[pivot];
      vector[pivot] = vector[best];
      vector[best] = vectorValue;
      for (int row = pivot + 1; row < size; row++) {
        double factor = matrix[row][pivot] / matrix[pivot][pivot];
        for (int column = pivot; column < size; column++) {
          matrix[row][column] -= factor * matrix[pivot][column];
        }
        vector[row] -= factor * vector[pivot];
      }
    }
    double[] result = new double[size];
    for (int row = size - 1; row >= 0; row--) {
      double value = vector[row];
      for (int column = row + 1; column < size; column++) value -= matrix[row][column] * result[column];
      result[row] = value / matrix[row][row];
    }
    return result;
  }
}
