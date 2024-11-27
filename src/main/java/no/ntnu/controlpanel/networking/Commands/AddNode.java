package no.ntnu.controlpanel.networking.Commands;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;

public class AddNode extends Command {
    private SensorActuatorNodeInfo sensorActuatorNodeInfo;

    public AddNode(ControlPanelLogic logic, SensorActuatorNodeInfo info) {
        super(logic);
        this.sensorActuatorNodeInfo = info;
    }

    @Override
    public void execute() {
        this.logic.onNodeAdded(this.sensorActuatorNodeInfo);
    }
}