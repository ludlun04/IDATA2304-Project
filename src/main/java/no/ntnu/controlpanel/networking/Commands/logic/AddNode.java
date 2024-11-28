package no.ntnu.controlpanel.networking.Commands.logic;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;

/**
 * A {@link LogicCommand} that contains one and can this
 * {@link SensorActuatorNodeInfo} to the {@link ControlPanelLogic} contained in
 * the super-class.
 */
public class AddNode extends LogicCommand {
    private SensorActuatorNodeInfo sensorActuatorNodeInfo;

    /**
     * Constructs a {@link AddNode} object
     *
     * @param logic
     * @param info
     */
    public AddNode(ControlPanelLogic logic, SensorActuatorNodeInfo info) {
        super(logic);
        this.sensorActuatorNodeInfo = info;
    }

    @Override
    public void execute() {
        this.logic.onNodeAdded(this.sensorActuatorNodeInfo);
    }
}
