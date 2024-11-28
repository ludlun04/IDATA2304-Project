package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.SensorActuatorNode;

/**
 * Command for adding an actuator to a node
 */
public class AddActuatorToNode extends SimulatorCommand {
  private Actuator actuator;
  private int nodeId;

  /**
   * Constructor for AddActuatorToNode
   * @param simulator the greenhouse simulator
   * @param nodeId the id of the node
   * @param actuator the actuator to add
   */
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
