package no.ntnu.server;

import java.net.Socket;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.greenhouse.SensorActuatorNode;

public class NodeClientHandler extends ClientHandler {
  private SensorActuatorNode node;

  public NodeClientHandler(Socket socket, SensorActuatorNode node) {
    super(socket);
    this.node = node;
  }

  public void handleClient() {
    String message = this.getMessage();
    if (message != null) {
      System.out.println("NodeClientHandler: " + message);
    }
  }
}
