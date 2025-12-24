package org.threepixeldev.saungeraclient.features.auth.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserResponse user
) {}
