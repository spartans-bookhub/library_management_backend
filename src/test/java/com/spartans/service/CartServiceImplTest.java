package com.spartans.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.spartans.dto.CartDTO;
import com.spartans.mapper.BookMapper;
import com.spartans.model.Book;
import com.spartans.model.Cart;
import com.spartans.model.User;
import com.spartans.repository.BookRepository;
import com.spartans.repository.CartRepository;
import com.spartans.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CartServiceImplTest {

  @Mock private CartRepository cartRepository;
  @Mock private UserRepository userRepository;
  @Mock private BookRepository bookRepository;
  @Mock private BookMapper mapper;

  @InjectMocks private CartServiceImpl cartService;

  private User user;
  private Book book;
  private Cart cart;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    user = new User();
    user.setUserId(1L);
    book = new Book();
    book.setBookId(2L);
    cart = new Cart(user, book);
    cart.setQuantity(1);
  }

  @Test
  void addBookToCart_PositiveCase() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
    when(cartRepository.findByUserAndBook_BookId(user, 2L)).thenReturn(Optional.empty());
    when(cartRepository.save(any(Cart.class))).thenReturn(cart);
    when(mapper.toCartDto(any(Cart.class)))
        .thenReturn(
            new CartDTO(
                10L,
                1L,
                "The Alchemist",
                "Paulo Coelho",
                "Fiction",
                "978-0061122415",
                "https://example.com/images/alchemist.jpg"));

    CartDTO result = cartService.addBookToCart(1L, 2L);

    assertNotNull(result);
    verify(cartRepository, times(1)).save(any(Cart.class));
  }

  @Test
  void addBookToCart_NegativeCase_UserNotFound() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> cartService.addBookToCart(99L, 2L));

    assertEquals("User not found", ex.getMessage());
    verify(cartRepository, never()).save(any());
  }
}
