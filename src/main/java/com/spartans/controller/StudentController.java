package com.spartans.controller;

import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.service.TransactionService;
import com.spartans.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class StudentController {
    @Autowired
    UserService userService;

    //Get student by id
    @GetMapping("/{id}")
    public ResponseEntity<?> viewProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getStudent(id));
    }
}
