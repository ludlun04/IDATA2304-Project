package no.ntnu.server;

public class ServerStarter {
  public static void main(String[] args) {
    Server server = new Server(1234);
    server.run();
  }
}
