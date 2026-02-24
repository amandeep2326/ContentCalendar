package com.example.content_calendar.DTO.author;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequestDTO {
    private String name;
    private String email;
    private String bio;
    private String profilePictureUrl;
    private List<String> socialMediaLinks;
}
