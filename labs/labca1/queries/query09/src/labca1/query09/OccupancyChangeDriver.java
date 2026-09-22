package labca1.query09;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import labca1.common.OccupabilityRecord;
import org.apache.hadoop.fs.FileSystem;
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

public final class OccupancyChangeDriver {
  private OccupancyChangeDriver() {}

  public static final class AnnualMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, Text> {
    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      if (value.toString().startsWith("FECHA_CORTE;")) return;
      OccupabilityRecord record = OccupabilityRecord.parse(value.toString());
      if (record.isOverall() && (record.year() == 2019 || record.year() == 2024)) {
        output.collect(new Text(record.year() + ";" + record.department()),
            new Text(record.tnoh() + ",1"));
      }
    }
  }

  public static final class AnnualReducer extends MapReduceBase
      implements Reducer<Text, Text, Text, Text> {
    @Override
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      double sum = 0.0;
      long count = 0;
      while (values.hasNext()) {
        String[] item = values.next().toString().split(",", -1);
        sum += Double.parseDouble(item[0]);
        count += Long.parseLong(item[1]);
      }
      output.collect(key, new Text(String.format(Locale.ROOT, "%.10f", sum / count)));
    }
  }

  public static final class ChangeMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, Text> {
    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      String[] row = value.toString().split("\t", -1);
      String[] group = row[0].split(";", -1);
      output.collect(new Text(group[1]), new Text(group[0] + "," + row[1]));
    }
  }

  public static final class ChangeReducer extends MapReduceBase
      implements Reducer<Text, Text, Text, Text> {
    @Override
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      Map<Integer, Double> means = new HashMap<>();
      while (values.hasNext()) {
        String[] item = values.next().toString().split(",", -1);
        means.put(Integer.parseInt(item[0]), Double.parseDouble(item[1]));
      }
      if (means.containsKey(2019) && means.containsKey(2024)) {
        double oldValue = means.get(2019);
        double newValue = means.get(2024);
        output.collect(key, new Text(String.format(Locale.ROOT,
            "2019=%.4f\t2024=%.4f\tchange_pp=%.4f", oldValue, newValue, newValue - oldValue)));
      }
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2) throw new IllegalArgumentException("Uso: OccupancyChangeDriver <input> <output>");
    Path intermediate = new Path(args[1] + "-intermediate");
    JobConf first = job("labca1-query09-annual-means", AnnualMapper.class, AnnualReducer.class);
    FileInputFormat.setInputPaths(first, new Path(args[0]));
    FileOutputFormat.setOutputPath(first, intermediate);
    FileSystem fileSystem = intermediate.getFileSystem(first);
    if (fileSystem.exists(intermediate)) {
      throw new IllegalStateException("La ruta intermedia ya existe: " + intermediate);
    }
    JobClient.runJob(first);

    JobConf second = job("labca1-query09-occupancy-change", ChangeMapper.class, ChangeReducer.class);
    FileInputFormat.setInputPaths(second, intermediate);
    FileOutputFormat.setOutputPath(second, new Path(args[1]));
    JobClient.runJob(second);
    fileSystem.delete(intermediate, true);
  }

  private static JobConf job(String name, Class<? extends Mapper> mapper, Class<? extends Reducer> reducer) {
    JobConf job = new JobConf(OccupancyChangeDriver.class);
    job.setJobName(name);
    job.setMapperClass(mapper);
    job.setReducerClass(reducer);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    job.setInputFormat(TextInputFormat.class);
    job.setOutputFormat(TextOutputFormat.class);
    return job;
  }
}
