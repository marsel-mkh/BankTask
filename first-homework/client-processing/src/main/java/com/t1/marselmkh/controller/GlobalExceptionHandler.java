package com.t1.marselmkh.controller;

import com.t1.marselmkh.dto.ErrorResponse;
import com.t1.marselmkh.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Duplicate value. Please use another email or login.";

        if (ex.getMessage().contains("uc_users_email")) {
            message = "Email already exists";
        } else if (ex.getMessage().contains("uc_users_login")) {
            message = "Login already exists";
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleClientNotFound(ClientNotFoundException ex) {
        return buildResponse("Client not found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ClientProductNotFound.class)
    public ResponseEntity<ErrorResponse> handleClientProductNotFound(ClientProductNotFound ex) {
        return buildResponse("Client product not found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductKeyNotFound.class)
    public ResponseEntity<ErrorResponse> handleProductKeyNotFound(ProductKeyNotFound ex) {
        return buildResponse("Product key not found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        return buildResponse("Product not found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BlacklistedUserException.class)
    public ResponseEntity<ErrorResponse> handleBlacklistedUser(BlacklistedUserException ex) {
        return buildResponse("User in blackList", ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<ErrorResponse> buildResponse(String error, String message, HttpStatus status) {
        ErrorResponse response = new ErrorResponse(
                error,
                message,
                status.value(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }
}