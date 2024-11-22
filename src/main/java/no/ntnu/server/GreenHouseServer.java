package no.ntnu.server;

import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;

import java.net.Socket;
import java.util.ArrayList;

public class GreenHouseServer extends Server {
  private ArrayList<ControlPanelHandler> controlPanels;
  private GreenHouseHandler greenhouse;

  public GreenHouseServer(int port) {
    super(port);
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

        if (initialMessage.equals("Hello controlpanel")) {
          ControlPanelHandler controlPanelHandler = new ControlPanelHandler(newHandler, this);

          System.out.println("Controlpanel added");

          controlPanelHandler.start();

          this.controlPanels.add(controlPanelHandler);

        } else if (initialMessage.equals("Hello greenhouse")) {
          GreenHouseHandler greenHouseHandler = new GreenHouseHandler(newHandler, this);

          System.out.println("Greenhouse connected");
          this.greenhouse = greenHouseHandler;
          greenHouseHandler.start();
          this.greenhouse = null;
        }
      } catch (Exception e) {
        System.out.println("Failed to create handler");
      }
    }).start();
  }
}
