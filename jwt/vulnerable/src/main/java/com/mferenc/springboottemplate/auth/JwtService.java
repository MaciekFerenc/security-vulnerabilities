package com.mferenc.springboottemplate.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtService {
    private static final Key SECRET_KEY =
            Keys.secretKeyFor(SignatureAlgorithm.HS256);
//    private static final Key SECRET_KEY =
//            Keys.hmacShaKeyFor(
//                    "12345678901234567890123456789012"
//                            .getBytes(StandardCharsets.UTF_8)
//            );

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return ((Claims) (Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parse(token)
                .getBody()))
                .getSubject();
    }
}
