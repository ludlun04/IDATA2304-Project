package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.GreenhouseSimulator;

/**
 * Command for starting the data transfer
 */
public class StartDataTransfer extends SimulatorCommand {

  /**
   * Constructor for StartDataTransfer
   * @param simulator the greenhouse simulator
   */
  public StartDataTransfer(GreenhouseSimulator simulator) {
    super(simulator);
  }

  @Override
  public void execute() {
    this.simulator.startDataTransfer();
  }


}
