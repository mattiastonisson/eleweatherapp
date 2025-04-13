package com.example.eleweatherapp.exception;

import com.example.eleweatherapp.dtos.ApiResponseDto;
import com.example.eleweatherapp.utils.api.ApiResponse;
import com.example.eleweatherapp.utils.api.ApiValidationException;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(CsvValidationException.class)
    public ResponseEntity<ApiResponseDto<String>> handleCsvValidationException (CsvValidationException ex) {
        logger.warn("The provided CSV file is in / contains data in an invalid format | {}", ex.getMessage());
        ApiResponse<String> apiResponse = new ApiResponse<>(ex, HttpStatus.BAD_REQUEST, "The provided CSV file is in / contains data in an invalid format.");
        return apiResponse.asResponseEntity();
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponseDto<String>> handleIOException (IOException ex) {
        logger.warn("Error processing the file | {}", ex.getMessage());
        ApiResponse<String> apiResponse = new ApiResponse<>(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Error processing the file.");
        return apiResponse.asResponseEntity();
    }

    @ExceptionHandler({DateTimeParseException.class})
    public ResponseEntity<ApiResponseDto<String>> handleInvalidDateFormat(Exception ex) {
        logger.warn("Invalid date format, please use YYYY-MM-DD | {}", ex.getMessage());
        ApiResponse<String> apiResponse = new ApiResponse<>(ex, HttpStatus.BAD_REQUEST, "Invalid date format, please use YYYY-MM-DD");
        return apiResponse.asResponseEntity();
    }

    @ExceptionHandler(ApiValidationException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleApiValidationException(ApiValidationException ex) {
        logger.warn("API validation error: {}", ex.getMessage());
        ApiResponseDto<Object> response = new ApiResponseDto<>(null, ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<String>> handleGeneralException (Exception ex) {
        logger.warn("An unexpected error occurred: {}", ex.getMessage());
        ApiResponse<String> apiResponse = new ApiResponse<>(ex, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        return apiResponse.asResponseEntity();
    }
}
