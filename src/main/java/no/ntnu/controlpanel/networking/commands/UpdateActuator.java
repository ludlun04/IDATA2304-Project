package no.ntnu.controlpanel.networking.commands;

import no.ntnu.controlpanel.ControlPanelLogic;

/**
 * Command for updating the state of an actuator.
 */
public class UpdateActuator extends LogicCommand {
  private int actuatorId;
  private boolean state;
  private int nodeId;

  /**
   * Create a new update actuator command.
   *
   * @param logic The control panel logic.
   * @param nodeId The id of the node that the actuator belongs to.
   * @param actuatorId The id of the actuator to update.
   * @param state The new state of the actuator.
   */
  public UpdateActuator(ControlPanelLogic logic, int nodeId, int actuatorId, boolean state) {
    super(logic);
    this.actuatorId = actuatorId;
    this.nodeId = nodeId;
    this.state = state;
  }

  /**
   * Execute the command.
   */
  @Override
  public void execute() {
    this.logic.onActuatorStateChanged(this.nodeId, this.actuatorId, this.state);
  }
}
