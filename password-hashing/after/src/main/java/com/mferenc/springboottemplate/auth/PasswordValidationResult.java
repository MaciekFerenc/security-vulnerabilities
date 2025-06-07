package com.mferenc.springboottemplate.auth;

public record PasswordValidationResult(
        boolean isSuccess,
        String errorMessage
) {
}
