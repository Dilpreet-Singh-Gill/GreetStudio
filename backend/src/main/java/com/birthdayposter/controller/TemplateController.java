package com.birthdayposter.controller;

import com.birthdayposter.dto.ApiResponse;
import com.birthdayposter.dto.PagedResponse;
import com.birthdayposter.dto.TemplateResponse;
import com.birthdayposter.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping
    public ResponseEntity<TemplateResponse> uploadTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "textColor", required = false, defaultValue = "#FFFFFF") String textColor,
            @RequestParam(value = "boundingBoxes", required = false) String boundingBoxes) {
        return ResponseEntity.ok(templateService.uploadTemplate(file, name, textColor, boundingBoxes));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TemplateResponse>> getAllTemplates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(templateService.getAllTemplates(page, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok(new ApiResponse(true, "Template deleted successfully"));
    }
}
