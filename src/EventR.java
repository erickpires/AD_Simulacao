/**
 * Created by Vítor on 19/08/2015.
 *
 * R for Reentry
 */

public class EventR implements Comparable<EventR> {

    public EventType getType() {
        return type;
    }

    public double getTime() {
        return time;
    }

    public enum EventType {
        entry,
        exit,
        reentry
    }

    private double time;
    private EventType type;

    public EventR(double time, EventType type) {
        this.time = time;
        this.type = type;
    }

    @Override
    public int compareTo(EventR event) {
        return this.time < event.time ? -1 : this.time == event.time ? 0 : 1;
    }
}

