package com.example.content_calendar.DTO.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String id;
    private String userName;
    private String email;
    private String createdAt;
    private boolean author;
    private String authorId;
}
