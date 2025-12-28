package org.threepixeldev.saungeraclient.features.auth.service.impl;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.threepixeldev.saungeraclient.features.auth.dto.request.ChangePasswordRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.request.LoginRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.request.OtpRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.request.RefreshTokenRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.request.RegisterRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.request.VerifyOtpRequest;
import org.threepixeldev.saungeraclient.features.auth.dto.response.AuthResponse;
import org.threepixeldev.saungeraclient.features.auth.dto.response.UserResponse;
import org.threepixeldev.saungeraclient.features.auth.service.AuthService;
import org.threepixeldev.saungeraclient.security.exceptions.UnauthorizedException;
import org.threepixeldev.saungeraclient.security.service.JwtService;
import org.threepixeldev.saungeraclient.shared.data.model.User;
import org.threepixeldev.saungeraclient.shared.data.repository.jpa.UserJpaRepository;
import org.threepixeldev.saungeraclient.shared.utls.PhoneNumberHelper;
import org.threepixeldev.saungeraclient.shared.utls.SecurityUtils;

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

    private static final String OTP_PWD_PREFIX = "otp:pwd_change:";
    private static final String OTP_REG_PREFIX = "otp:register_req:";
    private static final String RATE_LIMIT_PREFIX = "otp:rate_limit:";
    private static final String VERIFIED_TOKEN_PREFIX = "verify_token:";
    @Override
    public AuthResponse register(RegisterRequest request) {
    	String tokenKey = VERIFIED_TOKEN_PREFIX + request.verificationToken();
    	String tokenValue = redisTemplate.opsForValue().get(tokenKey);

    	if (tokenValue == null) {
            throw new IllegalArgumentException("Session expired");
        }
    	String[] parts = tokenValue.split(":");
        String verifiedPhone = PhoneNumberHelper.normalizePhoneNumber(parts[0]);
        String verifiedMode = parts[1];

        if (!"register".equals(verifiedMode)) {
            throw new IllegalArgumentException("Invalid token scope. This token cannot be used for registration.");
        }
        String requestPhone = PhoneNumberHelper.normalizePhoneNumber(request.phoneNumber());
        System.out.println("REQUESTED PHONE "+ requestPhone);
        System.out.println("VERIFIED PHONE "+verifiedPhone);
        if (!verifiedPhone.equals(requestPhone)) {
             throw new IllegalArgumentException("Phone number mismatch");
        }
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
                .phoneNumber(verifiedPhone)
                .build();

        userRepository.save(user);
        redisTemplate.delete(tokenKey);
        return generateAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
    	boolean isEmail = request.identifier().contains("@");
    	User user;
        if (isEmail) {
            user = userRepository.findByEmail(request.identifier())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with this email"));
        } else {
            user = userRepository.findByPhoneNumber(PhoneNumberHelper.normalizePhoneNumber(request.identifier()))
                    .orElseThrow(() -> new IllegalArgumentException("User not found with this phone number"));
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.password())
        );

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
    public void requestOtp(OtpRequest request) {
        String phoneNumber = PhoneNumberHelper.normalizePhoneNumber(request.phoneNumber());
        String mode = request.mode();

        String prefix = mode.equals("password") ? OTP_PWD_PREFIX : OTP_REG_PREFIX;

        String rateLimitKey = RATE_LIMIT_PREFIX + phoneNumber;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(rateLimitKey))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Please wait 60 seconds before requesting another OTP");
        }

        String otp = String.format("%06d", new SecureRandom().nextInt(999999));

        String otpKey = prefix + phoneNumber;
        redisTemplate.opsForValue().set(otpKey, otp, 5, TimeUnit.MINUTES);

        redisTemplate.opsForValue().set(rateLimitKey, "LOCKED", 1, TimeUnit.MINUTES);

        log.info("++++++++++++++++++++++++++++++++++++++++");
        log.info("OTP for {} (Mode: {}): {}", phoneNumber, mode, otp);
        log.info("++++++++++++++++++++++++++++++++++++++++");
    }
    
    @Override
    public String verifyOtp(VerifyOtpRequest request) {
        String phone = PhoneNumberHelper.normalizePhoneNumber(request.phoneNumber());
        String mode = request.mode();
        
        String otpKey = (mode.equals("register") ? OTP_REG_PREFIX : OTP_PWD_PREFIX) + phone;
        String cachedOtp = redisTemplate.opsForValue().get(otpKey);
        if (cachedOtp == null || !cachedOtp.equals(request.otp())) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        String verificationToken = java.util.UUID.randomUUID().toString();
        String tokenValue = phone + ":" + mode;
        redisTemplate.opsForValue().set(
            VERIFIED_TOKEN_PREFIX + verificationToken, 
            tokenValue, 
            15, 
            TimeUnit.MINUTES
        );

        redisTemplate.delete(otpKey);

        return verificationToken;
    }
    @Override
    public void changePassword(ChangePasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        String tokenKey = VERIFIED_TOKEN_PREFIX + request.verificationToken();
        String tokenValue = redisTemplate.opsForValue().get(tokenKey);

        if (tokenValue == null) {
            throw new IllegalArgumentException("Session expired. Please verify OTP again.");
        }

        String[] parts = tokenValue.split(":");
        String verifiedPhone = parts[0];
        String verifiedMode = parts[1];

        if (!"password".equals(verifiedMode)) {
            throw new IllegalArgumentException("Invalid token scope. This token cannot be used for password reset.");
        }

        User user = userRepository.findByPhoneNumber(verifiedPhone)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        redisTemplate.delete(tokenKey);
        
        log.info("Password successfully reset for user: {}", user.getUsername());
    }

    private User getAuthenticatedUser() {
        return userRepository.findById(SecurityUtils.getUserId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
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
