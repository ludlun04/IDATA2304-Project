package no.ntnu.greenhouse;

import java.io.IOException;
import javax.crypto.SecretKey;
import no.ntnu.controlpanel.networking.NoSuchCommand;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;
import no.ntnu.utils.commands.Command;

/**
 * Class used for handeling communication for the greenhouse
 */
public class GreenhouseCommunicationHandler {
  private final GreenhouseCommandParser greenhouseCommandParser;
  private final CommunicationHandler handler;

  public GreenhouseCommunicationHandler(CommunicationHandler handler,
      GreenhouseCommandParser greenhouseCommandParser) {
    this.handler = handler;
    this.greenhouseCommandParser = greenhouseCommandParser;
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
  public boolean handleEncryptedMessage() throws IOException {
    String message = this.getDecryptedMessage();

    handlePlainMessage(message);
    return false;
  }

  /**
   * Handles recieving of messages
   * 
   * @throws IOException if it fails to read a message
   */
  public void handleMessage() throws IOException {
    String message = this.getMessage();

    handlePlainMessage(message);
  }

  private void handlePlainMessage(String message) throws IOException {
    if (message != null) {
      Command command = null;
      try {
        command = this.greenhouseCommandParser.parse(message);
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

  /**
   * Closes the handler
   */
  public void close() {
    this.handler.close();
  }
}
