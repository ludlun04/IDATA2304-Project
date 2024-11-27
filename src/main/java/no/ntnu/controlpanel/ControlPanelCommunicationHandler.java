package no.ntnu.controlpanel;

import javax.crypto.SecretKey;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ControlPanelCommunicationHandler {

  private ControlPanelLogic logic;
  private CommunicationHandler handler;

  public ControlPanelCommunicationHandler(Socket socket, ControlPanelLogic logic)
      throws IOException {
    this.handler = new CommunicationHandler(socket);
    this.logic = logic;
  }

  /**
   * Sends a message to the client.
   *
   * @param message String to be sent to the client
   */
  public void sendMessage(String message) {
    this.handler.sendMessage(message);
  }

  /**
   * Sends a encrypted message to the client.
   *
   * @param encryptedMessage to be sent
   */
  public void sendEncryptedMessageAES(String encryptedMessage) {
    this.handler.sendEncryptedMessageAES(encryptedMessage);
  }

  /**
   * Waits for a message from the client
   *
   * @return message that has been sent from the client or null if the was no
   * message to get
   */
  public String getMessage() {
    return this.handler.getMessage();
  }

  /**
   * Waits for a encrypted message from the client
   *
   * @return encrypted message that has been sent from the client or null if the was no
   * message to get
   */
  public String getDecryptedMessageAES() {
    return this.handler.getDecryptedMessageAES();
  }

  /**
   * Sets the AES key
   *
   * @param key the AES key
   */
  public void setAESKey(SecretKey key) {
    this.handler.setAESKey(key);
  }

  /**
   * Closes the communication
   *
   * @throws IOException
   */
  public void close() {
    this.handler.close();
  }

  public void handleCommunication() throws IOException {
    String message = getDecryptedMessageAES();

    if (message == null) {
      throw new IOException("Communication interrupted");
    }

    String[] args = message.split(" ");

    String command = args[0];
    int nodeId = Integer.parseInt(args[1]);

    switch (command) {
      case "add" -> {

        SensorActuatorNodeInfo sensorActuatorNodeInfo =
            new SensorActuatorNodeInfo(nodeId);

        for (int i = 2; i < args.length; i += 2) {
          int actuatorId = Integer.parseInt(args[i]);
          String actuatorType = args[i + 1];

          Actuator actuator = new Actuator(actuatorId, actuatorType, nodeId);
          actuator.setListener(this.logic);
          sensorActuatorNodeInfo.addActuator(actuator);
        }

        this.logic.onNodeAdded(sensorActuatorNodeInfo);
      }
      case "remove" -> this.logic.onNodeRemoved(nodeId);
      case "updateSensorsInformation" -> {
        ArrayList<SensorReading> readings = new ArrayList<>();

        for (int i = 2; i < args.length; i += 3) {
          String sensorType = args[i];
          Double value = Double.parseDouble(args[i + 1]);
          String unit = args[i + 2];

          SensorReading sensorReading =
              new SensorReading(sensorType, value, unit);

          readings.add(sensorReading);
        }

        this.logic.onSensorData(nodeId, readings);
      }
      case "updateActuatorInformation" -> {
        int actuatorId = Integer.parseInt(args[2]);
        boolean state = Boolean.parseBoolean(args[3]);

        this.logic.onActuatorStateChanged(nodeId, actuatorId, state);
      }
      default -> Logger.info("Unknown command: " + command);
    }

  }
}

