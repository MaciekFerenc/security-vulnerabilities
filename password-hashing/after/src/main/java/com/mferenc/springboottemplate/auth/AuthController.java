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

    private final PasswordValidator passwordValidator;

    @PersistenceContext
    private EntityManager entityManager;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder, PasswordValidator passwordValidator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest,
                                        HttpServletRequest request) {

        User user = userRepository.findByUsername(loginRequest.username())
                .orElse(null);

        if (user == null ||
            !passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(), loginRequest.password()
                );
        Authentication auth = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(auth);
        securityContextRepository.saveContext(
                SecurityContextHolder.getContext(),
                request,
                null);
        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @RequestBody RegisterUserRequest registerUserRequest) {
        boolean usernameTaken = userRepository
                .findByUsername(registerUserRequest.username())
                .isPresent();
        if (usernameTaken) {
            return ResponseEntity.
                    status(HttpStatus.BAD_REQUEST)
                    .body("Username is taken");
        }
        var result = passwordValidator.
                validatePassword(registerUserRequest.password());
        if (!result.isSuccess()) {
            return ResponseEntity.
                    status(HttpStatus.BAD_REQUEST)
                    .body(result.errorMessage());
        }

        User user = new User();
        user.setUsername(registerUserRequest.username());
        String encodedPassword =
                passwordEncoder.encode(registerUserRequest.password());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return ResponseEntity.ok("Registration successful");
    }

}
