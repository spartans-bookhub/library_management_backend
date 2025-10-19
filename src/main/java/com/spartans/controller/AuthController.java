package com.spartans.controller;

import com.spartans.dto.*;
import com.spartans.service.AuthService;
import com.spartans.util.JWTUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AuthController {

  @Autowired AuthService authService;

  @Autowired private JWTUtils jwtUtils;

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
    authService.register(registerRequest);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
    LoginResponseDTO responseDto = authService.login(loginRequest);
    return ResponseEntity.ok(responseDto);
  }

  @PostMapping("/api/password")
  public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordRequestDto passwordReqDto) {
    authService.changePassword(passwordReqDto);
    return ResponseEntity.ok().build();
  }
}
