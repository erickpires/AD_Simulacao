/**
 * Created by Vítor on 19/08/2015.
 */
public class Sampler {

    public static void main(String[] args) {

        Simulator scenario1 = new Simulator(new ExpDistribution(0.1),new ExpDistribution(1),0);
        Simulator scenario4 = new Simulator(new ExpDistribution(0.01),new ExpDistribution(1),0.9);

        scenario1.samplesExitTime();

        scenario4.samplesExogenExits();

        scenario4.samplesExitTime();

        scenario4.sampleEntryTime();


    }



}
