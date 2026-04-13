package com.example.content_calendar.DTO.content;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagSummaryDTO implements Serializable {
    private String id;
    private String tagName;
    private String colorCode;
}
