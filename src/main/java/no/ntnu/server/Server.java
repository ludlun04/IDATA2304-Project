package no.ntnu.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import no.ntnu.tools.Logger;

/**
 * Class representing a server accepting incoming connection requests
 */
public abstract class Server {
  private ServerSocket serverSocket;
  private List<ControlPanelClientHandler> handlers;

  /**
   * Constructor of server
   *
   * @param port port to listen for clients
   */
  public Server(int port) {
    this.handlers = new ArrayList<>();

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

        ControlPanelClientHandler newHandler = getClientHandler(newSocket);
        this.handlers.add(newHandler);

        new Thread(() -> {
          Logger.info("Clients = " + this.handlers.size());

          // Handle client for socket lifetime
          while (newSocket.isConnected()) {
            newHandler.handleCommunication();
          }
        }).start();
      } catch (IOException e) {
        Logger.error(e.getMessage());
        finished = true;
      }
    }
  }

  public void stop() {
    Logger.info("Server stopping");
    try {
      this.serverSocket.close();
    } catch (Exception e) {
      Logger.error("failed to close communication due to:");
      Logger.error(e.getMessage());
    }

    for (ControlPanelClientHandler controlPanelClientHandler : handlers) {
      controlPanelClientHandler.closeConnection();
    }
  }

  protected abstract ControlPanelClientHandler getClientHandler(Socket socket);
}
