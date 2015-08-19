import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by Vítor on 18/08/2015. Watch out for my copypaste errors
 */
public class MainFor5 {

        private static final int NUMBER_OF_ITERATIONS = 30;
        private static final double Z = 1.96;

        public static void main(String[] args) {

            System.out.println("5.1\n");
            varyLambdaSimulateSpaguettiTimeEmpty(Distribution.DistributionType.exponential, 0.0, 1.0, "5dot1-TotalTime.csv");
            varyLambdaSimulateSpaguettiSeeEmpty(Distribution.DistributionType.exponential, 0.0, 1.0, "5dot1-SeeTime.csv");

//            System.out.println("5.2\n");
//            Distribution sceneryFourEntryDistribution = new ExpDistribution(0.01);
//            varyMuSimulateSpaguettiTimeEmpty(sceneryFourEntryDistribution, 0.9, "5dot2-TotalTime.csv");
//            varyMuSimulateSpaguettiSeeEmpty(sceneryFourEntryDistribution, 0.9, "5dot2-SeeTime.csv");
//
//            System.out.println("5.3\n");
//            sceneryFourEntryDistribution = new ExpDistribution(0.01);
//            varyMuSimulateSpaguettiTimeEmptyExits(sceneryFourEntryDistribution, 0.9, "5dot3-TotalTime.csv");
//            varyMuSimulateSpaguettiSeeEmptyExits(sceneryFourEntryDistribution, 0.9, "5dot3-SeeTime.csv");
        }


    /* /---------------------/
      /     Vary methods    /
     /---------------------/ */

    private static void varyLambdaSimulateSpaguettiTimeEmpty(Distribution.DistributionType type, double reentryProbability,
                                                            double mu, String outputFileName){//5.1

        try {
            PrintStream out = new PrintStream(new FileOutputStream(outputFileName));

            Distribution exitDistribution = new ExpDistribution(mu);

            for (double lambda = 0.05; lambda <= 0.9001; lambda += 0.05) {
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
                simulateSpaguettiTimeEmpty(entryDistribution, exitDistribution, reentryProbability, out);// Only change from the original
            }
        }catch (Exception ignored) {}
    }

    private static void varyLambdaSimulateSpaguettiSeeEmpty(Distribution.DistributionType type, double reentryProbability,
                                                             double mu, String outputFileName){//5.1

        try {
            PrintStream out = new PrintStream(new FileOutputStream(outputFileName));

            Distribution exitDistribution = new ExpDistribution(mu);

            for (double lambda = 0.05; lambda <= 0.9001; lambda += 0.05) {
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
                simulateSpaghettiSeeEmpty(entryDistribution, exitDistribution, reentryProbability, out);// Only change from the original
            }
        }catch (Exception ignored) {}
    }

    private static void varyMuSimulateSpaguettiTimeEmpty(Distribution entryDistribution, double reentryProbability,
                                                         String outputFileName) {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(outputFileName));
            for (double mu = 1.0; mu <= 10.0; mu += 0.5) {
                Distribution exitDistribution = new ExpDistribution(mu);

                out.print(mu + ",");
                simulateSpaguettiTimeEmpty(entryDistribution, exitDistribution, reentryProbability, out);
            }

            out.close();
        }catch (Exception ignored){}
    }

    private static void varyMuSimulateSpaguettiSeeEmpty(Distribution entryDistribution, double reentryProbability,
                                                String outputFileName) {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(outputFileName));
            for (double mu = 1.0; mu <= 10.0; mu += 0.5) {
                Distribution exitDistribution = new ExpDistribution(mu);

                out.print(mu + ",");
                simulateSpaghettiSeeEmpty(entryDistribution, exitDistribution, reentryProbability, out);
            }

            out.close();
        }catch (Exception ignored){}
    }

    private static void varyMuSimulateSpaguettiTimeEmptyExits(Distribution entryDistribution, double reentryProbability,
                                                String outputFileName) {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(outputFileName));
            for (double mu = 1.0; mu <= 10.0; mu += 0.5) {
                Distribution exitDistribution = new ExpDistribution(mu);

                out.print(mu + ",");
                simulateSpaghettiTotalTimeExits(entryDistribution, exitDistribution, reentryProbability, out);
            }

            out.close();
        }catch (Exception ignored){}
    }

    private static void varyMuSimulateSpaguettiSeeEmptyExits(Distribution entryDistribution, double reentryProbability,
                                               String outputFileName) {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(outputFileName));
            for (double mu = 1.0; mu <= 10.0; mu += 0.5) {
                Distribution exitDistribution = new ExpDistribution(mu);

                out.print(mu + ",");
                simulateSpaghettiSeeEmptyExits(entryDistribution, exitDistribution, reentryProbability, out);
            }

            out.close();
        }catch (Exception ignored){}
    }


    /* /-------------------------/
      /     Simulate methods    /
     /-------------------------/ */


    private static void simulateSpaguettiTimeEmpty(Distribution entryDistribution, Distribution exitDistribution,
                                                   double reentryProbability, PrintStream out) {

        double meanSum = 0.0;
        double[] values = new double[NUMBER_OF_ITERATIONS];

        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            Simulator simulator = new Simulator(entryDistribution, exitDistribution, reentryProbability);
            values[i] = simulator.spaghettiTimeEmpty();
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

    private static void simulateSpaghettiSeeEmpty(Distribution entryDistribution, Distribution exitDistribution,
                                                   double reentryProbability, PrintStream out) {

        double meanSum = 0.0;
        double[] values = new double[NUMBER_OF_ITERATIONS];

        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            Simulator simulator = new Simulator(entryDistribution, exitDistribution, reentryProbability);
            values[i] = simulator.spaghettiSeeEmpty();
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

    private static void simulateSpaghettiTotalTimeExits(Distribution entryDistribution, Distribution exitDistribution,
                                                   double reentryProbability, PrintStream out) {

        double meanSum = 0.0;
        double[] values = new double[NUMBER_OF_ITERATIONS];

        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            Simulator simulator = new Simulator(entryDistribution, exitDistribution, reentryProbability);
            values[i] = simulator.spaghettiTotalTimeExits();
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

    private static void simulateSpaghettiSeeEmptyExits(Distribution entryDistribution, Distribution exitDistribution,
                                                   double reentryProbability, PrintStream out) {

        double meanSum = 0.0;
        double[] values = new double[NUMBER_OF_ITERATIONS];

        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            Simulator simulator = new Simulator(entryDistribution, exitDistribution, reentryProbability);
            values[i] = simulator.spaghettiSeeEmptyExits();
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

        private static double sqr(double value) {
            return value * value;
        }

    }