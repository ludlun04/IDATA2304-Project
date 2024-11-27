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
  private static CipherKeyHandler instance;

  public CipherKeyHandler() {
    generateAESKey();
  }

  public static synchronized CipherKeyHandler getInstance() {
    if (instance == null) {
      instance = new CipherKeyHandler();
    }
    return instance;
  }

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
