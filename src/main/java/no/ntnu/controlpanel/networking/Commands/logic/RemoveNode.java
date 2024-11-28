package no.ntnu.controlpanel.networking.Commands.logic;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;

/**
 * A {@link LogicCommand} that contains a node-id, and can remove a node by this
 * id from the {@link ControlPanelLogic} passed in through the constructor.
 */
public class RemoveNode extends LogicCommand {
    private int nodeId;

    /**
     * Constructs a {@link RemoveNode} object
     *
     * @param logic  the {@link ControlPanelLogic} we want to remove
     *               {@link SensorActuatorNodeInfo} from
     * @param nodeId by which we can attempt to remove a particular
     *               {@link SensorActuatorNodeInfo}
     */
    public RemoveNode(ControlPanelLogic logic, int nodeId) {
        super(logic);
        this.nodeId = nodeId;
    }

    @Override
    public void execute() {
        this.logic.onNodeRemoved(nodeId);
    }
}
