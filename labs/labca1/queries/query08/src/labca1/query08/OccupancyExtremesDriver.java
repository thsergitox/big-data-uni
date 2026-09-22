package labca1.query08;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
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

public final class OccupancyExtremesDriver {
  private OccupancyExtremesDriver() {}

  public static final class ExtremesMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, Text> {
    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      if (value.toString().startsWith("FECHA_CORTE;")) return;
      OccupabilityRecord record = OccupabilityRecord.parse(value.toString());
      if (record.isOverall()) {
        output.collect(new Text(Integer.toString(record.year())),
            new Text(record.department() + "," + record.tnoh()));
      }
    }
  }

  public static final class ExtremesReducer extends MapReduceBase
      implements Reducer<Text, Text, Text, Text> {
    @Override
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      Map<String, double[]> departments = new HashMap<>();
      while (values.hasNext()) {
        String[] item = values.next().toString().split(",", -1);
        double[] total = departments.computeIfAbsent(item[0], ignored -> new double[2]);
        total[0] += Double.parseDouble(item[1]);
        total[1]++;
      }
      String minimumDepartment = null;
      String maximumDepartment = null;
      double minimum = Double.POSITIVE_INFINITY;
      double maximum = Double.NEGATIVE_INFINITY;
      for (Map.Entry<String, double[]> entry : departments.entrySet()) {
        double mean = entry.getValue()[0] / entry.getValue()[1];
        String department = entry.getKey();
        if (mean < minimum || mean == minimum && department.compareTo(minimumDepartment) < 0) {
          minimum = mean;
          minimumDepartment = department;
        }
        if (mean > maximum || mean == maximum && department.compareTo(maximumDepartment) > 0) {
          maximum = mean;
          maximumDepartment = department;
        }
      }
      output.collect(key, new Text(String.format(Locale.ROOT,
          "min=%s:%.4f\tmax=%s:%.4f", minimumDepartment, minimum, maximumDepartment, maximum)));
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2) throw new IllegalArgumentException("Uso: OccupancyExtremesDriver <input> <output>");
    JobConf job = new JobConf(OccupancyExtremesDriver.class);
    job.setJobName("labca1-query08-occupancy-extremes");
    job.setMapperClass(ExtremesMapper.class);
    job.setReducerClass(ExtremesReducer.class);
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
