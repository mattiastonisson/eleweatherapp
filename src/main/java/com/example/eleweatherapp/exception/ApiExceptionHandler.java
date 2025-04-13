package com.example.eleweatherapp.exception;

import com.example.eleweatherapp.dtos.ApiResponseDto;
import com.example.eleweatherapp.utils.api.ApiResponse;
import com.example.eleweatherapp.utils.api.ApiValidationException;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(CsvValidationException.class)
    public ResponseEntity<ApiResponseDto<String>> handleCsvValidationException (CsvValidationException ex) {
        ApiResponse<String> apiResponse = new ApiResponse<>(ex, HttpStatus.BAD_REQUEST, "The provided CSV file is in / contains data in an invalid format.");
        return apiResponse.asResponseEntity();
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponseDto<String>> handleIOException (IOException ex) {
        ApiResponse<String> apiResponse = new ApiResponse<>(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Error processing the file.");
        return apiResponse.asResponseEntity();
    }

    @ExceptionHandler({DateTimeParseException.class})
    public ResponseEntity<ApiResponseDto<String>> handleInvalidDateFormat(Exception ex) {
        ApiResponse<String> apiResponse = new ApiResponse<>(ex, HttpStatus.BAD_REQUEST, "Invalid date format, please use YYYY-MM-DD");
        return apiResponse.asResponseEntity();
    }

    @ExceptionHandler(ApiValidationException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleApiValidationException(ApiValidationException ex) {
        ApiResponseDto<Object> response = new ApiResponseDto<>(null, ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<String>> handleGeneralException (Exception ex) {
        ApiResponse<String> apiResponse = new ApiResponse<>(ex, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        return apiResponse.asResponseEntity();
    }
}
