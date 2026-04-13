package com.example.content_calendar.DTO.author;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorResponseDTO implements Serializable {
    private String id;
    private String name;
    private String email;
    private String bio;
    private String profilePictureUrl;
    private BigInteger followersCount;
    private List<String> socialMediaLinks;
}
