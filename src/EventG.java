/**
 * Created by Vítor on 18/08/2015.
 *
 * Events for the second simulator
 */
public class EventG implements Comparable<EventG> {

    public EventType getType() { return type;  }

    public double getTime() {
        return time;
    }

    public MachineType getMachine() { return machine; }

    public enum EventType {
        bikeExit,
        treadmillExit,
        bikeEntry,
        treadmillEntry
    }

    public enum MachineType {

        bike,
        treadmill
    }

    private double time;
    private EventType type;



    private MachineType machine;

    public EventG(double time, EventType type) {
        this.time = time;
        this.type = type;

        initMachine();
    }

    public void initMachine(){
        switch (this.type) {

            case bikeExit:

                machine = MachineType.bike;

                break;

            case bikeEntry:

                machine = MachineType.bike;

                break;

            case treadmillExit:

                machine = MachineType.treadmill;

                break;

            case treadmillEntry:

                machine = MachineType.treadmill;

                break;


        }

    }
    @Override
    public int compareTo(EventG event) {
        return this.time < event.time ? -1 : this.time == event.time ? 0 : 1;
    }
}