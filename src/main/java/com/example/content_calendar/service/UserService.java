package com.example.content_calendar.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.content_calendar.DTO.auth.AuthResponseDTO;
import com.example.content_calendar.DTO.author.AuthorResponseDTO;
import com.example.content_calendar.DTO.content.ContentResponseDTO;
import com.example.content_calendar.DTO.user.UserLoginDTO;
import com.example.content_calendar.DTO.user.UserRegisterDTO;
import com.example.content_calendar.DTO.user.UserResponseDTO;
import com.example.content_calendar.ExceptionHandler.BadRequestException;
import com.example.content_calendar.ExceptionHandler.ResourceNotFoundException;
import com.example.content_calendar.SecurityConfig.JwtService;
import com.example.content_calendar.mapper.AuthorMapper;
import com.example.content_calendar.mapper.ContentMapper;
import com.example.content_calendar.mapper.UserMapper;
import com.example.content_calendar.model.Author;
import com.example.content_calendar.model.Content;
import com.example.content_calendar.model.User;
import com.example.content_calendar.repository.AuthorRepository;
import com.example.content_calendar.repository.ContentCollectionRepository;
import com.example.content_calendar.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final ContentCollectionRepository contentRepository;
    private final UserMapper userMapper;
    private final AuthorMapper authorMapper;
    private final ContentMapper contentMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository,
                       AuthorRepository authorRepository,
                       ContentCollectionRepository contentRepository,
                       UserMapper userMapper,
                       AuthorMapper authorMapper,
                       ContentMapper contentMapper,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
        this.contentRepository = contentRepository;
        this.userMapper = userMapper;
        this.authorMapper = authorMapper;
        this.contentMapper = contentMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // --- Register ---

    public AuthResponseDTO register(UserRegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        if (userRepository.existsByUserName(dto.getUserName())) {
            throw new BadRequestException("Username already taken");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                saved.getEmail(), saved.getPassword(), Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponseDTO(token, saved.getId(), saved.getUserName(), saved.getEmail());
    }

    // --- Login ---

    public AuthResponseDTO login(UserLoginDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + dto.getEmail()));

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponseDTO(token, user.getId(), user.getUserName(), user.getEmail());
    }

    // --- Get profile ---

    public UserResponseDTO getUserById(String userId) {
        User user = userRepository.findByIdWithSubscriptions(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return userMapper.toResponseDTO(user);
    }

    // --- Subscribe ---

    public UserResponseDTO subscribe(String userId, String authorId) {
        User user = userRepository.findByIdWithSubscriptions(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Author author = authorRepository.findById(authorId)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));

        if (user.getSubscribedAuthors().contains(author)) {
            throw new BadRequestException("Already subscribed to this author");
        }

        user.getSubscribedAuthors().add(author);
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    // --- Unsubscribe ---

    public UserResponseDTO unsubscribe(String userId, String authorId) {
        User user = userRepository.findByIdWithSubscriptions(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Author author = authorRepository.findById(authorId)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));

        if (!user.getSubscribedAuthors().contains(author)) {
            throw new BadRequestException("Not subscribed to this author");
        }

        user.getSubscribedAuthors().remove(author);
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    // --- My subscriptions ---

    public List<AuthorResponseDTO> getSubscriptions(String userId) {
        User user = userRepository.findByIdWithSubscriptions(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return user.getSubscribedAuthors().stream()
            .map(authorMapper::toResponseDTO)
            .toList();
    }

    // --- Feed: content from subscribed authors ---

    public List<ContentResponseDTO> getFeed(String userId) {
        User user = userRepository.findByIdWithSubscriptions(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<String> authorIds = user.getSubscribedAuthors().stream()
            .map(Author::getId)
            .toList();

        if (authorIds.isEmpty()) {
            return List.of();
        }

        List<Content> feed = authorIds.stream()
            .flatMap(id -> contentRepository.findByAuthorId(id).stream())
            .toList();

        return contentMapper.toListResponseDTOList(feed);
    }
}
