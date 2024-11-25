package no.ntnu.controlpanel;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;

public class ControlPanelCommunicationChannel extends CommunicationHandler
    implements CommunicationChannel {
  private final ControlPanelLogic logic;
  private SecretKey aesKey;

  public ControlPanelCommunicationChannel(ControlPanelLogic logic) throws IOException {
    super(new Socket("127.0.0.1", 8765));
    if (logic == null) {
      throw new IllegalArgumentException("logic cannot be null");
    }
    this.logic = logic;
  }

  public void sendInitialDataRequest() {
    this.sendEncryptedMessageAES("initialData");
  }

  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
    this.sendEncryptedMessageAES(String.format("set %d %d %b", nodeId, actuatorId, isOn));
  }

  /**
   * Sends a message to the server to add a sensor to a node
   *
   * @param nodeId     id of node to have a sensor added
   * @param sensorType type of sensor
   * @param min        min value the sensor can register
   * @param max        max value the sensor can register
   * @param current    current value the sensor reads
   * @param unit       unit the sensor reads in
   * @param amount     amount of sensors to add
   */
  public void addSensor(int nodeId, String sensorType, int min, int max, int current, String unit,
                        int amount) {
    this.sendEncryptedMessageAES(String.format("add sensor %d %s %d %d %d %s %d",
        nodeId, sensorType, min, max, current, unit, amount));
  }

  /**
   * Sends a message to the server to add an actuator to a node
   *
   * @param nodeId       id of node to have an actuator added
   * @param actuatorType type of actuator
   */
  public void addActuator(int nodeId, String actuatorType) {
    this.sendEncryptedMessageAES(String.format("add actuator %d %s", nodeId, actuatorType));
  }

  @Override
  public boolean open() {
    boolean connected = false;
    Logger.info("Connecting to socket");
    receivingAESKey();
    sendInitialDataRequest();

    new Thread(() -> {
      handleCommunication();
    }).start();

    connected = true;

    return connected;
  }

  /**
   * Receives the AES key from the server
   */
  private void receivingAESKey() {
    String message = super.getMessage();
    String[] args = message.split(" ");
    if (args[0].equals("aesKey")) {
      byte[] aesKeyBytes = Base64.getDecoder().decode(args[1]);
      this.aesKey = new SecretKeySpec(aesKeyBytes, "AES");
    }
  }

  @Override
  public void handleCommunication() {
    try {
      String message = decryptMessageAES(super.getMessage());

      while (message != null) {
        String[] args = message.split(" ");

        String command = args[0];
        int nodeId = Integer.parseInt(args[1]);

        switch (command) {
          case "add":
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
            break;
          case "remove":
            this.logic.onNodeRemoved(nodeId);
            break;
          case "updateSensorsInformation":
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
            break;
          case "updateActuatorInformation":
            int actuatorId = Integer.parseInt(args[2]);
            boolean state = Boolean.parseBoolean(args[3]);
            this.logic.onActuatorStateChanged(nodeId, actuatorId, state);
            break;
          default:
            Logger.info("Unknown command: " + command);
        }
        message = decryptMessageAES(super.getMessage());
      }
    } catch (Exception e) {
      Logger.error("Internal Error:");
      Logger.error(e.getMessage());
    }
  }

  @Override
  public void close() {
    //TODO: Implement close
  }

  /**
   * Encrypts a message using AES encryption
   *
   * @param message message to encrypt
   * @return encrypted message
   */
  public String encryptMessageAES(String message) {
    String result = null;
    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, this.aesKey);
      byte[] encryptedMessage = cipher.doFinal(message.getBytes());
      result = Base64.getEncoder().encodeToString(encryptedMessage);
    } catch (Exception e) {
      Logger.error("AES encryption: " + e.getMessage());
    }
    return result;
  }

  /**
   * Decrypts a message using AES encryption
   *
   * @param encryptedMessage message to decrypt
   * @return decrypted message
   */
  public String decryptMessageAES(String encryptedMessage) {
    String result = null;
    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, this.aesKey);
      byte[] decryptedMessage = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
      result = new String(decryptedMessage);
    } catch (Exception e) {
      Logger.error("AES decryption: " + e.getMessage());
    }
    return result;
  }

  /**
   * Sends an encrypted message to the server
   *
   * @param message message to send
   */
  public void sendEncryptedMessageAES(String message) {
    super.sendMessage(encryptMessageAES(message));
  }
}
