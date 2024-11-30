package no.ntnu.utils;

import java.util.ArrayDeque;
import java.util.List;
import no.ntnu.controlpanel.networking.NoSuchCommand;
import no.ntnu.utils.commands.Command;
import no.ntnu.utils.commands.EnableEncryption;

/**
 * Abstract class for parsing commands from messages.
 *
 * Used by GreenhouseCommandParser.java and ControlPanelCommandParser.java
 */
public abstract class CommandParser {
  protected CommunicationHandler handler;

  /**
   * Create a new command parser.
   *
   * @param handler the communication handler
   */
  public CommandParser(CommunicationHandler handler) {
    this.handler = handler;
  }

    /**
     * Takes in a message and returns a command based on the parsed contents of the message.
     *
     * @param message the message to be parsed
     * @return returns the corresponding command
     * @throws IllegalArgumentException if the message is not a valid command
     */
  public  Command parse(String message) throws IllegalArgumentException {
    Command command = null;

    List<String> strings = List.of(message.split(" "));
    ArrayDeque<String> args = new ArrayDeque<>(strings);

    String commandWord = args.poll();

    if (commandWord.equals("Encrypt")) {
      String keyEncoded = String.valueOf(args.poll());
      command = new EnableEncryption(handler, keyEncoded);
    } else {
      command = parseSpecificCommand(commandWord, args);
    }

    return command;
  }

  /**
   * Takes in a message and returns a command based on the parsed contents of the message.
   *
   * @param commandWord the command to be executed
   * @param args        the arguments for the command
   * @return returns the corresponding command
   * @throws NoSuchCommand    exception if command isn't found or other exception
   * @throws RuntimeException if something else goes wrong during parsing of the message
   */
    protected abstract Command parseSpecificCommand(String commandWord, ArrayDeque<String> args);
}
