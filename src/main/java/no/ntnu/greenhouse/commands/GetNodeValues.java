package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.GreenhouseSimulator;

/**
 * Command for getting the values of a node
 */
public class GetNodeValues extends SimulatorCommand {
  private final int nodeId;

  /**
   * Constructor for GetNodeValues
   *
   * @param simulator the greenhouse simulator
   * @param nodeId    the id of the node
   */
  public GetNodeValues(GreenhouseSimulator simulator, int nodeId) {
    super(simulator);
    this.nodeId = nodeId;
  }

  @Override
  public void execute() {
    this.simulator.getNodeValues(this.simulator.getNode(this.nodeId));
  }
}
