package com.mferenc.springboottemplate.auth;

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


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;


    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest,
                                        HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String username = loginRequest.username();

        if (loginAttemptService.isIpBlocked(clientIp)) {
            return ResponseEntity.status(429).body("Too many attempts. Try again later");
        }
        if (loginAttemptService.isUsernameBlocked(username)) {
            return ResponseEntity.status(429).body("Too many attempts. Try again later.");
        }

        User user = userRepository.findByUsername(loginRequest.username())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            loginAttemptService.loginFailed(clientIp, username);
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        loginAttemptService.loginSucceeded(clientIp, username);

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

}
