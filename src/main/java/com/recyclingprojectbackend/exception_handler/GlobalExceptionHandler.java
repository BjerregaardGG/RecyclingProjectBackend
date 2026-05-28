package com.recyclingprojectbackend.exception_handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException e) {
            Map<String, Object> body = Map.of(
                    "status", e.getStatusCode().value(),
                    "message", e.getReason() != null ? e.getReason() : "Der opstod en fejl",
                    "timestamp", LocalDateTime.now()
            );
            return ResponseEntity.status(e.getStatusCode()).body(body);
        }
    }

