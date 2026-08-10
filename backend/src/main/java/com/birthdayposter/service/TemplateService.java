package com.birthdayposter.service;

import com.birthdayposter.dto.PagedResponse;
import com.birthdayposter.dto.TemplateResponse;
import com.birthdayposter.entity.Template;
import com.birthdayposter.entity.User;
import com.birthdayposter.exception.BadRequestException;
import com.birthdayposter.exception.ResourceNotFoundException;
import com.birthdayposter.repository.TemplateRepository;
import com.birthdayposter.repository.UserRepository;
import com.birthdayposter.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    private static final String TEMPLATE_FOLDER = "greet-studio/templates";

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    private User getCurrentUser() {
        return userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public TemplateResponse uploadTemplate(MultipartFile file, String name, String textColor, String boundingBoxes) {
        if (file.isEmpty()) {
            throw new BadRequestException("Template image file is required.");
        }

        Map<String, String> uploadResult = cloudinaryService.uploadImage(file, TEMPLATE_FOLDER);

        Template template = Template.builder()
                .name(name)
                .templateUrl(uploadResult.get("url"))
                .templatePublicId(uploadResult.get("public_id"))
                .textColor(textColor)
                .boundingBoxes(boundingBoxes)
                .user(getCurrentUser())
                .build();

        Template saved = templateRepository.save(template);
        return mapToResponse(saved);
    }

    public PagedResponse<TemplateResponse> getAllTemplates(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Template> templatePage = templateRepository.findByUserId(getCurrentUserId(), pageable);

        List<TemplateResponse> content = templatePage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                templatePage.getNumber(),
                templatePage.getSize(),
                templatePage.getTotalElements(),
                templatePage.getTotalPages(),
                templatePage.isLast()
        );
    }

    public void deleteTemplate(Long id) {
        Template template = templateRepository.findByIdAndUserId(id, getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Template not found"));

        // Delete from Cloudinary first
        cloudinaryService.deleteImage(template.getTemplatePublicId());

        // Then delete from database
        templateRepository.delete(template);
    }

    private TemplateResponse mapToResponse(Template template) {
        TemplateResponse response = new TemplateResponse();
        response.setId(template.getId());
        response.setName(template.getName());
        response.setTemplateUrl(template.getTemplateUrl());
        response.setTextColor(template.getTextColor());
        response.setBoundingBoxes(template.getBoundingBoxes());
        response.setCreatedAt(template.getCreatedAt());
        return response;
    }
}
