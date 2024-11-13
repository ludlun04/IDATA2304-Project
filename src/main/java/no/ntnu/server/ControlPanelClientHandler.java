package no.ntnu.server;

import java.io.IOException;
import java.net.Socket;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.SensorActuatorNode;
import java.util.List;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;

public class ControlPanelClientHandler extends CommunicationHandler {
  private GreenhouseSimulator simulator;

  public ControlPanelClientHandler(Socket socket, GreenhouseSimulator simulator) throws
      IOException {
    super(socket);
    this.simulator = simulator;
  }

  @Override
  public void handleCommunication() {
    String message = super.getMessage();
    if (message != null) {
      System.out.println("ControlPanelClientHandler: " + message);
    }
    while (message != null) {
      Logger.info("Client message:" + message);
      String args[] = message.split(" ");

      try {
        switch (args[0]) {
          case "initialData":
            sendInitialData();
            break;
          case "get":
            getNodeValues(args);
            break;
          case "set":
            setActuatorValue(args);
            break;
          case "add":
            switch (args[1]){
              case "sensor":
                addSensorToNode(args);
                break;
              case "actuator":
                addActuatorToNode(args);
                break;
            }
          case "close":
            closeConnection();
            break;
          default:
            Logger.error("Unknown command: " + args[0]);
            break;
        }
      } catch (NumberFormatException e) {
        Logger.error(e.getMessage());
      }

      message = super.getMessage();
    }
  }

  /**
   * Add an actuator to a node.
   * @param args The arguments for values.
   */
  private void addActuatorToNode(String[] args) {
    int nodeId = Integer.parseInt(args[2]);
    Actuator actuator = new Actuator(args[3], nodeId);
    this.simulator.getNode(nodeId).addActuator(actuator);
  }

  /**
   * Add a sensor to a node.
   * @param args The arguments for values.
   */
  private void addSensorToNode(String[] args) {
    Sensor sensor = new Sensor(args[3], Integer.parseInt(args[4]),
        Integer.parseInt(args[5]), Integer.parseInt(args[6]), args[7]);
    this.simulator.getNode(Integer.parseInt(args[2]))
        .addSensors(sensor, Integer.parseInt(args[8]));
  }

  /**
   * Set the actuator value.
   * @param args The arguments for values.
   */
  private void setActuatorValue(String[] args) {
    this.simulator.getNode(Integer.parseInt(args[1])).getActuators()
        .get(Integer.parseInt(args[2])).set(Boolean.parseBoolean(args[3]));
  }

  /**
   * Get the node values.
   * @param args The arguments for values.
   */
  private void getNodeValues(String[] args) {
    for (Sensor sensor : this.simulator.getNode(Integer.parseInt(args[1])).getSensors()) {
      super.sendMessage("" + sensor.getReading().getValue());
    }
  }

  /**
   * Send initial data to client
   */
    public void sendInitialData() {
      for (SensorActuatorNode node : this.simulator.getNodes()) {
        sendNodeInformation(node);
        initializeSensorListeners(node);
        initializeActuatorListeners(node);
      }
    }

  /**
   * Send node information to client
   * @param node The node to send information about
   */
  private void sendNodeInformation(SensorActuatorNode node) {
    String response = String.format("add %d", node.getId());

    for (Actuator actuator : node.getActuators()) {
      response = String.format("%s %d %s", response, actuator.getId(), actuator.getType());
    }

    super.sendMessage(response);
  }

  /**
   * Initialize actuator listeners
   * @param node The node to initialize listeners for
   */
  private void initializeActuatorListeners(SensorActuatorNode node) {
    node.addActuatorListener((int nodeID, Actuator actuator) -> {
      String response = String.format(
          "updateActuatorInformation %d %d %b", nodeID,
          actuator.getId(),
          actuator.isOn());
      super.sendMessage(response);
    });
  }

  /**
   * Initialize sensor listeners
   * @param node The node to initialize listeners for
   */
  private void initializeSensorListeners(SensorActuatorNode node) {
    node.addSensorListener((List<Sensor> sensors) -> {
      String response = String.format("updateSensorsInformation %d", node.getId());
        for (Sensor sensor : sensors) {
            response = String.format("%s %s %f %s",response, sensor.getType(),
                sensor.getReading().getValue(), sensor.getReading().getUnit());
        }
        super.sendMessage(response);
    });
  }

  private void closeConnection() {
    super.close();
  }

}
