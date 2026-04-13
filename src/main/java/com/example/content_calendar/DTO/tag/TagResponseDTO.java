package com.example.content_calendar.DTO.tag;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagResponseDTO implements Serializable {
    private String id;
    private String tagName;
    private String description;
    private String colorCode;
    private String iconUrl;
}
