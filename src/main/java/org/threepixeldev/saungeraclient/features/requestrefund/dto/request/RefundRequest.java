package org.threepixeldev.saungeraclient.features.requestrefund.dto.request;
import jakarta.validation.constraints.NotBlank;

public record RefundRequest(
        @NotBlank(message = "Reason is required")
        String reason
) {}