/**
 * Created by erickpires on 05/08/15.
 */
public class DeterministicDistribution implements Distribution {

    private double value;

    public DeterministicDistribution(double value) {
        this.value = value;
    }

    @Override
    public double nextNumber() {
        return value;
    }
}
