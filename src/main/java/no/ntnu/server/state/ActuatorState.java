package no.ntnu.server.state;

public class ActuatorState {
    private String type;
    private boolean on;
    private int id;

    public ActuatorState(int id, String type, boolean on) {
        this.id = id;
        this.type = type;
        this.on = on;
    }

    public int getId() {
        return this.id;
    }
}
