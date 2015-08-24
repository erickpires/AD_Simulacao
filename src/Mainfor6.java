import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by V�tor on 20/08/2015.
 */
public class Mainfor6 {

    private static final int NUMBER_OF_ITERATIONS = 60;
    private static final double Z = 1.96;

    public static void main(String[] args) {

        Gym scenario1 = new Gym(1.0, 0.9, new ExpDistribution(5.0), new ExpDistribution(5.0), new ExpDistribution(0.1), 0.1 );
        Gym scenario2 = new Gym(0.1, 0.9, new ExpDistribution(1.0), new ExpDistribution(1.0), new ExpDistribution(0.1), 0.1 );
        Gym scenario3 = new Gym(0.9, 0.9, new ExpDistribution(1.0), new ExpDistribution(1.0), new ExpDistribution(10.0), 10.0 );

        System.out.println("6.1");

        simulate(1.0, 0.9, new ExpDistribution(5.0), new ExpDistribution(5.0), new ExpDistribution(0.1), 0.1, "6dot1scenario1.txt");

//        simulate(0.1, 0.9, new ExpDistribution(1.0), new ExpDistribution(1.0), new ExpDistribution(0.1), 0.1, "6dot1scenario2.txt");

//        simulate(0.9, 0.9, new ExpDistribution(1.0), new ExpDistribution(1.0), new ExpDistribution(10.0), 10.0, "6dot1scenario3.txt");

//        System.out.println("6.2");

//
//        scenario1.sampleExogeanEntry("6dot2_ExogeanEntry.csv");
//
//        scenario1 = new Gym(1.0, 0.9, new ExpDistribution(5.0), new ExpDistribution(5.0), new ExpDistribution(0.1), 0.1 );
//
//        scenario1.sampleTotalEntry("6dot2_TotalEntry.csv");
//
//        scenario1 = new Gym(1.0, 0.9, new ExpDistribution(5.0), new ExpDistribution(5.0), new ExpDistribution(0.1), 0.1 );
//
//        scenario1.sampleBikeEntry("6dot2_BikeEntry.csv");
//
//        scenario1 = new Gym(1.0, 0.9, new ExpDistribution(5.0), new ExpDistribution(5.0), new ExpDistribution(0.1), 0.1 );
//
//        scenario1.sampleTreadmillQuit("6dot2_TreadmillQuit.csv");
//
//        scenario1 = new Gym(1.0, 0.9, new ExpDistribution(5.0), new ExpDistribution(5.0), new ExpDistribution(0.1), 0.1 );
//
//        scenario1.sampleBikeQuit("6dot2_BikeQuit.csv");
//
//        scenario1 = new Gym(1.0, 0.9, new ExpDistribution(5.0), new ExpDistribution(5.0), new ExpDistribution(0.1), 0.1 );
//
//        scenario1.sampleTreadmillReentry("6dot2_TreadmillReentry.csv");
//
//        scenario1 = new Gym(1.0, 0.9, new ExpDistribution(5.0), new ExpDistribution(5.0), new ExpDistribution(0.1), 0.1 );
//
//        scenario1.sampleTotalBikeExits("6dot2_TotalBikeExits.csv");



        //--------------------------------------------------------------------------------------------------------------------------//

//
//
//
//
//        scenario2.sampleExogeanEntry("6dot2_ExogeanEntry_another.csv");
//
//        scenario2 = new Gym(0.1, 0.9, new ExpDistribution(1.0), new ExpDistribution(1.0), new ExpDistribution(0.1), 0.1 );
//
//        scenario2.sampleTotalEntry("6dot2_TotalEntry_another.csv");
//
//        scenario2 = new Gym(0.1, 0.9, new ExpDistribution(1.0), new ExpDistribution(1.0), new ExpDistribution(0.1), 0.1 );
//
//        scenario2.sampleBikeEntry("6dot2_BikeEntry_another.csv");
//
//        scenario2 = new Gym(0.1, 0.9, new ExpDistribution(1.0), new ExpDistribution(1.0), new ExpDistribution(0.1), 0.1 );
//
//       scenario2.sampleTreadmillQuit("6dot2_TreadmillQuit_another.csv");
//
//        scenario2 = new Gym(0.1, 0.9, new ExpDistribution(1.0), new ExpDistribution(1.0), new ExpDistribution(0.1), 0.1 );
//
//       scenario2.sampleBikeQuit("6dot2_BikeQuit_another.csv");
//
//        scenario2 = new Gym(0.1, 0.9, new ExpDistribution(1.0), new ExpDistribution(1.0), new ExpDistribution(0.1), 0.1 );
//
//        scenario2.sampleTreadmillReentry("6dot2_TreadmillReentry_another.csv");
//
//        scenario2 = new Gym(0.1, 0.9, new ExpDistribution(1.0), new ExpDistribution(1.0), new ExpDistribution(0.1), 0.1 );
//
//        scenario2.sampleTotalBikeExits("6dot2_TotalBikeExits_another.csv");
//
//




    }


    public static void simulate(double reentryIntoBike, double reentryIntoTreadmill, Distribution serviceTreadmillDistribution, Distribution serviceBikeDistribution, Distribution arrivalsDistribution, double lambda, String name)  {

        double meanSumBikeUsage = 0.0;
        double meanSumTreadmillUsage = 0.0;
        double meanSumTimeInGym = 0.0;

        Metrics[] values = new Metrics[NUMBER_OF_ITERATIONS];


        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {

            Gym gym = new Gym(reentryIntoBike, reentryIntoTreadmill, serviceTreadmillDistribution, serviceBikeDistribution, arrivalsDistribution, lambda);

            values[i] = gym.run();

 //           System.out.println("meanBikeUsage = " + values[i].meanBikeUsage + "at index = " + i);

            meanSumBikeUsage += values[i].meanBikeUsage;
            meanSumTreadmillUsage += values[i].meanTreadmillUsage;
            meanSumTimeInGym += values[i].meanTimeInGym;


        }

        double meanValueEstimatorBikeUsage = meanSumBikeUsage / NUMBER_OF_ITERATIONS;
        double meanValueEstimatorTreadmillUsage = meanSumTreadmillUsage / NUMBER_OF_ITERATIONS;
        double meanValueEstimatorTimeInGym = meanSumTimeInGym / NUMBER_OF_ITERATIONS;

        double diffToMeanSumBikeUsage = 0.0;
        double diffToMeanSumTreadmillUsage = 0.0;
        double diffToMeanSumTimeInGym = 0.0;

        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            diffToMeanSumBikeUsage += sqr(values[i].getMeanBikeUsage() - meanValueEstimatorBikeUsage);
            diffToMeanSumTreadmillUsage += sqr(values[i].getMeanTreadmillUsage() - meanValueEstimatorTreadmillUsage);
            diffToMeanSumTimeInGym += sqr(values[i].getMeanTimeInGym() - meanValueEstimatorTimeInGym);
        }

        double varianceValueEstimatorBikeUsage = diffToMeanSumBikeUsage / (NUMBER_OF_ITERATIONS - 1);
        double varianceValueEstimatorTreadmillUsage = diffToMeanSumTreadmillUsage / (NUMBER_OF_ITERATIONS - 1);
        double varianceValueEstimatorTimeInGym = diffToMeanSumTimeInGym / (NUMBER_OF_ITERATIONS - 1);

        double standardDeviationBikeUsage = Math.sqrt(varianceValueEstimatorBikeUsage);
        double standardDeviationTreadmillUsage = Math.sqrt(varianceValueEstimatorTreadmillUsage);
        double standardDeviationTimeInGym = Math.sqrt(varianceValueEstimatorTimeInGym);

        double errorBikeUsage = Z * (standardDeviationBikeUsage / Math.sqrt(NUMBER_OF_ITERATIONS));
        double errorTreadmillUsage = Z * (standardDeviationTreadmillUsage / Math.sqrt(NUMBER_OF_ITERATIONS));
        double errorTimeInGym = Z * (standardDeviationTimeInGym / Math.sqrt(NUMBER_OF_ITERATIONS));

        try {

            PrintStream out = new PrintStream(new FileOutputStream(name));

            out.print("meanValueEstimatorTreadmillUsage\n");

            out.print(meanValueEstimatorTreadmillUsage + "," + errorTreadmillUsage + "\n");

            out.print("\n");

            out.print("meanValueEstimatorBikeUsage\n");

            out.print(meanValueEstimatorBikeUsage + "," + errorBikeUsage + "\n");

            out.print("\n");

            out.print("meanValueEstimatorTimeInGym\n");

            out.print(meanValueEstimatorTimeInGym + "," + errorTimeInGym + "\n");


            }catch (Exception ignored){}
        }



        private static double sqr(double value) {
            return value * value;
        }

}
