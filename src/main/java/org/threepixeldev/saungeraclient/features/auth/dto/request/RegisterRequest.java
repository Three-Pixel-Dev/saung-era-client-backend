package org.threepixeldev.saungeraclient.features.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record RegisterRequest(
        @NotBlank(message = "Name is required")
        String name,
        
        @NotBlank(message = "Username is required")
        String username,
        
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        
        @NotBlank(message = "Password is required")
        String password,
        
        String phoneNumber
) {}
