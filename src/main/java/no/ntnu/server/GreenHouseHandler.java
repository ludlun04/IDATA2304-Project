package no.ntnu.server;

import no.ntnu.utils.CommunicationHandler;

public class GreenHouseHandler {
    private CommunicationHandler communicationHandler;
    private GreenHouseServer server;
    private boolean isConnected;

    public GreenHouseHandler(CommunicationHandler communicationHandler, GreenHouseServer server) {
        this.communicationHandler = communicationHandler;
        this.server = server;
    }

    public void SendMessage(String message) {
      this.communicationHandler.sendMessage(message);
    }

    public void start() {
        this.isConnected = true;
        while (isConnected) {
            String message = this.communicationHandler.getMessage();

            if (message == null) {
                this.isConnected = false;
            } else {
              this.server.sendToAllClients(message);
            }
        }
    }

    public void stop() {
        this.isConnected = false;
    }
}
