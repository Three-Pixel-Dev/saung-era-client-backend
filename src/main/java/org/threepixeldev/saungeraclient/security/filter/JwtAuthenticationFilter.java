package org.threepixeldev.saungeraclient.security.filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.threepixeldev.saungeraclient.security.dto.CustomUserPrincipal;
import org.threepixeldev.saungeraclient.security.service.JwtService;
import org.threepixeldev.saungeraclient.security.utils.ResponseWriterUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = extractToken(request);
        if (token != null) {
            try {
                final Claims claims = jwtService.validateToken(token);
                
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    setAuthenticationContext(claims, request);
                }
            } catch (Exception e) {
                log.warn("JWT Authentication failed: {}", e.getMessage());
                ResponseWriterUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage(), request);
                return; 
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void setAuthenticationContext(Claims claims, HttpServletRequest request) {
        Long userId = claims.get("id", Long.class);
        String identifier = claims.getSubject();
        System.out.println("Identifier is ========================++++++++++"+identifier);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new CustomUserPrincipal(userId, identifier), 
                null, 
                Collections.emptyList()
        );
        
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}