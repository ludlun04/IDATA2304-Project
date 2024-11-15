package no.ntnu.controlpanel;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CipherKeyGenerator;
import no.ntnu.utils.CommunicationHandler;

public class ControlPanelCommunicationChannel extends CommunicationHandler
    implements CommunicationChannel {
  private final ControlPanelLogic logic;
  private final CipherKeyGenerator cipherKeyGenerator;
  private final PublicKey publicKey;
  private final PrivateKey privateKey;
  private SecretKey aesKey;
  private List<byte[]> publicKeyPartsBuffer = new ArrayList<>();

  public ControlPanelCommunicationChannel(ControlPanelLogic logic) throws IOException {
    super(new Socket("127.0.0.1", 8765));
    if (logic == null) {
      throw new IllegalArgumentException("logic cannot be null");
    }
    this.logic = logic;
    this.cipherKeyGenerator = new CipherKeyGenerator();
    this.publicKey = cipherKeyGenerator.getPublicKey();
    this.privateKey = cipherKeyGenerator.getPrivateKey();
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

  public void sendPublicKeyInParts(PublicKey publicKey) {
    try {
      byte[] publicKeyBytes = publicKey.getEncoded();
      int partSize = 200;
      for (int i = 0; i < publicKeyBytes.length; i += partSize) {
        int end = Math.min(publicKeyBytes.length, i + partSize);
        byte[] part = Arrays.copyOfRange(publicKeyBytes, i, end);
        String encodedPart = Base64.getEncoder().encodeToString(part);
        super.sendMessage("publicKeyPart " + encodedPart);
      }
      super.sendMessage("publicKeyEnd");
    } catch (Exception e) {
      Logger.error("Error sending public key in parts: " + e.getMessage());
    }
  }

  public PublicKey handlePublicKeyParts(String[] args) {
    PublicKey publicKey = null;
    try {
      if (args[0].equals("publicKeyPart")) {
        byte[] part = Base64.getDecoder().decode(args[1]);
        publicKeyPartsBuffer.add(part);
      } else if (args[0].equals("publicKeyEnd")) {
        int totalLength = publicKeyPartsBuffer.stream().mapToInt(part -> part.length).sum();
        byte[] publicKeyBytes = new byte[totalLength];
        int currentIndex = 0;
        for (byte[] part : publicKeyPartsBuffer) {
          System.arraycopy(part, 0, publicKeyBytes, currentIndex, part.length);
          currentIndex += part.length;
        }
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        publicKey = keyFactory.generatePublic(keySpec);
        publicKeyPartsBuffer.clear();
      }
    } catch (Exception e) {
      Logger.error("Error handling public key parts: " + e.getMessage());
    }
    return publicKey;
  }

  @Override
  public boolean open() {
    boolean connected = false;
    Logger.info("Connecting to socket");
    publicKeyExchange();
    receivingAESKey();
    sendInitialDataRequest();

    new Thread(() -> {
      handleCommunication();
    }).start();

    connected = true;

    return connected;
  }

  private void receivingAESKey() {
    String message = decryptMessageRSA(super.getMessage());
    String[] args = message.split(" ");
    if (args[0].equals("aesKey")) {
      byte[] aesKeyBytes = Base64.getDecoder().decode(args[1]);
      this.aesKey = new SecretKeySpec(aesKeyBytes, "AES");
    }
  }

  private void publicKeyExchange() {
    String message = super.getMessage();
    String[] args = message.split(" ");
    sendPublicKeyInParts(handlePublicKeyParts(args));
  }

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

  public String encryptMessageRSA(String message, PublicKey publicKey) {
    String result = null;
    try {
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      byte[] encryptedMessage = cipher.doFinal(message.getBytes());
      result = Base64.getEncoder().encodeToString(encryptedMessage);
    } catch (Exception e) {
      Logger.error("RSA encryption: " + e.getMessage());
      Logger.error("Data size is: " + message.getBytes().length + " message is \n" + message);
    }
    return result;
  }

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

  public String decryptMessageRSA(String encryptedMessage) {
    String result = null;
    try {
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
      byte[] decryptedMessage = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
      result = new String(decryptedMessage);
    } catch (Exception e) {
      Logger.error("RSA decryption: " + e.getMessage());
    }
    return result;
  }

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

  public void sendEncryptedMessageAES(String message) {
    super.sendMessage(encryptMessageAES(message));
  }
}
