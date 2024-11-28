package no.ntnu.run;

import no.ntnu.server.GreenHouseServer;

public class GreenhouseServerStarter {
  public static void main(String[] args) {
    GreenHouseServer server = new GreenHouseServer(8765);
    server.start();
  }
}
