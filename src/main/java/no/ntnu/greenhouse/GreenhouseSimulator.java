package no.ntnu.greenhouse;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;

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

          boolean reachedEnd = false;
          while (!reachedEnd) {
            String message = handler.getMessage();
      
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
    boolean shouldClose = fake;
    try {
      switch (args[0]) {
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
    } catch (NumberFormatException e) {
      Logger.error("Handeling messages failed because");
      Logger.error(e.getMessage());
    }

    return shouldClose;
  }

  /**
   * Add an actuator to a node.
   * 
   * @param args The arguments for values.
   */
  private void addActuatorToNode(String[] args) {
    int nodeId = Integer.parseInt(args[2]);
    Actuator actuator = new Actuator(args[3], nodeId);
    this.getNode(nodeId).addActuator(actuator);
  }

  /**
   * Add a sensor to a node.
   * 
   * @param args The arguments for values.
   */
  private void addSensorToNode(String[] args) {
    Sensor sensor = new Sensor(args[3], Integer.parseInt(args[4]),
        Integer.parseInt(args[5]), Integer.parseInt(args[6]), args[7]);
    this.getNode(Integer.parseInt(args[2]))
        .addSensors(sensor, Integer.parseInt(args[8]));
  }

  /**
   * Set the actuator value.
   * 
   * @param args The arguments for values.
   */
  private void setActuatorValue(String[] args) {
    this.getNode(Integer.parseInt(args[1])).getActuators()
        .get(Integer.parseInt(args[2])).set(Boolean.parseBoolean(args[3]));
  }

  /**
   * Get the node values.
   * 
   * @param args The arguments for values.
   */
  private void getNodeValues(String[] args) {
    for (Sensor sensor : this.getNode(Integer.parseInt(args[1])).getSensors()) {
      this.handler.sendMessage("" + sensor.getReading().getValue());
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
      response = String.format("%s %d %s", response, actuator.getId(), actuator.getType());
    }

    this.handler.sendMessage(response);
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
      this.handler.sendMessage(response);
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
      this.handler.sendMessage(response);
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
