package com.example.content_calendar.controllers;

import java.util.List;

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

    // GET /api/subscriptions/users/{userId}
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<SubscriptionResponseDTO>> getSubscriptions(@PathVariable String userId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptions(userId));
    }

    // GET /api/subscriptions/authors/{authorId}
    @GetMapping("/authors/{authorId}")
    public ResponseEntity<List<SubscriptionResponseDTO>> getSubscribers(@PathVariable String authorId) {
        return ResponseEntity.ok(subscriptionService.getSubscribers(authorId));
    }

    // GET /api/subscriptions/users/{userId}/authors/{authorId}/status
    @GetMapping("/users/{userId}/authors/{authorId}/status")
    public ResponseEntity<Boolean> isSubscribed(
            @PathVariable String userId, @PathVariable String authorId) {
        return ResponseEntity.ok(subscriptionService.isSubscribed(userId, authorId));
    }

    // GET /api/subscriptions/users/{userId}/feed
    @GetMapping("/users/{userId}/feed")
    public ResponseEntity<List<ContentResponseDTO>> getFeed(@PathVariable String userId) {
        return ResponseEntity.ok(subscriptionService.getFeed(userId));
    }
}
