package com.example.content_calendar.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import com.example.content_calendar.model.Status;
import com.example.content_calendar.model.Tags;
import com.example.content_calendar.model.Type;
import com.example.content_calendar.model.User;
import com.example.content_calendar.repository.UserRepository;
import com.example.content_calendar.service.ContentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/content")
@CrossOrigin(origins = "*")
public class ContentController {
    private final ContentService contentService;
    private final UserRepository userRepository;
    private final com.example.content_calendar.SecurityConfig.JwtService jwtService;

    public ContentController(ContentService contentService,
                             UserRepository userRepository,
                             com.example.content_calendar.SecurityConfig.JwtService jwtService) {
        this.contentService = contentService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    // GET /api/content — returns free content for anonymous users, free + subscribed premium for authenticated
    @GetMapping("")
    public ResponseEntity<List<ContentResponseDTO>> findAll(HttpServletRequest request) {
        String userId = extractUserIdFromRequest(request);
        if (userId != null) {
            return ResponseEntity.ok(contentService.getContentsForUser(userId));
        }
        return ResponseEntity.ok(contentService.getFreeContents());
    }

    // GET /api/content/{id} — premium content requires subscription
    @GetMapping("/{id}")
    public ResponseEntity<ContentResponseDTO> findById(@PathVariable String id, HttpServletRequest request) {
        String userId = extractUserIdFromRequest(request);
        return ResponseEntity.ok(contentService.getContentById(id, userId));
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

    // FETCH JOIN: GET /api/content/with-details
    @GetMapping("/with-details")
    public ResponseEntity<List<ContentResponseDTO>> findAllWithDetails() {
        return ResponseEntity.ok(contentService.getAllWithAuthorAndTags());
    }

    // JOIN: GET /api/content/by-author/{authorId} — subscription-aware
    @GetMapping("/by-author/{authorId}")
    public ResponseEntity<List<ContentResponseDTO>> findByAuthorId(@PathVariable String authorId, HttpServletRequest request) {
        String userId = extractUserIdFromRequest(request);
        return ResponseEntity.ok(contentService.getContentsByAuthorId(authorId, userId));
    }

    // JOIN: GET /api/content/{contentId}/tags
    @GetMapping("/{contentId}/tags")
    public ResponseEntity<List<Tags>> getTagsByContentId(@PathVariable String contentId) {
        return ResponseEntity.ok(contentService.getTagsByContentId(contentId));
    }

    // --- Helper to extract userId from JWT in request ---
    private String extractUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String jwt = authHeader.substring(7);
                return jwtService.extractUserId(jwt);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}