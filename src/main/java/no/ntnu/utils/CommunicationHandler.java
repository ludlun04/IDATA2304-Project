package no.ntnu.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.crypto.SecretKey;
import no.ntnu.tools.Logger;

/**
 * Class for handling client
 */
public class CommunicationHandler {
  protected BufferedReader inputReader;
  private final PrintWriter outputWriter;
  private Socket socket;
  private SecretKey aesKey;
  private CipherKeyHandler cipherKeyHandler;

  /**
   * Constructor for client handeler
   *
   * @param clientSocket socket the client is connected to
   * @throws RuntimeException if constructor fails to open communication with
   *                          socket
   */
  public CommunicationHandler(Socket clientSocket) throws IOException {
    this.socket = clientSocket;
    this.inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    this.outputWriter = new PrintWriter(clientSocket.getOutputStream(), true);
    this.cipherKeyHandler = new CipherKeyHandler();
    this.aesKey = cipherKeyHandler.getAESKey();
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
   * Sends a encrypted message through the associated socket
   *
   * @param encryptedMessage to be sent
   */
  public void sendEncryptedMessageAES(String encryptedMessage) {
    this.outputWriter.println(encryptMessageAES(encryptedMessage));
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
   * Waits for a encrypted message from the client
   *
   * @return decrypted message that has been sent from the client or null if the was no
   * message to get
   */
  public String getDecryptedMessageAES() {
    return decryptMessageAES(getMessage());
  }

  /**
   * Encrypts a message using AES
   *
   * @param message to be encrypted
   */
  public String encryptMessageAES(String message) {
    return this.cipherKeyHandler.encryptAES(message, this.aesKey);
  }

  /**
   * Decrypts a message using AES
   *
   * @param message to be decrypted
   */
  public String decryptMessageAES(String message) {
    return this.cipherKeyHandler.decryptAES(message, this.aesKey);
  }

  /**
   * Closes the communication on the socket
   */
  public void close() {
    try {
      this.socket.close();
      // this.inputReader.close();
      // this.outputWriter.close();

    } catch (IOException e) {
      Logger.error("Failed to close because");
      Logger.error(e.getMessage());
    }
  }
}
