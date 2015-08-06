/**
 * Created by erickpires on 05/08/15.
 */
public interface Distribution {
    double nextNumber();

    enum DistributionType {
        exponential,
        uniform,
        deterministic
    }
}
