package no.ntnu.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a server accepting incoming connection requests
 */
public class Server {
  private ServerSocket nodeServerSocket;
  private ServerSocket controlPanelServerSocket;
  private List<NodeClientHandler> SensorActuatorNodes;
  private List<ControlPanelClientHandler> ControlPanels;


  /**
   * Constructor of server
   *
   * @param port port to listen for clients
   */
  public Server(int port) {
    this.SensorActuatorNodes = new ArrayList<>();
    this.ControlPanels = new ArrayList<>();
    try {
      this.nodeServerSocket = new ServerSocket(port);
      this.controlPanelServerSocket = new ServerSocket(port);
    } catch (IOException e) {
      System.out.println("Could not establish server socket");
      throw new RuntimeException(e);
    }
  }

  /**
   * Runs the server
   * <p>
   * Waits for clients and listens to commands.
   */
  public void run() {
    while (true) {
      System.out.println("Looking for new client...");

      try {
        Socket newNodeSocket = this.nodeServerSocket.accept();
        Socket newControlPanelSocket = this.controlPanelServerSocket.accept();
        NodeClientHandler newNodeHandler = new NodeClientHandler(newNodeSocket);
        ControlPanelClientHandler newControlPanelHandler =
            new ControlPanelClientHandler(newControlPanelSocket);
        this.SensorActuatorNodes.add(newNodeHandler);
        this.ControlPanels.add(newControlPanelHandler);

        new Thread(() -> {
          System.out.println(
              "SensorActuatorNodes = " + this.SensorActuatorNodes.size() + " ControlPanels = " +
                  this.ControlPanels.size());

          newNodeHandler.sendMessage("Hello #" + this.SensorActuatorNodes.size());
          newControlPanelHandler.sendMessage("Hello #" + this.ControlPanels.size());

          // Handle client for socket lifetime
          while (newNodeSocket.isConnected()) {
            newNodeHandler.handleClient();
          }
          // Handle client for socket lifetime
          while (newControlPanelSocket.isConnected()) {
            newControlPanelHandler.handleClient();
          }
        }).start();
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    }
  }

}
