/**
 * Created by erickpires on 05/08/15.
 */
public class Main {

    private static final int NUMBER_OF_ITERARTIONS = 30;
    private static final double Z = 1.96;

    public static void main (String[] args) {
        double meanSum = 0.0;
        double[] values = new double[NUMBER_OF_ITERARTIONS];

        for (int i = 0; i < NUMBER_OF_ITERARTIONS; i++) {
            Simulator simulator = new Simulator(0.01, 1.0, 0.9);
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

        double tmp = Z *(standardDeviation / Math.sqrt(NUMBER_OF_ITERARTIONS));

        double lowerConfidenceIntervalPoint = meanValueEstimator - tmp;
        double upperConfidenceIntervalPoint = meanValueEstimator + tmp;

        System.out.println("Mean Estimator: " + meanValueEstimator);
        System.out.println("Confidence Interval is: [" + lowerConfidenceIntervalPoint + " : " + upperConfidenceIntervalPoint + "]");

        //System.out.println("Mean value of number of clients " + meanValueEstimator);

//        Simulator simulator = new Simulator(0.01, 1.0, 0.9);
//        System.out.println(simulator.run());
      }




    private static double sqr(double value) {
        return value * value;
    }
}
