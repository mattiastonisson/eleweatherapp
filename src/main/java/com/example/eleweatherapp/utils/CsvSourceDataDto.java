package com.example.eleweatherapp.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CsvSourceDataDto (Long timestamp, LocalDateTime date, BigDecimal price) {}
