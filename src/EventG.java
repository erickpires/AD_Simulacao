/**
 * Created by Vítor on 18/08/2015.
 *
 * Events for the second simulator
 */
public class EventG implements Comparable<EventG> {

    public EventType getType() {
        return type;
    }

    public double getTime() {
        return time;
    }

    public enum EventType {
        bikeExit,
        treadmillExit,
        bikeEntry,
        treadmillEntry
    }

    private double time;
    private EventType type;

    public EventG(double time, EventType type) {
        this.time = time;
        this.type = type;
    }

    @Override
    public int compareTo(EventG event) {
        return this.time < event.time ? -1 : this.time == event.time ? 0 : 1;
    }
}