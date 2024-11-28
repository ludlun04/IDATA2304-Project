package no.ntnu.server;

import no.ntnu.server.networking.ControlPanelHandler;
import no.ntnu.server.networking.GreenHouseHandler;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CipherKeyHandler;
import no.ntnu.utils.CommunicationHandler;

import javax.crypto.SecretKey;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;

public class GreenHouseServer extends Server {
  private ArrayList<ControlPanelHandler> controlPanels;
  private GreenHouseHandler greenhouse;

  public GreenHouseServer(int port) {
    super(port);
    this.controlPanels = new ArrayList<>();
  }

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

  public void stop() {
    // TODO: fix
  }

  public void sendToAllClients(String message) {
    for (ControlPanelHandler controlPanelHandler : this.controlPanels) {
      controlPanelHandler.sendEncryptedMessage(message);
    }
  }

  public void sendToGreenhouse(String message) {
    if (this.greenhouse != null) {
      this.greenhouse.sendEncryptedMessage(message);
    }
  }

  @Override
  protected void socketConnected(Socket socket) {
    // ControlPanelClientHandler result = null;
    // try {
    // result = new ControlPanelClientHandler(socket, this);
    // } catch (IOException e) {
    // Logger.error(e.getMessage());
    // }

    new Thread(() -> {
      try {
        CommunicationHandler newHandler = new CommunicationHandler(socket);
        SecretKey uniqueHandlerKey = CipherKeyHandler.getNewRandomAesKey();
        newHandler.enableEncryptionwithKey(uniqueHandlerKey);

        String initialMessage = newHandler.getMessage();

        String keyEncoded = Base64.getEncoder().encodeToString(uniqueHandlerKey.getEncoded());
        String encryptionResponse = null;

        switch (initialMessage) {
          case "I am controlpanel":
            newHandler.sendMessage("Hello from server");
            ControlPanelHandler controlPanelHandler = new ControlPanelHandler(newHandler, this);
            Logger.info("Controlpanel added (" + this.controlPanels.size() + " in total).");

            newHandler.sendMessage("Encrypt " + keyEncoded);
            encryptionResponse = newHandler.getDecryptedMessage();

            // attempt to get encrypted message, if 'OK' we know encryption is working
            if ("OK".equals(encryptionResponse)) {
              Logger.info("Encryption successfully established");
              this.controlPanels.add(controlPanelHandler);
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
            GreenHouseHandler greenHouseHandler = new GreenHouseHandler(newHandler, this);
            this.greenhouse = greenHouseHandler;
            Logger.info("Greenhouse connected");

            newHandler.sendMessage("Encrypt " + keyEncoded);
            encryptionResponse = newHandler.getDecryptedMessage();

            // attempt to get encrypted message, if 'OK' we know encryption is working
            if ("OK".equals(encryptionResponse)) {
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
        socket.close();
      } catch (Exception e) {
        Logger.error("Failed to create handler, " + e.getMessage());
      }

    }).start();
  }
}
