package com.mferenc.springboottemplate.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class PropertiesKeyManager implements KeyManager {

    @Value("${encryption.key}")
    private String encryptionKey;

    @Override
    public String getEncryptionKey() {
        return encryptionKey;
    }
}