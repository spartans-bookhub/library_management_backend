package com.spartans.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartans.config.JWTConfig;
import com.spartans.dto.ErrorResponseDTO;
import com.spartans.exception.TokenValidationException;
import com.spartans.util.JWTUtils;
import com.spartans.util.UserContext;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTFilter extends GenericFilter {

    @Autowired
    private JWTConfig jwtConfig;

    @Autowired
    private JWTUtils jwtUtil;

    @Override
    public void doFilter (ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {

            HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpResponse.setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE,OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Crendentials", "true");
            httpResponse.setHeader("Access-Control-Allow-Headers", "*");

            if (httpRequest.getMethod().equals(HttpMethod.OPTIONS.name())) {
                filterChain.doFilter(httpRequest, httpResponse);
            } else {
                String authHeader = httpRequest.getHeader("Authorization");
                if ((authHeader == null) || (!authHeader.startsWith("Bearer"))) {
                    sendUnauthorizedResponse(httpResponse, "JWT Token is missing", httpRequest.getRequestURI());
                    return;
                }

                String token = authHeader.substring(7);
                try{
                    jwtUtil.validateToken(token);
                }catch(TokenValidationException ex){
                    sendUnauthorizedResponse(httpResponse, "JWT Token has expired", httpRequest.getRequestURI());
                    return;
                }
            }
            filterChain.doFilter(httpRequest, httpResponse);
        } finally {
            UserContext.clear();
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse httpResponse, String message, String path) throws IOException {
        if (!httpResponse.isCommitted()) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");

            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    message,
                    path
            );

            ObjectMapper mapper = new ObjectMapper();
            httpResponse.getWriter().write(mapper.writeValueAsString(errorResponse));
        }
    }
}
