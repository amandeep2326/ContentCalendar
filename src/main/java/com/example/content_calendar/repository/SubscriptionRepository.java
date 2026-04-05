package com.example.content_calendar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "SELECT s FROM Subscription s JOIN FETCH s.author WHERE s.user.id = :userId",
           countQuery = "SELECT COUNT(s) FROM Subscription s WHERE s.user.id = :userId")
    Page<Subscription> findByUserIdWithAuthor(@Param("userId") String userId, Pageable pageable);

    @Query(value = "SELECT s FROM Subscription s JOIN FETCH s.user WHERE s.author.id = :authorId",
           countQuery = "SELECT COUNT(s) FROM Subscription s WHERE s.author.id = :authorId")
    Page<Subscription> findByAuthorIdWithUser(@Param("authorId") String authorId, Pageable pageable);
}
