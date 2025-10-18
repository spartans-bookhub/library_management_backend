package com.spartans.controller;

import com.spartans.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
  @Autowired UserService userService;

  // Get student by id
  @GetMapping("/{id}")
  public ResponseEntity<?> viewProfile(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getStudent(id));
  }
}
