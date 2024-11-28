package no.ntnu.controlpanel.networking.commands;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;

/**
 * Command for adding a node to the control panel.
 */
public class AddNode extends LogicCommand {
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