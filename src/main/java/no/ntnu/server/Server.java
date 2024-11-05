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
  private ServerSocket serverSocket;
  private List<ControlPanelClientHandler> ControlPanels;


  /**
   * Constructor of server
   *
   * @param port port to listen for clients
   */
  public Server(int port) {
    this.ControlPanels = new ArrayList<>();
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
      System.out.println("Looking for new client...");

      try {
        Socket newSocket = this.serverSocket.accept();
        ControlPanelClientHandler newControlPanelHandler =
            new ControlPanelClientHandler(newSocket);
        this.ControlPanels.add(newControlPanelHandler);

        new Thread(() -> {
          System.out.println("Clients = " + this.ControlPanels.size());

          newControlPanelHandler.sendMessage("Hello #" + this.ControlPanels.size());

          // Handle client for socket lifetime
          while (newSocket.isConnected()) {
            newControlPanelHandler.handleClient();
          }
        }).start();
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
      finished = true;
    }
  }

}
