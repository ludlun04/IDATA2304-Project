package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.utils.commands.Command;

public abstract class SimulatorCommand extends Command {
  protected GreenhouseSimulator simulator;

  public SimulatorCommand(GreenhouseSimulator simulator) {
    this.simulator = simulator;
  }
}
