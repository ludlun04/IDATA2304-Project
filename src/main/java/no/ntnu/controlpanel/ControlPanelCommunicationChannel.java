package no.ntnu.controlpanel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.tools.Logger;

public class ControlPanelCommunicationChannel implements CommunicationChannel {
    private ControlPanelLogic logic;
    private PrintWriter writer;
    private BufferedReader reader;
    private Socket socket;

    public ControlPanelCommunicationChannel(ControlPanelLogic logic) {
        if (logic == null) {
            throw new IllegalArgumentException("logic cannot be null");
        }
        this.logic = logic;
      try {
        this.socket = new Socket("127.0.0.1", 8765);
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      } catch (IOException e) {
        Logger.error(e.getMessage());
      }
    }

    public void sendInitialDataRequest() {
        this.writer.println("initialData");
    }

    @Override
    public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
        this.writer.println(String.format("set %d %d %b", nodeId, actuatorId, isOn));
    }

    /**
     *
     * Sends a message to the server to add a sensor to a node
     * @param nodeId id of node to have a sensor added
     * @param sensorType type of sensor
     * @param min min value the sensor can register
     * @param max max value the sensor can register
     * @param current current value the sensor reads
     * @param unit unit the sensor reads in
     * @param amount amount of sensors to add
     */
    public void addSensor(int nodeId, String sensorType, int min, int max, int current, String unit, int amount) {
        this.writer.println(String.format("add sensor %d %s %d %d %d %s %d",
            nodeId, sensorType, min, max, current, unit, amount));
    }

    /**
     * Sends a message to the server to add an actuator to a node
     * @param nodeId id of node to have an actuator added
     * @param actuatorType type of actuator
     */
    public void addActuator(int nodeId, String actuatorType) {
        this.writer.println(String.format("add actuator %d %s", nodeId, actuatorType));
    }

    @Override
    public boolean open() {
        boolean connected = false;
        Logger.info("Connecting to socket");
            sendInitialDataRequest();

            new Thread(() -> {
                handleCommunication();
            }).start();

            connected = true;

        return connected;
    }

    public void handleCommunication(){
      try {
        String message = reader.readLine();

        while (message != null) {
          String[] args = message.split(" ");

          String command = args[0];
          int nodeId = Integer.parseInt(args[1]);

          switch (command) {
            case "add" -> {

              SensorActuatorNodeInfo sensorActuatorNodeInfo =
                  new SensorActuatorNodeInfo(nodeId);

              for (int i = 2; i < args.length; i += 2) {
                int actuatorId = Integer.parseInt(args[i]);
                String actuatorType = args[i + 1];

                Actuator actuator = new Actuator(actuatorId, actuatorType, nodeId);
                actuator.setListener(this.logic);
                sensorActuatorNodeInfo.addActuator(actuator);
              }

              this.logic.onNodeAdded(sensorActuatorNodeInfo);
            }
            case "remove" -> this.logic.onNodeRemoved(nodeId);
            case "updateSensorsInformation" -> {
              ArrayList<SensorReading> readings = new ArrayList<>();

              for (int i = 2; i < args.length; i += 3) {
                String sensorType = args[i];
                Double value = Double.parseDouble(args[i + 1]);
                String unit = args[i + 2];

                SensorReading sensorReading =
                    new SensorReading(sensorType, value, unit);

                readings.add(sensorReading);
              }

              this.logic.onSensorData(nodeId, readings);
            }
            case "updateActuatorInformation" -> {
              int actuatorId = Integer.parseInt(args[2]);
              boolean state = Boolean.parseBoolean(args[3]);

              this.logic.onActuatorStateChanged(nodeId, actuatorId, state);
            }
            default -> Logger.info("Unknown command: " + command);
          }

          message = reader.readLine();
        }
      } catch (Exception e) {
        Logger.error("Internal Error:");
        Logger.error(e.getMessage());
      }
    }

    @Override
    public void close() {
        this.writer.println("close");
        this.writer.close();
        try {
            this.reader.close();
            this.socket.close();
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }

}
