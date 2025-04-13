package com.example.eleweatherapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "electricity_price")
public class ElectricityPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @NotNull
    @Column(nullable = false)
    private LocalDateTime datetime;

    @Getter
    @NotNull
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal price;

    public ElectricityPrice () {}
    public ElectricityPrice (LocalDateTime datetime, BigDecimal price) {
        this.datetime = datetime;
        this.price = price;
    }
}
