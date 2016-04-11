package citybike;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class Req4Mapper2 extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

    @Override
    public void map(LongWritable k1, Text v1, OutputCollector<Text, Text> oc, Reporter rprtr) throws IOException {

        Text k2 = new Text();
        Text v2 = new Text();
        
        String[] cols = v1.toString().split("\t");
        String day = cols[0];
        String station = cols[1];
        String occurrences = cols[2];
        
        k2.set(day);
        v2.set(station+"#"+occurrences);
        
        
        oc.collect(k2, v2);

    }

}
