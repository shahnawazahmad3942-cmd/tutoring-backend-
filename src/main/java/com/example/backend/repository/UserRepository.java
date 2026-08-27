package com.example.backend.repository;

import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{

    Optional<User> findByEmail(String email); //return Optional because it may not exist - forces you to handle "not found" instead of getting a surprise null.

    boolean existsByEmail(String email);
}
