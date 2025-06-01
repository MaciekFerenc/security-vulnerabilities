package com.mferenc.springboottemplate.auth;

public record PasswordResetRequest(
        String token,
        String newPassword
) {}