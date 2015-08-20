/**
 * Created by Vítor on 19/08/2015.
 *
 * New main for Question 4
 */
public class Sampler {

    public static void main(String[] args) {

        Simulator scenario1 = new Simulator(new ExpDistribution(0.1),new ExpDistribution(1),0);
        Simulator scenario4 = new Simulator(new ExpDistribution(0.01),new ExpDistribution(1),0.9);

        scenario1.samplesExitTime("new4dot1.csv");

        System.out.println("4.1 done");

        scenario4.samplesExogenExits("new4dot2.csv");

        System.out.println("4.2 done");

        scenario4 = new Simulator(new ExpDistribution(0.01),new ExpDistribution(1),0.9);

        scenario4.samplesExitTime("new4dot3.csv");

        System.out.println("4.3 done");


        scenario4.sampleEntryTime("new4dot4extratime.csv");

        System.out.println("4.4 done");

//        Simulator scenario4AnotherMu =new Simulator(new ExpDistribution(0.01), new ExpDistribution(10), 0.9);
//
//        scenario4AnotherMu.sampleEntryTime("mu10.csv");

        System.out.println("Execution end");

    }



}
