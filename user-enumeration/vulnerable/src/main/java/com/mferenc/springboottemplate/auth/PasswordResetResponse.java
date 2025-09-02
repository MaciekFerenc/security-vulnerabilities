package com.mferenc.springboottemplate.auth;

public record PasswordResetResponse(
        boolean success,
        String message
) {}