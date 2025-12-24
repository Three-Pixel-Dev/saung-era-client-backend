package org.threepixeldev.saungeraclient.features.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordOtpRequest(
	    @NotBlank String otp,
	    @NotBlank String newPassword,
	    @NotBlank String confirmPassword
	) {}
