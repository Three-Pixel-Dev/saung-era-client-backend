package org.threepixeldev.saungeraclient.features.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyOtpRequest(@NotBlank String phoneNumber, @NotBlank String otp, @NotBlank String mode) {
}
