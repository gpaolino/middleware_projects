package citybike;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class Req2Mapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, LongWritable> {

    @Override
    public void map(LongWritable k1, Text v1, OutputCollector<Text, LongWritable> oc, Reporter rprtr) throws IOException {
  
        Text k2 = new Text();
        LongWritable v2 = new LongWritable();
        v2.set(1);

        String[] cols = v1.toString().replaceAll("\"", "").split(Pattern.quote(","));
        
        String date = cols[1];
        String customerType = cols[12];
        
        if( customerType.equals("Subscriber") ) 
            return;
        
        String dt[] = date.split(Pattern.quote(" "))[0].split(Pattern.quote("/"));
        String day, month, year;

        if (dt.length == 3) {
            
            month = dt[0];
            day = dt[1];
            year = dt[2];

            Calendar sDateCalendar = new GregorianCalendar();

            if (month.equals("") || year.equals("") || day.equals("")) {
                System.err.println(date);
            } else {
                sDateCalendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
            }
            Integer weekOfTheYear = sDateCalendar.get(Calendar.WEEK_OF_YEAR);

            k2.set(weekOfTheYear.toString());
 
            oc.collect(k2, v2);
        }

    }

}
