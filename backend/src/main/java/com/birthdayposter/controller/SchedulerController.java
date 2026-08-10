package com.birthdayposter.controller;

import com.birthdayposter.dto.ApiResponse;
import com.birthdayposter.service.PosterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
@Slf4j
public class SchedulerController {

    private final PosterService posterService;

    @Value("${app.cron-secret}")
    private String cronSecret;

    @PostMapping("/daily-run")
    public ResponseEntity<ApiResponse> triggerDailyRun(@RequestHeader(value = "X-Cron-Secret", required = false) String secretHeader) {
        if (secretHeader == null || !secretHeader.equals(cronSecret)) {
            log.warn("Unauthorized attempt to trigger daily run");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Invalid Cron Secret"));
        }

        // Run asynchronously so the webhook returns immediately
        new Thread(() -> {
            try {
                posterService.runDailyAutomation();
            } catch (Exception e) {
                log.error("Error during automated daily run", e);
            }
        }).start();

        return ResponseEntity.ok(new ApiResponse(true, "Daily run triggered successfully"));
    }
}
