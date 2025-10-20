package com.spartans.util;

import com.spartans.config.JWTConfig;
import com.spartans.model.UserAuth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JWTUtils {

  @Autowired private JWTConfig jwtConfig;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(UserAuth userAuth) {
    Map<String, Object> userData = new HashMap<>();
    if (userAuth.getRole().equalsIgnoreCase("STUDENT")) {
      userData.put("id", userAuth.getStudent().getUserId());
    }
    userData.put("email", userAuth.getEmail());
    userData.put("role", userAuth.getRole());
    return Jwts.builder()
        .subject(userAuth.getEmail())
        .claim("user", userData)
        .issuedAt(new Date())
        .issuer("book-nest")
        .expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
        .signWith(getSigningKey())
        .compact();
  }

  public void validateToken(String token) {
    Jws<Claims> jws =
        Jwts.parser()
            .verifyWith(getSigningKey())
            .requireIssuer("book-nest")
            .build()
            .parseSignedClaims(token);
    addClaimsInContext(jws.getPayload());
  }

  public void addClaimsInContext(Claims claims) {
    Map<String, Object> userClaims = new HashMap<>();
    Map<String, Object> userMap = (HashMap<String, Object>) claims.get("user");
    userClaims.put("role", userMap.get("role"));
    userClaims.put("id", ((Number) userMap.get("id")).longValue());
    userClaims.put("email", userMap.get("email"));
    UserContext.setUser(userClaims);
  }
}
