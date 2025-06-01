package com.mferenc.springboottemplate.auth;

import ch.qos.logback.core.util.StringUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;

    @PersistenceContext
    private EntityManager entityManager;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder, MailSenderService mailSenderService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderService = mailSenderService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest,
                                        HttpServletRequest request) {
        randomDelay(200, 700);
        User user = userRepository
                .findByUsername(loginRequest.username())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.password());
        Authentication auth = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(auth);
        securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, null);

        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        if (userRepository.findByUsername(registerUserRequest.username()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is taken");
        }

        User user = new User();
        user.setUsername(registerUserRequest.username());
        String encodePassword = passwordEncoder.encode(registerUserRequest.password());
        user.setPassword(encodePassword);
        userRepository.save(user);

        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<PasswordResetResponse> forgotPassword(@RequestBody ForgotPasswordRequest resetRequest) {
        randomDelay(200, 700);
        String successMessage = "Password reset token was sent to given email address";

        User user = userRepository.findByEmail(resetRequest.email()).orElse(null);

        if (user != null) {
            String resetToken = user.generateResetToken();
            userRepository.save(user);
            mailSenderService.sendForgotPasswordEmail(user.getEmail(), resetToken);
        }

        var response = new PasswordResetResponse(true, successMessage);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResponse> resetPassword(@RequestBody PasswordResetRequest resetRequest) {
        User user = userRepository.findByPasswordResetToken(resetRequest.token()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404)
                    .body(new PasswordResetResponse(false, "Invalid or expired token"));
        }

        if (!user.isResetTokenValid()) {
            return ResponseEntity.status(400)
                    .body(new PasswordResetResponse(false, "Invalid or expired token"));
        }

        user.setPassword(passwordEncoder.encode(resetRequest.newPassword()));
        user.clearResetToken();
        userRepository.save(user);

        return ResponseEntity.ok(new PasswordResetResponse(
                true,
                "Password has been successfully reset"));
    }

    private void randomDelay(int minMillis, int maxMillis) {
        try {
            int delay = ThreadLocalRandom.current().nextInt(minMillis, maxMillis + 1);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
