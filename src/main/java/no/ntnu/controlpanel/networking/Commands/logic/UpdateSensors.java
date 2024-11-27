package no.ntnu.controlpanel.networking.Commands.logic;

import java.util.List;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.greenhouse.SensorReading;

public class UpdateSensors extends LogicCommand {
    private List<SensorReading> readings;
    private int nodeId;

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
