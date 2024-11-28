package no.ntnu.controlpanel.networking;

import java.io.IOException;
import java.net.Socket;

import no.ntnu.controlpanel.CommunicationChannel;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;

/**
 * Handles all network communication for control panels
 */
public class ControlPanelCommunicationChannel implements CommunicationChannel {

    // less than zero means infinite attempts
    private static final int RECONNECT_ATTEMPTS = -1;

    private static final int RECONNECT_ATTEMPT_WAIT_MILLIS = 5000;
    private static final int HANDSHAKE_WAIT_MILLIS = 1000;

    private boolean handShakeCompleted;
    private boolean stayConnected;
    private ControlPanelCommunicationHandler handler;
    private ControlPanelLogic logic;

    /**
     * Constructs a {@link ControlPanelCommunicationChannel} object
     * 
     * @param logic
     * @throws IOException
     */
    public ControlPanelCommunicationChannel(ControlPanelLogic logic) throws IOException {
        if (logic == null) {
            throw new IllegalArgumentException("logic cannot be null");
        }
        this.logic = logic;
        this.handShakeCompleted = false;
    }

    /**
     * Coninuusly attempts to establish connection to server
     */
    public void performHandshake() {

        this.handShakeCompleted = false;
        new Thread(() -> {
            while (!handShakeCompleted) {
                this.handler.sendMessage("I am controlpanel");
                try {
                    Thread.sleep(HANDSHAKE_WAIT_MILLIS);
                } catch (InterruptedException e) {
                    Logger.error("Could not sleep during ping");
                }
            }
        }).start();

        String expectedResponse = "Hello from server";
        String response = null;

        while (!expectedResponse.equals(response)) {
            response = this.handler.getMessage();
        }

        this.handShakeCompleted = true;
    }

    @Override
    public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
        this.handler.sendEncryptedMessage(String.format("set %d %d %b", nodeId, actuatorId, isOn));
    }

    /**
     * Sends a message to the server to add a sensor to a node
     *
     * @param nodeId     id of node to have a sensor added
     * @param sensorType type of sensor
     * @param min        min value the sensor can register
     * @param max        max value the sensor can register
     * @param current    current value the sensor reads
     * @param unit       unit the sensor reads in
     * @param amount     amount of sensors to add
     */
    public void addSensor(int nodeId, String sensorType, int min, int max, int current, String unit, int amount) {
        this.handler.sendEncryptedMessage(String.format("add sensor %d %s %d %d %d %s %d",
                nodeId, sensorType, min, max, current, unit, amount));
    }

    /**
     * Sends a message to the server to add an actuator to a node
     *
     * @param nodeId       id of node to have an actuator added
     * @param actuatorType type of actuator
     */
    public void addActuator(int nodeId, String actuatorType) {
        this.handler.sendEncryptedMessage(String.format("add actuator %d %s", nodeId, actuatorType));
    }

    @Override
    public boolean open() {
        this.stayConnected = true;
        Logger.info("Connecting to socket");

        attemptConnect();
        new Thread(() -> {
            while (this.stayConnected) {
                try {
                    this.handler.handleEncryptedMessage();
                } catch (IOException | NullPointerException exception) {
                    Logger.error("Lost connection. " + exception.getMessage());
                    handleAttemptedReconnection();
                }

            }
        }).start();

        return stayConnected;
    }

    /**
     * Handles reconnecting to the server.
     * Used if the connection is lost.
     */
    private void handleAttemptedReconnection() {
        stayConnected = attemptConnect();
        if (!stayConnected) {
            Logger.info("Could not connect, stopping communication channel.");
            this.stayConnected = false;
            this.logic.onCommunicationChannelClosed();

        }
    }

    /**
     * Creates and registers a {@link CommunicationHandler} used for various
     * purposes in the class.
     *
     * @throws IOException
     */
    private void createHandler() throws IOException {
        CommunicationHandler communicationHandler = new CommunicationHandler(new Socket("127.0.0.1", 8765));
        this.handler = new ControlPanelCommunicationHandler(communicationHandler,
                new CommandParser(logic, communicationHandler));

        performHandshake();
        this.handler.handleMessage(); // needed to establish encryption
    }

    /**
     * Attempt reconnecting to the server
     *
     * @return true if reconnecting was successful
     */
    private boolean attemptConnect() {
        int tries = RECONNECT_ATTEMPTS;
        boolean result = false;

        while (tries != 0 && !result) {

            try {
                createHandler();
                result = true;
                Logger.info("Successfully connected to server");
            } catch (IOException ioException) {

                if (RECONNECT_ATTEMPTS < 0) {
                    Logger.info("Attempting to connect...");
                } else {
                    Logger.info("Attempting to connect, " + tries + " tries left...");
                }
                wait(RECONNECT_ATTEMPT_WAIT_MILLIS);

                if (tries > 0) {
                    tries--;
                }
            }

        }
        return result;
    }

    /**
     * A simple way to block code from executing a certain amount of time.
     *
     * @param delayMillis how long to block
     */
    private static void wait(int delayMillis) {

        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Logger.error("Could not sleep");
        }
    }

    @Override
    public void close() {
        this.stayConnected = false;
        this.handler.close();
    }
}
