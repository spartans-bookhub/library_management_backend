package com.spartans.util;

import com.spartans.exception.TokenValidationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtils {

    private final String SECRET_KEY = "mySecretKey12345"; // use env variable in production
    private final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 hours

    // Generate the SecretKey
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String loginId, String role) {
        return Jwts.builder()
                .subject(loginId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey())
                .compact();

    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(getSigningKey()) // The modern way to set the signing key for verification
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            throw new TokenValidationException(e.getMessage());

        }

    }
}
