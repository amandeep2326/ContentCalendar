package com.example.content_calendar.DTO.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRequestDTO {
    private String tagName;
    private String description;
    private String colorCode;
    private String iconUrl;
}
