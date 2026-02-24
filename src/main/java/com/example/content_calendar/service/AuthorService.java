package com.example.content_calendar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.content_calendar.DTO.author.AuthorRequestDTO;
import com.example.content_calendar.DTO.author.AuthorResponseDTO;
import com.example.content_calendar.DTO.content.ContentResponseDTO;
import com.example.content_calendar.DTO.tag.TagResponseDTO;
import com.example.content_calendar.DTO.user.UserResponseDTO;
import com.example.content_calendar.ExceptionHandler.BadRequestException;
import com.example.content_calendar.ExceptionHandler.ResourceNotFoundException;
import com.example.content_calendar.mapper.AuthorMapper;
import com.example.content_calendar.mapper.ContentMapper;
import com.example.content_calendar.mapper.TagMapper;
import com.example.content_calendar.mapper.UserMapper;
import com.example.content_calendar.model.Author;
import com.example.content_calendar.model.User;
import com.example.content_calendar.repository.AuthorRepository;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final ContentMapper contentMapper;
    private final TagMapper tagMapper;
    private final UserMapper userMapper;

    public AuthorService(AuthorRepository authorRepository,
                         AuthorMapper authorMapper,
                         ContentMapper contentMapper,
                         TagMapper tagMapper,
                         UserMapper userMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
        this.contentMapper = contentMapper;
        this.tagMapper = tagMapper;
        this.userMapper = userMapper;
    }

    // --- CRUD ---

    public List<AuthorResponseDTO> getAllAuthors() {
        return authorMapper.toResponseDTOList(authorRepository.findAll());
    }

    public AuthorResponseDTO getAuthorById(String id) {
        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        return authorMapper.toResponseDTO(author);
    }

    public AuthorResponseDTO createAuthor(AuthorRequestDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BadRequestException("Author name is required");
        }
        Author author = authorMapper.toEntity(dto);
        return authorMapper.toResponseDTO(authorRepository.save(author));
    }

    public AuthorResponseDTO updateAuthor(String id, AuthorRequestDTO dto) {
        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        authorMapper.updateEntityFromDTO(dto, author);
        return authorMapper.toResponseDTO(authorRepository.save(author));
    }

    public void deleteAuthor(String id) {
        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        authorRepository.delete(author);
    }

    // --- Join query methods ---

    public List<ContentResponseDTO> getContentByAuthorId(String authorId) {
        authorRepository.findById(authorId)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
        return contentMapper.toListResponseDTOList(authorRepository.findContentByAuthorId(authorId));
    }

    public List<TagResponseDTO> getTagsByAuthorId(String authorId) {
        authorRepository.findById(authorId)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
        return tagMapper.toResponseDTOList(authorRepository.findTagsByAuthorId(authorId));
    }

    public List<UserResponseDTO> getSubscribersByAuthorId(String authorId) {
        authorRepository.findById(authorId)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
        List<User> subscribers = authorRepository.findSubscribersByAuthorId(authorId);
        return subscribers.stream()
            .map(userMapper::toResponseDTO)
            .toList();
    }
}
