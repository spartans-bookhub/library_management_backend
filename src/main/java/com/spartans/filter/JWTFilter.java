package com.spartans.filter;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JWTFilter extends GenericFilter {

    private final String secretKey;

    public JWTFilter(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        {
            HttpServletRequest httprequest = (HttpServletRequest) servletRequest;
            HttpServletResponse httpresonse = (HttpServletResponse) servletResponse;
            httpresonse.setHeader("Access-Control-Allow-Origin", "*");
            httpresonse.setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE,OPTIONS");
            httpresonse.setHeader("Access-Control-Allow-Crendentials", "true");
            httpresonse.setHeader("Access-Control-Allow-Headers", "*");

            if (httprequest.getMethod().equals(HttpMethod.OPTIONS.name())) {
                filterChain.doFilter(httprequest, httpresonse);
            } else {
                String authHeader = httprequest.getHeader("Authorization");
                if ((authHeader == null) || (!authHeader.startsWith("Bearer"))) {
                    throw new ServletException("JWT Token is missing");
                }

                String token = authHeader.substring(7);
                try {
                    System.out.println("jwtSecret--"+secretKey);
                    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
                    Jws<Claims> jws = Jwts.parser()
                            .verifyWith(key) // Verifies the signature
                            .build()
                            // This method performs all validation checks automatically
                            .parseSignedClaims(token);
                    //TO DO: check for claims for user
                } catch (SignatureException sign) {
                    throw new ServletException("Signature mismatch");

                } catch (MalformedJwtException malform) {
                    throw new ServletException("Token modified");
                }
            }
            filterChain.doFilter(httprequest, httpresonse);
        }
    }
}
