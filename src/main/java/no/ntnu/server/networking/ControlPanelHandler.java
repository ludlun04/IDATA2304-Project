package no.ntnu.server.networking;

import no.ntnu.server.GreenHouseServer;
import no.ntnu.utils.CommunicationHandler;

public class ControlPanelHandler {

  private CommunicationHandler communicationHandler;
  private GreenHouseServer server;
  private boolean isConnected;

  public ControlPanelHandler(CommunicationHandler handler, GreenHouseServer server) {
    this.communicationHandler = handler;
    this.server = server;
  }

  public void sendMessage(String message) {
    this.communicationHandler.sendEncryptedMessage(message);
  }

  public void sendEncryptedMessage(String message) {
    this.communicationHandler.sendEncryptedMessage(message);
  }

  public void start() {
    this.isConnected = true;
    while (isConnected) {
      String message = this.communicationHandler.getDecryptedMessage();

      if (message == null) {
        this.isConnected = false;
      } else {
        this.server.sendToGreenhouse(message);
      }

    }
  }

  public void stop() {
    this.isConnected = false;
  }
}
