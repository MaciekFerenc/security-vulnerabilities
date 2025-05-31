package com.mferenc.springboottemplate.encryption;

import com.mferenc.springboottemplate.auth.User;
import org.springframework.stereotype.Component;

@Component
class EncryptionBootstrap {
    public EncryptionBootstrap(EncryptionService encryptionService) {
        User.setEncryptionService(encryptionService);
    }
}
