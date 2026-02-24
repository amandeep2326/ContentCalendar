package com.example.content_calendar.DTO.user;

import java.util.List;

import com.example.content_calendar.DTO.author.AuthorResponseDTO;

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
    private List<AuthorResponseDTO> subscribedAuthors;
}
