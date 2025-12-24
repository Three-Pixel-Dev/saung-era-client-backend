package org.threepixeldev.saungeraclient.features.auth.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.threepixeldev.saungeraclient.features.auth.dto.request.ChangePasswordOtpRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.request.LoginRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.request.RefreshTokenRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.request.RegisterRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.response.AuthResponse;
import org.threepixeldev.saungeraclient.features.auth.dto.response.UserResponse;
import org.threepixeldev.saungeraclient.features.auth.service.AuthService;
import org.threepixeldev.saungeraclient.security.dto.CustomUserPrincipal;
import org.threepixeldev.saungeraclient.security.exceptions.UnauthorizedException;
import org.threepixeldev.saungeraclient.security.service.JwtService;
import org.threepixeldev.saungeraclient.shared.data.model.User;
import org.threepixeldev.saungeraclient.shared.data.repository.jpa.UserJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String OTP_PREFIX = "otp:pwd_change:";
    private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000; // 15 mins
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 days

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .build();

        userRepository.save(user);
        return generateAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        return generateAuthResponse(user);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        // This will throw exception if invalid/revoked
        var claims = jwtService.validateToken(refreshToken);
        
        String username = claims.getSubject();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        return generateAuthResponse(user);
    }

    @Override
    public void logout(String token) {
        jwtService.revokeToken(token);
    }

    @Override
    public UserResponse getCurrentUser() {
        User user = getAuthenticatedUser();
        return mapToUserResponse(user);
    }

    @Override
    public void requestPasswordChangeOtp() {
        User user = getAuthenticatedUser();
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Save to Redis (5 mins validity)
        redisTemplate.opsForValue().set(
            OTP_PREFIX + user.getId(), 
            otp, 
            5, 
            TimeUnit.MINUTES
        );
        
        // MOCK SENDING EMAIL
        log.info("==========================================");
        log.info("OTP for Password Change (User: {}): {}", user.getUsername(), otp);
        log.info("==========================================");
    }

    @Override
    public void changePasswordWithOtp(ChangePasswordOtpRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = getAuthenticatedUser();
        String cachedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + user.getId());

        if (cachedOtp == null || !cachedOtp.equals(request.getOtp())) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Clear OTP
        redisTemplate.delete(OTP_PREFIX + user.getId());
    }

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserPrincipal customPrincipal) {
            return userRepository.findById(customPrincipal.userId())
                    .orElseThrow(() -> new UnauthorizedException("User not found"));
        }
        // Fallback or error
        throw new UnauthorizedException("User not authenticated");
    }

    private AuthResponse generateAuthResponse(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());

        String accessToken = jwtService.generateToken(claims, user.getUsername(), ACCESS_TOKEN_VALIDITY);
        String refreshToken = jwtService.generateToken(claims, user.getUsername(), REFRESH_TOKEN_VALIDITY);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserResponse(user))
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
