package com.spartans.controller;

import com.spartans.service.UserService;
import com.spartans.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    UserService userService;

    //Get student by id
    @GetMapping("/{id}")
    public ResponseEntity<?> viewProfile(@PathVariable Long id) {
         return ResponseEntity.ok(userService.getStudent(id));
    }
}
