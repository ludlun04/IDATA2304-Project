package no.ntnu.controlpanel.networking.Commands;

import no.ntnu.controlpanel.ControlPanelLogic;

public class RemoveNode extends Command {
    private int nodeId;

    public RemoveNode(ControlPanelLogic logic, int nodeId) {
        super(logic);
        this.nodeId = nodeId;
    }

    @Override
    public void execute() {
        this.logic.onNodeRemoved(nodeId);
    }
}
