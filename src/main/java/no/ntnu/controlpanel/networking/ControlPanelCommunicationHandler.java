package no.ntnu.controlpanel.networking;

import no.ntnu.controlpanel.networking.Commands.Command;
import no.ntnu.tools.Logger;
import no.ntnu.utils.CommunicationHandler;

import java.io.IOException;

/**
 * Handler used by {@link ControlPanelCommunicationChannel} to exchange
 * encryption keys and send encrypted messages.
 */
public class ControlPanelCommunicationHandler {

    private CommandParser commandParser;
    private CommunicationHandler handler;

    public ControlPanelCommunicationHandler(CommunicationHandler handler, CommandParser commandParser)
            throws IOException {
        this.handler = handler;
        this.commandParser = commandParser;
    }

    /**
     * Sends an unencrypted message to the client.
     * 
     * @param message String to be sent to the client
     */
    public void sendMessage(String message) {
        this.handler.sendMessage(message);
    }

    /**
     * Send an encrypted message to client.
     * Presupposes encryption keys are already exchanged.
     *
     * @param message String to be sent to client
     */
    public void sendEncryptedMessage(String message) {
        this.handler.sendEncryptedMessage(message);
    }

    /**
     * Get received unencrypted message.
     * 
     * @return message as String
     */
    public String getMessage() {
        return this.handler.getMessage();
    }

    /**
     * Get received message after decrytion.
     *
     * @return message as string
     */
    public String getDecryptedMessage() {
        return this.handler.getDecryptedMessage();
    }

    /**
     * Decrypt and pass message on to command evaluation
     *
     * @throws IOException
     */
    public void handleEncryptedMessage() throws IOException {
        String message = this.handler.getDecryptedMessage();

        handlePlainMessage(message);
    }

    /**
     * Pass unencrypted message on to command evaluation
     *
     * @throws IOException
     */
    public void handleMessage() throws IOException {
        String message = this.handler.getMessage();

        handlePlainMessage(message);
    }

    /**
     * Evaluate plain message as a command
     *
     * @param message the message to consider
     * @throws IOException when the message is null
     */
    private void handlePlainMessage(String message) throws IOException {
        if (message != null) {
            Command command = null;
            try {
                command = this.commandParser.parse(message);
            } catch (IllegalArgumentException e) {
                Logger.error("Failed to parse command. " + e.getMessage());
            }

            if (command != null) {
                command.execute();
            }
        } else {
            throw new IOException("Communication interrupted");
        }
    }

    /**
     * Close this {@link ControlPanelCommunicationChannel}
     */
    public void close() {
        this.handler.close();
    }
}
