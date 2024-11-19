package no.ntnu.greenhouse;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;

public class NodeCommunicationHandler {
  private GreenhouseNode node;
  private CommunicationHandler handler;

  public NodeCommunicationHandler(Socket socket, GreenhouseNode node) throws IOException {
    this.handler = new CommunicationHandler(socket);
    this.node = node;
  }

  /**
   * Sends a message to the client.
   * 
   * @param message String to be sent to the client
   */
  public void sendMessage(String message) {
    this.handler.sendMessage(message);
  }

  /**
   * Closes the communication
   * 
   * @throws IOException
   */
  public void close() {
    this.handler.close();
  }

  public void handleCommunication() throws IOException {
    String message = this.handler.getMessage();

    if (message == null) {
      throw new IOException("Communication interrupted");
    }

    String[] args = message.split(" ");

    String command = args[0];

    switch (command) {
      case "set" -> {
        int actuatorId = Integer.parseInt(args[1]);
        boolean state = Boolean.parseBoolean(args[2]);
        this.node.setActuator(actuatorId, state);

        break;
      }
      default -> Logger.info("Unknown command: " + command);
    }
  }

}
