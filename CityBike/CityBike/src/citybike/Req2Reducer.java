package citybike;

import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class Req2Reducer extends MapReduceBase implements Reducer<Text, LongWritable, Text, LongWritable> {

    @Override
    public void reduce(Text k2, Iterator<LongWritable> values, OutputCollector<Text, LongWritable> oc, Reporter rprtr) throws IOException {

        LongWritable v2 = new LongWritable();
        long sum = 0;
        
        while ( values.hasNext() ) {
            sum += values.next().get();
        } 
        
        v2.set(sum);
        
        oc.collect(k2, v2);
    }

}
