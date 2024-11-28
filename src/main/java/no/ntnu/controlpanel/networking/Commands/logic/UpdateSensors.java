package no.ntnu.controlpanel.networking.Commands.logic;

import java.util.List;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.greenhouse.SensorReading;

/**
 * A {@link LogicCommand} that contain the necessary info and methods to update
 * current sensor readings for a particular {@link SensorActuatorNodeInfo} in
 * the {@link ControlPanelLogic} supplied by the constructor
 */
public class UpdateSensors extends LogicCommand {
    private List<SensorReading> readings;
    private int nodeId;

    /**
     * Constructs a {@link UpdateSensors} object
     *
     * @param logic    the {@link ControlPanelLogic} we want to update sensor
     *                 readings for
     * @param nodeId   id of the particular {@link SensorActuatorNodeInfo} we want
     *                 to update sensor readings for
     * @param readings the new sensor data
     */
    public UpdateSensors(ControlPanelLogic logic, int nodeId, List<SensorReading> readings) {
        super(logic);
        this.nodeId = nodeId;
        this.readings = readings;
    }

    @Override
    public void execute() {
        this.logic.onSensorData(nodeId, readings);
    }
}
