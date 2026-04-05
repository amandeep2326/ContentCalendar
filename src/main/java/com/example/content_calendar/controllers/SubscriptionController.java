package com.example.content_calendar.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.content_calendar.DTO.content.ContentResponseDTO;
import com.example.content_calendar.DTO.subscription.SubscriptionResponseDTO;
import com.example.content_calendar.service.SubscriptionService;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "*")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // POST /api/subscriptions/users/{userId}/authors/{authorId}
    @PostMapping("/users/{userId}/authors/{authorId}")
    public ResponseEntity<SubscriptionResponseDTO> subscribe(
            @PathVariable String userId, @PathVariable String authorId) {
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionService.subscribe(userId, authorId));
    }

    // DELETE /api/subscriptions/users/{userId}/authors/{authorId}
    @DeleteMapping("/users/{userId}/authors/{authorId}")
    public ResponseEntity<Void> unsubscribe(
            @PathVariable String userId, @PathVariable String authorId) {
        subscriptionService.unsubscribe(userId, authorId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/subscriptions/users/{userId}?page=0&size=10&sort=subscribedAt,desc
    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<SubscriptionResponseDTO>> getSubscriptions(
            @PathVariable String userId,
            @PageableDefault(size = 10, sort = "subscribedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(subscriptionService.getSubscriptions(userId, pageable));
    }

    // GET /api/subscriptions/authors/{authorId}?page=0&size=10&sort=subscribedAt,desc
    @GetMapping("/authors/{authorId}")
    public ResponseEntity<Page<SubscriptionResponseDTO>> getSubscribers(
            @PathVariable String authorId,
            @PageableDefault(size = 10, sort = "subscribedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(subscriptionService.getSubscribers(authorId, pageable));
    }

    // GET /api/subscriptions/users/{userId}/authors/{authorId}/status
    @GetMapping("/users/{userId}/authors/{authorId}/status")
    public ResponseEntity<Boolean> isSubscribed(
            @PathVariable String userId, @PathVariable String authorId) {
        return ResponseEntity.ok(subscriptionService.isSubscribed(userId, authorId));
    }

    // GET /api/subscriptions/users/{userId}/feed?page=0&size=10
    @GetMapping("/users/{userId}/feed")
    public ResponseEntity<Page<ContentResponseDTO>> getFeed(
            @PathVariable String userId,
            @PageableDefault(size = 10, sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(subscriptionService.getFeed(userId, pageable));
    }
}
