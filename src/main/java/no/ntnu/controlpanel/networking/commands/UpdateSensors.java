package no.ntnu.controlpanel.networking.commands;

import java.util.List;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.greenhouse.SensorReading;

/**
 * Command for updating the sensor data of a node.
 */
public class UpdateSensors extends LogicCommand {
  private List<SensorReading> readings;
  private int nodeId;

  /**
   * Create a new update sensors command.
   *
   * @param logic The control panel logic.
   * @param nodeId The id of the node that the sensors belong to.
   * @param readings The new sensor readings.
   */
  public UpdateSensors(ControlPanelLogic logic, int nodeId, List<SensorReading> readings) {
    super(logic);
    this.nodeId = nodeId;
    this.readings = readings;
  }

  /**
   * Execute the command.
   */
  @Override
  public void execute() {
    this.logic.onSensorData(nodeId, readings);
  }
}
