package com.birthdayposter.controller;

import com.birthdayposter.dto.ApiResponse;
import com.birthdayposter.dto.AuthRequest;
import com.birthdayposter.dto.AuthResponse;
import com.birthdayposter.dto.RegisterRequest;
import com.birthdayposter.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody AuthRequest loginRequest) {
        AuthResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully!"));
    }
}
