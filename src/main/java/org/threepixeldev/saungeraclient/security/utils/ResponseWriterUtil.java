package org.threepixeldev.saungeraclient.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.threepixeldev.saungeraclient.security.exceptions.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResponseWriterUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message, HttpServletRequest request) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, String> meta = new HashMap<>();
        meta.put("method", request.getMethod());
        meta.put("endpoint", request.getRequestURI());

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                message,
                meta
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}