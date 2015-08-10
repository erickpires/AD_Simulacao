/**
 * Created by erickpires on 05/08/15.
 */
public class DeterministicDistribution implements Distribution {

    private double rate;

    public DeterministicDistribution(double rate) {
        this.rate = rate;
    }

    @Override
    public double nextNumber() {
        return 1.0 / rate;
    }
}
