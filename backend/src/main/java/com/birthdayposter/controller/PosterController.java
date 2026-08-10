package com.birthdayposter.controller;

import com.birthdayposter.dto.GenerationHistoryResponse;
import com.birthdayposter.dto.PagedResponse;
import com.birthdayposter.service.PosterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/poster")
@RequiredArgsConstructor
public class PosterController {

    private final PosterService posterService;

    @PostMapping("/generate/{personId}")
    public ResponseEntity<GenerationHistoryResponse> generatePoster(
            @PathVariable Long personId,
            @RequestParam(required = false) Long templateId) {
        return ResponseEntity.ok(posterService.generatePoster(personId, templateId));
    }

    @GetMapping("/history")
    public ResponseEntity<PagedResponse<GenerationHistoryResponse>> getPosterHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(posterService.getPosterHistory(page, size));
    }
}
