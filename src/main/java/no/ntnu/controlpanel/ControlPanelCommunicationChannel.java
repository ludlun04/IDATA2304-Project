package no.ntnu.controlpanel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import no.ntnu.greenhouse.SensorReading;

public class ControlPanelCommunicationChannel implements CommunicationChannel {
    private ControlPanelLogic logic;
    private PrintWriter writer;

    public ControlPanelCommunicationChannel(ControlPanelLogic logic) {
        if (logic == null) {
            throw new IllegalArgumentException("logic cannot be null");
        }

        this.logic = logic;
    }

    @Override
    public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
        this.writer.println(String.format("set %d %d %b", nodeId, actuatorId, isOn));
        this.writer.flush();
    }

    @Override
    public boolean open() {
        boolean connected = false;

        try (Socket socket = new Socket("127.0.0.1", 8765)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.writer = writer;

            ControlPanelCommunicationChannel comChannel = this;

            new Thread() {
                @Override
                public void run() {
                    String message = "";

                    try {
                        while ((message = reader.readLine()) != null) {
                            String[] args = message.split(" ");

                            String command = args[0];
                            int nodeId = Integer.parseInt(args[1]);

                            if (command == "add") {
                                comChannel.logic.onNodeAdded(new SensorActuatorNodeInfo(nodeId));
                            } else if (command == "remove") {
                                comChannel.logic.onNodeRemoved(nodeId);
                            } else if (command == "updateSensors") {
                                ArrayList<SensorReading> readings = new ArrayList<>();
                                
                                for (int i = 2; i < args.length; i += 3) {
                                    String sensorType = args[i];
                                    Double value = Double.parseDouble(args[i + 1]); 
                                    String unit = args[i + 2];

                                    SensorReading sensorReading = new SensorReading(sensorType, value, unit);

                                    readings.add(sensorReading);
                                }

                                comChannel.logic.onSensorData(nodeId, readings);
                            } else if (command == "updateActuator") {
                                int actuatorId = Integer.parseInt(args[2]);
                                boolean state = Boolean.parseBoolean(args[3]);

                                comChannel.logic.onActuatorStateChanged(nodeId, actuatorId, state);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }.start();

            connected = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return connected;
    }

}
