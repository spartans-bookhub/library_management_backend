package com.spartans.service;

import com.spartans.dto.CartDTO;
import java.util.List;

public interface CartService {
  List<CartDTO> getCartForUser(Long userId);

  CartDTO addBookToCart(Long userId, Long bookId);

  void removeBookFromCart(Long userId, Long bookId);
}
