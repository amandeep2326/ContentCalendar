package com.example.content_calendar.DTO.content;

import java.util.Set;

import com.example.content_calendar.model.Status;
import com.example.content_calendar.model.Type;
import com.example.content_calendar.validation.NullOrNotBlank;
import com.example.content_calendar.validation.ValidEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NullOrNotBlank(message = "Author ID must not be blank")
    private String authorId;

    @NullOrNotBlank(message = "Published date must not be blank")
    private String publishedDate;

    @ValidEnum(enumClass = Status.class, message = "Invalid status")
    private Status status;

    @NullOrNotBlank(message = "Type must not be blank")
    @ValidEnum(enumClass = Type.class, message = "Invalid type. Allowed values: IDEA, ARTICLE, VIDEO, CONFERENCE")
    private String type;

    private Set<String> tagIds;

    private Boolean premium;
}
