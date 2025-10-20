package com.spartans.controller;

import com.spartans.dto.UserRequestDTO;
import com.spartans.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
  @Autowired UserService userService;

  // Get student by id
  @GetMapping("/{id}")
  public ResponseEntity<?> viewProfile(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUser(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> editProfile(@RequestBody UserRequestDTO request, @PathVariable Long id) {
    return ResponseEntity.ok(userService.editUser(request, id));
  }

  @GetMapping
  public ResponseEntity<?> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    return ResponseEntity.ok(userService.deleteUser(id));
  }
}
