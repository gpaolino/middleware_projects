package citybike;

import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class Req1Reducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

    @Override
    public void reduce(Text k2, Iterator<Text> values, OutputCollector<Text, Text> oc, Reporter rprtr) throws IOException {
        
        double sum = 0;
        double n = 0;
        double ni, localSum;
        

        while ( values.hasNext() ) {
            
            Text next = values.next();
            String[] cols = next.toString().split(Pattern.quote("#"));
            ni = Double.parseDouble(cols[1]);
            localSum = Double.parseDouble(cols[0]);
            n += ni;
            sum +=  localSum;
            
        } 
        
        double average = (sum/n)/60;
        
        Text outValue = new Text();
        outValue.set( n +"\t"+ (new Double(average)).toString() );
        
        oc.collect(k2, outValue);
    }

}
