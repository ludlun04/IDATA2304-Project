package no.ntnu.greenhouse;

import java.io.*;
import java.net.Socket;
import java.util.*;

import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Application entrypoint - a simulator for a greenhouse.
 */
public class GreenhouseSimulator {
  private final Map<Integer, SensorActuatorNode> nodes = new HashMap<>();

  private final List<PeriodicSwitch> periodicSwitches = new LinkedList<>();
  private final boolean fake;
  private CommunicationHandler handler;

  /**
   * Create a greenhouse simulator.
   *
   * @param fake When true, simulate a fake periodic events instead of creating
   *             socket communication
   */
  public GreenhouseSimulator(boolean fake) {
    this.fake = fake;
  }

  /**
   * Initialise the greenhouse but don't start the simulation just yet.
   */
  public void initialize() {
    createNode(1, 2, 1, 0, 0);
    createNode(1, 0, 0, 2, 1);
    createNode(2, 0, 0, 0, 0);
    Logger.info("Greenhouse initialized");
  }

  private void createNode(int temperature, int humidity, int windows, int fans, int heaters) {
    SensorActuatorNode node = DeviceFactory.createNode(
        temperature, humidity, windows, fans, heaters);
    nodes.put(node.getId(), node);
  }

  /**
   * Start a simulation of a greenhouse - all the sensor and actuator nodes inside
   * it.
   */
  public void start() {
    initiateCommunication();
    for (SensorActuatorNode node : nodes.values()) {
      node.start();
    }
    for (PeriodicSwitch periodicSwitch : periodicSwitches) {
      periodicSwitch.start();
    }

    Logger.info("Simulator started");
  }

  private void initiateCommunication() {
    if (fake) {
      initiateFakePeriodicSwitches();
    } else {
      initiateRealCommunication();
    }
  }

  private void initiateRealCommunication() {
    // TODO - here you can set up the TCP or UDP communication
    new Thread(() -> {
      boolean reconnect = true;
      while (reconnect) {
        try (Socket socket = new Socket("127.0.0.1", 8765)) {
          System.out.println("WE MADE A SOCKET!!!!!!");
          CommunicationHandler handler = new CommunicationHandler(socket);
          this.handler = handler;
          handler.sendMessage("I am greenhouse");

          // initializeSensorListeners(node);
          // initializeActuatorListeners(node);

          handleMessage(this.handler.getMessage()); //first message not encrypted

          boolean reachedEnd = false;
          while (!reachedEnd) {
            String message = handler.getDecryptedMessage();

            if (message == null) {
              reachedEnd = true;
            } else {
              reachedEnd = handleMessage(message);
            }
          }

          //reconnect = false;

        } catch (IOException e) {
          //throw new RuntimeException(e);
        }
      }
    }).start();
  }

  private boolean handleMessage(String message) {
    String args[] = message.split(" ");
    //TODO: Move all stuff here to commandparser, also change the classes and methods to get a nodeId instead of a Node. Getting a node might lead to problems.
    boolean shouldClose = fake;
    try {
      switch (args[0]) {
        case "Encrypt":
          setupEncryption(args[1]);
          break;
        case "setupNodes":
          setupNodes();
          break;
        case "startDataTransfer":
          startDataTransfer();
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
          shouldClose = true;
          break;
        default:
          Logger.error("Unknown command: " + args[0]);
          break;
      }
    } catch (IllegalArgumentException e) {
      Logger.error("Handling messages failed. " + e.getMessage());
    } catch (IndexOutOfBoundsException e) {
      Logger.error("Missing command parameters. " + e.getMessage());
    }

    return shouldClose;
  }

  public void setupEncryption(SecretKey key) {
    this.handler.enableEncryptionwithKey(key);
    this.handler.sendEncryptedMessage("OK");
  }

  /**
   * Add an actuator to a node.
   */
  public void addActuatorToNode(int nodeId, Actuator actuator) {
    this.getNode(nodeId).addActuator(actuator);
  }

  /**
   * Add a sensor to a node.
   */
  public void addSensorToNode(int nodeId, Sensor sensor, int amount) {
    this.getNode(nodeId).addSensors(sensor, amount);
  }

  /**
   * Set the actuator value.
   */
  public void setActuatorState(int nodeId, int actuatorId, boolean state) {
    this.getNode(nodeId).getActuators().get(actuatorId).set(state);
  }

  /**
   * Get the node values.
   */
  public void getNodeValues(SensorActuatorNode node) {
    for (Sensor sensor : node.getSensors()) {
      this.handler.sendEncryptedMessage("" + sensor.getReading().getValue());
    }
  }

  public void setupNodes() {
    for (SensorActuatorNode node : this.getNodes()) {
      sendNodeInformation(node);
    }
  }

  public void startDataTransfer() {
    for (SensorActuatorNode node : this.getNodes()) {
      initializeActuatorListeners(node);
      initializeSensorListeners(node);
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
      response = String.format("%s %d %s %b", response, actuator.getId(), actuator.getType(),
          actuator.isOn());
    }

    this.handler.sendEncryptedMessage(response);
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
      this.handler.sendEncryptedMessage(response);
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
      this.handler.sendEncryptedMessage(response);
    });
  }

  private void initiateFakePeriodicSwitches() {
    periodicSwitches.add(new PeriodicSwitch("Window DJ", nodes.get(1), 2, 20000));
    periodicSwitches.add(new PeriodicSwitch("Heater DJ", nodes.get(2), 7, 8000));
  }

  /**
   * Stop the simulation of the greenhouse - all the nodes in it.
   */
  public void stop() {
    stopCommunication();
    for (SensorActuatorNode node : nodes.values()) {
      node.stop();
    }
  }

  private void stopCommunication() {
    if (fake) {
      for (PeriodicSwitch periodicSwitch : periodicSwitches) {
        periodicSwitch.stop();
      }
    } else {
      // TODO - here you stop the TCP/UDP communication
    }
  }

  /**
   * Add a listener for notification of node staring and stopping.
   *
   * @param listener The listener which will receive notifications
   */
  public void subscribeToLifecycleUpdates(NodeStateListener listener) {
    for (SensorActuatorNode node : nodes.values()) {
      node.addStateListener(listener);
    }
  }

  public SensorActuatorNode getNode(int id) {
    return nodes.get(id);
  }

  public List<SensorActuatorNode> getNodes() {
    return nodes.values().stream().toList();
  }

}
