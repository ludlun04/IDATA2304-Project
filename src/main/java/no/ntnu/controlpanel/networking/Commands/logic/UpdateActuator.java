package no.ntnu.controlpanel.networking.Commands.logic;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;

/**
 * A {@link LogicCommand} that contains the information and methods necessary to
 * update the state of a particular {@link Actuator} within a
 * {@link SensorActuatorNodeInfo}
 */
public class UpdateActuator extends LogicCommand {
    private int actuatorId;
    private boolean state;
    private int nodeId;

    /**
     * Constructs a {@link UpdateActuator} object
     *
     * @param logic      the {@link ControlPanelLogic} that contains all the
     *                   {@link SensorActuatorNodeInfo} objects
     * @param nodeId     id of the particular {@link SensorActuatorNodeInfo} the
     *                   actuator is contained within
     * @param actuatorId id of the particular {@link Actuator} contained within the
     *                   target {@link SensorActuatorNodeInfo} object
     * @param state      the new state of the actuator
     */
    public UpdateActuator(ControlPanelLogic logic, int nodeId, int actuatorId, boolean state) {
        super(logic);
        this.actuatorId = actuatorId;
        this.nodeId = nodeId;
        this.state = state;
    }

    @Override
    public void execute() {
        this.logic.onActuatorStateChanged(this.nodeId, this.actuatorId, this.state);
    }
}
