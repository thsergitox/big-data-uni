package labca1.query10;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
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

public final class ForeignShareDriver {
  private ForeignShareDriver() {}

  public static final class MonthlyMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, Text> {
    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      if (value.toString().startsWith("FECHA_CORTE;")) return;
      OccupabilityRecord record = OccupabilityRecord.parse(value.toString());
      if (record.isOverall() && record.arrivals() > 0) {
        double share = 100.0 * record.foreignArrivals() / record.arrivals();
        String group = record.department() + ";" + period(record.year());
        String month = String.format(Locale.ROOT, "%02d", record.month());
        output.collect(new Text(group + ";" + record.year() + ";" + month), new Text(share + ",1"));
      }
    }

    private static String period(int year) {
      if (year == 2019) return "PRE_2019";
      if (year <= 2021) return "IMPACT_2020_2021";
      if (year <= 2024) return "RECOVERY_2022_2024";
      return "CURRENT_2025_H1";
    }
  }

  public static final class MonthlyReducer extends MapReduceBase
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
      String[] group = key.toString().split(";", -1);
      output.collect(new Text(group[0] + ";" + group[1]), new Text(Double.toString(sum / count)));
    }
  }

  public static final class PeriodMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, Text> {
    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      String[] row = value.toString().split("\t", -1);
      output.collect(new Text(row[0]), new Text(row[1] + ",1"));
    }
  }

  public static final class PeriodReducer extends MapReduceBase
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
      output.collect(key, new Text(String.format(Locale.ROOT, "mean_foreign_share=%.4f", sum / count)));
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2) throw new IllegalArgumentException("Uso: ForeignShareDriver <input> <output>");
    Path intermediate = new Path(args[1] + "-intermediate");
    JobConf first = job("labca1-query10-monthly-share", MonthlyMapper.class, MonthlyReducer.class);
    FileInputFormat.setInputPaths(first, new Path(args[0]));
    FileOutputFormat.setOutputPath(first, intermediate);
    FileSystem fileSystem = intermediate.getFileSystem(first);
    if (fileSystem.exists(intermediate)) throw new IllegalStateException("La ruta intermedia ya existe: " + intermediate);
    JobClient.runJob(first);
    JobConf second = job("labca1-query10-period-share", PeriodMapper.class, PeriodReducer.class);
    FileInputFormat.setInputPaths(second, intermediate);
    FileOutputFormat.setOutputPath(second, new Path(args[1]));
    JobClient.runJob(second);
    fileSystem.delete(intermediate, true);
  }

  private static JobConf job(String name, Class<? extends Mapper> mapper, Class<? extends Reducer> reducer) {
    JobConf job = new JobConf(ForeignShareDriver.class);
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
