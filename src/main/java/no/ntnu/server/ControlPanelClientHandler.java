package no.ntnu.server;

import java.net.Socket;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.greenhouse.SensorActuatorNode;

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
      server.sendMessagesToNodes(message);
    }
  }
}
