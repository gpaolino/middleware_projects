package citybike;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class CityBike {

    private static void printMenu() {
        System.out.println("Options:");
        System.out.println("1- average duration of trips per week in 2015");
        System.out.println("2- number of customers using the bikes per week in 2015");
        System.out.println("3- number of trips and average duration of trips per biker age range");
        System.out.println("4- for each day in [1/6 - 31/8] id and name of the station that saw the most amount of traffic");
        System.out.print("\n Type the option number or 'e' to quit : ");
    }

    public static void runTool(Tool tool, String[] args, int option) throws Exception {
        System.out.print("\nRunning " + option + " ...\n\n");
        long tStart = System.currentTimeMillis();
        ToolRunner.run(new Configuration(), tool, args);
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.print(" completed after " + elapsedSeconds + " seconds. Results in folder 'out/" + option + "/'.\n\n");
    }

    public static void main(String[] args) throws Exception {

        boolean loop = true;

        System.out.println("###### CITYBIKE MAPREDUCE #######");
        printMenu();
        while (loop) {
            try {
                BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                String s = bufferRead.readLine();

                switch (s) {
                    case "e":
                        System.out.print("########### ENDED ###############\n\n");
                        loop = false;
                        break;
                    case "1":
                        runTool(new Req1(args[0]), args, 1);
                        break;
                    case "2":
                        runTool(new Req2(args[0]), args, 2);
                        break;
                    case "3":
                        runTool(new Req3(args[0]), args, 3);
                        break;
                    case "4":
                        runTool(new Req4(args[0]), args, 4);
                        break;
                    default:
                        System.out.println("\n Selected option is not available \n");
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (loop) {
                printMenu();
            }
        }

    }
}
