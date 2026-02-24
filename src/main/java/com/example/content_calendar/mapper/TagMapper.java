package com.example.content_calendar.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.content_calendar.DTO.tag.TagRequestDTO;
import com.example.content_calendar.DTO.tag.TagResponseDTO;
import com.example.content_calendar.model.Tags;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagResponseDTO toResponseDTO(Tags tag);

    List<TagResponseDTO> toResponseDTOList(List<Tags> tags);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "contents", ignore = true)
    Tags toEntity(TagRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "contents", ignore = true)
    void updateEntityFromDTO(TagRequestDTO dto, @MappingTarget Tags tag);
}
