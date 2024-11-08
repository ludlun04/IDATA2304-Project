package no.ntnu.server;

import java.net.Socket;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.SensorActuatorNode;
import no.ntnu.tools.Logger;

public class ControlPanelClientHandler extends ClientHandler {
  private GreenhouseSimulator simulator;

  public ControlPanelClientHandler(Socket socket, GreenhouseSimulator simulator) {
    super(socket);
    this.simulator = simulator;
  }

  public void handleClient() {
    String message = this.getMessage();
    if (message != null) {
      System.out.println("ControlPanelClientHandler: " + message);
    }
    while (message != null) {
      message = this.getMessage();
      Logger.info("Client message:" + message);
      String args[] = message.split(" ");

      try {
        switch (args[0]) {
          case "get":
            getNodeValues(args);
            break;
          case "set":
            setActuatorValue(args);
            break;
          default:
            Logger.error("Unknown command: " + args[0]);
            break;
        }
      } catch (NumberFormatException e) {
        Logger.error(e.getMessage());
      }
    }
  }

  /**
   * Set the actuator value.
   * @param args The arguments for values.
   */
  private void setActuatorValue(String[] args) {
    this.simulator.getNode(Integer.parseInt(args[1])).getActuators()
        .get(Integer.parseInt(args[2])).set(Boolean.parseBoolean(args[3]));
  }

  /**
   * Get the node values.
   * @param args The arguments for values.
   */
  private void getNodeValues(String[] args) {
    for (Sensor sensor : this.simulator.getNode(Integer.parseInt(args[1])).getSensors()) {
      super.sendMessage("" + sensor.getReading().getValue());
    }
  }
}
