package com.example.spring_class.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Task with the requested ID does not exist.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                Instant.now(),
                404,
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                new HashMap<>()
        );

        return ResponseEntity.status(404).body(error);
    }

    // Request fields do not pass @NotBlank, @Size, @NotNull, etc.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, List<String>> fieldErrors = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();

            if (!fieldErrors.containsKey(field)) {
                fieldErrors.put(field, new ArrayList<>());
            }

            fieldErrors.get(field).add(message);
        }

        ApiError error = new ApiError(
                Instant.now(),
                400,
                "Bad Request",
                "Validation failed",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(error);
    }

    // Broken JSON, incorrect parameter type, or missing query parameter.
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            TypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(
            HttpServletRequest request) {

        ApiError error = new ApiError(
                Instant.now(),
                400,
                "Bad Request",
                "Check your request body and parameters",
                request.getRequestURI(),
                new HashMap<>()
        );

        return ResponseEntity.badRequest().body(error);
    }

    // Other Spring errors, such as unsupported methods or content types.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOtherErrors(
            Exception ex,
            HttpServletRequest request) {

        int status = 500;
        String message = "An unexpected server error occurred";
        HttpHeaders headers = new HttpHeaders();

        // Keep Spring's original status and headers.
        if (ex instanceof ErrorResponse springError) {
            status = springError.getStatusCode().value();
            message = "The request could not be processed";
            headers.putAll(springError.getHeaders());
        }

        ApiError error = new ApiError(
                Instant.now(),
                status,
                HttpStatus.valueOf(status).getReasonPhrase(),
                message,
                request.getRequestURI(),
                new HashMap<>()
        );

        return ResponseEntity.status(status)
                .headers(headers)
                .body(error);
    }
}