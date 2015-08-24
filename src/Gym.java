import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Vítor on 18/08/2015.
 */


public class Gym {

    private static final double TIME_LIMIT = 100000;

    private double reentryIntoBike;

    private double reentryIntoTreadmill;

    private Distribution serviceBikeDistribution;

    private Distribution serviceTreadmillDistribution;

    private Distribution arrivalsDistribution;


    private double lambda;

    private int numberOfClientsBike;

    private int numberOfClientsTreadmill;

    private List<EventG> events = new ArrayList<EventG>();

    private List<EventGR> eventsGR = new ArrayList<EventGR>();

    private List<Double> answer = new ArrayList<Double>();
    private Random random = new Random(System.currentTimeMillis());


    public Gym(double reentryIntoBike, double reentryIntoTreadmill, Distribution serviceTreadmillDistribution, Distribution serviceBikeDistribution, Distribution arrivalsDistribution, double lambda) {
        this.reentryIntoBike = reentryIntoBike;
        this.reentryIntoTreadmill = reentryIntoTreadmill;
        this.serviceBikeDistribution = serviceBikeDistribution;
        this.arrivalsDistribution = arrivalsDistribution;
        this.serviceTreadmillDistribution = serviceTreadmillDistribution;
        this.lambda = lambda;

        init();
    }


    private void addEventGR(EventGR event) {
        eventsGR.add(event);
        Collections.sort(eventsGR);
    }


    private void init() {


        EventGR firstEventGR = new EventGR(arrivalsDistribution.nextNumber(), EventGR.EventType.treadmillEntry);


        numberOfClientsBike = 0;
        numberOfClientsTreadmill = 0;

//        addEventG(firstEvent);
        addEventGR(firstEventGR);


    }

    public Metrics runalt() {

        double areaBike = 0;
        double areaTreadmill = 0;
        double lastEventTimeBike = 0;
        double lastEventTimeTreadmill = 0;
        double lastNumberOfClientsBike = 0;
        double lastNumberOfClientsTreadmill = 0;





        while(!eventsGR.isEmpty()) {


            EventGR currentEvent = eventsGR.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {

                case treadmillEntry:

                    numberOfClientsTreadmill++;

                    double newEntryTime = currentEvent.getTime() + arrivalsDistribution.nextNumber();
                    EventGR newEntryEvent = new EventGR(newEntryTime, EventGR.EventType.treadmillEntry);
                    addEventGR(newEntryEvent);


                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }


                    areaTreadmill += lastNumberOfClientsTreadmill*(currentEvent.getTime()-lastEventTimeTreadmill);
                    lastEventTimeTreadmill = currentEvent.getTime();
                    lastNumberOfClientsTreadmill = numberOfClientsTreadmill;


                    break;

                case treadmillReentry:

                    numberOfClientsTreadmill++;

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }


                    areaTreadmill += lastNumberOfClientsTreadmill*(currentEvent.getTime()-lastEventTimeTreadmill);
                    lastEventTimeTreadmill = currentEvent.getTime();
                    lastNumberOfClientsTreadmill = numberOfClientsTreadmill;


                    break;

                case treadmillExit:

                    if (random.nextDouble() < reentryIntoBike) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.bikeEntry);
                        addEventGR(instant);


                    }




                    numberOfClientsTreadmill--;

                    if(numberOfClientsTreadmill > 0) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }


                    areaTreadmill += lastNumberOfClientsTreadmill*(currentEvent.getTime()-lastEventTimeTreadmill);
                    lastEventTimeTreadmill = currentEvent.getTime();
                    lastNumberOfClientsTreadmill = numberOfClientsTreadmill;

                    break;

                case bikeEntry:


                    numberOfClientsBike++;

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }

                    areaBike += lastNumberOfClientsBike*(currentEvent.getTime()-lastEventTimeBike);
                    lastEventTimeBike = currentEvent.getTime();
                    lastNumberOfClientsBike = numberOfClientsBike;

                    break;

                case bikeExit:

                    numberOfClientsBike--;


                    if (random.nextDouble() < reentryIntoTreadmill) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.treadmillReentry);
                        addEventGR(instant);

//                        System.out.println("Reentry Happened\n");

                    }

                    else{

//                        System.out.println("No reentry\n");

                    }

                    if(numberOfClientsBike > 0) {

                        double newExitTime = currentEvent.getTime() + serviceBikeDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }

                    areaBike += lastNumberOfClientsBike*(currentEvent.getTime()-lastEventTimeBike);
                    lastEventTimeBike = currentEvent.getTime();
                    lastNumberOfClientsBike = numberOfClientsBike;
                    break;


            }


        }


        /*

        Time for some theory. According to Sadoc's email

        'E[N] = E[Ne] + E[Nb] and then you apply Little's'

        and Little is

        L = lambda * W , where
        L = Average number of items in the queuing system ( E[N] i think )
        lambda = average number of items arriving per unit time
        W= average waiting time in the system ( our answer )

        */




// Metrics(int meanBikeUsage, int meanTreadmillUsage, double meanTimeInGym)

        double meanNumberOfClientsBike = areaBike/lastEventTimeBike;
        double meanNumberOfClientsTreadmill = areaTreadmill/lastEventTimeTreadmill;
        double meanNumberOfClientsTotal = meanNumberOfClientsBike + meanNumberOfClientsTreadmill;
        double meanWaitingTime = meanNumberOfClientsTotal / lambda;

        return new Metrics(meanNumberOfClientsTreadmill, meanNumberOfClientsBike, meanWaitingTime );

    }




    public Metrics run() {

        double areaBike = 0;
        double areaTreadmill = 0;
        double lastEventTimeBike = 0;
        double lastEventTimeTreadmill = 0;
        double lastNumberOfClientsBike = 0;
        double lastNumberOfClientsTreadmill = 0;


        while(!eventsGR.isEmpty()) {


            EventGR currentEvent = eventsGR.remove(0);

     //       System.out.println(currentEvent.getType() + "\n");

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {

                case treadmillEntry:

                    numberOfClientsTreadmill++;

                    double newEntryTime = currentEvent.getTime() + arrivalsDistribution.nextNumber();
                    EventGR newEntryEvent = new EventGR(newEntryTime, EventGR.EventType.treadmillEntry);
                    addEventGR(newEntryEvent);


                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;

                case treadmillReentry:

                    numberOfClientsTreadmill++;

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;

                case treadmillExit:


                    numberOfClientsTreadmill--;

                    if (random.nextDouble() < reentryIntoBike) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.bikeEntry);
                        addEventGR(instant);

 //                       System.out.println("Decided to go to the bike\n" );

                    }

 //                   else {
 //                       System.out.println("Quit from treadmill\n");
 //                   }


                    if(numberOfClientsTreadmill > 0) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }
                    break;

                case bikeEntry:


                    numberOfClientsBike++;

                    if(numberOfClientsBike == 1) {

                        double newExitTime = currentEvent.getTime() + serviceBikeDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }

                    break;

                case bikeExit:

                    numberOfClientsBike--;


                    if (random.nextDouble() < reentryIntoTreadmill) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.treadmillReentry);
                        addEventGR(instant);

//                        System.out.println("Reentry Happened\n");

                    }

                    else{

//                        System.out.println("No reentry\n");

                    }

                    if(numberOfClientsBike > 0) {

                        double newExitTime = currentEvent.getTime() + serviceBikeDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }
                    break;


            }

            switch (currentEvent.getMachine()) {

                case bike:

//                    System.out.println("Entered case bike. Event type was = " + currentEvent.getType() + "\n");

                    areaBike += lastNumberOfClientsBike*(currentEvent.getTime()-lastEventTimeBike);
                    lastEventTimeBike = currentEvent.getTime();
                    lastNumberOfClientsBike = numberOfClientsBike;

//                   System.out.println("Did the sums for areaBike, ending this loop with = " + areaBike + "\n");

//                    System.out.println("Also the meanNumberOfClientsBike as right now is = " + areaBike/lastEventTimeBike + "\n");

                    break;

                case treadmill:

                    areaTreadmill += lastNumberOfClientsTreadmill*(currentEvent.getTime()-lastEventTimeTreadmill);
                    lastEventTimeTreadmill = currentEvent.getTime();
                    lastNumberOfClientsTreadmill = numberOfClientsTreadmill;

            }

        }


        /*

        Time for some theory. According to Sadoc's email

        'E[N] = E[Ne] + E[Nb] and then you apply Little's'

        and Little is

        L = lambda * W , where
        L = Average number of items in the queuing system ( E[N] i think )
        lambda = average number of items arriving per unit time
        W= average waiting time in the system ( our answer )

        */




// Metrics(int meanBikeUsage, int meanTreadmillUsage, double meanTimeInGym)

        double meanNumberOfClientsBike = areaBike/lastEventTimeBike;
        double meanNumberOfClientsTreadmill = areaTreadmill/lastEventTimeTreadmill;
        double meanNumberOfClientsTotal = meanNumberOfClientsBike + meanNumberOfClientsTreadmill;
        double meanWaitingTime = meanNumberOfClientsTotal / lambda;

    return new Metrics(meanNumberOfClientsTreadmill, meanNumberOfClientsBike, meanWaitingTime );

    }



    public void sampleExogeanEntry(String name) {

        double areaBike = 0;
        double areaTreadmill = 0;
        double lastEventTimeBike = 0;
        double lastEventTimeTreadmill = 0;
        double lastNumberOfClientsBike = 0;
        double lastNumberOfClientsTreadmill = 0;
        answer = new ArrayList<Double>();

        double lastEventEntryTime = 0;
        double timeSinceLastEntry = 0;



        while(!eventsGR.isEmpty()) {

            EventGR currentEvent = eventsGR.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {

                case treadmillEntry:

                    numberOfClientsTreadmill++;

                    timeSinceLastEntry = currentEvent.getTime() - lastEventEntryTime;

                    answer.add(timeSinceLastEntry);

                    lastEventEntryTime = currentEvent.getTime();

                    double newEntryTime = currentEvent.getTime() + arrivalsDistribution.nextNumber();
                    EventGR newEntryEvent = new EventGR(newEntryTime, EventGR.EventType.treadmillEntry);
                    addEventGR(newEntryEvent);


                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;

                case treadmillReentry:

                    numberOfClientsTreadmill++;

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;



                case treadmillExit:

                    if (random.nextDouble() < reentryIntoBike) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.bikeEntry);
                        addEventGR(instant);


                    }

                    numberOfClientsTreadmill--;

                    if(numberOfClientsTreadmill > 0) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }
                    break;

                case bikeEntry:

                    numberOfClientsBike++;

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }

                    break;


                case bikeExit:

                    if (random.nextDouble() < reentryIntoTreadmill) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.treadmillReentry);
                        addEventGR(instant);
                    }

                    numberOfClientsBike--;

                    if(numberOfClientsBike > 0) {

                        double newExitTime = currentEvent.getTime() + serviceBikeDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }
                    break;


            }

            switch (currentEvent.getMachine()) {

                case bike:

                    areaBike += lastNumberOfClientsBike*(currentEvent.getTime()-lastEventTimeBike);
                    lastEventTimeBike = currentEvent.getTime();
                    lastNumberOfClientsBike = numberOfClientsBike;

                    break;

                case treadmill:

                    areaTreadmill += lastNumberOfClientsTreadmill*(currentEvent.getTime()-lastEventTimeTreadmill);
                    lastEventTimeTreadmill = currentEvent.getTime();
                    lastNumberOfClientsTreadmill = numberOfClientsTreadmill;

            }




        }



        try {
            PrintStream out = new PrintStream(new FileOutputStream(name));


            System.out.println("sampleExogeanEntry finished simulating!\n");

            Collections.sort(answer);

            System.out.println("sampleExogeanEntry finished sorting!\n");

            double steps = 1.0 / answer.size();
            int index = 0;
            double axisX = 0;

            while (!answer.isEmpty()) {



                axisX = steps * index;

                out.print(axisX + ",");


                double current;

                current = answer.remove(0);
                out.print(current + "\n");

                index++;
            }

            System.out.println("sampleExogeanEntry finished writing!\n");

            out.close();

        }catch (Exception ignored) {}


    }

    public void sampleTotalEntry(String name) {

        double areaBike = 0;
        double areaTreadmill = 0;
        double lastEventTimeBike = 0;
        double lastEventTimeTreadmill = 0;
        double lastNumberOfClientsBike = 0;
        double lastNumberOfClientsTreadmill = 0;
        answer = new ArrayList<Double>();

        double lastEventEntryTime = 0;
        double timeSinceLastEntry = 0;



        while(!eventsGR.isEmpty()) {

            EventGR currentEvent = eventsGR.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {

                case treadmillEntry:

                    numberOfClientsTreadmill++;

                    timeSinceLastEntry = currentEvent.getTime() - lastEventEntryTime;

                    answer.add(timeSinceLastEntry);

                    lastEventEntryTime = currentEvent.getTime();

                    double newEntryTime = currentEvent.getTime() + arrivalsDistribution.nextNumber();
                    EventGR newEntryEvent = new EventGR(newEntryTime, EventGR.EventType.treadmillEntry);
                    addEventGR(newEntryEvent);


                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;

                case treadmillReentry:

                    numberOfClientsTreadmill++;

                    timeSinceLastEntry = currentEvent.getTime() - lastEventEntryTime;

                    answer.add(timeSinceLastEntry);

                    lastEventEntryTime = currentEvent.getTime();

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;



                case treadmillExit:

                    if (random.nextDouble() < reentryIntoBike) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.bikeEntry);
                        addEventGR(instant);


                    }

                    numberOfClientsTreadmill--;

                    if(numberOfClientsTreadmill > 0) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }
                    break;

                case bikeEntry:

                    numberOfClientsBike++;

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }

                    break;


                case bikeExit:

                    if (random.nextDouble() < reentryIntoTreadmill) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.treadmillEntry);
                        addEventGR(instant);
                    }

                    numberOfClientsBike--;

                    if(numberOfClientsBike > 0) {

                        double newExitTime = currentEvent.getTime() + serviceBikeDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }
                    break;


            }

            switch (currentEvent.getMachine()) {

                case bike:

                    areaBike += lastNumberOfClientsBike*(currentEvent.getTime()-lastEventTimeBike);
                    lastEventTimeBike = currentEvent.getTime();
                    lastNumberOfClientsBike = numberOfClientsBike;

                    break;

                case treadmill:

                    areaTreadmill += lastNumberOfClientsTreadmill*(currentEvent.getTime()-lastEventTimeTreadmill);
                    lastEventTimeTreadmill = currentEvent.getTime();
                    lastNumberOfClientsTreadmill = numberOfClientsTreadmill;

            }




        }



        try {
            PrintStream out = new PrintStream(new FileOutputStream(name));


            System.out.println("sampleTotalEntry finished simulating!\n");

            Collections.sort(answer);

            System.out.println("sampleTotalEntry finished sorting!\n");

            double steps = 1.0 / answer.size();
            int index = 0;
            double axisX = 0;

            while (!answer.isEmpty()) {

                axisX = steps * index;

                out.print(axisX + ",");


                double current;

                current = answer.remove(0);
                out.print(current + "\n");

                index++;
            }

            System.out.println("sampleTotalEntry finished writing!\n");

            out.close();

        }catch (Exception ignored) {}


    }

    public void sampleBikeEntry(String name) {

        double areaBike = 0;
        double areaTreadmill = 0;
        double lastEventTimeBike = 0;
        double lastEventTimeTreadmill = 0;
        double lastNumberOfClientsBike = 0;
        double lastNumberOfClientsTreadmill = 0;
        answer = new ArrayList<Double>();

        double lastEventEntryTime = 0;
        double timeSinceLastEntry = 0;



        while(!eventsGR.isEmpty()) {

            EventGR currentEvent = eventsGR.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {

                case treadmillEntry:

                    numberOfClientsTreadmill++;



                    double newEntryTime = currentEvent.getTime() + arrivalsDistribution.nextNumber();
                    EventGR newEntryEvent = new EventGR(newEntryTime, EventGR.EventType.treadmillEntry);
                    addEventGR(newEntryEvent);


                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;

                case treadmillReentry:

                    numberOfClientsTreadmill++;

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;



                case treadmillExit:

                    if (random.nextDouble() < reentryIntoBike) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.bikeEntry);
                        addEventGR(instant);


                    }

                    numberOfClientsTreadmill--;

                    if(numberOfClientsTreadmill > 0) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }
                    break;

                case bikeEntry:

                    numberOfClientsBike++;

                    timeSinceLastEntry = currentEvent.getTime() - lastEventEntryTime;

                    answer.add(timeSinceLastEntry);

                    lastEventEntryTime = currentEvent.getTime();

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }

                    break;


                case bikeExit:

                    if (random.nextDouble() < reentryIntoTreadmill) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.treadmillReentry);
                        addEventGR(instant);
                    }

                    numberOfClientsBike--;

                    if(numberOfClientsBike > 0) {

                        double newExitTime = currentEvent.getTime() + serviceBikeDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }
                    break;


            }

            switch (currentEvent.getMachine()) {

                case bike:

                    areaBike += lastNumberOfClientsBike*(currentEvent.getTime()-lastEventTimeBike);
                    lastEventTimeBike = currentEvent.getTime();
                    lastNumberOfClientsBike = numberOfClientsBike;

                    break;

                case treadmill:

                    areaTreadmill += lastNumberOfClientsTreadmill*(currentEvent.getTime()-lastEventTimeTreadmill);
                    lastEventTimeTreadmill = currentEvent.getTime();
                    lastNumberOfClientsTreadmill = numberOfClientsTreadmill;

            }




        }



        try {
            PrintStream out = new PrintStream(new FileOutputStream(name));


            System.out.println("sampleBikeEntry finished simulating!\n");

            Collections.sort(answer);

            System.out.println("sampleBikeEntry finished sorting!\n");

            double steps = 1.0 / answer.size();
            int index = 0;
            double axisX = 0;

            while (!answer.isEmpty()) {

                axisX = steps * index;

                out.print(axisX + ",");


                double current;

                current = answer.remove(0);
                out.print(current + "\n");

                index++;
            }

            System.out.println("sampleBikeEntry finished writing!\n");

            out.close();

        }catch (Exception ignored) {}


    }

    public void sampleTreadmillQuit(String name) {

        double areaBike = 0;
        double areaTreadmill = 0;
        double lastEventTimeBike = 0;
        double lastEventTimeTreadmill = 0;
        double lastNumberOfClientsBike = 0;
        double lastNumberOfClientsTreadmill = 0;
        answer = new ArrayList<Double>();

        double lastEventExitTime = 0;
        double timeSinceLastExit = 0;
        numberOfClientsTreadmill = 0;
        numberOfClientsBike = 0;



        while(!eventsGR.isEmpty()) {

            EventGR currentEvent = eventsGR.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

//           System.out.println("Type = " + currentEvent.getType() + " , Time = " + currentEvent.getTime());

            switch (currentEvent.getType()) {

                case treadmillEntry:


                    numberOfClientsTreadmill++;


                    double newEntryTime = currentEvent.getTime() + arrivalsDistribution.nextNumber();
                    EventGR newEntryEvent = new EventGR(newEntryTime, EventGR.EventType.treadmillEntry);
                    addEventGR(newEntryEvent);


                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;

                case treadmillReentry:

                    numberOfClientsTreadmill++;

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;



                case treadmillExit:

                    if (random.nextDouble() < reentryIntoBike) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.bikeEntry);
                        addEventGR(instant);

                    }
                    else{
                        timeSinceLastExit = currentEvent.getTime() - lastEventExitTime;

 //                       System.out.println("timeSinceLastExit = " + timeSinceLastExit);

                        answer.add(timeSinceLastExit);

                        lastEventExitTime = currentEvent.getTime();

                    }

                    numberOfClientsTreadmill--;

                    if(numberOfClientsTreadmill > 0) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }
                    break;

                case bikeEntry:

                    numberOfClientsBike++;


                    if(numberOfClientsTreadmill == 1) {


//                        System.out.println("numberOfClientsBike here should be 0 but it is = " + numberOfClientsBike +"\n" );

                        double newExitTime = currentEvent.getTime() - 0.5;
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }

//                    System.out.println("numberOfClientsBike right after the increase = " + numberOfClientsBike +"\n" );

                    break;


                case bikeExit:

//                    System.out.println("got into bike exit\n");

                    if (random.nextDouble() < reentryIntoTreadmill) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.treadmillReentry);
                        addEventGR(instant);
                    }

                    numberOfClientsBike--;

                    if(numberOfClientsBike > 0) {

                        double newExitTime = currentEvent.getTime() + serviceBikeDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }
                    break;


            }

            switch (currentEvent.getMachine()) {

                case bike:

                    areaBike += lastNumberOfClientsBike*(currentEvent.getTime()-lastEventTimeBike);
                    lastEventTimeBike = currentEvent.getTime();
                    lastNumberOfClientsBike = numberOfClientsBike;

                    break;

                case treadmill:

                    areaTreadmill += lastNumberOfClientsTreadmill*(currentEvent.getTime()-lastEventTimeTreadmill);
                    lastEventTimeTreadmill = currentEvent.getTime();
                    lastNumberOfClientsTreadmill = numberOfClientsTreadmill;

            }




        }



        try {
            PrintStream out = new PrintStream(new FileOutputStream(name));


            System.out.println("sampleTreadmillQuit finished simulating!\n");

            Collections.sort(answer);

            System.out.println("sampleTreadmillQuit finished sorting!\n");

            double steps = 1.0 / answer.size();
            int index = 0;
            double axisX = 0;

            while (!answer.isEmpty()) {


                axisX = steps * index;

                out.print(axisX + ",");


                double current;

                current = answer.remove(0);
                out.print(current + "\n");

//                System.out.println("index = " + index + "\ncurrent = " + current);

                index++;
            }

            System.out.println("sampleTreadmillQuit finished writing!\n");

            out.close();

        }catch (Exception ignored) {}


    }

    public void sampleBikeQuit(String name) {
        ;
        answer = new ArrayList<Double>();

        double lastEventExitTime = 0;
        double timeSinceLastExit = 0;



        while(!eventsGR.isEmpty()) {

            EventGR currentEvent = eventsGR.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {

                case treadmillEntry:

                    numberOfClientsTreadmill++;


                    double newEntryTime = currentEvent.getTime() + arrivalsDistribution.nextNumber();
                    EventGR newEntryEvent = new EventGR(newEntryTime, EventGR.EventType.treadmillEntry);
                    addEventGR(newEntryEvent);


                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;

                case treadmillReentry:

                    numberOfClientsTreadmill++;

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;



                case treadmillExit:

                    if (random.nextDouble() < reentryIntoBike) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.bikeEntry);
                        addEventGR(instant);


                    }

                    numberOfClientsTreadmill--;

                    if(numberOfClientsTreadmill > 0) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }
                    break;

                case bikeEntry:

                    numberOfClientsBike++;

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }

                    break;


                case bikeExit:

                    if (random.nextDouble() < reentryIntoTreadmill) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.treadmillReentry);
                        addEventGR(instant);
                    }

                    else{

                        timeSinceLastExit = currentEvent.getTime() - lastEventExitTime;

                        answer.add(timeSinceLastExit);

                        lastEventExitTime = currentEvent.getTime();



                    }

                    numberOfClientsBike--;

                    if(numberOfClientsBike > 0) {

                        double newExitTime = currentEvent.getTime() + serviceBikeDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }
                    break;


            }





        }



        try {
            PrintStream out = new PrintStream(new FileOutputStream(name));


            System.out.println("sampleBikeQuit finished simulating!\n");

            Collections.sort(answer);

            System.out.println("sampleBikeQuit finished sorting!\n");

            double steps = 1.0 / answer.size();
            int index = 0;
            double axisX = 0;

            while (!answer.isEmpty()) {

                axisX = steps * index;

                out.print(axisX + ",");


                double current;

                current = answer.remove(0);
                out.print(current + "\n");

                index++;
            }

            System.out.println("sampleBikeQuit finished writing!\n");

            out.close();

        }catch (Exception ignored) {}


    }

    public void sampleTreadmillReentry(String name) {

        double areaBike = 0;
        double areaTreadmill = 0;
        double lastEventTimeBike = 0;
        double lastEventTimeTreadmill = 0;
        double lastNumberOfClientsBike = 0;
        double lastNumberOfClientsTreadmill = 0;
        answer = new ArrayList<Double>();

        double lastEventEntryTime = 0;
        double timeSinceLastEntry = 0;



        while(!eventsGR.isEmpty()) {

            EventGR currentEvent = eventsGR.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {

                case treadmillEntry:

                    numberOfClientsTreadmill++;



                    double newEntryTime = currentEvent.getTime() + arrivalsDistribution.nextNumber();
                    EventGR newEntryEvent = new EventGR(newEntryTime, EventGR.EventType.treadmillEntry);
                    addEventGR(newEntryEvent);


                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;

                case treadmillReentry:

                    numberOfClientsTreadmill++;

                    timeSinceLastEntry = currentEvent.getTime() - lastEventEntryTime;

                    answer.add(timeSinceLastEntry);

                    lastEventEntryTime = currentEvent.getTime();

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;



                case treadmillExit:

                    if (random.nextDouble() < reentryIntoBike) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.bikeEntry);
                        addEventGR(instant);


                    }

                    numberOfClientsTreadmill--;

                    if(numberOfClientsTreadmill > 0) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }
                    break;

                case bikeEntry:

                    numberOfClientsBike++;

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }

                    break;


                case bikeExit:

                    if (random.nextDouble() < reentryIntoTreadmill) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.treadmillReentry);
                        addEventGR(instant);
                    }

                    numberOfClientsBike--;

                    if(numberOfClientsBike > 0) {

                        double newExitTime = currentEvent.getTime() + serviceBikeDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }
                    break;


            }

            switch (currentEvent.getMachine()) {

                case bike:

                    areaBike += lastNumberOfClientsBike*(currentEvent.getTime()-lastEventTimeBike);
                    lastEventTimeBike = currentEvent.getTime();
                    lastNumberOfClientsBike = numberOfClientsBike;

                    break;

                case treadmill:

                    areaTreadmill += lastNumberOfClientsTreadmill*(currentEvent.getTime()-lastEventTimeTreadmill);
                    lastEventTimeTreadmill = currentEvent.getTime();
                    lastNumberOfClientsTreadmill = numberOfClientsTreadmill;

            }




        }



        try {
            PrintStream out = new PrintStream(new FileOutputStream(name));


            System.out.println("sampleTreadmillReentry finished simulating!\n");

            Collections.sort(answer);

            System.out.println("sampleTreadmillReentry finished sorting!\n");

            double steps = 1.0 / answer.size();
            int index = 0;
            double axisX = 0;

            while (!answer.isEmpty()) {

                axisX = steps * index;

                out.print(axisX + ",");


                double current;

                current = answer.remove(0);
                out.print(current + "\n");

                index++;
            }

            System.out.println("sampleTreadmillReentry finished writing!\n");

            out.close();

        }catch (Exception ignored) {}


    }

    public void sampleTotalBikeExits(String name) {

        double areaBike = 0;
        double areaTreadmill = 0;
        double lastEventTimeBike = 0;
        double lastEventTimeTreadmill = 0;
        double lastNumberOfClientsBike = 0;
        double lastNumberOfClientsTreadmill = 0;
        answer = new ArrayList<Double>();

        double lastEventExitTime = 0;
        double timeSinceLastExit = 0;



        while(!eventsGR.isEmpty()) {

            EventGR currentEvent = eventsGR.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {

                case treadmillEntry:

                    numberOfClientsTreadmill++;


                    double newEntryTime = currentEvent.getTime() + arrivalsDistribution.nextNumber();
                    EventGR newEntryEvent = new EventGR(newEntryTime, EventGR.EventType.treadmillEntry);
                    addEventGR(newEntryEvent);


                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;

                case treadmillReentry:

                    numberOfClientsTreadmill++;

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }

                    break;



                case treadmillExit:

                    if (random.nextDouble() < reentryIntoBike) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.bikeEntry);
                        addEventGR(instant);


                    }

                    numberOfClientsTreadmill--;

                    if(numberOfClientsTreadmill > 0) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.treadmillExit);
                        addEventGR(newExitEvent);

                    }
                    break;

                case bikeEntry:

                    numberOfClientsBike++;

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }

                    break;


                case bikeExit:

                    if (random.nextDouble() < reentryIntoTreadmill) {

                        EventGR instant = new EventGR(currentEvent.getTime(), EventGR.EventType.treadmillReentry);
                        addEventGR(instant);
                    }

                    timeSinceLastExit = currentEvent.getTime() - lastEventExitTime;

                    answer.add(timeSinceLastExit);

                    lastEventExitTime = currentEvent.getTime();

                    numberOfClientsBike--;

                    if(numberOfClientsBike > 0) {

                        double newExitTime = currentEvent.getTime() + serviceBikeDistribution.nextNumber();
                        EventGR newExitEvent = new EventGR(newExitTime, EventGR.EventType.bikeExit);
                        addEventGR(newExitEvent);

                    }
                    break;


            }

            switch (currentEvent.getMachine()) {

                case bike:

                    areaBike += lastNumberOfClientsBike*(currentEvent.getTime()-lastEventTimeBike);
                    lastEventTimeBike = currentEvent.getTime();
                    lastNumberOfClientsBike = numberOfClientsBike;

                    break;

                case treadmill:

                    areaTreadmill += lastNumberOfClientsTreadmill*(currentEvent.getTime()-lastEventTimeTreadmill);
                    lastEventTimeTreadmill = currentEvent.getTime();
                    lastNumberOfClientsTreadmill = numberOfClientsTreadmill;

            }




        }



        try {
            PrintStream out = new PrintStream(new FileOutputStream(name));


            System.out.println("sampleTotalBikeExits finished simulating!\n");

            Collections.sort(answer);

            System.out.println("sampleTotalBikeExits finished sorting!\n");

            double steps = 1.0 / answer.size();
            int index = 0;
            double axisX = 0;

            while (!answer.isEmpty()) {

                axisX = steps * index;

                out.print(axisX + ",");


                double current;

                current = answer.remove(0);
                out.print(current + "\n");

                index++;
            }

            System.out.println("sampleTotalBikeExits finished writing!\n");

            out.close();

        }catch (Exception ignored) {}


    }











}




