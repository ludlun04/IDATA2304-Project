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

  @Override
  protected void socketConnected(Socket socket) {
    // ControlPanelClientHandler result = null;
    // try {
    // result = new ControlPanelClientHandler(socket, this);
    // } catch (IOException e) {
    // Logger.error(e.getMessage());
    // }

    try {
      CommunicationHandler newHandler = new CommunicationHandler(socket);

      new Thread(() -> {
        String initialMessage = newHandler.getMessage();

        if (initialMessage.equals("Hello controlpanel")) {
          ControlPanelHandler controlPanelHandler = new ControlPanelHandler(newHandler);

          System.out.println("Controlpanel added");

          this.controlPanels.add(controlPanelHandler);

        } else if (initialMessage.equals("Hello greenhouse")) {
          GreenHouseHandler greenHouseHandler = new GreenHouseHandler(newHandler);

          
          System.out.println("Greenhouse connected");
          this.greenhouse = greenHouseHandler;
          greenHouseHandler.start();
          this.greenhouse = null;
        }
      }).start();
    } catch (Exception e) {
      System.out.println("Failed to create handler");
    }
  }
}
