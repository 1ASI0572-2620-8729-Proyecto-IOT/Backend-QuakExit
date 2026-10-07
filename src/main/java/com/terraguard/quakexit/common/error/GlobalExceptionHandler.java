package com.terraguard.quakexit.common.error;

import com.terraguard.quakexit.common.exception.ApiExceptions.*;
import com.terraguard.quakexit.subscription.exception.FeatureAccessException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> notFound(ResourceNotFoundException ex, HttpServletRequest req) { return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, List.of()); }
    @ExceptionHandler({DuplicateResourceException.class, BusinessRuleException.class})
    ResponseEntity<ApiError> conflict(RuntimeException ex, HttpServletRequest req) { return build(HttpStatus.CONFLICT, ex.getMessage(), req, List.of()); }
    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<ApiError> unauthorized(RuntimeException ex, HttpServletRequest req) { return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req, List.of()); }
    @ExceptionHandler(FeatureAccessException.class)
    ResponseEntity<Map<String, Object>> feature(FeatureAccessException ex) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("status", 403, "code", ex.getCode(), "message", ex.getMessage())); }
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiError> denied(AccessDeniedException ex, HttpServletRequest req) { return build(HttpStatus.FORBIDDEN, ex.getMessage(), req, List.of()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> invalid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        var fields = ex.getBindingResult().getFieldErrors().stream().map(e -> new ApiError.FieldError(e.getField(), e.getDefaultMessage())).toList();
        return build(HttpStatus.BAD_REQUEST, "La solicitud contiene errores", req, fields);
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> generic(Exception ex, HttpServletRequest req) {
        log.error("Unhandled API error for {} {}", req.getMethod(), req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", req, List.of());
    }
    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest req, List<ApiError.FieldError> fields) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, req.getRequestURI(), fields));
    }
}
