package labca1.query14;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import labca1.common.FeatureVector;
import labca1.common.LinearRegressionModel;
import labca1.common.OccupabilityRecord;
import labca1.common.RegressionMetrics;
import labca1.common.TemporalSplit;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

public final class RidgeRegressionDriver {
  private static final double LAMBDA = 1.0;

  private RidgeRegressionDriver() {}

  public static final class ModelMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, Text> {
    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      if (value.toString().startsWith("FECHA_CORTE;")) return;
      OccupabilityRecord record = OccupabilityRecord.parse(value.toString());
      if (record.isOverall()) output.collect(new Text("MODEL"), new Text(encode(record)));
    }
  }

  public static final class ModelReducer extends MapReduceBase
      implements Reducer<Text, Text, Text, Text> {
    @Override
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      List<Example> examples = new ArrayList<>();
      while (values.hasNext()) examples.add(Example.parse(values.next().toString()));
      List<Example> training = examples.stream().filter(e -> TemporalSplit.isTraining(e.year)).toList();
      double[][] features = training.stream().map(e -> e.features).toArray(double[][]::new);
      double[] targets = training.stream().mapToDouble(e -> e.tnoh).toArray();
      LinearRegressionModel model = LinearRegressionModel.fit(features, targets, LAMBDA);
      output.collect(new Text("MODEL"), new Text(modelDescription(model)));
      output.collect(new Text("TRAINING"), new Text("samples=" + training.size()));
      emitMetrics(examples, model, output);
    }
  }

  private static void emitMetrics(List<Example> examples, LinearRegressionModel model,
      OutputCollector<Text, Text> output) throws IOException {
    Map<String, RegressionMetrics> metrics = new LinkedHashMap<>();
    metrics.put("TEST_2024", new RegressionMetrics());
    metrics.put("VALIDATION_2025_H1", new RegressionMetrics());
    for (Example example : examples) {
      String period = TemporalSplit.evaluationPeriod(example.year);
      if (period != null) metrics.get(period).add(example.tnoh, model.predict(example.features));
    }
    for (Map.Entry<String, RegressionMetrics> entry : metrics.entrySet()) {
      RegressionMetrics value = entry.getValue();
      output.collect(new Text(entry.getKey()), new Text(String.format(Locale.ROOT,
          "samples=%d\tmae=%.6f\trmse=%.6f\tr_squared=%.6f",
          value.count(), value.mae(), value.rmse(), value.rSquared())));
    }
  }

  private static String modelDescription(LinearRegressionModel model) {
    StringBuilder result = new StringBuilder(String.format(Locale.ROOT,
        "RIDGE_REGRESSION\tlambda=%.4f\tintercept=%.10f\tcoefficients=",
        model.lambda(), model.intercept()));
    double[] coefficients = model.coefficients();
    for (int index = 0; index < coefficients.length; index++) {
      if (index > 0) result.append(',');
      result.append(String.format(Locale.ROOT, "%.10f", coefficients[index]));
    }
    return result.toString();
  }

  private static String encode(OccupabilityRecord record) {
    StringBuilder result = new StringBuilder().append(record.year()).append('|').append(record.tnoh()).append('|');
    double[] values = FeatureVector.from(record).values();
    for (int index = 0; index < values.length; index++) {
      if (index > 0) result.append(',');
      result.append(values[index]);
    }
    return result.toString();
  }

  private record Example(int year, double tnoh, double[] features) {
    static Example parse(String value) {
      String[] fields = value.split("\\|", -1);
      String[] raw = fields[2].split(",", -1);
      double[] features = new double[raw.length];
      for (int index = 0; index < features.length; index++) features[index] = Double.parseDouble(raw[index]);
      return new Example(Integer.parseInt(fields[0]), Double.parseDouble(fields[1]), features);
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2) throw new IllegalArgumentException("Uso: RidgeRegressionDriver <input> <output>");
    JobConf job = new JobConf(RidgeRegressionDriver.class);
    job.setJobName("labca1-query14-ridge-regression");
    job.setMapperClass(ModelMapper.class);
    job.setReducerClass(ModelReducer.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    job.setInputFormat(TextInputFormat.class);
    job.setOutputFormat(TextOutputFormat.class);
    FileInputFormat.setInputPaths(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    JobClient.runJob(job);
  }
}
