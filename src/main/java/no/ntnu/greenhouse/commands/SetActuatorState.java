package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.GreenhouseSimulator;

/**
 * Command for setting the state of an actuator
 */
public class SetActuatorState extends SimulatorCommand {
  private int nodeId;
  private int actuatorId;
  private boolean state;

  /**
   * Constructor for SetActuatorState
   * @param simulator the greenhouse simulator
   * @param nodeId the id of the node
   * @param actuatorId the id of the actuator
   * @param state the state to set
   */
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
