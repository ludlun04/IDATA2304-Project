package no.ntnu.run;

import no.ntnu.server.GreenHouseServer;

/**
 * Starter for the Greenhouse server.
 */
public class GreenhouseServerStarter {

  /**
   * Entrypoint for the Greenhouse server. Starts the server.
   *
   * @param args Command line arguments, not used.
   */
  public static void main(String[] args) {
    GreenHouseServer server = new GreenHouseServer(8765);
    server.start();
  }
}
