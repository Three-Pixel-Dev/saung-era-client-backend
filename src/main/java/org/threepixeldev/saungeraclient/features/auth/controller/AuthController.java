package org.threepixeldev.saungeraclient.features.auth.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.threepixeldev.saungeraclient.features.auth.dto.request.ChangePasswordOtpRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.request.LoginRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.request.RefreshTokenRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.request.RegisterRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.response.AuthResponse;
import org.threepixeldev.saungeraclient.features.auth.dto.response.UserResponse;
import org.threepixeldev.saungeraclient.features.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/client/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Client Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user (Revoke token)")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7));
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get current logged in user details")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PostMapping("/password/request-otp")
    @Operation(summary = "Request OTP for password change")
    public ResponseEntity<String> requestPasswordChangeOtp() {
        authService.requestPasswordChangeOtp();
        return ResponseEntity.ok("OTP sent successfully (Check server logs for mock otp)");
    }

    @PostMapping("/password/change")
    @Operation(summary = "Change password using OTP")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordOtpRequest request) {
        authService.changePasswordWithOtp(request);
        return ResponseEntity.ok("Password changed successfully");
    }
}
