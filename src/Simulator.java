import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by erickpires on 05/08/15.
 */
public class Simulator {
    private static final double TIME_LIMIT = 10000;

    private double reentryProbability;

    private Distribution entryDistribution;
    private Distribution exitDistribution;

    private int numberOfClients;

    private List<Event> events  = new ArrayList<Event>();
    private Random random = new Random(System.currentTimeMillis());

    public Simulator(Distribution entryDistribution, Distribution exitDistribution, double reentryProbability) {
        this.entryDistribution = entryDistribution;
        this.exitDistribution = exitDistribution;
        this.reentryProbability = reentryProbability;

        init();
    }

    private void init() {
        Event firstEvent = new Event(entryDistribution.nextNumber(), Event.EventType.entry);
        numberOfClients = 0;

        addEvent(firstEvent);
    }

    public double run() {
        double area = 0;
        double lastNumberOfClients = 0;
        double lastEventTime = 0;

        while (!events.isEmpty()) {
            Event currentEvent = events.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {
                case entry:
                    numberOfClients++;
//                    System.out.println("Entrou, N=" + numberOfClients);

                    double newEntryTime = currentEvent.getTime() + entryDistribution.nextNumber();
                    Event newEntryEvent = new Event(newEntryTime, Event.EventType.entry);
                    addEvent(newEntryEvent);

                    if(numberOfClients == 1) {

                        double newExitTime = currentEvent.getTime() + exitDistribution.nextNumber();
                        Event newExitEvent = new Event(newExitTime, Event.EventType.exit);
                        addEvent(newExitEvent);

                    }

                    break;

                case exit:

                    if(!(random.nextDouble() < reentryProbability)) {
                        numberOfClients--;
                    }

                    if(numberOfClients>0) {

                        double newExitTime = currentEvent.getTime() + exitDistribution.nextNumber();
                        Event newExitEvent = new Event(newExitTime, Event.EventType.exit);
                        addEvent(newExitEvent);

                    }
                    break;
            }

            area += lastNumberOfClients*(currentEvent.getTime()-lastEventTime);
            lastEventTime = currentEvent.getTime();
            lastNumberOfClients = numberOfClients;
        }

        double meanNumberOfClients = area/lastEventTime;
//        System.out.println("The simulation ended after " + lastEventTime);
//        System.out.println("The average numbers of clients was " + meanNumberOfClients);
        return meanNumberOfClients;
    }

    public double meanTotalExitTime() { // For 4.1 and 4.3
        double area = 0;
        double lastNumberOfClients = 0;
        double lastEventTime = 0;
        double sumTimes = 0;
        double thisExistsToUseDistribution2Times = 0;
        double numberOfExits = 0;

        while (!events.isEmpty()) {
            Event currentEvent = events.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {
                case entry:
                    numberOfClients++;
//                    System.out.println("Entrou, N=" + numberOfClients);

                    double newEntryTime = currentEvent.getTime() + entryDistribution.nextNumber();
                    Event newEntryEvent = new Event(newEntryTime, Event.EventType.entry);
                    addEvent(newEntryEvent);

                    if(numberOfClients == 1) {

                        double newExitTime = currentEvent.getTime() + exitDistribution.nextNumber();
                        Event newExitEvent = new Event(newExitTime, Event.EventType.exit);
                        addEvent(newExitEvent);

                    }

                    break;

                case exit:

                    thisExistsToUseDistribution2Times = exitDistribution.nextNumber();
                    sumTimes += thisExistsToUseDistribution2Times;
                    numberOfExits++;

                    if(!(random.nextDouble() < reentryProbability)) {
                        numberOfClients--;
                    }

                    if(numberOfClients>0) {

                        double newExitTime = currentEvent.getTime() + thisExistsToUseDistribution2Times;
                        Event newExitEvent = new Event(newExitTime, Event.EventType.exit);
                        addEvent(newExitEvent);

                    }
                    break;
            }

            area += lastNumberOfClients*(currentEvent.getTime()-lastEventTime);
            lastEventTime = currentEvent.getTime();
            lastNumberOfClients = numberOfClients;
        }

        double meanNumberOfClients = area/lastEventTime;
//        System.out.println("The simulation ended after " + lastEventTime);
//        System.out.println("The average numbers of clients was " + meanNumberOfClients);
        return sumTimes/numberOfExits;
    }

    public double meanExogenExitTimes() {// For 4.2
        double area = 0;
        double lastNumberOfClients = 0;
        double lastEventTime = 0;
        double sumTimes = 0;
        double thisExistsToUseDistribution2Times =0;
        double numberOfExits = 0;

        while (!events.isEmpty()) {
            Event currentEvent = events.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {
                case entry:
                    numberOfClients++;
//                    System.out.println("Entrou, N=" + numberOfClients);

                    double newEntryTime = currentEvent.getTime() + entryDistribution.nextNumber();
                    Event newEntryEvent = new Event(newEntryTime, Event.EventType.entry);
                    addEvent(newEntryEvent);

                    if(numberOfClients == 1) {

                        double newExitTime = currentEvent.getTime() + exitDistribution.nextNumber();
                        Event newExitEvent = new Event(newExitTime, Event.EventType.exit);
                        addEvent(newExitEvent);

                    }

                    break;

                case exit:

                    if(!(random.nextDouble() < reentryProbability)) {
                        numberOfClients--;


                        if (numberOfClients > 0) {

                            double newExitTime = currentEvent.getTime() + exitDistribution.nextNumber();
                            Event newExitEvent = new Event(newExitTime, Event.EventType.exit);
                            addEvent(newExitEvent);

                        }

                    }

                    else {

                        numberOfExits++;
                        thisExistsToUseDistribution2Times = exitDistribution.nextNumber();
                        sumTimes += thisExistsToUseDistribution2Times;

                        if (numberOfClients > 0) {

                            double newExitTime = currentEvent.getTime() + thisExistsToUseDistribution2Times;
                            Event newExitEvent = new Event(newExitTime, Event.EventType.exit);
                            addEvent(newExitEvent);

                        }





                    }


                    break;
            }

            area += lastNumberOfClients*(currentEvent.getTime()-lastEventTime);
            lastEventTime = currentEvent.getTime();
            lastNumberOfClients = numberOfClients;
        }

        double meanNumberOfClients = area/lastEventTime;
//        System.out.println("The simulation ended after " + lastEventTime);
//        System.out.println("The average numbers of clients was " + meanNumberOfClients);
        return sumTimes/numberOfExits;
    }

    public double meanTotalEntryTime() {// For 4.4
        double area = 0;
        double lastNumberOfClients = 0;
        double lastEventTime = 0;
        double sumTimes = 0;
        double thisExistsToUseDistribution2Times =0;
        double numberOfEntries = 0;

        while (!events.isEmpty()) {
            Event currentEvent = events.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {
                case entry:
                    numberOfClients++;
//                    System.out.println("Entrou, N=" + numberOfClients);

                    thisExistsToUseDistribution2Times = entryDistribution.nextNumber();
                    sumTimes += thisExistsToUseDistribution2Times;
                    numberOfEntries++;


                    double newEntryTime = currentEvent.getTime() + thisExistsToUseDistribution2Times;
                    Event newEntryEvent = new Event(newEntryTime, Event.EventType.entry);
                    addEvent(newEntryEvent);

                    if(numberOfClients == 1) {

                        double newExitTime = currentEvent.getTime() + exitDistribution.nextNumber();
                        Event newExitEvent = new Event(newExitTime, Event.EventType.exit);
                        addEvent(newExitEvent);

                    }

                    break;

                case exit:

                    if(!(random.nextDouble() < reentryProbability)) {
                        numberOfClients--;
                    }

                    if(numberOfClients>0) {

                        double newExitTime = currentEvent.getTime() + exitDistribution.nextNumber();
                        Event newExitEvent = new Event(newExitTime, Event.EventType.exit);
                        addEvent(newExitEvent);

                    }
                    break;
            }

            area += lastNumberOfClients*(currentEvent.getTime()-lastEventTime);
            lastEventTime = currentEvent.getTime();
            lastNumberOfClients = numberOfClients;
        }
        return sumTimes/numberOfEntries;
    }

    public double spaghetti() {// For 5.1 and 5.2
        double area = 0;
        double lastNumberOfClients = 0;
        double lastEventTime = 0;
        double emptyTotalTime = 0;
        double emptyStart = 0;
        int wasItEmptyLastTime = 0;
        double seeEmpty = 0;
        double lifetimeClients = 0;

        while (!events.isEmpty()) {
            Event currentEvent = events.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {
                case entry:

                    //This is for the fraction of the clients that see the system empty

                    lifetimeClients++;

                    if(numberOfClients == 0) {

                        seeEmpty++;
                    }

                    numberOfClients++;
//                    System.out.println("Entrou, N=" + numberOfClients);

                    double newEntryTime = currentEvent.getTime() + entryDistribution.nextNumber();
                    Event newEntryEvent = new Event(newEntryTime, Event.EventType.entry);
                    addEvent(newEntryEvent);

                    if(numberOfClients == 1) {

                        double newExitTime = currentEvent.getTime() + exitDistribution.nextNumber();
                        Event newExitEvent = new Event(newExitTime, Event.EventType.exit);
                        addEvent(newExitEvent);

                    }

                    break;

                case exit:

                    if(!(random.nextDouble() < reentryProbability)) {
                        numberOfClients--;
                    }

                    if(numberOfClients>0) {

                        double newExitTime = currentEvent.getTime() + exitDistribution.nextNumber();
                        Event newExitEvent = new Event(newExitTime, Event.EventType.exit);
                        addEvent(newExitEvent);

                    }
                    break;
            }

            // This is for the empty time

            if (numberOfClients == 0) {
                emptyStart = currentEvent.getTime();
                wasItEmptyLastTime = 1;
            }
            else if(wasItEmptyLastTime == 1){
                  emptyTotalTime+= emptyStart + currentEvent.getTime();
                  emptyStart = 0;
                  wasItEmptyLastTime = 0;
                }


            area += lastNumberOfClients*(currentEvent.getTime()-lastEventTime);
            lastEventTime = currentEvent.getTime();
            lastNumberOfClients = numberOfClients;


        }

        double emptyRelativeTime = emptyTotalTime/lastEventTime;
        double relativeSeeEmpty = seeEmpty/lifetimeClients;

        System.out.println("The time the system spent empty was " + emptyRelativeTime + "of the total time");
        System.out.println("The number of entries that saw the system empty was" + relativeSeeEmpty + "of the number of entries");

        return Math.abs(emptyRelativeTime - relativeSeeEmpty);

    }

    public double spaghettiExits() {// For 5.3
        double area = 0;
        double lastNumberOfClients = 0;
        double lastEventTime = 0;
        double emptyTotalTime = 0;
        double emptyStart = 0;
        int wasItEmptyLastTime = 0;
        double seeEmpty = 0;
        double lifetimeExits = 0;

        while (!events.isEmpty()) {
            Event currentEvent = events.remove(0);

            if(currentEvent.getTime()>TIME_LIMIT) {
                break;
            }

            switch (currentEvent.getType()) {
                case entry:

                    numberOfClients++;
//                    System.out.println("Entrou, N=" + numberOfClients);

                    double newEntryTime = currentEvent.getTime() + entryDistribution.nextNumber();
                    Event newEntryEvent = new Event(newEntryTime, Event.EventType.entry);
                    addEvent(newEntryEvent);

                    if(numberOfClients == 1) {

                        double newExitTime = currentEvent.getTime() + exitDistribution.nextNumber();
                        Event newExitEvent = new Event(newExitTime, Event.EventType.exit);
                        addEvent(newExitEvent);

                    }

                    break;

                case exit:

                    if(!(random.nextDouble() < reentryProbability)) {
                        numberOfClients--;

                        lifetimeExits++;

                        if(numberOfClients == 0) {

                            seeEmpty++;
                        }



                    }

                    if(numberOfClients>0) {

                        double newExitTime = currentEvent.getTime() + exitDistribution.nextNumber();
                        Event newExitEvent = new Event(newExitTime, Event.EventType.exit);
                        addEvent(newExitEvent);

                    }
                    break;
            }

            // This is for the empty time

            if (numberOfClients == 0) {
                emptyStart = currentEvent.getTime();
                wasItEmptyLastTime = 1;
            }
            else if(wasItEmptyLastTime == 1){
                emptyTotalTime+= emptyStart + currentEvent.getTime();
                emptyStart = 0;
                wasItEmptyLastTime = 0;
            }


            area += lastNumberOfClients*(currentEvent.getTime()-lastEventTime);
            lastEventTime = currentEvent.getTime();
            lastNumberOfClients = numberOfClients;


        }

        double emptyRelativeTime = emptyTotalTime/lastEventTime;
        double relativeSeeEmpty = seeEmpty/lifetimeExits;

        System.out.println("The time the system spent empty was " + emptyRelativeTime + "of the total time");
        System.out.println("The number of entries that saw the system empty was" + relativeSeeEmpty + "of the number of entries");

        return Math.abs(emptyRelativeTime - relativeSeeEmpty);

    }

    private void addEvent(Event event) {
        events.add(event);
        Collections.sort(events);
    }
}