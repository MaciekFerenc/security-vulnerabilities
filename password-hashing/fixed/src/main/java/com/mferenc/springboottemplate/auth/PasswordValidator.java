package com.mferenc.springboottemplate.auth;

public interface PasswordValidator {
    PasswordValidationResult validatePassword(String password);
}
