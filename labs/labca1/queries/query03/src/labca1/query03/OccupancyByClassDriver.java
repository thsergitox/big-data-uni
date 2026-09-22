package labca1.query03;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import labca1.common.OccupabilityRecord;
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

public final class OccupancyByClassDriver {
  private OccupancyByClassDriver() {}

  public static final class OccupancyMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, Text> {
    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      if (value.toString().startsWith("FECHA_CORTE;")) return;
      OccupabilityRecord record = OccupabilityRecord.parse(value.toString());
      if (record.isClassTotal() && !record.isOverall()) {
        output.collect(new Text(record.year() + ";" + record.className()),
            new Text(record.tnoh() + "," + record.tnoc() + ",1"));
      }
    }
  }

  public static final class OccupancyReducer extends MapReduceBase
      implements Reducer<Text, Text, Text, Text> {
    @Override
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      double tnoh = 0.0;
      double tnoc = 0.0;
      long count = 0;
      while (values.hasNext()) {
        String[] partial = values.next().toString().split(",", -1);
        tnoh += Double.parseDouble(partial[0]);
        tnoc += Double.parseDouble(partial[1]);
        count += Long.parseLong(partial[2]);
      }
      output.collect(key, new Text(String.format(Locale.ROOT,
          "mean_tnoh=%.4f\tmean_tnoc=%.4f", tnoh / count, tnoc / count)));
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2) throw new IllegalArgumentException("Uso: OccupancyByClassDriver <input> <output>");
    JobConf job = new JobConf(OccupancyByClassDriver.class);
    job.setJobName("labca1-query03-occupancy-by-class");
    job.setMapperClass(OccupancyMapper.class);
    job.setReducerClass(OccupancyReducer.class);
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
