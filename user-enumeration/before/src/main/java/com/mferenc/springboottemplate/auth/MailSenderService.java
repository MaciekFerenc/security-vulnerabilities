package com.mferenc.springboottemplate.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    public void sendForgotPasswordEmail(String email, String token) {
        log.info("Sending forgot password email to {}; token {}", email, token);
    }
}
