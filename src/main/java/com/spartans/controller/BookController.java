package com.thoughtworks.Book.Controller;

import com.thoughtworks.Book.Model.Book;
import com.thoughtworks.Book.Service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    BookService bookService;


    //http://localhost:9007/api/book/list
    @GetMapping("/list")
    public  ResponseEntity<?> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);

    }
    //http://localhost:9007/api/book/details?title=Harry%20Potter
    @GetMapping("/details")
    public ResponseEntity<?> getBookDetails(@RequestParam String title) {
        Map<String, Object> bookDetails = bookService.getBookDetails(title);

        // Agar bookDetails me "Message" key hai, matlab book nahi mili
        if (bookDetails.containsKey("Message")) {
            return new ResponseEntity<>(bookDetails, HttpStatus.NOT_FOUND);
        }

        // Book mil gayi, 200 OK return karo
        return new ResponseEntity<>(bookDetails, HttpStatus.OK);
    }




}