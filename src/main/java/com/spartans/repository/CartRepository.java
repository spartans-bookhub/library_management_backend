package com.spartans.repository;

import com.spartans.model.Cart;
import com.spartans.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

  List<Cart> findByUser(User user);

  Optional<Cart> findByUserAndBook_BookId(User user, Long bookId);

  void deleteByUserAndBook_BookId(User user, Long bookId);
}
