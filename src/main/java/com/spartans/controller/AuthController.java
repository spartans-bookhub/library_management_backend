package com.spartans.controller;

import com.spartans.dto.LoginRequestDTO;
import com.spartans.dto.LoginResponseDTO;
import com.spartans.dto.RegisterRequestDTO;
import com.spartans.dto.StudentResponseDTO;
import com.spartans.exception.UserAlreadyExistException;
import com.spartans.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        try {
            StudentResponseDTO responseDto = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (UserAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO responseDto = authService.login(loginRequest);
        return ResponseEntity.ok(responseDto);
    }
    
}
