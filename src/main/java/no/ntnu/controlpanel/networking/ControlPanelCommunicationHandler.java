package no.ntnu.controlpanel.networking;

import java.io.IOException;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;
import no.ntnu.utils.commands.Command;


/**
 * Class used for handleing communication for the control panel
 */
public class ControlPanelCommunicationHandler {

  private final ControlPanelCommandParser commandParser;
  private final CommunicationHandler handler;

  public ControlPanelCommunicationHandler(CommunicationHandler handler,
                                          ControlPanelCommandParser commandParser) {
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


  /**
   * Sends an encrypted message through the socket
   * Uses the set key to encrypt
   * 
   * @param message String to be encrypted and sent
   */
  public void sendEncryptedMessage(String message) {
    this.handler.sendEncryptedMessage(message);
  }


  /**
   * Blocks the thread until a full message gets recieved
   * 
   * @return String message that gets recieved
   */
  public String getMessage() {
    return this.handler.getMessage();
  }


  /**
  * Blocks the thread until a full message gets recieved 
  * This method also decrypts with the currently set key
  *
  * @return String message that gets recieved and decrypted
  */
  public String getDecryptedMessage() {
    return this.handler.getDecryptedMessage();
  }

  /**
   * Handles recieving of encrypted messages
   * 
   * @throws IOException if it fails to read a message
   */
  public void handleEncryptedMessage() throws IOException {
    String message = this.handler.getDecryptedMessage();

    handlePlainMessage(message);
  }


  /**
   * Handles recieving of messages
   * 
   * @throws IOException if it fails to read a message
   */
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

  /**
   * Closes the handler
   */
  public void close() {
    this.handler.close();
  }
}
