package no.ntnu.controlpanel.networking;

import no.ntnu.controlpanel.networking.Commands.Command;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;

import java.io.IOException;

public class ControlPanelCommunicationHandler {

  private CommandParser commandParser;
  private CommunicationHandler handler;

  public ControlPanelCommunicationHandler(CommunicationHandler handler, CommandParser commandParser) throws IOException {
    this.handler = handler;
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

  public void sendEncryptedMessage(String message) {
    this.handler.sendEncryptedMessage(message);
  }

  public String getMessage() {
    return this.handler.getMessage();
  }

  public String getDecryptedMessage() {
    return this.handler.getDecryptedMessage();
  }

  public void handleEncryptedMessage() throws IOException {
    String message = this.handler.getDecryptedMessage();

    handlePlainMessage(message);
  }

  public void handleMessage() throws IOException {
    String message = this.handler.getMessage();

    handlePlainMessage(message);
  }

  private void handlePlainMessage(String message) throws IOException {
    if (message != null) {
      Command command = null;
      try {
        command = this.commandParser.parse(message);
      } catch (IllegalArgumentException e) {
        Logger.error("Failed to parse command. " + e.getMessage());
      }

      if (command != null) {
        command.execute();
      }
    } else {
      throw new IOException("Communication interrupted");
    }
  }
}
