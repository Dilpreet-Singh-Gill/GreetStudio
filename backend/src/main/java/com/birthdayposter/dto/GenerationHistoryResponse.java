package com.birthdayposter.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GenerationHistoryResponse {
    private Long id;
    private Long personId;
    private String personName;
    private String posterUrl;
    private String wishText;
    private String status;
    private LocalDateTime createdAt;
}
