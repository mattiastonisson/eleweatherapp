package com.example.eleweatherapp.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AggregatedDataDto (LocalDate date, BigDecimal averageTemperature, BigDecimal averagePrice) {}
