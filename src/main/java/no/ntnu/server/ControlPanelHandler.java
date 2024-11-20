package no.ntnu.server;

import no.ntnu.utils.CommunicationHandler;

public class ControlPanelHandler {

  private CommunicationHandler communicationHandler;
  private boolean isConnected;

  public ControlPanelHandler(CommunicationHandler handler) {
    this.communicationHandler = handler;
    
  }

  public void start() {
    this.isConnected = true;
    while (isConnected) {
        String message = this.communicationHandler.getMessage();

        if (message == null) {
            this.isConnected = false;
        }

        switch (message) {
          case "set":
            System.out.println("");
            break;
        
          default:
            break;
        }
    }
  }

  public void stop() {
    this.isConnected = false;
  }
}
