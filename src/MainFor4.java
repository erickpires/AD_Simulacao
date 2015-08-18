import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by Vítor on 17/08/2015.Watch out for my copypaste errors
 */
public class MainFor4 {

    private static final int NUMBER_OF_ITERATIONS = 30;
    private static final double Z = 1.96;

    public static void main(String[] args) {

        // 4.1
        System.out.println("4.1\n");
        varyLambdaSimulateMeanTotalExitTime(Distribution.DistributionType.exponential, 0.0, 1.0, "4dot1.csv");

        //4.2
        System.out.println("4.2\n");
        Distribution sceneryFourEntryDistribution = new ExpDistribution(0.01);
        varyMuSimulateMeanExogenExitTime(sceneryFourEntryDistribution, 0.9, "4dot2.csv");

        //4.3
        System.out.println("4.3\n");
        sceneryFourEntryDistribution = new ExpDistribution(0.01);
        varyMuSimulateMeanTotalExitTime(sceneryFourEntryDistribution, 0.9, "4dot3.csv");

        //4.4
        System.out.println("4.4\n");
        sceneryFourEntryDistribution = new ExpDistribution(0.01);
        varyMuSimulateMeanTotalEntryTime(sceneryFourEntryDistribution, 0.9, "4dot4.csv");


    }




    /* /---------------------/
      /     Vary methods    /
     /---------------------/ */

    private static void varyLambdaSimulateMeanTotalExitTime(Distribution.DistributionType type, double reentryProbability,
                                   double mu, String outputFileName){//4.1

        try {
            PrintStream out = new PrintStream(new FileOutputStream(outputFileName));

            Distribution exitDistribution = new ExpDistribution(mu);

            for (double lambda = 0.05; lambda <= 0.9; lambda += 0.05) {
                Distribution entryDistribution = null;
                switch (type) {
                    case exponential:
                        entryDistribution = new ExpDistribution(lambda);
                        break;
                    case deterministic:
                        entryDistribution = new DeterministicDistribution(lambda);
                        break;
                    case uniform:
                        entryDistribution = new UniformDistribution(lambda);
                        break;
                }

                out.print(lambda + ",");
                simulateMeanTotalExitTime(entryDistribution, exitDistribution, reentryProbability, out);// Only change from the original
            }
        }catch (Exception ignored) {}
    }

    private static void varyMuSimulateMeanExogenExitTime(Distribution entryDistribution, double reentryProbability,
                                                          String outputFileName) {//4.2
        try {
            PrintStream out = new PrintStream(new FileOutputStream(outputFileName));
            for (double mu = 1.0; mu <= 10.0; mu += 0.5) {
                Distribution exitDistribution = new ExpDistribution(mu);

                out.print(mu + ",");
                simulateMeanExogenExitTime(entryDistribution, exitDistribution, reentryProbability, out);
            }

            out.close();
        }catch (Exception ignored){}
    }

    private static void varyMuSimulateMeanTotalExitTime(Distribution entryDistribution, double reentryProbability,
                                                          String outputFileName) {//4.3
        try {
            PrintStream out = new PrintStream(new FileOutputStream(outputFileName));
            for (double mu = 1.0; mu <= 10.0; mu += 0.5) {
                Distribution exitDistribution = new ExpDistribution(mu);

                out.print(mu + ",");
                simulateMeanTotalExitTime(entryDistribution, exitDistribution, reentryProbability, out);
            }

            out.close();
        }catch (Exception ignored){}
    }

    private static void varyMuSimulateMeanTotalEntryTime(Distribution entryDistribution, double reentryProbability,
                                                        String outputFileName) {//4.4
        try {
            PrintStream out = new PrintStream(new FileOutputStream(outputFileName));
            for (double mu = 1.0; mu <= 10.0; mu += 0.5) {
                Distribution exitDistribution = new ExpDistribution(mu);

                out.print(mu + ",");
                simulateMeanTotalEntryTime(entryDistribution, exitDistribution, reentryProbability, out);
            }

            out.close();
        }catch (Exception ignored){}
    }


    /* /-------------------------/
      /     Simulate methods    /
     /-------------------------/ */

    private static void simulateMeanExogenExitTime(Distribution entryDistribution, Distribution exitDistribution,
                                                    double reentryProbability, PrintStream out) {

        double meanSum = 0.0;
        double[] values = new double[NUMBER_OF_ITERATIONS];

        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            Simulator simulator = new Simulator(entryDistribution, exitDistribution, reentryProbability);
            values[i] = simulator.meanExogenExitTime();
            meanSum += values[i];
        }

        double meanValueEstimator = meanSum / NUMBER_OF_ITERATIONS;

        double diffToMeanSum = 0.0;
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            diffToMeanSum += sqr(values[i] - meanValueEstimator);
        }

        double varianceValueEstimator = diffToMeanSum / (NUMBER_OF_ITERATIONS - 1);
        double standardDeviation = Math.sqrt(varianceValueEstimator);

        double error = Z * (standardDeviation / Math.sqrt(NUMBER_OF_ITERATIONS));

        double lowerConfidenceIntervalPoint = meanValueEstimator - error;
        double upperConfidenceIntervalPoint = meanValueEstimator + error;

        System.out.println("Mean Estimator: " + meanValueEstimator);
        System.out.println("Standard deviation: " + standardDeviation);
        System.out.println("Confidence Interval is: [" + lowerConfidenceIntervalPoint + " : " + upperConfidenceIntervalPoint + "]");

        out.print(meanValueEstimator + "," +  error + "\n");
    }


    private static void simulateMeanTotalExitTime(Distribution entryDistribution, Distribution exitDistribution,
        double reentryProbability, PrintStream out) {

        double meanSum = 0.0;
        double[] values = new double[NUMBER_OF_ITERATIONS];

        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            Simulator simulator = new Simulator(entryDistribution, exitDistribution, reentryProbability);
            values[i] = simulator.meanTotalExitTime();
            meanSum += values[i];
        }

        double meanValueEstimator = meanSum / NUMBER_OF_ITERATIONS;

        double diffToMeanSum = 0.0;
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            diffToMeanSum += sqr(values[i] - meanValueEstimator);
        }

        double varianceValueEstimator = diffToMeanSum / (NUMBER_OF_ITERATIONS - 1);
        double standardDeviation = Math.sqrt(varianceValueEstimator);

        double error = Z * (standardDeviation / Math.sqrt(NUMBER_OF_ITERATIONS));

        double lowerConfidenceIntervalPoint = meanValueEstimator - error;
        double upperConfidenceIntervalPoint = meanValueEstimator + error;

        System.out.println("Mean Estimator: " + meanValueEstimator);
        System.out.println("Standard deviation: " + standardDeviation);
        System.out.println("Confidence Interval is: [" + lowerConfidenceIntervalPoint + " : " + upperConfidenceIntervalPoint + "]");

        out.print(meanValueEstimator + "," +  error + "\n");
    }

    private static void simulateMeanTotalEntryTime(Distribution entryDistribution, Distribution exitDistribution,
                                                   double reentryProbability, PrintStream out) {

        double meanSum = 0.0;
        double[] values = new double[NUMBER_OF_ITERATIONS];

        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            Simulator simulator = new Simulator(entryDistribution, exitDistribution, reentryProbability);
            values[i] = simulator.meanTotalEntryTime();
            meanSum += values[i];
        }

        double meanValueEstimator = meanSum / NUMBER_OF_ITERATIONS;

        double diffToMeanSum = 0.0;
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            diffToMeanSum += sqr(values[i] - meanValueEstimator);
        }

        double varianceValueEstimator = diffToMeanSum / (NUMBER_OF_ITERATIONS - 1);
        double standardDeviation = Math.sqrt(varianceValueEstimator);

        double error = Z * (standardDeviation / Math.sqrt(NUMBER_OF_ITERATIONS));

        double lowerConfidenceIntervalPoint = meanValueEstimator - error;
        double upperConfidenceIntervalPoint = meanValueEstimator + error;

        System.out.println("Mean Estimator: " + meanValueEstimator);
        System.out.println("Standard deviation: " + standardDeviation);
        System.out.println("Confidence Interval is: [" + lowerConfidenceIntervalPoint + " : " + upperConfidenceIntervalPoint + "]");

        out.print(meanValueEstimator + "," + error + "\n");

    }

    private static double sqr(double value) {
        return value * value;
    }

}
