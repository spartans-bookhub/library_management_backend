package com.spartans.service;



import com.spartans.dto.CartDTO;
import com.spartans.mapper.BookMapper;
import com.spartans.model.Book;
import com.spartans.model.Cart;
import com.spartans.model.User;
import com.spartans.repository.BookRepository;
import com.spartans.repository.CartRepository;
import com.spartans.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper mapper;

    @Override
    public List<CartDTO> getCartForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartItemRepository.findByUser(user).stream().map((cart) -> mapper.toCartDto(cart)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CartDTO addBookToCart(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Cart cart = cartItemRepository.findByUserAndBook_BookId(user, bookId)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + 1);
                    return cartItemRepository.save(existing);
                })
                .orElseGet(() -> cartItemRepository.save(new Cart(user, book)));

        return mapper.toCartDto(cart);
    }

    @Override
    @Transactional
    public void removeBookFromCart(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        cartItemRepository.deleteByUserAndBook_BookId(user, bookId);
    }
}

