package com.example.eleweatherapp.dtos.WeatherData;

public record MeteoApiResponseDto(double longitude, double latitude, MeteoHourlyWeatherDataDto hourly) {}
