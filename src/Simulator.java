import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static Event.EventType.*;

/**
 * Created by erickpires on 05/08/15.
 */
public class Simulator {
    private static final double TIME_LIMIT = 1000;

    private double mu; // Service rate
    private double lambda; // Arrival rate
    private double reentryProbability;

    private Distribution entryDistribution;
    private Distribution exitDistribution;

    private int numberOfClients;

    private List<Event> events  = new ArrayList<Event>();
    private Random random = new Random(System.currentTimeMillis());


    public Simulator(double mu, double lambda, double reentryProbability) {
        this.mu = mu;
        this.lambda = lambda;
        this.reentryProbability = reentryProbability;

        init();
    }

    private void init() {

        entryDistribution = new ExpDistribution(lambda);
        exitDistribution = new ExpDistribution(mu);

        Event firstEvent = new Event(entryDistribution.nextNumber(), entry);

        numberOfClients = 0;

        addEvent(firstEvent);
    }

    public void run() {
        while (!events.isEmpty()) {
            Event currentEvent = events.remove(0);

            switch (currentEvent.getType()) {
                case entry:
                    numberOfClients++;

                    double newEntryTime = currentEvent.getTime() + entryDistribution.nextNumber();
                    Event newEntryEvent = new Event(newEntryTime, entry);
                    addEvent(newEntryEvent);

                    if(numberOfClients == 1) {
                        double newExitTime = currentEvent.getTime() + exitDistribution.nextNumber();
                        Event newExitEvent = new Event(newExitTime, exit);
                        addEvent(newExitEvent);
                    }

                    break;
                case exit:

                    if(random.nextDouble() < reentryProbability) {
                        //TODO: do we need an entry event
                    }
                    else {
                        numberOfClients--;
                    }
                    
                    double newExitTime = currentEvent.getTime() + exitDistribution.nextNumber();
                    Event newExitEvent = new Event(newExitTime, exit);
                    addEvent(newExitEvent);

                    break;
            }
        }
    }

    private void addEvent(Event event) {
        events.add(event);
        Collections.sort(events);
    }
}
