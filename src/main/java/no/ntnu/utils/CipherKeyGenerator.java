package no.ntnu.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import no.ntnu.tools.Logger;

public class CipherKeyGenerator {
  private PrivateKey privateKey;
  private PublicKey publicKey;
  private SecretKey aesKey;

  public CipherKeyGenerator() {
    generateRSAKeyPair();
    generateAESKey();
  }

  private void generateRSAKeyPair() {
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      KeyPair keyPair = keyPairGenerator.generateKeyPair();
      this.privateKey = keyPair.getPrivate();
      this.publicKey = keyPair.getPublic();
    } catch (NoSuchAlgorithmException e) {
      Logger.error(e.getMessage());
    }
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

  public PrivateKey getPrivateKey() {
    return this.privateKey;
  }

  public PublicKey getPublicKey() {
    return this.publicKey;
  }

  public SecretKey getAESKey() {
    return this.aesKey;
  }
}
