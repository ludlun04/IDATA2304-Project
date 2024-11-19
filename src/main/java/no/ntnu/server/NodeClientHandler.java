package no.ntnu.server;

import java.net.Socket;
import no.ntnu.greenhouse.GreenhouseNode;

public class NodeClientHandler extends ClientHandler {

  public NodeClientHandler(Socket socket) {
    super(socket);
  }

  public void handleClient() {
    String message = this.getMessage();
    if (message != null) {
      System.out.println("NodeClientHandler: " + message);
    }
  }
}
