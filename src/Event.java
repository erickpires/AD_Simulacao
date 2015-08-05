/**
 * Created by vitortrindade on 05/08/15.
 */
public class Event implements Comparable<Event> {

    public EventType getType() {
        return type;
    }

    public double getTime() {
        return time;
    }

    public enum EventType {
        entry,
        exit
    }

    private double time;
    private EventType type;

    public Event(double time, EventType type) {
        this.time = time;
        this.type = type;
    }

    @Override
    public int compareTo(Event event) {
        return this.time < event.time ? -1 : this.time == event.time ? 0 : 1;
    }
}
