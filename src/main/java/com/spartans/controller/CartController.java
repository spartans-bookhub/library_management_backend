package com.spartans.controller;



import com.spartans.dto.CartDTO;
import com.spartans.repository.UserRepository;
import com.spartans.service.CartService;
import com.spartans.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;



    @GetMapping
    public ResponseEntity<List<CartDTO>> viewCart() {

        return ResponseEntity.ok(cartService.getCartForUser(UserContext.getUserId()));
    }

    @PostMapping("/add/{bookId}")
    public ResponseEntity<?> addToCart(@PathVariable Long bookId) {

        CartDTO item = cartService.addBookToCart(UserContext.getUserId(), bookId);

        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long bookId) {

        cartService.removeBookFromCart(UserContext.getUserId(), bookId);
        return ResponseEntity.ok().build();
    }
}

