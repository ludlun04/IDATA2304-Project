package no.ntnu.greenhouse;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.server.ControlPanelClientHandler;
import no.ntnu.server.Server;
import no.ntnu.tools.Logger;

/**
 * Application entrypoint - a simulator for a greenhouse.
 */
public class GreenhouseServer extends Server {
  
  private final boolean fake;

  /**
   * Create a greenhouse simulator.
   *
   * @param fake When true, simulate a fake periodic events instead of creating
   *             socket communication
   */
  public GreenhouseServer(boolean fake, int port) {
    super(port);
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
    GreenhouseNode node = DeviceFactory.createNode(
        temperature, humidity, windows, fans, heaters);
    nodes.put(node.getId(), node);
  }

  /**
   * Start a simulation of a greenhouse - all the sensor and actuator nodes inside
   * it.
   */
  public void start() {
    initiateCommunication();
    for (GreenhouseNode node : nodes.values()) {
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
    try {
      new Thread(() -> {
        super.run();
      }).start();
    } catch (Exception e) {
      Logger.error("Failed to start server: " + e.getMessage());
      throw new RuntimeException();
    }
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
    for (GreenhouseNode node : nodes.values()) {
      node.stop();
    }
  }

  private void stopCommunication() {
    if (fake) {
      for (PeriodicSwitch periodicSwitch : periodicSwitches) {
        periodicSwitch.stop();
      }
    } else {
      super.stop();
    }
  }

  /**
   * Add a listener for notification of node staring and stopping.
   *
   * @param listener The listener which will receive notifications
   */
  public void subscribeToLifecycleUpdates(NodeStateListener listener) {
    for (GreenhouseNode node : nodes.values()) {
      node.addStateListener(listener);
    }
  }

  public GreenhouseNode getNode(int id) {
    return nodes.get(id);
  }

  public List<GreenhouseNode> getNodes() {
    return nodes.values().stream().toList();
  }

  @Override
  protected ControlPanelClientHandler getClientHandler(Socket socket) {
    ControlPanelClientHandler result = null;
    try {
      result = new ControlPanelClientHandler(socket, this);
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }
    return result;
  }
}
