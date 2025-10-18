package com.spartans.util;

import com.spartans.exception.TokenValidationException;
import com.spartans.model.User;
import io.jsonwebtoken.*;
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

    public String generateToken(String loginId, String role, User student) {
        Map<String, Object> studentData = new HashMap<>();
        studentData.put("id", student.getUserId());
        studentData.put("name", student.getUserName());
        return Jwts.builder()
                .subject(loginId)
                .claim("role", role)
                .claim("student", studentData)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey())
                .compact();

    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Long getUserId(String token) {
        Map<String, Object> studentData = extractAllClaims(token).get("student", Map.class);
        if (studentData != null && studentData.get("id") != null) {
            return Long.parseLong(studentData.get("id").toString());
        }
        return null;
    }

    public String getLoginId(String token) {
        return extractAllClaims(token).getSubject();
    }
}
