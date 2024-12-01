package no.ntnu.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import no.ntnu.tools.Logger;

/**
 * Class representing a server accepting incoming connection requests. The server is abstract and
 * must be extended to implement the logic for handling incoming connections.
 */
public abstract class Server {
  private final ServerSocket serverSocket;

  /**
   * Creates a new server using the specified port.
   *
   * @param port port to listen for clients
   */
  public Server(int port) {
    try {
      this.serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      Logger.error("Could not establish server socket");
      throw new RuntimeException(e);
    }
  }

  /**
   * Runs the server.
   *
   * <p>Waits for clients and listens to commands.
   */
  public void run() {
    boolean finished = false;
    while (!finished) {
      Logger.info("Looking for new client...");

      try {
        Socket newSocket = this.serverSocket.accept();
        this.socketConnected(newSocket);
      } catch (IOException e) {
        Logger.error(e.getMessage());
        finished = true;
      }
    }
  }

  /**
   * Method to be called when a new socket is connected.
   *
   * @param socket the socket that is connected
   */
  protected abstract void socketConnected(Socket socket);
}
