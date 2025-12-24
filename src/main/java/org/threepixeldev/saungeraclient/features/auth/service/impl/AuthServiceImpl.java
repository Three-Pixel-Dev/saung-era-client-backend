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

import io.jsonwebtoken.Claims;
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

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .name(request.name())
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .build();

        userRepository.save(user);
        return generateAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        return generateAuthResponse(user);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        Claims claims = jwtService.validateToken(refreshToken);
        
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
        
        redisTemplate.opsForValue().set(
            OTP_PREFIX + user.getId(), 
            otp, 
            5, 
            TimeUnit.MINUTES
        );
        
        log.info("++++++++++++++++++++++++++++++++++++++++");
        log.info("OTP is (User: {}): {}", user.getUsername(), otp);
        log.info("++++++++++++++++++++++++++++++++++++++++");
    }

    @Override
    public void changePasswordWithOtp(ChangePasswordOtpRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = getAuthenticatedUser();
        String cachedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + user.getId());

        if (cachedOtp == null || !cachedOtp.equals(request.otp())) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        
        redisTemplate.delete(OTP_PREFIX + user.getId());
    }

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserPrincipal customPrincipal) {
            return userRepository.findById(customPrincipal.userId())
                    .orElseThrow(() -> new UnauthorizedException("User not found"));
        }
        throw new UnauthorizedException("User not authenticated");
    }

    private AuthResponse generateAuthResponse(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());

        String accessToken = jwtService.generateAccessToken(claims, user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(claims, user.getUsername());
        return new AuthResponse(
                accessToken,
                refreshToken,
                mapToUserResponse(user)
        );
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
    	        user.getId(),
    	        user.getName(),
    	        user.getUsername(),
    	        user.getEmail(),
    	        user.getPhoneNumber(),
    	        user.getCreatedAt()
    	);
    }
}
