package no.ntnu.gui.common;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.tools.Logger;

/**
 * A section of the GUI representing a list of actuators. Can be used both on the sensor/actuator
 * node, and on a control panel node.
 */
public class ActuatorPane extends TitledPane {
  private final Map<Actuator, SimpleStringProperty> actuatorValue = new HashMap<>();
  private final Map<Actuator, SimpleBooleanProperty> actuatorActive = new HashMap<>();

  /**
   * Create an actuator pane.
   *
   * @param actuators A list of actuators to display in the pane.
   */
  public ActuatorPane(ActuatorCollection actuators) {
    super();
    setText("Actuators");
    VBox vbox = new VBox();
    vbox.setSpacing(10);
    setContent(vbox);
    addActuatorControls(actuators, vbox);
    GuiTools.stretchVertically(this);
  }

  /**
   * Add actuator controls to the parent pane.
   *
   * @param actuators The actuators to add
   * @param parent    The parent pane to add the controls to
   */
  private void addActuatorControls(ActuatorCollection actuators, Pane parent) {
    actuators.forEach(actuator ->
        parent.getChildren().add(createActuatorGui(actuator))
    );
  }

  /**
   * Create a GUI part for an actuator in the form of an HBox.
   *
   * @param actuator The actuator to create the GUI part for
   * @return The GUI part
   */
  private Node createActuatorGui(Actuator actuator) {
    HBox actuatorGui = new HBox(createActuatorLabel(actuator), createActuatorButton(actuator));
    actuatorGui.setSpacing(5);
    return actuatorGui;
  }

  /**
   * Create a button for an actuator.
   *
   * @param actuator The actuator to create a button for
   * @return The button
   */
  private Button createActuatorButton(Actuator actuator) {
    Button button = new Button();
    SimpleBooleanProperty isSelected = new SimpleBooleanProperty(actuator.isOn());
    actuatorActive.put(actuator, isSelected);
    button.setText("Change state");
    button.setOnAction(event -> {
      actuator.toggle();
      Logger.info("Actuator " + actuator + " toggled");
    });
    return button;
  }

  /**
   * Create a label for an actuator.
   *
   * @param actuator The actuator to create a label for
   * @return The label
   */
  private Label createActuatorLabel(Actuator actuator) {
    SimpleStringProperty props = new SimpleStringProperty(generateActuatorText(actuator));
    actuatorValue.put(actuator, props);
    Label label = new Label();
    label.textProperty().bind(props);
    return label;
  }

  /**
   * Generate a text representation of an actuator.
   *
   * @param actuator The actuator to generate text for
   * @return The text representation
   */
  private String generateActuatorText(Actuator actuator) {
    String onOff = actuator.isOn() ? "ON" : "off";
    return actuator.getType() + ": " + onOff;
  }

  /**
   * An actuator has been updated, update the corresponding GUI parts.
   *
   * @param actuator The actuator which has been updated
   */
  public void update(Actuator actuator) {
    SimpleStringProperty actuatorText = actuatorValue.get(actuator);
    SimpleBooleanProperty actuatorSelected = actuatorActive.get(actuator);
    if (actuatorText == null || actuatorSelected == null) {
      throw new IllegalStateException("Can't update GUI for an unknown actuator: " + actuator);
    }

    Platform.runLater(() -> {
      actuatorText.set(generateActuatorText(actuator));
      actuatorSelected.set(actuator.isOn());
    });
  }
}
