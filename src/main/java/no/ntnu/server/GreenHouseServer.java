package no.ntnu.server;

import no.ntnu.server.networking.ControlPanelHandler;
import no.ntnu.server.networking.GreenHouseHandler;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;

import java.net.Socket;
import java.util.ArrayList;

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
      controlPanelHandler.sendMessage(message);
    }
  }

  public void sendToGreenhouse(String message) {
    if (this.greenhouse != null) {
      this.greenhouse.SendMessage(message);
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

        String initialMessage = newHandler.getMessage();

        switch (initialMessage) {
          case "I am controlpanel":
            ControlPanelHandler controlPanelHandler = new ControlPanelHandler(newHandler, this);
            Logger.info("Controlpanel added");
            this.controlPanels.add(controlPanelHandler);
            newHandler.sendMessage("Hello from server");
            controlPanelHandler.start();
            this.controlPanels.remove(controlPanelHandler);
            break;
          case "I am greenhouse":
            GreenHouseHandler greenHouseHandler = new GreenHouseHandler(newHandler, this);
            Logger.info("Greenhouse connected");
            newHandler.sendMessage("initialData");
            this.greenhouse = greenHouseHandler;
            greenHouseHandler.start();
            this.greenhouse = null;
            break;
          default:
            Logger.error("Unsupported node: " + initialMessage);
        }
        socket.close();
      } catch (Exception e) {
        Logger.error("Failed to create handler");
      }

    }).start();
  }

  public static void main(String[] args) {
    GreenHouseServer server = new GreenHouseServer(8765);
    server.start();


  }
}
