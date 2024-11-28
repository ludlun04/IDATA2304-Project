package no.ntnu.greenhouse;

import java.util.ArrayDeque;
import java.util.List;
import no.ntnu.controlpanel.networking.NoSuchCommand;
import no.ntnu.greenhouse.commands.AddActuatorToNode;
import no.ntnu.greenhouse.commands.AddSensorToNode;
import no.ntnu.greenhouse.commands.GetNodeValues;
import no.ntnu.greenhouse.commands.SetActuatorState;
import no.ntnu.greenhouse.commands.SetupNodes;
import no.ntnu.greenhouse.commands.StartDataTransfer;
import no.ntnu.utils.CommunicationHandler;
import no.ntnu.utils.commands.Command;
import no.ntnu.utils.commands.EnableEncryption;

public class CommandParser {
  private GreenhouseSimulator simulator;
  private CommunicationHandler handler;

  public CommandParser(GreenhouseSimulator simulator, CommunicationHandler handler) {
    this.simulator = simulator;
    this.handler = handler;
  }


  /**
   * Takes in a message and parses out a command based on the contents of the message
   *
   * @param message to be parsed
   * @return returns the coresponding command
   * @throws NoSuchCommand    exception if command isn't found or another exception
   * @throws RuntimeException if something else goes wrong during parsing of the message
   */
  public Command parse(String message) throws IllegalArgumentException {
    Command command = null;

    List<String> strings = List.of(message.split(" "));
    ArrayDeque<String> args = new ArrayDeque<>(strings);

    String commandWord = args.poll();

      switch (commandWord) {
        case "Encrypt" -> {
          String keyEncoded = String.valueOf(args.poll());
          command = new EnableEncryption(handler, keyEncoded);
        }
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

