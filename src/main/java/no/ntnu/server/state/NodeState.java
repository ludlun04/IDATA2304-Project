package no.ntnu.server.state;

import java.util.ArrayList;
import java.util.HashMap;

public class NodeState {
    private ArrayList<SensorState> sensors;
    private HashMap<Integer, ActuatorState> actuators;
    private String Id;

    public NodeState() {
        this.actuators = new HashMap<>();
    }

    public void addActuator(ActuatorState actuatorState) {
        if (actuatorState == null) {
            throw new IllegalArgumentException("State cannot be null");
        }

        actuators.put(actuatorState.getId(), actuatorState);
    }

    public void removeActuator(int actuatorId) {
        if (this.actuators.containsKey(actuatorId)) {
            this.actuators.remove(actuatorId);
        }
    }

    public void addSensorState(SensorState sensorState) {

    }

    public void removeSensorState() {

    }

    
}
