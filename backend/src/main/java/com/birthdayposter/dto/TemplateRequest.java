package com.birthdayposter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TemplateRequest {

    @NotBlank(message = "Template name is required")
    private String name;

    private String textColor;

    private String boundingBoxes;
}
