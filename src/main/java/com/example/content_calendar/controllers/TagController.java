package com.example.content_calendar.controllers;

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

import com.example.content_calendar.DTO.content.ContentResponseDTO;
import com.example.content_calendar.DTO.tag.TagRequestDTO;
import com.example.content_calendar.DTO.tag.TagResponseDTO;
import com.example.content_calendar.service.TagService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // --- CRUD ---

    // GET /api/tags?page=0&size=10&sort=tagName,asc
    @GetMapping("")
    public ResponseEntity<Page<TagResponseDTO>> getAllTags(
            @PageableDefault(size = 10, sort = "tagName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(tagService.getAllTags(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDTO> getTagById(@PathVariable String id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @PostMapping("")
    public ResponseEntity<TagResponseDTO> createTag(@Valid @RequestBody TagRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createTag(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponseDTO> updateTag(@PathVariable String id, @Valid @RequestBody TagRequestDTO dto) {
        return ResponseEntity.ok(tagService.updateTag(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable String id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    // --- Join queries ---

    // GET /api/tags/contents/{tagName}?page=0&size=10
    @GetMapping("/contents/{tagName}")
    public ResponseEntity<Page<ContentResponseDTO>> getContentsByTag(
            @PathVariable String tagName,
            @PageableDefault(size = 10, sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(tagService.getContentsByTagName(tagName, pageable));
    }
}
