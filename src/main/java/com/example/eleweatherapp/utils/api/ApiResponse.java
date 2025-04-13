package com.example.eleweatherapp.utils.api;

import com.example.eleweatherapp.dtos.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse<T> {
    private final T data;
    private String message;
    private final HttpStatus status;

    public ApiResponse (T data) {
        this(data, HttpStatus.OK);
        this.message = "OK";
    }

    public ApiResponse (T data, HttpStatus status) {
        this.data = data;
        this.status = status;
    }

    public ApiResponse (Exception error, HttpStatus status, T data) {
        this.data = data;
        this.message = error.getMessage();
        this.status = status;
    }

    private ApiResponseDto<T> toDto () {
        return new ApiResponseDto<>(this.data, this.message);
    }

    public ResponseEntity<ApiResponseDto<T>> asResponseEntity () {
        return new ResponseEntity<>(this.toDto(), this.status);
    }
}
