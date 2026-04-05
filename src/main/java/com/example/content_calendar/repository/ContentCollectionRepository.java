package com.example.content_calendar.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.content_calendar.model.Content;
import com.example.content_calendar.model.Status;
import com.example.content_calendar.model.Tags;
import com.example.content_calendar.model.Type;

@Repository
public interface ContentCollectionRepository extends JpaRepository<Content, String> {

    Page<Content> findByStatus(Status status, Pageable pageable);

    Page<Content> findByType(Type type, Pageable pageable);

    // All free (non-premium) content
    Page<Content> findByPremiumFalse(Pageable pageable);

    // Free content + premium content from specific authors (subscribed)
    @Query("SELECT c FROM Content c WHERE c.premium = false OR (c.premium = true AND c.author.id IN :authorIds)")
    Page<Content> findAccessibleContent(@Param("authorIds") List<String> authorIds, Pageable pageable);

    // Free content by author + premium content by author (for subscribed users)
    @Query("SELECT c FROM Content c WHERE c.author.id = :authorId AND (c.premium = false OR c.premium = :includePremium)")
    Page<Content> findByAuthorIdWithAccess(@Param("authorId") String authorId, @Param("includePremium") boolean includePremium, Pageable pageable);

    // JOIN: Find all content by a specific author name (Content JOIN Author)
    @Query("SELECT c FROM Content c JOIN c.author a WHERE a.name = :authorName")
    Page<Content> findByAuthorName(@Param("authorName") String authorName, Pageable pageable);

    // JOIN: Find all content that has a specific tag name (Content JOIN Tags via content_tags)
    @Query("SELECT c FROM Content c JOIN c.tags t WHERE t.tagName = :tagName")
    Page<Content> findByTagName(@Param("tagName") String tagName, Pageable pageable);

    // JOIN: Find all content by status AND author name
    @Query("SELECT c FROM Content c JOIN c.author a WHERE c.status = :status AND a.name = :authorName")
    Page<Content> findByStatusAndAuthorName(@Param("status") Status status, @Param("authorName") String authorName, Pageable pageable);

    // FETCH JOIN: Get content with author and tags eagerly loaded (avoids N+1 problem)
    @Query(value = "SELECT DISTINCT c FROM Content c LEFT JOIN FETCH c.author LEFT JOIN FETCH c.tags",
           countQuery = "SELECT COUNT(DISTINCT c) FROM Content c")
    Page<Content> findAllWithAuthorAndTags(Pageable pageable);

    // JOIN: Find all content by author id
    @Query("SELECT c FROM Content c JOIN c.author a WHERE a.id = :authorId")
    Page<Content> findByAuthorId(@Param("authorId") String authorId, Pageable pageable);

    // Find all content by multiple author ids (for feed)
    @Query("SELECT c FROM Content c WHERE c.author.id IN :authorIds")
    Page<Content> findByAuthorIds(@Param("authorIds") List<String> authorIds, Pageable pageable);

    // JOIN: Find all tags for a given content id (Content JOIN Tags via content_tags)
    @Query("SELECT t FROM Content c JOIN c.tags t WHERE c.id = :contentId")
    List<Tags> findTagsByContentId(@Param("contentId") String contentId);
} 
