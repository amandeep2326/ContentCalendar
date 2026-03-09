package com.example.content_calendar.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.example.content_calendar.DTO.content.ContentRequestDTO;
import com.example.content_calendar.DTO.content.ContentResponseDTO;
import com.example.content_calendar.DTO.content.TagSummaryDTO;
import com.example.content_calendar.model.Status;
import com.example.content_calendar.model.Tags;
import com.example.content_calendar.model.Type;
import com.example.content_calendar.service.ContentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/content")
@CrossOrigin(origins = "*")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("")
    public ResponseEntity<List<ContentResponseDTO>> findAll() {
        return ResponseEntity.ok(contentService.getContents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentResponseDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(contentService.getContentById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<ContentResponseDTO> create(@Valid @RequestBody ContentRequestDTO contentRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contentService.save(contentRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentResponseDTO> update(@Valid @RequestBody ContentRequestDTO contentRequest, @PathVariable String id) {
        return ResponseEntity.ok(contentService.update(id, contentRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        contentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Join / Filter endpoints ---

    // GET /api/content/filter/status?status=PLANNED
    @GetMapping("/filter/status")
    public ResponseEntity<List<ContentResponseDTO>> findByStatus(@RequestParam Status status) {
        return ResponseEntity.ok(contentService.getContentsByStatus(status));
    }

    // GET /api/content/filter/type?type=ARTICLE
    @GetMapping("/filter/type")
    public ResponseEntity<List<ContentResponseDTO>> findByType(@RequestParam Type type) {
        return ResponseEntity.ok(contentService.getContentsByType(type));
    }

    // JOIN: GET /api/content/filter/author?name=John
    @GetMapping("/filter/author")
    public ResponseEntity<List<ContentResponseDTO>> findByAuthorName(@RequestParam String name) {
        return ResponseEntity.ok(contentService.getContentsByAuthorName(name));
    }

    // JOIN: GET /api/content/filter/tag?tagName=java
    @GetMapping("/filter/tag")
    public ResponseEntity<List<ContentResponseDTO>> findByTagName(@RequestParam String tagName) {
        return ResponseEntity.ok(contentService.getContentsByTagName(tagName));
    }

    // JOIN: GET /api/content/filter/status-author?status=PLANNED&authorName=John
    @GetMapping("/filter/status-author")
    public ResponseEntity<List<ContentResponseDTO>> findByStatusAndAuthor(
            @RequestParam Status status,
            @RequestParam String authorName) {
        return ResponseEntity.ok(contentService.getContentsByStatusAndAuthor(status, authorName));
    }

    // FETCH JOIN: GET /api/content/with-details  (eagerly loads author + tags to avoid N+1)
    @GetMapping("/with-details")
    public ResponseEntity<List<ContentResponseDTO>> findAllWithDetails() {
        return ResponseEntity.ok(contentService.getAllWithAuthorAndTags());
    }

    // JOIN: GET /api/content/by-author/{authorId}
    @GetMapping("/by-author/{authorId}")
    public ResponseEntity<List<ContentResponseDTO>> findByAuthorId(@PathVariable String authorId) {
        return ResponseEntity.ok(contentService.getContentsByAuthorId(authorId));
    }

    // JOIN: GET /api/content/{contentId}/tags — get all tags for a specific content
    @GetMapping("/{contentId}/tags")
    public ResponseEntity<List<Tags>> getTagsByContentId(@PathVariable String contentId) {
        return ResponseEntity.ok(contentService.getTagsByContentId(contentId));
    }
}