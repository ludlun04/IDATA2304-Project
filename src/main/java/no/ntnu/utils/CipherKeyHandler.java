package no.ntnu.utils;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import no.ntnu.tools.Logger;

/**
 * Class for handling encryption and decryption of messages using AES.
 *
 * <p>Uses an AES key for encryption and decryption.
 */
public class CipherKeyHandler {
  private final SecretKey aesKey;

  /**
   * Creates a new CipherKeyHandler with the given AES key.
   *
   * @param aesKey The AES key to use for encryption and decryption
   */
  public CipherKeyHandler(SecretKey aesKey) {
    this.aesKey = aesKey;
  }

  /**
   * Get a new random AES key.
   *
   * @return A new random AES key
   */
  public static SecretKey getNewRandomAesKey() {
    SecretKey secretKey = null;
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(256);
      secretKey = keyGenerator.generateKey();
    } catch (NoSuchAlgorithmException e) {
      Logger.error(e.getMessage());
    }
    return secretKey;
  }

  /**
   * Get an encrypted message from a given plaintext message.
   *
   * @param message The message to encrypt
   * @return The encrypted message
   */
  public String encryptMessageAes(String message) {
    String encryptedMessage = null;
    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, this.aesKey);
      byte[] encryptedBytes = cipher.doFinal(message.getBytes());
      encryptedMessage = Base64.getEncoder().encodeToString(encryptedBytes);
    } catch (Exception e) {
      Logger.error("AES encryption: " + e.getMessage());
    }
    return encryptedMessage;
  }

  /**
   * Get a decrypted message from a given encrypted message.
   *
   * @param encryptedMessage The message to decrypt
   * @return The decrypted message
   */
  public String decryptMessageAes(String encryptedMessage) {
    String decryptedMessage = null;
    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, this.aesKey);
      byte[] decodedMessage = Base64.getDecoder().decode(encryptedMessage);
      byte[] decryptedBytes = cipher.doFinal(decodedMessage);
      decryptedMessage = new String(decryptedBytes);
    } catch (Exception e) {
      Logger.error("AES decryption: " + e.getMessage());
    }
    return decryptedMessage;
  }
}
