package com.example.content_calendar.DTO.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponseDTO {
    private String id;
    private String userId;
    private String authorId;
    private String authorName;
    private String subscribedAt;
}
