package no.ntnu.controlpanel.networking;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.controlpanel.networking.Commands.Command;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ControlPanelCommunicationHandler {

  private CommandParser commandParser;
  private CommunicationHandler handler;

  public ControlPanelCommunicationHandler(Socket socket, CommandParser commandParser) throws IOException {
    this.handler = new CommunicationHandler(socket);
    this.commandParser = commandParser;
  }

  /**
   * Sends a message to the client.
   * 
   * @param message String to be sent to the client
   */
  public void sendMessage(String message) {
    this.handler.sendMessage(message);
  }

  public String getMessage() {
    return this.handler.getMessage();
  }

  public void handleCommunication() throws IOException {
    String message = this.handler.getMessage();

    if (message != null) {
      Command command = this.commandParser.parse(message);

      if (command != null) {
        command.execute();
      }
    } else {
      throw new IOException("Communication interrupted");
    }
  }
}
