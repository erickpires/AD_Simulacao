import java.util.Random;

/**
 * Created by erickpires on 05/08/15.
 */
public class ExpDistribution implements Distribution {

    private double lambda;
    private Random random;

    public ExpDistribution(double lambda) {
        this.lambda = lambda;
        random = new Random(System.currentTimeMillis()); // seeding with current time
    }

    @Override
    public double nextNumber() {
        double uniform = random.nextDouble();
        return - Math.log(1 - uniform) / lambda;
    }
}
