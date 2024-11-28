package no.ntnu.greenhouse.commands;

import no.ntnu.greenhouse.GreenhouseSimulator;

public class StartDataTransfer extends SimulatorCommand {

  public StartDataTransfer(GreenhouseSimulator simulator) {
    super(simulator);
  }

  @Override
  public void execute() {
    this.simulator.startDataTransfer();
  }


}
