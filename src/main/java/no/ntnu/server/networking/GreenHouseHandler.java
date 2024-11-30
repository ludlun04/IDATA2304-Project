package no.ntnu.server.networking;

import no.ntnu.server.GreenHouseServer;
import no.ntnu.utils.CommunicationHandler;

/**
 * Handles TCP communication between a server and greenhouse on the server side.
 */
public class GreenHouseHandler extends ServerSideHandler {

  /**
   * Creates a new instance of the GreenHouseHandler.
   *
   * @param communicationHandler The communication handler to use.
   * @param server               The server to use.
   */
  public GreenHouseHandler(CommunicationHandler communicationHandler, GreenHouseServer server) {
    super(communicationHandler, server);
  }

  /**
   * Handles a message. Sends the message to all control panels.
   *
   * @param message The message to handle.
   */
  @Override
  protected void handleMessage(String message) {
    this.getServer().sendToAllControlPanels(message);
  }
}
