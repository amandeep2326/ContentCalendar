package com.example.content_calendar.DTO.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagSummaryDTO {
    private String id;
    private String tagName;
    private String colorCode;
}
