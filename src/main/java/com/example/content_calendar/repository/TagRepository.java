package com.example.content_calendar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.content_calendar.model.Content;
import com.example.content_calendar.model.Tags;

public interface TagRepository extends JpaRepository<Tags, String> {

    Tags findByTagName(String tagName);

    // JOIN: Find all tags for a given content id (via the content_tags join table)
    @Query("SELECT t FROM Tags t JOIN t.contents c WHERE c.id = :contentId")
    List<Tags> findTagsByContentId(@Param("contentId") String contentId);

    // JOIN: Find all content for a given tag name
    @Query("SELECT c FROM Content c JOIN c.tags t WHERE t.tagName = :tagName")
    List<Content> findContentsByTagName(@Param("tagName") String tagName);
}
