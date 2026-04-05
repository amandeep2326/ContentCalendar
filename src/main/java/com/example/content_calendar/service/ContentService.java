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
    public List<ContentResponseDTO> getFreeContents() {
        return contentMapper.toListResponseDTOList(contentRepository.findByPremiumFalse());
    }

    // Returns free content + premium content from subscribed authors (for authenticated users)
    public List<ContentResponseDTO> getContentsForUser(String userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<String> subscribedAuthorIds = subscriptionRepository.findByUserId(userId).stream()
            .map(s -> s.getAuthor().getId())
            .toList();

        List<Content> contents;
        if (subscribedAuthorIds.isEmpty()) {
            contents = contentRepository.findByPremiumFalse();
        } else {
            contents = contentRepository.findAccessibleContent(subscribedAuthorIds);
        }
        return contentMapper.toListResponseDTOList(contents);
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

    public List<ContentResponseDTO> getContentsByAuthorId(String authorId, String userId) {
        boolean includePremium = false;
        if (userId != null) {
            includePremium = subscriptionRepository.existsByUserIdAndAuthorId(userId, authorId);
        }
        return contentMapper.toListResponseDTOList(
            contentRepository.findByAuthorIdWithAccess(authorId, includePremium));
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
