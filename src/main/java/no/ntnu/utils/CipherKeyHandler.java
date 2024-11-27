package no.ntnu.utils;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import no.ntnu.tools.Logger;

public class CipherKeyHandler {
  private SecretKey aesKey;

  public CipherKeyHandler() {
    generateAESKey();
  }

  /**
   * Generates a new AES key
   */
  private void generateAESKey() {
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(256);
      SecretKey secretKey = keyGenerator.generateKey();
      this.aesKey = secretKey;
    } catch (NoSuchAlgorithmException e) {
      Logger.error(e.getMessage());
    }
  }

  /**
   * Returns the AES key
   *
   * @return the AES key
   */
  public SecretKey getAESKey() {
    return this.aesKey;
  }

  /**
   * Sets the AES key
   *
   * @param key the AES key
   */
  public void setAESKey(SecretKey key) {
    this.aesKey = key;
  }

  /**
   * Encrypts a message using AES
   *
   * @param message the message to encrypt
   * @return the encrypted message
   */
  public String encryptMessageAES(String message) {
    String encryptedMessage = null;
    try {
      Cipher ciper = Cipher.getInstance("AES");
      ciper.init(Cipher.ENCRYPT_MODE, this.aesKey);
      byte[] encryptedBytes = ciper.doFinal(message.getBytes());
      encryptedMessage = Base64.getEncoder().encodeToString(encryptedBytes);
    } catch (Exception e) {
      Logger.error("AES encryption error: " + e.getMessage());
    }
    return encryptedMessage;
  }

  /**
   * Decrypts a message using AES
   *
   * @param message the message to decrypt
   * @return the decrypted message
   */
  public String decryptMessageAES(String message) {
    String decryptedMessage = null;
    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, this.aesKey);
      byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(message));
      decryptedMessage = new String(decryptedBytes);
    } catch (Exception e) {
      Logger.error("AES decryption error: " + e.getMessage());
    }
    return decryptedMessage;
  }
}
