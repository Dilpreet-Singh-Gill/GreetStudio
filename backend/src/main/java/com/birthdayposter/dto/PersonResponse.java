package com.birthdayposter.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PersonResponse {
    private Long id;
    private String name;
    private LocalDate dob;
    private String email;
    private String phone;
    private String department;
    private String designation;
    private String relationship;
    private String photoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
