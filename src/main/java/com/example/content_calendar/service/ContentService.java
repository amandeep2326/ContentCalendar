package com.example.content_calendar.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

import com.example.content_calendar.DTO.content.ContentRequestDTO;
import com.example.content_calendar.DTO.content.ContentResponseDTO;
import com.example.content_calendar.ExceptionHandler.*;
import com.example.content_calendar.mapper.ContentMapper;
import com.example.content_calendar.model.Author;
import com.example.content_calendar.model.Content;
import com.example.content_calendar.model.Status;
import com.example.content_calendar.model.Tags;
import com.example.content_calendar.model.Type;
import com.example.content_calendar.repository.*;

@Service
public class ContentService {
    private final AuthorRepository authorRepository;
    private final ContentCollectionRepository contentRepository;
    private final TagRepository tagRepository;
    private final ContentMapper contentMapper;

    public ContentService(AuthorRepository authorRepository,
                          ContentCollectionRepository contentRepository,
                          TagRepository tagRepository,
                          ContentMapper contentMapper) {
        this.authorRepository = authorRepository;
        this.contentRepository = contentRepository;
        this.tagRepository = tagRepository;
        this.contentMapper = contentMapper;
    }

    public List<ContentResponseDTO> getContents() {
        return contentMapper.toListResponseDTOList(contentRepository.findAll());
    }

    public ContentResponseDTO getContentById(String id) {
        Content content = contentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
        return contentMapper.toResponseDTO(content);
    }

    public ContentResponseDTO save(ContentRequestDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            throw new BadRequestException("Title is required");
        }

        Content content = contentMapper.toEntity(dto);
        resolveAuthorAndTags(content, dto);
        content.setUpDatedAt(LocalDateTime.now());

        return contentMapper.toResponseDTO(contentRepository.save(content));
    }

    public ContentResponseDTO update(String id, ContentRequestDTO dto) {
        Content content = contentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));

        contentMapper.updateEntityFromDTO(dto, content);
        resolveAuthorAndTags(content, dto);
        content.setUpDatedAt(LocalDateTime.now());

        return contentMapper.toResponseDTO(contentRepository.save(content));
    }

    public void delete(String id) {
        Content content = contentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
        contentRepository.delete(content);
    }

    // --- Join query methods ---

    public List<ContentResponseDTO> getContentsByStatus(Status status) {
        return contentMapper.toListResponseDTOList(contentRepository.findByStatus(status));
    }

    public List<ContentResponseDTO> getContentsByType(Type type) {
        return contentMapper.toListResponseDTOList(contentRepository.findByType(type));
    }

    public List<ContentResponseDTO> getContentsByAuthorName(String authorName) {
        return contentMapper.toListResponseDTOList(contentRepository.findByAuthorName(authorName));
    }

    public List<ContentResponseDTO> getContentsByTagName(String tagName) {
        return contentMapper.toListResponseDTOList(contentRepository.findByTagName(tagName));
    }

    public List<ContentResponseDTO> getContentsByStatusAndAuthor(Status status, String authorName) {
        return contentMapper.toListResponseDTOList(contentRepository.findByStatusAndAuthorName(status, authorName));
    }

    public List<ContentResponseDTO> getAllWithAuthorAndTags() {
        return contentMapper.toResponseDTOList(contentRepository.findAllWithAuthorAndTags());
    }

    public List<ContentResponseDTO> getContentsByAuthorId(String authorId) {
        return contentMapper.toListResponseDTOList(contentRepository.findByAuthorId(authorId));
    }

    public List<Tags> getTagsByContentId(String contentId) {
        return contentRepository.findTagsByContentId(contentId);
    }

    // --- Helper ---

    private void resolveAuthorAndTags(Content content, ContentRequestDTO dto) {
        if (dto.getAuthorId() != null) {
            Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + dto.getAuthorId()));
            content.setAuthor(author);
        }

        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            Set<Tags> tags = new HashSet<>(tagRepository.findAllById(dto.getTagIds()));
            if (tags.size() != dto.getTagIds().size()) {
                throw new BadRequestException("One or more tag IDs are invalid");
            }
            content.setTags(tags);
        }
    }
}
