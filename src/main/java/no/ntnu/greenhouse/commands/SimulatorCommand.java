package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.utils.commands.Command;

/**
 * Abstract class for simulator commands
 */
public abstract class SimulatorCommand extends Command {
  protected GreenhouseSimulator simulator;

  /**
   * Constructor for SimulatorCommand
   * @param simulator the greenhouse simulator
   */
  protected SimulatorCommand(GreenhouseSimulator simulator) {
    this.simulator = simulator;
  }
}
