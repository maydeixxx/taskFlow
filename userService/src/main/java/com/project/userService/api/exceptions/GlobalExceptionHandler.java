package com.project.userService.api.exceptions;

import com.project.userService.api.DTOs.ApiResponse;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUserNotFoundException(UserNotFoundException exception) {
        ErrorDetails errorDetails = new ErrorDetails(
                "USER_NOT_FOUND",
                exception.getMessage(),
                LocalDateTime.now()
                );
        return new ResponseEntity<>(ApiResponse.builder()
                .error(errorDetails)
                .success(false)
                .message("user not found")
                .build(), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ApiResponse<?>> databaseExceptionHandler(DatabaseException exception) {
        ErrorDetails errorDetails = new ErrorDetails(
                "DATABASE_EXCEPTION",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(ApiResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .error(errorDetails)
                .build(), HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ApiResponse<?>> handleKafkaException(KafkaException exception) {
        ErrorDetails errorDetails = new ErrorDetails(
                "KAFKA_EXCEPTION",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(ApiResponse.builder()
                .success(false)
                .error(errorDetails)
                .message("error while kafka sending/receiveng message")
                .build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthException(AuthException exception) {
        ErrorDetails errorDetails = new ErrorDetails(
                "AUTH_EXCEPTION",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(ApiResponse.builder()
                .success(false)
                .error(errorDetails)
                .message("error while authenticating")
                .build(), HttpStatus.CONFLICT);
    }

}
