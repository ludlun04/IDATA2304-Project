package no.ntnu.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKey;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.SensorActuatorNode;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CipherKeyHandler;
import no.ntnu.utils.CommunicationHandler;

public class ControlPanelClientHandler {
  private GreenhouseSimulator simulator;
  private final CipherKeyHandler cipherKeyGenerator;
  private final SecretKey aesKey;
  private CommunicationHandler handler;

  public ControlPanelClientHandler(Socket socket, GreenhouseSimulator simulator)
      throws IOException {
    this.handler = new CommunicationHandler(socket);
    this.simulator = simulator;
    this.cipherKeyGenerator = new CipherKeyHandler();
    this.aesKey = cipherKeyGenerator.getAESKey();
  }

  /**
   * Initializes the communication with the client by sending public key,
   * Getting the public key from the client and sending the AES key.
   */
  public void createCipherCommunication() {
    sendAESKey();
  }

  public void handleCommunication() {
    boolean running = true;

    while (running) {
      String message = this.handler.getDecryptedMessageAES();

      if (message == null) {
        running = false;
      } else {
        handleMessage(message);
      }
    }
  }


  /**
   * Parses message and performs action based on parsed value.
   *
   * @param message
   */
  private void handleMessage(String message) {
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
          switch (args[1]) {
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
      Logger.error("Handeling messages failed because");
      Logger.error(e.getMessage());
    }
  }

  /**
   * Add an actuator to a node.
   *
   * @param args The arguments for values.
   */
  private void addActuatorToNode(String[] args) {
    int nodeId = Integer.parseInt(args[2]);
    Actuator actuator = new Actuator(args[3], nodeId);
    this.simulator.getNode(nodeId).addActuator(actuator);
  }

  /**
   * Add a sensor to a node.
   *
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
   *
   * @param args The arguments for values.
   */
  private void setActuatorValue(String[] args) {
    this.simulator.getNode(Integer.parseInt(args[1])).getActuators()
        .get(Integer.parseInt(args[2])).set(Boolean.parseBoolean(args[3]));
  }

  /**
   * Get the node values.
   *
   * @param args The arguments for values.
   */
  private void getNodeValues(String[] args) {
    for (Sensor sensor : this.simulator.getNode(Integer.parseInt(args[1])).getSensors()) {
      this.handler.sendEncryptedMessageAES("" + sensor.getReading().getValue());
    }
  }

  /**
   * Send initial data to the client
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
   *
   * @param node The node to send information about
   */
  private void sendNodeInformation(SensorActuatorNode node) {
    String response = String.format("add %d", node.getId());

    for (Actuator actuator : node.getActuators()) {
      response = String.format("%s %d %s", response, actuator.getId(), actuator.getType());
    }

    this.handler.sendEncryptedMessageAES(response);
  }

  /**
   * Initialize actuator listeners
   *
   * @param node The node to initialize listeners for
   */
  private void initializeActuatorListeners(SensorActuatorNode node) {
    node.addActuatorListener((int nodeID, Actuator actuator) -> {
      String response = String.format(
          "updateActuatorInformation %d %d %b", nodeID,
          actuator.getId(),
          actuator.isOn());
      this.handler.sendEncryptedMessageAES(response);
    });
  }

  /**
   * Initialize sensor listeners
   *
   * @param node The node to initialize listeners for
   */
  private void initializeSensorListeners(SensorActuatorNode node) {
    node.addSensorListener((List<Sensor> sensors) -> {
      String response = String.format("updateSensorsInformation %d", node.getId());
      for (Sensor sensor : sensors) {
        response = String.format("%s %s %f %s", response, sensor.getType(),
            sensor.getReading().getValue(), sensor.getReading().getUnit());
      }
      this.handler.sendEncryptedMessageAES(response);
    });
  }

  /**
   * Sends the AES key to the client
   */
  private void sendAESKey() {
    String encodedKey = Base64.getEncoder().encodeToString(this.aesKey.getEncoded());
    this.handler.sendMessage(encodedKey);
    Logger.info("Sent AES key to client " + encodedKey);
    Logger.info("AES key: " + Arrays.toString(this.aesKey.getEncoded()));
  }

  public void closeConnection() {
    this.handler.close();
  }
}
