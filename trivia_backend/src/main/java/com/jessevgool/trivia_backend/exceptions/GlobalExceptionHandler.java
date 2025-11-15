package com.jessevgool.trivia_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 * @author Jesse van Gool
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    record ErrorResponse(String code, String message) {}

    @ExceptionHandler(SessionExpiredException .class)
    public ResponseEntity<ErrorResponse> handleSessionExpired(SessionExpiredException  ex) {
        ErrorResponse body = new ErrorResponse(
            "SESSION_EXPIRED",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.GONE).body(body);
    }
}