package citybike;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class Req4Mapper1 extends MapReduceBase implements Mapper<LongWritable, Text, Text, LongWritable> {

    @Override
    public void map(LongWritable k1, Text v1, OutputCollector<Text, LongWritable> oc, Reporter rprtr) throws IOException {

        Text k2 = new Text();
        LongWritable v2 = new LongWritable();
        v2.set(1);

        String[] cols = v1.toString().replaceAll("\"", "").split(Pattern.quote(","));

        String starttime = cols[1];
        String startstaionid = cols[3];
        String startstaionname = cols[4];
        String endstationid = cols[7];
        String endstationname = cols[8];

        String dt[] = starttime.split(Pattern.quote(" "))[0].split(Pattern.quote("/"));
        int day, month, year;

        if (dt.length == 3) {

            month = Integer.parseInt(dt[0]) - 1;
            day = Integer.parseInt(dt[1]);
            year = Integer.parseInt(dt[2]);

            Date date = new Date(year, month, day);
            Date lower = new Date(2015, 4, 31);
            Date upper = new Date(2015, 8, 1);
            
           if( date.after(lower) && date.before(upper) ) {
               
               k2.set(year+"-"+(month+1)+"-"+day+"\t"+startstaionid+" : "+startstaionname);
               oc.collect(k2, v2);
               
               k2.set(year+"-"+(month+1)+"-"+day+"\t"+endstationid+" : "+endstationname);
               oc.collect(k2, v2);
           }
        
        } 

    }

}
