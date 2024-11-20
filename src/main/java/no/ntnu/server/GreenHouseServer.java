package no.ntnu.server;

import no.ntnu.tools.Logger;

import java.io.IOException;
import java.net.Socket;

public class GreenHouseServer extends Server {

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
  protected ControlPanelClientHandler getClientHandler(Socket socket) {
    ControlPanelClientHandler result = null;
//    try {
//      result = new ControlPanelClientHandler(socket, this);
//    } catch (IOException e) {
//      Logger.error(e.getMessage());
//    }
    return result;
  }
}
