package com.example.content_calendar.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    // GET /api/content?page=0&size=10&sort=publishedDate,desc
    @GetMapping("")
    public ResponseEntity<Page<ContentResponseDTO>> findAll(
            HttpServletRequest request,
            @PageableDefault(size = 10, sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        String userId = extractUserIdFromRequest(request);
        if (userId != null) {
            return ResponseEntity.ok(contentService.getContentsForUser(userId, pageable));
        }
        return ResponseEntity.ok(contentService.getFreeContents(pageable));
    }

    // GET /api/content/{id}
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

    // GET /api/content/filter/status?status=PLANNED&page=0&size=10
    @GetMapping("/filter/status")
    public ResponseEntity<Page<ContentResponseDTO>> findByStatus(
            @RequestParam Status status,
            @PageableDefault(size = 10, sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.getContentsByStatus(status, pageable));
    }

    // GET /api/content/filter/type?type=ARTICLE&page=0&size=10
    @GetMapping("/filter/type")
    public ResponseEntity<Page<ContentResponseDTO>> findByType(
            @RequestParam Type type,
            @PageableDefault(size = 10, sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.getContentsByType(type, pageable));
    }

    // JOIN: GET /api/content/filter/author?name=John&page=0&size=10
    @GetMapping("/filter/author")
    public ResponseEntity<Page<ContentResponseDTO>> findByAuthorName(
            @RequestParam String name,
            @PageableDefault(size = 10, sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.getContentsByAuthorName(name, pageable));
    }

    // JOIN: GET /api/content/filter/tag?tagName=java&page=0&size=10
    @GetMapping("/filter/tag")
    public ResponseEntity<Page<ContentResponseDTO>> findByTagName(
            @RequestParam String tagName,
            @PageableDefault(size = 10, sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.getContentsByTagName(tagName, pageable));
    }

    // JOIN: GET /api/content/filter/status-author?status=PLANNED&authorName=John&page=0&size=10
    @GetMapping("/filter/status-author")
    public ResponseEntity<Page<ContentResponseDTO>> findByStatusAndAuthor(
            @RequestParam Status status,
            @RequestParam String authorName,
            @PageableDefault(size = 10, sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.getContentsByStatusAndAuthor(status, authorName, pageable));
    }

    // FETCH JOIN: GET /api/content/with-details?page=0&size=10
    @GetMapping("/with-details")
    public ResponseEntity<Page<ContentResponseDTO>> findAllWithDetails(
            @PageableDefault(size = 10, sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.getAllWithAuthorAndTags(pageable));
    }

    // JOIN: GET /api/content/by-author/{authorId}?page=0&size=10
    @GetMapping("/by-author/{authorId}")
    public ResponseEntity<Page<ContentResponseDTO>> findByAuthorId(
            @PathVariable String authorId,
            HttpServletRequest request,
            @PageableDefault(size = 10, sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        String userId = extractUserIdFromRequest(request);
        return ResponseEntity.ok(contentService.getContentsByAuthorId(authorId, userId, pageable));
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