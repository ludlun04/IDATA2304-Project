package no.ntnu.server;

import no.ntnu.utils.CommunicationHandler;

public class GreenHouseHandler {
    private CommunicationHandler communicationHandler;
    private boolean isConnected;

    public GreenHouseHandler(CommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
    }

    public void start() {
        this.isConnected = true;
        while (isConnected) {
            String message = this.communicationHandler.getMessage();

            if (message == null) {
                this.isConnected = false;
            }

            
        }
    }

    public void stop() {
        this.isConnected = false;
    }
}
