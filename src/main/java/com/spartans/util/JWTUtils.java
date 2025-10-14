package com.spartans.util;

import com.spartans.exception.TokenValidationException;
import com.spartans.model.Student;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtils {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 hours

    // Generate the SecretKey
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String loginId, String role, Student student) {
        Map<String, Object> studentData = new HashMap<>();
        studentData.put("id", student.getStudentId());
        studentData.put("name", student.getStudentName());
        return Jwts.builder()
                .subject(loginId)
                .claim("role", role)
                .claim("student", studentData)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey())
                .compact();

    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            throw new TokenValidationException(e.getMessage());
        }
    }
}
