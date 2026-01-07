package org.threepixeldev.saungeraclient.features.auth.service;

import org.threepixeldev.saungeraclient.features.auth.dto.request.*;
import org.threepixeldev.saungeraclient.features.auth.dto.response.AuthResponse;
import org.threepixeldev.saungeraclient.features.auth.dto.response.UserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    void verifyRegister(VerifyRegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(String token);
    UserResponse getCurrentUser();
    
    void requestOtp(OtpRequest request);
    String verifyOtp(VerifyOtpRequest request);
    void changePassword(ChangePasswordRequest request);

    AuthResponse loginWithGoogle(GoogleLoginRequest request);
}
