package com.example.eleweatherapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "weather_data")
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @Getter
    @NotNull
    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal averageTemperature;

    public WeatherData () {}
    public WeatherData (LocalDate date, BigDecimal averageTemperature) {
        this.date = date;
        this.averageTemperature = averageTemperature;
    }
}
