package no.ntnu.server;

import java.net.Socket;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.greenhouse.SensorActuatorNode;

public class ControlPanelClientHandler extends ClientHandler {

  public ControlPanelClientHandler(Socket socket) {
    super(socket);
  }

  public void handleClient() {
    String message = this.getMessage();
    if (message != null) {
      System.out.println("ControlPanelClientHandler: " + message);
    }
  }
}
