/**
 * Created by Vítor on 20/08/2015.
 *
 * For the second simulator, with reentry
 */
public class EventGR implements Comparable<EventGR> {



    public enum EventType {

        bikeExit,
        treadmillExit,
        bikeEntry,
        treadmillEntry,
        treadmillReentry
    }

    public enum MachineType {

        bike,
        treadmill
    }

    public double getTime() {
        return time;
    }

    public EventType getType() {
        return type;
    }

    public MachineType getMachine() {
        return machine;
    }

    private double time;
    private EventType type;



    private MachineType machine;

    public EventGR(double time, EventType type) {
        this.time = time;
        this.type = type;

        initMachine();
    }

    public void initMachine(){
        switch (type) {

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

            case treadmillReentry:

                machine = MachineType.treadmill;

                break;


        }

    }
    @Override
    public int compareTo(EventGR event) {
        return this.time < event.time ? -1 : this.time == event.time ? 0 : 1;
    }
}














