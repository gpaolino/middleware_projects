package citybike;

import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class Req4Reducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

    @Override
    public void reduce(Text k2, Iterator<Text> values, OutputCollector<Text, Text> oc, Reporter rprtr) throws IOException {

        Text v = new Text();

        Long max = new Long(0);
        String maxStation = "";

        while (values.hasNext()) {

            Text next = values.next();
            String[] cols = next.toString().split(Pattern.quote("#"));

            String curStation = cols[0];
            Long cur = Long.parseLong(cols[1]);

            if (cur >= max) {
                max = cur;
                maxStation = curStation;
            }

        }

        v.set(maxStation);
        
        oc.collect(k2,v);

    }

}
