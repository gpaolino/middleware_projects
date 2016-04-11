package citybike;

import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class Req1Combiner extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

    @Override
    public void reduce(Text k2, Iterator<Text> values, OutputCollector<Text, Text> oc, Reporter rprtr) throws IOException {
        
        double sum = 0;
        double n = 0;
    
        while ( values.hasNext() ) {
            n++;
            sum += Double.parseDouble(values.next().toString());
        }
     
        String out_s = sum+"#"+n;
        Text out_t = new Text();
        out_t.set(out_s);

        oc.collect(k2, out_t);
    }

}
