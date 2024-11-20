package no.ntnu.server;

import no.ntnu.utils.CommunicationHandler;

public class ControlPanelHandler {

  private CommunicationHandler communicationHandler;

  public ControlPanelHandler(CommunicationHandler handler) {
    this.communicationHandler = handler;
  }

  public void handleClient() {
    String message = this.communicationHandler.getMessage();
    if (message != null) {
      System.out.println("NodeClientHandler: " + message);
    }
  }
}
