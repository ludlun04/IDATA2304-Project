package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.SensorActuatorNode;

public class AddActuatorToNode extends SimulatorCommand {
  private Actuator actuator;
  private int nodeId;

  public AddActuatorToNode(GreenhouseSimulator simulator, int nodeId, Actuator actuator) {
    super(simulator);
    this.actuator = actuator;
    this.nodeId = nodeId;
  }

  @Override
  public void execute() {
    this.simulator.addActuatorToNode(this.nodeId, this.actuator);
  }
}
