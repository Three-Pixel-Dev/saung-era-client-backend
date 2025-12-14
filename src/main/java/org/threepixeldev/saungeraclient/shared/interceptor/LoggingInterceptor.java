package org.threepixeldev.saungeraclient.shared.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.*;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "startTime";
    private static final String CORRELATION_ID_ATTRIBUTE = "correlationId";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final long SLOW_REQUEST_THRESHOLD_MS = 1000; // 1 second

    // Endpoints to exclude from detailed logging (health checks, etc.)
    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/actuator/health",
            "/actuator/info",
            "/health",
            "/favicon.ico"
    );

    // Sensitive headers to mask
    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization",
            "cookie",
            "x-api-key",
            "x-auth-token"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        
        // Skip logging for excluded paths
        if (EXCLUDED_PATHS.contains(requestUri)) {
            return true;
        }

        // Generate or extract correlation ID
        String correlationId = getOrGenerateCorrelationId(request);
        request.setAttribute(CORRELATION_ID_ATTRIBUTE, correlationId);
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        
        // Add correlation ID to response header
        response.setHeader(REQUEST_ID_HEADER, correlationId);

        // Log incoming request
        logRequest(request, correlationId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        String requestUri = request.getRequestURI();
        
        // Skip logging for excluded paths
        if (EXCLUDED_PATHS.contains(requestUri)) {
            return;
        }

        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        String correlationId = (String) request.getAttribute(CORRELATION_ID_ATTRIBUTE);
        
        if (startTime == null || correlationId == null) {
            return;
        }

        long duration = System.currentTimeMillis() - startTime;
        int statusCode = response.getStatus();

        if (ex != null) {
            logError(request, response, correlationId, duration, ex);
        } else {
            logResponse(request, response, correlationId, duration, statusCode);
        }
    }

    private void logRequest(HttpServletRequest request, String correlationId) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUri = queryString != null ? uri + "?" + queryString : uri;
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String contentType = request.getContentType();
        long contentLength = request.getContentLengthLong();

        Map<String, String> headers = getFilteredHeaders(request);

        log.info(
                "=== Incoming Request [{}] ===\n" +
                "Method: {}\n" +
                "URI: {}\n" +
                "Client IP: {}\n" +
                "User-Agent: {}\n" +
                "Content-Type: {}\n" +
                "Content-Length: {}\n" +
                "Headers: {}\n" +
                "========================================",
                correlationId, method, fullUri, clientIp, userAgent, contentType, contentLength, headers
        );
    }

    private void logResponse(HttpServletRequest request, HttpServletResponse response, String correlationId, long duration, int statusCode) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String contentType = response.getContentType();
        long contentLength = getContentLength(response);

        String logLevel = duration > SLOW_REQUEST_THRESHOLD_MS ? "WARN" : "INFO";
        String performanceNote = duration > SLOW_REQUEST_THRESHOLD_MS ? " [SLOW REQUEST]" : "";

        if (logLevel.equals("WARN")) {
            log.warn(
                    "=== Request Completed [{}] ===\n" +
                    "Method: {}\n" +
                    "URI: {}\n" +
                    "Status: {} {}\n" +
                    "Duration: {} ms{}\n" +
                    "Content-Type: {}\n" +
                    "Content-Length: {}\n" +
                    "========================================",
                    correlationId, method, uri, statusCode, getStatusMessage(statusCode), duration, performanceNote, contentType, contentLength
            );
        } else {
            log.info(
                    "=== Request Completed [{}] ===\n" +
                    "Method: {}\n" +
                    "URI: {}\n" +
                    "Status: {} {}\n" +
                    "Duration: {} ms\n" +
                    "Content-Type: {}\n" +
                    "Content-Length: {}\n" +
                    "========================================",
                    correlationId, method, uri, statusCode, getStatusMessage(statusCode), duration, contentType, contentLength
            );
        }
    }

    private void logError(HttpServletRequest request, HttpServletResponse response, String correlationId, long duration, Exception ex) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int statusCode = response.getStatus();

        log.error(
                "=== Request Failed [{}] ===\n" +
                "Method: {}\n" +
                "URI: {}\n" +
                "Status: {} {}\n" +
                "Duration: {} ms\n" +
                "Exception Type: {}\n" +
                "Exception Message: {}\n" +
                "Stack Trace:\n{}\n" +
                "========================================",
                correlationId, method, uri, statusCode, getStatusMessage(statusCode), duration,
                ex.getClass().getSimpleName(), ex.getMessage(), getStackTrace(ex)
        );
    }

    private String getOrGenerateCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(REQUEST_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        return correlationId;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // If multiple IPs, take the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }

    private Map<String, String> getFilteredHeaders(HttpServletRequest request) {
        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            
            // Mask sensitive headers
            if (SENSITIVE_HEADERS.contains(headerName.toLowerCase())) {
                headers.put(headerName, "***MASKED***");
            } else {
                headers.put(headerName, headerValue);
            }
        }
        
        return headers;
    }

    private long getContentLength(HttpServletResponse response) {
        String contentLengthHeader = response.getHeader("Content-Length");
        if (contentLengthHeader != null && !contentLengthHeader.isEmpty()) {
            try {
                return Long.parseLong(contentLengthHeader);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    private String getStatusMessage(int statusCode) {
        HttpStatus httpStatus = HttpStatus.resolve(statusCode);
        return httpStatus != null ? httpStatus.getReasonPhrase() : "Unknown";
    }

    private String getStackTrace(Exception ex) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = ex.getStackTrace();
        int maxLines = Math.min(stackTrace.length, 10);
        
        for (int i = 0; i < maxLines; i++) {
            sb.append("\t").append(stackTrace[i].toString()).append("\n");
        }
        
        if (stackTrace.length > maxLines) {
            sb.append("\t... and ").append(stackTrace.length - maxLines).append(" more");
        }
        
        return sb.toString();
    }
}
