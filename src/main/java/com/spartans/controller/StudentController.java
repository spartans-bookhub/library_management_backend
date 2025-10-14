package com.spartans.controller;

import com.spartans.model.Transaction;
import com.spartans.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;
  
    @GetMapping("/{id}")
    public ResponseEntity<?> viewProfile(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudent(id));
    }

    @PostMapping("/{studentId}/borrow/{bookId}")
    public Transaction borrowBook(@PathVariable Long studentId, @PathVariable Long bookId) {
        return studentService.borrowBook(studentId, bookId);
    }

    @PostMapping("/{studentId}/return/{bookId}")
    public Transaction returnBook(@PathVariable Long studentId, @PathVariable Long bookId) {
        return studentService.returnBook(studentId, bookId);
    }

    @GetMapping("/{studentId}/borrowed")
    public List<Transaction> borrowedBooks(@PathVariable Long studentId) {
        return studentService.getBorrowedBooks(studentId);
    }

    @GetMapping("/{studentId}/history")
    public List<Transaction> borrowHistory(@PathVariable Long studentId) {
        return studentService.getBorrowHistory(studentId);
    }
}
