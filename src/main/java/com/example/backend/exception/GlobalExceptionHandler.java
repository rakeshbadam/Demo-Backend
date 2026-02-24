package com.example.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 🔹 Handle Resource Not Found (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        log.warn("event=exception type={} message={}",
                ex.getClass().getSimpleName(),
                ex.getMessage());

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 🔹 Handle Validation Errors (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        log.warn("event=validation_failed errors={}",
                ex.getBindingResult().getFieldErrors().size());

        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("error", "Validation Failed");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        errors.put("fieldErrors", fieldErrors);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // 🔹 Handle Other Exceptions (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(
            Exception ex) {

        log.error("event=exception type={} message={}",
                ex.getClass().getSimpleName(),
                ex.getMessage());

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Internal Server Error");
        error.put("message", "Unexpected error occurred");

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}