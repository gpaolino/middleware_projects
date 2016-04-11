package citybike;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.MultipleInputs;
import org.apache.hadoop.util.Tool;

public class Req2 extends Configured implements Tool {

    private final String inputPath;
    
    Req2(String arg) {
        this.inputPath = arg;
    }

    @Override
    public int run(String[] strings) throws Exception {
        
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.ERROR);
        
        Configuration conf = getConf();
        JobConf job = new JobConf(conf, Req2.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        job.setMapperClass(Req2Mapper.class);
        job.setCombinerClass(Req2Reducer.class);
        job.setReducerClass(Req2Reducer.class);

        job.setInputFormat(TextInputFormat.class);
        job.setOutputFormat(TextOutputFormat.class);

        MultipleInputs.addInputPath(job, new Path(inputPath + "/201501-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(job, new Path(inputPath + "/201502-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(job, new Path(inputPath + "/201503-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(job, new Path(inputPath + "/201504-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(job, new Path(inputPath + "/201505-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(job, new Path(inputPath + "/201506-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(job, new Path(inputPath + "/201507-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(job, new Path(inputPath + "/201508-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(job, new Path(inputPath + "/201509-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(job, new Path(inputPath + "/201510-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(job, new Path(inputPath + "/201511-citibike-tripdata.csv"), TextInputFormat.class);

        FileSystem fs = FileSystem.get(conf);
        fs.delete(new Path(inputPath + "/out/2"), true);

        FileOutputFormat.setOutputPath(job, new Path(inputPath + "/out/2"));

        JobClient.runJob(job);

        return 0;
    }

}
