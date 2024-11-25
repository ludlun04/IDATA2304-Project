package no.ntnu.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Class for handling client
 */
public abstract class CommunicationHandler {
  protected BufferedReader inputReader;
  private final PrintWriter outputWriter;

  /**
   * Constructor for client handeler
   *
   * @param clientSocket socket the client is connected to
   * @throws RuntimeException if constructor fails to open communication with
   *                          socket
   */
  public CommunicationHandler(Socket clientSocket) throws IOException {
    this.inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    this.outputWriter = new PrintWriter(clientSocket.getOutputStream(), true);
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
   * Abstract method for handling communication
   */
  public abstract void handleCommunication();

  protected void close() {
    try {
      this.inputReader.close();
      this.outputWriter.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }
}
