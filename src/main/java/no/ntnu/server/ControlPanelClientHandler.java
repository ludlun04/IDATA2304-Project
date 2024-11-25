package no.ntnu.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.SensorActuatorNode;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CipherKeyGenerator;
import no.ntnu.utils.CommunicationHandler;

public class ControlPanelClientHandler extends CommunicationHandler {
  private GreenhouseSimulator simulator;
  private final CipherKeyGenerator cipherKeyGenerator;
  private final SecretKey aesKey;

  public ControlPanelClientHandler(Socket socket, GreenhouseSimulator simulator) throws
      IOException {
    super(socket);
    this.simulator = simulator;
    this.cipherKeyGenerator = new CipherKeyGenerator();
    this.aesKey = cipherKeyGenerator.getAESKey();
  }

  /**
   * Initializes the communication with the client by sending public key,
   * Getting the public key from the client and sending the AES key.
   */
  public void createCipherCommunication() {
    sendAESKey();
  }

  @Override
  public void handleCommunication() {
    String message = decryptMessageAES(super.getMessage());
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
        Logger.error(e.getMessage());
      }

      message = decryptMessageAES(super.getMessage());
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
      this.sendEncryptedMessageAES("" + sensor.getReading().getValue());
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

    this.sendEncryptedMessageAES(response);
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
      this.sendEncryptedMessageAES(response);
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
      this.sendEncryptedMessageAES(response);
    });
  }

  /**
   * Close the connection
   */
  private void closeConnection() {
    super.close();
  }


  /**
   * Send the AES key to the client
   */
  public void sendAESKey() {
    try {
      String encodedKey = Base64.getEncoder().encodeToString(this.aesKey.getEncoded());
      super.sendMessage("aesKey " + encodedKey);
      Logger.info("AES key sent" + this.aesKey.getEncoded());
    } catch (Exception e) {
      Logger.error(e.getMessage());
    }
  }

  /**
   * Encrypt a message with AES
   *
   * @param message The message to encrypt
   * @return The encrypted message
   */
  private String encryptMessageAES(String message) {
    String result = null;
    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, this.aesKey);
      byte[] encryptedMessage = cipher.doFinal(message.getBytes());
      result = Base64.getEncoder().encodeToString(encryptedMessage);
    } catch (Exception e) {
      Logger.error(e.getMessage());
    }
    return result;
  }

  /**
   * Decrypt a message with AES
   *
   * @param encryptedMessage The message to decrypt
   * @return The decrypted message
   */
  private String decryptMessageAES(String encryptedMessage) {
    String result = null;
    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, this.aesKey);
      byte[] decodedMessage = Base64.getDecoder().decode(encryptedMessage);
      byte[] decryptedMessage = cipher.doFinal(decodedMessage);
      result = new String(decryptedMessage);
    } catch (Exception e) {
      Logger.error(e.getMessage());
    }
    return result;
  }

  public void sendEncryptedMessageAES(String message) {
    super.sendMessage(encryptMessageAES(message));
  }
}