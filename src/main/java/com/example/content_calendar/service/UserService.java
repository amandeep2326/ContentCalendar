package com.example.content_calendar.service;

import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.content_calendar.DTO.auth.AuthResponseDTO;
import com.example.content_calendar.DTO.user.UserLoginDTO;
import com.example.content_calendar.DTO.user.UserRegisterDTO;
import com.example.content_calendar.DTO.user.UserResponseDTO;
import com.example.content_calendar.ExceptionHandler.BadRequestException;
import com.example.content_calendar.ExceptionHandler.ResourceNotFoundException;
import com.example.content_calendar.SecurityConfig.JwtService;
import com.example.content_calendar.mapper.UserMapper;
import com.example.content_calendar.model.User;
import com.example.content_calendar.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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
        user.setRole(com.example.content_calendar.model.Role.USER);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                saved.getEmail(), saved.getPassword(), Collections.emptyList());
        String token = jwtService.generateToken(userDetails, saved.getRole().name(), saved.getId());

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
        String token = jwtService.generateToken(userDetails, user.getRole().name(), user.getId());

        return new AuthResponseDTO(token, user.getId(), user.getUserName(), user.getEmail());
    }

    // --- Get profile ---

    public UserResponseDTO getUserById(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return userMapper.toResponseDTO(user);
    }
}
