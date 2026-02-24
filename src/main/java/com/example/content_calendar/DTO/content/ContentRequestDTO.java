package com.example.content_calendar.DTO.content;

import java.util.Set;

import com.example.content_calendar.model.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentRequestDTO {
    private String title;
    private String description;
    private String authorId;
    private String publishedDate;
    private Status status;
    private String type;
    private Set<String> tagIds;
}
