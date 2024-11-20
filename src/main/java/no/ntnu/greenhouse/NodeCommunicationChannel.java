package no.ntnu.greenhouse;

import no.ntnu.controlpanel.ControlPanelCommunicationHandler;
import no.ntnu.server.ClientHandler;
import no.ntnu.tools.Logger;

import java.io.IOException;
import java.net.Socket;

public class NodeCommunicationChannel {

  private static final int RECONNECT_ATTEMPTS = 5;
  private static final int RECONNECT_ATTEMPT_WAIT_MILLIS = 5000;

  private boolean stayConnected;
  private GreenhouseNode node;
  private NodeCommunicationHandler handler;

  public NodeCommunicationChannel(GreenhouseNode node) throws IllegalArgumentException {
    if (node == null) {
      throw new IllegalArgumentException("node cannot be null");
    }
    this.node = node;
  }

  public void sendInitialDataRequest() {
    this.handler.sendMessage("initialData");
  }

  public boolean open() {
    this.stayConnected = true;
    Logger.info("Connecting to socket");

    new Thread(() -> {
      if (!attemptReconnect()) {
        Logger.info("Failed to connect");
        this.node.onCommunicationChannelClosed();
        return;
      }

      sendInitialDataRequest();
      while (this.stayConnected) {
        try {
          this.handler.handleCommunication();
        } catch (IOException ioException) {
          if (!attemptReconnect()) {
            Logger.info("Could not connect, stopping communication channel.");
            this.stayConnected = false;
            this.node.onCommunicationChannelClosed();
          }
        } catch (Exception e) {
          Logger.error(e.getMessage());
        }
      }
    }).start();
    return stayConnected;
  }

  private void createHandler() throws IOException {
    this.handler = new NodeCommunicationHandler(
        new Socket("127.0.0.1", 8765), this.node);
  }

  private boolean attemptReconnect() {
    int tries = RECONNECT_ATTEMPTS;

    boolean result = false;

    while (tries > 0 && !result) {

      try {
        createHandler();
        result = true;
      } catch (IOException ioException) {
        Logger.info("Attempting to reconnect, " + tries + " tries left...");
        wait(RECONNECT_ATTEMPT_WAIT_MILLIS);
        tries--;
      }

    }
    return result;
  }

  private static void wait(int delayMillis) {
    try {
      Thread.sleep(delayMillis);
    } catch (InterruptedException e) {
      Logger.error("Could not sleep");
    }
  }

  public void close() {

  }

}
