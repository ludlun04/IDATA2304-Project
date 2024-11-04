package no.ntnu.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import no.ntnu.greenhouse.GreenhouseSimulator;

/**
 * Class representing a server accepting incoming connection requests
 */
public class Server {
  private ServerSocket serverSocket;
  private List<ControlPanelClientHandler> controlPanels;
  private List<GreenhouseSimulator> greenhouseSimulators;


  /**
   * Constructor of server
   *
   * @param port port to listen for clients
   */
  public Server(int port) {
    this.greenhouseSimulators = new ArrayList<>();
    this.controlPanels = new ArrayList<>();
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
  @SuppressWarnings("all")
  public void run() {
    this.initializeGreenhouseSimulators();
    while (true) {
      System.out.println("Looking for new client...");

      try {
        Socket newSocket = this.serverSocket.accept();
        System.out.println("New client connected: " + newSocket.getRemoteSocketAddress());
          ControlPanelClientHandler newControlPanelHandler =
              new ControlPanelClientHandler(newSocket, this);
          this.controlPanels.add(newControlPanelHandler);
          new Thread(() -> {
            newControlPanelHandler.sendMessage("Hello CP #" + this.controlPanels.size());

            // Handle client for socket lifetime
            while (newSocket.isConnected()) {
              newControlPanelHandler.handleClient();
            }
          }).start();
        System.out.println(
            "Control panels = " + this.controlPanels.size());
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    }
  }

  public void initializeGreenhouseSimulators() {
    GreenhouseSimulator greenhouseSimulator1 = new GreenhouseSimulator(false);
    GreenhouseSimulator greenhouseSimulator2 = new GreenhouseSimulator(false);
    greenhouseSimulator1.initialize();
    greenhouseSimulator2.initialize();
    greenhouseSimulator1.start();
    greenhouseSimulator2.start();
    this.greenhouseSimulators.add(greenhouseSimulator1);
    this.greenhouseSimulators.add(greenhouseSimulator2);
    }

    public List<GreenhouseSimulator> getGreenhouseSimulators() {
        return this.greenhouseSimulators;
    }

}
