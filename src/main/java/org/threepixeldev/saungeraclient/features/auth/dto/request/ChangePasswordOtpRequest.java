package org.threepixeldev.saungeraclient.features.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordOtpRequest {
    @NotBlank
    private String otp;
    @NotBlank
    private String newPassword;
    @NotBlank
    private String confirmPassword;
}
