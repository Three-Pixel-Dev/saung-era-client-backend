package org.threepixeldev.saungeraclient.security.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.threepixeldev.saungeraclient.security.utils.ResponseWriterUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ResponseWriterUtil.writeErrorResponse(
            response, 
            HttpStatus.UNAUTHORIZED, 
            "Unauthorized: Authentication token is missing or invalid.", 
            request
        );
    }
}