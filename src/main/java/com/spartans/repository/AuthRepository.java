package com.spartans.repository;

import com.spartans.model.UserAuth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<UserAuth, String> {

  Optional<UserAuth> findByResetToken(String token);
}
