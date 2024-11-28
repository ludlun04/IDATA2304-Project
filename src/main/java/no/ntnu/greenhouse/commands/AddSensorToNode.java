package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.SensorActuatorNode;

/**
 * Command for adding a sensor to a node
 */
public class AddSensorToNode extends SimulatorCommand {
  private int nodeId;
  private Sensor sensor;
  private int amount;

  /**
   * Constructor for AddSensorToNode
   * @param simulator the greenhouse simulator
   * @param nodeId the id of the node
   * @param sensor  the sensor to add
   * @param amount the number of sensors to add
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
