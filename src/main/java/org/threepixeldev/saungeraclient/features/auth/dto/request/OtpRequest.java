package org.threepixeldev.saungeraclient.features.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OtpRequest(
	@NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
    String phoneNumber,

    @NotBlank(message = "Mode is required")
    @Pattern(regexp = "^(register|password)$", message = "Mode must be 'register' or 'password'")
    String mode
) {}
