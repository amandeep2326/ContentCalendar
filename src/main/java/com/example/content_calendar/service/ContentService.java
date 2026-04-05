package com.example.content_calendar.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.example.content_calendar.model.User;
import com.example.content_calendar.repository.*;

@Service
public class ContentService {
    private final AuthorRepository authorRepository;
    private final ContentCollectionRepository contentRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ContentMapper contentMapper;

    public ContentService(AuthorRepository authorRepository,
                          ContentCollectionRepository contentRepository,
                          TagRepository tagRepository,
                          UserRepository userRepository,
                          SubscriptionRepository subscriptionRepository,
                          ContentMapper contentMapper) {
        this.authorRepository = authorRepository;
        this.contentRepository = contentRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.contentMapper = contentMapper;
    }

    // Returns free content only (for unauthenticated users)
    public Page<ContentResponseDTO> getFreeContents(Pageable pageable) {
        return contentRepository.findByPremiumFalse(pageable)
                .map(contentMapper::toListResponseDTO);
    }

    // Returns free content + premium content from subscribed authors (for authenticated users)
    public Page<ContentResponseDTO> getContentsForUser(String userId, Pageable pageable) {
        userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<String> subscribedAuthorIds = subscriptionRepository.findByUserId(userId).stream()
            .map(s -> s.getAuthor().getId())
            .toList();

        Page<Content> contents;
        if (subscribedAuthorIds.isEmpty()) {
            contents = contentRepository.findByPremiumFalse(pageable);
        } else {
            contents = contentRepository.findAccessibleContent(subscribedAuthorIds, pageable);
        }
        return contents.map(contentMapper::toListResponseDTO);
    }

    // Single content by id — checks premium access
    public ContentResponseDTO getContentById(String id, String userId) {
        Content content = contentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));

        if (content.isPremium()) {
            if (userId == null) {
                throw new BadRequestException("This is premium content. Please login and subscribe to the author to view it.");
            }
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            boolean isSubscribed = subscriptionRepository.existsByUserIdAndAuthorId(
                userId, content.getAuthor().getId());

            if (!isSubscribed) {
                throw new BadRequestException("This is premium content. Subscribe to author '" 
                    + content.getAuthor().getName() + "' to access it.");
            }
        }
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

    public Page<ContentResponseDTO> getContentsByStatus(Status status, Pageable pageable) {
        return contentRepository.findByStatus(status, pageable)
                .map(contentMapper::toListResponseDTO);
    }

    public Page<ContentResponseDTO> getContentsByType(Type type, Pageable pageable) {
        return contentRepository.findByType(type, pageable)
                .map(contentMapper::toListResponseDTO);
    }

    public Page<ContentResponseDTO> getContentsByAuthorName(String authorName, Pageable pageable) {
        return contentRepository.findByAuthorName(authorName, pageable)
                .map(contentMapper::toListResponseDTO);
    }

    public Page<ContentResponseDTO> getContentsByTagName(String tagName, Pageable pageable) {
        return contentRepository.findByTagName(tagName, pageable)
                .map(contentMapper::toListResponseDTO);
    }

    public Page<ContentResponseDTO> getContentsByStatusAndAuthor(Status status, String authorName, Pageable pageable) {
        return contentRepository.findByStatusAndAuthorName(status, authorName, pageable)
                .map(contentMapper::toListResponseDTO);
    }

    public Page<ContentResponseDTO> getAllWithAuthorAndTags(Pageable pageable) {
        return contentRepository.findAllWithAuthorAndTags(pageable)
                .map(contentMapper::toResponseDTO);
    }

    public Page<ContentResponseDTO> getContentsByAuthorId(String authorId, String userId, Pageable pageable) {
        boolean includePremium = false;
        if (userId != null) {
            includePremium = subscriptionRepository.existsByUserIdAndAuthorId(userId, authorId);
        }
        return contentRepository.findByAuthorIdWithAccess(authorId, includePremium, pageable)
                .map(contentMapper::toListResponseDTO);
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
