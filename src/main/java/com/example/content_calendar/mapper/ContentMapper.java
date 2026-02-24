package com.example.content_calendar.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.example.content_calendar.DTO.content.ContentRequestDTO;
import com.example.content_calendar.DTO.content.ContentResponseDTO;
import com.example.content_calendar.DTO.content.TagSummaryDTO;
import com.example.content_calendar.model.Content;
import com.example.content_calendar.model.Tags;
import com.example.content_calendar.model.Type;

@Mapper(componentModel = "spring")
public interface ContentMapper {

    DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // ── Entity → Response DTO ──

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "tags", target = "tags", qualifiedByName = "tagsToSummary")
    @Mapping(source = "publishedDate", target = "publishedDate", qualifiedByName = "localDateTimeToString")
    @Mapping(source = "type", target = "type", qualifiedByName = "typeToString")
    ContentResponseDTO toResponseDTO(Content content);

    // ── Entity → List DTO (lightweight – no description/tags for card/table views) ──

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "publishedDate", target = "publishedDate", qualifiedByName = "localDateTimeToString")
    @Mapping(source = "type", target = "type", qualifiedByName = "typeToString")
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "tags", ignore = true)
    ContentResponseDTO toListResponseDTO(Content content);

    default List<ContentResponseDTO> toResponseDTOList(List<Content> contents) {
        if (contents == null) return null;
        return contents.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    default List<ContentResponseDTO> toListResponseDTOList(List<Content> contents) {
        if (contents == null) return null;
        return contents.stream()
                .map(this::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // ── Request DTO → Entity (partial – author & tags resolved in service) ──

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "upDatedAt", ignore = true)
    @Mapping(source = "publishedDate", target = "publishedDate", qualifiedByName = "stringToLocalDateTime")
    @Mapping(source = "type", target = "type", qualifiedByName = "stringToType")
    Content toEntity(ContentRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "upDatedAt", ignore = true)
    @Mapping(source = "publishedDate", target = "publishedDate", qualifiedByName = "stringToLocalDateTime")
    @Mapping(source = "type", target = "type", qualifiedByName = "stringToType")
    void updateEntityFromDTO(ContentRequestDTO dto, @MappingTarget Content content);

    // ── Date converters ──

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATE_FMT);
    }

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String dateTime) {
        return dateTime == null || dateTime.isBlank() ? null : LocalDateTime.parse(dateTime, DATE_FMT);
    }

    // ── Type converters ──

    @Named("typeToString")
    default String typeToString(Type type) {
        return type == null ? null : type.name();
    }

    @Named("stringToType")
    default Type stringToType(String type) {
        return type == null || type.isBlank() ? null : Type.valueOf(type.toUpperCase());
    }

    // ── Tag helpers ──

    TagSummaryDTO tagToSummary(Tags tag);

    @Named("tagsToSummary")
    default Set<TagSummaryDTO> tagsToSummary(Set<Tags> tags) {
        if (tags == null) return null;
        return tags.stream()
                .map(this::tagToSummary)
                .collect(Collectors.toSet());
    }
}
