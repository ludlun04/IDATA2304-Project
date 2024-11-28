package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.GreenhouseSimulator;

public class SetupNodes extends SimulatorCommand {

  public SetupNodes(GreenhouseSimulator simulator) {
    super(simulator);
  }

  @Override
  public void execute() {
    this.simulator.setupNodes();
  }
}
