import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by erickpires on 05/08/15.
 *
 */
public class Main {

    private static final int NUMBER_OF_ITERARTIONS = 30;
    private static final double Z = 1.96;

    public static void main(String[] args) {
        //Scenery 1
        System.out.println("Scenery 1");
        varyLambda(Distribution.DistributionType.exponential, 0.0, 1.0, "scenery1.csv");

        //Scenery 2
        System.out.println("\nScenery 2");
        varyLambda(Distribution.DistributionType.deterministic, 0.0, 1.0, "scenery2.csv");

        //Scenery 3
        System.out.println("\nScenery 3");
        Distribution sceneryThreeEntryDistribution = new UniformDistribution(5.0, 15.0);
        varyMu(sceneryThreeEntryDistribution, 0.0, "scenery3.csv");

        //Scenery 4
        System.out.println("\nScenery 4");
        Distribution sceneryFourEntryDistribution = new ExpDistribution(0.01);
        varyMu(sceneryFourEntryDistribution, 0.9, "scenery4.csv");

        //Scenery 5
        System.out.println("\nScenery 5");
        Distribution sceneryFiveEntryDistribution = new DeterministicDistribution(0.01);
        varyMu(sceneryFiveEntryDistribution, 0.9, "scenery5.csv");

        //Scenery 6
        System.out.println("\nScenery 6");
        Distribution scenerySixEntryDistribution = new UniformDistribution(50.0, 150.0);
        varyMu(scenerySixEntryDistribution, 0.9, "scenery6.csv");
    }

    private static void varyLambda(Distribution.DistributionType type, double reentryProbability,
                                   double mu, String outputFileName){
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
                simulate(entryDistribution, exitDistribution, reentryProbability, out);
            }
        }catch (Exception ignored) {}
    }

    private static void varyMu(Distribution entryDistribution, double reentryProbability, String outputFileName) {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(outputFileName));
            for (double mu = 1.0; mu <= 10.0; mu += 0.5) {
                Distribution exitDistribution = new ExpDistribution(mu);

                out.print(mu + ",");
                simulate(entryDistribution, exitDistribution, reentryProbability, out);
            }

            out.close();
        }catch (Exception ignored){}
    }

    private static void simulate(Distribution entryDistribution, Distribution exitDistribution,
                                 double reentryProbability, PrintStream out) {

        double meanSum = 0.0;
        double[] values = new double[NUMBER_OF_ITERARTIONS];

        for (int i = 0; i < NUMBER_OF_ITERARTIONS; i++) {
            Simulator simulator = new Simulator(entryDistribution, exitDistribution, reentryProbability);
            values[i] = simulator.run();
            meanSum += values[i];
        }

        double meanValueEstimator = meanSum / NUMBER_OF_ITERARTIONS;

        double diffToMeanSum = 0.0;
        for (int i = 0; i < NUMBER_OF_ITERARTIONS; i++) {
            diffToMeanSum += sqr(values[i] - meanValueEstimator);
        }

        double varianceValueEstimator = diffToMeanSum / (NUMBER_OF_ITERARTIONS - 1);
        double standardDeviation = Math.sqrt(varianceValueEstimator);

        double error = Z * (standardDeviation / Math.sqrt(NUMBER_OF_ITERARTIONS));

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
