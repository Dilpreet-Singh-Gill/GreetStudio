package com.birthdayposter.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TemplateResponse {
    private Long id;
    private String name;
    private String templateUrl;
    private String textColor;
    private String boundingBoxes;
    private LocalDateTime createdAt;
}
