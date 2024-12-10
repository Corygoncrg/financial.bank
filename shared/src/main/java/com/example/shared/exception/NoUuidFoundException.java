package com.example.shared.exception;

public class NoUuidFoundException extends RuntimeException {
    public NoUuidFoundException(String message) {
        super(message);
    }
}
