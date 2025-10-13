package com.spartans.controller;

import com.spartans.dto.LoginRequestDTO;
import com.spartans.dto.LoginResponseDTO;
import com.spartans.dto.RegisterRequestDTO;
import com.spartans.dto.StudentResponseDTO;
import com.spartans.service.AuthService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequest){
        StudentResponseDTO responseDto = authService.register(registerRequest);
//        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpSession session){
        LoginResponseDTO responseDto = authService.login(loginRequest);
        //TO DO: Stale data. Handle
        session.setAttribute("account", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session != null)
            session.invalidate();
        return ResponseEntity.ok("Logout successful");
    }

    //Is JWT needed?
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
//        UserAccount account = userAccountRepo.findById(loginRequest.getLoginId())
//                .orElseThrow(() -> new RuntimeException("Invalid login"));
//
//        if (!passwordEncoder.matches(loginRequest.getPassword(), account.getPassword())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//        }
//
//        // Generate JWT
//        String token = jwtUtil.generateToken(account.getLoginId(), account.getRole().name());
//
//        // If student, return profile + token
//        if (account.getRole() == Role.STUDENT && account.getStudent() != null) {
//            return ResponseEntity.ok(Map.of(
//                    "token", token,
//                    "student", account.getStudent()
//            ));
//        }
//
//        // If librarian, return token + role
//        return ResponseEntity.ok(Map.of(
//                "token", token,
//                "role", account.getRole()
//        ));
//    }

}
