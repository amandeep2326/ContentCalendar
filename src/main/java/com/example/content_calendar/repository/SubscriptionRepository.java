package com.example.content_calendar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.content_calendar.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    Optional<Subscription> findByUserIdAndAuthorId(String userId, String authorId);

    boolean existsByUserIdAndAuthorId(String userId, String authorId);

    List<Subscription> findByUserId(String userId);

    List<Subscription> findByAuthorId(String authorId);

    void deleteByUserIdAndAuthorId(String userId, String authorId);

    @Query("SELECT s FROM Subscription s JOIN FETCH s.author WHERE s.user.id = :userId")
    List<Subscription> findByUserIdWithAuthor(@Param("userId") String userId);

    @Query("SELECT s FROM Subscription s JOIN FETCH s.user WHERE s.author.id = :authorId")
    List<Subscription> findByAuthorIdWithUser(@Param("authorId") String authorId);
}
