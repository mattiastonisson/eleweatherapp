package com.example.eleweatherapp.dtos.WeatherData;

import java.math.BigDecimal;
import java.util.List;

public record MeteoHourlyWeatherDataDto (List<String> time, List<BigDecimal> temperature_2m) {}
