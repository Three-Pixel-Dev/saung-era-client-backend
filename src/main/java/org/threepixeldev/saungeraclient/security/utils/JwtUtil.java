package org.threepixeldev.saungeraclient.security.utils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

    public static Key getSigningKey(JwtProperties props) {
        byte[] decodedKey = Base64.getDecoder().decode(props.getSecret());
        return Keys.hmacShaKeyFor(decodedKey);
    }

    public static String generateToken(
            JwtProperties props,
            Map<String, Object> claims,
            String subject,
            long expirationMillis
    ) {
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(props.getIssuer())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationMillis))
                .signWith(getSigningKey(props))
                .compact();
    }

    public static Claims decodeToken(JwtProperties props, String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(props))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static boolean isTokenValid(JwtProperties props, String token) {
        try {
            Claims claims = decodeToken(props, token);
            return claims.getExpiration().after(new Date())
                    && props.getIssuer().equals(claims.getIssuer())
                    && claims.getSubject() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static long getTokenRemainingValidityMillis(JwtProperties props, String token) {
        try {
            Claims claims = decodeToken(props, token);
            return Math.max(claims.getExpiration().getTime() - System.currentTimeMillis(), 0);
        } catch (Exception e) {
            return 0;
        }
    }
}
