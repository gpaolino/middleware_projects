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

public class Req1Mapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

    @Override
    public void map(LongWritable k1, Text v1, OutputCollector<Text, Text> oc, Reporter rprtr) throws IOException {
  
        Text k2 = new Text();
        Text v2 = new Text();

        String[] cols = v1.toString().replaceAll("\"", "").split(Pattern.quote(","));
        
        String tripduration = cols[0];
        String starttime = cols[1];

        String dt[] = starttime.split(Pattern.quote(" "))[0].split(Pattern.quote("/"));
        String day, month, year;

        if (dt.length == 3) {
            
            month = dt[0];
            day = dt[1];
            year = dt[2];

            Calendar sDateCalendar = new GregorianCalendar();

            if (month.equals("") || year.equals("") || day.equals("")) {
                System.err.println(starttime);
            } else {
                sDateCalendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
            }
            Integer weekOfTheYear = sDateCalendar.get(Calendar.WEEK_OF_YEAR);

            k2.set(weekOfTheYear.toString());
            v2.set(tripduration);
            oc.collect(k2, v2);
        }

    }

}
