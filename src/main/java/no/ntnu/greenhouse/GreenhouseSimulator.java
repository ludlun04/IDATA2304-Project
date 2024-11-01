package no.ntnu.greenhouse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.tools.Logger;

/**
 * Application entrypoint - a simulator for a greenhouse.
 */
public class GreenhouseSimulator {
    private final Map<Integer, SensorActuatorNode> nodes = new HashMap<>();

    private final List<PeriodicSwitch> periodicSwitches = new LinkedList<>();
    private final boolean fake;

    /**
     * Create a greenhouse simulator.
     *
     * @param fake When true, simulate a fake periodic events instead of creating
     *             socket communication
     */
    public GreenhouseSimulator(boolean fake) {
        this.fake = fake;
    }

    /**
     * Initialise the greenhouse but don't start the simulation just yet.
     */
    public void initialize() {
        createNode(1, 2, 1, 0, 0);
        createNode(1, 0, 0, 2, 1);
        createNode(2, 0, 0, 0, 0);
        Logger.info("Greenhouse initialized");
    }

    private void createNode(int temperature, int humidity, int windows, int fans, int heaters) {
        SensorActuatorNode node = DeviceFactory.createNode(
                temperature, humidity, windows, fans, heaters);
        nodes.put(node.getId(), node);
    }

    /**
     * Start a simulation of a greenhouse - all the sensor and actuator nodes inside
     * it.
     */
    public void start() {
        initiateCommunication();
        for (SensorActuatorNode node : nodes.values()) {
            node.start();
        }
        for (PeriodicSwitch periodicSwitch : periodicSwitches) {
            periodicSwitch.start();
        }

        Logger.info("Simulator started");
    }

    private void initiateCommunication() {
        if (fake) {
            initiateFakePeriodicSwitches();
        } else {
            initiateRealCommunication();
        }
    }

    private void initializeClient(PrintWriter writer) {
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            // TODO: handle exception
            System.err.println("Failed to sleep: " + e.getMessage());
        }

        for (SensorActuatorNode node : this.nodes.values()) {
            sendNodeInformation(node, writer);
            initializeSensorListeners(writer, node);
            initializeActuatorListeners(writer, node);
        }
    }

    private String listenForClientMessage(Socket clientSocket) {
        InputStream inputStream = null;
        try {
            inputStream = clientSocket.getInputStream();
        } catch (IOException ioException) {
            System.err.println("Failed to get input stream: " + ioException.getMessage());
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        try {
            return reader.readLine();
        } catch (IOException ioException) {
            System.err.println("Failed to read client message: " + ioException.getMessage());
        }
        return null;

    }

    private static void initializeActuatorListeners(PrintWriter writer, SensorActuatorNode node) {
        node.addActuatorListener((int nodeID, Actuator actuator) -> {
            writer.println(String.format(
                    "updateActuatorInformation %d %d %b", nodeID,
                    actuator.getId(),
                    actuator.isOn()));
            writer.flush();
        });
    }

    private void initializeSensorListeners(PrintWriter writer, SensorActuatorNode node) {
        node.addSensorListener((List<Sensor> sensors) -> {
            writer.print(String.format(
                    "updateSensorsInformation %d", node.getId()));
            for (Sensor sensor : sensors) {
                String type = sensor.getType();
                SensorReading reading = sensor.getReading();
                writer.print(String.format(" %s %f %s", type,
                        reading.getValue(),
                        reading.getUnit()));
            }
            writer.print("\n");
            writer.flush();
        });
    }


    private PrintWriter getClientPrintWriter(Socket clientSocket) {

        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Failed to get client print writer");
        }
        return printWriter;
    }

    private static void sendNodeInformation(SensorActuatorNode node, PrintWriter writer) {
        writer.print("add " + node.getId());

        for (Actuator actuator : node.getActuators()) {
            writer.print(String.format(" %d %s", actuator.getId(), actuator.getType()));
        }

        writer.println("");

        writer.flush();
    }

    private void initiateRealCommunication() {
        // TODO - here you can set up the TCP or UDP communication

        try {
            ServerSocket server = new ServerSocket(8765);

            new Thread(() -> handleClient(server)).start();
        }

        catch (IOException e) {
            // ehe
        }
    }

    private void handleClient(ServerSocket server) {
        Socket clientSocket = null;
        try {
            clientSocket = server.accept();
        } catch (IOException e) {
            System.err.println("Failed to accept client connection" + e.getMessage());;
            throw new RuntimeException();
        }

        PrintWriter clientWriter = getClientPrintWriter(clientSocket);
        initializeClient(clientWriter);

        while (clientSocket.isConnected()) {
            String message = listenForClientMessage(clientSocket);
            System.out.println("Client message:" + message);
        }

    }

    private void initiateFakePeriodicSwitches() {
        periodicSwitches.add(new PeriodicSwitch("Window DJ", nodes.get(1), 2, 20000));
        periodicSwitches.add(new PeriodicSwitch("Heater DJ", nodes.get(2), 7, 8000));
    }

    /**
     * Stop the simulation of the greenhouse - all the nodes in it.
     */
    public void stop() {
        stopCommunication();
        for (SensorActuatorNode node : nodes.values()) {
            node.stop();
        }
    }

    private void stopCommunication() {
        if (fake) {
            for (PeriodicSwitch periodicSwitch : periodicSwitches) {
                periodicSwitch.stop();
            }
        } else {
            // TODO - here you stop the TCP/UDP communication
        }
    }

    /**
     * Add a listener for notification of node staring and stopping.
     *
     * @param listener The listener which will receive notifications
     */
    public void subscribeToLifecycleUpdates(NodeStateListener listener) {
        for (SensorActuatorNode node : nodes.values()) {
            node.addStateListener(listener);
        }
    }
}
