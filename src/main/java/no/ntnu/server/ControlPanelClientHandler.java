package no.ntnu.server;

import java.net.Socket;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.greenhouse.Actuator;
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
    String[] split = message.split(" ");
    String first = "";
    try {
      first = split[0];
    } catch (IndexOutOfBoundsException e) {
      Logger.error(e.getMessage());
    }
      switch (first) {
        case "Activate_actuator":
          try{
            int greenhouseId = Integer.parseInt(split[1]);
            int nodeId = Integer.parseInt(split[2]);
            int actuatorId = Integer.parseInt(split[3]);
            Actuator actuator = this.server.getGreenhouseSimulators().get(greenhouseId).getNodes().get(nodeId).getActuators().get(actuatorId);
            actuator.toggle();
          } catch (IndexOutOfBoundsException e) {
            Logger.error(e.getMessage());
          }

          break;
        default:
          System.out.println("Unknown command: " + message);
          break;
  }
}
}
