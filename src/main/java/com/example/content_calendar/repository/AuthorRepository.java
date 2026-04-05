package com.example.content_calendar.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.content_calendar.model.Author;
import com.example.content_calendar.model.Content;
import com.example.content_calendar.model.Tags;

public interface AuthorRepository extends JpaRepository<Author, String> {

    public Author findByEmail(String email);
    public Author findByName(String name);

    // JOIN: Get all content by author id (Content JOIN Author)
    @Query("SELECT c FROM Content c JOIN c.author a WHERE a.id = :authorId")
    Page<Content> findContentByAuthorId(@Param("authorId") String authorId, Pageable pageable);

    // JOIN: Get distinct tags an author has posted with (Content JOIN Tags via content_tags)
    @Query("SELECT DISTINCT t FROM Content c JOIN c.author a JOIN c.tags t WHERE a.id = :authorId")
    List<Tags> findTagsByAuthorId(@Param("authorId") String authorId);
} 
