package citybike;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.jobcontrol.Job;
import org.apache.hadoop.mapred.jobcontrol.JobControl;
import org.apache.hadoop.mapred.lib.MultipleInputs;
import org.apache.hadoop.util.Tool;

public class Req4 extends Configured implements Tool {
    
    private final String inputPath;

    Req4(String arg) {
        this.inputPath = arg;
    }

    @Override
    public int run(String[] strings) throws Exception {

        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);

        Configuration conf = getConf();
        FileSystem fs = FileSystem.get(conf);

        JobConf jobConf1 = new JobConf(conf, Req4.class);

        jobConf1.setOutputKeyClass(Text.class);
        jobConf1.setOutputValueClass(LongWritable.class);

        jobConf1.setMapperClass(Req4Mapper1.class);
        jobConf1.setCombinerClass(Req2Reducer.class);
        jobConf1.setReducerClass(Req2Reducer.class);

        jobConf1.setInputFormat(TextInputFormat.class);
        jobConf1.setOutputFormat(TextOutputFormat.class);

        /*MultipleInputs.addInputPath(jobConf1, new Path("data/201501-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(jobConf1, new Path("data/201502-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(jobConf1, new Path("data/201503-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(jobConf1, new Path("data/201504-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(jobConf1, new Path("data/201505-citibike-tripdata.csv"), TextInputFormat.class);*/
        MultipleInputs.addInputPath(jobConf1, new Path(inputPath + "/201506-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(jobConf1, new Path(inputPath + "/201507-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(jobConf1, new Path(inputPath + "/201508-citibike-tripdata.csv"), TextInputFormat.class);
        /*MultipleInputs.addInputPath(jobConf1, new Path("data/201509-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(jobConf1, new Path("data/201510-citibike-tripdata.csv"), TextInputFormat.class);
        MultipleInputs.addInputPath(jobConf1, new Path("data/201511-citibike-tripdata.csv"), TextInputFormat.class);*/

        
        fs.delete(new Path(inputPath + "/temp"), true);
        
        FileOutputFormat.setOutputPath(jobConf1, new Path(inputPath + "/temp"));

        JobConf jobConf2 = new JobConf(conf, Req4.class);

        FileInputFormat.setInputPaths(jobConf2, new Path(inputPath + "/temp"));

        fs.delete(new Path(inputPath + "/out/4"), true);

        FileOutputFormat.setOutputPath(jobConf2, new Path(inputPath + "/out/4"));

        jobConf2.setOutputKeyClass(Text.class);
        jobConf2.setOutputValueClass(Text.class);

        jobConf2.setMapperClass(Req4Mapper2.class);
        jobConf2.setReducerClass(Req4Reducer.class);

        jobConf2.setInputFormat(TextInputFormat.class);
        jobConf2.setOutputFormat(TextOutputFormat.class);

        Job job1 = new Job(jobConf1);
        Job job2 = new Job(jobConf2);
        JobControl jobControl = new JobControl("Most Visited Stations");
        jobControl.addJob(job1);
        jobControl.addJob(job2);
        job2.addDependingJob(job1);
        
        Thread t = new Thread(jobControl); 
        t.setDaemon(true);
        t.start(); 
                      
        while (!jobControl.allFinished()) { 
          try { 
            Thread.sleep(1000); 
          } catch (InterruptedException e) { 
            // Ignore. 
          } 
        } 

        return 0;
    }

}
