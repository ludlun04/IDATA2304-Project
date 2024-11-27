package no.ntnu.utils;

import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import no.ntnu.tools.Logger;

public class CipherKeyHandler {
  private SecretKey aesKey;

  public CipherKeyHandler() {
    generateAESKey();
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

  public SecretKey getAESKey() {
    return this.aesKey;
  }

  public void setAESKey(SecretKey key) {
    this.aesKey = key;
  }

  public void encryptMessageAES(String message) {
    // TODO - implement encryption
  }

  public void decryptMessageAES(String message) {
    // TODO - implement decryption
  }
}
