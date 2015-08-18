import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Vítor on 18/08/2015.
 */


public class Gym {

    private static final double TIME_LIMIT = 10000;

    private double reentryIntoBike;

    private double reentryIntoTreadmill;

    private Distribution serviceBikeDistribution;

    private Distribution serviceTreadmillDistribution;

    private Distribution arrivalsDistribution;

    private int numberOfClientsBike;

    private int numberOfClientsTreadmill;

    private List<EventG> events = new ArrayList<EventG>();
    private Random random = new Random(System.currentTimeMillis());


    public Gym(double reentryIntoBike, double reentryIntoTreadmill, Distribution serviceTreadmillDistribution, Distribution serviceBikeDistribution, Distribution arrivalsDistribution) {
        this.reentryIntoBike = reentryIntoBike;
        this.reentryIntoTreadmill = reentryIntoTreadmill;
        this.serviceBikeDistribution = serviceBikeDistribution;
        this.arrivalsDistribution = arrivalsDistribution;
        this.serviceTreadmillDistribution = serviceTreadmillDistribution;

        init();
    }

    private void addEventG(EventG event) {
        events.add(event);
        Collections.sort(events);
    }

    private void init() {

        EventG firstEvent = new EventG(arrivalsDistribution.nextNumber(), EventG.EventType.treadmillEntry);

        numberOfClientsBike = 0;
        numberOfClientsTreadmill = 0;

        addEventG(firstEvent);


    }

    private double run() {

        while(!events.isEmpty()) {

            EventG currentEvent = events.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {

                case treadmillEntry:

                    numberOfClientsTreadmill++;

                    double newEntryTime = currentEvent.getTime() + arrivalsDistribution.nextNumber();
                    EventG newEntryEvent = new EventG(newEntryTime, EventG.EventType.treadmillEntry);
                    addEventG(newEntryEvent);


                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventG newExitEvent = new EventG(newExitTime, EventG.EventType.treadmillExit);
                        addEventG(newExitEvent);

                    }

                    break;

                case treadmillExit:

                    if (random.nextDouble() < reentryIntoBike) {

                        EventG instant = new EventG(currentEvent.getTime(), EventG.EventType.bikeEntry);
                        addEventG(instant);


                    }

                    numberOfClientsTreadmill--;

                    if(numberOfClientsTreadmill>0) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventG newExitEvent = new EventG(newExitTime, EventG.EventType.treadmillExit);
                        addEventG(newExitEvent);

                    }
                    break;

                case bikeEntry:

                    numberOfClientsBike++;

                    if(numberOfClientsTreadmill == 1) {

                        double newExitTime = currentEvent.getTime() + serviceTreadmillDistribution.nextNumber();
                        EventG newExitEvent = new EventG(newExitTime, EventG.EventType.treadmillExit);
                        addEventG(newExitEvent);

                    }

                    break;

                case bikeExit:

                    if (random.nextDouble() < reentryIntoTreadmill) {

                        EventG instant = new EventG(currentEvent.getTime(), EventG.EventType.treadmillEntry);
                        addEventG(instant);
                    }

                    numberOfClientsBike--;

                    if(numberOfClientsBike>0) {

                        double newExitTime = currentEvent.getTime() + serviceBikeDistribution.nextNumber();
                        EventG newExitEvent = new EventG(newExitTime, EventG.EventType.bikeExit);
                        addEventG(newExitEvent);

                    }
                    break;


            }








        }




    return 0;

    }








}




