package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.SensorActuatorNode;

public class GetNodeValues extends SimulatorCommand {
  private int nodeId;

  public GetNodeValues(GreenhouseSimulator simulator, int nodeId) {
    super(simulator);
    this.nodeId = nodeId;
  }

  @Override
  public void execute() {
    this.simulator.getNodeValues(this.simulator.getNode(this.nodeId));
  }
}
