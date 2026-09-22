package labca1.query12;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import labca1.common.ClassificationMetrics;
import labca1.common.FeatureVector;
import labca1.common.LogisticRegressionModel;
import labca1.common.OccupabilityRecord;
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

public final class LogisticClassificationDriver {
  private LogisticClassificationDriver() {}

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
      double threshold = median(examples);
      List<Example> training = examples.stream().filter(e -> TemporalSplit.isTraining(e.year)).toList();
      double[][] features = training.stream().map(e -> e.features).toArray(double[][]::new);
      boolean[] labels = new boolean[training.size()];
      for (int index = 0; index < labels.length; index++) labels[index] = training.get(index).tnoh >= threshold;
      LogisticRegressionModel model = LogisticRegressionModel.fit(features, labels, 200, 0.1);
      output.collect(new Text("MODEL"), new Text("LOGISTIC_REGRESSION"));
      output.collect(new Text("TRAINING"), new Text(String.format(Locale.ROOT,
          "samples=%d\tthreshold=%.4f\titerations=%d\tlearning_rate=%.4f", training.size(), threshold, model.iterations(), model.learningRate())));
      emitMetrics(examples, threshold, model, output);
    }
  }

  private static void emitMetrics(List<Example> examples, double threshold,
      LogisticRegressionModel model, OutputCollector<Text, Text> output) throws IOException {
    Map<String, ClassificationMetrics> metrics = new LinkedHashMap<>();
    metrics.put("TEST_2024", new ClassificationMetrics());
    metrics.put("VALIDATION_2025_H1", new ClassificationMetrics());
    for (Example example : examples) {
      String period = TemporalSplit.evaluationPeriod(example.year);
      if (period != null) metrics.get(period).add(example.tnoh >= threshold, model.probabilityHigh(example.features));
    }
    for (Map.Entry<String, ClassificationMetrics> entry : metrics.entrySet()) {
      output.collect(new Text(entry.getKey()), new Text(format(entry.getValue())));
    }
  }

  private static String format(ClassificationMetrics metrics) {
    return String.format(Locale.ROOT,
        "samples=%d\ttp=%d\ttn=%d\tfp=%d\tfn=%d\taccuracy=%.6f\tprecision=%.6f\trecall=%.6f\tf1=%.6f\tlog_loss=%.6f",
        metrics.count(), metrics.truePositives(), metrics.trueNegatives(), metrics.falsePositives(),
        metrics.falseNegatives(), metrics.accuracy(), metrics.precision(), metrics.recall(),
        metrics.f1(), metrics.logLoss());
  }

  private static double median(List<Example> examples) {
    List<Double> values = examples.stream().filter(e -> TemporalSplit.isTraining(e.year))
        .map(e -> e.tnoh).sorted().toList();
    int middle = values.size() / 2;
    return values.size() % 2 == 1 ? values.get(middle) : (values.get(middle - 1) + values.get(middle)) / 2.0;
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
      String[] rawFeatures = fields[2].split(",", -1);
      double[] features = new double[rawFeatures.length];
      for (int i = 0; i < features.length; i++) features[i] = Double.parseDouble(rawFeatures[i]);
      return new Example(Integer.parseInt(fields[0]), Double.parseDouble(fields[1]), features);
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2) throw new IllegalArgumentException("Uso: LogisticClassificationDriver <input> <output>");
    JobConf job = new JobConf(LogisticClassificationDriver.class);
    job.setJobName("labca1-query12-logistic-regression");
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
