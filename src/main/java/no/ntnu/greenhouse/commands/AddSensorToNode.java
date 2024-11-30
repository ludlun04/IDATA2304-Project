package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.Sensor;

/**
 * Command for adding a sensor to a node
 */
public class AddSensorToNode extends SimulatorCommand {
  private final int nodeId;
  private final Sensor sensor;
  private final int amount;

  /**
   * Constructor for AddSensorToNode
   *
   * @param simulator the greenhouse simulator
   * @param nodeId    the id of the node
   * @param sensor    the sensor to add
   * @param amount    the number of sensors to add
   */
  public AddSensorToNode(GreenhouseSimulator simulator, int nodeId, Sensor sensor,
                         int amount) {
    super(simulator);
    this.sensor = sensor;
    this.nodeId = nodeId;
    this.amount = amount;
  }

  @Override
  public void execute() {
    this.simulator.addSensorToNode(this.nodeId, this.sensor, this.amount);
  }
}
