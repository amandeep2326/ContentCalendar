package com.example.content_calendar.DTO.tag;

import com.example.content_calendar.validation.NullOrNotBlank;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRequestDTO {

    @NotBlank(message = "Tag name is required")
    @Size(min = 2, max = 50, message = "Tag name must be between 2 and 50 characters")
    private String tagName;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NullOrNotBlank(message = "Color code must not be blank")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Color code must be a valid hex color (e.g. #FF5733)")
    private String colorCode;

    @NullOrNotBlank(message = "Icon URL must not be blank")
    private String iconUrl;
}
