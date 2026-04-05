package com.example.content_calendar.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.content_calendar.DTO.user.UserRegisterDTO;
import com.example.content_calendar.DTO.user.UserResponseDTO;
import com.example.content_calendar.model.Author;
import com.example.content_calendar.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Mapping(source = "author", target = "author", qualifiedByName = "hasAuthor")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "dateToString")
    UserResponseDTO toResponseDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserRegisterDTO dto);

    @Named("hasAuthor")
    default boolean hasAuthor(Author author) {
        return author != null;
    }

    @Named("dateToString")
    default String dateToString(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATE_FMT);
    }
}
