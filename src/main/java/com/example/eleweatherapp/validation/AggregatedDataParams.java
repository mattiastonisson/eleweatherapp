package com.example.eleweatherapp.validation;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record AggregatedDataParams (
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
) {
    @AssertTrue(message = "Start date cannot be after end date.")
    public boolean _isValidDateRange() {
        return !startDate.isAfter(endDate);
    }
}
