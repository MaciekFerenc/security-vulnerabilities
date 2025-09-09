package com.mferenc.springboottemplate.encryption;

import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    private final KeyManager keyManager;
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // bytes
    private static final int GCM_TAG_LENGTH = 16; // bytes

    public EncryptionService(KeyManager keyManager) {
        this.keyManager = keyManager;
    }

    public String encrypt(String input) throws Exception {
        var key = buildKey();
        byte[] iv = generateIV();
        Cipher cipher = createCipher(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedData = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(combineIvAndData(iv, encryptedData));
    }

    public String decrypt(String encrypted) throws Exception {
        var key = buildKey();
        byte[] decodedData = Base64.getDecoder().decode(encrypted);
        validateDataLength(decodedData);
        byte[] iv = extractIV(decodedData);
        byte[] encryptedData = extractEncryptedData(decodedData);
        Cipher cipher = createCipher(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    private SecretKeySpec buildKey() {
        byte[] keyBytes = keyManager.getEncryptionKey().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("Invalid key length: " + keyBytes.length + " bytes");
        }
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    private byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private Cipher createCipher(int mode, SecretKeySpec key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(mode, key, gcmParameterSpec);
        return cipher;
    }

    private byte[] combineIvAndData(byte[] iv, byte[] encryptedData) {
        byte[] combined = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
        return combined;
    }

    private void validateDataLength(byte[] decodedData) {
        if (decodedData.length < GCM_IV_LENGTH + GCM_TAG_LENGTH) {
            throw new IllegalArgumentException("Invalid data length");
        }
    }

    private byte[] extractIV(byte[] decodedData) {
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(decodedData, 0, iv, 0, GCM_IV_LENGTH);
        return iv;
    }

    private byte[] extractEncryptedData(byte[] decodedData) {
        byte[] encryptedData = new byte[decodedData.length - GCM_IV_LENGTH];
        System.arraycopy(decodedData, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);
        return encryptedData;
    }
}