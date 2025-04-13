package com.example.eleweatherapp.utils;

import java.time.LocalDate;

public record InclusiveDateRange(LocalDate startDate, LocalDate endDate) {
    @Override
    public String toString () {
        return "DateRange [" + startDate + " - " + endDate + "]";
    }

    public String toHRString () {
        return this.startDate() + " - " + this.endDate();
    }
}
