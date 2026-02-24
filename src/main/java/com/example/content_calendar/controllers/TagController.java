package com.example.content_calendar.controllers;

import java.util.List;

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

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // --- CRUD ---

    @GetMapping("")
    public ResponseEntity<List<TagResponseDTO>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDTO> getTagById(@PathVariable String id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @PostMapping("")
    public ResponseEntity<TagResponseDTO> createTag(@RequestBody TagRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createTag(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponseDTO> updateTag(@PathVariable String id, @RequestBody TagRequestDTO dto) {
        return ResponseEntity.ok(tagService.updateTag(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable String id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    // --- Join queries ---

    @GetMapping("/contents/{tagName}")
    public ResponseEntity<List<ContentResponseDTO>> getContentsByTag(@PathVariable String tagName) {
        return ResponseEntity.ok(tagService.getContentsByTagName(tagName));
    }
}
