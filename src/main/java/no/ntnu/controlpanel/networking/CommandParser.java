package no.ntnu.controlpanel.networking;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.controlpanel.networking.commands.AddNode;
import no.ntnu.controlpanel.networking.commands.RemoveNode;
import no.ntnu.controlpanel.networking.commands.UpdateActuator;
import no.ntnu.controlpanel.networking.commands.UpdateSensors;
import no.ntnu.utils.commands.Command;

import no.ntnu.utils.commands.EnableEncryption;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.utils.CommunicationHandler;

/**
 * Class for parsing commands from messages
 */
public class CommandParser {
  private ControlPanelLogic logic;
  private CommunicationHandler handler;

  public CommandParser(ControlPanelLogic logic, CommunicationHandler handler) {
    this.logic = logic;
    this.handler = handler;
  }

  /**
   * Takes in a message and parses out a command based on the contents of the message
   *
   * @param message to be parsed
   * @return returns the coresponding command
   * @throws NoSuchCommand    exception if command isn't found or other exception
   * @throws RuntimeException if something else goes wrong during parsing of the message
   */
  public Command parse(String message) throws IllegalArgumentException {
    Command command = null;

    List<String> strings = List.of(message.split(" "));
    ArrayDeque<String> args = new ArrayDeque<>(strings);

    String commandword = args.poll();

    if (commandword.equals("Encrypt")) {
      String keyEncoded = String.valueOf(args.poll());
      command = new EnableEncryption(handler, keyEncoded);
    } else {
      int nodeId = Integer.parseInt(args.poll());

      switch (commandword) {
        case "add" -> {
          SensorActuatorNodeInfo sensorActuatorNodeInfo = parseSensorActuatorNodeInfo(nodeId, args);
          command = new AddNode(logic, sensorActuatorNodeInfo);
        }
        case "remove" -> command = new RemoveNode(logic, nodeId);
        case "updateSensorsInformation" -> {
          ArrayList<SensorReading> readings = parseReadings(args);
          command = new UpdateSensors(logic, nodeId, readings);
        }
        case "updateActuatorInformation" -> {
          int actuatorId = Integer.parseInt(args.poll());
          boolean state = Boolean.parseBoolean(args.poll());
          command = new UpdateActuator(logic, nodeId, actuatorId, state);
        }
        default -> throw new NoSuchCommand();
      }
    }


    return command;
  }

  /**
   * Takes in a node id and a list of arguments that represent the actuators in an actuator node
   *
   * @param nodeId the id of the node to make SensorActuatorNodeInfo for
   * @param args   a queue of information for the actuators in the node
   * @return SensorActuatorNodeInfo for the node
   */
  public SensorActuatorNodeInfo parseSensorActuatorNodeInfo(int nodeId, ArrayDeque<String> args) {
    SensorActuatorNodeInfo info = new SensorActuatorNodeInfo(nodeId);

    while (!args.isEmpty()) {
      int actuatorId = Integer.parseInt(args.poll());
      String actuatorType = args.poll();
      boolean actuatorState = Boolean.parseBoolean(args.poll());

      Actuator actuator = new Actuator(actuatorId, actuatorType, nodeId);
      actuator.set(actuatorState);
      actuator.setListener(this.logic);
      info.addActuator(actuator);
    }

    return info;
  }

  /**
   * Takes in a queue of arguments that will be parsed in to a list of sensor readings
   *
   * @param args queue
   * @return ArrayList<SensorReading> list of parsed sensor readings
   */
  private ArrayList<SensorReading> parseReadings(ArrayDeque<String> args) {
    ArrayList<SensorReading> readings = new ArrayList<>();

    while (!args.isEmpty()) {
      String sensorType = args.poll();
      Double value = Double.parseDouble(args.poll());
      String unit = args.poll();

      SensorReading sensorReading = new SensorReading(sensorType, value, unit);

      readings.add(sensorReading);
    }

    return readings;
  }
}
