package com.devhoard.repository; // Data access layer for user identity persistence

// Internal domain entities and Spring Data JPA primitives
import com.devhoard.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for User entity persistence. 
 * Managed by Spring Data JPA to provide CRUD operations and custom derived query methods.
 */
public interface UserRepo extends JpaRepository<User, Long> {

    /**
     * Resolves a User entity by its unique username.
     * Returns an Optional to enforce explicit handling of cases where the principal identity is not found.
     */
    Optional<User> findByUsername(String username);

}

