package com.example.eleweatherapp.utils.api;

public class ApiValidationException extends RuntimeException {
    public ApiValidationException (String message) {
        super(message);
    }
}
