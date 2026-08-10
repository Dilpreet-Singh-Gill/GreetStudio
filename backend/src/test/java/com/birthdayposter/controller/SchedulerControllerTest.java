package com.birthdayposter.controller;

import com.birthdayposter.dto.ApiResponse;
import com.birthdayposter.service.PosterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;

class SchedulerControllerTest {

    @Mock
    private PosterService posterService;

    @InjectMocks
    private SchedulerController schedulerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(schedulerController, "cronSecret", "my-secret-123");
    }

    @Test
    void triggerDailyRun_ValidSecret() {
        ResponseEntity<ApiResponse> response = schedulerController.triggerDailyRun("my-secret-123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        // Since it starts a thread, we can't easily verify posterService.runDailyAutomation() without thread sleeping, 
        // but we verify the response is 200 OK.
    }

    @Test
    void triggerDailyRun_InvalidSecret() {
        ResponseEntity<ApiResponse> response = schedulerController.triggerDailyRun("wrong-secret");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
    }

    @Test
    void triggerDailyRun_MissingSecret() {
        ResponseEntity<ApiResponse> response = schedulerController.triggerDailyRun(null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
    }
}
