package no.ntnu.gui.greenhouse;

import java.util.List;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.SensorActuatorNode;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.greenhouse.SensorListener;

/**
 * Window with GUI for overview and control of one specific sensor/actuator node.
 */
public class NodeGuiWindow extends Stage implements SensorListener, ActuatorListener {
  private static final double VERTICAL_OFFSET = 50;
  private static final double HORIZONTAL_OFFSET = 150;
  private static final double WINDOW_WIDTH = 300;
  private static final double WINDOW_HEIGHT = 300;
  private final SensorActuatorNode node;

  private ActuatorPane actuatorPane;
  private SensorPane sensorPane;

  /**
   * Create a GUI window for a specific node.
   *
   * @param node The node which will be handled in this window
   */
  public NodeGuiWindow(SensorActuatorNode node) {
    this.node = node;
    Scene scene = new Scene(createContent(), WINDOW_WIDTH, WINDOW_HEIGHT);
    setScene(scene);
    setTitle("Node " + node.getId());
    initializeListeners(node);
    setPositionAndSize();
  }

  /**
   * Set the position and size of the window.
   */
  private void setPositionAndSize() {
    setX((node.getId() - 1) * HORIZONTAL_OFFSET);
    setY(node.getId() * VERTICAL_OFFSET);
    setMinWidth(WINDOW_HEIGHT);
    setMinHeight(WINDOW_WIDTH);
  }

  /**
   * Initialize listeners for the node.
   *
   * @param node The node to listen to
   */
  private void initializeListeners(SensorActuatorNode node) {
    setOnCloseRequest(windowEvent -> shutDownNode());
    node.addSensorListener(this);
    node.addActuatorListener(this);
  }

  /**
   * Shut down the node.
   */
  private void shutDownNode() {
    node.stop();
  }

  /**
   * Create the content of the window.
   *
   * @return The content of the window
   */
  private Parent createContent() {
    actuatorPane = new ActuatorPane(node.getActuators());
    sensorPane = new SensorPane(node.getSensors());
    return new VBox(sensorPane, actuatorPane);
  }

  /**
   * Update the sensor values in the GUI using values from a list of sensors.
   *
   * @param sensors A list of sensors
   */
  @Override
  public void sensorsUpdated(List<Sensor> sensors) {
    if (sensorPane != null) {
      sensorPane.update(sensors);
    }
  }

  /**
   * Update the actuator values in the GUI using a given actuator.
   *
   * @param nodeId   ID of the node on which this actuator is placed
   * @param actuator The actuator that has changed its state
   */
  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    if (actuatorPane != null) {
      actuatorPane.update(actuator);
    }
  }
}
