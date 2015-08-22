/**
 * Created by Vítor on 19/08/2015.
 *
 *
 * This class is the return of Gym.run(), so you don't need multiple functions for 6.1. Shoulda've done it at Simulator.run() too
 */
public class Metrics {

    public double getMeanBikeUsage() {
        return meanBikeUsage;
    }

    public void setMeanBikeUsage(int meanBikeUsage) {
        this.meanBikeUsage = meanBikeUsage;
    }

    public double getMeanTreadmillUsage() {
        return meanTreadmillUsage;
    }

    public void setMeanTreadmillUsage(int meanTreadmillUsage) {
        this.meanTreadmillUsage = meanTreadmillUsage;
    }

    public double getMeanTimeInGym() {
        return meanTimeInGym;
    }

    public void setMeanTimeInGym(double meanTimeInGym) {
        this.meanTimeInGym = meanTimeInGym;
    }

    public double meanBikeUsage;
    public double meanTreadmillUsage;
    public double meanTimeInGym;

    public Metrics( double meanTreadmillUsage, double meanBikeUsage, double meanTimeInGym) {
        this.meanBikeUsage = meanBikeUsage;
        this.meanTreadmillUsage = meanTreadmillUsage;
        this.meanTimeInGym = meanTimeInGym;
    }


}
