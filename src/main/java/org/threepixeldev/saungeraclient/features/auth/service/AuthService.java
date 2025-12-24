package org.threepixeldev.saungeraclient.features.auth.service;

import org.threepixeldev.saungeraclient.features.auth.dto.request.ChangePasswordOtpRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.request.LoginRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.request.RefreshTokenRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.request.RegisterRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.response.AuthResponse;
import org.threepixeldev.saungeraclient.features.auth.dto.response.UserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(String token);
    UserResponse getCurrentUser();
    
    void requestPasswordChangeOtp();
    void changePasswordWithOtp(ChangePasswordOtpRequest request);
}
