package no.ntnu.controlpanel.networking.Commands;

import no.ntnu.utils.CommunicationHandler;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EnableEncryption extends Command {

    private CommunicationHandler handler;
    private String keyEncoded;

    public EnableEncryption(CommunicationHandler handler, String keyEncoded) {
        this.handler = handler;
        this.keyEncoded = keyEncoded;
    }

    @Override
    public void execute() {
        byte[] keyDecoded = Base64.getDecoder().decode(this.keyEncoded);
        SecretKey key = new SecretKeySpec(keyDecoded, "AES");
        this.handler.enableEncryptionwithKey(key);
        this.handler.sendEncryptedMessage("OK");
    }
}
