package no.ntnu.controlpanel.networking.commands;

import no.ntnu.controlpanel.ControlPanelLogic;

/**
 * Command for removing a node from the control panel.
 */
public class RemoveNode extends LogicCommand {
  private final int nodeId;

  /**
   * Create a new remove node command.
   *
   * @param logic  The control panel logic.
   * @param nodeId The id of the node to remove.
   */
  public RemoveNode(ControlPanelLogic logic, int nodeId) {
    super(logic);
    this.nodeId = nodeId;
  }

  /**
   * Execute the command.
   */
  @Override
  public void execute() {
    this.logic.onNodeRemoved(nodeId);
  }
}
