package com.example.content_calendar.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.content_calendar.DTO.content.ContentResponseDTO;
import com.example.content_calendar.DTO.tag.TagRequestDTO;
import com.example.content_calendar.DTO.tag.TagResponseDTO;
import com.example.content_calendar.ExceptionHandler.BadRequestException;
import com.example.content_calendar.ExceptionHandler.ResourceNotFoundException;
import com.example.content_calendar.mapper.ContentMapper;
import com.example.content_calendar.mapper.TagMapper;
import com.example.content_calendar.model.Tags;
import com.example.content_calendar.repository.TagRepository;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final ContentMapper contentMapper;

    public TagService(TagRepository tagRepository, TagMapper tagMapper, ContentMapper contentMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
        this.contentMapper = contentMapper;
    }

    // --- CRUD ---

    public Page<TagResponseDTO> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable)
                .map(tagMapper::toResponseDTO);
    }

    public TagResponseDTO getTagById(String id) {
        Tags tag = tagRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));
        return tagMapper.toResponseDTO(tag);
    }

    public TagResponseDTO createTag(TagRequestDTO dto) {
        if (dto.getTagName() == null || dto.getTagName().isBlank()) {
            throw new BadRequestException("Tag name is required");
        }
        Tags tag = tagMapper.toEntity(dto);
        return tagMapper.toResponseDTO(tagRepository.save(tag));
    }

    public TagResponseDTO updateTag(String id, TagRequestDTO dto) {
        Tags tag = tagRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));
        tagMapper.updateEntityFromDTO(dto, tag);
        return tagMapper.toResponseDTO(tagRepository.save(tag));
    }

    public void deleteTag(String id) {
        Tags tag = tagRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));
        tagRepository.delete(tag);
    }

    // --- Join queries ---

    public Page<ContentResponseDTO> getContentsByTagName(String tagName, Pageable pageable) {
        Tags tag = tagRepository.findByTagName(tagName);
        if (tag == null) {
            throw new ResourceNotFoundException("Tag not found with name: " + tagName);
        }
        return tagRepository.findContentsByTagName(tagName, pageable)
                .map(contentMapper::toListResponseDTO);
    }

    public List<TagResponseDTO> getTagsByContentId(String contentId) {
        return tagMapper.toResponseDTOList(tagRepository.findTagsByContentId(contentId));
    }
}
