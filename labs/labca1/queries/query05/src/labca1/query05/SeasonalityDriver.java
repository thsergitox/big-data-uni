package labca1.query05;

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

public final class SeasonalityDriver {
  private SeasonalityDriver() {}

  public static final class SeasonalityMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, Text> {
    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      if (value.toString().startsWith("FECHA_CORTE;")) return;
      OccupabilityRecord record = OccupabilityRecord.parse(value.toString());
      if (record.isOverall()) {
        output.collect(new Text(String.format("%02d", record.month())),
            new Text(record.arrivals() + "," + record.overnightStays()));
      }
    }
  }

  public static final class SeasonalityReducer extends MapReduceBase
      implements Reducer<Text, Text, Text, Text> {
    @Override
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      long arrivals = 0;
      long nights = 0;
      while (values.hasNext()) {
        String[] partial = values.next().toString().split(",", -1);
        arrivals += Long.parseLong(partial[0]);
        nights += Long.parseLong(partial[1]);
      }
      output.collect(key, new Text("arrivals=" + arrivals + "\tovernight_stays=" + nights));
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2) throw new IllegalArgumentException("Uso: SeasonalityDriver <input> <output>");
    JobConf job = new JobConf(SeasonalityDriver.class);
    job.setJobName("labca1-query05-seasonality");
    job.setMapperClass(SeasonalityMapper.class);
    job.setReducerClass(SeasonalityReducer.class);
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
