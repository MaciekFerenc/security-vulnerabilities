package com.mferenc.springboottemplate.encryption;

import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


@Service
public class EncryptionService {
    private final KeyManager keyManager;
    private final static String algorithm = "AES";

    public EncryptionService(KeyManager keyManager) {
        this.keyManager = keyManager;
    }

    public String encrypt(String input) throws Exception {
        var key = buildKey();
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64
                .getEncoder()
                .encodeToString(cipher.doFinal(input.getBytes()));
    }

    public String decrypt(String encrypted) throws Exception {
        var key = buildKey();
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(
                cipher.doFinal(Base64.getDecoder().decode(encrypted))
        );
    }

    private SecretKeySpec buildKey() {
        return new SecretKeySpec(
                keyManager.getEncryptionKey().getBytes(), algorithm);
    }
}