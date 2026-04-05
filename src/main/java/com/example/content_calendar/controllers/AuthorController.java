package com.example.content_calendar.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.content_calendar.DTO.author.AuthorRequestDTO;
import com.example.content_calendar.DTO.author.AuthorResponseDTO;
import com.example.content_calendar.DTO.content.ContentResponseDTO;
import com.example.content_calendar.DTO.tag.TagResponseDTO;
import com.example.content_calendar.service.AuthorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/authors")
@CrossOrigin(origins = "*")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // --- CRUD ---

    // GET /api/authors?page=0&size=10&sort=name,asc
    @GetMapping("")
    public ResponseEntity<Page<AuthorResponseDTO>> getAllAuthors(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(authorService.getAllAuthors(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> getAuthorById(@PathVariable String id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @PostMapping("")
    public ResponseEntity<AuthorResponseDTO> createAuthor(@Valid @RequestBody AuthorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> updateAuthor(@PathVariable String id, @Valid @RequestBody AuthorRequestDTO dto) {
        return ResponseEntity.ok(authorService.updateAuthor(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable String id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    // --- Join queries ---

    // GET /api/authors/{authorId}/content?page=0&size=10
    @GetMapping("/{authorId}/content")
    public ResponseEntity<Page<ContentResponseDTO>> getContentByAuthorId(
            @PathVariable String authorId,
            @PageableDefault(size = 10, sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(authorService.getContentByAuthorId(authorId, pageable));
    }

    @GetMapping("/{authorId}/tags")
    public ResponseEntity<List<TagResponseDTO>> getTagsByAuthorId(@PathVariable String authorId) {
        return ResponseEntity.ok(authorService.getTagsByAuthorId(authorId));
    }
}
