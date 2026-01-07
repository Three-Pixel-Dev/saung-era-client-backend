package org.threepixeldev.saungeraclient.features.auth.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.threepixeldev.saungeraclient.features.auth.dto.request.*;
import org.threepixeldev.saungeraclient.features.auth.dto.response.AuthResponse;
import org.threepixeldev.saungeraclient.features.auth.dto.response.UserResponse;
import org.threepixeldev.saungeraclient.features.auth.service.AuthService;

import java.util.Map;

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
    
    @PostMapping("/verify")
    @Operation(summary = "Validate all fields before registration process (fail first)")
    public ResponseEntity<String> verifyFields(@RequestBody @Valid VerifyRegisterRequest request) {
    	authService.verifyRegister(request);
        return ResponseEntity.ok("Fields validation success.");
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login user with email or phone number")
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

    @PostMapping("/request-otp")
    @Operation(summary = "Request OTP with mode (register or password)")
    public ResponseEntity<String> requestPasswordChangeOtp(@Valid @RequestBody OtpRequest request) {
        authService.requestOtp(request);
        return ResponseEntity.ok("OTP sent successfully (Check server logs for mock otp)");
    }
    
    @PostMapping("/otp/verify")
    @Operation(summary = "Verify OTP")
    public ResponseEntity<Map<String, String>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        String token = authService.verifyOtp(request);
        
        return ResponseEntity.ok(Map.of("verificationToken", token));
    }
    @PostMapping("/password/change")
    @Operation(summary = "Change password using OTP")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/google")
    @Operation(summary = "Login or Register with Google")
    public ResponseEntity<AuthResponse> loginWithGoogle(@RequestBody @Valid GoogleLoginRequest request) {
        System.out.println("Token is "+request.idToken());
        return ResponseEntity.ok(authService.loginWithGoogle(request));
    }
}
