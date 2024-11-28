package no.ntnu.greenhouse;

import javax.crypto.SecretKey;
import no.ntnu.controlpanel.networking.NoSuchCommand;
import no.ntnu.utils.commands.Command;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;

import java.io.IOException;

public class GreenhouseCommunicationHandler {
  private CommandParser commandParser;
  private CommunicationHandler handler;

  public GreenhouseCommunicationHandler(CommunicationHandler handler, CommandParser commandParser) {
    this.handler = handler;
    this.commandParser = commandParser;
  }

  public void enableEncryptionwithKey(SecretKey key) {
    this.handler.enableEncryptionwithKey(key);
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

  public boolean handleEncryptedMessage() throws IOException {
    String message = this.getDecryptedMessage();

    handlePlainMessage(message);
    return false;
  }

  public void handleMessage() throws IOException {
    String message = this.getMessage();

    handlePlainMessage(message);
  }

  private void handlePlainMessage(String message) throws IOException {
    if (message != null) {
      Command command = null;
      try {
        command = this.commandParser.parse(message);
      } catch (IllegalArgumentException | NoSuchCommand e) {
        Logger.error("Failed to parse command. " + e.getMessage());
      }

      if (command != null) {
        command.execute();
      }
    } else {
      throw new IOException("Communication interrupted");
    }
  }

  public void close() {
    this.handler.close();
  }
}
