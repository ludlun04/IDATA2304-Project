package no.ntnu.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.crypto.SecretKey;
import no.ntnu.tools.Logger;

/**
 * Class for handling the client
 */
public class CommunicationHandler {
  protected BufferedReader inputReader;
  private final PrintWriter outputWriter;
  private final Socket socket;
  private CipherKeyHandler cipherKeyHandler;

  /**
   * Constructor for client handler
   *
   * @param socket socket the client is connected to
   * @throws RuntimeException if constructor fails to open communication with
   *                          socket
   */
  public CommunicationHandler(Socket socket) throws IOException {
    this.inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.outputWriter = new PrintWriter(socket.getOutputStream(), true);
    this.socket = socket;
  }

  public void enableEncryptionwithKey(SecretKey key) {
    this.cipherKeyHandler = new CipherKeyHandler(key);
  }

  /**
   * Sends a message through the associated socket
   *
   * @param message to be sent
   */
  public void sendMessage(String message) {
    this.outputWriter.println(message);
  }

  /**
   * Sends an encrypted message through the associated socket
   */
  public void sendEncryptedMessage(String message) {

    String encryptedMessage = this.cipherKeyHandler.encryptMessageAES(message);
    Logger.info("Sending and encrypting message: " + message);
    sendMessage(encryptedMessage);
    Logger.info("Sent encrypted message: " + encryptedMessage);
  }

  /**
   * Waits for a message from the client
   *
   * @return message that has been sent from the client or null if the was no
   * message to get
   */
  public String getMessage() {
    String result = null;
    try {
      result = this.inputReader.readLine();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    return result;
  }

  /**
   * Waits for a message from the client and decrypts it
   *
   * @return decrypted message that has been sent from the client or null if there
   * was no message to get
   */
  public String getDecryptedMessage() {
    String encryptedMessage = getMessage();
    if (encryptedMessage == null) {
      Logger.error("Received null message");
      return null;
    }
    String decryptedMessage = this.cipherKeyHandler.decryptMessageAES(encryptedMessage);
    Logger.info("Received encrypted message: " + encryptedMessage);
    Logger.info("Decrypted message is: " + decryptedMessage);
    return decryptedMessage;
  }

  public void close() {
    try {
      this.inputReader.close();
      this.outputWriter.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  public Socket getSocket() {
    return this.socket;
  }
}
