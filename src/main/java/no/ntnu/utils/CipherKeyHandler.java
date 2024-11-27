package no.ntnu.utils;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import no.ntnu.tools.Logger;

public class CipherKeyHandler {
  private SecretKey aesKey;

  public CipherKeyHandler(SecretKey aesKey) {
    this.aesKey = aesKey;
  }

  public static SecretKey getNewRandomAESKey() {
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

  public String encryptMessageAES(String message) {
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

  public String decryptMessageAES(String encryptedMessage) {
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
