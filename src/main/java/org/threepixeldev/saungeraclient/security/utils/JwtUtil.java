package org.threepixeldev.saungeraclient.security.utils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().systemProperties().load();

    private static final String SECRET_KEY_STRING = dotenv.get("JWT_SECRET_KEY");
    private static final Key SECRET_KEY;
    private static final String ISSUER = "threepixeldev";

    static {
        if (SECRET_KEY_STRING == null || SECRET_KEY_STRING.isBlank()) {
            throw new IllegalStateException("JWT_SECRET_KEY is missing in environment variables!");
        }
        try {
            byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY_STRING);
            SECRET_KEY = Keys.hmacShaKeyFor(decodedKey);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("JWT_SECRET_KEY is not a valid Base64 string!", e);
        }
    }

    public static String generateToken(final Map<String, Object> claims, final String subject,
            final long expirationMillis) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims decodeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static boolean isTokenValid(String token) {
        if (token == null) return false;
        try {
            final Claims claims = decodeToken(token);

            if (claims.getExpiration().before(new Date())) {
                return false;
            }
            if (!ISSUER.equals(claims.getIssuer())) {
                return false;
            }
            return claims.getSubject() != null && !claims.getSubject().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    public static long getTokenRemainingValidityMillis(String token) {
        try {
            Claims claims = decodeToken(token);
            return Math.max(claims.getExpiration().getTime() - System.currentTimeMillis(), 0);
        } catch (Exception e) {
            return 0;
        }
    }
}