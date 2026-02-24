package com.example.content_calendar.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.content_calendar.DTO.author.AuthorResponseDTO;
import com.example.content_calendar.DTO.user.UserRegisterDTO;
import com.example.content_calendar.DTO.user.UserResponseDTO;
import com.example.content_calendar.model.Author;
import com.example.content_calendar.model.User;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class})
public interface UserMapper {

    DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Mapping(source = "author", target = "author", qualifiedByName = "hasAuthor")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "dateToString")
    @Mapping(source = "subscribedAuthors", target = "subscribedAuthors", qualifiedByName = "authorsToList")
    UserResponseDTO toResponseDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "subscribedAuthors", ignore = true)
    User toEntity(UserRegisterDTO dto);

    @Named("hasAuthor")
    default boolean hasAuthor(Author author) {
        return author != null;
    }

    @Named("dateToString")
    default String dateToString(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATE_FMT);
    }

    @Named("authorsToList")
    default List<AuthorResponseDTO> authorsToList(Set<Author> authors) {
        if (authors == null) return List.of();
        List<AuthorResponseDTO> list = new ArrayList<>();
        for (Author a : authors) {
            AuthorResponseDTO dto = new AuthorResponseDTO();
            dto.setId(a.getId());
            dto.setName(a.getName());
            dto.setEmail(a.getEmail());
            dto.setBio(a.getBio());
            dto.setProfilePictureUrl(a.getProfilePictureUrl());
            dto.setFollowersCount(a.getFollowersCount());
            dto.setSocialMediaLinks(a.getSocialMediaLinks());
            list.add(dto);
        }
        return list;
    }
}
