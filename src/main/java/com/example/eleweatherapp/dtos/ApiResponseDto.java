package com.example.eleweatherapp.dtos;

public record ApiResponseDto<T> (T data, String message) {}
