package labca1.query07;

import java.io.IOException;
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
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

public final class SubstringSearchDriver {
  private static final String SEARCH_TERM = "labca1.query07.search-term";

  private SubstringSearchDriver() {}

  public static final class SearchMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, LongWritable, Text> {
    private String term;

    @Override
    public void configure(JobConf job) {
      term = job.get(SEARCH_TERM).toLowerCase(Locale.ROOT);
    }

    @Override
    public void map(LongWritable key, Text value,
        OutputCollector<LongWritable, Text> output, Reporter reporter) throws IOException {
      String line = value.toString();
      if (line.startsWith("FECHA_CORTE;")) return;
      OccupabilityRecord record = OccupabilityRecord.parse(line);
      if (contains(record.className()) || contains(record.categoryName())
          || contains(record.department())) {
        output.collect(key, new Text(line));
      }
    }

    private boolean contains(String value) {
      return value.toLowerCase(Locale.ROOT).contains(term);
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 3) throw new IllegalArgumentException("Uso: SubstringSearchDriver <input> <output> <texto>");
    JobConf job = new JobConf(SubstringSearchDriver.class);
    job.setJobName("labca1-query07-substring-search");
    job.set(SEARCH_TERM, args[2]);
    job.setMapperClass(SearchMapper.class);
    job.setNumReduceTasks(0);
    job.setOutputKeyClass(LongWritable.class);
    job.setOutputValueClass(Text.class);
    job.setInputFormat(TextInputFormat.class);
    job.setOutputFormat(TextOutputFormat.class);
    FileInputFormat.setInputPaths(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    JobClient.runJob(job);
  }
}
