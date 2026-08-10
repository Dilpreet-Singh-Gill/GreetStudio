package com.birthdayposter.controller;

import com.birthdayposter.dto.ApiResponse;
import com.birthdayposter.entity.Person;
import com.birthdayposter.exception.ResourceNotFoundException;
import com.birthdayposter.repository.PersonRepository;
import com.birthdayposter.security.UserDetailsImpl;
import com.birthdayposter.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final CloudinaryService cloudinaryService;
    private final PersonRepository personRepository;

    private static final String PHOTO_FOLDER = "greet-studio/photos";

    @PostMapping("/upload-photo/{personId}")
    public ResponseEntity<ApiResponse> uploadPersonPhoto(
            @PathVariable Long personId,
            @RequestParam("file") MultipartFile file) {

        Long userId = getCurrentUserId();
        Person person = personRepository.findByIdAndUserId(personId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        // Delete old photo from Cloudinary if it exists
        if (person.getPhotoPublicId() != null && !person.getPhotoPublicId().isBlank()) {
            cloudinaryService.deleteImage(person.getPhotoPublicId());
        }

        // Upload new photo
        Map<String, String> result = cloudinaryService.uploadImage(file, PHOTO_FOLDER);

        // Update person entity
        person.setPhotoUrl(result.get("url"));
        person.setPhotoPublicId(result.get("public_id"));
        personRepository.save(person);

        return ResponseEntity.ok(new ApiResponse(true, result.get("url")));
    }

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getId();
    }
}
