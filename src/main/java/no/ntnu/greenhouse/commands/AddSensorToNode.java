package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.SensorActuatorNode;

public class AddSensorToNode extends SimulatorCommand {
  private int nodeId;
  private Sensor sensor;
  private int amount;

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
