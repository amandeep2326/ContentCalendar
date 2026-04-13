package com.example.content_calendar.DTO.content;

import java.io.Serializable;
import java.util.Set;

import com.example.content_calendar.model.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponseDTO implements Serializable {
    private String id;
    private String title;
    private String description;
    private String authorId;
    private String authorName;
    private String publishedDate;
    private Status status;
    private String type;
    private Set<TagSummaryDTO> tags;
    private boolean premium;
}
