package no.ntnu.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import no.ntnu.tools.Logger;

/**
 * Class representing a server accepting incoming connection requests
 */
public class Server {
  private static Server instance;
  private ServerSocket serverSocket;
  private List<ControlPanelClientHandler> controlPanels;
  private List<NodeClientHandler> nodes;


  /**
   * Constructor of server
   *
   * @param port port to listen for clients
   */
  public Server(int port) {
    this.nodes = new ArrayList<>();
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
    while (true) {
      System.out.println("Looking for new client...");

      try {
        Socket newSocket = this.serverSocket.accept();
        System.out.println("New client connected: " + newSocket.getRemoteSocketAddress());
        BufferedReader inputReader =
            new BufferedReader(new InputStreamReader(newSocket.getInputStream()));
        String input = inputReader.readLine();
        if (input.equals("Control panel")) {
          ControlPanelClientHandler newControlPanelHandler =
              new ControlPanelClientHandler(newSocket, this);
          this.controlPanels.add(newControlPanelHandler);
          new Thread(() -> {
            System.out.println(
                "Control panels = " + this.controlPanels.size() + ". Nodes = " + this.nodes.size() +
                    ".");

            newControlPanelHandler.sendMessage("Hello #" + this.controlPanels.size());

            // Handle client for socket lifetime
            while (newSocket.isConnected()) {
              newControlPanelHandler.handleClient();
            }
          }).start();
        } else if (input.equals("Node")) {
          NodeClientHandler newNodeHandler = new NodeClientHandler(newSocket);
          this.nodes.add(newNodeHandler);
          new Thread(() -> {
            System.out.println(
                "Control panels = " + this.controlPanels.size() + ". Nodes = " + this.nodes.size() +
                    ".");

            newNodeHandler.sendMessage("Hello #" + this.nodes.size());

            // Handle client for socket lifetime
            while (newSocket.isConnected()) {
              newNodeHandler.handleClient();
            }
          }).start();
        } else {
          Logger.error("Unknown client type");
          continue;
        }
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    }
  }

  public void sendMessagesToNodes(String message) {
    for (NodeClientHandler node : this.nodes) {
      node.sendMessage(message);
    }
  }
}
