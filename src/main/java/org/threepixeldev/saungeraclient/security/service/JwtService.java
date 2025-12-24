package org.threepixeldev.saungeraclient.security.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.threepixeldev.saungeraclient.security.exceptions.TokenExpiredException;
import org.threepixeldev.saungeraclient.security.exceptions.UnauthorizedException;
import org.threepixeldev.saungeraclient.security.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String REVOKED_TOKEN_PREFIX = "revoked:token:";
    
    public Claims validateToken(final String token) {
        if (!JwtUtil.isTokenValid(token)) {
            throw new TokenExpiredException("Invalid or expired token.");
        }

        if (this.isTokenRevoked(token)) {
            throw new UnauthorizedException("Token has been revoked.");
        }

        return JwtUtil.decodeToken(token);
    }

    public void revokeToken(final String token) {
        long expirationMillis = JwtUtil.getTokenRemainingValidityMillis(token);

        if (expirationMillis > 0) {
            redisTemplate.opsForValue().set(
                REVOKED_TOKEN_PREFIX + token,
                "revoked",
                expirationMillis,
                TimeUnit.MILLISECONDS
            );
        }
    }

    private boolean isTokenRevoked(final String token) {
        return Boolean.TRUE.equals(
            redisTemplate.hasKey(REVOKED_TOKEN_PREFIX + token)
        );
    }

    public String generateToken(final Map<String, Object> claims, final String subject, final long expirationMillis) {
        return JwtUtil.generateToken(claims, subject, expirationMillis);
    }
    
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = validateToken(token);
        String username = claims.getSubject();
        return new UsernamePasswordAuthenticationToken(
            username, null, null
        );
    }
}