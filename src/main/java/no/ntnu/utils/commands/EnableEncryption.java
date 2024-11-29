package no.ntnu.utils.commands;

import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import no.ntnu.utils.CommunicationHandler;


/**
 * Command for enabling encryption.
 */
public class EnableEncryption extends Command {

  private CommunicationHandler handler;
  private String keyEncoded;

  /**
   * Creates a new instance of the EnableEncryption command.
   *
   * @param handler The communication handler to use.
   * @param keyEncoded The key to use for encryption.
   */
  public EnableEncryption(CommunicationHandler handler, String keyEncoded) {
    this.handler = handler;
    this.keyEncoded = keyEncoded;
  }

  /**
   * Executes the command. Enables encryption with the given key. Sends an OK message.
   */
  @Override
  public void execute() {
    byte[] keyDecoded = Base64.getDecoder().decode(this.keyEncoded);
    SecretKey key = new SecretKeySpec(keyDecoded, "AES");
    this.handler.enableEncryptionwithKey(key);
    this.handler.sendEncryptedMessage("OK");
  }
}
