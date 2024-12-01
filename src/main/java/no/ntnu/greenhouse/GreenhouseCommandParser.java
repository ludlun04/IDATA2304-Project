package no.ntnu.greenhouse;

import java.util.ArrayDeque;
import no.ntnu.controlpanel.networking.NoSuchCommand;
import no.ntnu.greenhouse.commands.AddActuatorToNode;
import no.ntnu.greenhouse.commands.AddSensorToNode;
import no.ntnu.greenhouse.commands.GetNodeValues;
import no.ntnu.greenhouse.commands.SetActuatorState;
import no.ntnu.greenhouse.commands.SetupNodes;
import no.ntnu.greenhouse.commands.StartDataTransfer;
import no.ntnu.utils.CommandParser;
import no.ntnu.utils.CommunicationHandler;
import no.ntnu.utils.commands.Command;

/**
 * Class for parsing commands from messages
 */
public class GreenhouseCommandParser extends CommandParser {
  private final GreenhouseSimulator simulator;

  /**
   * Constructor for GreenhouseCommandParser
   *
   * @param simulator the greenhouse simulator
   * @param handler   the communication handler
   */
  public GreenhouseCommandParser(GreenhouseSimulator simulator, CommunicationHandler handler) {
    super(handler);
    this.simulator = simulator;
  }

  @Override
  public Command parseSpecificCommand(String commandWord, ArrayDeque<String> args)
      throws IllegalArgumentException {
    Command command = null;
    switch (commandWord) {
      case "setupNodes" -> {
        command = new SetupNodes(simulator);
      }
      case "startDataTransfer" -> {
        command = new StartDataTransfer(simulator);
      }
      case "get" -> {
        int nodeId = Integer.parseInt(args.poll());
        command = new GetNodeValues(simulator, nodeId);
      }
      case "set" -> {
        int nodeId = Integer.parseInt(args.poll());
        int actuatorId = Integer.parseInt(args.poll());
        boolean state = Boolean.parseBoolean(args.poll());

        command = new SetActuatorState(this.simulator, nodeId, actuatorId, state);
      }
      case "add" -> {
        switch (args.poll()) {
          case "sensor" -> {
            int nodeId = Integer.parseInt(args.poll());
            Sensor sensor = new Sensor(args.poll(), Double.parseDouble(args.poll()),
                Double.parseDouble(args.poll()), Double.parseDouble(args.poll()), args.poll());
            int amount = Integer.parseInt(args.poll());
            command = new AddSensorToNode(simulator, nodeId, sensor, amount);
          }
          case "actuator" -> {
            int nodeId = Integer.parseInt(args.poll());
            Actuator actuator = new Actuator(Integer.parseInt(args.poll()), args.poll(), nodeId);
            command = new AddActuatorToNode(simulator, nodeId, actuator);
          }
        }
      }
      default -> throw new NoSuchCommand();
    }
    return command;
  }
}

