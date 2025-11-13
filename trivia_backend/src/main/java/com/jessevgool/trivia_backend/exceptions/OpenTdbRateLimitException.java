package com.jessevgool.trivia_backend.exceptions;

public class OpenTdbRateLimitException extends RuntimeException {
    public OpenTdbRateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}