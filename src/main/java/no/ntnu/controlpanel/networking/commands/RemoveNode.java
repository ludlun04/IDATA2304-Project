package no.ntnu.controlpanel.networking.commands;

import no.ntnu.controlpanel.ControlPanelLogic;

/**
 * Command for removing a node from the control panel.
 */
public class RemoveNode extends LogicCommand {
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
