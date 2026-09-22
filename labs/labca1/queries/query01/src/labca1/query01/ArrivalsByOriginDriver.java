package labca1.query01;

import java.io.IOException;
import java.util.Iterator;
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

public final class ArrivalsByOriginDriver {
  private ArrivalsByOriginDriver() {}

  public static final class ArrivalsMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, Text> {
    @Override
    public void map(
        LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      if (value.toString().startsWith("FECHA_CORTE;")) {
        return;
      }
      OccupabilityRecord record = OccupabilityRecord.parse(value.toString());
      if (record.isOverall()) {
        output.collect(
            new Text(Integer.toString(record.year())),
            new Text(record.nationalArrivals() + "," + record.foreignArrivals()));
      }
    }
  }

  public static final class ArrivalsReducer extends MapReduceBase
      implements Reducer<Text, Text, Text, Text> {
    @Override
    public void reduce(
        Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      long national = 0;
      long foreign = 0;
      while (values.hasNext()) {
        String[] totals = values.next().toString().split(",", -1);
        national += Long.parseLong(totals[0]);
        foreign += Long.parseLong(totals[1]);
      }
      output.collect(key, new Text("national=" + national + "\tforeign=" + foreign));
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      throw new IllegalArgumentException("Uso: ArrivalsByOriginDriver <input> <output>");
    }

    JobConf job = new JobConf(ArrivalsByOriginDriver.class);
    job.setJobName("labca1-query01-arrivals-by-origin");
    job.setMapperClass(ArrivalsMapper.class);
    job.setReducerClass(ArrivalsReducer.class);
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
