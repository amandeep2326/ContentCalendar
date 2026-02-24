package com.example.content_calendar.repository;

import java.util.List;

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

    List<Content> findByStatus(Status status);

    List<Content> findByType(Type type);

    // JOIN: Find all content by a specific author name (Content JOIN Author)
    @Query("SELECT c FROM Content c JOIN c.author a WHERE a.name = :authorName")
    List<Content> findByAuthorName(@Param("authorName") String authorName);

    // JOIN: Find all content that has a specific tag name (Content JOIN Tags via content_tags)
    @Query("SELECT c FROM Content c JOIN c.tags t WHERE t.tagName = :tagName")
    List<Content> findByTagName(@Param("tagName") String tagName);

    // JOIN: Find all content by status AND author name
    @Query("SELECT c FROM Content c JOIN c.author a WHERE c.status = :status AND a.name = :authorName")
    List<Content> findByStatusAndAuthorName(@Param("status") Status status, @Param("authorName") String authorName);

    // FETCH JOIN: Get content with author and tags eagerly loaded (avoids N+1 problem)
    @Query("SELECT DISTINCT c FROM Content c LEFT JOIN FETCH c.author LEFT JOIN FETCH c.tags")
    List<Content> findAllWithAuthorAndTags();

    // JOIN: Find all content by author id
    @Query("SELECT c FROM Content c JOIN c.author a WHERE a.id = :authorId")
    List<Content> findByAuthorId(@Param("authorId") String authorId);

    // JOIN: Find all tags for a given content id (Content JOIN Tags via content_tags)
    @Query("SELECT t FROM Content c JOIN c.tags t WHERE c.id = :contentId")
    List<Tags> findTagsByContentId(@Param("contentId") String contentId);
} 
