package com.spartans.controller;


import com.spartans.dto.LoginRequestDTO;
import com.spartans.dto.LoginResponseDTO;
import com.spartans.dto.RegisterRequestDTO;
import com.spartans.dto.StudentResponseDTO;
import com.spartans.exception.UserAlreadyExistException;
import com.spartans.model.Transaction;
import com.spartans.service.AuthService;
import com.spartans.service.TransactionService;
import com.spartans.util.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/auth")
public class AuthController {


    @Autowired
    AuthService authService;


    @Autowired
    private JWTUtils jwtUtils;


    @Autowired
    private TransactionService transactionService;


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




    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }


    @PostMapping("/book/{bookId}/borrow")
    public ResponseEntity<Transaction> borrowBook(@PathVariable Long bookId, HttpServletRequest request) {
        String token = extractToken(request);
        Long userId = jwtUtils.getUserId(token);
        Transaction transaction = transactionService.borrowBook(userId, bookId);
        return ResponseEntity.ok(transaction);
    }
}
