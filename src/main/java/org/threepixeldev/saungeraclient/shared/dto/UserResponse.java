package org.threepixeldev.saungeraclient.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User information response DTO used across all features")
public class UserResponse {
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    @Schema(description = "Full name of the user", example = "Admin User")
    private String name;

    @Schema(description = "Username of the user", example = "admin")
    private String username;

    @Schema(description = "Email address of the user", example = "admin@saungera.com")
    private String email;

    @Schema(description = "Phone number of the user", example = "+1234567890")
    private String phoneNumber;
}

