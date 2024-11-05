package no.ntnu.controlpanel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import no.ntnu.greenhouse.Actuator;
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
        System.out.println("Connecting to socket");
        try {
            Socket socket = new Socket("127.0.0.1", 8765);
            System.out.println("Socket connected");
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.writer = writer;

            ControlPanelCommunicationChannel comChannel = this;

            new Thread() {
                @Override
                public void run() {
                    try {
                        String message = reader.readLine();

                        while (message != null) {
                            String[] args = message.split(" ");

                            String command = args[0];
                            int nodeId = Integer.parseInt(args[1]);

                            if (command.equals("add")) {

                                SensorActuatorNodeInfo sensorActuatorNodeInfo = new SensorActuatorNodeInfo(nodeId);

                                for (int i = 2; i < args.length; i += 2) {
                                    int actuatorId = Integer.parseInt(args[i]);
                                    String actuatorType = args[i + 1];

                                    Actuator actuator = new Actuator(actuatorId, actuatorType, nodeId);
                                    actuator.setListener(logic);
                                    sensorActuatorNodeInfo.addActuator(actuator);
                                }

                                comChannel.logic.onNodeAdded(sensorActuatorNodeInfo);

                            } else if (command.equals("remove")) {
                                comChannel.logic.onNodeRemoved(nodeId);
                            } else if (command.equals("updateSensorsInformation")) {
                                ArrayList<SensorReading> readings = new ArrayList<>();

                                System.out.println("Parsing sensors");

                                for (int i = 2; i < args.length; i += 3) {
                                    String sensorType = args[i];
                                    Double value = Double.parseDouble(args[i + 1]);
                                    String unit = args[i + 2];

                                    SensorReading sensorReading = new SensorReading(sensorType, value, unit);

                                    readings.add(sensorReading);
                                }

                                comChannel.logic.onSensorData(nodeId, readings);

                            } else if (command.equals("updateActuatorInformation")) {
                                int actuatorId = Integer.parseInt(args[2]);
                                boolean state = Boolean.parseBoolean(args[3]);

                                comChannel.logic.onActuatorStateChanged(nodeId, actuatorId, state);
                            }

                            message = reader.readLine();
                        }
                    } catch (Exception e) {
                        System.out.println("Internal Error:");
                        System.out.println(e.getMessage());
                    }
                }
            }.start();

            connected = true;
        } catch (Exception e) {
            System.out.println("External error:");
            System.out.println(e.getMessage());
        }

        return connected;
    }

}
