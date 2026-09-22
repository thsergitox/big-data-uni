package labca1.query06;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import labca1.common.OccupabilityRecord;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
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

public final class OccupancyStatisticsDriver {
  private OccupancyStatisticsDriver() {}

  public static final class StatisticsMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, DoubleWritable> {
    @Override
    public void map(LongWritable key, Text value,
        OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
      if (value.toString().startsWith("FECHA_CORTE;")) return;
      OccupabilityRecord record = OccupabilityRecord.parse(value.toString());
      if (record.isOverall()) output.collect(new Text("TNOH"), new DoubleWritable(record.tnoh()));
    }
  }

  public static final class StatisticsReducer extends MapReduceBase
      implements Reducer<Text, DoubleWritable, Text, Text> {
    @Override
    public void reduce(Text key, Iterator<DoubleWritable> values,
        OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
      List<Double> samples = new ArrayList<>();
      long count = 0;
      double mean = 0.0;
      double m2 = 0.0;
      while (values.hasNext()) {
        double value = values.next().get();
        samples.add(value);
        count++;
        double delta = value - mean;
        mean += delta / count;
        m2 += delta * (value - mean);
      }
      Collections.sort(samples);
      double median = count % 2 == 1
          ? samples.get((int) count / 2)
          : (samples.get((int) count / 2 - 1) + samples.get((int) count / 2)) / 2.0;
      output.collect(key, new Text(String.format(Locale.ROOT,
          "count=%d\tmean=%.4f\tmedian=%.4f\tstddev=%.10f",
          count, mean, median, Math.sqrt(m2 / count))));
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2) throw new IllegalArgumentException("Uso: OccupancyStatisticsDriver <input> <output>");
    JobConf job = new JobConf(OccupancyStatisticsDriver.class);
    job.setJobName("labca1-query06-occupancy-statistics");
    job.setMapperClass(StatisticsMapper.class);
    job.setReducerClass(StatisticsReducer.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(DoubleWritable.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    job.setInputFormat(TextInputFormat.class);
    job.setOutputFormat(TextOutputFormat.class);
    FileInputFormat.setInputPaths(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    JobClient.runJob(job);
  }
}
