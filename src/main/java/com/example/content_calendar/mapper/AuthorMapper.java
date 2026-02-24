package com.example.content_calendar.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.content_calendar.DTO.author.AuthorRequestDTO;
import com.example.content_calendar.DTO.author.AuthorResponseDTO;
import com.example.content_calendar.model.Author;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorResponseDTO toResponseDTO(Author author);

    List<AuthorResponseDTO> toResponseDTOList(List<Author> authors);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "followersCount", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "subscribers", ignore = true)
    Author toEntity(AuthorRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "followersCount", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "subscribers", ignore = true)
    void updateEntityFromDTO(AuthorRequestDTO dto, @MappingTarget Author author);
}
