import java.util.Random;

/**
 * Created by erickpires on 05/08/15.
 */
public class UniformDistribution implements Distribution {

    private double lowerBound;
    private double upperBound;
    private Random random;

    public UniformDistribution(double lowerBound, double upperBound) {
        if(lowerBound >= upperBound)
            throw new IllegalArgumentException("Upper bound must be greater than lower bound");

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        random = new Random(System.currentTimeMillis());
    }

    @Override
    public double nextNumber() {
        double uniform = random.nextDouble();
        double diff = upperBound - lowerBound;

        return lowerBound + (uniform * diff);
    }
}
