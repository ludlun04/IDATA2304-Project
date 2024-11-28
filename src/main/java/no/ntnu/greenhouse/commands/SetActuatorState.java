package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.GreenhouseSimulator;

public class SetActuatorState extends SimulatorCommand {
  private int nodeId;
  private int actuatorId;
  private boolean state;

  public SetActuatorState(GreenhouseSimulator simulator, int nodeId, int actuatorId,
                          boolean state) {
    super(simulator);
    this.nodeId = nodeId;
    this.actuatorId = actuatorId;
    this.state = state;
  }

  @Override
  public void execute() {
    this.simulator.setActuatorState(this.nodeId, this.actuatorId, this.state);
  }
}
