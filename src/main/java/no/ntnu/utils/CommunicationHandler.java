package no.ntnu.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.crypto.SecretKey;
import no.ntnu.tools.Logger;

/**
 * Class for handling communication with a socket.
 *
 * <p>Handles reading from and writing to the socket
 *
 * <p>Handles optional enabling of encryption of the communication using a given key
 */
public class CommunicationHandler {
  protected BufferedReader inputReader;
  private final PrintWriter outputWriter;
  private final Socket socket;
  private CipherKeyHandler cipherKeyHandler;

  /**
   * Create a communication handler using a given socket.
   *
   * @param socket socket to communicate with
   * @throws RuntimeException if constructor fails to open communication with
   *                          socket
   */
  public CommunicationHandler(Socket socket) throws IOException {
    this.inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.outputWriter = new PrintWriter(socket.getOutputStream(), true);
    this.socket = socket;
  }

  /**
   * Enable encryption of the communication using a given key.
   *
   * @param key key to use for encryption
   */
  public void enableEncryptionwithKey(SecretKey key) {
    this.cipherKeyHandler = new CipherKeyHandler(key);
  }

  /**
   * Sends a message through the associated socket.
   *
   * @param message to be sent
   */
  public void sendMessage(String message) {
    this.outputWriter.println(message);
  }

  /**
   * Sends an encrypted message through the associated socket.
   */
  public void sendEncryptedMessage(String message) {

    String encryptedMessage = this.cipherKeyHandler.encryptMessageAes(message);
    sendMessage(encryptedMessage);
  }

  /**
   * Waits for a message from the socket. Returns a message if there is one, and null otherwise.
   *
   * @return message that has been sent from the socket or null if the was no
   *     message to get
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
   * Waits for a message from the socket and decrypts it.
   *
   * @return decrypted message that has been sent from the client or null if there
   *     was no message to get
   */
  public String getDecryptedMessage() {
    String encryptedMessage = getMessage();
    if (encryptedMessage == null) {
      Logger.error("Received null message");
      return null;
    }
    String decryptedMessage = this.cipherKeyHandler.decryptMessageAes(encryptedMessage);
    return decryptedMessage;
  }

  /**
   * Closes the communication with the socket.
   */
  public void close() {
    try {
      this.inputReader.close();
      this.outputWriter.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Get the socket associated with the communication handler.
   *
   * @return the socket associated with the communication handler
   */
  public Socket getSocket() {
    return this.socket;
  }
}
