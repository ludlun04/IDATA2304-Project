package no.ntnu.server;

import java.net.Socket;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.SensorActuatorNode;
import no.ntnu.tools.Logger;

public class ControlPanelClientHandler extends ClientHandler {
  private Server server;

  public ControlPanelClientHandler(Socket socket, Server server) {
    super(socket);
    this.server = server;
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
          case "add":
            switch (args[1]){
              case "sensor":
                Sensor sensor = new Sensor(args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]),Integer.parseInt(args[5]), args[6]);
                this.server.getSimulator().getNode(Integer.parseInt(args[1])).addSensors(sensor, Integer.parseInt(args[7]));
                break;
              case "actuator":
                Actuator actuator = new Actuator(args[2], Integer.parseInt(args[3]));
                this.server.getSimulator().getNode(Integer.parseInt(args[1])).addActuator(actuator);
                break;
            }
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
    this.server.getSimulator().getNode(Integer.parseInt(args[1])).getActuators()
        .get(Integer.parseInt(args[2])).set(Boolean.parseBoolean(args[3]));
  }

  /**
   * Get the node values.
   * @param args The arguments for values.
   */
  private void getNodeValues(String[] args) {
    for (Sensor sensor : this.server.getSimulator().getNode(Integer.parseInt(args[1])).getSensors()) {
      this.sendMessage("" + sensor.getReading().getValue());
    }
  }
}
