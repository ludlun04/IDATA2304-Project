package no.ntnu.controlpanel.networking.commands;

import no.ntnu.controlpanel.ControlPanelLogic;

/**
 * Command for updating the state of an actuator.
 */
public class UpdateActuator extends LogicCommand {
  private int actuatorId;
  private boolean state;
  private int nodeId;

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
