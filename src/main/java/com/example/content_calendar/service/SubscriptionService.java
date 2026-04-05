package com.example.content_calendar.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.content_calendar.DTO.content.ContentResponseDTO;
import com.example.content_calendar.DTO.subscription.SubscriptionResponseDTO;
import com.example.content_calendar.ExceptionHandler.ResourceNotFoundException;
import com.example.content_calendar.mapper.ContentMapper;
import com.example.content_calendar.model.Author;
import com.example.content_calendar.model.Content;
import com.example.content_calendar.model.Subscription;
import com.example.content_calendar.model.User;
import com.example.content_calendar.repository.AuthorRepository;
import com.example.content_calendar.repository.ContentCollectionRepository;
import com.example.content_calendar.repository.SubscriptionRepository;
import com.example.content_calendar.repository.UserRepository;

@Service
public class SubscriptionService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final ContentCollectionRepository contentRepository;
    private final ContentMapper contentMapper;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository,
                               AuthorRepository authorRepository,
                               ContentCollectionRepository contentRepository,
                               ContentMapper contentMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
        this.contentRepository = contentRepository;
        this.contentMapper = contentMapper;
    }

    // --- Subscribe (idempotent) ---

    @Transactional
    public SubscriptionResponseDTO subscribe(String userId, String authorId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));

        // Idempotent: return existing subscription if already present
        Optional<Subscription> existing = subscriptionRepository.findByUserIdAndAuthorId(userId, authorId);
        if (existing.isPresent()) {
            return toResponseDTO(existing.get());
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setAuthor(author);
        subscription.setSubscribedAt(LocalDateTime.now());

        return toResponseDTO(subscriptionRepository.save(subscription));
    }

    // --- Unsubscribe (idempotent) ---

    @Transactional
    public void unsubscribe(String userId, String authorId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));

        // Idempotent: no-op if not subscribed
        subscriptionRepository.deleteByUserIdAndAuthorId(userId, authorId);
    }

    // --- Get all subscriptions for a user ---

    @Transactional(readOnly = true)
    public List<SubscriptionResponseDTO> getSubscriptions(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return subscriptionRepository.findByUserIdWithAuthor(userId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // --- Get all subscribers of an author ---

    @Transactional(readOnly = true)
    public List<SubscriptionResponseDTO> getSubscribers(String authorId) {
        authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));

        return subscriptionRepository.findByAuthorIdWithUser(authorId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // --- Check if subscribed ---

    @Transactional(readOnly = true)
    public boolean isSubscribed(String userId, String authorId) {
        return subscriptionRepository.existsByUserIdAndAuthorId(userId, authorId);
    }

    // --- Feed: content from subscribed authors ---

    @Transactional(readOnly = true)
    public List<ContentResponseDTO> getFeed(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<String> authorIds = subscriptionRepository.findByUserIdWithAuthor(userId).stream()
                .map(s -> s.getAuthor().getId())
                .toList();

        if (authorIds.isEmpty()) {
            return List.of();
        }

        List<Content> feed = authorIds.stream()
                .flatMap(id -> contentRepository.findByAuthorId(id).stream())
                .toList();

        return contentMapper.toListResponseDTOList(feed);
    }

    // --- Helper ---

    private SubscriptionResponseDTO toResponseDTO(Subscription subscription) {
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.setId(subscription.getId());
        dto.setUserId(subscription.getUser().getId());
        dto.setAuthorId(subscription.getAuthor().getId());
        dto.setAuthorName(subscription.getAuthor().getName());
        dto.setSubscribedAt(subscription.getSubscribedAt().format(DATE_FMT));
        return dto;
    }
}
