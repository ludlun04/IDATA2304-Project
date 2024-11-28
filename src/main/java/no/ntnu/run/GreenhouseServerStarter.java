package no.ntnu.run;

import no.ntnu.server.GreenHouseServer;

/**
 * A class that starts a {@link GreenHouseServer}
 */
public class GreenhouseServerStarter {
  public static void main(String[] args) {
    GreenHouseServer server = new GreenHouseServer(8765);
    server.start();
  }
}
