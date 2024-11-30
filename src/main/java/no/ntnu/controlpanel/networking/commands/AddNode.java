package no.ntnu.controlpanel.networking.commands;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;

/**
 * Command for adding a node to the control panel.
 */
public class AddNode extends LogicCommand {
  private final SensorActuatorNodeInfo sensorActuatorNodeInfo;

  /**
   * Create a new add node command.
   *
   * @param logic The control panel logic.
   * @param info  The information about the node to add.
   */
  public AddNode(ControlPanelLogic logic, SensorActuatorNodeInfo info) {
    super(logic);
    this.sensorActuatorNodeInfo = info;
  }

  /**
   * Execute the command.
   */
  @Override
  public void execute() {
    this.logic.onNodeAdded(this.sensorActuatorNodeInfo);
  }
}