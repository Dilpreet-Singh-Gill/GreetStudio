package com.birthdayposter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PersonRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Date of Birth is required")
    private LocalDate dob;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;
    private String department;
    private String designation;
    private String relationship;
}
