package com.example.eleweatherapp.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyAverageElectricityPriceDto(LocalDate date, BigDecimal price) {}
