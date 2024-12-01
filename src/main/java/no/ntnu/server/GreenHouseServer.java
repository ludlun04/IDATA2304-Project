package no.ntnu.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import javax.crypto.SecretKey;
import no.ntnu.server.networking.ControlPanelHandler;
import no.ntnu.server.networking.GreenHouseHandler;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CipherKeyHandler;
import no.ntnu.utils.CommunicationHandler;


/**
 * Server for the greenhouse system.
 */
public class GreenHouseServer extends Server {
  private final ArrayList<ControlPanelHandler> controlPanels;
  private GreenHouseHandler greenhouse;

  /**
   * Create a new server.
   *
   * @param port port to use for the server
   */
  public GreenHouseServer(int port) {
    super(port);
    this.controlPanels = new ArrayList<>();
  }

  /**
   * Start the server.
   */
  public void start() {
    try {
      new Thread(() -> {
        super.run();
      }).start();
    } catch (Exception e) {
      Logger.error("Failed to start server: " + e.getMessage());
      throw new RuntimeException();
    }
  }

  /**
   * Send a message to all connected control panels.
   *
   * @param message message to send
   */
  public void sendToAllControlPanels(String message) {
    for (ControlPanelHandler controlPanelHandler : this.controlPanels) {
      controlPanelHandler.sendEncryptedMessage(message);
    }
  }

  /**
   * Send a message to the greenhouse.
   *
   * @param message message to send
   */
  public void sendToGreenhouse(String message) {
    if (this.greenhouse != null) {
      this.greenhouse.sendEncryptedMessage(message);
    }
  }

  /**
   * Handle a new socket connection.
   *
   * @param socket socket to handle
   */
  @Override
  protected void socketConnected(Socket socket) {

    new Thread(() -> {
      try {
        CommunicationHandler newHandler = new CommunicationHandler(socket);
        SecretKey uniqueHandlerKey = CipherKeyHandler.getNewRandomAesKey();
        newHandler.enableEncryptionwithKey(uniqueHandlerKey);

        String initialMessage = newHandler.getMessage();

        String keyEncoded = Base64.getEncoder().encodeToString(uniqueHandlerKey.getEncoded());

        executeHandlerLogic(initialMessage, newHandler, keyEncoded);

        newHandler.close();

      } catch (Exception e) {
        Logger.error("Failed to create handler, " + e.getMessage());
      }

    }).start();
  }

  /**
   * Execute the logic for a new handler.
   *
   * @param initialMessage initial message from the handler
   * @param newHandler     handler to execute logic for
   * @param keyEncoded     encoded key for the handler
   */
  private void executeHandlerLogic(String initialMessage, CommunicationHandler newHandler,
                                   String keyEncoded) {
    String encryptionResponse;
    switch (initialMessage) {
      case "I am controlpanel":
        newHandler.sendMessage("Hello from server");
        ControlPanelHandler controlPanelHandler = new ControlPanelHandler(newHandler, this);

        newHandler.sendMessage("Encrypt " + keyEncoded);
        encryptionResponse = newHandler.getDecryptedMessage();

        // attempt to get encrypted message, if 'OK' we know encryption is working
        if ("OK".equals(encryptionResponse)) {
          Logger.info("Encryption successfully established");
          this.controlPanels.add(controlPanelHandler);
          Logger.info("Controlpanel added (" + this.controlPanels.size() + " in total).");
          if (this.greenhouse != null) {
            this.greenhouse.sendEncryptedMessage("setupNodes");
          }
          controlPanelHandler.start();
          this.controlPanels.remove(controlPanelHandler);
        } else {
          Logger.error("Encryption failed");
        }

        break;

      case "I am greenhouse":
        if (this.greenhouse != null) {
          return;
        }

        GreenHouseHandler greenHouseHandler = new GreenHouseHandler(newHandler, this);
        this.greenhouse = greenHouseHandler;
        Logger.info("Greenhouse connected");

        newHandler.sendMessage("Encrypt " + keyEncoded);
        encryptionResponse = newHandler.getDecryptedMessage();

        // attempt to get encrypted message, if 'OK' we know encryption is working
        if ("OK".equals(encryptionResponse)) {
          Logger.info("Encryption successfully established");
          this.greenhouse.sendEncryptedMessage("setupNodes");
          this.greenhouse.sendEncryptedMessage("startDataTransfer");
          this.greenhouse.start();
          this.greenhouse = null;
        } else {
          Logger.error("Encryption failed");
        }
        break;
      default:
        Logger.error("Unsupported node: " + initialMessage);
    }
  }
}
