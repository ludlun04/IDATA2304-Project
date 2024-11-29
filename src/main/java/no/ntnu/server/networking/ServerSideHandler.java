package no.ntnu.server.networking;

import no.ntnu.server.GreenHouseServer;
import no.ntnu.utils.CommunicationHandler;

public abstract class ServerSideHandler {

  private CommunicationHandler communicationHandler;
  private boolean isConnected;
  private GreenHouseServer server;

  /**
   * Creates a new instance of the ServerSideHandler.
   *
   * @param communicationHandler The communication handler to use.
   */
  public ServerSideHandler(CommunicationHandler communicationHandler, GreenHouseServer server) {
    this.communicationHandler = communicationHandler;
    this.server = server;
  }

  protected GreenHouseServer getServer() {
    return this.server;
  }

  /**
   * Sends a plain text message.
   *
   * @param message The message to send.
   */
  public void sendMessage(String message) {
    this.communicationHandler.sendEncryptedMessage(message);
  }

  /**
   * Sends an encrypted message.
   *
   * @param message The message to send.
   */
  public void sendEncryptedMessage(String message) {
    this.communicationHandler.sendEncryptedMessage(message);
  }

  /**
   * Starts the communication handler. Will keep running until a null message is received.
   */
  public void start() {
    this.isConnected = true;
    while (isConnected) {
      String message = this.communicationHandler.getDecryptedMessage();

      if (message == null) {
        stop();
      } else {
        handleMessage(message);
      }
    }
  }

  /**
   * Handles a message.
   *
   * @param message The message to handle.
   */
  protected abstract void handleMessage(String message);

  /**
   * Stops the communication handler.
   */
  public void stop() {
    this.isConnected = false;
  }

}
