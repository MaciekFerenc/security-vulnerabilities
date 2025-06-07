package com.mferenc.springboottemplate.auth;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class SimplePasswordValidator implements PasswordValidator {
    private static final int MIN_LENGTH = 12;
    private static final int MIN_SPECIAL_CHARS = 1;
    private static final int MIN_DIGITS = 1;

    private static final Pattern SPECIAL_CHARS_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
    private static final Pattern DIGITS_PATTERN = Pattern.compile("[0-9]");

    @Override
    public PasswordValidationResult validatePassword(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return new PasswordValidationResult(
                    false,
                    String.format("Password must be at least %d characters long", MIN_LENGTH)
            );
        }

        long specialCharsCount = SPECIAL_CHARS_PATTERN.matcher(password).results().count();
        if (specialCharsCount < MIN_SPECIAL_CHARS) {
            return new PasswordValidationResult(
                    false,
                    String.format("Password must contain at least %d special character(s)", MIN_SPECIAL_CHARS)
            );
        }

        long digitsCount = DIGITS_PATTERN.matcher(password).results().count();
        if (digitsCount < MIN_DIGITS) {
            return new PasswordValidationResult(
                    false,
                    String.format("Password must contain at least %d digit(s)", MIN_DIGITS)
            );
        }

        return new PasswordValidationResult(true, null);
    }
}