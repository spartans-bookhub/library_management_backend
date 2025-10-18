package com.spartans.controller;

import com.spartans.config.LibraryConfig;
import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.service.BookService;
import com.spartans.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    private LibraryConfig libraryConfig;

    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book savedBook = bookService.addBook(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }
}
