package com.example.content_calendar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.content_calendar.model.User;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String userName);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    // FETCH JOIN: load user with subscribed authors eagerly
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.subscribedAuthors WHERE u.id = :userId")
    Optional<User> findByIdWithSubscriptions(@Param("userId") String userId);
}
