package no.ntnu.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.tools.Logger;

/**
 * Class representing a server accepting incoming connection requests
 */
public class Server {
  private ServerSocket serverSocket;
  private List<ControlPanelClientHandler> ControlPanels;
  private GreenhouseSimulator simulator;


  /**
   * Constructor of server
   *
   * @param port port to listen for clients
   */
  public Server(int port, GreenhouseSimulator simulator) {
    this.ControlPanels = new ArrayList<>();
    this.simulator = simulator;
    try {
      this.serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      System.out.println("Could not establish server socket");
      throw new RuntimeException(e);
    }
  }

  /**
   * Runs the server
   * <p>
   * Waits for clients and listens to commands.
   * </p>
   */
  public void run() {
    boolean finished = false;
    while (!finished) {
      Logger.info("Looking for new client...");

      try {
        Socket newSocket = this.serverSocket.accept();
        ControlPanelClientHandler newControlPanelHandler =
            new ControlPanelClientHandler(newSocket, this);
        this.ControlPanels.add(newControlPanelHandler);

        new Thread(() -> {
          Logger.info("Clients = " + this.ControlPanels.size());

          newControlPanelHandler.sendMessage("Hello #" + this.ControlPanels.size());

          // Handle client for socket lifetime
          while (newSocket.isConnected()) {
            newControlPanelHandler.handleClient();
          }
        }).start();
      } catch (IOException e) {
        Logger.error(e.getMessage());
      }
      finished = true;
    }
  }

  public GreenhouseSimulator getSimulator() {
    return this.simulator;
  }
}
