package no.ntnu.server.networking;

import no.ntnu.server.GreenHouseServer;
import no.ntnu.utils.CommunicationHandler;

/**
 * Handles TCP communication between a server and control panel on the server side.
 */
public class ControlPanelHandler extends ServerSideHandler {

  /**
   * Creates a new instance of the ControlPanelHandler.
   *
   * @param handler The communication handler to use.
   * @param server The server to use.
   */
  public ControlPanelHandler(CommunicationHandler handler, GreenHouseServer server) {
    super(handler, server);
  }

  /**
   * Handles a message. Sends the message to all control panels.
   *
   * @param message The message to handle.
   */
  @Override
  protected void handleMessage(String message) {
    this.getServer().sendToGreenhouse(message);
  }
}
