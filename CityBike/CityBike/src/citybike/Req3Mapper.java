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

public class Req3Mapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

    @Override
    public void map(LongWritable k1, Text v1, OutputCollector<Text, Text> oc, Reporter rprtr) throws IOException {

        Text k2 = new Text();
        Text v2 = new Text();

        String[] cols = v1.toString().replaceAll("\"", "").split(Pattern.quote(","));
        String tripduration = cols[0];
        String birthyear = cols[13];

        Integer years = null;
        try {
            years = 2015 - Integer.parseInt(birthyear);
        } catch (NumberFormatException e) {
            return;
        }

        if (years >= 16 && years <= 19) {
            k2.set("16-19");
        } else if (years >= 20 && years <= 29) {
            k2.set("20-29");
        } else if (years >= 30 && years <= 39) {
            k2.set("30-39");
        } else if (years >= 40 && years <= 49) {
            k2.set("40-49");
        } else if (years >= 50 && years <= 59) {
            k2.set("50-59");
        } else if (years >= 60 && years <= 69) {
            k2.set("60-69");
        } else {
            return;
        }

        v2.set(tripduration);

        oc.collect(k2, v2);

    }

}
