/**
 * Created by vitortrindade on 05/08/15.
 */
public class Event {

    public enum EventType {
        entry,
        exit
    }

    public Event(double time, EventType type) {
        this.time = time;
        this.type = type;
    }


    private double time;
    private EventType type;

}
