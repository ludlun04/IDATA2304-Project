package no.ntnu.server;

import java.net.Socket;
import no.ntnu.greenhouse.GreenhouseSimulator;

public class GreenhouseClientHandler extends ClientHandler {
  private GreenhouseSimulator greenhouse;

  public GreenhouseClientHandler(Socket socket) {
    super(socket);
  }


  public void handleClient() {
    String message = this.getMessage();
    if (message != null) {
      System.out.println("GreenhouseClientHandler: " + message);
    }

  }
}
