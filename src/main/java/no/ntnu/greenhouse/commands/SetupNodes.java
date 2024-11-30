package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.GreenhouseSimulator;

/**
 * Command for setting up the nodes
 */
public class SetupNodes extends SimulatorCommand {

  /**
   * Constructor for SetupNodes
   *
   * @param simulator the greenhouse simulator
   */
  public SetupNodes(GreenhouseSimulator simulator) {
    super(simulator);
  }

  @Override
  public void execute() {
    this.simulator.setupNodes();
  }
}
